package com.owaistelecom.telecom.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberImagePainter
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.owaistelecom.telecom.shared.CustomCard
import com.owaistelecom.telecom.shared.CustomRow
import com.owaistelecom.telecom.shared.MainCompose1
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.RequestServer
import com.owaistelecom.telecom.shared.SingletonStores
import com.owaistelecom.telecom.shared.StateController
import com.owaistelecom.telecom.shared.builderForm3
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString


class OrdersActivity : ComponentActivity() {
    val stateController = StateController()
    val requestServer = RequestServer(this)
    private var orders by mutableStateOf<List<Order>>(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        read()

        setContent {
            OwaisTelecomTheme  {
                MainCompose1(
                    0.dp, stateController, this,{
                         read()
                    }

                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        itemsIndexed(orders){index, order ->
                            CustomCard( modifierBox = Modifier.fillMaxSize().clickable {

                            }) {
                                Column {
                                    CustomRow {
                                        Text( " رقم الطلب: " + order.id.toString(),Modifier.padding(8.dp))
                                        Text( " اسم المستخدم : "+order.userName.toString(),Modifier.padding(8.dp))
                                    }
                                    Text(  " رقم المستخدم : "+ order.userPhone.toString(),Modifier.padding(8.dp))
                                    Text(
                                        text = order.amounts.joinToString(
                                            separator = ", "
                                        ) { it.amount.toString() +" "+ it.currencyName },
                                        fontSize = 14.sp,
                                    )
                                    Button(onClick = {
                                        gotoOrderProducts(order)

                                    }, modifier = Modifier.padding(8.dp).fillMaxSize()) {
                                        Text("عرض المنتجات")
                                    }
                                }

                            }
                        }

                    }
                }
            }
        }
    }

    fun read() {
        stateController.startRead()
        val body = builderForm3()
            .addFormDataPart("storeId",SingletonStores.selectedStore.id.toString())
            .build()

        requestServer.request2(body, "getOrders", { code, fail ->
            stateController.errorStateRead(fail)
        }
        ) { data ->
            orders =
                MyJson.IgnoreUnknownKeys.decodeFromString(
                    data
                )

            stateController.successState()
        }
    }

    private fun gotoOrderProducts(order: Order) {
        val intent = Intent(this, OrderProductsActivity::class.java)
        intent.putExtra("order", MyJson.MyJson.encodeToString(order))
        startActivity(intent)
    }
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
    var amount: Double,
)