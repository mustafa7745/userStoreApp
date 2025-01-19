package com.owaistelecom.telecom.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.owaistelecom.telecom.R
import com.owaistelecom.telecom.models.ProductView
import com.owaistelecom.telecom.models.StoreProduct
import com.owaistelecom.telecom.models.UserInfo
import com.owaistelecom.telecom.shared.ADControll
import com.owaistelecom.telecom.shared.AToken
import com.owaistelecom.telecom.shared.CustomCard
import com.owaistelecom.telecom.shared.CustomIcon
import com.owaistelecom.telecom.shared.CustomImageView
import com.owaistelecom.telecom.shared.CustomImageView1
import com.owaistelecom.telecom.shared.CustomImageViewUri
import com.owaistelecom.telecom.shared.CustomRow
import com.owaistelecom.telecom.shared.CustomRow2
import com.owaistelecom.telecom.shared.MainCompose2
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.ReadMoreText
import com.owaistelecom.telecom.shared.RequestServer
import com.owaistelecom.telecom.shared.SingletonRemoteConfig
import com.owaistelecom.telecom.shared.SingletonStores
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.shared.builderForm3
import com.owaistelecom.telecom.shared.formatPrice
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import kotlinx.serialization.encodeToString

class SearchActivity : ComponentActivity() {
    val requestServer = RequestServer(this)
    val stateController = StateController()
    var search by mutableStateOf("")
    private var productViews by mutableStateOf<List<ProductView>>(listOf())



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OwaisTelecomTheme {
                Column(Modifier.safeDrawingPadding()) {
                    CustomCard(modifierBox = Modifier) {
                        CustomRow2 {
                            CustomIcon(Icons.AutoMirrored.Default.ArrowBack, border = true) {
                                finish()
                            }
                            Row {
                                TextField(value = search, onValueChange = {
                                    search = it
                                    if (it.isEmpty())productViews= emptyList()
                                    else
                                    search()
                                }, label = { Text("ابحث هنا") })
                            }
                        }
                    }

                    LazyColumn {
                        itemsIndexed(productViews){index: Int, item: ProductView ->
                            item.products.forEach { product->
                                CustomCard(modifierBox = Modifier.clickable { goToAddToCart(product) }) {
                                    CustomRow {
                                        Text(product.product.productName)
                                        if (product.product.images.isNotEmpty())
                                        CustomImageViewUri(
                                            modifier = Modifier.size(30.dp),
                                            imageUrl =  SingletonRemoteConfig.remoteConfig.BASE_IMAGE_URL +
                                                    SingletonRemoteConfig.remoteConfig.SUB_FOLDER_PRODUCT +
                                                    product.product.images.first().image
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }





    ///
    private fun search() {
        val body = builderForm3()
            .addFormDataPart("storeId",SingletonStores.selectedStore.id.toString())
            .addFormDataPart("search",search)
            .build()

        requestServer.request2(body, "search", { code, fail ->
            stateController.errorStateAUD(fail)
        }
        ) { data ->
            productViews =
                MyJson.IgnoreUnknownKeys.decodeFromString(
                    data
                )
//            stateController.successStateAUD()
        }
    }
    private fun goToAddToCart(s: StoreProduct) {

        val intent = Intent(
            this,
            AddToCartActivity::class.java
        )
        intent.putExtra("product", MyJson.MyJson.encodeToString(s))
        startActivity(intent)
    }
}