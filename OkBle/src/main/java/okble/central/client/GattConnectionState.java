package okble.central.client;


final class GattConnectionState {

    private final int value;

    private GattConnectionState(final int value){
        this.value = value;
    }

    public static GattConnectionState of(final int value){
        return new GattConnectionState(value);
    }

    public int value(){
        return this.value;
    }

}
