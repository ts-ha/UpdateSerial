package com.cuchen.updateserial.presentation.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.cuchen.updateserial.core.Constants.TAG
import com.cuchen.updateserial.domin.Response
import com.cuchen.updateserial.presentation.SerialViewModel

@Composable
fun UpdateSerialResult(
    viewModel: SerialViewModel = hiltViewModel(),
    createProfileImageContent: @Composable (success: Boolean) -> Unit
) {
    Log.d(TAG, "UpdateSerialResult: ${viewModel.viewState.value}")
//    Log.d(TAG, "UpdateSerialResult: ${viewModel.getImageFromDatabaseResponse}")
    createProfileImageContent(viewModel.updateSerialResponse.success)

}
