package com.owaistelecom.telecom.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.owaistelecom.telecom.application.MyApplication
import com.owaistelecom.telecom.ui.general_info.GeneralInfoActivity
import com.owaistelecom.telecom.ui.login.LoginActivity
import com.owaistelecom.telecom.ui.orders.OrdersActivity
import com.owaistelecom.telecom.ui.profile.ProfileActivity

fun openStoreLocationInMap(storeName:String,latitude: String, longitude: String, context: Context) {
    // بناء رابط Google Maps
    val gmmIntentUri =
        Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${storeName})")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    mapIntent.setPackage("com.google.android.apps.maps")

    // التحقق من وجود تطبيق Google Maps
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
    } else {
        Toast.makeText(context, "Google Maps app not found", Toast.LENGTH_SHORT).show()
    }
}
fun goToLogin(context: Context){
    val intent = Intent(MyApplication.AppContext, LoginActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
    Toast.makeText(context, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show()
}

fun gotoOrders(context: Context) {
        val intent = Intent(context, OrdersActivity::class.java)
        context.startActivity(intent)
}
fun gotoProfile(context: Context) {
        val intent = Intent(context, ProfileActivity::class.java)
        context.startActivity(intent)
}
fun gotoGeneralInfo(context: Context) {
        val intent = Intent(context, GeneralInfoActivity::class.java)
       context. startActivity(intent)
}