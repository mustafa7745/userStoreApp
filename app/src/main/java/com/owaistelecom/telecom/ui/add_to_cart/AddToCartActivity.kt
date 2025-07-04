package com.owaistelecom.telecom.ui.add_to_cart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.models.Product
import com.owaistelecom.telecom.models.ProductOption
import com.owaistelecom.telecom.models.Store
import com.owaistelecom.telecom.models.StoreProduct
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

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
    var cartProducts:MutableState<List<CartProduct>>
)

@Serializable
data class OrderProductWithQntModel (
    val id: Int,
    val qnt: Int,
)


@AndroidEntryPoint
class AddToCartActivity : ComponentActivity() {
    lateinit var storeProduct: StoreProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val str = intent.getStringExtra("product")
        if (str != null) {
            try {
                storeProduct = MyJson.IgnoreUnknownKeys.decodeFromString(str)
            }catch (e:Exception){
                finish()
            }

        } else {
            finish()
        }

        enableEdgeToEdge()
        setContent {
            AddToCartScreen(this,storeProduct)
        }
    }
}

