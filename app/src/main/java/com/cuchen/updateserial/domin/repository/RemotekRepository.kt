package com.cuchen.updateserial.domin.repository

import com.cuchen.updateserial.domin.UpdateSerialResponse

interface RemoteRepository {

    suspend fun updateSerial(
        macAddr: String,
        deviceType: String,
        serialNo: String
    ): UpdateSerialResponse
}