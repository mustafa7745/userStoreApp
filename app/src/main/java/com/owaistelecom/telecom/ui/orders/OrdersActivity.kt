package com.owaistelecom.telecom.ui.orders

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owaistelecom.telecom.Singlton.FormBuilder
import com.owaistelecom.telecom.shared.MainComposeRead
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.RequestServer2
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.ui.order_products.OrderProductsActivity
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val requestServer: RequestServer2,
    private val builder: FormBuilder
):ViewModel(){
    val stateController = StateController()
    fun read() {
        stateController.startRead()
        viewModelScope.launch {
            try {
                val body = builder.sharedBuilderFormWithStoreId()

                val data = requestServer.request(body, "getOrders")
                orders = MyJson.IgnoreUnknownKeys.decodeFromString(data as String)
                stateController.successState()
            } catch (e: Exception) {
                stateController.errorStateRead(e.message.toString())
            }
        }


//        stateController.startRead()
//        requestServer.checkTokenIsNeedUpdate({ _, fail ->
//            stateController.errorStateRead(fail)
//        }){ token->
//            val body =builder.builderFormWithAccessTokenAndStoreId_2(token).build()
//            requestServer.request2(body, "getOrders", { code, fail ->
//                stateController.errorStateRead(fail)
//            }
//            ) { data ->
//                orders =
//                    MyJson.IgnoreUnknownKeys.decodeFromString(
//                        data
//                    )
//
//                stateController.successState()
//            }
//        }
    }
    var orders by mutableStateOf<List<Order>>(listOf())
}

@AndroidEntryPoint
class OrdersActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrdersScreen()
        }
    }
}

@Composable
private fun OrdersScreen() {
    val viewModel:OrdersViewModel = hiltViewModel()
    LaunchedEffect (2){
        viewModel.read()
    }
    val activity = LocalContext.current as Activity
    OwaisTelecomTheme {
        MainComposeRead("سجل الطلبات السايقة",viewModel.stateController,{activity.finish()}, {
            viewModel.read()
        }) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(viewModel.orders) { index, order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(12.dp))
                            .clickable { gotoOrderProducts(order,activity) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("رقم الطلب: ${order.id}", fontWeight = FontWeight.Bold)
                                Text("اسم المستخدم: ${order.userName}", fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("رقم الهاتف: ${order.userPhone}", fontSize = 14.sp)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "المبالغ:",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color.DarkGray
                            )

                            Text(
                                text = order.amounts.joinToString(separator = "\n") {
                                    "${it.amount} ${it.currencyName}"
                                },
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { gotoOrderProducts(order,activity) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("عرض المنتجات")
                            }
                        }
                    }
                }
            }
        }
    }
}
private fun gotoOrderProducts(order: Order,context: Context) {
    val intent = Intent(context, OrderProductsActivity::class.java)
    intent.putExtra("order", MyJson.MyJson.encodeToString(order))
    context.startActivity(intent)
}
@Serializable
data class Order(
    val id: Int,
    val userName: String,
    val userPhone: String,
    val amounts:List<OrderAmount>
)
@Serializable
data class OrderAmount(
    val id: Int,
    val currencyName: String,
    val currencyId: Int,

    var amount: Double,
)