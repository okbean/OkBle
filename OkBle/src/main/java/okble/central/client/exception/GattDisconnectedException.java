package okble.central.client.exception;


public class GattDisconnectedException extends OkBleException{

    public GattDisconnectedException(final int status, final int state) {
        super(GATT_DISCONNECTED);
        this.status = status;
        this.state = state;
    }

    public GattDisconnectedException(final int status,final int state, final String message) {
        super(GATT_DISCONNECTED,message);
        this.status = status;
        this.state = state;
    }

    private final int state;

    public int state(){
        return this.state;
    }

    private final int status;

    public int status(){
        return this.status;
    }

}