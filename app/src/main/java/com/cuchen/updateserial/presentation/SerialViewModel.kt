package com.cuchen.updateserial.presentation

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuchen.updateserial.core.Constants.TAG
import com.cuchen.updateserial.domin.BluetoothController
import com.cuchen.updateserial.domin.Response
import com.cuchen.updateserial.domin.SerialAPI
import com.cuchen.updateserial.states.DeviceScanViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SCAN_PERIOD = 20000L

@HiltViewModel
class SerialViewModel @Inject constructor(
    private val bluetoothController: BluetoothController, private val serialApi: SerialAPI
) : ViewModel() {

    private lateinit var scanFilters: List<ScanFilter>
    private lateinit var scanSettings: ScanSettings
    private val scanResults = mutableMapOf<String, ScanResult>()
    private val adapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private val _viewState = MutableLiveData<DeviceScanViewState>()
    val viewState = _viewState as LiveData<DeviceScanViewState>
    private var scanCallback: DeviceScanCallback? = null
    private var scanner: BluetoothLeScanner? = null


    private val _state = MutableStateFlow(BluetoothUiState())

    private var mGithubRepositories: MutableStateFlow<Response> = MutableStateFlow(Response(false))
    val githubRepositories: StateFlow<Response> get() = mGithubRepositories

    val state = combine(
        bluetoothController.scannedDevices, bluetoothController.pairedDevices, _state
    ) { scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices, pairedDevices = pairedDevices
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)


    init {
        startScan()

    }

    /*suspend fun updateSerial(macAddr: String, deviceType: String, serialNo: String) = viewModelScope.launch{
        Log.d(TAG, "updateSerial: ")
        val updateSerial = serialApi.updateSerial(macAddr, deviceType, serialNo)

//        return flow<Response> {
//            val updateSerial = serialApi.updateSerial(macAddr, deviceType, serialNo)
//
//            emit(updateSerial)
//            Log.d(TAG, "updateSerial 2222: ${updateSerial}")
//        }
    }*/


    fun updateSerial(
        macAddr: String, deviceType: String, serialNo: String
    ) {
        viewModelScope.launch {

            val updateSerial = serialApi.updateSerial(macAddr, deviceType, serialNo)

        }/*return flow {
            Log.d(TAG, "updateSerial: ")
            val updateSerial = serialApi.updateSerial(macAddr, deviceType, serialNo)
            if (updateSerial.success)
                emit(githubRepositories.value = updateSerial)

//        return flow<Response> {
//            val updateSerial = serialApi.updateSerial(macAddr, deviceType, serialNo)
//
//            emit(updateSerial)
//            Log.d(TAG, "updateSerial 2222: ${updateSerial}")
//        }
        }.flowOn(Dispatchers.IO)*/
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
//        bluetoothController.startDiscovery()
        Log.d(TAG, "startScan: ")
//        viewModelScope.launch {
//            updateSerial("00067A101C13", "test", "1234567892")
//        }

        scanFilters = buildScanFilters()
        scanSettings = buildScanSettings()
        if (!adapter.isMultipleAdvertisementSupported) {
            _viewState.value = DeviceScanViewState.AdvertisementNotSupported
            return
        }

        if (scanCallback == null) {
            scanner = adapter.bluetoothLeScanner
            _viewState.value = DeviceScanViewState.ActiveScan
            Handler().postDelayed({ stopScanning() }, SCAN_PERIOD)

            scanCallback = DeviceScanCallback()
            scanner?.startScan(scanFilters, scanSettings, scanCallback)
        }
    }

    fun stopScan() {
//        bluetoothController.stopDiscovery()
        stopScanning()
    }

    @SuppressLint("MissingPermission")
    private fun stopScanning() {
        scanner?.stopScan(scanCallback)
        scanCallback = null
        _viewState.value = DeviceScanViewState.ScanResults(scanResults)
    }

    private fun buildScanFilters(): List<ScanFilter> {
        val builder = ScanFilter.Builder()
//        builder.setServiceUuid(ParcelUuid(SERVICE_UUID))
//        builder.
        val filter = builder.build()
        return listOf(filter)
    }


    private fun buildScanSettings(): ScanSettings {
        return ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
    }


    private inner class DeviceScanCallback : ScanCallback() {
        override fun onBatchScanResults(results: List<ScanResult>) {
            super.onBatchScanResults(results)
            for (item in results) {
                item.device?.let { device ->
                    scanResults[device.address] = item
                }
            }
            Log.i(TAG, scanResults.toString())
            _viewState.value = DeviceScanViewState.ScanResults(scanResults)
        }

        override fun onScanResult(
            callbackType: Int, result: ScanResult
        ) {
            super.onScanResult(callbackType, result)
            result.device?.let { device ->

                scanResults[device.address] = result
            }
            Log.i(TAG, scanResults.toString())
            _viewState.value = DeviceScanViewState.ScanResults(scanResults)
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            val errorMessage = "Scan failed with error: $errorCode"
            _viewState.value = DeviceScanViewState.Error(errorMessage)
        }
    }
}