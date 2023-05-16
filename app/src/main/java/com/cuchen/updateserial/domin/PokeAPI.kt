package com.cuchen.updateserial.domin

import retrofit2.http.*

interface PokeAPI {
   /* @GET("pokemon/")
    suspend fun getPokemons(): Response

    @POST("smartcooking/api/updateSerial.action")
    suspend fun updateSerial(
        @Field("macAddr") macAddr: String,
        @Field("deviceType") deviceType: String,
        @Field("serialNo") serialNo: String
    ): Response

    @GET("pokemon/")
    suspend fun getPokemons(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response*/

    @GET("pokemon/{pid}/")
    suspend fun getPokemon(@Path("pid") pid: Int): PokemonResponse
}

/*data class Response(
    val count: Int?,
    val previous: String?,
    val next: String?,
    val success: Boolean,
    val errors: List<ErrorResult>?,
    val successBen: SuccessBen?,
    val results: List<Result>?
) {
    data class SuccessBen(
        val count: Int,
        val macList: List<String>
    )

    data class ErrorResult(
        val code: String,
        val field: String,
        val messaeg: String
    )

    data class Result(
        val url: String,
        val name: String
    )
}*/

data class PokemonResponse(
    val species: Species,
    val sprites: Sprites
) {
    data class Species(var name: String)

    data class Sprites(var frontDefault: String)
}

