package com.cuchen.updateserial.presentation.components

import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.cuchen.updateserial.core.BarCodeAnalyser
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun BarcodePreview(modifier: Modifier, barCodeString: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<Preview?>(null) }
    val barCodeVal = remember { mutableStateOf("") }
    var showBarcodeAnalysis by remember { mutableStateOf(false) }
    Column() {
        Box(
            modifier = modifier
        ) {
            AndroidView(
                factory = { AndroidViewContext ->
                    PreviewView(AndroidViewContext).apply {
                        this.scaleType = PreviewView.ScaleType.FILL_CENTER
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        layoutParams.width = 240
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { previewView ->
                    if (showBarcodeAnalysis) {
                        val cameraSelector: CameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()
                        val cameraExecutor: ExecutorService =
                            Executors.newSingleThreadExecutor()
                        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
                            ProcessCameraProvider.getInstance(context)

                        cameraProviderFuture.addListener({
                            preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraProvider: ProcessCameraProvider =
                                cameraProviderFuture.get()
                            val barcodeAnalyser = BarCodeAnalyser { barcodes ->
                                barcodes.forEach { barcode ->
                                    barcode.rawValue?.let { barcodeValue ->
                                        barCodeVal.value = barcodeValue
                                        barCodeString(barcodeValue)
                                        Toast.makeText(
                                            context,
                                            barcodeValue,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        cameraProvider.unbindAll()
                                    }
                                }
                            }
                            val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                                .also {
                                    it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                                }

                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    cameraSelector,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (e: Exception) {
                                Log.d("TAG", "CameraPreview: ${e.localizedMessage}")
                            }
                        }, ContextCompat.getMainExecutor(context))
                    }
                }

            )
        }
        Row {
            Text(
                text = barCodeVal.value,

                modifier = Modifier
                    .height(40.dp)
                    .weight(0.8f),
                style = TextStyle(
                    fontStyle = FontStyle.Italic,
                    fontSize = 30.sp
                )
            )
            Button(onClick = {
                showBarcodeAnalysis = !showBarcodeAnalysis
            }) {
                Text("바코드 실행")
            }

        }
    }
}