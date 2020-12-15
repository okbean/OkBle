package okble.central.client;

import static android.bluetooth.BluetoothDevice.PHY_LE_1M_MASK;
import static android.bluetooth.BluetoothDevice.PHY_LE_2M_MASK;
import static android.bluetooth.BluetoothDevice.PHY_LE_CODED_MASK;

public enum PhyMask {
    LE_1M(PHY_LE_1M_MASK),
    LE_2M(PHY_LE_2M_MASK),
    LE_CODED(PHY_LE_CODED_MASK),

    LE_1M_OR_2M(PHY_LE_1M_MASK | PHY_LE_2M_MASK),
    LE_1M_OR_CODED(PHY_LE_1M_MASK | PHY_LE_CODED_MASK),
    LE_2M_OR_CODED(PHY_LE_2M_MASK | PHY_LE_CODED_MASK),

    LE_1M_OR_2M_OR_CODED(PHY_LE_1M_MASK | PHY_LE_2M_MASK | PHY_LE_CODED_MASK);

    private final int value;
    PhyMask(final int val){
        this.value = val;
    }
    public int value(){
        return this.value;
    }

    public static PhyMask valueOf(final int value){
        final boolean _1M = (PHY_LE_1M_MASK & value) > 0;
        final boolean _2M = (PHY_LE_2M_MASK & value) > 0;
        final boolean _CODED = (PHY_LE_CODED_MASK & value) > 0;

        final int val = (_1M ? PHY_LE_1M_MASK : 0)
                | (_2M ? PHY_LE_2M_MASK : 0)
                | (_CODED ? PHY_LE_CODED_MASK : 0);

        PhyMask ret = null;
        switch (val){
            case (PHY_LE_1M_MASK):
                ret = LE_1M;
                break;

            case (PHY_LE_2M_MASK):
                ret = LE_2M;
                break;

            case (PHY_LE_CODED_MASK):
                ret = LE_CODED;
                break;

            case (PHY_LE_1M_MASK | PHY_LE_2M_MASK):
                ret = LE_1M_OR_2M;
                break;

            case (PHY_LE_1M_MASK | PHY_LE_CODED_MASK):
                ret = LE_1M_OR_CODED;
                break;

            case (PHY_LE_2M_MASK | PHY_LE_CODED_MASK):
                ret = LE_2M_OR_CODED;
                break;

            case (PHY_LE_1M_MASK | PHY_LE_2M_MASK | PHY_LE_CODED_MASK):
                ret = LE_1M_OR_2M_OR_CODED;
                break;
        }
        return ret;
    }
}
