package com.owaistelecom.telecom.ui.inside_store

import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.models.Ads
import com.owaistelecom.telecom.shared.CustomImageView
import com.owaistelecom.telecom.shared.CustomImageView1
import com.owaistelecom.telecom.shared.CustomImageViewUri
import com.owaistelecom.telecom.shared.HeaderUI2
import com.owaistelecom.telecom.shared.HeaderUI3
import com.owaistelecom.telecom.shared.MainCompose
import com.owaistelecom.telecom.shared.formatPrice
import com.owaistelecom.telecom.shared.isStoreOpen
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InsideStoreScreen(componentActivity: ComponentActivity){
    val viewModel: InsideStoreViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    OwaisTelecomTheme {
        if (viewModel.shouldExit){
            gotoLogin(componentActivity)
        }
        LaunchedEffect(3) {
            viewModel.initApp()
        }
        MainCompose( viewModel.stateController, {

            coroutineScope.launch {
                viewModel.initApp()
            }
           }) {
            LazyColumn(
                modifier = Modifier.safeDrawingPadding()
            ) {
                Header(viewModel, componentActivity)
                Ads(viewModel)

                stickyHeader {
                    Column (Modifier.fillMaxWidth().background(Color.White)) {
                        Sections(viewModel)
                    }
                }
                ////
                item {
                    Products(viewModel)
                }
                Youtube(viewModel)
            }
        }
    }
}

@Composable
private fun Products(viewModel: InsideStoreViewModel) {
    val context = LocalContext.current

    if (viewModel.stateControllerProducts.isLoadingRead.value)
        LinearProgressIndicator(Modifier.fillMaxWidth())
//    viewModel.productViews.filter { it.id == 1 }.forEach { productView: ProductView ->
//        Text(
//            text = productView.name,
//            fontSize = 14.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
//            color = MaterialTheme.colorScheme.onSurface
//        )
//
//        LazyRow(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(220.dp), // زيادة الارتفاع قليلاً
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            contentPadding = PaddingValues(horizontal = 16.dp)
//        ) {
//            itemsIndexed(productView.products) { index, product ->
//
//                Card(
//                    modifier = Modifier
//                        .width(160.dp)
//                        .clickable {
//                            goToAddToCart(context, product)
//                        },
//                    shape = RoundedCornerShape(16.dp),
//                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
//                ) {
//                    Column(
//                        modifier = Modifier.fillMaxSize()
//                    ) {
//
//                        // صورة المنتج
//                        val imageBackground =
//                            viewModel.imageBackgroundColors[index % viewModel.imageBackgroundColors.size]
//
//                        Box(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(140.dp)
//                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
//                                .background(imageBackground), // ← هنا يتغير اللون حسب العنصر
//                            contentAlignment = Alignment.Center
//                        ) {
//                            CustomImageView(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .padding(8.dp)
//                                    .clip(RoundedCornerShape(12.dp)),
//                                contentScale = ContentScale.Fit,
//                                imageUrl =
//                                if (product.product.images.isNotEmpty())
//                                    viewModel.getAppSession().remoteConfig.BASE_IMAGE_URL +
//                                            viewModel.getAppSession().remoteConfig.SUB_FOLDER_PRODUCT +
//                                            product.product.images.first().image
//                                else R.drawable.logo.toString(),
//                            )
//                        }
//
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        // اسم المنتج
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 12.dp),
//                            verticalArrangement = Arrangement.Center
//                        ) {
//                            Text(
//                                text = product.product.productName,
//                                fontSize = 14.sp,
//                                fontWeight = FontWeight.SemiBold,
//                                color = MaterialTheme.colorScheme.onSurface,
//                                maxLines = 2,
//                                overflow = TextOverflow.Ellipsis
//                            )
//
//                            Spacer(modifier = Modifier.height(6.dp))
//
//                            // يمكنك وضع السعر أو زر
//
//                            if (product.options.isNotEmpty())
//                                Box(
//                                    modifier = Modifier
//                                        .background(
//                                            color = Color(0xFFE53935), // أخضر أو أحمر
//                                            shape = RoundedCornerShape(8.dp)
//                                        )
//                                        .padding(horizontal = 8.dp, vertical = 4.dp)
//                                ) {
//                                    Text(
//                                        text = if (product.options.size > 1) "متعدد الخيارات" else formatPrice(
//                                            product.options.first().price.toString()
//                                        ) + " " + product.options.first().currency.name,
//                                        color = Color.White,
//                                        fontSize = 10.sp,
//                                        fontWeight = FontWeight.Bold
//                                    )
//                                }
//                        }
//                    }
//                }
//            }
//        }
//    }
    val storeProductViews= viewModel.appSession.home.storeProductViews.filter { it.productViewId == 1 }
    val storeNestedSectionId = viewModel.appSession.selectedStoreNestedSection?.id
    val homeProduct= viewModel.appSession.homeProducts[storeNestedSectionId]

    if (homeProduct != null){
        ///
        storeProductViews.forEach { storeProductView ->

            Text(
                text = storeProductView.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp), // زيادة الارتفاع قليلاً
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                itemsIndexed(homeProduct.storeProducts.filter { it.storeProductViewId == storeProductView.storeProductViewId }.distinctBy { it.productId }) { index, storeProduct ->

                    val product = homeProduct.products.first { it.id == storeProduct.productId }
                    val currencyName = viewModel.appSession.home.storeCurrencies
                        .firstOrNull { it.currencyId == storeProduct.currencyId }
                        ?.currencyName ?: ""

                    Card(
                        modifier = Modifier
                            .width(160.dp)
                            .clickable {
//                                goToProductOptions(storeProduct.productId,storeProductView.storeProductViewId)
                                goToAddToCart(context, product)
                            },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {

                            // صورة المنتج
//                                                val imageBackground =
//                                                    viewModel.imageBackgroundColors[index % viewModel.imageBackgroundColors.size]
                            val imageBackground = viewModel.imageBackgroundColors.random(Random)


                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                    .background(imageBackground), // ← هنا يتغير اللون حسب العنصر
//                                ,
                                contentAlignment = Alignment.Center
                            ) {
                                val images = homeProduct.productsImages.filter { it.productId == storeProduct.productId }
                                CustomImageView(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Fit,
                                    imageUrl =
                                    if (images.isNotEmpty())
                                        viewModel.appSession.remoteConfig.BASE_IMAGE_URL +
                                                viewModel.appSession.remoteConfig.SUB_FOLDER_PRODUCT +
                                                images.first().image
                                    else R.drawable.logo.toString(),
                                )
                            }

//                            Spacer(modifier = Modifier.height(8.dp))

                            // اسم المنتج

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .padding(6.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = product.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

//                                Spacer(modifier = Modifier.height(6.dp))
                                val storeProduct = homeProduct.storeProducts.firstOrNull { it.productId == product.id }
                                if (storeProduct != null){
//                                    if (storeProduct.name.isNotEmpty())
//                                    Text(
//                                        text = storeProduct.name,
//                                        fontSize = 12.sp,
//                                        fontWeight = FontWeight.ExtraBold,
//                                        color = MaterialTheme.colorScheme.onSurface,
//                                        maxLines = 1,
//                                        overflow = TextOverflow.Ellipsis
//                                    )

                                    Text(
                                        text = "${formatPrice(storeProduct.price.toString())} $currencyName",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 12.sp
                                    )
                                }

                                // يمكنك وضع السعر أو زر

//                                                    if (storeProduct.options.isNotEmpty())
//                                                        Box(
//                                                            modifier = Modifier
//                                                                .background(
//                                                                    color = Color(0xFFE53935), // أخضر أو أحمر
//                                                                    shape = RoundedCornerShape(8.dp)
//                                                                )
//                                                                .padding(horizontal = 8.dp, vertical = 4.dp)
//                                                        ) {
//                                                            Text(
//                                                                text = if (storeProduct.options.size > 1) "متعدد الخيارات" else formatPrice(
//                                                                    storeProduct.options.first().price.toString()
//                                                                ) + " " + storeProduct.options.first().currency.name,
//                                                                color = Color.White,
//                                                                fontSize = 10.sp,
//                                                                fontWeight = FontWeight.Bold
//                                                            )
//                                                        }
                            }
                        }
                    }
                }
            }


        }
        ///
        val storeProductViews2 = viewModel.appSession.home.storeProductViews.filter { it.productViewId == 2 }

        storeProductViews2.forEach { storeProductView ->
            Text(
                text = storeProductView.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                color = MaterialTheme.colorScheme.onSurface
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                homeProduct.storeProducts.filter { it.storeProductViewId == storeProductView.storeProductViewId }.distinctBy { it.productId }.forEachIndexed { index, storeProduct ->
                    val product = homeProduct.products.first { it.id == storeProduct.productId }
                    val imageBackground = viewModel.imageBackgroundColors.random(Random)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                goToAddToCart(context, product )

//                                                    goToAddToCart(context, storeProduct)
                            },
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = imageBackground // الخلفية المخصصة
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val images = homeProduct.productsImages.filter { it.productId == storeProduct.productId }
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            ) {
                                if (images.isNotEmpty()) {
                                    CustomImageView(
                                        modifier = Modifier.fillMaxSize(),
                                        imageUrl = viewModel.appSession.remoteConfig.BASE_IMAGE_URL +
                                                viewModel.appSession.remoteConfig.SUB_FOLDER_PRODUCT +
                                                images.first().image,
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            val product = homeProduct.products.first { it.id == storeProduct.productId }
                            Column(
                                modifier = Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = product.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
//                                                    if (storeProduct.options.isNotEmpty())
//                                                        Box(
//                                                            modifier = Modifier
//                                                                .background(
//                                                                    color = Color(0xFF3F51B5), // أخضر أو أحمر
//                                                                    shape = RoundedCornerShape(8.dp)
//                                                                )
//                                                                .padding(horizontal = 8.dp, vertical = 4.dp)
//                                                        ) {
//                                                            Text(
//                                                                text = if (storeProduct.options.size > 1) "متعدد الخيارات" else formatPrice(
//                                                                    storeProduct.options.first().price
//                                                                ) + " " + storeProduct.options.first().currency.name,
//                                                                color = Color.White,
//                                                                fontSize = 10.sp,
//                                                                fontWeight = FontWeight.Bold
//                                                            )
//                                                        }
                            }
                        }
                    }
                }
            }


        }
    }

//    viewModel.productViews.filter { it.id == 2 }.forEach { productView ->
//        if (productView.products.isNotEmpty()) {
//            Text(
//                text = productView.name,
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
//                color = MaterialTheme.colorScheme.onSurface
//            )
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 16.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp)
//            ) {
//                productView.products.forEachIndexed { index, product ->
//                    val imageBackground =
//                        viewModel.imageBackgroundColors[index % viewModel.imageBackgroundColors.size]
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .clickable {
////                                viewModel.selectProduct()
////                                                    goToAddToCart(product)
////                                goToAddToCart(context)
////                                viewModel.selectProduct(product,)
//                                goToAddToCart(context, product)
//                            },
//                        shape = RoundedCornerShape(16.dp),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
//                        colors = CardDefaults.cardColors(
//                            containerColor = imageBackground // الخلفية المخصصة
//                        )
//                    ) {
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(12.dp),
//                            verticalAlignment = Alignment.CenterVertically
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .size(80.dp)
////                                                    .background(MaterialTheme.colorScheme.surfaceVariant)
//                                    .clip(RoundedCornerShape(12.dp))
//                            ) {
//                                if (product.product.images.firstOrNull() != null) {
//                                    CustomImageView(
//                                        modifier = Modifier.fillMaxSize(),
//                                        imageUrl = viewModel.appSession.remoteConfig.BASE_IMAGE_URL +
//                                                viewModel.appSession.remoteConfig.SUB_FOLDER_PRODUCT +
//                                                product.product.images.first().image,
//                                    )
//                                }
//                            }
//                            Spacer(modifier = Modifier.width(12.dp))
//                            Column(
//                                modifier = Modifier.fillMaxHeight(),
//                                verticalArrangement = Arrangement.SpaceBetween
//                            ) {
//                                Text(
//                                    text = product.product.productName,
//                                    fontSize = 16.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    color = MaterialTheme.colorScheme.onSurface,
//                                    maxLines = 2,
//                                    overflow = TextOverflow.Ellipsis
//                                )
//                                if (product.options.isNotEmpty())
//                                    Box(
//                                        modifier = Modifier
//                                            .background(
//                                                color = Color(0xFF3F51B5), // أخضر أو أحمر
//                                                shape = RoundedCornerShape(8.dp)
//                                            )
//                                            .padding(horizontal = 8.dp, vertical = 4.dp)
//                                    ) {
//                                        Text(
//                                            text = if (product.options.size > 1) "متعدد الخيارات" else formatPrice(
//                                                product.options.first().price
//                                            ) + " " + product.options.first().currency.name,
//                                            color = Color.White,
//                                            fontSize = 10.sp,
//                                            fontWeight = FontWeight.Bold
//                                        )
//                                    }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//    }
}

            @Composable
            private fun Sections(viewModel: InsideStoreViewModel) {
                val scope = rememberCoroutineScope()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    DropDownDemo()
                    if (viewModel.isLoadingLinear) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Loading indicator مع تصميم جديد


                // الأقسام الرئيسية مع تصميم جديد
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    itemsIndexed(viewModel.home.storeSections.filter { it.storeCategoryId == viewModel.selectedCategory!!.id }) { index, item ->
                        val isSelected = item.sectionId == viewModel.selectedSection!!.sectionId
                        Card(
                            modifier = Modifier
                                .height(30.dp)
                                .clickable(enabled = !viewModel.isLoadingLinear) {
                                    if (!viewModel.isLoadingLinear) {
                                        if (viewModel.home.storeNestedSections
                                                .filter { it.storeSectionId == item.id }
                                                .isEmpty()
                                        ) {
                                            viewModel.stateController.showMessage("لاتوجد اقسام داخلية لهذا القسم")
                                        } else {
                                            viewModel.isLoadingLinear = true
                                            viewModel.selectedSection = item
                                            viewModel.appSession.selectedStoreNestedSection =
                                                viewModel.home.storeNestedSections.first { it.storeSectionId == viewModel.selectedSection!!.id }
                                            scope.launch {
                                                viewModel.readProducts()
                                            }

                                        }
                                    }
                                },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    Color(0xFFE91E63)
                                else
                                    MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isSelected) 4.dp else 1.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.sectionName,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                // الأقسام الفرعية مع تصميم جديد
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    itemsIndexed(viewModel.home.storeNestedSections.filter { it.storeSectionId == viewModel.selectedSection!!.id }) { index, item ->
                        val isSelected =
                            item.nestedSectionId == viewModel.appSession.selectedStoreNestedSection!!.nestedSectionId
                        Card(
                            modifier = Modifier
                                .height(30.dp)
                                .clickable(enabled = !viewModel.isLoadingLinear) {
                                    viewModel.isLoadingLinear = true
                                    viewModel.appSession.selectedStoreNestedSection = item
                                    scope.launch {
                                        viewModel.readProducts()
                                    }
                                },
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = if (isSelected) 4.dp else 1.dp
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.nestedSectionName,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.onPrimary
                                    else
                                        MaterialTheme.colorScheme.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            private fun LazyListScope.Youtube(viewModel: InsideStoreViewModel) {
                if (viewModel.home.videoData.filter { it.isReels == 0 }.isNotEmpty())
                    item {
                        HorizontalDivider(Modifier.padding(8.dp))
                        Text("منتجات الريلز", Modifier.padding(8.dp))

                        LazyRow(
                            Modifier
                                .height(250.dp)
                                .fillMaxWidth()
                        ) {

                            itemsIndexed(viewModel.home.videoData.filter { it.isReels == 1 }) { index, item ->
                                Card(
                                    Modifier
                                        .width(150.dp)
                                        .padding(8.dp)
                                        .fillParentMaxHeight()
                                        .clickable {
//                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
//                                startActivity(intent)
                                        }) {
                                    CustomImageView1(
                                        item.image,
                                        modifier = Modifier.fillParentMaxSize(),
                                        contentScale = ContentScale.Inside,
                                        isLoading = false
                                    )
                                }
                            }
                        }
                        HorizontalDivider(Modifier.padding(8.dp))
                        Text("اخر المنشورات", Modifier.padding(8.dp))
                    }

                //
                if (viewModel.home!!.videoData.filter { it.isReels == 1 }.isNotEmpty())
                    itemsIndexed(viewModel.home.videoData.filter { it.isReels == 0 }) { index, item ->

                        Card(
                            Modifier
                                .height(250.dp)
                                .padding(8.dp)
                                .fillMaxWidth()
                                .clickable {
//                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url))
//                        startActivity(intent)
                                }) {
                            CustomImageView1(
                                item.image,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.FillHeight,
                                isLoading = false
                            )
                        }
                    }
            }

            private fun LazyListScope.Ads(viewModel: InsideStoreViewModel) {
                item {
                    AdsCarousel(viewModel.home.ads) { ad ->
//                                // Handle click — maybe navigate to store or product
//                                if (ad.productId != null) {
//                                    // Navigate to product
//                                } else if (ad.storeId != null) {
//                                    // Navigate to store
//                                }
                    }
                }
            }

            @OptIn(ExperimentalFoundationApi::class)
            private fun LazyListScope.Header(
                viewModel: InsideStoreViewModel,
                componentActivity: ComponentActivity
            ) {
                stickyHeader {
                    val context = LocalContext.current
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .background(Color.White),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CustomImageViewUri(
                            modifier = Modifier
                                .height(50.dp)
                                .width(180.dp),
                            imageUrl = R.drawable.rectangale_logo
                        )
                        Column {
                            viewModel.home.storeTime.let { storeTime ->
                                val isOpen = isStoreOpen(storeTime)
                                viewModel.appSession.isOpen = isOpen

                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (isOpen) Color(0xFF4CAF50) else Color(
                                                0xFFF44336
                                            ), // أخضر أو أحمر
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isOpen) "المتجر مفتوح" else "المتجر مغلق",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Row {
//                            IconButton(onClick = {
//                                gotoSearch(context)
//                            }) {
//                                Icon(
//                                    imageVector = Icons.Default.Search,
//                                    contentDescription = null,
//                                    //                                     tint = Color.White,
//                                    modifier = Modifier.size(24.dp)
//                                )
//                            }
                            IconButton(onClick = {
                                gotoSettings(context)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    //                                     tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                        }


                        //                             Column(Modifier.width(30.dp).clickable { gotoSettings() }) {
                        //                                    CustomImageViewUri(
                        //                                        modifier = Modifier.size(30.dp),
                        //                                        imageUrl = R.drawable.settingicon
                        //                                    )
                        //                                    HorizontalDivider(Modifier.padding(8.dp))
                        //                                    Text("الاعدادات", fontSize = 8.sp)
                        //                                }

                    }
                    HorizontalDivider()
                    val isFree = viewModel.appSession.home.storeCurrencies.find { it.isSelected == 1 }
                    HeaderUI2(if(isFree != null)isFree.deliveryPrice.toDouble() == 0.0 else false)
                }
            }

            @Composable
            fun DropDownDemo() {
                val scope = rememberCoroutineScope()
                val viewModel: InsideStoreViewModel = hiltViewModel()

                val isDropDownExpanded = remember {
                    mutableStateOf(false)
                }

//    val itemPosition = remember {
//        mutableStateOf(0)
//    }
//
//    val usernames = listOf("Alexander", "Isabella", "Benjamin", "Sophia", "Christopher")

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Box(Modifier.background(Color.White)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .clickable {
                                    isDropDownExpanded.value = true
                                }
                        ) {
                            Text(text = viewModel.selectedCategory!!.categoryName)
                            Text(text = "عرض الكل")
                        }
                        DropdownMenu(
                            expanded = isDropDownExpanded.value,
                            onDismissRequest = {
                                isDropDownExpanded.value = false
                            }) {
                            viewModel.home!!.storeCategories.forEachIndexed { index, username ->
                                DropdownMenuItem(text = {
                                    Text(text = username.categoryName)
                                },
                                    onClick = {

                                        val s =
                                            viewModel.home!!.storeSections.filter { it.storeCategoryId == username.id }
                                        if (s.isEmpty()) {
                                            viewModel.stateController.showMessage("لاتوجد اقسام لهذه الفئة")
                                        } else {
                                            if (viewModel.home!!.storeNestedSections.filter { it.storeSectionId == s.first().id }
                                                    .isEmpty()) {
                                                viewModel.stateController.showMessage("هناك اقسام داخلية فارغة")
                                            } else {
                                                viewModel.selectedCategory =
                                                    viewModel.home!!.storeCategories.first { it.categoryId == username.categoryId }
                                                viewModel.selectedSection =
                                                    viewModel.home!!.storeSections.first { it.storeCategoryId == viewModel.selectedCategory!!.id }
                                                viewModel.appSession.selectedStoreNestedSection =
                                                    viewModel.home!!.storeNestedSections.first { it.storeSectionId == viewModel.selectedSection!!.id }
                                                isDropDownExpanded.value = false
                                                viewModel.productViews = emptyList()
                                                scope.launch {
                                                    viewModel.readProducts()
                                                }
                                            }
                                        }
                                    })
                            }
                        }
                    }

                }
            }
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AdsCarousel(ads: List<Ads>, onAdClick: (Ads) -> Unit) {
//    val carouselState = rememberCarouselState { ads.size }
//
//    HorizontalMultiBrowseCarousel(
//        state = carouselState,
//        preferredItemWidth = 300.dp,
//        itemSpacing = 12.dp,
//        modifier = Modifier
//            .padding(8.dp)
//            .height(220.dp)
//    ) { page ->
//        val ad = home!!.ads[page]
//
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .clip(RoundedCornerShape(20.dp))
////                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
////                    .shadow(8.dp, RoundedCornerShape(20.dp))
////                    .background(Color.White)
//                .clickable {
//                    // Handle ad click
//                }
//        ) {
//            CustomImageViewUri(
//                imageUrl = CustomSingleton.remoteConfig.BASE_IMAGE_URL + "stores/ads/" + ad.image,
//                modifier = Modifier.fillMaxSize().maskClip(MaterialTheme.shapes.extraLarge),
//                contentScale = ContentScale.Fit
//            )
//
//            // Optional overlay or action text
////                Text(
////                    text = "عرض خاص",
////                    color = Color.White,
////                    fontWeight = FontWeight.Bold,
////                    modifier = Modifier
////                        .align(Alignment.BottomStart)
////                        .padding(12.dp)
////                        .background(
////                            brush = Brush.horizontalGradient(
////                                listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
////                            ),
////                            shape = RoundedCornerShape(8.dp)
////                        )
////                        .padding(horizontal = 12.dp, vertical = 6.dp)
////                )
//        }
//    }
//}

            @OptIn(ExperimentalMaterial3Api::class)
            @Composable
            fun AdsCarousel(ads: List<Ads>, onAdClick: (Ads) -> Unit) {
                val viewModel: InsideStoreViewModel = hiltViewModel()
                val carouselState = rememberCarouselState { ads.size }

                HorizontalMultiBrowseCarousel(
                    state = carouselState,
                    preferredItemWidth = 300.dp,
                    itemSpacing = 12.dp,
                    modifier = Modifier
                        .padding(8.dp)
                        .height(220.dp)
                ) { page ->
                    val ad = viewModel.home.ads[page]

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
//                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
//                    .shadow(8.dp, RoundedCornerShape(20.dp))
//                    .background(Color.White)
                            .clickable {
                                // Handle ad click
                            }
                    ) {
                        CustomImageViewUri(
                            imageUrl = viewModel.appSession.remoteConfig.BASE_IMAGE_URL + "stores/ads/" + ad.image,
                            modifier = Modifier.fillMaxSize()
                                .maskClip(MaterialTheme.shapes.extraLarge),
                            contentScale = ContentScale.Fit
                        )

                        // Optional overlay or action text
//                Text(
//                    text = "عرض خاص",
//                    color = Color.White,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier
//                        .align(Alignment.BottomStart)
//                        .padding(12.dp)
//                        .background(
//                            brush = Brush.horizontalGradient(
//                                listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
//                            ),
//                            shape = RoundedCornerShape(8.dp)
//                        )
//                        .padding(horizontal = 12.dp, vertical = 6.dp)
//                )
                    }
                }
            }
