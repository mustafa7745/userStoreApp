package com.owaistelecom.telecom.ui.add_to_cart

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.Singlton.FormBuilder
import com.owaistelecom.telecom.models.CustomPrice
import com.owaistelecom.telecom.models.Home
import com.owaistelecom.telecom.models.ProductView
import com.owaistelecom.telecom.models.StoreCategory
import com.owaistelecom.telecom.models.StoreNestedSection
import com.owaistelecom.telecom.models.StoreSection
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.RemoteConfigModel
import com.owaistelecom.telecom.shared.RequestServer2
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.shared.getCurrentDate
import com.owaistelecom.telecom.storage.HomeStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import okhttp3.MultipartBody
import org.json.JSONObject
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class AddToCartViewModel @Inject constructor(
    private val requestServer: RequestServer2,
    private val appSession: AppSession,
    private val formBuilder: FormBuilder,
    private val remoteConfigRepository: AppSession
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

    fun get(){
        stateController.startRead()
        getHome("1934"){
            appSession.selectedStore = home.stores.first()
            if (home.storeCategories.isNotEmpty() && home.storeSections.isNotEmpty() && home.storeNestedSections.isNotEmpty()){

                selectedCategory = home.storeCategories.first()
                selectedSection = home.storeSections.first()
                selectedStoreNestedSection = home.storeNestedSections.first()
//                readProducts()
                productViews = home.products
            }
        }
    }

    private fun getHome(storeId: String, onSuccess: () -> Unit){

//        Log.e("DDDAA22",stores.value.toString())
        if (homeStorage.isSetHome(storeId)) {
//            Log.e("DDDAA33",stores.value.toString())
            val diff =
                Duration.between(homeStorage.getDate(storeId), getCurrentDate()).toSeconds()
            if (diff <= 20) {

                home = homeStorage.getHome(storeId)
                Log.e("storedHome", home.toString())
                stateController.successState()
                onSuccess()
            }
            else{
//                Log.e("frf23478", home.toString())
                readHome(storeId, onSuccess )
            }
        }else{
//            Log.e("frf2344", home.toString())
            readHome(storeId, onSuccess )
        }
    }

    private fun readHome(storeId: String, onSuccess: () -> Unit) {
//        viewModelScope.launch {
//            try {
//                // 1. تحقق من صلاحية التوكن أو حدث تحديثه
//                val token = requestServer.checkTokenIsNeedUpdate()
//
//                // 2. بناء body مع التوكن الجديد
//                val body = formBuilder.builderFormWithAccessToken2(token).build()
//
//                // 3. طلب بيانات الصفحة الرئيسية
//                val data = requestServer.request2Suspend(body, "getHome")
//
//                // 4. فك التشفير
//                val result: Home = MyJson.IgnoreUnknownKeys.decodeFromString(data)
//
//                // 5. حفظ البيانات
//                home = result
//                homeStorage.setHome(data, storeId)
//
//                // 6. تحديث الحالة ونداء النجاح
//                stateController.successState()
//                onSuccess()
//
//            } catch (e: Exception) {
//                // التعامل مع الأخطاء
//                stateController.errorStateRead(e.message ?: "Unknown error")
//            }
//        }
    }


    private fun readHome2(storeId: String, onSuccess: () -> Unit) {
//        viewModelScope.launch {
//            try {
//                 requestServer.checkTokenIsNeedUpdate2()
//                val body = builder.loginBuilderForm()
//                    .addFormDataPart("countryCode", selectedCountryCode.code)
//                    .addFormDataPart("phone", phone)
//                    .addFormDataPart("password", password)
//                    .build()
//
//                val token = requestServer.request2Suspend(body, "login")
//                aToken.setAccessToken(token)
//                onSuccess()
//            } catch (e: Exception) {
//                stateController.errorStateAUD(e.message ?: "خطأ غير معروف")
//            }
//        }
//
//        //
//        viewModelScope.launch {
//        requestServer.checkTokenIsNeedUpdate({ _, fail ->
//            stateController.errorStateRead(fail)
//        }){ token->
//            Log.e("aEEE","STARt")
//            val body = formBuilder.builderFormWithAccessToken2(token).build()
//         viewModelScope.launch {
//             requestServer.request2(body, "getHome", { _, fail ->
//                 stateController.errorStateRead(fail)
//             }
//             ) { data ->
//                 val result: Home =
//                     MyJson.IgnoreUnknownKeys.decodeFromString(
//                         data
//                     )
//                 home = result
//
//                 homeStorage.setHome(data,storeId)
//                 Log.e("dsd", home.toString())
//                 Log.e("dsd2",result.toString())
//                 stateController.successState()
//                 onSuccess()
//             }
//         }
//
//
//            }
//        }
    }

    fun readProducts(onSuccess: () -> Unit = {}){
//        if (!isLoadingLinear)
//            stateControllerProducts.startRead()
//
//        val body = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("storeNestedSectionId",selectedStoreNestedSection!!.id.toString())
//            .addFormDataPart("storeId",appSession.selectedStore.id.toString())
//            .build()
//
//        viewModelScope.launch {
//            requestServer.request2(body, "getProducts", { code, fail ->
//                if (!isLoadingLinear)
//                    stateControllerProducts.errorStateRead(fail)
//                else
//                    stateControllerProducts.errorStateAUD(fail)
//                isLoadingLinear = false
//            }) { data ->
//
//                productViews =
//                    MyJson.IgnoreUnknownKeys.decodeFromString(
//                        data
//                    )
//
//                if (appSession.selectedStore.storeConfig != null) {
//                    readCustomPrices({
//                        stateController.errorStateRead(it)
//                    }) {
//                        customPrices = it
//                        onSuccess()
//
//                        processCustomPrices()
//                        if (!isLoadingLinear) {
//                            stateControllerProducts.successState()
//                        } else {
//                            isLoadingLinear = false
//                        }
//                    }
//                } else {
//                    onSuccess()
//                    if (!isLoadingLinear) {
//                        stateControllerProducts.successState()
//                    } else {
//                        isLoadingLinear = false
//                    }
//                }
////            isGetProduct = true
//            }
//        }
    }

    private fun readCustomPrices(onFail: (data: String) -> Unit, onSuccess: (data: List<CustomPrice>) -> Unit){
//        stateController.startRead()
//
//        val body = MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("storeId",appSession.selectedStore.id.toString())
//            .build()
//
//        viewModelScope.launch {
//            requestServer.request2(body,"getCustomPrices",{code,fail->
//                onFail(fail)
//            }
//            ){data->
//
//                val result:List<CustomPrice> =
//                    MyJson.IgnoreUnknownKeys.decodeFromString(
//                        data
//                    )
//                onSuccess(result)
//            }
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

//    fun initApp(onSuccess: () -> Unit) {
//
//        if (!requestServer.serverConfig.isSetSubscribeApp()){
//            val appId = "101"
//            Firebase.messaging.subscribeToTopic(appId)
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        requestServer.serverConfig.setSubscribeApp(appId)
//                        Log.d("Subscription", "Subscribed to topic: $appId")
//                    } else {
//                        Log.w("Subscription", "Subscription failed", task.exception)
//                    }
//                }
//        }
//
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val token = task.result
//                Log.d("FCM Token", "Token: $token")
//                if (requestServer.serverConfig.getAppToken() != token){
//                    requestServer.serverConfig.setAppToken(token)
//                }
//            }
//        }
//
//        if (requestServer.serverConfig.isSetRemoteConfig()){
//            remoteConfigRepository.remoteConfig =  requestServer.serverConfig.getRemoteConfig()
//            Log.e("rrer2",remoteConfigRepository.remoteConfig.toString())
//            onSuccess()
//        }else{
//            stateController.startRead()
//            if (!requestServer.isInternetAvailable()) {
//                stateController.errorStateRead("No Internet")
//                return
//            }
//            val remoteConfig = Firebase.remoteConfig
//            val configSettings = remoteConfigSettings {
//                minimumFetchIntervalInSeconds = 5
//                fetchTimeoutInSeconds = 60
//            }
//            remoteConfig.setConfigSettingsAsync(configSettings)
//            remoteConfig.fetchAndActivate()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        val allConfigs = remoteConfig.all
//                        val jsonObject = JSONObject()
//                        for ((key, value) in allConfigs) {
//                            jsonObject.put(key, value.asString())
//                        }
//
//                        try {
//                            val myRemoteConfig = MyJson.IgnoreUnknownKeys.decodeFromString<RemoteConfigModel>(
//                                jsonObject.toString()
//                            )
//                            requestServer.serverConfig.setRemoteConfig(MyJson.IgnoreUnknownKeys.encodeToString(myRemoteConfig))
//                            remoteConfigRepository.remoteConfig = myRemoteConfig
//                            Log.e("rrer",remoteConfigRepository.remoteConfig.toString())
//
//                        } catch (e: Exception) {
//                            stateController.errorStateRead(e.message.toString())
//                            return@addOnCompleteListener
//                        }
//
//                        onSuccess()
//
//                    } else {
//                        stateController.errorStateRead("Failed to fetch remote config: ${task.exception}")
//                    }
//                }
//        }
//
//    }
}

