package com.cuchen.updateserial.presentation.components

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermission() {
    val cameraPermissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
        )
    )
    if (cameraPermissionState.allPermissionsGranted) {
        Text("Camera permission Granted")

    } else {
        Column {
            val textToShow = if (cameraPermissionState.shouldShowRationale) {
                "The camera is important for this app. Please grant the permission."

            } else {
                "Camera not available"
            }
            Text(textToShow)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { cameraPermissionState.launchMultiplePermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}