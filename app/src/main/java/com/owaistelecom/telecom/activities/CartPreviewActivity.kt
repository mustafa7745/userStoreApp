package com.owaistelecom.telecom.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.owaistelecom.telecom.shared.CustomImageView
import com.owaistelecom.telecom.shared.IconDelete
import com.owaistelecom.telecom.shared.MainCompose1
import com.owaistelecom.telecom.shared.RequestServer
import com.owaistelecom.telecom.shared.SingletonRemoteConfig
import com.owaistelecom.telecom.shared.SingletonStores
import com.owaistelecom.telecom.shared.builderForm3
import com.owaistelecom.telecom.shared.formatPrice
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.encodeToJsonElement

class CartPreviewActivity : ComponentActivity() {
    private var locations by mutableStateOf<List<Location>>(emptyList())
    private val stateController = StateController()
    var selectedLocation by mutableStateOf<Location?>(null)

    var isShowReadLocations by mutableStateOf(false)
    val requestServer = RequestServer(this)
    var cartView by mutableStateOf(true)
    var isShowSelectPaymentMethod by mutableStateOf(false)
    var selectedPaymentMethod by mutableStateOf<PaymentModel?>(null)


    val list = listOf<PaymentModel>(
        PaymentModel("عند التوصيل", R.drawable.ondelivery, 1),
//        PaymentModel("من المحفظة", R.drawable.wallet, 2),
        PaymentModel("دفع الكتروني", R.drawable.epay, 3)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stateController.successState()
        setContent {
            BackHand()
            //

            OwaisTelecomTheme {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (cartView) {
                        Text("السلة")
                        HorizontalDivider()
                        MainContentCartPreview()
                    } else {
                        MainCompose1(0.dp,stateController,this@CartPreviewActivity,{

                        }) {
                            Text("الطلب",)
                            MainContentOrderPreview()
                        }
                        if (isShowReadLocations)modalShowLocations()
                        if (isShowSelectPaymentMethod) ChoosePaymentMethod()
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
                                    Text(SingletonStores.selectedStore.name)
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
                                        )
                                    )
                                }
                                HorizontalDivider()
                                Button(
                                    onClick = {
                                        cartView = false
                                    },
                                    modifier = Modifier.padding(top = 4.dp).fillMaxWidth()
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
                                            text = formatPrice(cartProductOption.productOption.price) + " ريال ",
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

        val radioOptions = listOf(
            DeliveryOption(1,"التوصيل للموقع"),
            DeliveryOption(2,"الاستلام من المتجر")
        )
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
        Button(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            onClick = {
confirmOrder()
            }) {
            Text("تأكيد الطلب")
        }


            LazyColumn(
                Modifier.padding(bottom = 50.dp),
                content = {
                    item {
                        CustomCard(modifierBox = Modifier) {
                            Text("خيار استلام الطلب")
                            Column(Modifier.selectableGroup()) {
                                radioOptions.forEach { text ->
                                    Row(
                                        Modifier.fillMaxWidth().height(56.dp)
                                            .selectable(
                                                selected = (text == selectedOption),
                                                onClick = { onOptionSelected(text) },
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

                                    if (locations.isEmpty()) {
                                        readLocation()
                                    }else{
                                        isShowReadLocations = true
                                    }
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
                            if (selectedPaymentMethod != null)
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


    fun confirmOrder() {
        stateController.startAud()

        val body = builderForm3()
            .addFormDataPart("orderProducts", MyJson.MyJson.encodeToJsonElement(SingletonCart.getProductsIdsWithQnt()).toString())
            .addFormDataPart("storeId",SingletonStores.selectedStore.id.toString())
            .build()

        requestServer.request2(body, "confirmOrder", { code, fail ->
            stateController.errorStateAUD(fail)
        }
        ) { data ->
            val result: List<Location> =
                MyJson.IgnoreUnknownKeys.decodeFromString(
                    data
                )

//            locations= result
////            SelectedStore.store.value!! .latLng = latiLng
//            MyToast(this,"تم بنجاح")
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
                                    if (item.id == 3) {
//                                        intentFunWhatsapp()
                                    } else
                                        selectedPaymentMethod = item
                                    isShowSelectPaymentMethod = false
                                },
                            colors = CardColors(
                                containerColor = Color.White,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.Blue,
                                disabledContentColor = Color.Cyan
                            )
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

data class PaymentModel(val name:String, val image:Int, val id:Int)

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
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White, // Background color
        ),
        modifier = modifier ,
    ) {

        Text(
            text = text,
            color = MaterialTheme.colorScheme.primary, fontSize = 14.sp,
        )
    }
}

