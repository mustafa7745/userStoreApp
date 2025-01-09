package com.owaistelecom.telecom.activities

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.storage.HomeStorage
import com.owaistelecom.telecom.models.StoreCategory
import com.owaistelecom.telecom.models.StoreProduct
import com.owaistelecom.telecom.shared.CustomIcon
import com.owaistelecom.telecom.shared.CustomImageView
import com.owaistelecom.telecom.shared.MainCompose1
import com.owaistelecom.telecom.shared.RequestServer
import com.owaistelecom.telecom.shared.SingletonRemoteConfig
import com.owaistelecom.telecom.shared.formatNumber
import com.owaistelecom.telecom.shared.getCurrentDate
import com.owaistelecom.telecom.models.Home
import com.owaistelecom.telecom.models.Store
import com.owaistelecom.telecom.models.StoreNestedSection
import com.owaistelecom.telecom.models.StoreSection
import com.owaistelecom.telecom.shared.AToken
import com.owaistelecom.telecom.shared.CustomImageView1
import com.owaistelecom.telecom.shared.SingletonStores
import com.owaistelecom.telecom.shared.builderForm2
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import okhttp3.MultipartBody
import java.time.Duration

class InsideStoreActivity : ComponentActivity() {
    val homeStorage = HomeStorage();
    private val products = mutableStateOf<List<StoreProduct>>(listOf())
    private val stores = mutableStateOf<List<Store>>(listOf())
    var home by mutableStateOf<Home?>(null)
    val stateController = StateController()
    val stateControllerProducts = StateController()
    val requestServer = RequestServer(this)

    var currentPage by mutableStateOf(0)
   private var store by mutableStateOf<Store?>(null)


    var selectedCategory = mutableStateOf<StoreCategory?>(null)
    var selectedSection = mutableStateOf<StoreSection?>(null)
    var selectedStoreNestedSection = mutableStateOf<StoreNestedSection?>(null)
    var isEmptyComponent by mutableStateOf(true)


    val aToken = AToken()
    val pageList = listOf(
       PageModel("متجر اويس تيليكوم", 0),
       PageModel("اخر العروض", 1),
    )



    var isLoadingLinear by mutableStateOf(false)


    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//
        if (!requestServer.serverConfig.isSetSubscribeApp()){
            subscribeToAppTopic()
        }

        if (!requestServer.serverConfig.isSetRemoteConfig()){

            requestServer.initVarConfig({
                stateController.errorStateRead("enable get remote config")
            }){
//                stateController.successState()
//                Log.e("serverConfig",SingletonRemoteConfig.remoteConfig.toString())
                SingletonRemoteConfig.remoteConfig = requestServer.serverConfig.getRemoteConfig()
                checkTokenToRead()
            }
        }else{
            checkTokenToRead()
        }

//        stateController




        enableEdgeToEdge()
        setContent {
            OwaisTelecomTheme {
//                Button(onClick = {
//                    requestEnableGPS()
//                }) {
//                    Text("Enable gps")
//                }
//                LazyColumn (Modifier.fillMaxSize()) {
//                    item {
//                        Spacer(Modifier.height(100.dp))
//                        OfferView()
//                    }
//
//
//                }
                MyStore()
            }
        }
    }



    @Composable
    private fun MyStore() {
//        // Get current window insets (for the system navigation bar)
//        val insets = LocalView.current.rootView.bottom
//
//        // Get the bottom inset (for the navigation bar space)
//        val bottomInset = insets.getBottom(density =LocalDensity.current.)


        Box (Modifier.fillMaxSize().safeDrawingPadding()){
            MainCompose1(
                10.dp, stateController, this@InsideStoreActivity,
                { readStores() },
            ) {
//                HorizontalPager(
//                    pagerState,
//                    modifier = Modifier.fillMaxSize()
//                ) { item ->
                    if (pageList.find { 0 == currentPage } != null) {
                        LazyColumn(Modifier.background(Color.White).safeDrawingPadding()) {
                            HeaderComponent(this)
                            MyProducts(this)
                        }
                    }
                    else {
//                        Text("Moosmdkd")
                        OfferView()
                    }

//                }
            }
            TabRow(currentPage,Modifier.align(Alignment.BottomCenter)) {
                val coroutineScope = rememberCoroutineScope()
                pageList.map { item ->
                    Tab(
                        modifier = Modifier.padding(3.dp),
                        selected = item.pageId == currentPage,
                        onClick = {
                            currentPage = item.pageId
//                            coroutineScope.launch {
//                                pagerState.animateScrollToPage(item.pageId)
//                            }
                        },
                        text = {
                            Text(
                                modifier = Modifier.padding(3.dp),
                                textAlign = TextAlign.Start,
                                text = item.pageName,
                                fontSize = 14.sp,
                                color = if (item.pageId == currentPage) MaterialTheme.colorScheme.primary else Color.Black
                            )
                        }
                    )
                }
            }
        }


    }

    @Composable
    private fun OfferView() {
//        Spacer()
       LazyRow(Modifier.height(220.dp).fillMaxWidth()) {

           repeat(5){
               item {
                   Card(Modifier.width(160.dp).padding(8.dp).fillParentMaxHeight()) {

                   }
               }
           }
       }
        Text("اخر المنشورات")
        //

        repeat(15){
            Card(Modifier.height(130.dp).padding(8.dp).fillMaxWidth()) {

            }
        }

    }


    @OptIn(ExperimentalFoundationApi::class)
    private fun HeaderComponent(
        lazyListScope: LazyListScope
    ) {
        lazyListScope.item {
            Box {

                CustomImageView1(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .height(200.dp)
                        .clickable {

                        },
                    imageUrl = SingletonRemoteConfig.remoteConfig.BASE_IMAGE_URL + SingletonRemoteConfig.remoteConfig.SUB_FOLDER_STORE_COVERS + store!!.cover,
                )
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 150.dp, start = 8.dp, end = 8.dp)
                        .align(Alignment.TopCenter)
                        .border(1.dp, Color.Gray, RoundedCornerShape(12.dp)),
                ) {
                    Column {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            CustomImageView1(
                                modifier = Modifier
                                    .size(80.dp)
                                    .padding(10.dp)
                                    .clickable {

                                    },
                                imageUrl = SingletonRemoteConfig.remoteConfig.BASE_IMAGE_URL + SingletonRemoteConfig.remoteConfig.SUB_FOLDER_STORE_LOGOS + store!!.logo,
                            )
                            Column {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = store!!.name, fontSize = 16

                                            .sp, fontWeight = FontWeight.Bold
                                    )
                                    CustomIcon(Icons.Outlined.Info) { }
                                }

                                Text(
                                    text = home!!.storeCategories.joinToString(
                                        separator = ", "
                                    ) { it.categoryName },
                                    fontSize = 14.sp,
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = formatNumber(store!!.subscriptions) + " مشترك ",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp,
                                    )

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .border(
                                                1.dp,
                                                 MaterialTheme.colorScheme.primary,
                                                RoundedCornerShape(12.dp)
                                            ),
                                    ) {
                                        Box(
                                            Modifier
                                                .clickable {

                                                }
                                        ) {
                                            Row(
                                                Modifier.fillMaxSize(),
                                                verticalAlignment = Alignment.CenterVertically,
                                            ) {
                                                Text(
                                                    store!!.stars.toString(),
                                                    fontSize = 14.sp,
                                                    modifier = Modifier.padding(2.dp)
                                                )
//                                                CustomIcon(Icons.Outlined.Star, modifierIcon = Modifier.size(14.dp)) {
//
//                                                }
                                            }
                                        }
                                    }

//                                    StarRating(store!!.stars)
                                }
                            }
                        }
                        HorizontalDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

    //                                                Column(
    //                                                    verticalArrangement = Arrangement.Center,
    //                                                    horizontalAlignment = Alignment.CenterHorizontally
    //                                                ) {
    //                                                    Row(
    //                                                        verticalAlignment = Alignment.CenterVertically
    //                                                    ){
    //                                                        CustomIcon(Icons.Outlined.Star) { }
    //                                                        Text(text = "${formatNumber(store!!.likes)}", modifier = Modifier.clickable { /* handle click */ })
    //                                                    }
    //
    //                                                    Text(text = " اعجاب", modifier = Modifier.clickable { /* handle click */ })
    //                                                }


                            // Divider between the first and second section
    //                                            VerticalDivider(modifier = Modifier.height(30.dp).padding(horizontal = 5.dp))

                            // Second Row: Comments
                            Row(
                                modifier = Modifier.clickable { /* handle click */ },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CustomIcon(Icons.Outlined.FavoriteBorder) { }
                                Text(
                                    text = "${formatNumber(store!!.likes)} اعجاب",
                                    modifier = Modifier.clickable { /* handle click */ })
                            }

                            // Divider between the second and third section
                            VerticalDivider(
                                modifier = Modifier
                                    .height(30.dp)
                                    .padding(horizontal = 5.dp)
                            )

                            // Third Row: Stars
                            Row(
                                modifier = Modifier.clickable { /* handle click */ },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CustomIcon(Icons.Outlined.Star) { }
                                Text(text = "${formatNumber(store!!.stars)} تعليق")
                            }
                        }


    //                                        Row (Modifier.fillMaxWidth(),
    //                                            verticalAlignment = Alignment.CenterVertically,
    //                                            horizontalArrangement = Arrangement.SpaceEvenly
    //                                            ){
    //                                            Row (
    //                                                    verticalAlignment = Alignment.CenterVertically,
    ////                                                horizontalArrangement = Arrangement.SpaceBetween
    //                                            ){
    ////                                                VerticalDivider(modifier = Modifier.height(30.dp).padding(5.dp))
    //                                                CustomIcon(Icons.Outlined.Star) { }
    //                                                Text(text = "${formatNumber(store!!.likes) } اعجاب" , modifier = Modifier.clickable {  })
    //
    //                                            }
    //                                            // Text with like count
    //
    //
    //                                            // Vertical divider with default thickness
    ////                                            VerticalDivider(modifier = Modifier.height(30.dp))
    //
    //                                            Row (
    //                                                verticalAlignment = Alignment.CenterVertically,
    ////                                                horizontalArrangement = Arrangement.SpaceBetween
    //                                            ){
    //                                                VerticalDivider(modifier = Modifier.height(30.dp).padding(5.dp))
    //                                                CustomIcon(Icons.Outlined.Star) { }
    //                                                Text(text = "${ formatNumber(store!!.likes) } تعليق", modifier = Modifier.clickable {  })
    //
    //                                            }
    //
    //                                            // Text with like count
    //
    //
    //                                            // Vertical divider with custom thickness
    //
    //
    //
    //                                            Row (
    //                                                verticalAlignment = Alignment.CenterVertically,
    ////                                                horizontalArrangement = Arrangement.SpaceBetween
    //                                            ){
    //                                                VerticalDivider(modifier = Modifier.height(30.dp).padding(5.dp))
    //                                                CustomIcon(Icons.Outlined.Star) { }
    //                                                Text(text = "${formatNumber(store!!.stars)} تقييم", modifier = Modifier.clickable {  })
    //
    //                                            }
    //                                            // Text with like count
    //
    //
    //
    //                                        }
                    }


                }
    //                                Column(Modifier.align(Alignment.TopCenter)) {
    //
    //                                    Text("mustafa")
    //                                    Text("mustafa")
    //                                    Text("mustafa")
    //                                }


            }
        }


    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun MyProducts(
        lazyListScope: LazyListScope
    ) {
        if (isEmptyComponent) {
            Log.e("ffdd355", isEmptyComponent.toString())
            lazyListScope.item {
                Text("No component")
            }
        } else {
            Log.e("ffdd", isEmptyComponent.toString())
            lazyListScope.stickyHeader {
                DropDownDemo()
                if (isLoadingLinear)
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                HorizontalDivider()
                LazyRow(
                    Modifier
                        .height(55.dp)
                        .background(Color.White)
                        .fillMaxWidth()) {
                    //                                    stickyHeader {
                    //                                        Card {
                    //                                            Text(selectedSection.sectionName)
                    //                                        }
                    //                                    }
                    itemsIndexed(home!!.storeSections.filter { it.storeCategoryId == selectedCategory.value!!.id }) { index, item ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .padding(8.dp)
                                .border(
                                    1.dp,
                                    if (item.sectionId == selectedSection.value!!.sectionId) MaterialTheme.colorScheme.primary else Color.Gray,
                                    RoundedCornerShape(12.dp)
                                ),
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        if (!isLoadingLinear){
                                            isLoadingLinear = true
                                            selectedSection.value = item
                                            selectedStoreNestedSection.value =
                                                home!!.storeNestedSections.first { it.storeSectionId == selectedSection.value!!.id }
//                                            products.value = emptyList()
                                            readProducts()
                                        }

                                    }
                            ) {
                                Row(
                                    Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        item.sectionName,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                LazyRow(
                    Modifier
                        .height(40.dp)
                        .background(Color.White)
                        .fillMaxWidth()) {
                    //                                    stickyHeader {
                    //                                        Card {
                    //                                            Text(selectedSection.sectionName)
                    //                                        }
                    //                                    }
                    itemsIndexed(home!!.storeNestedSections.filter { it.storeSectionId == selectedSection.value!!.id }) { index, item ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier
                                .padding(6.dp)
                                .border(
                                    1.dp,
                                    if (item.nestedSectionId == selectedStoreNestedSection.value!!.nestedSectionId) MaterialTheme.colorScheme.primary else Color.Gray,
                                    RoundedCornerShape(12.dp)
                                ),
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        if (!isLoadingLinear){
                                            isLoadingLinear = true
                                            selectedStoreNestedSection.value = item
                                            readProducts()
                                        }
                                        //                                            goToStores(item)
                                    }
                            ) {
                                Row(
                                    Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        item.nestedSectionName,
                                        modifier = Modifier.padding(2.dp),
                                        fontSize = 12.sp
                                    )
                                }

                            }
                        }
                    }
                }
            }
            lazyListScope.item {

                MainCompose1(
                    0.dp, stateControllerProducts, this@InsideStoreActivity,
                    {
                        readProducts()
                    },
                ) {
                    products.value.forEach { product ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .height(100.dp)
                                .border(
                                    1.dp, Color.Gray,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {

                                    goToAddToCart(product)
                                }) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        product.product.productName,
                                        Modifier.padding(8.dp),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }

                                if (product.product.images.firstOrNull() != null)
                                    CustomImageView(
                                        modifier = Modifier
                                            .size(100.dp)
                                            .padding(5.dp),
                                        context = this@InsideStoreActivity,
                                        imageUrl = SingletonRemoteConfig.remoteConfig.BASE_IMAGE_URL +
                                                SingletonRemoteConfig.remoteConfig.SUB_FOLDER_PRODUCT +
                                                product.product.images.first().image,
                                        okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                                    )
                                //                                        if (product.options.firstOrNull() != null){
                                //                                            Text(product.options.first().name,Modifier.padding(8.dp))
                                //                                            Text(
                                //                                                modifier = Modifier.padding(8.dp),
                                //                                                text = formatPrice(product.options.first().price) + " ريال ",
                                //                                                fontWeight = FontWeight.Bold,
                                //                                                fontSize = 16.sp,
                                //                                                color = Color.Black
                                //                                            )
                                //                                        }

                            }
                        }

                    }
                }


            }
        }
    }

    private fun checkTokenToRead() {
        if (!aToken.isSetAccessToken()) {
            gotoLogin()
        } else {
            readStores()
        }
    }

//    private fun read(onSuccess:()->Unit) {
//        if (store!!.typeId == 1) {
//            Log.e("shared Store",store!!.storeConfig!!.storeIdReference.toString())
//            SingletonHome.initHome(store!!.storeConfig!!.storeIdReference.toString(), onSuccess)
//        } else {
//            Log.e("custom Store",store!!.id.toString())
//            SingletonHome.initHome(store!!.id.toString(), onSuccess)
//        }
//    }

    fun readProducts(){
        if (!isLoadingLinear)
        stateControllerProducts.startRead()

        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("storeNestedSectionId",selectedStoreNestedSection.value!!.id.toString())
            .addFormDataPart("storeId",store!!.id.toString())
            .build()

        requestServer.request2(body,"getProducts",{code,fail->
            if (!isLoadingLinear)
            stateControllerProducts.errorStateRead(fail)
            else
                stateControllerProducts.errorStateAUD(fail)
        }
        ){data->

            products.value =
                MyJson.IgnoreUnknownKeys.decodeFromString(
                    data
                )

            if (!isLoadingLinear){
                stateControllerProducts.successState()
            }else{
                isLoadingLinear = false
            }

        }
    }

    fun readStores(){
        stateController.startRead()
        val body = builderForm2().build()

        requestServer.request2(body,"getStores",{code,fail->
            stateController.errorStateRead(fail)
        }
        ){data->

            stores.value =
                MyJson.IgnoreUnknownKeys.decodeFromString(
                    data
                )
            store = stores.value.first()
            SingletonStores.selectedStore = store!!
            initHome(store!!.id.toString()){
                if (home!!.storeCategories.isNotEmpty() && home!!.storeSections.isNotEmpty() && home!!.storeNestedSections.isNotEmpty()){
                    Log.e("dfee666",isEmptyComponent.toString())
                    selectedCategory.value = home!!.storeCategories.first()
                    selectedSection.value = home!!.storeSections.first()
                    selectedStoreNestedSection.value = home!!.storeNestedSections.first()
                    isEmptyComponent = false
                    Log.e("dfee667",isEmptyComponent.toString())
                    readProducts()
                }
            }
//            stateController.successState()

        }
    }


    @Composable
    fun DropDownDemo() {

        val isDropDownExpanded = remember {
            mutableStateOf(false)
        }

//    val itemPosition = remember {
//        mutableStateOf(0)
//    }
//
//    val usernames = listOf("Alexander", "Isabella", "Benjamin", "Sophia", "Christopher")

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,

                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.White)
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            isDropDownExpanded.value = true
                        }
                ) {
                    Text(text = selectedCategory.value!!.categoryName )
                    Text(text = "عرض الكل" )
                }
                DropdownMenu(
                    expanded = isDropDownExpanded.value,
                    onDismissRequest = {
                        isDropDownExpanded.value = false
                    }) {
                    home!!.storeCategories .forEachIndexed { index, username ->
                        DropdownMenuItem(text = {
                            Text(text = username.categoryName)
                        },
                            onClick = {

                                selectedCategory.value = home!!.storeCategories.first { it.categoryId == username.categoryId }
                                selectedSection.value = home!!.storeSections.first { it.storeCategoryId == selectedCategory.value!!.id }
                                selectedStoreNestedSection.value = home!!.storeNestedSections.first { it.storeSectionId == selectedSection.value!!.id }
                                isDropDownExpanded.value = false
                                products.value = emptyList()
                                readProducts()
                            })
                    }
                }
            }

        }
    }
    private fun goToAddToCart(
        s: StoreProduct
    ) {

        val intent = Intent(
            this,
            AddToCartActivity::class.java
        )
        intent.putExtra("product", MyJson.MyJson.encodeToString(s))
        startActivity(intent)
    }


    @Composable
    fun StarRating(stars: Int) {
        // Ensure that stars never exceed 5
        val clampedStars = stars.coerceIn(0, 5)  // Clamps the value between 0 and 5

        Row(modifier = Modifier) {
            // Display full stars
            repeat(clampedStars) {
                Icon(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(20.dp),
                    imageVector = Icons.Filled.Star,
                    tint = Color.Red,
                    contentDescription = null
                )
            }

            // Display empty stars (remaining stars to make it 5 stars total)
            repeat(5 - clampedStars) {
                Icon(
                    modifier = Modifier
                        .padding(2.dp)
                        .size(20.dp),
                    imageVector = Icons.Outlined.Star,
                    tint = Color.Gray,
                    contentDescription = null
                )
            }
        }
    }

    private fun subscribeToAppTopic() {
        val appId = "3"
        Firebase.messaging.subscribeToTopic(appId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    requestServer.serverConfig.setSubscribeApp("1")
                    Log.e("subsecribed",appId)
                }
            }
    }
    private fun gotoLogin() {
        val intent =
            Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }

    private fun initHome(storeId: String, onSuccess: () -> Unit){
        if (homeStorage.isSetHome(storeId)) {
            val diff =
                Duration.between(homeStorage.getDate(storeId), getCurrentDate()).toMinutes()
            if (diff <= 1) {

                home = homeStorage.getHome(storeId)
                Log.e("storedHome", home.toString())
                stateController.successState()
                onSuccess()
            }
            else{
                Log.e("frf23478", home.toString())
                readHome(storeId, onSuccess )
            }
        }else{
            Log.e("frf2344", home.toString())
            readHome(storeId, onSuccess )
        }

    }
    private fun readHome(storeId: String, onSuccess: () -> Unit) {
        val body = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("storeId",storeId)
            .build()

        requestServer.request2(body, "getHome", { code, fail ->
            stateController.errorStateRead(fail)
        }) { data ->
            val result: Home =
                MyJson.IgnoreUnknownKeys.decodeFromString(
                    data
                )
            home = result
            homeStorage.setHome(data,storeId)
            Log.e("dsd", home.toString())
            Log.e("dsd2",result.toString())
            stateController.successState()
            onSuccess()
        }
    }
}

data class PageModel(val pageName:String,val pageId:Int)