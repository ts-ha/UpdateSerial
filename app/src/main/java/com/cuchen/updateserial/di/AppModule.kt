package com.cuchen.updateserial.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.cuchen.updateserial.data.chat.AndroidBluetoothController
import com.cuchen.updateserial.domin.BluetoothController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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


//    @Provides
//    @Singleton
//    fun provideTempHumidityReceiveManager(
//        @ApplicationContext context: Context, bluetoothAdapter: BluetoothAdapter
//    ): BleManager {
//        return BleManager(bluetoothAdapter, context)
//    }

//    @Provides
//    fun provideProfileImageRepository(
//        storage: FirebaseStorage,
//        db: FirebaseFirestore
//    ): ProfileImageRepository = ProfileImageRepositoryImpl(
//        storage = storage,
//        db = db
//    )
}