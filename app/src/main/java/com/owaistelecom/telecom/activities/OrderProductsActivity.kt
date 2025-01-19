package com.owaistelecom.telecom.activities

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.shared.CustomCard
import com.owaistelecom.telecom.shared.CustomIcon
import com.owaistelecom.telecom.shared.CustomRow
import com.owaistelecom.telecom.shared.CustomRow2
import com.owaistelecom.telecom.shared.IconDelete
import com.owaistelecom.telecom.shared.MainCompose1
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.MyTextField
import com.owaistelecom.telecom.shared.RequestServer
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.shared.builderForm3
import com.owaistelecom.telecom.shared.formatPrice
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import kotlinx.serialization.Serializable


class OrderProductsActivity : ComponentActivity() {
    val stateController = StateController()
    val requestServer = RequestServer(this)
    private var orderProducts by mutableStateOf<List<OrderProduct>>(listOf())
    private var orderComponent by mutableStateOf<OrderComponent?>(null)
    lateinit var order: Order
    lateinit var orderProductO: OrderProduct
    var paidCode by mutableStateOf<String>("")

    var isShowControllProduct by mutableStateOf(false)

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        read()

        setContent {
            OwaisTelecomTheme  {
                MainCompose1(
                    0.dp, stateController, this,{
                         read()
                    }

                ) {
                    var ids by remember { mutableStateOf<List<Int>>(emptyList()) }
                    LazyColumn(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            CustomCard(
                                modifierBox = Modifier
                                    .fillMaxSize()
                                    .clickable {

                                    }
                            ){
                                Column {
                                    CustomRow  {
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
                                                    ))
                                        )
                                    }
                                    HorizontalDivider(Modifier.padding(8.dp))

                                    CustomRow2 {
                                        Text(" طريقة الدفع : ")
                                        Text(if (orderComponent!!.orderDetail.paid == 0) "عند التوصيل" else "الكترونيا")
                                    }

                                    if (orderComponent!!.orderPayment != null){
                                        CustomRow {
                                            Text("معلومات الدفع الالكتروني:")
                                            AsyncImage(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .padding(10.dp),
                                                model = orderComponent!!.orderPayment!!.paymentImage,
                                                contentDescription = null
                                            )
                                        }

                                        CustomRow2 {
                                            Text("الدفع عن طريق: ")
                                            Text(orderComponent!!.orderPayment!!.paymentName)
                                        }
                                        CustomRow2 {
                                            Text("تاريخ الدفع: ")
                                            Text(orderComponent!!.orderPayment!!.createdAt)
                                        }
                                    }
                                    else{
                                        Text(  if (orderComponent!!.orderDetail.paid != 0) "لم يتم ادخال كود الشراء بعد" else "لم يتم الدفع بعد" ,Modifier.padding(8.dp))
                                        if (orderComponent!!.orderDetail.paid != 0){
                                            CustomRow2 {
                                                MyTextField(
                                                    hinty = "ادخل كود الشراء هنا"
                                                ) {
                                                    paidCode = it
                                                }

                                            }
                                            CustomIcon(Icons.AutoMirrored.Filled.Send) {
                                                addPaidCode()
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
                            ){
                                Column {
                                    CustomRow  {
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
                                                    ))
                                        )
                                    }
                                    HorizontalDivider(Modifier.padding(8.dp))
                                    if (orderComponent!!.orderDelivery != null){

                                        if (orderComponent!!.orderDelivery!!.deliveryMan != null){
                                            CustomRow2 {
                                                Text("موصل الطلب: ")
                                                Text(orderComponent!!.orderDelivery!!.deliveryMan!!.firstName,
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
                                            Text(orderComponent!!.orderDelivery!!.street)
                                        }
                                        CustomRow2 {
                                            Text("الموقع على الخريطه: ")
                                            CustomIcon(Icons.Default.Place) {
                                                val googleMapsUrl = "https://www.google.com/maps?q=${orderComponent!!.orderDelivery!!.latLng}"
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsUrl))
                                                startActivity(intent)}
                                        }

                                    }else{
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
                        itemsIndexed(orderComponent!!.orderProducts) { index: Int, orderProduct:OrderProduct ->

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
                                        CustomIcon(Icons.Outlined.MoreVert) {
                                            orderProductO =orderProduct
                                            isShowControllProduct = true
                                        }
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
                                            text = formatPrice((orderProduct.price * orderProduct.quantity).toString()) +" "+ orderProduct.currencyName,
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

                    if (isShowControllProduct) modalControll()
                }
            }
        }
    }

    fun read() {
        stateController.startRead()

        val body = builderForm3()
            .addFormDataPart("orderId",order.id.toString())
            .build()

        requestServer.request2(body, "getOrderProducts", { code, fail ->
            stateController.errorStateRead(fail)
        }
        ) { data ->
            orderComponent =
                MyJson.IgnoreUnknownKeys.decodeFromString(
                    data
                )

            stateController.successState()
        }
    }

    fun changeQuantity(id:String,qnt:String) {
        stateController.startAud()

        val body = builderForm3()
            .addFormDataPart("orderId",order.id.toString())
            .addFormDataPart("id",id)
            .addFormDataPart("qnt",qnt)
            .build()

        requestServer.request2(body, "updateOrderProductQuantity", { code, fail ->
            stateController.errorStateAUD(fail)
        }
        ) { data ->
            val orderProduct:OrderProduct =
                MyJson.IgnoreUnknownKeys.decodeFromString(
                    data
                )

          orderProducts =  orderProducts.map {
                if (it.id == orderProduct.id){
                    orderProduct
                }else
                    it
            }

            isShowControllProduct = false
            stateController.successStateAUD()
        }
    }
    fun addPaidCode() {
        stateController.startAud()

        val body = builderForm3()
            .addFormDataPart("orderId",order.id.toString())
            .addFormDataPart("paidCode",paidCode)
            .build()

        requestServer.request2(body, "addPaidCode", { code, fail ->
            stateController.errorStateAUD(fail)
        }
        ) { data ->
            val orderPayment:OrderPayment =
                MyJson.IgnoreUnknownKeys.decodeFromString(
                    data
                )

            orderComponent = orderComponent!!.updateOrderDPayment(orderPayment)
//          orderComponent!!.copy(
//
//
//          ) =  orderComponent!!.orderProducts .map {
//                if (it.id == orderProduct.id){
//                    orderProduct
//                }else
//                    it
//            }

//            isShowChooseDeliveryMan = false
            stateController.successStateAUD()
        }
    }

    fun deleteOrderProducts(ids:List<Int>,onDone:()->Unit) {
        stateController.startAud()

        val body = builderForm3()
            .addFormDataPart("ids",ids.toString())
            .build()

        requestServer.request2(body, "deleteOrderProducts", { code, fail ->
            stateController.errorStateAUD(fail)
        }
        ) { data ->
//            val orderProduct:OrderProduct =
//                MyJson.IgnoreUnknownKeys.decodeFromString(
//                    data
//                )

            orderProducts =  orderProducts.filterNot {
                it.id in ids
            }

            onDone()
            stateController.successStateAUD()
        }
    }




    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun modalControll() {
        var product by remember { mutableStateOf(orderProductO) }
        ModalBottomSheet(
            onDismissRequest = { isShowControllProduct = false }) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp)
            ){

                LazyColumn(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        CustomCard(
                            modifierBox = Modifier
                                .fillMaxSize()
                                .clickable {

                                }
                        ){
                            Column {
                                CustomRow  {
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
                                                ))
                                    )
                                }
                                HorizontalDivider(Modifier.padding(8.dp))

                                CustomRow2 {
                                    Text(" طريقة الدفع : ")
                                    Text(if (orderComponent!!.orderDetail.paid == 0) "عند التوصيل" else "الكترونيا")
                                }

                                if (orderComponent!!.orderPayment != null){
                                    CustomRow {
                                        Text("معلومات الدفع الالكتروني:")
                                        AsyncImage(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .padding(10.dp),
                                            model = orderComponent!!.orderPayment!!.paymentImage,
                                            contentDescription = null
                                        )
                                    }

                                    CustomRow2 {
                                        Text("الدفع عن طريق: ")
                                        Text(orderComponent!!.orderPayment!!.paymentName)
                                    }
                                    CustomRow2 {
                                        Text("تاريخ الدفع: ")
                                        Text(orderComponent!!.orderPayment!!.createdAt)
                                    }
                                }
                                else{
                                    Text(  if (orderComponent!!.orderDetail.paid != 0) "لم يتم ادخال كود الشراء بعد" else "لم يتم الدفع بعد" ,Modifier.padding(8.dp))
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
                        ){
                            Column {
                                CustomRow  {
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
                                                ))
                                    )
                                }
                                HorizontalDivider(Modifier.padding(8.dp))
                                if (orderComponent!!.orderDelivery != null){

                                    if (orderComponent!!.orderDelivery!!.deliveryMan != null){
                                        CustomRow2 {
                                            Text("موصل الطلب: ")
                                            Text(orderComponent!!.orderDelivery!!.deliveryMan!!.firstName,
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
                                        Text(orderComponent!!.orderDelivery!!.street)
                                    }
                                    CustomRow2 {
                                        Text("الموقع على الخريطه: ")
                                        CustomIcon(Icons.Default.Place) {
                                            val googleMapsUrl = "https://www.google.com/maps?q=${orderComponent!!.orderDelivery!!.latLng}"
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsUrl))
                                            startActivity(intent)}
                                    }

                                }else{
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
                    itemsIndexed(orderComponent!!.orderProducts) { index: Int, orderProduct:OrderProduct ->

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
                                    CustomIcon(Icons.Outlined.MoreVert) {
                                        orderProductO =orderProduct
                                        isShowControllProduct = true
                                    }
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
                                        text = formatPrice((orderProduct.price * orderProduct.quantity).toString()) +" "+ orderProduct.currencyName,
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