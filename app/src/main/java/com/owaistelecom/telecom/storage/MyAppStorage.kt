package com.owaistelecom.telecom.storage

import com.owaistelecom.telecom.models.Language
import com.owaistelecom.telecom.shared.MyJson
import kotlinx.serialization.encodeToString
import java.util.Locale

class MyAppStorage {
    private val getStorage = GetStorage("appstorage")
    private val languageKey = "lang"

    fun setLang(data: Language){
        getStorage.setData(languageKey, MyJson.MyJson.encodeToString(data))
    }

    fun getLang():Language{
        return try {
             MyJson.IgnoreUnknownKeys.decodeFromString(getStorage.getData(languageKey))
        }catch (e:Exception){
         val s = Language("",Locale.getDefault().language)
            setLang(s)
            s
        }
    }
}