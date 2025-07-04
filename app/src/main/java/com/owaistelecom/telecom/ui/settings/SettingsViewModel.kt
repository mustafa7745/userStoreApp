package com.owaistelecom.telecom.ui.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.Singlton.FormBuilder
import com.owaistelecom.telecom.models.UserInfo
import com.owaistelecom.telecom.shared.AToken
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.RequestServer2
import com.owaistelecom.telecom.shared.StateController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val requestServer: RequestServer2,
    private val builder: FormBuilder,
     val appSession: AppSession,
    private val aToken: AToken
) :ViewModel(){
    val stateController = StateController()
    var userInfo by mutableStateOf<UserInfo?>(null)
    var storeLocationLatitude : String? = null
    var storeLocationLongitude : String? = null

    var openMap by mutableStateOf(false)
    var gotoLogin by mutableStateOf(false)
    fun getStoreLocation(){
        if (storeLocationLongitude != null){
            openMap = true
        }else
        viewModelScope.launch {
            stateController.startAud()
            try {
                val body = builder.sharedBuilderFormWithStoreId()

                val data = requestServer.request(body, "getStoreLocation")
                val result:StringData  = MyJson.IgnoreUnknownKeys.decodeFromString(data as String)

                val latLng = result.data // مثال: "21.4225,39.8262"
                val parts = latLng.split(",")
                val latitude = parts[0].trim()
                val longitude = parts[1].trim()
                storeLocationLatitude = latitude
                storeLocationLongitude = longitude
                openMap = true
                stateController.successStateAUD()
            } catch (e: Exception) {
                stateController.errorStateAUD(e.message.toString())
            }
        }

//        stateController.startAud()
//
//        requestServer.checkTokenIsNeedUpdate({ _, fail ->
//            stateController.errorStateRead(fail)
//        }){ token->
//            val body = builder.builderFormWithAccessTokenAndStoreId_2(token).build()
//
//            requestServer.request2(body, "getStoreLocation", { code, fail ->
//                stateController.errorStateAUD(fail)
//            }
//            ) { data ->
//                // استخراج خط العرض والطول
//                val result:StringData  =
//                    MyJson.IgnoreUnknownKeys.decodeFromString(
//                        data
//                    )
////
//                val latLng = result.data // مثال: "21.4225,39.8262"
//                val parts = latLng.split(",")
//                val latitude = parts[0].trim()
//                val longitude = parts[1].trim()
//                println(latitude)
//
//                onSuccess(appSession.selectedStore.name,latitude, longitude)
//                stateController.successStateAUD()
//            }
//        }
    }


    fun logout(onSuccess: () -> Unit) {
        stateController.startAud()
        viewModelScope.launch {
            try {
                val body = builder.sharedBuilderFormWithStoreId()
                val data = requestServer.request(body, "logout")

                aToken.setAccessToken("")
                stateController.showMessage("تم تسجيل الخروج بنجاح")
                gotoLogin = true

                stateController.successState()
            } catch (e: Exception) {
                stateController.errorStateAUD(e.message.toString())
            }
        }
//
//        val body =builder.builderFormWithAccessToken().build()
//        requestServer.request2(body, "logout", { code, fail ->
//            stateController.errorStateAUD(fail)
//        }
//        ) { data ->
//            viewModelScope.launch {
//                aToken.setAccessToken("")
//            }
//
//            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("YOUR_WEB_CLIENT_ID")
//                .requestEmail()
//                .build()
//
//            val googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//            googleSignInClient.signOut()
//                .addOnCompleteListener {
//                    // ✅ تم تسجيل الخروج
//                    Toast.makeText(this, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show()
//
//                }
//
//            onSuccess()
////            gotoLogin()
//
//        }

    }
}