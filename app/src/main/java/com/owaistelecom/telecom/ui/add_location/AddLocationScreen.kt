package com.owaistelecom.telecom.ui.add_location

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PinConfig
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.owaistelecom.telecom.shared.MainComposeRead
import com.owaistelecom.telecom.ui.theme.OwaisTelecomTheme

@Composable
fun AddLocationScreen(myLocationManager: MyLocationManager){

    val viewModel: AddLocationViewModel = hiltViewModel()
    val context = LocalContext.current
    val activity = context as? ComponentActivity ?: return // ✅ الحصول على ComponentActivity بطريقة آمنة



    LaunchedEffect(Unit) {
        myLocationManager.initLocation()
    }

//    val viewModel:AddLocationViewModel = hiltViewModel()
//    val myLocationManager = remember {  MyLocationManager(componentActivity)}
//
//    LaunchedEffect(1) {
//        myLocationManager.initLocation()
//    }
    OwaisTelecomTheme {
        if (myLocationManager.isLoading){
            viewModel.stateController.startRead()
        }else{
            if (myLocationManager.isSuccess){
                viewModel.stateController.successStateAUD()
                viewModel.stateController.successState()
            }else{
//                viewModel. stateController.errorStateAUD(myLocationManager.messageLocation)
                viewModel.stateController.errorStateRead(myLocationManager.messageLocation)
            }
        }

        MainComposeRead ("اضافة موقع للتوصيل",viewModel.stateController,{activity.finish()},{
            myLocationManager.initLocation{

            }
        }){
                OutlinedTextField(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    label = { Text("وصف الموقع") },
                    value = viewModel.street,
                    onValueChange = {
                       viewModel.street = it
                    }
                )
                ComposeMapp(myLocationManager,activity)
        }
    }
}
@Composable
private fun ComposeMapp(myLocationManager: MyLocationManager,componentActivity: Activity) {
    val viewModel:AddLocationViewModel = hiltViewModel()
    var location = myLocationManager.location!!
    val markerState = rememberMarkerState(position = location)

    if (viewModel.exitWithSuccess){
        val resultIntent = Intent()
        resultIntent.putExtra("location", viewModel.resultString) // إضافة بيانات إلى النتيجة
        componentActivity.setResult(RESULT_OK, resultIntent)
        componentActivity.finish()
    }

    var cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(location, 16f)
    }

    val pinConfig = PinConfig.builder().build()

// Checking if camera is moving and editMode is enabled
    if (cameraPositionState.isMoving && !viewModel.isCurrentLocation) {
        // Update location based on camera's current target position
        val updatedLatLng = LatLng(
            cameraPositionState.position.target.latitude,
            cameraPositionState.position.target.longitude
        )

        // Set new position to markerState and location
        location = updatedLatLng
        markerState.position = updatedLatLng
    }

    Button(
        onClick = {
            if (viewModel.street.length < 10){
                viewModel.stateController.errorStateAUD("يجب كتابة الموقع بشكل صحيح ولا يقل عن 10 احرف")
                return@Button
            }
            viewModel.addLocation( cameraPositionState.position.target.latitude.toString()+ "," + cameraPositionState.position.target.longitude.toString())
//                        updateLocation()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(50.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = "حفظ", color = Color.White, fontSize = 18.sp)
    }




    Box(
        Modifier.fillMaxSize(),
    ) {
        GoogleMap(
            Modifier.fillMaxWidth().height(400.dp).align(Alignment.TopCenter),
            cameraPositionState = cameraPositionState
        ) {
            AdvancedMarker(
                state = markerState,
                pinConfig = pinConfig
            )
        }
        Column (Modifier.fillMaxWidth().align(Alignment.TopCenter)){


            if (!viewModel.isCurrentLocation){
                Text(modifier = Modifier.fillMaxWidth().background(Color.LightGray), textAlign = TextAlign.Center, text = "قم بتحريك المؤشر للموقع الذي تريد")
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(
                    modifier =
                    Modifier.weight(1f)
                        .padding(8.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        viewModel.isCurrentLocation = true
                        viewModel.stateController.startAud()
                        myLocationManager.initLocation()
                        cameraPositionState.move(
                            CameraUpdateFactory.newCameraPosition(
                                CameraPosition.fromLatLngZoom(location, 16f)))
                        markerState.position = myLocationManager.location!!
                    }
                ) {
                    Row {
                        Text("الموقع الحالي")
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }

                }
                Button(
                    modifier =
                    Modifier.weight(1f)
                        .padding(8.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        viewModel. isCurrentLocation = false
//                    stateController.successStateAUD("")
//                    stateController.startAud()
//                    myLocationManager.initLocation()
//                    cameraPositionState.move(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(location, 16f)))
//                    markerState.position = myLocationManager.location!!
                    }
                ) {
                    Row {
                        Text("موقع اخر")
                        Icon(
                            imageVector = Icons.Outlined.Place,
                            contentDescription = "",
                            tint = Color.White
                        )
                    }

                }
            }
        }
    }
}
