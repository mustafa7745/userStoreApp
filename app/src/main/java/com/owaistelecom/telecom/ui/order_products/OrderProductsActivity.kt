package com.owaistelecom.telecom.ui.order_products

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.Singlton.FormBuilder
import com.owaistelecom.telecom.shared.CustomCard
import com.owaistelecom.telecom.shared.CustomIcon
import com.owaistelecom.telecom.shared.CustomRow
import com.owaistelecom.telecom.shared.CustomRow2
import com.owaistelecom.telecom.shared.MainComposeRead
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.MyTextField
import com.owaistelecom.telecom.shared.RequestServer2
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.shared.formatPrice
import com.owaistelecom.telecom.ui.orders.Order
import com.owaistelecom.telecom.ui.orders.OrderAmount
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import javax.inject.Inject

@HiltViewModel
class OrderProductsViewModel @Inject constructor(
    private val requestServer: RequestServer2,
    private val builder: FormBuilder
): ViewModel(){
    val stateController = StateController()
    var orderComponent by mutableStateOf<OrderComponent?>(null)
    lateinit var order: Order
    var paidCode by mutableStateOf<String>("")

    fun read() {
        stateController.startRead()
        viewModelScope.launch {
            try {
                val body = builder.sharedBuilderFormWithStoreId()
                    .addFormDataPart("orderId",order.id.toString())
                val data = requestServer.request(body, "getOrderProducts")
                orderComponent = MyJson.IgnoreUnknownKeys.decodeFromString(data as String)
                stateController.successState()
            } catch (e: Exception) {
                stateController.errorStateRead(e.message.toString())
            }
        }
//        stateController.startRead()
//
//        val body = builder. builderFormWithAccessToken()
//            .addFormDataPart("orderId",order.id.toString())
//            .build()
//
//        requestServer.request2(body, "getOrderProducts", { code, fail ->
//            stateController.errorStateRead(fail)
//        }
//        ) { data ->
//            orderComponent =
//                MyJson.IgnoreUnknownKeys.decodeFromString(
//                    data
//                )
//
//            stateController.successState()
//        }
    }
    fun addPaidCode() {
//        stateController.startAud()
//
//        val body = builder. builderFormWithAccessToken()
//            .addFormDataPart("orderId",order.id.toString())
//            .addFormDataPart("paidCode",paidCode)
//            .build()
//
//        requestServer.request2(body, "addPaidCode", { code, fail ->
//            stateController.errorStateAUD(fail)
//        }
//        ) { data ->
//            val orderPayment:OrderPayment =
//                MyJson.IgnoreUnknownKeys.decodeFromString(
//                    data
//                )
//
//            orderComponent = orderComponent!!.updateOrderDPayment(orderPayment)
////          orderComponent!!.copy(
////
////
////          ) =  orderComponent!!.orderProducts .map {
////                if (it.id == orderProduct.id){
////                    orderProduct
////                }else
////                    it
////            }
//
////            isShowChooseDeliveryMan = false
//            stateController.successStateAUD()
//        }
    }
}

@AndroidEntryPoint
class OrderProductsActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var order: Order

        val intent = intent
        val str = intent.getStringExtra("order")
        if (str != null) {
            try {
                order = MyJson.IgnoreUnknownKeys.decodeFromString(str)
            }catch (e:Exception){
                finish()
            }
        } else {
            finish()
        }
        setContent {
            OrderProductScreen(order)
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun OrderProductScreen(order: Order) {
        val viewModel:OrderProductsViewModel = hiltViewModel()
        val activity = LocalContext.current as Activity
        LaunchedEffect(3) {
            viewModel.order = order
            viewModel.read()
        }
        OwaisTelecomTheme {
            MainComposeRead("فاتورة الطلب: " + order.id,viewModel.stateController,{activity.finish()},{
                viewModel.read()
            }) {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    stickyHeader {
                        CustomCard(
                            modifierBox = Modifier
                                .fillMaxSize()
                                .clickable {

                                }
                        ) {
                            Column {
                                Text(
                                    "اجمالي الطلبات: " + order.amounts.joinToString(
                                        separator = " و "
                                    ) { formatPrice(it.amount.toString()) + " " + it.currencyName },
                                    Modifier.padding(8.dp)
                                )
                                var f = order.amounts.toMutableList()
                                if (viewModel.orderComponent!!.orderDelivery != null) {
                                    val s =
                                        f.find { it.currencyId == viewModel.orderComponent!!.orderDelivery!!.currencyId }
                                    if (s != null) {
                                        f = f.map {
                                            if (s.id == it.id) {
                                                it.copy(amount = it.amount + viewModel.orderComponent!!.orderDelivery!!.deliveryPrice.toDouble())
                                            } else
                                                it
                                        }.toMutableList()
                                    } else
                                        f += OrderAmount(
                                            viewModel.orderComponent!!.orderDelivery!!.currencyId,
                                            viewModel.orderComponent!!.orderDelivery!!.currencyName,
                                            viewModel.orderComponent!!.orderDelivery!!.currencyId,
                                            viewModel.orderComponent!!.orderDelivery!!.deliveryPrice.toDouble()
                                        )
                                    Text(
                                        "سعر التوصيل: " + formatPrice(viewModel.orderComponent!!.orderDelivery!!.deliveryPrice) + " " + viewModel.orderComponent!!.orderDelivery!!.currencyName,
                                        Modifier.padding(8.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                HorizontalDivider()


                                Text(
                                    " الاجمالي النهائي : " + f.joinToString(
                                        separator = " و "
                                    ) { formatPrice(it.amount.toString()) + " " + it.currencyName },
                                    Modifier.padding(8.dp)
                                )
                            }

                        }
                    }

                    item {
                        CustomCard(
                            modifierBox = Modifier
                                .fillMaxSize()
                                .clickable {

                                }
                        ) {
                            Column {
                                CustomRow {
                                    Text("معلومات الدفع", fontSize = 20.sp)
                                    Image(
                                        painter = rememberImagePainter(R.drawable.epay),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(bottom = 8.dp)
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(
                                                    16.dp
                                                )
                                            )
                                            .clip(
                                                RoundedCornerShape(
                                                    16.dp
                                                )
                                            )
                                    )
                                }
                                HorizontalDivider(Modifier.padding(8.dp))

                                CustomRow2 {
                                    Text(" طريقة الدفع : ")
                                    Text(if (viewModel.orderComponent!!.orderDetail.paid == 0) "عند التوصيل" else "الكترونيا")
                                }

                                if (viewModel.orderComponent!!.orderPayment != null) {
                                    CustomRow {
                                        Text("معلومات الدفع الالكتروني:")
                                        AsyncImage(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .padding(10.dp),
                                            model = viewModel.orderComponent!!.orderPayment!!.paymentImage,
                                            contentDescription = null
                                        )
                                    }

                                    CustomRow2 {
                                        Text("الدفع عن طريق: ")
                                        Text(viewModel.orderComponent!!.orderPayment!!.paymentName)
                                    }
                                    CustomRow2 {
                                        Text("تاريخ الدفع: ")
                                        Text(viewModel.orderComponent!!.orderPayment!!.createdAt)
                                    }
                                } else {
                                    Text(
                                        if (viewModel.orderComponent!!.orderDetail.paid != 0) "لم يتم ادخال كود الشراء بعد" else "لم يتم الدفع بعد",
                                        Modifier.padding(8.dp)
                                    )
                                    if (viewModel.orderComponent!!.orderDetail.paid != 0) {
                                        CustomRow2 {
                                            MyTextField(
                                                hinty = "ادخل كود الشراء هنا"
                                            ) {
                                                viewModel.paidCode = it
                                            }

                                        }
                                        CustomIcon(Icons.AutoMirrored.Filled.Send) {
                                           viewModel.addPaidCode()
                                        }
                                    }
                                }
                            }


                        }
                        HorizontalDivider()
                    }

                    item {
                        CustomCard(
                            modifierBox = Modifier
                                .fillMaxSize()
                                .clickable {

                                }
                        ) {
                            Column {
                                CustomRow {
                                    Text("معلومات التوصيل", fontSize = 20.sp)
                                    Image(
                                        painter = rememberImagePainter(R.drawable.delivery),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(bottom = 8.dp)
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(
                                                    16.dp
                                                )
                                            )
                                            .clip(
                                                RoundedCornerShape(
                                                    16.dp
                                                )
                                            )
                                    )
                                }
                                HorizontalDivider(Modifier.padding(8.dp))
                                if (viewModel.orderComponent!!.orderDelivery != null) {

                                    if (viewModel.orderComponent!!.orderDelivery!!.deliveryMan != null) {
                                        CustomRow2 {
                                            Text("موصل الطلب: ")
                                            Text(viewModel.orderComponent!!.orderDelivery!!.deliveryMan!!.firstName,
                                                Modifier
                                                    .padding(8.dp)
                                                    .clickable {
                                                        //                                                        isShowChooseDeliveryMan = true
                                                    })
                                        }
                                    }
                                    Text("موقع توصيل الطلب: ")
                                    CustomRow2 {
                                        Text("شارع: ")
                                        Text(viewModel.orderComponent!!.orderDelivery!!.street)
                                    }
                                    CustomRow2 {
                                        Text("سعر التوصيل: ")
                                        Text(viewModel.orderComponent!!.orderDelivery!!.deliveryPrice + " ")
                                        Text(viewModel.orderComponent!!.orderDelivery!!.currencyName)
                                    }
                                    CustomRow2 {
                                        Text("الموقع على الخريطه: ")
                                        CustomIcon(Icons.Default.Place) {
                                            val googleMapsUrl =
                                                "https://www.google.com/maps?q=${viewModel.orderComponent!!.orderDelivery!!.latLng}"
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsUrl))
                                            startActivity(intent)
                                        }
                                    }

                                } else {
                                    Text("الاستلام الذاتي من المركز")
                                }
                            }

                        }
                        HorizontalDivider()
                        //                        IconDelete(ids) {
                        //                            deleteOrderProducts(ids){
                        //                                ids = emptyList()
                        //                            }
                        //                        }
                    }
                    itemsIndexed(viewModel.orderComponent!!.orderProducts) { index: Int, orderProduct: OrderProduct ->

                        CustomCard(
                            modifierBox = Modifier
                                .fillMaxSize()
                                .clickable {

                                }
                        ) {


                            Column {

                                //                                    Log.e(
                                //                                        "image", SingletonRemoteConfig.remoteConfig.BASE_IMAGE_URL +
                                //                                                SingletonRemoteConfig.remoteConfig.SUB_FOLDER_PRODUCT +
                                //                                                cartProduct.product.images.first()
                                //                                    )
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(orderProduct.productName)
                                    Text(orderProduct.optionName)
                                    //                                        CustomIcon(Icons.Outlined.MoreVert) {
                                    //                                            orderProductO =orderProduct
                                    //                                            isShowControllProduct = true
                                    //                                        }
                                    //                                    Checkbox(checked = ids.find { it == orderProduct.id } != null, onCheckedChange = {
                                    //                                        val itemC = ids.find { it == orderProduct.id}
                                    //                                        if (itemC == null) {
                                    //                                            ids = ids + orderProduct.id
                                    //                                        }else{
                                    //                                            ids = ids - orderProduct.id
                                    //                                        }
                                    //                                    })
                                    //                                            Text(
                                    //                                                modifier = Modifier.padding(8.dp),
                                    //                                                text = formatPrice(orderProduct.price.toString()) +" "+ orderProduct.currencyName,
                                    //                                                fontWeight = FontWeight.Bold,
                                    ////                                                color = MaterialTheme.colorScheme.primary
                                    //                                            )
                                    //                                            ADControll(
                                    //                                                orderProduct.product,
                                    //                                                option.productOption
                                    //                                            )
                                }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(formatPrice((orderProduct.price).toString()))
                                    Text(orderProduct.quantity.toString())
                                    Text(
                                        modifier = Modifier.padding(8.dp),
                                        text = formatPrice((orderProduct.price * orderProduct.quantity).toString()) + " " + orderProduct.currencyName,
                                        fontWeight = FontWeight.Bold,
                                        //                                                color = MaterialTheme.colorScheme.primary
                                    )
                                    //                                            ADControll(
                                    //                                                orderProduct.product,
                                    //                                                option.productOption
                                    //                                            )
                                }

                            }
                        }
                    }
                }
            }
        }
    }




}

@Serializable
data class OrderProduct(
    val id: Int,
    val productName: String,
    val currencyName: String,
    val optionName: String,
    val quantity:Int,
    val price:Double,
)

@Serializable
data class OrderDelivery(
    val id: Int,
    val latLng: String,
    val street: String,
    val deliveryMan: DeliveryMan?,
    val deliveryPrice :String,
    val currencyName: String,
    val currencyId: Int,
    val createdAt:String,
    val updatedAt:String,
)
@Serializable
data class OrderPayment(
    val id: Int,
    val paymentId: Int,
    val paymentName: String,
    val paymentImage: String,
    val createdAt:String,
    val updatedAt:String,
)

@Serializable
data class OrderDetail(
    val id: Int,
    val storeId: Int,
    val userId: Int,
    val withApp: Int,
    val paid: Int,
    val inStore: Int,
    val systemOrderNumber: String?,
    val situationId: Int,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class OrderComponent(
    val orderDelivery: OrderDelivery?,
    val orderProducts: List<OrderProduct> ,
    val orderPayment: OrderPayment?,
    val orderDetail: OrderDetail
){
    // Example function to update a product in the list
    fun updateOrderProduct(updatedProduct: OrderProduct): OrderComponent {
        // Replace the product with the same id or add it if it doesn't exist
        val updatedProducts = orderProducts.map { product ->
            if (product.id == updatedProduct.id) updatedProduct else product
        }
        return this.copy(orderProducts = updatedProducts)
    }
    fun updateOrderDelivery(updatedDelivery: OrderDelivery): OrderComponent {
//        // Replace the product with the same id or add it if it doesn't exist
//        val updatedProducts = orderProducts.map { product ->
//            if (product.id == updatedProduct.id) updatedProduct else product
//        }
        return this.copy(orderDelivery = updatedDelivery)
    }
    fun updateOrderDPayment(updated: OrderPayment): OrderComponent {
//        // Replace the product with the same id or add it if it doesn't exist
//        val updatedProducts = orderProducts.map { product ->
//            if (product.id == updatedProduct.id) updatedProduct else product
//        }
        return this.copy(orderPayment = updated)
    }


    fun filterProduct(ids: List<Int>): OrderComponent {
        // Filter out the products with ids present in the `ids` list
        val updatedProducts = orderProducts.filterNot { it.id in ids }
        // Return a new OrderComponent with the updated list of products
        return this.copy(orderProducts = updatedProducts)
    }
}

@Serializable
data class DeliveryMan(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val phone: String,
)