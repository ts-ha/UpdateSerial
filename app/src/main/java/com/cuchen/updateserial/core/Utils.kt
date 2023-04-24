package com.cuchen.updateserial.core

import android.util.Log
import com.cuchen.updateserial.core.Constants.TAG

class Utils {
    companion object {
        fun print(e: Exception?) = e?.apply {
            Log.e(TAG, stackTraceToString())
        }
    }
}