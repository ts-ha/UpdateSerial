package com.cuchen.updateserial.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuchen.updateserial.domin.BluetoothController
import com.cuchen.updateserial.domin.PokeAPI
import com.cuchen.updateserial.domin.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import okhttp3.Dispatcher
import javax.inject.Inject

@HiltViewModel
class SerialViewModel @Inject constructor(
    private val bluetoothController: BluetoothController,
    private val pokeAPI: PokeAPI
) : ViewModel() {

    private val _state = MutableStateFlow(BluetoothUiState())

    val updateSerialFlow: Flow<Response.Result> = updateSerial("","","")
    val state = combine(
        bluetoothController.scannedDevices, bluetoothController.pairedDevices, _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices, pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)


    fun updateSerial(deviceType : String,serialNo:String, macAddr:String ) : Flow<Response.Result>  {
        return flow<Response.Result> {
            val updateSerial = pokeAPI.updateSerial(
                deviceType = deviceType,
                serialNo = serialNo,
                macAddr = macAddr
            )
            updateSerial.success
        }.flowOn(
            Dispatchers.IO
        )
    }
    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }
}