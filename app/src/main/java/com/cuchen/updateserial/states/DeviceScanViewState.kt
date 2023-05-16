package com.cuchen.updateserial.states

import android.bluetooth.le.ScanResult

sealed class DeviceScanViewState {
    object ActiveScan: DeviceScanViewState()
    class ScanResults(val scanResults: Map<String, ScanResult>): DeviceScanViewState()
    class Error(val message: String): DeviceScanViewState()
    object AdvertisementNotSupported: DeviceScanViewState()
}