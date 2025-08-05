package com.owaistelecom.telecom.ui.cart_preview

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.owaistelecom.telecom.shared.ADControll2
import com.owaistelecom.telecom.shared.CustomCard
import com.owaistelecom.telecom.shared.CustomImageView
import com.owaistelecom.telecom.shared.MainComposeAUD
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.MyTextField
import com.owaistelecom.telecom.shared.formatPrice
import com.owaistelecom.telecom.ui.add_location.AddLocationActivity
import com.owaistelecom.telecom.ui.add_to_cart2.CartProduct2
import com.owaistelecom.telecom.ui.login.goToDashboard
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import kotlinx.coroutines.launch

@Composable
fun CartPreviewScreen(componentActivity: ComponentActivity,onAddLocation:()->Unit){
    val viewModel: CartPreviewViewModel = hiltViewModel()
    viewModel.title = "عرض السلة"
    if (viewModel.shouldExitToOrder){
        goToDashboard(componentActivity)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val json = result.data!!.getStringExtra("location")
            if (!json.isNullOrEmpty()) {
                val location = MyJson.IgnoreUnknownKeys.decodeFromString<Location>(json)
                viewModel.locations += location
                viewModel.selectedLocation = location
//                viewModel.isShowReadLocations = true
            }
        }
    }


    OwaisTelecomTheme {
       MainComposeAUD(viewModel.title,viewModel.stateController,{
           if (viewModel.cartView) {
               componentActivity.finish()
           } else
               viewModel.cartView = true
       }){
//            if (!viewModel.cartView)
//                PulsingBoxConfirm(componentActivity)
            if (viewModel.cartView) {

                MainContentCartPreview()
            } else {
                viewModel.title = "تأكيد الطلب"
                MainContentOrderPreview()
                if (viewModel.isShowReadLocations) modalShowLocations(launcher,componentActivity)
                if (viewModel.isShowSelectPaymentMethod) ChoosePaymentMethod()
                if (viewModel.isShowShowPaymentTypes) ChoosePaymentTypes()
                if (viewModel.isShowChooseCouponCode) ChooseCouponCode()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun MainContentCartPreview() {
    val viewModel: CartPreviewViewModel = hiltViewModel()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyColumn(
            Modifier.padding(bottom = 50.dp),
            content = {

                stickyHeader {
                    Card(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
//                                .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text =viewModel.getSelectedStore().name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (!viewModel.isEmptyCartProducts()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "الإجمالي: ${viewModel.getAllCartProductsSum()}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                CustomImageView(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(
                                            1.dp,
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(12.dp)
                                        ),
                                    imageUrl = viewModel.getRemoteConfig().BASE_IMAGE_URL +
                                            viewModel.getRemoteConfig().SUB_FOLDER_STORE_LOGOS +
                                            viewModel.getSelectedStore().logo,
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider()

                            if (!viewModel.isEmptyCartProducts()
                            )
                                Button(
                                    onClick = { viewModel.cartView = false },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text(text = "متابعة", style = MaterialTheme.typography.labelLarge)

                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                                }
                        }
                    }
                }


                itemsIndexed(viewModel.getAllCartProducts()) { index: Int, cartProduct: CartProduct2 ->

                    Card (
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
//                                .background(MaterialTheme.colorScheme.surface)
                            .clickable { }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = cartProduct.product.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "عدد الخيارات: ${cartProduct.cartProductOption.size}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                }

//                                if (cartProduct.product.images.isNotEmpty()) {
//                                    CustomImageView(
//                                        modifier = Modifier
//                                            .size(60.dp)
//                                            .clip(RoundedCornerShape(12.dp))
////                                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)
////                                                )
//                                        ,
//                                        imageUrl = viewModel.getRemoteConfig().BASE_IMAGE_URL +
//                                                viewModel.getRemoteConfig().SUB_FOLDER_PRODUCT +
//                                                cartProduct.product.images.first().image,
//                                    )
//                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()

                            cartProduct.cartProductOption.forEach { option ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val currencyName = viewModel.appSession.home.storeCurrencies
                                        .firstOrNull { it.currencyId == option.option.currencyId }
                                        ?.currencyName ?: ""
                                    Column {
                                        Text(
                                            text = option.option.name,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${formatPrice(option.option.price.toString())} ${currencyName} ",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    ADControll2(
                                        option.option
                                    )
                                }
                            }
                        }
                    }

//
//                        CustomCard(
//                            modifierBox = Modifier.fillMaxSize().clickable {
//
//                            }
//                        ) {
//
//                            Column {
//                                Row(
//                                    Modifier.fillMaxWidth(),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
////                                    Log.e(
////                                        "image", CustomSingleton.remoteConfig.BASE_IMAGE_URL +
////                                                CustomSingleton.remoteConfig.SUB_FOLDER_PRODUCT +
////                                                cartProduct.product.images.first()
////                                    )
//                                    Text(cartProduct.product.productName)
//                                    if (cartProduct.product.images.isNotEmpty())
//                                    CustomImageView(
//                                        modifier = Modifier
//                                            .size(50.dp)
//                                            .padding(8.dp)
//                                            .clickable {
//
//                                            },
//                                        context = this@CartPreviewActivity,
//                                        imageUrl = CustomSingleton.remoteConfig.BASE_IMAGE_URL +
//                                                CustomSingleton.remoteConfig.SUB_FOLDER_PRODUCT +
//                                                cartProduct.product.images.first().image,
//                                        okHttpClient = requestServer.createOkHttpClientWithCustomCert()
//                                    )
//
//                                }
//                                HorizontalDivider()
//                                cartProduct.cartProductOption.forEach { cartProductOption ->
//                                    Row(
//                                        Modifier.fillMaxWidth().padding(8.dp),
//                                        verticalAlignment = Alignment.CenterVertically,
//                                        horizontalArrangement = Arrangement.SpaceBetween
//                                    ) {
//                                        Text(cartProductOption.productOption.name)
//                                        Text(
//                                            modifier = Modifier.padding(8.dp),
//                                            text = formatPrice(cartProductOption.productOption.price) +" "+ cartProductOption.productOption.currency.name ,
//                                            fontWeight = FontWeight.Bold,
//                                            color = MaterialTheme.colorScheme.primary
//                                        )
//                                        ADControll(
//                                            cartProduct.product,
//                                            cartProductOption.productOption
//                                        )
//                                    }
//                                }
//                            }
//
//                        }
                }
            })
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MainContentOrderPreview() {
    val viewModel: CartPreviewViewModel = hiltViewModel()
    LazyColumn(
        Modifier,
        content = {
            stickyHeader {
                OrderInvoiceSection()
//                        CustomCard(modifierBox = Modifier) {
//                            Column {
//                                Row(
//                                    Modifier.fillMaxWidth(),
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    horizontalArrangement = Arrangement.SpaceBetween
//                                ) {
//                                    Text("فاتورة الطلب",Modifier.padding(8.dp))
//                                }
//                                HorizontalDivider()
//                                if (SingletonCart.getAllCartProducts(SingletonStores.selectedStore)
//                                        .isNotEmpty()
//                                ) {
//
//                                    Text(
//                                        "اجمالي الطلبات: " + SingletonCart.getAllCartProductsSum(
//                                            SingletonStores.selectedStore
//                                        ),Modifier.padding(8.dp)
//                                    )
//                                    if (selectedLocation!=null){
//                                        Text("سعر التوصيل: " + formatPrice(selectedLocation!!.deliveryPrice.deliveryPrice.toString()) + " "+ selectedLocation!!.deliveryPrice.currencyName ,Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
//                                    }
//                                    HorizontalDivider()
//                                    Text(
//                                        "المبلغ المتوجب دفعه: " + SingletonCart.getAllCartProductsSum(
//                                            SingletonStores.selectedStore
//                                            ,if (selectedLocation!= null ) selectedLocation!!.deliveryPrice else null),Modifier.padding(8.dp)
//                                    )
//                                }
//                            }
//
//                        }


//                        Button(
//                            enabled = CustomSingleton.isOpen,
//                            modifier = Modifier.fillMaxWidth().padding(8.dp),
//                            onClick = {
//                                val isHaveLessCartPrice = CustomSingleton.selectedStore!!.storeCurrencies.any { it.lessCartPrice.toDouble() > 0 }
//                                if (isHaveLessCartPrice){
//                                    val lessCartPrices = CustomSingleton.selectedStore!!.storeCurrencies.filter { it.lessCartPrice.toDouble() > 0 }
//                                    val allCartPrices = SingletonCart.getAllCartProductsSumPrices(SingletonStores.selectedStore)
//
//                                    lessCartPrices.forEach { data ->
//                                        allCartPrices.forEach { orderAmount ->
//                                            if (data.lessCartPrice.toDouble() > orderAmount.amount && data.currencyId == orderAmount.currencyId){
//                                                stateController.showMessage(orderAmount.currencyName+" "+data.lessCartPrice+"اقل مبلغ للطلب يجب ان يستوفي")
//                                                return@Button
//                                            }
//                                        }
//                                    }
//                                }
//                                if (selectedOption.id == 1){
//                                    if (selectedLocation != null){
//
//                                        checkPaymentAndConfirm()
//
//                                    }else {
//                                        ShowLocations()
//                                        stateController.showMessage("يجب تحديد موقع للتوصيل")
//                                    }
//                                }else{
//                                    checkPaymentAndConfirm()
//                                }
//
//                            }) {
//                            Text("تأكيد الطلب")
//                        }
            }
            item {
                CustomCard(modifierBox = Modifier) {
                    Column(Modifier.selectableGroup()) {
                        Text("خيار استلام الطلب", modifier = Modifier.padding(14.dp))
                        viewModel.radioOptions.forEach { text ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .selectable(
                                        selected = (text == viewModel.selectedOption),
                                        onClick = {
                                            if (viewModel.selectedOption.id == 2) {
                                                viewModel.selectedLocation = null
                                            }
                                            viewModel.onOptionSelected(text)

                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                RadioButton(selected = (text == viewModel.selectedOption), onClick = null)
                                Text(text = text.name,style = MaterialTheme. typography. bodyLarge,modifier = Modifier. padding(start = 16.dp))
                            }
                        }
                    }
                }
            }
            if (viewModel.selectedOption.id == 1)
                item {
                    CardView({
                        OutLinedButton(
                            text = if (viewModel.selectedLocation != null) "تغيير" else "تحديد"
                        ) {
                           viewModel. ShowLocations()
                        }

                    },"معلومات التوصيل"){
                        if (viewModel.selectedLocation != null){
                            CustomCard(modifierBox = Modifier) {
                                Text("توصيل الى:",Modifier.padding(8.dp))
                                Text(viewModel.selectedLocation!!.street,Modifier.padding(8.dp))
                                Text(" سعر التوصيل:" +viewModel.selectedLocation!!.deliveryPrice.deliveryPrice.toString() + " "+ viewModel.selectedLocation!!.deliveryPrice.currencyName ,Modifier.padding(8.dp))
                                Text("المسافة:" +viewModel.selectedLocation!!.distanse.text ,Modifier.padding(8.dp))
                                Text("الوقت:" +viewModel.selectedLocation!!.duration.text ,Modifier.padding(8.dp))
                            }
                        }
                    }

                }
            item {
                CardView({
                    OutLinedButton(
                        text = if (viewModel.selectedPaymentMethod != null) "تغيير" else "تحديد"
                    ) {
                        viewModel.isShowSelectPaymentMethod = true
                    }

                },"طريقة الدفع")
                {
                    if (viewModel.selectedPaymentMethod != null )
                        Row (
                            Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Icon(
                                tint = MaterialTheme.colorScheme.primary,
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = ""
                            )

                            if (viewModel.selectedPaymentMethod!!.id != 0){
                                Row () {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(10.dp),
                                        model = viewModel.selectedPaymentMethod!!.image,
                                        contentDescription = null
                                    )
                                }
                                MyTextField(
                                    hinty = "ادخل كود الشراء هنا"
                                ) {
                                    viewModel.paidCode = it
                                }

                            }



                            Text(
                                modifier = Modifier.padding(start = 8.dp),
                                text = viewModel.selectedPaymentMethod!!.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }
//                    Row (
//                        Modifier
//                            .fillMaxWidth()
//                            .padding(5.dp),
//                        horizontalArrangement = Arrangement.Start,
//                        verticalAlignment = Alignment.CenterVertically,
//                    ){
//
//                    }

//                    Card (
//                        Modifier
//
//                            .clickable {
//
//                            },
//                    ){
//                        Box (
//                            Modifier
//                                .fillMaxSize()
//                                .background(MaterialTheme.colorScheme.primary)){
//                            Text(
//                                modifier = Modifier.padding(1.dp),
//
//                                text = "الدفع الالكتروني؟ تواصل معنا",
//                                fontSize = 12.sp,
//                                color = Color.White,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//
//                    }

                }
            }
        })
}

@Composable
fun InvoiceRow(
    title: String,
    value: String,
    titleColor: Color = Color.DarkGray,
    valueColor: Color = Color.Black,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = titleColor
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
    }
}







@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun modalShowLocations(launcher: ManagedActivityResultLauncher<Intent, ActivityResult> ,componentActivity: ComponentActivity) {
    val context = LocalContext.current
    val viewModel: CartPreviewViewModel = hiltViewModel()
    ModalBottomSheet(
        onDismissRequest = { viewModel.isShowReadLocations = false }) {
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
                    Button(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        onClick = {
                            val intent = Intent(
                                context,
                                AddLocationActivity::class.java
                            )
                            launcher.launch(intent)

//                            componentActivity.startActivity(intent)
                            viewModel.isShowReadLocations = false

                        }) { Text("اضافة") }
                }

                itemsIndexed(viewModel.locations){index,location->
                    Card(Modifier.padding(8.dp)) {
                        Row (
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ){
                            Text(location.street)
                            Button(
                                onClick = {
                                    viewModel.selectedLocation = location
                                    viewModel.isShowReadLocations = false
                                }) {
                                Text("اختيار") }
                        }
                    }
                }
            }
        }
    }
}

// controller

@Composable
fun OrderInvoiceSection() {
    val viewModel: CartPreviewViewModel = hiltViewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val cartProducts = viewModel.getAllCartProducts()
    val delivery = viewModel.selectedLocation?.deliveryPrice
    val cartTotal = viewModel.getAllCartProductsSum()
    val grandTotal = viewModel.getAllCartProductsSumPrices()
    val coupon = viewModel.coupon

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {

        Text(
            text = "🧾 ملخص الفاتورة",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Color(0xFF1B5E20),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Divider(color = Color(0xFFE0E0E0))

        if (cartProducts.isNotEmpty()) {

            Spacer(modifier = Modifier.height(12.dp))
            InvoiceRow(title = "🛍 إجمالي المنتجات", value = cartTotal)

            if (viewModel.appSession.selectedStore.hasCoupon == 1) {
                Spacer(modifier = Modifier.height(6.dp))

                if (coupon == null) {
                    Text(
                        text = "🎟 هل لديك كوبون خصم؟",
                        fontSize = 13.sp,
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { viewModel.isShowChooseCouponCode = true }
                            .padding(vertical = 4.dp)
                    )
                } else {
                    val discountValueText = if (coupon.type == 1) {
                        "${coupon.amount}%"
                    } else {
                        val currencyName = viewModel.appSession.home.storeCurrencies.find {
                            it.currencyId == coupon.currencyId
                        }?.currencyName ?: ""
                        "${coupon.amount} $currencyName"
                    }

                    InvoiceRow(title = "💸 الخصم", value = "- $discountValueText", valueColor = Color(0xFFD32F2F))
                }
            }

            delivery?.let {
                Spacer(modifier = Modifier.height(6.dp))
                InvoiceRow(
                    title = "🚚 التوصيل",
                    value = "${formatPrice(it.deliveryPrice.toString())} ${it.currencyName}"
                )
            }

            Divider(
                color = Color(0xFFBDBDBD),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            InvoiceRow(
                title = "💰 المبلغ النهائي",
                value = grandTotal,
                titleColor = Color.Black,
                valueColor = Color(0xFF2E7D32),
                isBold = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                        if (!viewModel.getIsOpen()) {
                            viewModel.stateController.showMessage("المتجر مغلق حالياً")
                            return@Button
                        }

//                        // التحقق من الحد الأدنى للسعر
//                        val lessPriceError = viewModel.getLessCartPriceError()
//                        if (lessPriceError != null) {
//                            viewModel.stateController.showMessage(lessPriceError)
//                            return@Button
//                        }

                        // التحقق من خيار التوصيل أو الحضور
                        if (viewModel.selectedOption.id == 1) {
                            // التوصيل
                            if (viewModel.selectedLocation == null) {
                                viewModel.ShowLocations()
                                viewModel.stateController.showMessage("يجب تحديد موقع للتوصيل")
                                return@Button
                            }
                        }

                        // التحقق من الدفع
                        if (viewModel.selectedPaymentMethod == null) {
                            viewModel.isShowSelectPaymentMethod = true
                            viewModel.stateController.showMessage("يجب تحديد طريقة الدفع")
                            return@Button
                        }

//                    (context as Activity).finish()

                        // تأكيد الطلب
                        scope.launch {
                            viewModel.confirmOrder {
                                viewModel.emptyCartProducts(viewModel.appSession.selectedStore.id)

                                (context as Activity).finish()
                            }
                        }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تأكيد الطلب",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "لا توجد منتجات في السلة.",
                color = Color.Gray,
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}



//
//@Composable
//fun OrderInvoiceSection() {
//    val viewModel: CartPreviewViewModel = hiltViewModel()
//    val cartProducts = viewModel.getAllCartProducts()
//    val delivery = viewModel.selectedLocation?.deliveryPrice
//    val cartTotal = viewModel.getAllCartProductsSum()
//    val grandTotal = viewModel.getAllCartProductsSumPrices()
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 12.dp, vertical = 8.dp)
//            .clip(RoundedCornerShape(12.dp))
//            .background(Color(0xFFF9F9F9)) // لون خلفية خفيف وأنيق
//            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
//            .padding(16.dp)
//    ) {
//        Button(onClick = {}) { Text("تأكيد الطلب")}
//        Row (Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween){
//            Text(
//                text = "🧾 فاتورة الطلب",
//                fontWeight = FontWeight.SemiBold,
//                fontSize = 16.sp,
//                color = Color(0xFF333333)
//            )
//        }
//        // عنوان
//
//
//        Spacer(modifier = Modifier.height(8.dp))
//        Divider(color = Color(0xFFE0E0E0))
//
//        if (cartProducts.isNotEmpty()) {
//
//            InvoiceRow(title = "إجمالي الطلبات", value = cartTotal)
//            if (viewModel.appSession.selectedStore.hasCoupon == 1) {
//                val coupon = viewModel.coupon
//                if (coupon == null) {
//                    Text(
//                        text = "هل لديك كوبون خصم؟",
//                        fontSize = 10.sp,
//                        color = Color.Blue,
//                        modifier = Modifier.clickable {
//                            viewModel.isShowChooseCouponCode = true
//                        }
//                    )
//                } else {
//                    val discountValueText = if (coupon.type == 1) {
//                        "${coupon.amount}%"
//                    } else {
//                        val currencyName = viewModel.appSession.home.storeCurrencies.find { it.currencyId == coupon.currencyId }?.currencyName
//                        "${coupon.amount} ${currencyName ?: ""}" // تأكد أن لديك العملة
//                    }
//
//                    InvoiceRow(
//                        title = "الخصم",
//                        value = discountValueText,
//
//                        )
//                }
//            }
//
//            if (delivery != null) {
////                val currencyName = viewModel.appSession.home.storeCurrencies.find { it.isSelected == 1 }
//                InvoiceRow(
//                    title = "سعر التوصيل",
//                    value = "${formatPrice(delivery.deliveryPrice.toString())} ${delivery.currencyName}"
//                )
//            }
//
//
//
//            Divider(
//                color = Color(0xFFE0E0E0),
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//
//            InvoiceRow(
//                title = "المبلغ النهائي",
//                value = grandTotal,
//                titleColor = Color.Black,
//                valueColor = Color(0xFF2E7D32),
//                isBold = true
//            )
//
//        } else {
//            Text(
//                text = "لا توجد منتجات في السلة.",
//                color = Color.Gray,
//                fontSize = 13.sp,
//                fontStyle = FontStyle.Italic,
//                modifier = Modifier.padding(top = 6.dp)
//            )
//        }
//    }
//}
@Composable
fun PulsingBoxConfirm(componentActivity: ComponentActivity) {
    val scope = rememberCoroutineScope()

    val viewModel: CartPreviewViewModel = hiltViewModel()
    // حركة مستمرة
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                var stop = false
                if (!viewModel.getIsOpen()) {
                    viewModel.stateController.showMessage("المتجر مغلق حاليا")
                    return@clickable
                }
//                val isHaveLessCartPrice = viewModel.getSelectedStore().storeCurrencies.any { it.lessCartPrice.toDouble() > 0 }
//                if (isHaveLessCartPrice){
//                    val lessCartPrices = viewModel.getSelectedStore().storeCurrencies.filter { it.lessCartPrice.toDouble() > 0 }
//                    val allCartPrices = viewModel.getAllCartProductsSumPrices()
//
//                    lessCartPrices.forEach { data ->
//                        allCartPrices.forEach { orderAmount:OrderAmount ->
//                            if (data.lessCartPrice.toDouble() > orderAmount.amount && data.currencyId == orderAmount.currencyId){
//                                viewModel.stateController.showMessage("اقل مبلغ للطلب يجب ان يستوفي"+ " "+data.lessCartPrice+" "+ orderAmount.currencyName)
////                               return;
//                                stop = true
//                            }
//                        }
//                    }
//                }
                if (stop) return@clickable
                if (viewModel.selectedOption.id == 1) {
                    if (viewModel.selectedLocation != null) {

                        if (viewModel.selectedPaymentMethod != null) {
                            scope.launch {
                                viewModel.confirmOrder {
                                    componentActivity.finish()
                                }
                            }

                        } else {
                            viewModel.isShowSelectPaymentMethod = true
                            viewModel.stateController.showMessage("يجب تحديد طريقة الدفع")
                        }

                    } else {
                        viewModel.ShowLocations()
                        viewModel.stateController.showMessage("يجب تحديد موقع للتوصيل")
                    }
                } else {
                    Log.e("selectedPaymentMethod", viewModel.selectedPaymentMethod.toString())
//                    viewModel.checkPaymentAndConfirm()
                    if (viewModel.selectedPaymentMethod != null) {
                        scope.launch {
                            viewModel.confirmOrder {
                                viewModel.emptyCartProducts(viewModel.appSession.selectedStore.id)
                                componentActivity.finish()

                            }
                        }

                    } else {
                        viewModel.isShowSelectPaymentMethod = true
                        viewModel.stateController.showMessage("يجب تحديد طريقة الدفع")
                    }
                }

            }
            .background(
                color = Color(0xFF9C27B0), // برتقالي غني
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = "ارسال وتأكيد الطلب",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChoosePaymentMethod() {
    val viewModel: CartPreviewViewModel = hiltViewModel()
    ModalBottomSheet(
        onDismissRequest = { viewModel.isShowSelectPaymentMethod = false }) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
            ) {

                itemsIndexed(viewModel.list) { index, item ->
                    Card(
                        Modifier
                            .padding(8.dp)
                            .clickable {
//                                    selectedPaymentMethod = item
//                                    isShowSelectPaymentMethod = false

                                if (item.id == 1) {
                                    viewModel.isShowShowPaymentTypes = true
//                                        intentFunWhatsapp()
                                } else
                                    viewModel.selectedPaymentMethod = item
                                viewModel.isShowSelectPaymentMethod = false
                            },
//                            colors = CardColors(
//                                containerColor = Color.White,
//                                contentColor = Color.Black,
//                                disabledContainerColor = Color.Blue,
//                                disabledContentColor = Color.Cyan
//                            )
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(10.dp),
                                model = item.image.toInt(),
                                contentDescription = null
                            )
                            HorizontalDivider(Modifier.padding(5.dp))
                            Text(item.name, fontSize = 12.sp)
                        }

                    }
                }
            }
        }
    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChoosePaymentTypes() {
    val scope = rememberCoroutineScope()
    val viewModel: CartPreviewViewModel = hiltViewModel()
    LaunchedEffect(6) {
        if (viewModel.paymentsTypes.isEmpty())
        viewModel.readPaymentTypes()
    }

    ModalBottomSheet(
        onDismissRequest = { viewModel.isShowShowPaymentTypes = false }) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
            ) {

                itemsIndexed(viewModel.paymentsTypes) { index, item ->
                    Card(
                        Modifier
                            .padding(8.dp)
                            .clickable {
                                viewModel.selectedPaymentMethod =
                                    PaymentModel(item.name, item.image, item.id)
//                                    isShowSelectPaymentMethod = false

//                                    if (item.id == 3) {
////                                        intentFunWhatsapp()
//                                    } else
//                                        selectedPaymentMethod = item
                                viewModel.isShowShowPaymentTypes = false
                            },
//                            colors = CardColors(
//                                containerColor = Color.White,
//                                contentColor = Color.Black,
//                                disabledContainerColor = Color.Blue,
//                                disabledContentColor = Color.Cyan
//                            )
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(10.dp),
                                model = item.image,
                                contentDescription = null
                            )
                            HorizontalDivider(Modifier.padding(5.dp))
                            Text(item.name, fontSize = 12.sp)
                        }

                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ChooseCouponCode() {
    val viewModel: CartPreviewViewModel = hiltViewModel()
    var code by remember { mutableStateOf("") }
    ModalBottomSheet(
        onDismissRequest = { viewModel.isShowChooseCouponCode = false }) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp)
        ) {
            LazyColumn {
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = code,
                        label = {
                            Text("كود الخصم")
                        },
                        onValueChange = {
                            code = it
                        }
                    )
                }
                item {
                    Button(
                        enabled =code.isNotEmpty(),
                        onClick = {

                            viewModel.readCoupon(code)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text( "تأكيد كود الخصم")
                    }
                }
            }
        }
    }
}



