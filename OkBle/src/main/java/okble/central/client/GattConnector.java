package okble.central.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Context;


public interface GattConnector {

    BluetoothGatt connectGatt(BluetoothDevice device, Context ctx, BluetoothGattCallback callback);

    interface Factory{

        GattConnector newGattConnector();

        Factory DEFAULT = new Factory(){

            @Override
            public GattConnector newGattConnector() {
                return new DefaultGattConnector();
            }
        };
    }

}
