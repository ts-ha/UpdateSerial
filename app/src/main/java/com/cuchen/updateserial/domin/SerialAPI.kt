package com.cuchen.updateserial.domin

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface SerialAPI {
    @FormUrlEncoded
    @POST("updateSerial.action")
    suspend fun updateSerial(
        @Field("macAddr") macAddr: String,
        @Field("deviceType") deviceType: String,
        @Field("serialNo") serialNo: String
    ): Response
}

data class Response(
    val success: Boolean
)