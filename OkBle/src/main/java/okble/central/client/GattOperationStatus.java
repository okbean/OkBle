package okble.central.client;


final class GattOperationStatus {

    private final int value;

    private GattOperationStatus(final int value){
        this.value = value;
    }

    public static GattOperationStatus of(final int value){
        return new GattOperationStatus(value);
    }

    public int value(){
        return this.value;
    }

}
