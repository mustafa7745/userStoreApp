package com.owaistelecom.telecom.ui.add_to_cart2

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.models.Coupon
import com.owaistelecom.telecom.models.OrderAmount
import com.owaistelecom.telecom.models.PrimaryProduct
import com.owaistelecom.telecom.models.Product
import com.owaistelecom.telecom.models.ProductOption
import com.owaistelecom.telecom.models.Store
import com.owaistelecom.telecom.models.StoreProduct1
import com.owaistelecom.telecom.shared.formatPrice
import com.owaistelecom.telecom.ui.cart_preview.DeliveryPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepository @Inject constructor(
    private val appSession: AppSession
)
{

    private var storeCartProducts by mutableStateOf<List<StoreCartProduct2>>(emptyList())

    // Add a product to the cart
    fun addProductToCart( product: PrimaryProduct, storeProduct1: StoreProduct1) {
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

                        if (newCartProductOption.find { it.option == storeProduct1 } != null) {
                            Log.e("dffd","fefe5788")
                            // If the option exists, increment the count
                            newCartProductOption.find { it.option == storeProduct1 }!!.count.value +=1
                        } else {
                            Log.e("dffd","fefe345")
                            // If the option doesn't exist, add a new option with a count of 1
                            newCartProductOption+=(CartProductOption2(storeProduct1, mutableStateOf(1)))
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
                val newCartProduct = CartProduct2(product, listOf(CartProductOption2(storeProduct1, mutableIntStateOf(1))))
                existingStoreCartProduct.cartProducts.value += newCartProduct
            }
        }else{
            val newCartProduct = CartProduct2(product, listOf(CartProductOption2(storeProduct1, mutableIntStateOf(1))))
            storeCartProducts = storeCartProducts + StoreCartProduct2(appSession.selectedStore, mutableStateOf(listOf(newCartProduct)))
        }

    }

    private fun existingCartProduct(
        existingStoreCartProduct: StoreCartProduct2,
        product: PrimaryProduct
    ): CartProduct2? {
        val existingCartProduct =
            existingStoreCartProduct.cartProducts.value.find { it.product == product }
        return existingCartProduct
    }

    fun decrement(product: PrimaryProduct,  storeProduct1: StoreProduct1) {
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
                        val existingOption = newCartProductOption.find { it.option == storeProduct1 }

                        if (existingOption != null) {
                            if (existingOption.count.value >= 1){
                                existingOption.count.value -=1
                                if (existingOption.count.value == 0){
                                    if (checkIfHaveOptions(appSession.selectedStore,product) ==1){
                                        removeProductFromCart(appSession.selectedStore,product)
                                    }
                                    else{
                                        removeProductOptionFromCart(appSession.selectedStore,product,storeProduct1)
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun ifOptionInCart(store: Store, product: Product,  storeProduct1: StoreProduct1): Boolean {
        val existingStoreCartProduct = storeCartProducts.find { it.store == store }
        if (existingStoreCartProduct != null){
            return existingStoreCartProduct.cartProducts.value.find { it.cartProductOption.find { it.option == storeProduct1 } != null } != null
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

    fun ifProductInCart(store: Store, product: PrimaryProduct): Boolean {
        val existingStoreCartProduct = storeCartProducts.find { it.store == store }
        if (existingStoreCartProduct != null){
            return existingStoreCartProduct.cartProducts.value.find { it.product == product} != null
        }
        return false

    }

    fun countOptionProduct( product: PrimaryProduct,  storeProduct1: StoreProduct1): Int {
        val existingStoreCartProduct = storeCartProducts.find { it.store == appSession.selectedStore }
        if (existingStoreCartProduct != null){
            val product1 = existingStoreCartProduct.cartProducts.value.find { it.product == product }
            if (product1 != null){
                return product1.cartProductOption.find { it.option == storeProduct1 }?.count?.value
                    ?: 0
            }
            return 0
        }
        return 0

    }

    // Remove a product from the cart
    fun removeProductFromCart(store: Store, product: PrimaryProduct) {
        val existingStoreCartProduct = storeCartProducts.find { it.store == store }
        if (existingStoreCartProduct != null){
            existingStoreCartProduct.cartProducts.value = existingStoreCartProduct.cartProducts.value.filter { it.product != product }
        }
    }

    fun removeAllProductsFromStore(storeId: Int) {
        val existingStoreCartProduct = storeCartProducts.find { it.store.id == storeId }
        if (existingStoreCartProduct != null) {
            existingStoreCartProduct.cartProducts.value = emptyList()
        }
    }


    // Remove a specific option from a product in the cart
    fun removeProductOptionFromCart(store: Store, product: PrimaryProduct,  storeProduct1: StoreProduct1) {
        val existingStoreCartProduct = storeCartProducts.find { it.store == store }
        if (existingStoreCartProduct != null){
            existingStoreCartProduct.cartProducts.value =existingStoreCartProduct.cartProducts.value.map { cartProduct ->
                if (cartProduct.product == product) {
                    val updatedOptions = cartProduct.cartProductOption.filterNot { it.option == storeProduct1 }
                    cartProduct.copy(cartProductOption = updatedOptions)
                } else {
                    cartProduct
                }
            }
            if (checkIfHaveOptions(store, product) == 0)
                removeProductFromCart(store, product)
        }
    }

    private fun checkIfHaveOptions(store: Store, product: PrimaryProduct):Int {
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
    fun getAllCartProducts(): List<CartProduct2> {
        val existingStoreCartProduct =storeCartProducts.find { it.store == appSession.selectedStore }
        if (existingStoreCartProduct != null){
            return existingStoreCartProduct. cartProducts.value
        }
        return  emptyList<CartProduct2>()
    }
    fun getAllCartProductsSum(deliveryPrice: DeliveryPrice? = null,coupon: Coupon? = null): String {
        val list = mutableListOf<OrderAmount>()

        val existingStoreCartProduct = storeCartProducts.find { it.store == appSession.selectedStore }

        existingStoreCartProduct?.cartProducts?.value?.forEach { cartProduct ->
            cartProduct.cartProductOption.forEach { productOption ->
                val currencyId = productOption.option.currencyId
                val price = productOption.option.price.toDouble()
                val count = productOption.count.value.toDouble()
                val amount = price * count

                val existing = list.find { it.currencyId == currencyId }
                if (existing != null) {
                    existing.amount += amount
                } else {
                    list.add(OrderAmount(currencyId, amount))
                }
            }
        }

        coupon?.let { dp ->
            val existing = list.find { it.currencyId == dp.currencyId }
            if (existing != null) {
                val discountAmount = if (dp.type == 1) {
                    // خصم مئوي: احسب النسبة من القيمة الحالية
                    (existing.amount * dp.amount / 100)
                } else {
                    // خصم نقدي مباشر
                    dp.amount
                }

                existing.amount -= discountAmount.coerceAtMost(existing.amount) // لا تجعلها بالسالب
            }
//            else {
//                // لا يوجد عنصر سابق، نضيف قيمة سالبة كناتج خصم
//                val discountAmount = if (dp.type == 1) {
//                    0.0 // لا يمكن حساب نسبة من لا شيء
//                } else {
//                    dp.amount
//                }
//
//                list.add(OrderAmount(dp.currencyId, -discountAmount))
//            }
        }



        // أضف سعر التوصيل إن وُجد
        deliveryPrice?.let { dp ->
            val existing = list.find { it.currencyId == dp.currencyId }
            if (existing != null) {
                existing.amount += dp.deliveryPrice
            } else {
                list.add(OrderAmount(dp.currencyId, dp.deliveryPrice))
            }
        }



        // تحويل النتيجة إلى نص منسق
        val formatted = list.joinToString(" و ") { orderAmount ->
            val currency = appSession.home.storeCurrencies
                .firstOrNull { it.currencyId == orderAmount.currencyId }
            val currencyName = currency?.currencyName ?: ""
            "${formatPrice(orderAmount.amount.toString())} $currencyName"
        }

        return formatted
    }


    fun getAllCartProductsSumPrices(deliveryPrice: DeliveryPrice? = null): ArrayList<OrderAmount> {
        val list = arrayListOf<OrderAmount>()
        var sum = 0.0
        val existingStoreCartProduct = storeCartProducts.find { it.store == appSession.selectedStore }
        if (existingStoreCartProduct != null){
            existingStoreCartProduct. cartProducts.value.forEach { cartProduct: CartProduct2 ->
                cartProduct.cartProductOption.forEach { productOption ->
                    if (list.find { it.currencyId == productOption.option.currencyId } != null){
                        list.find { it.currencyId == productOption.option.currencyId }!!.amount += productOption.option.price.toDouble() * productOption.count.value.toDouble()
                    }
                    else{
                        list.add(
                            OrderAmount(productOption.option.currencyId,productOption.option.price * productOption.count.value.toDouble()))
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
                    OrderAmount(deliveryPrice.currencyId,deliveryPrice.deliveryPrice))
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
                list.add(OrderProductWithQntModel(productOption.option.id,productOption.count.value))
            }
        }
        return list
    }
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,

    ) : ViewModel()
{

    fun getCartProducts(): List<CartProduct2> {
        return cartRepository.getAllCartProducts()
//        return emptyList()
    }

    fun isCartEmpty(): Boolean {
        return cartRepository.getAllCartProducts().isEmpty()
    }
    fun getCountOptionProduct(product: Product, productOptions: ProductOption): Int {
//        return cartRepository.countOptionProduct(product,productOptions)
        return  1
    }
    fun decrement(product: Product, productOptions: ProductOption){
//        cartRepository.decrement(product, productOptions)
    }

    fun addProductToCart(product: Product, productOptions: ProductOption){
//        cartRepository.addProductToCart(product,productOptions)
    }

}

@HiltViewModel
class Cart2ViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    val appSession: AppSession
) : ViewModel()
{

    fun getCartProducts(): List<CartProduct2> {
        return cartRepository.getAllCartProducts()
    }

    fun isCartEmpty(): Boolean {
        return cartRepository.getAllCartProducts().isEmpty()
    }
    fun getCountOptionProduct(product: PrimaryProduct, storeProduct1: StoreProduct1): Int {
        return cartRepository.countOptionProduct(product,storeProduct1)
    }
    fun decrement(product: PrimaryProduct, storeProduct1: StoreProduct1){
        cartRepository.decrement(product, storeProduct1)
    }

    fun addProductToCart(product: PrimaryProduct, storeProduct1: StoreProduct1){
        cartRepository.addProductToCart(product,storeProduct1)
    }

}



data class CartProduct(
    val product: Product,
    var cartProductOption:List<CartProductOption>
)
data class CartProductOption(
    val productOption: ProductOption,
    var count : MutableState<Int> = mutableIntStateOf(0)
)
data class StoreCartProduct(
    val store: Store,
    var cartProducts: MutableState<List<CartProduct>>
)

data class CartProduct2(
    val product: PrimaryProduct,
    var cartProductOption:List<CartProductOption2>
)
data class CartProductOption2(
    val option: StoreProduct1,
    var count : MutableState<Int> = mutableIntStateOf(0)
)
data class StoreCartProduct2(
    val store: Store,
    var cartProducts: MutableState<List<CartProduct2>>
)
@Serializable
data class OrderProductWithQntModel (
    val id: Int,
    val qnt: Int,
)

