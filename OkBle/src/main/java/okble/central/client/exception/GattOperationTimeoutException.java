package okble.central.client.exception;

public class GattOperationTimeoutException extends OkBleException {

    public GattOperationTimeoutException() {
        super(GATT_OPERATION_TIMEOUT);
    }

    public GattOperationTimeoutException(String message) {
        super(GATT_OPERATION_TIMEOUT, message);
    }

}
