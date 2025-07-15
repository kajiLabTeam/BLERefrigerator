package com.example.blerefrigerator

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi

class GetBLE {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private var scanCallback: ScanCallback? = null

    @OptIn(UnstableApi::class)
    @SuppressLint("MissingPermission")
    fun startScan() {
        var count = 0.0
        bluetoothLeScanner?.let {
            if (scanCallback == null) {
                scanCallback = object : ScanCallback() {
                    @OptIn(UnstableApi::class)
                    override fun onScanResult(callbackType: Int, result: ScanResult) {
                        super.onScanResult(callbackType, result)
                        val device: BluetoothDevice = result.device
                        val uuids = result.scanRecord?.serviceUuids
                        val receiveRssi = result.rssi
                        uuids?.forEach { _ ->
                            val address = device.address
                            // 冷蔵庫に入れたビーコンのMACアドレスをここに入力
                            if (address == "DC:0D:30:15:9A:D0") {  //"DC:0D:30:15:9A:AF""48:31:77:51:5A:1A"
                                deviceRSSI.value = receiveRssi
                                if (receiveRssi > threshold) {
                                    if (count < 5) {
                                        count += 1.5
                                    } else
                                        if (count >= 5.0) {
                                            door.value = true
                                        }
                                } else {
                                    if (count > 0) {
                                        count -= 1.5
                                    } else if (count <= 0) {
                                        door.value = false
                                        count = 0.0
                                    }
                                }
                            }
                        }
                    }

                    @OptIn(UnstableApi::class)
                    override fun onScanFailed(errorCode: Int) {
                        super.onScanFailed(errorCode)
                    }
                }
                // スキャンのセッティング
                val scanSettings = ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build()

                //val mUuid = UUID.fromString("138c35b6-0000-1000-8000-00805f9b34fb") // ここにサービスのUUIDを設定"0000fef5-0000-1000-8000-00805f9b34fb"
                // ↑がなくてもたぶん動くので今回は不使用

                val scanFilters = mutableListOf<ScanFilter>()
                val filter = ScanFilter.Builder()
                    .setServiceUuid(null)   //ParcelUuid(mUuid)　同上
                    .build()
                scanFilters.add(filter)

                bluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback)
            }
        }
    }


    @OptIn(UnstableApi::class)
    @SuppressLint("MissingPermission")
    fun stopScan() {
        scanCallback?.let { bluetoothLeScanner?.stopScan(it) }
        scanCallback = null
    }
}