package okble.central.client;

import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_BALANCED;
import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_HIGH;
import static android.bluetooth.BluetoothGatt.CONNECTION_PRIORITY_LOW_POWER;

public enum ConnectionPriority {
    BALANCED(CONNECTION_PRIORITY_BALANCED),
    HIGH(CONNECTION_PRIORITY_HIGH),
    LOW_POWER(CONNECTION_PRIORITY_LOW_POWER);

    private final int value;
    ConnectionPriority(final int value){
        this.value = value;
    }

    public int value(){
        return this.value;
    }


    public static ConnectionPriority valueOf(final int val){
        for(ConnectionPriority v : values()){
            if(v.value == val){
                return v;
            }
        }
        return null;
    }

}
