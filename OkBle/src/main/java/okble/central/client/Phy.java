package okble.central.client;

public final class Phy {

    private final int tx;
    private final int rx;

    private Phy(final int tx, final int rx){
        this.rx = rx;
        this.tx = tx;
    }

    public static Phy of(final int tx, final int rx){
        return new Phy(rx, tx);
    }

    public int tx() {
        return tx;
    }

    public int rx() {
        return rx;
    }

    @Override
    public String toString() {
        return "Phy{" +
                "tx=" + tx +
                ", rx=" + rx +
                '}';
    }
}
