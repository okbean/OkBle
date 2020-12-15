package okble.central.client.exception;


public class GattStatusException extends OkBleException{

    public GattStatusException(final int status) {
        super(GATT_OPERATION_STATUS_FAILED);
        this.status = status;
    }

    public GattStatusException(final int status, final String message) {
        super(GATT_OPERATION_STATUS_FAILED, message);
        this.status = status;
    }

    private final int status;

    public int status(){
        return this.status;
    }

}
