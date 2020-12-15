package okble.central.client;

import android.bluetooth.BluetoothGatt;

public final class GattOperationResult {

    private final int status;
    private final BluetoothGatt gatt;
    private final Object data;
    public GattOperationResult(final int status, final BluetoothGatt gatt, final Object data){
        this.status = status;
        this.data = data;
        this.gatt = gatt;
    }

    public int status(){
        return this.status;
    }

    public BluetoothGatt gatt(){
        return this.gatt;
    }

    public <T> T data(){
        return (T)this.data;
    }

    public <T> T data(Class<T> clazz){
        if(clazz.isInstance(this.data)){
            return (T)this.data;
        }
        return null;
    }

    @Override
    public String toString() {
        return "GattOperationResult{" +
                "status=" + status +
                ", data=" + data +
                '}';
    }
}
