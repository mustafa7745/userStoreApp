package com.owaistelecom.telecom.ui.cart_preview

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.Singlton.FormBuilder
import com.owaistelecom.telecom.models.Coupon
import com.owaistelecom.telecom.models.Store
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.RemoteConfigModel
import com.owaistelecom.telecom.shared.RequestServer2
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.ui.add_to_cart2.CartProduct2
import com.owaistelecom.telecom.ui.add_to_cart2.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.encodeToJsonElement
import javax.inject.Inject

@HiltViewModel
class CartPreviewViewModel @Inject constructor(
    private val requestServer: RequestServer2,
    val appSession: AppSession,
    private val formBuilder: FormBuilder,
    private val cartRepository: CartRepository
) : ViewModel() {
    var locations by mutableStateOf<List<Location>>(emptyList())
    var paymentsTypes by mutableStateOf<List<PaymentType>>(emptyList())
    val stateController = StateController()
    var selectedLocation by mutableStateOf<Location?>(null)
    var coupon by mutableStateOf<Coupon?>(null)

    var isShowReadLocations by mutableStateOf(false)
    var cartView by mutableStateOf(true)
    var isShowSelectPaymentMethod by mutableStateOf(false)
    var isShowChooseCouponCode by mutableStateOf(false)
    var isShowShowPaymentTypes by mutableStateOf(false)
    var selectedPaymentMethod by mutableStateOf<PaymentModel?>(null)

    var paidCode by mutableStateOf<String>("")


    var list = listOf<PaymentModel>(
        PaymentModel("عند الاستلام", R.drawable.ondelivery.toString(), 0),
//        PaymentModel("من المحفظة", R.drawable.wallet, 2),

    )
    var radioOptions = listOf(
    DeliveryOption(2,"الاستلام من المتجر")
    )
    init {
        if (appSession.selectedStore.hasEPayment == 1)
        list +=  PaymentModel("دفع الكتروني", R.drawable.epay.toString(), 1)
        if (appSession.selectedStore.hasDelivery == 1)
            radioOptions +=  DeliveryOption(1,"التوصيل للموقع")
    }






    var selectedOption by mutableStateOf(radioOptions[0])
    var title by mutableStateOf("")

    fun onOptionSelected(newOption: DeliveryOption) {
        selectedOption = newOption
    }
    fun isEmptyCartProducts(): Boolean {
       return cartRepository.getAllCartProducts().isEmpty()
    }
    fun getAllCartProductsSum(): String {
       return cartRepository.getAllCartProductsSum(null)
    }
    fun getAllCartProductsSumPrices(): String {
        return cartRepository.getAllCartProductsSum(selectedLocation?.deliveryPrice,coupon)
    }
    fun getAllCartProducts(): List<CartProduct2> {
        return cartRepository.getAllCartProducts()
    }

    fun emptyCartProducts(storeId:Int) {
         cartRepository.removeAllProductsFromStore(storeId)
    }

    fun getSelectedStore(): Store {
        return appSession.selectedStore
    }

    fun getRemoteConfig(): RemoteConfigModel {
        return appSession.remoteConfig
    }

   fun  getIsOpen(): Boolean {
        return appSession.isOpen
    }


    suspend fun readLocation() {
        stateController.startAud()
        try {
            val body = formBuilder.sharedBuilderFormWithStoreId()

            val data = requestServer.request(body, "getLocations")
            locations= MyJson.IgnoreUnknownKeys.decodeFromString(data as String)
            stateController.successStateAUD()
            isShowReadLocations = true
        } catch (e: Exception) {
            stateController.errorStateAUD(e.message.toString())
        }

//
//        val body = formBuilder.builderFormWithAccessToken()
//            .addFormDataPart("storeId",appSession.selectedStore.id.toString())
//            .build()
//
//        requestServer.request2(body, "getLocations", { code, fail ->
//            stateController.errorStateAUD(fail)
//        }
//        ) { data ->
//            val result: List<Location> =
//                MyJson.IgnoreUnknownKeys.decodeFromString(
//                    data
//                )
//
//            locations= result
////            SelectedStore.store.value!! .latLng = latiLng
////            MyToast(this,"تم بنجاح")
//            stateController.successStateAUD()
//
//            isShowReadLocations = true
//        }
    }
    suspend fun readPaymentTypes() {
        stateController.startAud()
        try {
            val body = formBuilder.sharedBuilderFormWithStoreId()

            val data = requestServer.request(body, "getPaymentTypes")
            paymentsTypes= MyJson.IgnoreUnknownKeys.decodeFromString(data as String)

            stateController.successStateAUD()
        } catch (e: Exception) {
            stateController.errorStateAUD(e.message.toString())
        }

//        stateController.startAud()
//        val body = formBuilder.builderFormWithAccessToken()
//            .addFormDataPart("storeId", appSession.selectedStore.id.toString())
//            .build()
//
//        requestServer.request2(body, "getPaymentTypes", { code, fail ->
//            stateController.errorStateAUD(fail)
//        }
//        ) { data ->
//            val result: List<PaymentType> =
//                MyJson.IgnoreUnknownKeys.decodeFromString(
//                    data
//                )
//
//            paymentsTypes = result
////            SelectedStore.store.value!! .latLng = latiLng
////            MyToast(this,"تم بنجاح")
//            stateController.successStateAUD()
//        }
    }

    var shouldExitToOrder by mutableStateOf(false)


    fun readCoupon(code:String) {
        viewModelScope.launch {
            stateController.startAud()
            try {
                val body = formBuilder.sharedBuilderFormWithStoreId().addFormDataPart("code",code)
                val data = requestServer.request(body, "getCoupon")
                coupon= MyJson.IgnoreUnknownKeys.decodeFromString(data as String)
                stateController.successStateAUD()
                isShowChooseCouponCode = false
            } catch (e: Exception) {
                stateController.errorStateAUD(e.message.toString())
            }
        }
    }

    suspend fun confirmOrder(onSuccess:()->Unit) {
        Log.e("dsds","ewewe")
        stateController.startAud()

        val bodyBuilder = formBuilder.sharedBuilderFormWithStoreId()
            .addFormDataPart("orderProducts", MyJson.MyJson.encodeToJsonElement(cartRepository.getProductsIdsWithQnt()).toString())


        if (selectedLocation != null) {
            bodyBuilder.addFormDataPart("locationId", selectedLocation!!.id.toString())
        }
        if (selectedPaymentMethod != null){
            bodyBuilder.addFormDataPart("paid", selectedPaymentMethod!!.id.toString())
            bodyBuilder.addFormDataPart("paidCode", paidCode)
        }
        if (coupon != null){
            bodyBuilder.addFormDataPart("couponCode", coupon!!.code.toString())
        }


        stateController.startAud()
        try {
//            val body = formBuilder.sharedBuilderFormWithStoreId()

            val data = requestServer.request(bodyBuilder, "confirmOrder")

            stateController.successStateAUD()
            stateController.showMessage("تم ارسال الطلب بنجاح")
            shouldExitToOrder = true
        } catch (e: Exception) {
            stateController.errorStateAUD(e.message.toString())
        }


//        requestServer.request2(body, "confirmOrder", { code, fail ->
//            stateController.errorStateAUD(fail)
//        }
//        ) { data ->
////            val result: List<Location> =
////                MyJson.IgnoreUnknownKeys.decodeFromString(
////                    data
////                )
//
////            locations= result
//////            SelectedStore.store.value!! .latLng = latiLng
////            MyToast(this,"تم بنجاح")
//            stateController.showMessage("تم ارسال الطلب بنجاح")
//           onSuccess()
//
////            stateController.successStateAUD()
////
////            isShowReadLocations = true
//        }
    }

    fun ShowLocations() {
        viewModelScope.launch {
            if (locations.isEmpty()) {
                readLocation()
            } else {
                isShowReadLocations = true
            }
        }
    }
    fun checkPaymentAndConfirm() {
        if (selectedPaymentMethod != null) {
//            confirmOrder()
        } else {
            isShowSelectPaymentMethod = true
            stateController.showMessage("يجب تحديد طريقة الدفع")
        }
    }

}

