package com.owaistelecom.telecom.shared


import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.application.MyApplication
import com.owaistelecom.telecom.models.Product
import com.owaistelecom.telecom.models.ProductOption
import com.owaistelecom.telecom.models.StoreProduct1
import com.owaistelecom.telecom.models.StoreTime
import com.owaistelecom.telecom.ui.add_to_cart2.Cart2ViewModel
import com.owaistelecom.telecom.ui.add_to_cart2.CartViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.util.Locale

@Composable
fun MainCompose(
    stateController: StateController,
    read: () -> Unit,
    onSuccess: @Composable() (() -> Unit)
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (stateController.isLoadingAUD.value) {
            Dialog(onDismissRequest = { }) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
        if (stateController.isErrorAUD.value) {
            Toast.makeText(context, stateController.errorAUD.value, Toast.LENGTH_SHORT).show()
            stateController.isErrorAUD.value = false
            stateController.errorAUD.value = ""
        }
        if (stateController.isShowMessage.value) {
            Toast.makeText(context, stateController.message.value, Toast.LENGTH_SHORT).show()
        }
        if (stateController.isSuccessRead.value) {
            if (stateController.isHaveSuccessAudMessage()) {
                Toast.makeText(context, stateController.getMessage(), Toast.LENGTH_SHORT).show()
            }

            onSuccess()
        }

            if (stateController.isLoadingRead.value || stateController.isErrorRead.value )
                Box(Modifier.fillMaxSize()) {
                    if (stateController.isLoadingRead.value)
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    if (stateController.isErrorRead.value) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(text = stateController.errorRead.value)
                            Button(onClick = {
                                stateController.errorRead.value = ""
                                stateController.isErrorRead.value = false
                                read()
                            }) {
                                Text(text = "جرب مرة اخرى")
                            }
                        }
                    }
                }



    }
}

@Composable
fun MainComposeAUD(
    name:String,
    stateController: StateController,
    back:()-> Unit,
    content: @Composable() (() -> Unit)
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize().background(Color.White),
                verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CustomIcon3(
                            Icons.AutoMirrored.Default.ArrowBack, border = false,
                            modifierButton = Modifier
                                .padding(14.dp).size(25.dp),
                        ) {
                           back()
                        }
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = (name),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                }
                HorizontalDivider(Modifier.fillMaxWidth())
                if (stateController.isLoadingAUD.value) {
                    Dialog(onDismissRequest = { }) {
                        CircularProgressIndicator()
                    }
                }

        if (stateController.isShowMessage.value) {
            Toast.makeText(context, stateController.message.value, Toast.LENGTH_SHORT).show()
        }

                if (stateController.isErrorAUD.value) {
                    Toast.makeText(context, stateController.errorAUD.value, Toast.LENGTH_SHORT)
                        .show()
                    stateController.isErrorAUD.value = false
                    stateController.errorAUD.value = ""
                }
                content()
    }
}

@Composable
fun MainComposeRead(
    name:String,
    stateController: StateController,
    back:()-> Unit,
    read: () -> Unit,
    onSuccess: @Composable() (() -> Unit)
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize().background(Color.White),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                CustomIcon3(
                    Icons.AutoMirrored.Default.ArrowBack, border = false,
                    modifierButton = Modifier
                        .padding(14.dp).size(25.dp),
                ) {
                    back()
                }
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = (name),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }
        HorizontalDivider(Modifier.fillMaxWidth())



        if (stateController.isLoadingAUD.value) {
            Dialog(onDismissRequest = { }) {
                Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }
        if (stateController.isErrorAUD.value) {
            Toast.makeText(context, stateController.errorAUD.value, Toast.LENGTH_SHORT).show()
            stateController.isErrorAUD.value = false
            stateController.errorAUD.value = ""
        }
        if (stateController.isShowMessage.value) {
            Toast.makeText(context, stateController.message.value, Toast.LENGTH_SHORT).show()
        }
        if (stateController.isSuccessRead.value) {
            if (stateController.isHaveSuccessAudMessage()) {
                Toast.makeText(context, stateController.getMessage(), Toast.LENGTH_SHORT).show()
            }

            onSuccess()
        }

        if (stateController.isLoadingRead.value || stateController.isErrorRead.value )

            Box(Modifier.fillMaxSize()) {
                if (stateController.isLoadingRead.value)
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                if (stateController.isErrorRead.value) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text(text = stateController.errorRead.value)
                        Button(onClick = {
                            stateController.errorRead.value = ""
                            stateController.isErrorRead.value = false
                            read()
                        }) {
                            Text(text = "جرب مرة اخرى")
                        }
                    }
                }
            }

    }
}

fun isStoreOpen(storeTime: StoreTime): Boolean {
    if (storeTime.isOpen != 1) return false

    val now = LocalDateTime.now()
    val currentDay = convertJavaDayToCustom(now.dayOfWeek)
    if (storeTime.day != currentDay) return false

    val openParts = storeTime.openAt.split(":").map { it.toInt() }
    val closeParts = storeTime.closeAt.split(":").map { it.toInt() }

    val openTime = LocalDateTime.of(
        now.year, now.month, now.dayOfMonth,
        openParts[0], openParts[1], openParts[2]
    )

    val closeHour = closeParts[0]
    val closeDay = if (closeHour >= 24) now.plusDays(1) else now

    val closeTime = LocalDateTime.of(
        closeDay.year, closeDay.month, closeDay.dayOfMonth,
        closeHour % 24, closeParts[1], closeParts[2]
    )

    return now.isAfter(openTime) && now.isBefore(closeTime)
}

fun convertJavaDayToCustom(dayOfWeek: DayOfWeek): Int {
    return when (dayOfWeek) {
        DayOfWeek.SATURDAY -> 1
        DayOfWeek.SUNDAY -> 2
        DayOfWeek.MONDAY -> 3
        DayOfWeek.TUESDAY -> 4
        DayOfWeek.WEDNESDAY -> 5
        DayOfWeek.THURSDAY -> 6
        DayOfWeek.FRIDAY -> 7
    }
}

@Composable
fun CustomImageView1(
    imageUrl: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    isLoading:Boolean = true
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
            if (isLoading)
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
    imageUrl: Any,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit
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
        contentScale = contentScale
    )
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
        elevation =  CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
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
            )
        else Modifier
        Icon(
            modifier = modifierIcon,
            imageVector = imageVector,
            contentDescription = ""
        )
    }
}
@Composable
fun CustomIcon2(imageVector: ImageVector,
               modifierIcon: Modifier = Modifier,
               border:Boolean=false,
               onClick: () -> Unit) {
    IconButton(onClick = onClick) {
         if (border) modifierIcon
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary,
                RoundedCornerShape(
                    8.dp
                )
            )
            .clip(
                RoundedCornerShape(
                    8.dp
                )
            )
        else Modifier
        Icon(
            modifier = modifierIcon,
            imageVector = imageVector,
            contentDescription = ""
        )
    }
}

@Composable
fun ADControll(product: Product, option: ProductOption) {
    val viewModel: CartViewModel = hiltViewModel()
    val count = viewModel.getCountOptionProduct(product, option)
    
    Row(
        modifier = Modifier
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // زر الطرح
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (count > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                )
                .clickable(enabled = count > 0) {
                    playClickSound()
                    viewModel.decrement( product, option)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.decrement),
                contentDescription = "Remove",
                tint = if (count > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }

        // عرض الكمية
        Text(
            text = count.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        // زر الإضافة
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                )
                .clickable {
                    playClickSound()

                    viewModel.addProductToCart(product, option)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun ADControll2(storeProduct1: StoreProduct1) {
    val viewModel: Cart2ViewModel = hiltViewModel()
    val storeNestedSectionId = viewModel.appSession.selectedStoreNestedSection?.id
    val homeProduct= viewModel.appSession.homeProducts[storeNestedSectionId] ?: return
    val product = homeProduct.products.find { it.id == storeProduct1.productId }
    if (product != null){
        val count = viewModel.getCountOptionProduct(product, storeProduct1)

        Row(
            modifier = Modifier
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // زر الطرح
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                     MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
                    .clickable(enabled = count > 0) {
                        playClickSound()
                        viewModel.decrement( product, storeProduct1)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.decrement),
                    contentDescription = "Remove",
                    tint =  Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            // عرض الكمية
            Text(
                text = count.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // زر الإضافة
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
                    .clickable {
                        playClickSound()

                        viewModel.addProductToCart(product, storeProduct1)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }



}
// Function to play the default click sound
private fun playClickSound() {
    val audioManager = MyApplication.AppContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK)
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


@Composable
fun CustomRow(content: @Composable() (RowScope.() -> Unit)){
    Row  (Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){

        content()
    }
}

@Composable
fun CustomRow2(content: @Composable() (RowScope.() -> Unit)){
    Row  (Modifier.fillMaxWidth().padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ){
        content()
    }
}


@Composable
fun CustomIcon3(imageVector: ImageVector, modifierIcon: Modifier = Modifier, modifierButton: Modifier = Modifier, borderColor: Color = MaterialTheme.colorScheme.primary, tint: Color = LocalContentColor.current, border:Boolean=false, onClick: () -> Unit) {
    val modifier = if (border) modifierButton
        .border(
            1.dp,
            borderColor,
            CircleShape
        )
        .clip(
            CircleShape
        )
    else modifierButton
    IconButton(
        modifier = modifier,
        onClick = onClick) {
        Box {
            Icon(
                modifier = modifierIcon,
                imageVector = imageVector,
                contentDescription = "",
                tint = tint
            )
//            Text(modifier =  Modifier.align(Alignment.TopEnd) .background(MaterialTheme.colorScheme.primary, CircleShape) // خلفية دائرية للـ Badge
//            , color = Color.White, fontSize = 10.sp, text = "3")
        }

    }
}
@Composable
fun CustomCircleBox(isFilled: Boolean, color: Color, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = if (isFilled) color else Color.Transparent,
                shape = CircleShape
            )
            .border(
                width = if (isFilled) 0.dp else 1.dp,
                color = color,
                shape = CircleShape
            )
    )
}


@Composable
fun GoToCartUI() {
    val viewModel: CartViewModel = hiltViewModel()
    val context = LocalContext.current

    viewModel
    // عدد المنتجات في السلة
    val cartCount = viewModel.getCartProducts().size // <-- تأكد أنك تملك هذه الدالة في ViewModel

    if (cartCount == 0) return

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .clickable {
                val intent = Intent(context, com.owaistelecom.telecom.ui.cart_preview.CartPreviewActivity::class.java)
                context.startActivity(intent)
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 🛒 أيقونة السلة + شارة العدد
        Box(
            modifier = Modifier
                .size(28.dp),
            contentAlignment = Alignment.Center
        ) {
            // دائرة خلفية خضراء ناعمة
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFFE8F5E9), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Cart",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(18.dp)
                )
            }

            // 🔴 شارة العدد
            if (cartCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(16.dp)
                        .background(Color.Red, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cartCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 📝 نص عرض السلة
        Text(
            text = "عرض السلة",
            color = Color(0xFF2E7D32),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        // ➡️ أيقونة السهم داخل دائرة وردية ناعمة
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color(0xFFFCE4EC), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go to cart",
                tint = Color(0xFFE91E63),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun HeaderUI2(isHaveFreeDelivery: Boolean) {
    val viewModel: CartViewModel = hiltViewModel()
    val context = LocalContext.current
    val cartCount = viewModel.getCartProducts().size
    val showHeader = isHaveFreeDelivery || cartCount > 0

    AnimatedVisibility(visible = showHeader) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE8F5E9)) // أخضر فاتح جدًا
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ✅ قسم التوصيل المجاني
            if (isHaveFreeDelivery) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color.White, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Free Delivery",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "توصيل مجاني",
                        color = Color(0xFF2E7D32),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ✅ زر عرض السلة
            if (cartCount > 0) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .clickable {
                            val intent = Intent(
                                context,
                                com.owaistelecom.telecom.ui.cart_preview.CartPreviewActivity::class.java
                            )
                            context.startActivity(intent)
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 🛒 أيقونة السلة مع شارة العدد
                    Box(
                        modifier = Modifier.size(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFFE8F5E9), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        if (cartCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .size(16.dp)
                                    .background(Color.Red, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartCount.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "عرض السلة",
                        color = Color(0xFF2E7D32),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFFFCE4EC), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go to cart",
                            tint = Color(0xFFE91E63),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun HeaderUI3(context: Context,appSession: AppSession){
    val isHaveFreeDelivery = false
//        appSession.selectedStore.storeCurrencies.any { it.deliveryPrice.toDouble() == 0.0 }

    Row(
        modifier = Modifier
            .clickable {  }
            .fillMaxWidth()
            .background(Color(0xFFE8F5E9)) // أخضر فاتح جدًا (خلفية هادئة)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (isHaveFreeDelivery)
            Row {
                // أيقونة ✅ داخل دائرة بيضاء
                Box(
                    modifier = Modifier
                        .size(25.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50), // أخضر واضح
                        modifier = Modifier.size(20.dp)
                    )
                }

//                    Spacer(modifier = Modifier.width(12.dp))

                // النص: توصيل مجاني
                Text(
                    text = "توصيل مجاني",
                    color = Color(0xFF2E7D32), // أخضر غامق
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

//        GoToCartUI(context)
    }
}
