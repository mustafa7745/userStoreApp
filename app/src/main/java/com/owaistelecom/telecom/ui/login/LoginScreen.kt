package com.owaistelecom.telecom.ui.login

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.shared.MainCompose
import com.owaistelecom.telecom.ui.general_info.GeneralInfoActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(componentActivity: ComponentActivity) {
    val viewModel: LoginViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    if (viewModel.successLogin){
        goToDashboard(componentActivity)
    }
    LaunchedEffect(null) {
//        viewModel.getLoginConfiguration()
    }
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                viewModel.signInWithGoogle(idToken) {
                    goToDashboard(componentActivity)
                }
            }
        } catch (e: Exception) {
            viewModel.stateController.errorStateAUD("Google Sign-In Failed: ${e.message}")
        }
    }

    fun signInWithGoogleClassic() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("635175556369-ltr2c9r3caj7805kgi4vo8l34uukok58.apps.googleusercontent.com")
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(componentActivity, gso)
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    fun signInWithGoogle() {
        val credentialManager = CredentialManager.create(componentActivity)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("635175556369-ltr2c9r3caj7805kgi4vo8l34uukok58.apps.googleusercontent.com") // استبدل بـ Web Client ID الخاص بك
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = credentialManager.getCredential(componentActivity, request)
                val credential = result.credential.data
                val idToken = credential.getString("com.google.android.libraries.identity.googleid.BUNDLE_KEY_ID_TOKEN")
                if (idToken != null) {
                    viewModel.signInWithGoogle(idToken) {
                        goToDashboard(componentActivity)
                    }
                }
            } catch (e: GetCredentialException) {
                viewModel.stateController.errorStateAUD("CredentialManager failed: ${e.message}")
                Log.e("SignIn", "CredentialManager Sign-in failed", e)
            }
        }
    }

    MainCompose( viewModel.stateController,{
//        viewModel.getLoginConfiguration()
    }) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    IconButton(onClick = {
                        val intent = Intent(componentActivity, GeneralInfoActivity::class.java)
                        componentActivity.startActivity(intent)
                    }) {
                        Icon(Icons.Outlined.Info, contentDescription = null)
                    }
                }
            }
            item {
                Column(Modifier.fillMaxWidth(), horizontalAlignment =  Alignment.CenterHorizontally) {
                    Row(horizontalArrangement =  Arrangement.SpaceBetween, verticalAlignment =  Alignment.CenterVertically) {
                        Image(
                            painter = rememberAsyncImagePainter(R.mipmap.ic_launcher_round),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp).padding(bottom = 16.dp)
                        )
                        Text("تسجيل الدخول", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))
                    }
                    CompositionLocalProvider(LocalTextStyle provides TextStyle(textDirection = TextDirection.Ltr)) {
                        OutlinedTextField(

                            value = viewModel.phone,
                            onValueChange = { viewModel.onPhoneChange(it) },
                            label = { Text("رقم الهاتف") },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { viewModel.toggleCountrySelector(true) }) {
                                    Text("+${viewModel.selectedCountryCode.code}")
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }

                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        label = { Text("الرقم السري") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp,start = 16.dp, end = 16.dp),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.login()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(5.dp)
                    ) {
                        Text("دخول")
                    }

                    Image(
                        painter = painterResource(id = R.drawable.android_light_sq),
                        contentDescription = null,
                        modifier = Modifier.padding(top = 30.dp).clickable {
                            viewModel.stateController.startAud()
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                signInWithGoogle()
                            } else {
                                signInWithGoogleClassic()
                            }
                        }
                    )

                    Spacer(Modifier.height(20.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
                        Text("ليس لدي حساب", fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text("اشتراك", color = Color.Blue, fontSize = 14.sp, modifier = Modifier.clickable { intentFunWhatsapp(componentActivity,"اشتراك")})
                    }

                    Text("نسيت كلمة المرور؟", color = Color.Red, fontSize = 10.sp, modifier = Modifier.padding(20.dp).clickable { intentFunWhatsapp(componentActivity,"نسيت كلمة المرور")})

                    Spacer(Modifier.height(50.dp))

                    Column(Modifier.fillMaxWidth(), horizontalAlignment =  Alignment.CenterHorizontally) {
                        Row { Text("من خلال تسجيل الدخول او الاشتراك فانك توافق على ", fontSize = 9.sp) }
                        Row(Modifier.clickable {}) {
                            Text("سياسة الاستخدام", color = Color.Blue, fontSize = 9.sp)
                            Text(" و ", fontSize = 9.sp)
                            Text("شروط الخدمة ", color = Color.Blue, fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }

    if (viewModel.isShowSelectCountryCode){ DialogCountryCodes()}
}
@Composable
private fun DialogCountryCodes() {
    val viewModel: LoginViewModel = hiltViewModel()
    Dialog(onDismissRequest = { viewModel.toggleCountrySelector(false) }) {

        LazyColumn(
            Modifier
                .fillMaxSize()
                .selectableGroup()
                .padding(16.dp)
                .background(Color.White)) {
            itemsIndexed(viewModel.countryList) { index, item ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp)
                        .selectable(
                            selected = (item == viewModel.selectedCountryCode),
                            onClick = {
                                viewModel.setSelectedCountry(item)
                                viewModel.toggleCountrySelector(false)
                            },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                )
                {
                    Row(
                        Modifier.height(56.dp)
                           .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        RadioButton(selected = (item == viewModel.selectedCountryCode), onClick = null)
                        Text(text = item.name["ar"].toString(),style = MaterialTheme. typography. bodyLarge,modifier = Modifier. padding(start = 16.dp))
                    }
                    Text(text = item.code + "+",style = MaterialTheme. typography. bodyLarge,modifier = Modifier. padding(start = 16.dp))
                }
                HorizontalDivider()
            }
        }
    }
}
