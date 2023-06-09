package com.cuchen.updateserial.presentation


import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cuchen.updateserial.bluetooth.ChatServer
import com.cuchen.updateserial.core.Constants.TAG
import com.cuchen.updateserial.presentation.components.BarcodePreview
import com.cuchen.updateserial.presentation.components.DeviceScanCompose
import com.cuchen.updateserial.ui.theme.UpdateSerialTheme
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val viewModel: SerialViewModel by viewModels()

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }
    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { /* Not needed */ }

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { perms ->
            val canEnableBluetooth = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ChatServer.startServer(application)
                viewModel.startScan()
                perms[Manifest.permission.BLUETOOTH_CONNECT] == true
            } else true

            if(canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.CAMERA,


                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN
                )
            )
        }


        setContent {
            UpdateSerialTheme {


              /*  val result = remember { mutableStateOf<Int?>(100) }
                val launcher =
                    rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                        result.value = it.resultCode
                    }

                LaunchedEffect(key1 = true) {

                    Dexter.withContext(this@MainActivity)
                        .withPermissions(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.BLUETOOTH_ADVERTISE,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN
                        )
                        .withListener(object : MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                launcher.launch(intent)
//                                ChatServer.startServer(application)
//                                viewModel.startScan()
                                Log.d(TAG, "onPermissionsChecked: ")
                            }

                            override fun onPermissionRationaleShouldBeShown(
                                p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                                p1: PermissionToken?
                            ) {
                                Log.d(TAG, "onPermissionRationaleShouldBeShown: ")
                            }

                        })
                        .check()

                }


                LaunchedEffect(key1 = result.value) {
                    if (result.value == RESULT_OK) {
                        ChatServer.startServer(application)
                        viewModel.startScan()
                    } else {
                        Log.d(TAG, "onPermissionRationaleShouldBeShown: ")
                    }
                }*/

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val barCodeVal = remember { mutableStateOf("") }
                        val macAddr = remember { mutableStateOf("") }
                        BarcodePreview(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .clipToBounds()
                        ) {
                            barCodeVal.value = it
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Choose a device to chat with:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val deviceScanningState by viewModel.viewState.observeAsState()

//                        val deviceConnectionState by ChatServer.deviceConnection.observeAsState()
//
//                        var isChatOpen by remember {
//                            mutableStateOf(false)
//                        }
                        deviceScanningState?.let {
                            DeviceScanCompose.DeviceScan(
                                deviceScanViewState = it,
                                viewModel = viewModel,
                                barCodeVal = barCodeVal.value
                            ) {

                            }
                        }
                        /*DeviceScreen(
                            state = state,
                            onStartScan = viewModel::startScan,
                            onStopScan = viewModel::stopScan
                        ) {
                            macAddr.value = it.address
                            Log.d(TAG, "onCreate: macAddr  $macAddr ")
                        }*/


                    }
                }
            }
        }
    }


}
