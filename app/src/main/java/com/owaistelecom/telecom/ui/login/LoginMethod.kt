package com.owaistelecom.telecom.ui.login

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.owaistelecom.telecom.ui.general_info.GeneralInfoActivity
import com.owaistelecom.telecom.ui.inside_store.InsideStoreActivity
import kotlinx.serialization.Serializable

fun goToDashboard(activity: ComponentActivity) {
    val intent = Intent(activity, InsideStoreActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    activity.startActivity(intent)
    activity.finish()
//    val intent = Intent(activity, InsideStoreActivity::class.java)
//    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//    activity.startActivity(intent)
}

fun intentFunWhatsapp(componentActivity: ComponentActivity, message: String): Boolean {
    val formattedNumber = "967781874077"
    // Create the URI for the WhatsApp link
    val uri =
        "https://api.whatsapp.com/send?phone=$formattedNumber&text=${Uri.encode(message)}"

    // Create an Intent to open the WhatsApp application
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(uri)
        putExtra(Intent.EXTRA_TEXT, message)
    }
    try {
       componentActivity.startActivity(intent)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(componentActivity, "يجب تثبيت الواتس اولا", Toast.LENGTH_LONG).show()
        return false
    }
}
private fun gotoGeneralInfo(context: Context) {
    val intent = Intent(context, GeneralInfoActivity::class.java)
    context. startActivity(intent)
}

@Serializable
data class LoginConfiguration (val countries:List<Country>)
@Serializable
data class Country(val name:  Map<String, String>, val code: String)