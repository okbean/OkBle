package okble.central.client;

public final class Mtu {

    private final int value;

    private Mtu(final int value){
        this.value = value;
    }

    public static Mtu of(final int value){
        return new Mtu(value);
    }

    public int value(){
        return this.value;
    }

    @Override
    public String toString() {
        return "Mtu{" +
                "value=" + value +
                '}';
    }
}
