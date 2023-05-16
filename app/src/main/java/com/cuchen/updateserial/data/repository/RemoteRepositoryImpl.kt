package com.cuchen.updateserial.data.repository

import com.cuchen.updateserial.domin.SerialAPI
import com.cuchen.updateserial.domin.UpdateSerialResponse
import com.cuchen.updateserial.domin.repository.RemoteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteRepositoryImpl @Inject constructor(
    private val serialAPI: SerialAPI
) : RemoteRepository {

    override suspend fun updateSerial(
        macAddr: String,
        deviceType: String,
        serialNo: String
    ): UpdateSerialResponse {
        return serialAPI.updateSerial(
            serialNo = serialNo,
            macAddr = macAddr,
            deviceType = deviceType
        )
    }
}