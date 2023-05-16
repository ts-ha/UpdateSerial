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
    ): UpdateSerialResponse
}


class UpdateSerialResponse(
    val success: Boolean = false,
    val bean: Bean? = null,
    val errors: List<Errors>? = null
) {
    class Bean(val count: Int, val mac: List<String>)
    class Errors(val code: String, val field: String, val message: String)
}


sealed class Response<out T> {
    object Loading : Response<Nothing>()

    data class Success<out T>(
        val updateSerialResponse: Response<UpdateSerialResponse>,
    ) : Response<T>()


    data class Failure(
        val e: Exception
    ) : Response<Nothing>()
}