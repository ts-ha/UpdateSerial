package com.cuchen.updateserial.data.chat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.cuchen.updateserial.domin.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}