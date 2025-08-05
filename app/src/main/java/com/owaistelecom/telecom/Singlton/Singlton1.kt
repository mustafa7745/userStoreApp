package com.owaistelecom.telecom.Singlton

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.owaistelecom.telecom.models.Home
import com.owaistelecom.telecom.models.HomeProduct
import com.owaistelecom.telecom.models.PrimaryProduct
import com.owaistelecom.telecom.models.Product
import com.owaistelecom.telecom.models.Store
import com.owaistelecom.telecom.models.StoreNestedSection
import com.owaistelecom.telecom.shared.AToken
import com.owaistelecom.telecom.shared.AppInfoMethod
import com.owaistelecom.telecom.shared.RemoteConfigModel
import com.owaistelecom.telecom.shared.ServerConfig
import okhttp3.MultipartBody
import javax.inject.Inject
import javax.inject.Singleton




@Singleton
class AppSession @Inject constructor() {
    lateinit var selectedStore: Store
    lateinit var remoteConfig: RemoteConfigModel
    var appToken = "101"
    var isOpen: Boolean = false
    var location: LatLng? = null
    lateinit var home: Home
    var homeProducts by mutableStateOf<Map<Int,HomeProduct>>(emptyMap())
    var isHomeLoaded: Boolean = false
    lateinit var selectedProduct: PrimaryProduct
    var selectedStoreNestedSection by mutableStateOf<StoreNestedSection?>(null)
}

@Singleton
class FormBuilder @Inject constructor(
    private val serverConfig: ServerConfig,
    private val remoteConfigRepository: AppSession,
    private val appInfoMethod: AppInfoMethod,
    private val aToken: AToken,
    private val appSession: AppSession
)
{

    fun sharedBuilderForm(): MultipartBody.Builder {
        val appToken = appSession.appToken
        val remoteConfigVersion = remoteConfigRepository.remoteConfig.REMOTE_CONFIG_VERSION

        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("sha",  appInfoMethod.getAppSha())
            .addFormDataPart("packageName", appInfoMethod.getAppPackageName())
            .addFormDataPart("deviceId", appInfoMethod.getDeviceId().toString())
            .addFormDataPart("appToken", appToken)
            .addFormDataPart("remoteConfigVersion", remoteConfigVersion.toString())
    }
    fun sharedBuilderFormWithStoreId(): MultipartBody.Builder {
        return sharedBuilderForm()
            .addFormDataPart("storeId", appSession.selectedStore.id.toString())
    }

    fun loginBuilderForm(): MultipartBody.Builder {
        return sharedBuilderForm()
            .addFormDataPart("model", Build.MODEL)
            .addFormDataPart("version", Build.VERSION.RELEASE)
    }

    fun builderFormWithAccessToken(): MultipartBody.Builder {
        return sharedBuilderForm()
            .addFormDataPart("accessToken", aToken.accessToken.token)
    }

    fun builderFormWithAccessTokenAndStoreId_2(token:String): MultipartBody.Builder {
        return builderFormWithAccessToken2(token)
            .addFormDataPart("storeId", appSession.selectedStore.id.toString())
    }
    fun builderFormWithAccessTokenAndStoreId(): MultipartBody.Builder {
        return builderFormWithAccessToken()
            .addFormDataPart("storeId", appSession.selectedStore.id.toString())
    }
    fun builderFormWithAccessToken2(token: String): MultipartBody.Builder {
        return sharedBuilderForm()
            .addFormDataPart("accessToken", token)
    }

}
