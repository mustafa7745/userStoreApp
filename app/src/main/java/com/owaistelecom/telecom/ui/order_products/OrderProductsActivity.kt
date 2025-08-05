package com.owaistelecom.telecom.ui.order_products

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.Singlton.FormBuilder
import com.owaistelecom.telecom.models.Coupon
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
    val appSession: AppSession,
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

//    @OptIn(ExperimentalFoundationApi::class)
//    @Composable
//    private fun OrderProductScreen(order: Order) {
//        val viewModel:OrderProductsViewModel = hiltViewModel()
//        val activity = LocalContext.current as Activity
//        LaunchedEffect(3) {
//            viewModel.order = order
//            viewModel.read()
//        }
//        OwaisTelecomTheme {
//            MainComposeRead("فاتورة الطلب: " + order.id,viewModel.stateController,{activity.finish()},{
//                viewModel.read()
//            }) {
//                LazyColumn(
//                    Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.Top,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    stickyHeader {
//                        CustomCard(
//                            modifierBox = Modifier
//                                .fillMaxSize()
//                                .clickable {
//
//                                }
//                        ) {
//                            Column {
//                                Text(
//                                    "اجمالي الطلبات: " + order.amounts.joinToString(
//                                        separator = " و "
//                                    ) { formatPrice(it.amount.toString()) + " " + it.currencyName },
//                                    Modifier.padding(8.dp)
//                                )
//                                var f = order.amounts.toMutableList()
//                                if (viewModel.orderComponent!!.orderDelivery != null) {
//                                    val s =
//                                        f.find { it.currencyId == viewModel.orderComponent!!.orderDelivery!!.currencyId }
//                                    if (s != null) {
//                                        f = f.map {
//                                            if (s.id == it.id) {
//                                                it.copy(amount = it.amount + viewModel.orderComponent!!.orderDelivery!!.deliveryPrice.toDouble())
//                                            } else
//                                                it
//                                        }.toMutableList()
//                                    } else
//                                        f += OrderAmount(
//                                            viewModel.orderComponent!!.orderDelivery!!.currencyId,
//                                            viewModel.orderComponent!!.orderDelivery!!.currencyName,
//                                            viewModel.orderComponent!!.orderDelivery!!.currencyId,
//                                            viewModel.orderComponent!!.orderDelivery!!.deliveryPrice.toDouble()
//                                        )
//                                    Text(
//                                        "سعر التوصيل: " + formatPrice(viewModel.orderComponent!!.orderDelivery!!.deliveryPrice) + " " + viewModel.orderComponent!!.orderDelivery!!.currencyName,
//                                        Modifier.padding(8.dp),
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                }
//                                HorizontalDivider()
//
//
//                                Text(
//                                    " الاجمالي النهائي : " + f.joinToString(
//                                        separator = " و "
//                                    ) { formatPrice(it.amount.toString()) + " " + it.currencyName },
//                                    Modifier.padding(8.dp)
//                                )
//                            }
//
//                        }
//                    }
//
//                    item {
//                        CustomCard(
//                            modifierBox = Modifier
//                                .fillMaxSize()
//                                .clickable {
//
//                                }
//                        ) {
//                            Column {
//                                CustomRow {
//                                    Text("معلومات الدفع", fontSize = 20.sp)
//                                    Image(
//                                        painter = rememberImagePainter(R.drawable.epay),
//                                        contentDescription = null,
//                                        modifier = Modifier
//                                            .size(50.dp)
//                                            .padding(bottom = 8.dp)
//                                            .border(
//                                                1.dp,
//                                                MaterialTheme.colorScheme.primary,
//                                                RoundedCornerShape(
//                                                    16.dp
//                                                )
//                                            )
//                                            .clip(
//                                                RoundedCornerShape(
//                                                    16.dp
//                                                )
//                                            )
//                                    )
//                                }
//                                HorizontalDivider(Modifier.padding(8.dp))
//
//                                CustomRow2 {
//                                    Text(" طريقة الدفع : ")
//                                    Text(if (viewModel.orderComponent!!.orderDetail.paid == 0) "عند التوصيل" else "الكترونيا")
//                                }
//
//                                if (viewModel.orderComponent!!.orderPayment != null) {
//                                    CustomRow {
//                                        Text("معلومات الدفع الالكتروني:")
//                                        AsyncImage(
//                                            modifier = Modifier
//                                                .size(50.dp)
//                                                .padding(10.dp),
//                                            model = viewModel.orderComponent!!.orderPayment!!.paymentImage,
//                                            contentDescription = null
//                                        )
//                                    }
//
//                                    CustomRow2 {
//                                        Text("الدفع عن طريق: ")
//                                        Text(viewModel.orderComponent!!.orderPayment!!.paymentName)
//                                    }
//                                    CustomRow2 {
//                                        Text("تاريخ الدفع: ")
//                                        Text(viewModel.orderComponent!!.orderPayment!!.createdAt)
//                                    }
//                                } else {
//                                    Text(
//                                        if (viewModel.orderComponent!!.orderDetail.paid != 0) "لم يتم ادخال كود الشراء بعد" else "لم يتم الدفع بعد",
//                                        Modifier.padding(8.dp)
//                                    )
//                                    if (viewModel.orderComponent!!.orderDetail.paid != 0) {
//                                        CustomRow2 {
//                                            MyTextField(
//                                                hinty = "ادخل كود الشراء هنا"
//                                            ) {
//                                                viewModel.paidCode = it
//                                            }
//
//                                        }
//                                        CustomIcon(Icons.AutoMirrored.Filled.Send) {
//                                           viewModel.addPaidCode()
//                                        }
//                                    }
//                                }
//                            }
//
//
//                        }
//                        HorizontalDivider()
//                    }
//
//                    item {
//                        CustomCard(
//                            modifierBox = Modifier
//                                .fillMaxSize()
//                                .clickable {
//
//                                }
//                        ) {
//                            Column {
//                                CustomRow {
//                                    Text("معلومات التوصيل", fontSize = 20.sp)
//                                    Image(
//                                        painter = rememberImagePainter(R.drawable.delivery),
//                                        contentDescription = null,
//                                        modifier = Modifier
//                                            .size(50.dp)
//                                            .padding(bottom = 8.dp)
//                                            .border(
//                                                1.dp,
//                                                MaterialTheme.colorScheme.primary,
//                                                RoundedCornerShape(
//                                                    16.dp
//                                                )
//                                            )
//                                            .clip(
//                                                RoundedCornerShape(
//                                                    16.dp
//                                                )
//                                            )
//                                    )
//                                }
//                                HorizontalDivider(Modifier.padding(8.dp))
//                                if (viewModel.orderComponent!!.orderDelivery != null) {
//
//                                    if (viewModel.orderComponent!!.orderDelivery!!.deliveryMan != null) {
//                                        CustomRow2 {
//                                            Text("موصل الطلب: ")
//                                            Text(viewModel.orderComponent!!.orderDelivery!!.deliveryMan!!.firstName,
//                                                Modifier
//                                                    .padding(8.dp)
//                                                    .clickable {
//                                                        //                                                        isShowChooseDeliveryMan = true
//                                                    })
//                                        }
//                                    }
//                                    Text("موقع توصيل الطلب: ")
//                                    CustomRow2 {
//                                        Text("شارع: ")
//                                        Text(viewModel.orderComponent!!.orderDelivery!!.street)
//                                    }
//                                    CustomRow2 {
//                                        Text("سعر التوصيل: ")
//                                        Text(viewModel.orderComponent!!.orderDelivery!!.deliveryPrice + " ")
//                                        Text(viewModel.orderComponent!!.orderDelivery!!.currencyName)
//                                    }
//                                    CustomRow2 {
//                                        Text("الموقع على الخريطه: ")
//                                        CustomIcon(Icons.Default.Place) {
//                                            val googleMapsUrl =
//                                                "https://www.google.com/maps?q=${viewModel.orderComponent!!.orderDelivery!!.latLng}"
//                                            val intent =
//                                                Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsUrl))
//                                            startActivity(intent)
//                                        }
//                                    }
//
//                                } else {
//                                    Text("الاستلام الذاتي من المركز")
//                                }
//                            }
//
//                        }
//                        HorizontalDivider()
//                        //                        IconDelete(ids) {
//                        //                            deleteOrderProducts(ids){
//                        //                                ids = emptyList()
//                        //                            }
//                        //                        }
//                    }
//                    itemsIndexed(viewModel.orderComponent!!.orderProducts) { index: Int, orderProduct: OrderProduct ->
//
//                        CustomCard(
//                            modifierBox = Modifier
//                                .fillMaxSize()
//                                .clickable {
//
//                                }
//                        ) {
//
//
//                            Column {
//
//                                //                                    Log.e(
//                                //                                        "image", SingletonRemoteConfig.remoteConfig.BASE_IMAGE_URL +
//                                //                                                SingletonRemoteConfig.remoteConfig.SUB_FOLDER_PRODUCT +
//                                //                                                cartProduct.product.images.first()
//                                //                                    )
//                                Row(
//                                    Modifier
//                                        .fillMaxWidth()
//                                        .padding(8.dp),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text(orderProduct.productName)
//                                    Text(orderProduct.optionName)
//                                    //                                        CustomIcon(Icons.Outlined.MoreVert) {
//                                    //                                            orderProductO =orderProduct
//                                    //                                            isShowControllProduct = true
//                                    //                                        }
//                                    //                                    Checkbox(checked = ids.find { it == orderProduct.id } != null, onCheckedChange = {
//                                    //                                        val itemC = ids.find { it == orderProduct.id}
//                                    //                                        if (itemC == null) {
//                                    //                                            ids = ids + orderProduct.id
//                                    //                                        }else{
//                                    //                                            ids = ids - orderProduct.id
//                                    //                                        }
//                                    //                                    })
//                                    //                                            Text(
//                                    //                                                modifier = Modifier.padding(8.dp),
//                                    //                                                text = formatPrice(orderProduct.price.toString()) +" "+ orderProduct.currencyName,
//                                    //                                                fontWeight = FontWeight.Bold,
//                                    ////                                                color = MaterialTheme.colorScheme.primary
//                                    //                                            )
//                                    //                                            ADControll(
//                                    //                                                orderProduct.product,
//                                    //                                                option.productOption
//                                    //                                            )
//                                }
//                                Row(
//                                    Modifier
//                                        .fillMaxWidth()
//                                        .padding(8.dp),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text(formatPrice((orderProduct.price).toString()))
//                                    Text(orderProduct.quantity.toString())
//                                    Text(
//                                        modifier = Modifier.padding(8.dp),
//                                        text = formatPrice((orderProduct.price * orderProduct.quantity).toString()) + " " + orderProduct.currencyName,
//                                        fontWeight = FontWeight.Bold,
//                                        //                                                color = MaterialTheme.colorScheme.primary
//                                    )
//                                    //                                            ADControll(
//                                    //                                                orderProduct.product,
//                                    //                                                option.productOption
//                                    //                                            )
//                                }
//
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    @Composable
    fun OrderStatusTimeline(orderStatus: OrderStatus) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // عنوان الحالة الرئيسية
            Text(
                text = "🧾 حالة الطلب: ${orderStatus.status}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32), // أخضر غامق
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // استخراج الحالات
            val statusPairs: List<Pair<String, Boolean?>> = orderStatus.timeLineList
                .flatMap { map -> map.entries }
                .map { it.toPair() }


            // عرض الحالات بشكل مرتب
            statusPairs.forEachIndexed {  index,( status, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // رقم تسلسلي
                    Text(
                        text = "${index + 1}.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.width(24.dp)
                    )

                    // أيقونة الحالة
                    Icon(
                        imageVector = when (value) {
                            true -> Icons.Default.CheckCircle
                            false -> Icons.Default.Cancel
                            null -> Icons.Default.Schedule
                        },
                        contentDescription = null,
                        tint = when (value) {
                            true -> Color(0xFF4CAF50) // أخضر
                            false -> Color(0xFFE53935) // أحمر
                            null -> Color(0xFFFFA000) // برتقالي
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .padding(end = 8.dp)
                    )

                    // نص الحالة
                    Text(
                        text = status,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun OrderProductScreen(order: Order) {
        val viewModel: OrderProductsViewModel = hiltViewModel()
        val activity = LocalContext.current as Activity

        LaunchedEffect(order.id) {
            viewModel.order = order
            viewModel.read()
        }

        OwaisTelecomTheme {
            MainComposeRead(
                name = "فاتورة الطلب: ${order.id}",
                stateController = viewModel.stateController,
                back = { activity.finish() },
                read = { viewModel.read() }
            ) {
                val orderComponent = viewModel.orderComponent ?: return@MainComposeRead

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    val orderStatus = orderComponent.orderStatus
                    item {
                        OrderStatusTimeline(orderStatus)
                    }
//
//                    statusPairs.forEachIndexed { index, (status, value) ->  // استخدمنا forEachIndexed
//                        item {
//                            Row(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .padding(vertical = 6.dp),
//                                verticalAlignment = Alignment.CenterVertically
//                            ) {
//                                // رقم تسلسلي صحيح
//                                Text(
//                                    text = "${index + 1}.",  // نعرض الرقم التسلسلي هنا
//                                    fontSize = 14.sp,
//                                    color = Color.Gray,
//                                    modifier = Modifier.width(24.dp)
//                                )
//
//                                // أيقونة الحالة
//                                Icon(
//                                    imageVector = when (value) {
//                                        true -> Icons.Default.CheckCircle
//                                        false -> Icons.Default.Cancel
//                                        null -> Icons.Default.Schedule
//                                    },
//                                    contentDescription = null,
//                                    tint = when (value) {
//                                        true -> Color(0xFF4CAF50) // أخضر
//                                        false -> Color(0xFFE53935) // أحمر
//                                        null -> Color(0xFFFFA000) // برتقالي
//                                    },
//                                    modifier = Modifier
//                                        .size(20.dp)
//                                        .padding(end = 8.dp)
//                                )
//
//                                // نص الحالة
//                                Text(
//                                    text = status,
//                                    fontSize = 15.sp,
//                                    color = Color.Black
//                                )
//                            }
//                        }
//
//                    }






                    val column0Weight = 0.07f // 30%
                    val column1Weight = 0.44f // 30%
                    val column2Weight = 0.12f // 70%
                    val column3Weight = 0.15f // 30%
                    val column4Weight = 0.22f // 70%


                    item {
                        Row(Modifier.background(Color.Gray)) {
                            TableCellHeader(text = "#", weight = column0Weight)
                            TableCellHeader(text = "الصنف", weight = column1Weight)
                            TableCellHeader(text = "الكمية", weight = column2Weight)
                            TableCellHeader(text = "السعر", weight = column3Weight)
                            TableCellHeader(text = "الاجمالي", weight = column4Weight)
                        }
                    }
                    ///
                    itemsIndexed( viewModel.orderComponent!!.orderProducts) { index, orderProduct ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                        )
                        {

                            TableCell(
                                text = (index + 1).toString(), weight = column0Weight
                            )
                            TableCell(
                                text = orderProduct.productName, weight = column1Weight
                            )
                            TableCell(
                                text = orderProduct.quantity.toString(),
                                weight = column2Weight
                            )
                            TableCell(
                                text = formatPrice(orderProduct.price.toString()) , weight = column3Weight
                            )
                            TableCell(
                                text = formatPrice ((orderProduct.price * orderProduct.quantity).toString()),
                                weight = column4Weight
                            )

                        }
                    }

                    if (viewModel.orderComponent!!.orderCoupon != null) {
                        val coupon = viewModel.orderComponent!!.orderCoupon!!
                        val index = viewModel.orderComponent!!.orderProducts.size + 1
                        val currencyName = viewModel.appSession .home.storeCurrencies.find { it.currencyId == coupon.currencyId }?.currencyName
                        val discountText = if (coupon.type == 1) {
                            "${coupon.amount}% خصم"
                        } else {
                            "${formatPrice(coupon.amount.toString())} $currencyName خصم"
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = false) {}, // غير قابل للنقر حاليًا
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(
                                    text = index.toString(),
                                    weight = column0Weight
                                )
                                TableCell(
                                    text = "خصم",
                                    weight = column1Weight
                                )
                                TableCell(
                                    text = discountText,
                                    weight = 0.49f
                                )
                            }
                        }
                    }

                    if (viewModel.orderComponent!!.orderDelivery != null){
                        val delivery =viewModel.orderComponent!!.orderDelivery!!
                        var index = viewModel.orderComponent!!.orderProducts.size
                        if (viewModel.orderComponent!!.orderCoupon != null){
                            index +2;
                        }
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = false) {}, // غير قابل للنقر حاليًا
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(
                                    text = index.toString(),
                                    weight = column0Weight
                                )
                                TableCell(
                                    text = "توصيل الطلب",
                                    weight = column1Weight
                                )
                                TableCell(
                                    text = formatPrice(delivery.deliveryPrice.toString()) + " "+ delivery.currencyName,
                                    weight = 0.49f
                                )
                            }
                        }
                    }


                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F8E9)) // أخضر فاتح جداً
                                .border(1.dp, Color(0xFF81C784), RoundedCornerShape(12.dp)) // حدود بلون أخضر لطيف
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {

                                Text(
                                    text = viewModel.order.amounts.joinToString(
                                        separator = " و "
                                    ) {
                                        "${formatPrice(it.amount.toString())} ${it.currencyName}"
                                    },
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = Color(0xFF2E7D32),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                }
//                LazyColumn(
//                    modifier = Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.Top,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    stickyHeader {
//                        OrderSummaryCard(order, viewModel)
//                    }
//
//                    item {
//                        PaymentInfoCard(viewModel)
//                    }
//
//                    item {
//                        DeliveryInfoCard(viewModel)
//                    }
//
//                    items(viewModel.orderComponent?.orderProducts ?: emptyList()) { product ->
//                        ProductItemCard(product)
//                    }
//                }
            }
        }
    }

    @Composable
    fun ProductItemCard(orderProduct: OrderProduct) {
        CustomCard(modifierBox = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(orderProduct.productName)
                    Text(orderProduct.optionName)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatPrice(orderProduct.price.toString()))
                    Text(orderProduct.quantity.toString())
                    Text(
                        formatPrice((orderProduct.price * orderProduct.quantity).toString()) +
                                " " + orderProduct.currencyName,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    @Composable
    fun OrderSummaryCard(order: Order, viewModel: OrderProductsViewModel) {
        CustomCard(modifierBox = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "اجمالي الطلبات: " + order.amounts.joinToString(" و ") {
                        formatPrice(it.amount.toString()) + " " + it.currencyName
                    }
                )

                val delivery = viewModel.orderComponent?.orderDelivery
                val updatedAmounts = order.amounts.toMutableList()

                delivery?.let {
                    val index = updatedAmounts.indexOfFirst { a -> a.currencyId == it.currencyId }
                    if (index >= 0) {
                        val current = updatedAmounts[index]
                        updatedAmounts[index] =
                            current.copy(amount = current.amount + it.deliveryPrice.toDouble())
                    } else {
                        updatedAmounts += OrderAmount(
                            it.currencyId, it.currencyName, it.currencyId, it.deliveryPrice.toDouble()
                        )
                    }

                    Text(
                        "سعر التوصيل: ${formatPrice(it.deliveryPrice)} ${it.currencyName}",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))

                Text(
                    text = "الاجمالي النهائي: " + updatedAmounts.joinToString(" و ") {
                        formatPrice(it.amount.toString()) + " " + it.currencyName
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    @Composable
    fun PaymentInfoCard(viewModel: OrderProductsViewModel) {
        val detail = viewModel.orderComponent?.orderDetail ?: return
        val payment = viewModel.orderComponent?.orderPayment

        CustomCard(modifierBox = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text("معلومات الدفع", fontSize = 18.sp, fontWeight = FontWeight.Medium)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                CustomRow2 {
                    Text("طريقة الدفع: ")
                    Text(if (detail.paid == 0) "عند التوصيل" else "الكترونيا")
                }

                if (payment != null) {
                    CustomRow {
                        Text("معلومات الدفع الالكتروني:")
                        AsyncImage(
                            model = payment.paymentImage,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp).padding(6.dp)
                        )
                    }

                    CustomRow2 {
                        Text("الدفع عن طريق: ")
                        Text(payment.paymentName)
                    }

                    CustomRow2 {
                        Text("تاريخ الدفع: ")
                        Text(payment.createdAt)
                    }
                } else {
                    Text(
                        if (detail.paid != 0) "لم يتم ادخال كود الشراء بعد" else "لم يتم الدفع بعد",
                        modifier = Modifier.padding(top = 6.dp)
                    )
                    if (detail.paid != 0) {
                        CustomRow2 {
                            MyTextField(hinty = "ادخل كود الشراء هنا") {
                                viewModel.paidCode = it
                            }
                            CustomIcon(Icons.AutoMirrored.Filled.Send) {
                                viewModel.addPaidCode()
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun DeliveryInfoCard(viewModel: OrderProductsViewModel) {
        val delivery = viewModel.orderComponent?.orderDelivery

        CustomCard(modifierBox = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text("معلومات التوصيل", fontSize = 18.sp, fontWeight = FontWeight.Medium)

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                if (delivery != null) {
                    delivery.deliveryMan?.let {
                        CustomRow2 {
                            Text("موصل الطلب: ")
                            Text(it.firstName)
                        }
                    }

                    CustomRow2 {
                        Text("الشارع: ")
                        Text(delivery.street)
                    }

                    CustomRow2 {
                        Text("سعر التوصيل: ")
                        Text("${delivery.deliveryPrice} ${delivery.currencyName}")
                    }

                    CustomRow2 {
                        Text("الموقع على الخريطة:")
                        CustomIcon(Icons.Default.Place) {
//                            val url = "https://www.google.com/maps?q=${delivery.latLng}"
//                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                            LocalContext.current.startActivity(intent)
                        }
                    }
                } else {
                    Text("الاستلام الذاتي من المركز")
                }
            }
        }
    }



    @Composable
    fun RowScope.TableCellHeader(
        text: String, weight: Float
    ) {
        Text(
            modifier = Modifier
                .border(1.dp, Color.Black)
                .weight(weight)
                .padding(8.dp),
            text = text,
            fontSize = 10.sp,
            color = Color.White,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
    }

    @Composable
    fun RowScope.TableCell(
        text: String, weight: Float
    )
    {
        Text(

            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color.Black)
                .weight(weight),
            textAlign= TextAlign.Center,

            text = text,
            fontSize = 10.sp,
            overflow = TextOverflow.Ellipsis, // Allow overflow to be visible
            maxLines = Int.MAX_VALUE, // Allow multiple lines
        )

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
data class OrderStatus(
    val status: String,
    val timeLineList:List<Map<String, Boolean?>>
)

@Serializable
data class OrderComponent(
    val orderDelivery: OrderDelivery?,
    val orderProducts: List<OrderProduct> ,
    val orderPayment: OrderPayment?,
    val orderDetail: OrderDetail,
    val orderCoupon: Coupon?,
    val orderStatus:OrderStatus

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