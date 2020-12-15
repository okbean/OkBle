package okble.central.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;

final class DefaultGattConnector implements GattConnector{

    public DefaultGattConnector(){

    }

    @Override
    public BluetoothGatt connectGatt(BluetoothDevice device, Context context, BluetoothGattCallback callback) {

        return device.connectGatt(context, false, callback);
    }
}
