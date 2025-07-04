package com.owaistelecom.telecom.ui.settings

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.shared.CustomIcon3
import com.owaistelecom.telecom.shared.CustomImageViewUri
import com.owaistelecom.telecom.shared.MainComposeAUD
import com.owaistelecom.telecom.ui.settings.SettingsActivity.SettingItem
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme


@Composable
fun SettingsScreen(componentActivity: ComponentActivity){
    val viewModel:SettingsViewModel = hiltViewModel()
    if (viewModel.openMap){
        openStoreLocationInMap(viewModel.appSession.selectedStore.name,viewModel.storeLocationLatitude.toString(), viewModel.storeLocationLongitude.toString(),componentActivity)
       viewModel.openMap = false
    }
    if (viewModel.gotoLogin){
        goToLogin(componentActivity)
    }
    OwaisTelecomTheme {
        MainComposeAUD ("الاعدادات",viewModel.stateController,{componentActivity.finish()}) {
            Column{
                SettingsList()
            }
        }
    }
}
@Composable
private fun SettingsList() {
    val viewModel:SettingsViewModel = hiltViewModel()
    val context = LocalContext.current
    val items = listOf(
        SettingItem("الملف الشخصي", R.drawable.uinfo) {
            gotoProfile(context)
                                                      },
        SettingItem("الطلبات", R.drawable.baseline_checklist_24) {
            gotoOrders(context)
                                                                 },
        SettingItem("موقع المتجر", R.drawable.settingicon) {
          viewModel.getStoreLocation()
        },
        SettingItem("معلومات التطبيق", R.drawable.settingicon) {
            gotoGeneralInfo(context)
                                                               },
        SettingItem("تسجيل الخروج", R.drawable.logouticon) {
//            logout()
            viewModel.logout {
                goToLogin(context)
            }
        },
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { item.onClick() },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    CustomImageViewUri(
                        modifier = Modifier.size(28.dp),
                        imageUrl = item.iconRes
                    )
                }
            }
        }
    }
}

