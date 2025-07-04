package com.owaistelecom.telecom.ui.add_to_cart

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.models.Ads
import com.owaistelecom.telecom.models.StoreProduct
import com.owaistelecom.telecom.shared.ADControll
import com.owaistelecom.telecom.shared.AToken
import com.owaistelecom.telecom.shared.CustomCircleBox
import com.owaistelecom.telecom.shared.CustomIcon3
import com.owaistelecom.telecom.shared.CustomImageView
import com.owaistelecom.telecom.shared.CustomImageView1
import com.owaistelecom.telecom.shared.CustomImageViewUri
import com.owaistelecom.telecom.shared.HeaderUI2
import com.owaistelecom.telecom.shared.MainComposeAUD
import com.owaistelecom.telecom.shared.ReadMoreText
import com.owaistelecom.telecom.shared.formatPrice
import com.owaistelecom.telecom.shared.isStoreOpen
import com.owaistelecom.telecom.ui.login.LoginViewModel
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import kotlinx.coroutines.launch

@Composable
fun AddToCartScreen(componentActivity: ComponentActivity,storeProduct: StoreProduct){
    val viewModel: AddToCartViewModel = hiltViewModel()
    OwaisTelecomTheme {
        MainComposeAUD(storeProduct.product.productName,viewModel.stateController,{componentActivity.finish()}) {
            val pagerState = rememberPagerState(initialPage = 0) { 2 }

            val pagerStateImage = rememberPagerState(pageCount = { storeProduct.product.images.size })
            val scope = rememberCoroutineScope()

                    HeaderUI2()
                    if (storeProduct.product.images.isNotEmpty()) {
                        if (storeProduct.product.images.isEmpty())
                            AsyncImage(
                                model = R.drawable.logo,
                                contentDescription = "",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                        else{
                            HorizontalPager(
                                pagerStateImage,
                                modifier = Modifier.background(Color.White),
                            ) { i ->
                                CustomImageViewUri(
                                    imageUrl = viewModel.getAppSession().remoteConfig.BASE_IMAGE_URL+
                                            viewModel.getAppSession().remoteConfig.SUB_FOLDER_PRODUCT+
                                            storeProduct.product.images[i].image,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(250.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }

                        LazyRow(
                            contentPadding = PaddingValues(8.dp),
                            modifier =  Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .height(25.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            items(storeProduct.product.images.size){item->
                                Log.e("dedede",item.toString())
                                IconButton(
                                    modifier = Modifier.size(15.dp),
                                    onClick = {
                                        scope.launch {
                                            pagerStateImage.animateScrollToPage(item)
                                        }
//                                        GlobalScope.launch {
//                                            withContext(Dispatchers.Main) {
//                                                pagerState.animateScrollToPage(item)
//                                            }
//                                        }
                                    }) {
                                    CustomCircleBox(pagerStateImage.currentPage == item,Color.Black,12.dp)

//                                    Icon(
////                                        modifier = modifierIcon,
//                                        imageVector = if (pagerState.currentPage == item) Icons.Default.AddCircle else Icons.Outlined.AddCircle,
//                                        contentDescription = ""
//                                    )
                                }
                            }
                        }
                    }

//                // اسم المنتج
//                Text(
//                    text = storeProduct!!.product.productName,
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp,
//                    color = Color.Black
//                )


                HorizontalDivider()
                // ✅ التبويبات
                Row(
                    Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton (onClick = { scope.launch { pagerState.animateScrollToPage(0) } }) {
                        Text(
                            "الخيارات",
                            color = if (pagerState.currentPage == 0) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    TextButton(onClick = { scope.launch { pagerState.animateScrollToPage(1) } }) {
                        Text(
                            "الوصف",
                            color = if (pagerState.currentPage == 1) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
                HorizontalDivider()

                // ✅ الصفحات (محتوى متغير حسب الصفحة)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) { page ->
                    when (page) {
                        0 -> {
                            // صفحة الخيارات
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(storeProduct.options) { option ->
                                    Row(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(option.name)
                                        Text(
                                            text = formatPrice(option.price) + " " + option.currency.name,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        ADControll(storeProduct.product, option)
                                    }
                                }
                            }
                        }

                        1 -> {
                            // صفحة الوصف
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Text(
                                        text = "الوصف:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    ReadMoreText(storeProduct.product.productDescription.toString())
                                }
                            }
                        }
                    }
                }

        }
    }
}



