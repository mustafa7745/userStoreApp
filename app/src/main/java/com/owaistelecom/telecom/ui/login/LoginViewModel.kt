package com.owaistelecom.telecom.ui.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.Singlton.FormBuilder
import com.owaistelecom.telecom.shared.AToken
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.RequestServer2
import com.owaistelecom.telecom.shared.ServerConfig
import com.owaistelecom.telecom.shared.StateController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val requestServer: RequestServer2,
    private val builder: FormBuilder,
    private val aToken: AToken
) : ViewModel() {

    val stateController = StateController()
    var countryList = listOf<Country>()
    lateinit var selectedCountryCode : Country
        private set
    var isShowSelectCountryCode by mutableStateOf(false)
        private set

    fun setSelectedCountry(country: Country) {
        selectedCountryCode = country
    }

    fun toggleCountrySelector(show: Boolean) {
        isShowSelectCountryCode = show
    }


    var password by mutableStateOf("")
        private set

    var phone by mutableStateOf("")
        private set

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onPhoneChange(newPhone: String) {
        phone = newPhone
    }

    var successLogin by mutableStateOf(false)

    suspend fun login() {
        stateController.startAud()
        try {
            val body = builder.loginBuilderForm()
                .addFormDataPart("countryCode", selectedCountryCode.code)
                .addFormDataPart("phone", phone)
                .addFormDataPart("password", password)
            val data = requestServer.request(body, "login",false) as String
            aToken.setAccessToken(data)
            successLogin = true
        } catch (e: Exception) {
            Log.e("UUURRRL3",e.message.toString())
            stateController.errorStateAUD(e.message.toString())
        }
    }
    fun signInWithGoogle(idToken:String) {
        stateController.startAud()
        viewModelScope.launch {
            try {
                val body = builder.sharedBuilderForm()
                    .addFormDataPart("loginType", "Google")
                    .addFormDataPart("googleToken", idToken)

                val data = requestServer.request(body, "login",false)
                aToken.setAccessToken(data as String)
                successLogin = true
            } catch (e: Exception) {
                Log.e("UUURRRL3",e.message.toString())
                stateController.errorStateAUD(e.message.toString())
            }
        }
//        viewModelScope.launch {
//            try {
//                val body = builder.loginBuilderForm()
//                    .addFormDataPart("loginType", "Google")
//                    .addFormDataPart("googleToken", idToken)
//                val token = requestServer.suspendRequest(body, "login")
//                aToken.setAccessToken(token)
//                onSuccess()
//            } catch (e: Exception) {
//                stateController.errorStateAUD(e.message ?: "خطأ غير معروف")
//            }
//        }
    }

     suspend fun getLoginConfiguration() {
         if (countryList.isNotEmpty()) return
        stateController.startRead()

             try {
                 val body = builder.loginBuilderForm()

                 Log.e("UUURRRL","getLoginConfiguration")
                 val data = requestServer.request(body, "getLoginConfiguration",false)
                 Log.e("UUURRRL2",data.toString())
                 val result:LoginConfiguration = MyJson.IgnoreUnknownKeys.decodeFromString(data as String)
                 countryList = result.countries
                 selectedCountryCode = countryList.first()
                 stateController.successState()
             } catch (e: Exception) {
                 Log.e("UUURRRL3",e.message.toString())
                 stateController.errorStateRead(e.message.toString())
             }

    }
}