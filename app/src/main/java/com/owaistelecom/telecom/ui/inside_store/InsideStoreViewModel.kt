package com.owaistelecom.telecom.ui.inside_store

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.Singlton.FormBuilder
import com.owaistelecom.telecom.models.Currency
import com.owaistelecom.telecom.models.CustomPrice
import com.owaistelecom.telecom.models.Home
import com.owaistelecom.telecom.models.PrimaryProduct
import com.owaistelecom.telecom.models.Product
import com.owaistelecom.telecom.models.ProductOption
import com.owaistelecom.telecom.models.ProductView
import com.owaistelecom.telecom.models.StoreCategory
import com.owaistelecom.telecom.models.StoreNestedSection
import com.owaistelecom.telecom.models.StoreProduct
import com.owaistelecom.telecom.models.StoreSection
import com.owaistelecom.telecom.shared.AToken
import com.owaistelecom.telecom.shared.CustomException
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.RequestServer2
import com.owaistelecom.telecom.shared.ServerConfig
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.shared.isStoreOpen
import com.owaistelecom.telecom.storage.HomeStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class InsideStoreViewModel @Inject constructor(
    private val requestServer: RequestServer2,
    val appSession: AppSession,
    private val formBuilder: FormBuilder,
    private val remoteConfigRepository: AppSession,
    private val serverConfig: ServerConfig,
     val aToken: AToken
) : ViewModel() {
    val stateController = StateController()
    val stateControllerProducts = StateController()
    val homeStorage = HomeStorage()
    private var customPrices by mutableStateOf<List<CustomPrice>?>(null)

    var selectedCategory by mutableStateOf<StoreCategory?>(null)
    var selectedSection by mutableStateOf<StoreSection?>(null)


    var productViews by mutableStateOf<List<ProductView>>(listOf())
    lateinit var home: Home

    val imageBackgroundColors = listOf(
        // ✨ ألوان فاتحة (ناعمة / باستيل)
        Color(0xFFFFEBEE), // وردي فاتح
        Color(0xFFE3F2FD), // أزرق سماوي
        Color(0xFFF1F8E9), // أخضر فاتح
        Color(0xFFFFF3E0), // برتقالي فاتح
        Color(0xFFEDE7F6), // بنفسجي فاتح
        Color(0xFFFFFDE7), // أصفر فاتح
        Color(0xFFE0F7FA), // أزرق مخضر فاتح
        Color(0xFFF3E5F5), // بنفسجي زهري فاتح

        // 🌈 ألوان زاهية ومبهجة
        Color(0xFFFFCDD2), // وردي زهري
        Color(0xFF81D4FA), // أزرق فيروزي
        Color(0xFF80CBC4), // فيروزي مخضر
        Color(0xFFFFF176), // أصفر مشرق
        Color(0xFFFFAB91), // خوخي مشرق
        Color(0xFFCE93D8), // بنفسجي متوسط

        // 🌑 ألوان داكنة أنيقة
        Color(0xFFB71C1C), // أحمر غامق
        Color(0xFF1A237E), // أزرق ملكي داكن
        Color(0xFF004D40), // أخضر زمردي داكن
        Color(0xFF4E342E), // بني كاكاوي داكن
        Color(0xFF263238), // رمادي فحمي أنيق
        Color(0xFF311B92), // بنفسجي داكن ملكي
        Color(0xFF37474F), // أزرق رمادي داكن

        // ☁️ محايدة ناعمة
        Color(0xFFF5F5F5), // رمادي فاتح جداً
        Color(0xFFE0E0E0), // رمادي ناعم
    )

//    val imageBackgroundColors = listOf(
//        Color(0xFFFFEBEE), // وردي فاتح
//        Color(0xFFE3F2FD), // أزرق سماوي
//        Color(0xFFF1F8E9), // أخضر فاتح
//        Color(0xFFFFF3E0), // برتقالي فاتح
//        Color(0xFFEDE7F6), // بنفسجي فاتح
//
//        // ألوان إضافية
//        Color(0xFFFFFDE7), // أصفر فاتح
//        Color(0xFFE0F7FA), // أزرق مخضر فاتح
//        Color(0xFFE8F5E9), // أخضر نعناعي فاتح
//        Color(0xFFFBE9E7), // خوخي وردي فاتح
//        Color(0xFFF3E5F5), // بنفسجي زهري فاتح
//        Color(0xFFFFF8E1), // ذهبي كريمي فاتح
//        Color(0xFFD7CCC8), // رمادي بني فاتح
//        Color(0xFFE1F5FE), // أزرق ثلجي فاتح
//        Color(0xFFF9FBE7), // ليموني فاتح
//        Color(0xFFEFEBE9)  // بيج رملي فاتح
//    )


    var isLoadingLinear by mutableStateOf(false)


//    fun appSession(): AppSession {
//        return appSession
//    }


//    private val _shouldExit = MutableStateFlow(false)
//    val shouldExit: StateFlow<Boolean> = _shouldExit
var shouldExit by mutableStateOf(false)

    suspend fun checkAccessToken() {
            val isSet = aToken.isSetAccessToken()
            if (!isSet) {
                shouldExit = true // ✅ التعديل يكون على الـ private MutableStateFlow
            }
            else{
                get()
            }

    }





    suspend fun get(){
        getHome{
            appSession.selectedStore = home.stores.first()
            val isOpen = isStoreOpen(home.storeTime)
            appSession.isOpen = isOpen
            if (home.storeCategories.isNotEmpty() && home.storeSections.isNotEmpty() && home.storeNestedSections.isNotEmpty()){

                selectedCategory = home.storeCategories.first()
                selectedSection = home.storeSections.first()
                appSession.selectedStoreNestedSection = home.storeNestedSections.first()
                appSession.homeProducts += mapOf(appSession.selectedStoreNestedSection!!.id to home.homeProducts)
//                readProducts()
                productViews = processProductViews()
            }
        }
    }

    private fun processProductViews(): List<ProductView> {
//        val productViews = mutableListOf<ProductView>()
        val options = home.homeProducts.options
        val primaryProducts = home.homeProducts.products
        val productsImages = home.homeProducts.productsImages
        val storeCurrencies = home.storeCurrencies
        val storeProducts = home.homeProducts.storeProducts

        home.storeProductViews.forEach { storeProductView ->
            val products = mutableListOf<StoreProduct>()

            storeProducts
                .filter { it.storeProductViewId == storeProductView.storeProductViewId }
                .groupBy { Pair(it.productId, it.storeNestedSectionId) }
                .forEach { (key, groupedStoreProducts) ->
                    val (productId, storeNestedSectionId) = key
                    val primaryProduct = primaryProducts.find { it.id == productId } ?: return@forEach
                    val imageList = productsImages.filter { it.productId == productId }

                    val productOptions = groupedStoreProducts.filter { it.storeProductViewId == storeProductView.storeProductViewId }.mapNotNull { sp ->
                        val option = options.find { it.id == sp.optionId } ?: return@mapNotNull null
                        val currency = storeCurrencies.find { it.currencyId == sp.currencyId } ?: return@mapNotNull null

                        ProductOption(
                            storeProductId = sp.id,
                            currency = Currency(currency.currencyId, currency.currencyName),
                            name = option.name,
                            price = sp.price.toString()
                        )
                    }

                    products += StoreProduct(
                        product = Product(
                            productId = primaryProduct.id,
                            productName = primaryProduct.name,
                            productDescription = primaryProduct.description,
                            images = imageList
                        ),
                        storeNestedSectionId = storeNestedSectionId,
                        options = productOptions
                    )
                }

            productViews += ProductView(
                id = storeProductView.productViewId,
                name = storeProductView.name,
                products = products
            )
        }
        return productViews
    }


//    private fun processProductViews(): List<ProductView> {
//        val productViews = mutableListOf<ProductView>()
//        val options = home.homeProducts.options
//        val primaryProducts = home.homeProducts.products
//        val productsImages = home.homeProducts.productsImages
//        val storeCurrencies = home.storeCurrencies
//        val storeProducts = home.homeProducts.storeProducts
//
//        home.storeProductViews.forEach { storeProductView ->
//
//            val products = mutableListOf<StoreProduct>()
//
//            var usedProductIds = mutableSetOf<String>()
//
//            storeProducts.filter { it.storeProductViewId == storeProductView.storeProductViewId }
//                .forEach { storeProduct ->
//                    val productId = storeProduct.productId
//                    if (usedProductIds.contains(productId.toString())) return@forEach
//
//                    val primaryProduct = primaryProducts.find { it.id == productId } ?: return@forEach
//                    val imageList = productsImages.filter { it.productId == productId }
//
//                    val productOptions = storeProducts
//                        .filter { it.productId == productId }
//                        .mapNotNull { sp ->
//                            val option = options.find { it.id == sp.optionId } ?: return@mapNotNull null
//                            val currency = storeCurrencies.find { it.currencyId == sp.currencyId } ?: return@mapNotNull null
//
//                            ProductOption(
//                                storeProductId = sp.id,
//                                currency = Currency(currency.currencyId, currency.currencyName),
//                                name = option.name,
//                                price = sp.price.toString()
//                            )
//                        }
//
//                    products += StoreProduct(
//                        product = Product(
//                            productId = primaryProduct.id,
//                            productName = primaryProduct.name,
//                            productDescription = primaryProduct.description,
//                            images = imageList
//                        ),
//                        storeNestedSectionId = storeProduct.storeNestedSectionId,
//                        options = productOptions
//                    )
//
//                    usedProductIds.add(productId.toString())
//                }
//
//            productViews += ProductView(
//                id = storeProductView.productViewId,
//                name = storeProductView.name,
//                products = products
//            )
//        }
//
//        return productViews
//    }


//    private fun processProductViews(): List<ProductView> {
//        val productViews = mutableListOf<ProductView>()
//
//        ///
////        val storeProducts = home.homeProducts.storeProducts
//        val options =home.homeProducts.options
//        val primaryProducts =home.homeProducts.products
//        val productsImages = home.homeProducts.productsImages
//        val storeCurrencies = home.storeCategories
//
//
//        home.storeProductViews.forEach { storeProductView ->
//            var products = mutableListOf<StoreProduct>()
//            home.homeProducts.storeProducts.forEach { storeProduct ->
//
//                products = products.filter { it.product.productId == storeProduct.productId }.toMutableList()
//                val p = primaryProducts.filter { it.id == storeProduct.productId }
//                p.forEach {primaryProduct ->
//                    val options = mutableListOf<ProductOption>()
//                    home.homeProducts.options.forEach {option->
//                        home.homeProducts.storeProducts.forEach { storeProduct1 ->
//                            if (option.id == storeProduct1.optionId){
//
//                                val cu = home.storeCurrencies.find { it.currencyId == storeProduct1.currencyId }
//                                if (cu != null){
//                                    options += ProductOption(storeProduct1.id,
//                                        Currency(cu.currencyId,cu.currencyName),option.name,
//                                        storeProduct1.price.toString()
//                                    )
//                                }
//                            }
//                        }
//                    }
//
//                    if (storeProductView.storeProductViewId == storeProduct.storeProductViewId){
//                        products += StoreProduct(
//                            Product(primaryProduct.id,primaryProduct.name,primaryProduct.description,productsImages.filter { it.productId == primaryProduct.id }),
//                            storeProduct.storeNestedSectionId,
//                            options
//                        )
//                    }
//                }
//
//            }
//            productViews +=  ProductView(
//                    products = products,
//            id= storeProductView.storeProductViewId,
//            name =  storeProductView.name
//            )
//        }
//        return  productViews
//
//    }

    private suspend fun getHome(onSuccess: () -> Unit){

//        val storeId = appSession.selectedStore.id.toString()
//        Log.e("DDDAA22",stores.value.toString())
//        if (homeStorage.isSetHome(storeId)) {
////            Log.e("DDDAA33",stores.value.toString())
//            val diff =
//                Duration.between(homeStorage.getDate(storeId), getCurrentDate()).toSeconds()
//            if (diff <= 20) {
//
//                home = homeStorage.getHome(storeId)
//                Log.e("storedHome", home.toString())
//                stateController.successState()
//                onSuccess()
//            }
//            else{
////                Log.e("frf23478", home.toString())
//                readHome(onSuccess)
//            }
//        }else{
////            Log.e("frf2344", home.toString())
//
//        }
        readHome(onSuccess)
    }

    fun selectProduct(product: PrimaryProduct){
        appSession.selectedProduct = product
    }

 private suspend fun readHome(onSuccess: () -> Unit) {
//     if (appSession.isHomeLoaded){
//         stateController.successState()
//         return
//     }

        stateController.startRead()
        try {
            val body = formBuilder.loginBuilderForm()

            val data = requestServer.request(body, "getHome")
            val result: Home = MyJson.IgnoreUnknownKeys.decodeFromString(data as String)
            home = result
            appSession.home = home


//            homeStorage.setHome(data,storeId)
            Log.e("dsd", home.toString())
            Log.e("dsd2",result.toString())
            stateController.successState()
            onSuccess()
            appSession.isHomeLoaded = true

        } catch (e: Exception) {
            Log.e("UUURRRL3",e.message.toString())
            stateController.errorStateRead(e.message.toString())
        }

//        requestServer.checkTokenIsNeedUpdate({ _, fail ->
//            stateController.errorStateRead(fail)
//        }){ token->
//            Log.e("aEEE","STARt")
//            val body = formBuilder.builderFormWithAccessToken2(token).build()
//            requestServer.request2(body, "getHome", { _, fail ->
//                stateController.errorStateRead(fail)
//            }
//            ) { data ->
//                val result: Home =
//                    MyJson.IgnoreUnknownKeys.decodeFromString(
//                        data
//                    )
//                home = result
//
//                homeStorage.setHome(data,storeId)
//                Log.e("dsd", home.toString())
//                Log.e("dsd2",result.toString())
//                stateController.successState()
//                onSuccess()
//            }
//        }
    }

    suspend fun initApp(){
        stateController.startRead()
        try {
            if (!requestServer.isInternetAvailable()){
                throw CustomException(0,"No Internet")
            }
            if (!serverConfig.isSetSubscribeApp()){
                val appId = "101"
                val success = serverConfig.subscribeToTopicSuspend(appId)
                if (success) {
                    serverConfig.setSubscribeApp(appId)
                    Log.d("Topic", "Subscribed successfully")
                } else {
                    Log.d("Topic", "Subscription failed")
                }
            }
            else{
                Log.e("App Sub Stored ","Done")
            }
            serverConfig.getFcmTokenSuspend()

            if (serverConfig.isSetRemoteConfig()){
                Log.e("Remote Config V",appSession.remoteConfig.toString())
            }else{
                requestServer.initVarConfig()
            }
            //
            checkAccessToken()
        }catch (e: CustomException){
            Log.e("Custom Error init server conige",e.message)
            stateController.errorStateRead(e.message)
        }
    }

    suspend fun readProducts(){
        if (appSession.homeProducts.containsKey(appSession.selectedStoreNestedSection!!.id)) {
            if (!isLoadingLinear){
                stateControllerProducts.successState()
            }else{
                isLoadingLinear = false
            }
            return
        }

        if (!isLoadingLinear)
            stateControllerProducts.startRead()
//

        try {
            val body = formBuilder.sharedBuilderFormWithStoreId()
                .addFormDataPart("storeNestedSectionId",appSession.selectedStoreNestedSection!!.id.toString())

            val data = requestServer.request(body, "getProducts")
//            appSession.home =  appSession.home.copy(homeProducts =  MyJson.IgnoreUnknownKeys.decodeFromString(data as String))
            appSession.homeProducts += mapOf(appSession.selectedStoreNestedSection!!.id to MyJson.IgnoreUnknownKeys.decodeFromString(data as String))
            if (!isLoadingLinear){
                stateControllerProducts.successState()
            }else{
                isLoadingLinear = false
            }


        } catch (e: Exception) {
            if (!isLoadingLinear)
                stateControllerProducts.errorStateRead(e.message.toString())
            else
                stateControllerProducts.errorStateAUD(e.message.toString())
            isLoadingLinear = false
        }

//        val body = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("storeNestedSectionId",selectedStoreNestedSection!!.id.toString())
//            .addFormDataPart("storeId",appSession.selectedStore.id.toString())
//            .build()
//
//        requestServer.request2(body,"getProducts",{code,fail->
//            if (!isLoadingLinear)
//                stateControllerProducts.errorStateRead(fail)
//            else
//                stateControllerProducts.errorStateAUD(fail)
//            isLoadingLinear = false
//        }
//        ){data->
//
//
//
//            productViews =
//                MyJson.IgnoreUnknownKeys.decodeFromString(
//                    data
//                )
//
//            if (CustomSingleton.selectedStore!!.storeConfig != null){
//                readCustomPrices({
//                    stateController.errorStateRead(it)
//                }){
//                    customPrices = it
//                    onSuccess()
//
//                    processCustomPrices()
//                    if (!isLoadingLinear){
//                        stateControllerProducts.successState()
//                    }else{
//                        isLoadingLinear = false
//                    }
//                }
//            }else{
//                onSuccess()
//                if (!isLoadingLinear){
//                    stateControllerProducts.successState()
//                }else{
//                    isLoadingLinear = false
//                }
//            }
////            isGetProduct = true
//        }
    }

    private fun readCustomPrices(onFail: (data: String) -> Unit, onSuccess: (data: List<CustomPrice>) -> Unit){
//        stateController.startRead()
//
//        val body = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("storeId",CustomSingleton.selectedStore!!.id.toString())
//            .build()
//
//        requestServer.request2(body,"getCustomPrices",{code,fail->
//            onFail(fail)
//        }
//        ){data->
//
//            val result:List<CustomPrice> =
//                MyJson.IgnoreUnknownKeys.decodeFromString(
//                    data
//                )
//            onSuccess(result)
//        }
    }

    private fun processCustomPrices() {
        if (customPrices != null) {
            productViews = productViews.map { view ->
                val products = view.products.map { product ->
                    // Update the products of each store product
                    val updatedOptions = product.options.map { option ->
//                        Log.e("ffff2334",customPrice.toString())
                        val customPrice =
                            customPrices!!.find { it.storeProductId == option.storeProductId }
                        if (customPrice != null) {
                            Log.e("ffff",customPrice.toString())
                            option.copy(price = customPrice.price)
                        } else {
                            option
                        }
                    }
                    product.copy(options = updatedOptions)
                }
                view.copy(products = products)
            }
        }
    }

//    fun mainInit() {
//        stateController.startRead()
//        initRemoteConfig({
//           readStores()
//        }){
//            stateController.errorStateRead(it)
//        }
//    }

//    suspend fun initApp(onSuccess: () -> Unit) {
//        try {
//            if (!serverConfig.isSetSubscribeApp()){
//                val appId = "101"
//                serverConfig.subscribeToTopicSuspend(appId)
//            }
//            else{
//                Log.e("App Sub Stored ","Done")
//            }
//            serverConfig.getFcmTokenSuspend()
//
//            if (serverConfig.isSetRemoteConfig()){
//                remoteConfigRepository.remoteConfig =  serverConfig.getRemoteConfig()
//                Log.e("rrer2",remoteConfigRepository.remoteConfig.toString())
//                onSuccess()
//            }else{
//                stateController.startRead()
//                requestServer.initVarConfig()
//            }
//        }catch (e:CustomException){
//            Log.e("Custom Error init server conige",e.message.toString())
//            stateController.errorStateRead(e.message)
//        }
//
//    }
}

