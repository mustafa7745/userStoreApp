package com.owaistelecom.telecom.shared


import android.app.Activity
import android.app.Activity.VIBRATOR_SERVICE
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.activities.SingletonCart
import com.owaistelecom.telecom.application.MyApplication
import com.owaistelecom.telecom.models.Product
import com.owaistelecom.telecom.models.ProductOption
import com.owaistelecom.telecom.models.Store
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDateTime
import java.util.Locale


@Composable
fun MainCompose1(
    padding: Dp,
    stateController: StateController,
    activity: Activity,
    read: () -> Unit,
    onSuccess: @Composable() (() -> Unit)
) {
    var verticalArrangement: Arrangement.Vertical by remember { mutableStateOf(Arrangement.Center) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = padding),
//        verticalArrangement = verticalArrangement,
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (stateController.isLoadingAUD.value) {
            Dialog(onDismissRequest = { }) {
                Box (Modifier.fillMaxSize()){
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
        if (stateController.isErrorAUD.value) {
            Toast.makeText(activity, stateController.errorAUD.value, Toast.LENGTH_SHORT).show()
            stateController.isErrorAUD.value = false
            stateController.errorAUD.value = ""
        }
        if (stateController.isShowMessage.value) {
            Toast.makeText(activity, stateController.message.value, Toast.LENGTH_SHORT).show()
        }
        if (stateController.isSuccessRead.value) {
            verticalArrangement = Arrangement.Top
            if (stateController.isHaveSuccessAudMessage()){
                Toast.makeText(activity, stateController.getMessage(), Toast.LENGTH_SHORT).show()
            }

            Column(Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                onSuccess()
            }

        }
        if (stateController.isLoadingRead.value) {
            CircularProgressIndicator(Modifier.align(Alignment.Center))


//            LoadingCompose()
        }
        if (stateController.isErrorRead.value) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =  Modifier.align(Alignment.Center)) {
                Text(text = stateController.errorRead.value)
                Button(onClick = {
                    stateController.errorRead.value = ""
                    stateController.isErrorRead.value = false
                    stateController.isLoadingRead.value = true
                    read()
                }) {
                    Text(text = "جرب مرة اخرى")
                }
            }


        }
    }
}

@Composable
fun MainCompose2(
    padding: Dp,
    stateController: StateController,
    activity: Activity,
    content: @Composable() (() -> Unit)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = padding).background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (stateController.isLoadingAUD.value) {
            Dialog(onDismissRequest = { }) {
                CircularProgressIndicator()
            }
        }
//        if (stateController.i())
        if (stateController.isErrorAUD.value) {
            Toast.makeText(activity, stateController.errorAUD.value, Toast.LENGTH_SHORT).show()
            stateController.isErrorAUD.value = false
            stateController.errorAUD.value = ""
        }
        content()
    }
}

@Composable
fun CustomImageView1(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    // Create ImageLoader with OkHttpClient
//    val imageLoader = ImageLoader.Builder(context)
//        .okHttpClient(okHttpClient)
//        .build()

    // Display the image using AsyncImage
    SubcomposeAsyncImage(
        error = {
            Column(
                Modifier,
//                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = R.drawable.logo,
                    contentDescription = null,
                    contentScale = contentScale

                )
            }

        },
        loading = {
            CircularProgressIndicator()
        },
        model = imageUrl,
//        imageLoader = imageLoader,
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize(),
        contentScale = contentScale
    )
}


@Composable
fun CustomImageView(
    context: Context,
    imageUrl: String,
    okHttpClient: OkHttpClient,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    // Create ImageLoader with OkHttpClient
//    val imageLoader = ImageLoader.Builder(context)
//        .okHttpClient(okHttpClient)
//        .build()

    // Display the image using AsyncImage
    SubcomposeAsyncImage(
        error = {
            Column(
                Modifier,
//                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = R.drawable.logo,
                    contentDescription = null,
                    contentScale = contentScale

                )
            }

        },
        loading = {
            CircularProgressIndicator()
        },
        model = imageUrl,
//        imageLoader = imageLoader,
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize(),
        contentScale = contentScale
    )
}
@Composable
fun CustomImageViewUri(
    context: Context,
    imageUrl: Uri,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    // Create ImageLoader with OkHttpClient
//    val imageLoader = ImageLoader.Builder(context)
//        .okHttpClient(okHttpClient)
//        .build()

    // Display the image using AsyncImage
    SubcomposeAsyncImage(
        error = {
            Column(
                Modifier,
//                Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = R.drawable.logo,
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }

        },
        loading = {
            CircularProgressIndicator()
        },
        model = imageUrl,
//        imageLoader = imageLoader,
        contentDescription = contentDescription,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Fit
    )
}

fun getRemoteConfig(): FirebaseRemoteConfig {
    val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
    val configSettings = remoteConfigSettings {
        minimumFetchIntervalInSeconds = 3600
    }
    remoteConfig.setConfigSettingsAsync(configSettings)
    return remoteConfig;
}


fun builderForm(token:String): MultipartBody.Builder {
    val appInfoMethod = AppInfoMethod()
    return MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("sha", appInfoMethod.getAppSha())
        .addFormDataPart("appToken", token)
        .addFormDataPart("packageName", appInfoMethod.getAppPackageName())
        .addFormDataPart("deviceId", appInfoMethod.getDeviceId().toString())
        .addFormDataPart("model", Build.MODEL)
        .addFormDataPart("version", Build.VERSION.RELEASE)
}
fun builderForm2(): MultipartBody.Builder {
    val appInfoMethod = AppInfoMethod()
    return MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("sha", appInfoMethod.getAppSha())
        .addFormDataPart("packageName", appInfoMethod.getAppPackageName())
        .addFormDataPart("deviceId", appInfoMethod.getDeviceId().toString())
        .addFormDataPart("model", Build.MODEL)
        .addFormDataPart("version", Build.VERSION.RELEASE)
}

fun builderForm3(): MultipartBody.Builder {
    val appInfoMethod = AppInfoMethod()
    return MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("accessToken", AToken().getAccessToken().token)
        .addFormDataPart("deviceId", appInfoMethod.getDeviceId().toString())
        .addFormDataPart("sha", appInfoMethod.getAppSha())
        .addFormDataPart("packageName", appInfoMethod.getAppPackageName())
}

fun getCurrentDate(): LocalDateTime {
    return LocalDateTime.now()
}

@Composable
fun CustomCard(modifierCard: Modifier = Modifier
.fillMaxWidth().padding(8.dp).border(1.dp, Color.Gray,
RoundedCornerShape(12.dp)),

               modifierBox: Modifier ,
               content: @Composable() (() -> Unit)){
    Card(
        colors  = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier =  modifierCard
        ){
        Box (
            modifier = modifierBox

        ){
            Column {
                content()
            }

        }
    }
}
@Composable
fun CustomIcon(imageVector: ImageVector,
               modifierIcon: Modifier = Modifier,
               border:Boolean=false,
               onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        val modifier = if (border) Modifier
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(
                    16.dp
                )
            )
            .clip(
                RoundedCornerShape(
                    16.dp
                )
            )else Modifier
        Icon(

            modifier = modifierIcon,
            imageVector = imageVector,
            contentDescription = ""
        )
    }
}

@Composable
private fun IconAdd( onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            modifier =
            Modifier
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(
                        16.dp
                    )
                )
                .clip(
                    RoundedCornerShape(
                        16.dp
                    )
                ),
            imageVector = Icons.Outlined.Add,
            contentDescription = ""
        )
    }
}
@Composable
private fun IconMinus(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            modifier =
            Modifier
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(
                        16.dp
                    )
                )
                .clip(
                    RoundedCornerShape(
                        16.dp
                    )
                ),
            painter = painterResource(
                R.drawable.decrement
            ),
            contentDescription = ""
        )
    }
}

@Composable
fun IconRemove( onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            modifier =
            Modifier
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(
                        16.dp
                    )
                )
                .clip(
                    RoundedCornerShape(
                        16.dp
                    )
                ),
            imageVector = Icons.Outlined.Delete,
            contentDescription = ""
        )
    }
}
@Composable
fun ADControll(product: Product, option: ProductOption) {
    val vibrator = MyApplication.AppContext.getSystemService(VIBRATOR_SERVICE) as Vibrator
    Row(
        modifier = Modifier
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(
                    16.dp
                )
            )
            .clip(
                RoundedCornerShape(
                    16.dp
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconAdd {
            // For devices running API 26 and above, use the VibrationEffect API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                // For older devices, use the simple vibrate method
                vibrator.vibrate(50) // Vibrate for 100 milliseconds
            }
            SingletonCart.addProductToCart(SingletonStores.selectedStore,product, option)
        }

        Text(SingletonCart.countOptionProduct(SingletonStores.selectedStore,product, option).toString())

        IconMinus {

            // For devices running API 26 and above, use the VibrationEffect API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                // For older devices, use the simple vibrate method
                vibrator.vibrate(50) // Vibrate for 100 milliseconds
            }
            SingletonCart.decrement(SingletonStores.selectedStore, product, option)
        }

        if (SingletonCart.ifOptionInCart(SingletonStores.selectedStore,product, option))
        IconRemove {
            SingletonCart.removeProductOptionFromCart(SingletonStores.selectedStore,product, option)
        }
    }
}
@Composable
fun IconDelete(ids: List<Int> , onClick: () -> Unit) {

    if (ids.isNotEmpty()) {
        IconButton(onClick = onClick) {
            Icon(
                modifier =
                Modifier
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(
                            16.dp
                        )
                    )
                    .clip(
                        RoundedCornerShape(
                            16.dp
                        )
                    ),
                imageVector = Icons.Outlined.Delete,
                contentDescription = ""
            )
        }
    }
}

@Composable
fun ReadMoreText(productDescription: String) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            text = if (isExpanded) productDescription else productDescription.take(100) + "...", // Truncate if not expanded
            fontSize = 10.sp,
            color = Color.Black
        )

        if (productDescription.length > 40){
            Button(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Text(text = if (isExpanded) "اقل" else "عرض المزيد")
            }
        }else{
            isExpanded = true
        }

    }
}

object SingletonStores{
    lateinit var selectedStore: Store
}

fun formatNumber(number: Int): String {
    return when {
        number >= 1_000_000 -> "${number / 1_000_000}M"  // For millions
        number >= 1_000 -> "${number / 1_000}K"            // For thousands
        else -> number.toString()                           // For smaller numbers
    }
}

object SingletonRemoteConfig{
    lateinit var remoteConfig: VarRemoteConfig
}

fun formatPrice(price: String): String {
    val doublePrice = price.toDouble()
    val symbols = DecimalFormatSymbols(Locale.ENGLISH)
    val decimalFormat = DecimalFormat("#.##", symbols) // Format to two decimal places
    return decimalFormat.format(doublePrice)
}
@Composable
 fun MyTextField(
    hinty:String = "ابحث هنا",
    height:Int = 140,
    onChange: (String) -> Unit) {
    AndroidView(factory = { context ->
        EditText(context).apply {
            hint = hinty
            background = null
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
//            inputType = InputType.TYPE_TEXT_FLAG_MULTI_LINE


        }
    },
        update = { editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    onChange(p0.toString())
//                        ttt = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            })

        }, modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(
                    10.dp
                )
            )
            .clip(
                RoundedCornerShape(
                    10.dp
                )
            )
    )
}