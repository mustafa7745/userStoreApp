package com.owaistelecom.telecom.ui.add_to_cart

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.models.OrderAmount
import com.owaistelecom.telecom.models.Product
import com.owaistelecom.telecom.models.ProductOption
import com.owaistelecom.telecom.models.Store
import com.owaistelecom.telecom.shared.formatPrice
import com.owaistelecom.telecom.ui.cart_preview.DeliveryPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val appSession: AppSession
)
{

    private var storeCartProducts by mutableStateOf<List<StoreCartProduct>>(emptyList())

    // Add a product to the cart
    fun addProductToCart( product: Product, productOptions: ProductOption) {
        // Find the existing cart product (if any)
        val existingStoreCartProduct = storeCartProducts.find { it.store == appSession.selectedStore }
        if (existingStoreCartProduct != null){
            val existingCartProduct = existingCartProduct(existingStoreCartProduct, product)

            if (existingCartProduct != null) {
                // Update the cart product options
                val  newCartProducts = existingStoreCartProduct.cartProducts.value.map { cartProduct ->
                    if (cartProduct.product == product) {

                        // Get the existing options and make a mutable copy
                        var newCartProductOption = existingCartProduct.cartProductOption

                        // Check if the options list is empty or if the product option already exists
//                    val existingOption = newCartProductOption.find { it.productOption == productOptions }

                        if (newCartProductOption.find { it.productOption == productOptions } != null) {
                            Log.e("dffd","fefe5788")
                            // If the option exists, increment the count
                            newCartProductOption.find { it.productOption == productOptions }!!.count.value +=1
                        } else {
                            Log.e("dffd","fefe345")
                            // If the option doesn't exist, add a new option with a count of 1
                            newCartProductOption+=(CartProductOption(productOptions, mutableStateOf(1)))
                        }

                        cartProduct.copy(cartProductOption = newCartProductOption)
                    } else {
                        // If the product doesn't match, keep it unchanged
                        cartProduct
                    }
                }

                existingStoreCartProduct.cartProducts.value = newCartProducts
            } else {
                // If the product doesn't exist in the cart, create a new product entry
                val newCartProduct = CartProduct(product, listOf(CartProductOption(productOptions, mutableIntStateOf(1))))
                existingStoreCartProduct.cartProducts.value += newCartProduct
            }
        }else{
            val newCartProduct = CartProduct(product, listOf(CartProductOption(productOptions, mutableIntStateOf(1))))
            storeCartProducts = storeCartProducts + StoreCartProduct(appSession.selectedStore, mutableStateOf(listOf(newCartProduct)))
        }

    }

    private fun existingCartProduct(
        existingStoreCartProduct: StoreCartProduct,
        product: Product
    ): CartProduct? {
        val existingCartProduct =
            existingStoreCartProduct.cartProducts.value.find { it.product == product }
        return existingCartProduct
    }

    fun decrement(product: Product, productOption: ProductOption) {
        // Find the existing cart product (if any)
        val existingStoreCartProduct = storeCartProducts.find { it.store == appSession.selectedStore }
        if (existingStoreCartProduct != null){
            val existingCartProduct = existingCartProduct(existingStoreCartProduct, product)
            if (existingCartProduct != null) {
                // Update the cart product options
                existingStoreCartProduct.cartProducts.value.forEach { cartProduct ->
                    if (cartProduct.product == product) {

                        // Get the existing options and make a mutable copy
                        var newCartProductOption = existingCartProduct.cartProductOption

                        // Check if the options list is empty or if the product option already exists
                        val existingOption = newCartProductOption.find { it.productOption == productOption }

                        if (existingOption != null) {
                            if (existingOption.count.value >= 1){
                                existingOption.count.value -=1
                                if (existingOption.count.value == 0){
                                    if (checkIfHaveOptions(appSession.selectedStore,product) ==1){
                                        removeProductFromCart(appSession.selectedStore,product)
                                    }
                                    else{
                                        removeProductOptionFromCart(appSession.selectedStore,product,productOption)
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun ifOptionInCart(store: Store, product: Product, productOptions: ProductOption): Boolean {
        val existingStoreCartProduct = storeCartProducts.find { it.store == store }
        if (existingStoreCartProduct != null){
            return existingStoreCartProduct.cartProducts.value.find { it.cartProductOption.find { it.productOption == productOptions } != null } != null
        }
        return false
    }

//    fun sumOptionInCart(store: Store,product: Product, productOptions: ProductOption): Double {
////        val existingStoreCartProduct = storeCartProducts.find { it.store == store }
////        if (existingStoreCartProduct != null){
////            return existingStoreCartProduct.cartProducts.value.find { it.cartProductOption.find { it.productOption == productOptions } != null } != null
////        }
////        return 0
//    }

    fun ifProductInCart(store: Store, product: Product): Boolean {
        val existingStoreCartProduct = storeCartProducts.find { it.store == store }
        if (existingStoreCartProduct != null){
            return existingStoreCartProduct.cartProducts.value.find { it.product == product} != null
        }
        return false

    }

    fun countOptionProduct( product: Product, productOptions: ProductOption): Int {
        val existingStoreCartProduct = storeCartProducts.find { it.store == appSession.selectedStore }
        if (existingStoreCartProduct != null){
            val product1 = existingStoreCartProduct.cartProducts.value.find { it.product == product }
            if (product1 != null){
                return product1.cartProductOption.find { it.productOption == productOptions }?.count?.value
                    ?: 0
            }
            return 0
        }
        return 0

    }

    // Remove a product from the cart
    fun removeProductFromCart(store: Store, product: Product) {
        val existingStoreCartProduct = storeCartProducts.find { it.store == store }
        if (existingStoreCartProduct != null){
            existingStoreCartProduct.cartProducts.value = existingStoreCartProduct.cartProducts.value.filter { it.product != product }
        }

    }

    // Remove a specific option from a product in the cart
    fun removeProductOptionFromCart(store: Store, product: Product, productOption: ProductOption) {
        val existingStoreCartProduct = storeCartProducts.find { it.store == store }
        if (existingStoreCartProduct != null){
            existingStoreCartProduct.cartProducts.value =existingStoreCartProduct.cartProducts.value.map { cartProduct ->
                if (cartProduct.product == product) {
                    val updatedOptions = cartProduct.cartProductOption.filterNot { it.productOption == productOption }
                    cartProduct.copy(cartProductOption = updatedOptions)
                } else {
                    cartProduct
                }
            }
            if (checkIfHaveOptions(store, product) == 0)
                removeProductFromCart(store, product)
        }
    }

    private fun checkIfHaveOptions(store: Store, product: Product):Int {
        val existingStoreCartProduct = storeCartProducts.find { it.store == store }
        if (existingStoreCartProduct != null){
            val p = existingStoreCartProduct.cartProducts.value.find { it.product == product }
            if (p != null) {
                return p.cartProductOption.size
            }
            return -1
        }
        return  -1
    }

    // Update the count of a specific option for a product in the cart
//    fun updateProductOptionCount(product: Product, productOption: ProductOption, newCount: Int) {
//        cartProducts = cartProducts.map { cartProduct ->
//            if (cartProduct.product == product) {
//                val updatedOptions = cartProduct.cartProductOption.map { option ->
//                    if (option.productOption == productOption) {
//                        option.copy(count = mutableStateOf(1))
//                    } else {
//                        option
//                    }
//                }
//                cartProduct.copy(cartProductOption = updatedOptions)
//            } else {
//                cartProduct
//            }
//        }
//    }

    // Get all products in the cart
    fun getAllCartProducts(): List<CartProduct> {
        val existingStoreCartProduct =storeCartProducts.find { it.store == appSession.selectedStore }
        if (existingStoreCartProduct != null){
            return existingStoreCartProduct. cartProducts.value
        }
        return  emptyList<CartProduct>()
    }
    fun getAllCartProductsSum( deliveryPrice: DeliveryPrice? = null): String {
        val list = arrayListOf<OrderAmount>()
        var sum = 0.0
        val existingStoreCartProduct = storeCartProducts.find { it.store == appSession.selectedStore }
        if (existingStoreCartProduct != null){
            existingStoreCartProduct. cartProducts.value.forEach { cartProduct: CartProduct ->
                cartProduct.cartProductOption.forEach { productOption ->
                    if (list.find { it.id == productOption.productOption.currency.id } != null){
                        list.find { it.id == productOption.productOption.currency.id }!!.amount += productOption.productOption.price.toDouble() * productOption.count.value.toDouble()
                    }
                    else{
                        list.add(
                            OrderAmount(productOption.productOption.currency.id,productOption.productOption.currency.name,productOption.productOption.currency.id,productOption.productOption.price.toDouble() * productOption.count.value.toDouble()))

                    }
//                    sum += productOption.productOption.price.toDouble() * productOption.count.value.toDouble()
                }
            }

        }
        if (deliveryPrice !=null){
            if (list.find { it.currencyId == deliveryPrice.currencyId } != null){
                list.find { it.currencyId == deliveryPrice.currencyId }!!.amount += deliveryPrice.deliveryPrice
            }
            else{
                list.add(
                    OrderAmount(deliveryPrice.currencyId,deliveryPrice.currencyName,deliveryPrice.currencyId,deliveryPrice.deliveryPrice))
            }
        }
        val f = list.joinToString(
            separator = " و "
        ) {  formatPrice(it.amount.toString()) +" "+ it.currencyName }
        return f
    }

    fun getAllCartProductsSumPrices(deliveryPrice: DeliveryPrice? = null): ArrayList<OrderAmount> {
        val list = arrayListOf<OrderAmount>()
        var sum = 0.0
        val existingStoreCartProduct = storeCartProducts.find { it.store == appSession.selectedStore }
        if (existingStoreCartProduct != null){
            existingStoreCartProduct. cartProducts.value.forEach { cartProduct: CartProduct ->
                cartProduct.cartProductOption.forEach { productOption ->
                    if (list.find { it.id == productOption.productOption.currency.id } != null){
                        list.find { it.id == productOption.productOption.currency.id }!!.amount += productOption.productOption.price.toDouble() * productOption.count.value.toDouble()
                    }
                    else{
                        list.add(
                            OrderAmount(productOption.productOption.currency.id,productOption.productOption.currency.name,productOption.productOption.currency.id,productOption.productOption.price.toDouble() * productOption.count.value.toDouble()))
                    }
//                    sum += productOption.productOption.price.toDouble() * productOption.count.value.toDouble()
                }
            }

        }
        if (deliveryPrice !=null){
            if (list.find { it.currencyId == deliveryPrice.currencyId } != null){
                list.find { it.currencyId == deliveryPrice.currencyId }!!.amount += deliveryPrice.deliveryPrice
            }
            else{
                list.add(
                    OrderAmount(deliveryPrice.currencyId,deliveryPrice.currencyName,deliveryPrice.currencyId,deliveryPrice.deliveryPrice))
            }
        }
        return list
//        val f = list.joinToString(
//            separator = " و "
//        ) {  formatPrice(it.amount.toString()) +" "+ it.currencyName }
//        return f
    }

    fun getProductsIdsWithQnt():List<OrderProductWithQntModel>{
        val existingStoreCartProduct = storeCartProducts.find { it.store == appSession.selectedStore }
        val list  = emptyList<OrderProductWithQntModel>().toMutableList()
        existingStoreCartProduct?.cartProducts?.value?.forEach { cartProduct->
            cartProduct.cartProductOption.forEach { productOption ->
                list.add(OrderProductWithQntModel(productOption.productOption.storeProductId,productOption.count.value))
            }
        }
        return list
    }
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    fun getCartProducts(): List<CartProduct> {
        return cartRepository.getAllCartProducts()
    }

    fun isCartEmpty(): Boolean {
        return cartRepository.getAllCartProducts().isEmpty()
    }
    fun getCountOptionProduct(product: Product, productOptions: ProductOption): Int {
        return cartRepository.countOptionProduct(product,productOptions)
    }
    fun decrement(product: Product, productOptions: ProductOption){
        cartRepository.decrement(product, productOptions)
    }

    fun addProductToCart(product: Product, productOptions: ProductOption){
        cartRepository.addProductToCart(product,productOptions)
    }

}

