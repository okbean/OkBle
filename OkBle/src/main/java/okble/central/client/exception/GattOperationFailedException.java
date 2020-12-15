package okble.central.client.exception;

public class GattOperationFailedException extends OkBleException {

    public GattOperationFailedException() {
        super(GATT_OPERATION_FAILED);
    }

    public GattOperationFailedException(String message) {
        super(GATT_OPERATION_FAILED,message);
    }

    public GattOperationFailedException(String message, Throwable cause) {
        super(GATT_OPERATION_FAILED,message, cause);
    }

    public GattOperationFailedException(Throwable cause) {
        super(GATT_OPERATION_FAILED, cause);
    }
}
