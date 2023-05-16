package com.cuchen.updateserial.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun UpdateAlertDialog() {
    val shouldDismiss = remember {
        mutableStateOf(true)
    }
    if (shouldDismiss.value)
        AlertDialog(onDismissRequest = { shouldDismiss.value = false }, title = {
            Text(text = "등록완료")
        }, text = {
            Text(text = "등록완료 하였습니다.")
        }, confirmButton = {
            TextButton(onClick = { shouldDismiss.value = false }) {
                Text(text = "확인")
            }
        },

            shape = RoundedCornerShape(24.dp)
        )


}
