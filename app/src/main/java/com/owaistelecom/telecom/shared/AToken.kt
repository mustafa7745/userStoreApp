package com.owaistelecom.telecom.shared
import android.content.Context
import com.owaistelecom.telecom.storage.GetStorage
import android.util.Log
import com.owaistelecom.telecom.models.AccessToken
import com.owaistelecom.telecom.storage.SecureDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

//
//class AToken @Inject constructor() {
//    private val inventory = "at"
//    private val getStorage = GetStorage(inventory);
//    private val Accesstoken = "at123"
//    private val appId = "aid"
//
//    fun setAccessToken(data:String){
//        getStorage.setData(Accesstoken, data)
//    }
//    fun getAccessToken(): AccessToken {
//        val token = MyJson.IgnoreUnknownKeys.decodeFromString<AccessToken>(getStorage.getData(Accesstoken))
//        Log.e("token",token.toString())
//        return  token
//    }
//    fun isSetAccessToken():Boolean{
//        return try {
//            getAccessToken()
//            true
//        }catch (e:Exception){
//            setAccessToken("")
//            false
//        }
//    }
//
//    fun getAppId():String{
//        return getStorage.getData(appId)
//    }
//    fun setAppId(data:String){
//        getStorage.setData(appId, data)
//    }
//    fun isSetAppId(): Boolean {
//        return getAppId().isNotEmpty()
//    }
//}

@Singleton
class AToken @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val secureStorage = SecureDataStore(context)
    private val accessTokenKey = "token"
    private val appIdKey = "app_id"
    lateinit var accessToken: AccessToken

    suspend fun setAccessToken(data: String) {
        withContext(Dispatchers.IO) {
            secureStorage.setSecureString(accessTokenKey, data)
            val at = getAccessToken1()
            if(at != null){
                accessToken = at
            }
        }
    }

    private suspend fun getAccessToken1(): AccessToken? {
        return withContext(Dispatchers.IO) {
            try {
                val json = secureStorage.getSecureString(accessTokenKey)
                if (json.isNotEmpty()) {
                    MyJson.IgnoreUnknownKeys.decodeFromString(json)
                } else null
            } catch (e: Exception) {
                Log.e("AToken2", "Failed to decode token", e)
                null
            }
        }
    }
    suspend fun getAccessToken(): AccessToken {
        return accessToken
    }

    suspend fun isSetAccessToken(): Boolean {
        val at = getAccessToken1()
         if(at != null){
             accessToken = at
            return true
        }
        return false
    }

    suspend fun setAppId(data: String) {
        withContext(Dispatchers.IO) {
            secureStorage.setSecureString(appIdKey, data)
        }
    }

    suspend fun getAppId(): String {
        return withContext(Dispatchers.IO) {
            secureStorage.getSecureString(appIdKey)
        }
    }

    suspend fun isSetAppId(): Boolean {
        return getAppId().isNotEmpty()
    }
}
