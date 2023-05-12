package com.cuchen.updateserial.presentation.components


import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.isNotEmpty
import com.cuchen.updateserial.core.Constants.TAG
import com.cuchen.updateserial.presentation.SerialViewModel
import com.cuchen.updateserial.states.DeviceScanViewState

object DeviceScanCompose {


    private lateinit var deviceScanResults: ScanResult

    @SuppressLint("MissingPermission")
    @Composable
    fun ShowDevices(
        scanResults: Map<String, ScanResult>,
        viewModel: SerialViewModel,
        barCodeVal: String,
        onClick: (ScanResult?) -> Unit

    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
        ) {
            var deviceName by remember { mutableStateOf("Unknown") }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
                    .height(60.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    text = deviceName,
                    textAlign = TextAlign.End
                )
                Button(onClick = {
                    getManufacturerSpecificData(deviceScanResults)
//                    viewModel.updateSerial(
//                        macAddr = deviceScanResults.device?.address ?: "",
//                        deviceType = deviceScanResults.device?.name?.take(4) ?: "",
//                        serialNo = barCodeVal
//                    )
                }) {
                    Text(text = "update serial")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .padding(10.dp)
                    .padding(start = 8.dp, end = 8.dp)
                    .fillMaxWidth()
            ) {
                itemsIndexed(scanResults.keys.toList()) { _, key ->
                    Column {
                        Column(modifier = Modifier
                            .clickable {
                                deviceScanResults = scanResults.get(key = key)!!
                                onClick(deviceScanResults)
//                                getManufacturerSpecificData(deviceScanResults)

                                deviceName = if (::deviceScanResults.isInitialized) {
                                    Log.d(TAG, "111 name: ${deviceScanResults.device.name ?: "Unknown"}")
                                    deviceScanResults.device.name ?: "Unknown"
                                } else {
                                    "Unknown"
                                }
                            }
                            .background(Color.LightGray, shape = RoundedCornerShape(10.dp))
                            .fillMaxWidth()
                            .border(1.dp, Color.Black, shape = RoundedCornerShape(10.dp))
                            .padding(5.dp)) {
                            Text(
                                text = scanResults[key]?.device?.name ?: "Unknown Device",
                            )
                            Spacer(modifier = Modifier.size(10.dp))
                            Text(
                                text = scanResults[key]?.device?.address ?: "",
                                fontWeight = FontWeight.Light
                            )
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }

        }
    }

    private fun getManufacturerSpecificData(result: ScanResult) {
        result.scanRecord?.let {
            val manufacturerSpecificData = it.manufacturerSpecificData
            Log.d(TAG, "111: ${manufacturerSpecificData.isNotEmpty()}")

            if (manufacturerSpecificData.isNotEmpty() && manufacturerSpecificData.get(
                    manufacturerSpecificData.keyAt(0)
                ).size >= 8
            ) {
                Log.d(
                    TAG, "111 size: ${
                        manufacturerSpecificData.get(
                            manufacturerSpecificData.keyAt(0)
                        ).size
                    }"
                )
                Log.d(TAG, "getMefesf: ${manufacturerSpecificData.keyAt(0).toString()}")
            }
        }


//        manufacturerSpecificData?.get(manufacturerSpecificData.keyAt(0))

    }


    @SuppressLint("MissingPermission")
    @Composable
    fun DeviceScan(
        deviceScanViewState: DeviceScanViewState,
        viewModel: SerialViewModel,
        barCodeVal: String,
        onDeviceSelected: () -> Unit
    ) {


        when (deviceScanViewState) {
            is DeviceScanViewState.ActiveScan -> {
                Box(
                    contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Scanning for devices",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }

            }

            is DeviceScanViewState.ScanResults -> {
                ShowDevices(scanResults = deviceScanViewState.scanResults,
                    barCodeVal = barCodeVal,
                    viewModel = viewModel,
                    onClick = {
                        onDeviceSelected()
                    })
//                    ChatServer.setCurrentChatConnection(macAddr = it.device?.address ?: "",
//                        deviceType = it.device?.name?.take(4) ?: "",
//                        serialNo = barCodeVal.value)
//                    Log.i(TAG, "Device Selected ${it!!.device?.name ?: ""}")
//                    ChatServer.setCurrentChatConnection(device = it.!!)

//                    if (barCodeVal.value.isNotBlank()) {
//                        viewModel.updateSerial(
//
//                        )
//                    }
////                    onDeviceSelected()

            }

            is DeviceScanViewState.Error -> {
                Text(text = deviceScanViewState.message)
            }

            else -> {
                Text(text = "Nothing")
            }
        }
    }


}