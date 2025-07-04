package com.owaistelecom.telecom.ui.add_location

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.owaistelecom.telecom.Singlton.FormBuilder
import com.owaistelecom.telecom.shared.MyJson
import com.owaistelecom.telecom.shared.RequestServer2
import com.owaistelecom.telecom.shared.StateController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLocationViewModel @Inject constructor(
    private val requestServer: RequestServer2,
    private val formBuilder: FormBuilder

):ViewModel() {
    val stateController = StateController()
    //
    var street by mutableStateOf("")
    var isCurrentLocation by mutableStateOf(true)

    lateinit var resultString :String
    var exitWithSuccess by mutableStateOf(false)
     fun addLocation(latLong:String) {
         viewModelScope.launch{
             stateController.startAud()
             try {
                 val body = formBuilder.sharedBuilderFormWithStoreId()
                     .addFormDataPart("latLng",latLong)
                     .addFormDataPart("street",street)
                 val data = requestServer.request(body, "addLocation")
                 stateController.successStateAUD()
                 resultString = data as String
                 exitWithSuccess = true
             } catch (e: Exception) {
                 stateController.errorStateAUD(e.message.toString())
             }
         }
    }
}