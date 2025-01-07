package com.owaistelecom.telecom.shared
import GetStorage
import java.time.Duration
import java.time.LocalDateTime

class ServerConfig {
    private val inventory = "config"
    private val getStorage = GetStorage(inventory);
    private val remoteConfig = "rc"
    private val subscribeApp = "sa"
    private val dateKey = "dateKey"

    fun setRemoteConfig(data:String){
        getStorage.setData(dateKey, getCurrentDate().toString())
        getStorage.setData(remoteConfig, data)
    }

    fun getRemoteConfig(): VarRemoteConfig {
        return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(remoteConfig))
    }

    fun isSetRemoteConfig():Boolean{
        return try {
            getRemoteConfig()
                SingletonRemoteConfig.remoteConfig = getRemoteConfig()
                return true
        }catch (e:Exception){
            setRemoteConfig("")
            false
        }
    }

    //
    private fun getSubscribeApp():String{
        return getStorage.getData(subscribeApp)
    }
    fun setSubscribeApp(data:String){
        getStorage.setData(subscribeApp, data)
    }
    fun isSetSubscribeApp(): Boolean {
        return getSubscribeApp().isNotEmpty()
    }
}