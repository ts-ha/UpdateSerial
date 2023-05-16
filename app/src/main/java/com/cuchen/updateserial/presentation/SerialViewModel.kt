package com.cuchen.updateserial.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cuchen.updateserial.core.Constants.TAG
import com.cuchen.updateserial.domin.Response
import com.cuchen.updateserial.domin.UpdateSerialResponse
import com.cuchen.updateserial.domin.repository.RemoteRepository
import com.cuchen.updateserial.states.DeviceScanViewState
import com.cuchen.updateserial.utils.toHex2
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SCAN_PERIOD = 20000L

@HiltViewModel
class SerialViewModel @Inject constructor(
    application: Application, private val repository: RemoteRepository
) : AndroidViewModel(application) {

    private lateinit var scanFilters: List<ScanFilter>
    private lateinit var scanSettings: ScanSettings
    private val scanResults = mutableMapOf<String, ScanResult>()
    private val bluetoothManager =
        getApplication<Application>().applicationContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

    private val adapter: BluetoothAdapter = bluetoothManager.adapter
    private val _viewState = MutableLiveData<DeviceScanViewState>()
    val viewState = _viewState as LiveData<DeviceScanViewState>
    private var scanCallback: DeviceScanCallback? = null
    private var scanner: BluetoothLeScanner? = null

    var updateSerialResponse by mutableStateOf(UpdateSerialResponse())
        private set

    init {
        startScan()
    }


    fun updateSerial(
        macAddr: String, deviceType: String, serialNo: String
    ) {
        viewModelScope.launch {
            val updateSerial = repository.updateSerial(macAddr, deviceType, serialNo)
            updateSerialResponse = updateSerial
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
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
            _viewState.value = DeviceScanViewState.ScanResults(scanResults)
        }

        override fun onScanResult(
            callbackType: Int, result: ScanResult
        ) {
            super.onScanResult(callbackType, result)

            if (result.scanRecord?.deviceName?.contains("CUCHEN_") == true) {
                result.device?.let { device ->
                    scanResults[device.address] = result
                }
                _viewState.value = DeviceScanViewState.ScanResults(scanResults)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            val errorMessage = "Scan failed with error: $errorCode"
            _viewState.value = DeviceScanViewState.Error(errorMessage)
        }
    }
}