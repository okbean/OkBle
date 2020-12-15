package okble.central.client;

public final class Rssi {

    private final int value;

    private Rssi(final int value){
        this.value = value;
    }

    public static Rssi of(final int value){
        return new Rssi(value);
    }

    public int value(){
        return this.value;
    }

    @Override
    public String toString() {
        return "Rssi{" +
                "value=" + value +
                '}';
    }
}
