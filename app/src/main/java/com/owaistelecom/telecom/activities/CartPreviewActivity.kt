package com.owaistelecom.telecom.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.shared.ADControll
import com.owaistelecom.telecom.shared.CustomCard
import com.owaistelecom.telecom.shared.CustomIcon
import com.owaistelecom.telecom.shared.CustomImageView
import com.owaistelecom.telecom.shared.CustomRow
import com.owaistelecom.telecom.shared.CustomRow2
import com.owaistelecom.telecom.shared.IconDelete
import com.owaistelecom.telecom.shared.MainCompose1
import com.owaistelecom.telecom.shared.MyTextField
import com.owaistelecom.telecom.shared.RequestServer
import com.owaistelecom.telecom.shared.SingletonRemoteConfig
import com.owaistelecom.telecom.shared.SingletonStores
import com.owaistelecom.telecom.shared.builderForm3
import com.owaistelecom.telecom.shared.formatPrice
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.encodeToJsonElement

//object SingletonP
class CartPreviewActivity : ComponentActivity() {
    private var locations by mutableStateOf<List<Location>>(emptyList())
    private var paymentsTypes by mutableStateOf<List<PaymentType>>(emptyList())
    private val stateController = StateController()
    var selectedLocation by mutableStateOf<Location?>(null)

    var isShowReadLocations by mutableStateOf(false)
    val requestServer = RequestServer(this)
    var cartView by mutableStateOf(true)
    var isShowSelectPaymentMethod by mutableStateOf(false)
    var isShowShowPaymentTypes by mutableStateOf(false)
    var selectedPaymentMethod by mutableStateOf<PaymentModel?>(null)

    var paidCode by mutableStateOf<String>("")


    val list = listOf<PaymentModel>(
        PaymentModel("عند التوصيل", R.drawable.ondelivery.toString(), 0),
//        PaymentModel("من المحفظة", R.drawable.wallet, 2),
        PaymentModel("دفع الكتروني", R.drawable.epay.toString(), 1)
    )

    val radioOptions = listOf(
        DeliveryOption(1,"التوصيل للموقع"),
        DeliveryOption(2,"الاستلام من المتجر")
    )


    var selectedOption by mutableStateOf(radioOptions[0])
    var title by mutableStateOf("")

    fun onOptionSelected(newOption: DeliveryOption) {
        selectedOption = newOption
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stateController.successState()

        enableEdgeToEdge()
        setContent {
            BackHand()
            //

            OwaisTelecomTheme {
                    Column(
                        Modifier.fillMaxSize().safeDrawingPadding(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomCard(modifierBox = Modifier) {
                            CustomRow2{
                                CustomIcon(Icons.AutoMirrored.Default.ArrowBack, border = true) {
                                    if (cartView) {
                                        finish()
                                    } else
                                        cartView = true
                                }
                                Text(title)
                            }
                        }
                        if (cartView) {
                            title = "عرض السلة"
//                            Text("السلة")
//                            HorizontalDivider()
                            MainContentCartPreview()
                        } else {
                            MainCompose1(0.dp, stateController, this@CartPreviewActivity, {

                            }) {
                                title = "تأكيد الطلب"
//                                Text("الطلب",)
                                MainContentOrderPreview()
                            }
                            if (isShowReadLocations) modalShowLocations()
                            if (isShowSelectPaymentMethod) ChoosePaymentMethod()
                            if (isShowShowPaymentTypes) ChoosePaymentTypes()
                        }
                    }
            }
        }
    }

    @Composable
    private fun BackHand() {
        BackHandler {
            if (cartView) {
                finish()
            } else
                cartView = true
        }
    }

    @Composable
    @OptIn(ExperimentalFoundationApi::class)
    private fun MainContentCartPreview() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            LazyColumn(
                Modifier.padding(bottom = 50.dp),
                content = {

                    stickyHeader {
                        CustomCard(modifierBox = Modifier) {
                            Column {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(SingletonStores.selectedStore.name,Modifier.padding(8.dp))
                                    CustomImageView(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(8.dp)
                                            .clickable {

                                            },
                                        context = this@CartPreviewActivity,
                                        imageUrl = SingletonRemoteConfig.remoteConfig.BASE_IMAGE_URL +
                                                SingletonRemoteConfig.remoteConfig.SUB_FOLDER_STORE_LOGOS +
                                                SingletonStores.selectedStore.logo,
                                        okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                                    )
                                }
                                HorizontalDivider()
                                if (SingletonCart.getAllCartProducts(SingletonStores.selectedStore)
                                        .isNotEmpty()
                                ) {

                                    Text(
                                        "الاجمالي : " + SingletonCart.getAllCartProductsSum(
                                            SingletonStores.selectedStore
                                        ),Modifier.padding(8.dp)
                                    )
                                }
                                HorizontalDivider()
                                Button(
                                    onClick = {
                                        cartView = false
                                    },
                                    modifier = Modifier.padding( 8.dp).fillMaxWidth()
                                ) {
                                    Text(text = "متابعة")
                                }
                            }

                        }
                    }

                    itemsIndexed(SingletonCart.getAllCartProducts(SingletonStores.selectedStore)) { index: Int, cartProduct: CartProduct ->

                        CustomCard(
                            modifierBox = Modifier.fillMaxSize().clickable {

                            }
                        ) {

                            Column {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
//                                    Log.e(
//                                        "image", SingletonRemoteConfig.remoteConfig.BASE_IMAGE_URL +
//                                                SingletonRemoteConfig.remoteConfig.SUB_FOLDER_PRODUCT +
//                                                cartProduct.product.images.first()
//                                    )
                                    Text(cartProduct.product.productName)
                                    if (cartProduct.product.images.isNotEmpty())
                                    CustomImageView(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .padding(8.dp)
                                            .clickable {

                                            },
                                        context = this@CartPreviewActivity,
                                        imageUrl = SingletonRemoteConfig.remoteConfig.BASE_IMAGE_URL +
                                                SingletonRemoteConfig.remoteConfig.SUB_FOLDER_PRODUCT +
                                                cartProduct.product.images.first().image,
                                        okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                                    )

                                }
                                HorizontalDivider()
                                cartProduct.cartProductOption.forEach { cartProductOption ->
                                    Row(
                                        Modifier.fillMaxWidth().padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(cartProductOption.productOption.name)
                                        Text(
                                            modifier = Modifier.padding(8.dp),
                                            text = formatPrice(cartProductOption.productOption.price) +" "+ cartProductOption.productOption.currency.name ,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        ADControll(
                                            cartProduct.product,
                                            cartProductOption.productOption
                                        )
                                    }
                                }
                            }

                        }
                    }
                })
        }
    }
    @Composable
    private fun MainContentOrderPreview() {


        Button(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            onClick = {
                if (selectedOption.id == 1){
                    if (selectedLocation != null){

                        checkPaymentAndConfirm()

                    }else {
                        ShowLocations()
                        stateController.showMessage("يجب تحديد موقع للتوصيل")
                    }
                }else{
                    checkPaymentAndConfirm()
                }

            }) {
            Text("تأكيد الطلب")
        }


            LazyColumn(
                Modifier.padding(bottom = 50.dp),
                content = {
                    item {
                        CustomCard(modifierBox = Modifier) {
                            Column(Modifier.selectableGroup()) {
                                Text("خيار استلام الطلب", modifier = Modifier.padding(14.dp))
                                radioOptions.forEach { text ->
                                    Row(
                                        Modifier.fillMaxWidth().height(56.dp)
                                            .selectable(
                                                selected = (text == selectedOption),
                                                onClick = {
                                                    if (selectedOption.id == 2 ){
                                                        selectedLocation = null
                                                    }
                                                    onOptionSelected(text)

                                                          },
                                                role = Role.RadioButton
                                            ).padding(horizontal = 16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    )
                                    {
                                        RadioButton(selected = (text == selectedOption), onClick = null)
                                        Text(text = text.name,style = MaterialTheme. typography. bodyLarge,modifier = Modifier. padding(start = 16.dp))
                                    }
                                }
                            }
                        }
                    }
                    if (selectedOption.id == 1)
                        item {
                            HorizontalDivider()
                            if (selectedLocation != null){
                                CustomCard(modifierBox = Modifier) {
                                    Text("توصيل الى:")
                                    Text(selectedLocation!!.street)
                                }
                            }
                            Button(
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                onClick = {
                                    ShowLocations()
                                }) {
                                Text(if (selectedLocation == null)"اختيار موقع" else "تغيير الموقع")
                            }
                        }
                    item {
                        CardView({
                            OutLinedButton(
                                text = if (selectedPaymentMethod != null) "تغيير" else "تحديد"
                            ) {
                                isShowSelectPaymentMethod = true
                            }

                        },"طريقة الدفع")
                        {
                            if (selectedPaymentMethod != null )
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

                                    if (selectedPaymentMethod!!.id != 0){
                                        Row () {
                                            AsyncImage(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .padding(10.dp),
                                                model = selectedPaymentMethod!!.image,
                                                contentDescription = null
                                            )
                                        }
                                        MyTextField(
                                            hinty = "ادخل كود الشراء هنا"
                                        ) {
                                            paidCode = it
                                        }

                                    }



                                    Text(
                                        modifier = Modifier.padding(start = 8.dp),
                                        text = selectedPaymentMethod!!.name,
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

    private fun checkPaymentAndConfirm() {
        if (selectedPaymentMethod != null) {
            confirmOrder()
        } else {
            isShowSelectPaymentMethod = true
            stateController.showMessage("يجب تحديد طريقة الدفع")
        }
    }


    private fun ShowLocations() {
        if (locations.isEmpty()) {
            readLocation()
        } else {
            isShowReadLocations = true
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun modalShowLocations() {
        ModalBottomSheet(
            onDismissRequest = { isShowReadLocations = false }) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp)
            ){
                var ids by remember { mutableStateOf<List<Int>>(emptyList()) }
                LazyColumn(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Button(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            onClick = {
                            val intent = Intent(
                                this@CartPreviewActivity,
                                AddLocationActivity::class.java
                            )

                            addLocationLauncher.launch(intent)
                            isShowReadLocations = false
//                            startActivity(intent)
                        }) { Text("اضافة") }
                    }
                    item {
                        var sectionName by remember { mutableStateOf("") }
                        Card(Modifier.padding(8.dp)){
                            IconDelete(ids) {

                            }
                        }
                    }

                    itemsIndexed(locations){index,location->
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
                                        selectedLocation = location
                                        isShowReadLocations = false
                                    }) {
                                    Text("اختيار") }

                                Checkbox(checked = ids.find { it == location.id } != null, onCheckedChange = {
                                    val itemC = ids.find { it == location.id}
                                    if (itemC == null) {
                                        ids = ids + location.id
                                    }else{
                                        ids = ids - location.id
                                    }
                                })
                            }
                        }
                    }
                }
            }
        }
    }

    // controller

    fun readLocation() {
        stateController.startAud()
        val body = builderForm3().build()

        requestServer.request2(body, "getLocations", { code, fail ->
            stateController.errorStateAUD(fail)
        }
        ) { data ->
            val result: List<Location> =
                MyJson.IgnoreUnknownKeys.decodeFromString(
                    data
                )

            locations= result
//            SelectedStore.store.value!! .latLng = latiLng
//            MyToast(this,"تم بنجاح")
            stateController.successStateAUD()

            isShowReadLocations = true
        }
    }
    fun readPaymentTypes() {
        stateController.startAud()
        val body = builderForm3()
            .addFormDataPart("storeId",SingletonStores.selectedStore.id.toString())
            .build()

        requestServer.request2(body, "getPaymentTypes", { code, fail ->
            stateController.errorStateAUD(fail)
        }
        ) { data ->
            val result: List<PaymentType> =
                MyJson.IgnoreUnknownKeys.decodeFromString(
                    data
                )

            paymentsTypes = result
//            SelectedStore.store.value!! .latLng = latiLng
//            MyToast(this,"تم بنجاح")
            stateController.successStateAUD()
        }
    }


    fun confirmOrder() {
        stateController.startAud()

        val bodyBuilder = builderForm3()
            .addFormDataPart("orderProducts", MyJson.MyJson.encodeToJsonElement(SingletonCart.getProductsIdsWithQnt()).toString())
            .addFormDataPart("storeId", SingletonStores.selectedStore.id.toString())


        if (selectedLocation != null) {
            bodyBuilder.addFormDataPart("locationId", selectedLocation!!.id.toString())
        }
        if (selectedPaymentMethod != null){
            bodyBuilder.addFormDataPart("paid", selectedPaymentMethod!!.id.toString())
            bodyBuilder.addFormDataPart("paidCode", paidCode)
        }

        val body = bodyBuilder.build()


        requestServer.request2(body, "confirmOrder", { code, fail ->
            stateController.errorStateAUD(fail)
        }
        ) { data ->
//            val result: List<Location> =
//                MyJson.IgnoreUnknownKeys.decodeFromString(
//                    data
//                )

//            locations= result
////            SelectedStore.store.value!! .latLng = latiLng
//            MyToast(this,"تم بنجاح")
            stateController.showMessage("تم ارسال الطلب بنجاح")
            finish()

//            stateController.successStateAUD()
//
//            isShowReadLocations = true
        }
    }


    private val addLocationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null && result.data!!.getStringExtra("location") != null) {
         val location = MyJson.IgnoreUnknownKeys.decodeFromString<Location>(result.data!!.getStringExtra("location")!!)
            locations += location
            isShowReadLocations = true
        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    private fun ChoosePaymentMethod() {
        ModalBottomSheet(
            onDismissRequest = { isShowSelectPaymentMethod = false }) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                ) {

                    itemsIndexed(list) { index, item ->
                        Card(
                            Modifier
                                .padding(8.dp)
                                .clickable {
//                                    selectedPaymentMethod = item
//                                    isShowSelectPaymentMethod = false

                                    if (item.id == 1) {
                                        isShowShowPaymentTypes = true
//                                        intentFunWhatsapp()
                                    } else
                                        selectedPaymentMethod = item
                                    isShowSelectPaymentMethod = false
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
        if (paymentsTypes.isEmpty())readPaymentTypes()
        ModalBottomSheet(
            onDismissRequest = { isShowShowPaymentTypes = false }) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = 10.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                ) {

                    itemsIndexed(paymentsTypes) { index, item ->
                        Card(
                            Modifier
                                .padding(8.dp)
                                .clickable {
                                    selectedPaymentMethod = PaymentModel(item.name,item.image,item.id)
//                                    isShowSelectPaymentMethod = false

//                                    if (item.id == 3) {
////                                        intentFunWhatsapp()
//                                    } else
//                                        selectedPaymentMethod = item
                                    isShowShowPaymentTypes = false
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
}

data class DeliveryOption (val id:Int, val name:String)

@Serializable
data class Location (val id:Int, val street:String)

@Serializable
data class PaymentType (val id:Int, val name:String, val image:String)

data class PaymentModel(val name:String, val image: String, val id:Int)

@Composable
private fun CardView(button : @Composable()()->Unit = {},title:String, content: @Composable() (ColumnScope.() -> Unit)) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                button()

            }

            HorizontalDivider(Modifier.padding(10.dp))
            content()
        }
    }
}

@Composable
fun OutLinedButton( modifier :Modifier = Modifier
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
    ),text:String,onClick: () -> Unit) {
    Button(
        onClick = onClick
        ,
//        colors = ButtonDefaults.buttonColors(
//            containerColor = Color.White, // Background color
//        ),
        modifier = modifier ,
    ) {

        Text(
            text = text,
            fontSize = 14.sp,
        )
    }
}

