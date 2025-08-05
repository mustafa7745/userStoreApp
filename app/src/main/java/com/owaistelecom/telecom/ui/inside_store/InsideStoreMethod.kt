package com.owaistelecom.telecom.ui.inside_store

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import com.owaistelecom.telecom.application.MyApplication
import com.owaistelecom.telecom.models.PrimaryProduct
import com.owaistelecom.telecom.models.Product
import com.owaistelecom.telecom.models.StoreProduct
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.ui.add_to_cart.AddToCart2Activity
import com.owaistelecom.telecom.ui.login.LoginActivity
import com.owaistelecom.telecom.ui.search.SearchActivity
import com.owaistelecom.telecom.ui.settings.SettingsActivity
import kotlinx.serialization.encodeToString


fun gotoLogin(componentActivity: ComponentActivity) {
    val intent = Intent(MyApplication.AppContext, LoginActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    componentActivity.startActivity(intent)
//    componentActivity.finish()
}

fun gotoSettings(context: Context) {
    val intent = Intent(context, SettingsActivity::class.java).apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
}
fun gotoSearch(context: Context) {
    val intent = Intent(context, SearchActivity::class.java).apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
}

fun goToAddToCart(context: Context,product:PrimaryProduct) {
    val intent = Intent(context, AddToCart2Activity::class.java).apply {
//        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    intent.putExtra("product", MyJson.MyJson.encodeToString(product))
    context.startActivity(intent)
}