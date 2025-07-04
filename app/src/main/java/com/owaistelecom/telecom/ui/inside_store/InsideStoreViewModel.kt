package com.owaistelecom.telecom.ui.inside_store

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.Singlton.FormBuilder
import com.owaistelecom.telecom.models.CustomPrice
import com.owaistelecom.telecom.models.Home
import com.owaistelecom.telecom.models.ProductView
import com.owaistelecom.telecom.models.StoreCategory
import com.owaistelecom.telecom.models.StoreNestedSection
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
    private val appSession: AppSession,
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
    var selectedStoreNestedSection by mutableStateOf<StoreNestedSection?>(null)

    var productViews by mutableStateOf<List<ProductView>>(listOf())
    lateinit var home: Home

    val imageBackgroundColors = listOf(
        Color(0xFFFFEBEE), // وردي فاتح
        Color(0xFFE3F2FD), // أزرق سماوي
        Color(0xFFF1F8E9), // أخضر فاتح
        Color(0xFFFFF3E0), // برتقالي فاتح
        Color(0xFFEDE7F6)  // بنفسجي فاتح
    )

    var isLoadingLinear by mutableStateOf(false)


    fun getAppSession(): AppSession {
        return appSession
    }


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
                selectedStoreNestedSection = home.storeNestedSections.first()
//                readProducts()
                productViews = home.products
            }
        }
    }

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

 private suspend fun readHome(onSuccess: () -> Unit) {
        stateController.startRead()

        try {
            val body = formBuilder.loginBuilderForm()

            Log.e("UUURRRL","getLoginConfiguration")
            val data = requestServer.request(body, "getHome")
            Log.e("UUURRRL2",data.toString())
            val result: Home = MyJson.IgnoreUnknownKeys.decodeFromString(data as String)
            home = result

//            homeStorage.setHome(data,storeId)
            Log.e("dsd", home.toString())
            Log.e("dsd2",result.toString())
            stateController.successState()
            onSuccess()

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
                serverConfig.subscribeToTopicSuspend(appId)
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
        if (!isLoadingLinear)
            stateControllerProducts.startRead()
//

        try {
            val body = formBuilder.sharedBuilderFormWithStoreId()
                .addFormDataPart("storeNestedSectionId",selectedStoreNestedSection!!.id.toString())

            val data = requestServer.request(body, "getProducts")
            productViews = MyJson.IgnoreUnknownKeys.decodeFromString(data as String)
            stateController.successState()
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

