package okble.central.client.util;


import android.bluetooth.BluetoothDevice;

interface BluetoothDeviceFilter {
    boolean accept(BluetoothDevice device);
}
