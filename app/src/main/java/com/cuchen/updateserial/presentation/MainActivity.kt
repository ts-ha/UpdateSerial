package com.cuchen.updateserial.presentation


import android.bluetooth.BluetoothManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cuchen.updateserial.presentation.components.BarcodePreview
import com.cuchen.updateserial.presentation.components.DeviceScreen
import com.cuchen.updateserial.ui.theme.UpdateSerialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val bluetoothManager by lazy {
        applicationContext.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UpdateSerialTheme {
                val viewModel = hiltViewModel<SerialViewModel>()
                val state by viewModel.state.collectAsState()
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val barCodeVal = remember { mutableStateOf("") }
                        BarcodePreview(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(240.dp)
                                .clipToBounds()
                        ) {
                            barCodeVal.value = it
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        DeviceScreen(
                            state = state,
                            onStartScan = viewModel::startScan,
                            onStopScan = viewModel::stopScan
                        )

                    }
                }
            }
        }
    }
}
