package okble.central.client;

import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_SIGNED;

public enum WriteType {

    WriteDefault(WRITE_TYPE_DEFAULT),
    WriteNoResponse(WRITE_TYPE_NO_RESPONSE),
    WriteSigned(WRITE_TYPE_SIGNED);

    private final int value;
    WriteType(final int val){
        this.value = val;
    }
    public int value(){
        return this.value;
    }
}
