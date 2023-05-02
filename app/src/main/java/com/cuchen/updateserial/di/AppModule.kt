package com.cuchen.updateserial.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.cuchen.updateserial.BuildConfig
import com.cuchen.updateserial.data.chat.AndroidBluetoothController
import com.cuchen.updateserial.domin.BluetoothController
import com.cuchen.updateserial.domin.PokeAPI
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideFirebaseStorage() = Firebase.storage

    @Provides
    fun provideFirebaseFirestore() = Firebase.firestore

    /*  @Provides
      @Singleton
      fun provideBluetoothManager(@ApplicationContext app: Context): BluetoothAdapter {
  //        return (app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
          val manager = app.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
          return manager.adapter
      }*/


    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothController {
        return AndroidBluetoothController(context)
    }

    @Singleton
    @Provides
    @Named("API_URI")
    fun provideWebAPI(): String = if (BuildConfig.DEBUG)
        "https://dev.on.cuchen.com"
    else
        "https://on.cuchen.com"

    @Singleton
    @Provides
    fun provideGson(): Gson =
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    @Singleton
    @Provides
    fun provideConverterFactory(
        gson: Gson
    ): Converter.Factory = GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun provideRetrofit(
        @Named("API_URI") apiUrl: String,
        converterFactory: Converter.Factory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(apiUrl)
        .addConverterFactory(converterFactory)
        .build()

    @Singleton
    @Provides
    fun provideGithubService(
        retrofit: Retrofit
    ): PokeAPI = retrofit.create(PokeAPI::class.java)
}