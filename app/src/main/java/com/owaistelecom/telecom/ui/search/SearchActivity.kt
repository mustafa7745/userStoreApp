package com.owaistelecom.telecom.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owaistelecom.telecom.Singlton.AppSession
import com.owaistelecom.telecom.Singlton.FormBuilder
import com.owaistelecom.telecom.models.ProductView
import com.owaistelecom.telecom.models.StoreProduct
import com.owaistelecom.telecom.shared.CustomCard
import com.owaistelecom.telecom.shared.CustomIcon
import com.owaistelecom.telecom.shared.CustomIcon2
import com.owaistelecom.telecom.shared.CustomImageViewUri
import com.owaistelecom.telecom.shared.CustomRow
import com.owaistelecom.telecom.shared.CustomRow2
import com.owaistelecom.telecom.shared.MainCompose
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.RequestServer2
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.ui.add_to_cart.AddToCartActivity
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val requestServer: RequestServer2,
    private val builder: FormBuilder,
    private val appSession: AppSession
):ViewModel(){
    val stateController = StateController()
    var search by mutableStateOf("")
    var productViews by mutableStateOf<List<ProductView>>(listOf())
    var isLoading by mutableStateOf(false)///
    fun search() {
        isLoading = true

        viewModelScope.launch {
            try {
                val body = builder.sharedBuilderFormWithStoreId()
                    .addFormDataPart("search",search)

                val data = requestServer.request(body, "search")
                productViews = MyJson.IgnoreUnknownKeys.decodeFromString(data as String)
                isLoading = false
                stateController.successStateAUD()
            } catch (e: Exception) {
                stateController.successStateAUD(e.message.toString())
            }
        }

//        requestServer.request2(body, "search", { code, fail ->
//            stateController.errorStateAUD(fail)
//            isLoading = false
//        }
//        ) { data ->
//            productViews =
//                MyJson.IgnoreUnknownKeys.decodeFromString(
//                    data
//                )
//            isLoading = false
////            stateController.successStateAUD()
//        }
    }
    fun getAppSession(): AppSession {
        return appSession
    }

}

@AndroidEntryPoint
class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SearchScreen()
        }
    }

    @Composable
    private fun SearchScreen() {
        val viewModel:SearchViewModel = hiltViewModel()
        val context = LocalContext.current
        viewModel.stateController.successState()
        OwaisTelecomTheme {
            MainCompose(viewModel.stateController, { }) {
                Column(Modifier.safeDrawingPadding()) {
                    CustomCard(modifierBox = Modifier) {
                        CustomRow2 {
                            CustomIcon(Icons.AutoMirrored.Default.ArrowBack, border = true) {
                                finish()
                            }
                            Row {
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = viewModel.search, onValueChange = {
                                        viewModel.search = it
                                        //                                    if (it.isEmpty())productViews= emptyList()
                                        //                                    else
                                        //                                    search()
                                    }, label = { Text("ابحث هنا") },
                                    trailingIcon = {
                                        CustomIcon2(Icons.Default.Search) {
                                            if (!viewModel.isLoading && viewModel.search.isNotEmpty())
                                                viewModel.search()
                                        }
                                    }
                                )
                            }
                        }
                    }
                    if (viewModel.isLoading)
                        LinearProgressIndicator(Modifier.fillMaxWidth())

                    LazyColumn {
                        itemsIndexed(viewModel.productViews) { index: Int, item: ProductView ->
                            item.products.forEach { product ->
                                CustomCard(modifierBox = Modifier.clickable { goToAddToCart(context,product) }) {
                                    CustomRow {
                                        Text(product.product.productName)
                                        if (product.product.images.isNotEmpty())
                                            CustomImageViewUri(
                                                modifier = Modifier.size(30.dp),
                                                imageUrl = viewModel.getAppSession().remoteConfig.BASE_IMAGE_URL +
                                                        viewModel.getAppSession().remoteConfig.SUB_FOLDER_PRODUCT +
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



}
private fun goToAddToCart(context: Context,s: StoreProduct) {
    val intent = Intent(
        context,
        AddToCartActivity::class.java
    )
    intent.putExtra("product", MyJson.MyJson.encodeToString(s))
    context.startActivity(intent)
}