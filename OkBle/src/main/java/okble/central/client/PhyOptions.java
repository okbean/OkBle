package okble.central.client;


import static android.bluetooth.BluetoothDevice.PHY_OPTION_NO_PREFERRED;
import static android.bluetooth.BluetoothDevice.PHY_OPTION_S2;
import static android.bluetooth.BluetoothDevice.PHY_OPTION_S8;

public enum PhyOptions {
    NO_PREFERRED(PHY_OPTION_NO_PREFERRED),
    S2(PHY_OPTION_S2),
    S8(PHY_OPTION_S8);

    private final int value;
    PhyOptions(final int val){
        this.value = val;
    }
    public int value(){
        return this.value;
    }

}
