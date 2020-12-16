package okble.central.client;

public final class OkBleEvent {

    public static final int CONNECTION_STATE_CONNECTED  = 1;
    public static final int CONNECTION_STATE_DISCONNECTED  = 2;

    public static final int DISCOVER_SERVICES = 3;

    public static final int SET_PREFERRED_PHY  = 4;
    public static final int READ_PHY = 5;



    public static final int READ_CHARACTERISTIC  = 6;
    public static final int WRITE_CHARACTERISTIC  = 7;

    public static final int CHARACTERISTIC_CHANGED = 8;

    public static final int READ_DESCRIPTOR = 9;
    public static final int WRITE_DESCRIPTOR = 10;

    public static final int READ_REMOTE_RSSI = 11;

    public static final int SET_MTU = 12;

    public static final int SET_CONNECTION_PRIORITY = 13;
    public static final int UPDATED_CONNECTION_PARAMETER = 14;

    public static final int CLIENT_CLOSE = 15;
    public static final int CLIENT_RELEASE = 16;
    public static final int BLUETOOTH_DISABLED = 17;

    private final int value;
    private final Object data;
    private final long time;



    private OkBleEvent(final int value, final Object data){
        this.value = value;
        this.data = data;
        this.time = System.currentTimeMillis();
    }

    public int value(){
        return this.value;
    }

    public long time(){
        return this.time;
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


//    public static OkBleEvent clientClosed(){
//        return new OkBleEvent(CLIENT_CLOSE, null);
//    }
//    public static OkBleEvent clientReleased(){
//        return new OkBleEvent(CLIENT_RELEASE, null);
//    }
    //    public static OkBleEvent bluetoothDisabled(){
//        return new OkBleEvent(BLUETOOTH_DISABLED, null);
//    }

    public static OkBleEvent connectionStateConnected(final GattOperationResult data){
        return new OkBleEvent(CONNECTION_STATE_CONNECTED, data);
    }

    public static OkBleEvent connectionStateDisconnected(final GattOperationResult data){
        return new OkBleEvent(CONNECTION_STATE_DISCONNECTED, data);
    }

    public static OkBleEvent readPhy(final GattOperationResult data){
        return new OkBleEvent(READ_PHY, data);
    }
    public static OkBleEvent setPreferredPhy(final GattOperationResult data){
        return new OkBleEvent(SET_PREFERRED_PHY, data);
    }

    public static OkBleEvent discoverServices(final GattOperationResult data){
        return new OkBleEvent(DISCOVER_SERVICES, data);
    }


    public static OkBleEvent writeCharacteristic(final GattOperationResult data){
        return new OkBleEvent(WRITE_CHARACTERISTIC, data);
    }
    public static OkBleEvent readCharacteristic(final GattOperationResult data){
        return new OkBleEvent(READ_CHARACTERISTIC, data);
    }


    public static OkBleEvent characteristicChanged(final GattOperationResult data){
        return new OkBleEvent(CHARACTERISTIC_CHANGED, data);
    }

    public static OkBleEvent writeDescriptor(final GattOperationResult data){
        return new OkBleEvent(WRITE_DESCRIPTOR, data);
    }
    public static OkBleEvent readDescriptor(final GattOperationResult data){
        return new OkBleEvent(READ_DESCRIPTOR, data);
    }


    public static OkBleEvent readRemoteRssi(final GattOperationResult data){
        return new OkBleEvent(READ_REMOTE_RSSI, data);
    }

    public static OkBleEvent setMtu(final GattOperationResult data){
        return new OkBleEvent(SET_MTU, data);
    }

    public static OkBleEvent setConnectionPriority(final GattOperationResult data){
        return new OkBleEvent(SET_CONNECTION_PRIORITY, data);
    }

//    public static OkBleEvent updateConnectionParameter(final GattOperationResult data){
//        return new OkBleEvent(UPDATED_CONNECTION_PARAMETER, data);
//    }



    @Override
    public String toString() {
        return "OkBleEvent{" +
                "value=" + value +
                ", data=" + data +
                ", time=" + time +
                '}';
    }
}
