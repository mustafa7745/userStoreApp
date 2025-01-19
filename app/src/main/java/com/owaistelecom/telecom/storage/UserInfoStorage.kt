package com.owaistelecom.telecom.storage

import GetStorage
import com.owaistelecom.telecom.shared.getCurrentDate
import com.owaistelecom.telecom.models.UserInfo
import com.owaistelecom.telecom.shared.MyJson
import java.time.LocalDateTime

class UserInfoStorage {
    private val getStorage = GetStorage("user")
    private val info1 = "info"
    private val dateKey = "dateKey"

    fun isSet():Boolean{
        return try {
//            Log.e("gtgt",getHome().toString())
            getData()
            true
        }catch (e:Exception){
            setData("")
            false
        }
    }
    fun setData(data:String){
        getStorage.setData(dateKey, getCurrentDate().toString())
        getStorage.setData(info1,data)
    }

    fun getDate(): LocalDateTime? {
       return (LocalDateTime.parse(getStorage.getData(dateKey)))
    }
    fun getData(): UserInfo {
       return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(info1))
    }
}