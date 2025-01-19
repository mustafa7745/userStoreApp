package com.owaistelecom.telecom.storage

import GetStorage
import android.util.Log
import com.owaistelecom.telecom.shared.getCurrentDate
import com.owaistelecom.telecom.models.UserInfo
import com.owaistelecom.telecom.shared.MyJson
import kotlinx.serialization.encodeToString
import java.time.LocalDateTime

class ThemeStorage {
    private val getStorage = GetStorage("theme")
    private val info1 = "info"
    private val dateKey = "dateKey"

    fun isDarkMode():Boolean{
        return try {
          val s = getData()
              Log.e("gtgt",s.toString())
          return  s
        }catch (e:Exception){
            Log.e("gtgt","fffaaaa")
            setData(MyJson.IgnoreUnknownKeys.encodeToString(false))
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
    fun getData(): Boolean {
       return MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(info1))
    }
}