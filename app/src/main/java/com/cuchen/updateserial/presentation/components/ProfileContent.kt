package com.cuchen.updateserial.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BarcodeContent(
    barcode: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Button(
            onClick = barcode
        ) {
            Text(
                text = "바코드 스캔",
                fontSize = 18.sp
            )
        }
    }

//    GetImageFromDatabase(
//        createProfileImageContent = { imageUrl ->
//            ProfileImageContent(imageUrl)
//        }
//    )
}