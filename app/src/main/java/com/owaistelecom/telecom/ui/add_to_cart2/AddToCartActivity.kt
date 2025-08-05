package com.owaistelecom.telecom.ui.add_to_cart

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.models.PrimaryProduct
import com.owaistelecom.telecom.models.Product
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.models.StoreProduct
import com.owaistelecom.telecom.shared.ADControll
import com.owaistelecom.telecom.shared.ADControll2
import com.owaistelecom.telecom.shared.CustomCircleBox
import com.owaistelecom.telecom.shared.CustomImageViewUri
import com.owaistelecom.telecom.shared.HeaderUI2
import com.owaistelecom.telecom.shared.MainComposeAUD
import com.owaistelecom.telecom.shared.ReadMoreText
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.shared.formatPrice
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class AddToCart2ViewModel @Inject constructor(
     val appSession: AppSession,
) : ViewModel() {
    val stateController = StateController()

}

@AndroidEntryPoint
class AddToCart2Activity : ComponentActivity() {
    lateinit var product: PrimaryProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent
        val str = intent.getStringExtra("product")
        if (str != null) {
            try {
                product = MyJson.IgnoreUnknownKeys.decodeFromString(str)
            }catch (e:Exception){
                finish()
            }

        } else {
            finish()
        }

        enableEdgeToEdge()
        setContent {
            AddToCartScreen(this)
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun AddToCartScreen(componentActivity: ComponentActivity){
        val viewModel: AddToCart2ViewModel = hiltViewModel()
        OwaisTelecomTheme {
            MainComposeAUD(product.name,viewModel.stateController,{componentActivity.finish()}) {
            val productImages = remember { viewModel.appSession.home.homeProducts.productsImages.filter { it.productId == product.id } }
                val pagerState = rememberPagerState(initialPage = 0) { 2 }

                val pagerStateImage = rememberPagerState(pageCount = { productImages .size })
                val scope = rememberCoroutineScope()
val isFree = viewModel.appSession.home.storeCurrencies.find { it.isSelected == 1 }
                HeaderUI2(if(isFree != null)isFree.deliveryPrice.toDouble() == 0.0 else false)
                if (productImages .isNotEmpty()) {

                        HorizontalPager(
                            pagerStateImage,
                            modifier = Modifier.background(Color.White),
                        ) { i ->
                            CustomImageViewUri(
                                imageUrl = viewModel.appSession.remoteConfig.BASE_IMAGE_URL+
                                        viewModel.appSession.remoteConfig.SUB_FOLDER_PRODUCT+
                                        productImages[i].image,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp),
                                contentScale = ContentScale.Fit
                            )
                        }


                    LazyRow(
                        contentPadding = PaddingValues(8.dp),
                        modifier =  Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .height(25.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        items(productImages .size){ item->
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
                                CustomCircleBox(pagerStateImage.currentPage == item, Color.Black,12.dp)

//                                    Icon(
////                                        modifier = modifierIcon,
//                                        imageVector = if (pagerState.currentPage == item) Icons.Default.AddCircle else Icons.Outlined.AddCircle,
//                                        contentDescription = ""
//                                    )
                            }
                        }
                    }
                }
                else{
                    AsyncImage(
                        model = R.drawable.logo,
                        contentDescription = "",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
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
//                            val homeProduct = viewModel.appSession.home.homeProducts
                            val storeNestedSectionId = viewModel.appSession.selectedStoreNestedSection?.id
                            val homeProduct= viewModel.appSession.homeProducts[storeNestedSectionId] ?: return@HorizontalPager
                            val storeProducts = homeProduct.storeProducts.filter { it.productId == product.id }
                            // صفحة الخيارات
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(storeProducts) { storeProduct1 ->
                                    val currencyName = viewModel.appSession.home.storeCurrencies
                                        .firstOrNull { it.currencyId == storeProduct1.currencyId }
                                        ?.currencyName ?: ""

                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp, horizontal = 12.dp),
                                        elevation = CardDefaults.cardElevation(4.dp),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                            if (storeProduct1.name.isNotEmpty()){
                                                Text(
                                                    text = storeProduct1.name,
                                                    style = MaterialTheme.typography.titleMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                            }

                                            // الوصف
                                            if (storeProduct1.description.isNotEmpty()) {
                                                Text(
                                                    text = storeProduct1.description,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = Color.Gray
                                                )

                                                Spacer(modifier = Modifier.height(4.dp))
                                            }
                                            FlowRow(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 10.dp)
                                            ) {
                                                storeProduct1.info.forEach { item ->
                                                    Surface(
                                                        shape = RoundedCornerShape(8.dp),
                                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Star,
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.primary,
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(4.dp))
                                                            Text(
                                                                text = item,
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = MaterialTheme.colorScheme.primary
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {

                                                if (storeProduct1.prePrice != 0.0 && storeProduct1.prePrice > storeProduct1.price) {
                                                    Text(
                                                        text = "${formatPrice(storeProduct1.prePrice.toString())} $currencyName",
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            textDecoration = TextDecoration.LineThrough,
                                                            color = Color.Gray
                                                        )
                                                    )
                                                }

                                                // السعر الحالي
                                                Text(
                                                    text = "${formatPrice(storeProduct1.price.toString())} $currencyName",
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                            HorizontalDivider(Modifier.padding(8.dp))
                                            Row (Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                                ADControll2(storeProduct1)
                                            }
                                        }
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
                                    product.description?.let { ReadMoreText(it) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

