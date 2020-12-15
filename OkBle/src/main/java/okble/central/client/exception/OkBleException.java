package okble.central.client.exception;

public class OkBleException extends Exception{

    public final static int UNDEFINED = -1;

    public final static int GATT_OPERATION_STATUS_FAILED = 1000;
    public final static int GATT_OPERATION_FAILED =     1001;
    public final static int GATT_OPERATION_TIMEOUT =    1002;
    public final static int DATA_RECEIVE_TIMEOUT =      1003;
    public final static int GATT_DISCONNECTED =         1004;

    public final static int SERVICES_NOT_DISCOVERED =   1005;
    public final static int GATT_NOT_FOUND =            1006;

    public final static int GATT_SERVICE_NOT_FOUND =        1007;
    public final static int GATT_CHARACTERISTIC_NOT_FOUND = 1008;
    public final static int GATT_DESCRIPTOR_NOT_FOUND =     1009;


    public final static int CLIENT_RELEASED =           1101;
    public final static int CLIENT_CLOSED =             1102;
    public final static int CLIENT_NOT_CONNECTED =      1103;


    public final static int BLUETOOTH_NOT_ENABLED =     1200;

    public final static int TASK_CANCELED =             1300;

    public final static int CONNECTION_LOCK_ACQUIRE_FAILED = 1400;

    private final int code;


    public OkBleException(final int code) {
        super();
        this.code = code;
    }


    public OkBleException(final String message) {
        super(message);
        this.code = UNDEFINED;
    }

    public OkBleException(final int code , final String message) {
        super(message);
        this.code = code;
    }


    public OkBleException(String message, Throwable cause) {
        super(message, cause);
        this.code = UNDEFINED;
    }
    public OkBleException(final int code ,String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }


    public OkBleException(Throwable cause) {
        super(cause);
        this.code = UNDEFINED;
    }

    public OkBleException(final int code, final Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int code(){
        return this.code;
    }

}
