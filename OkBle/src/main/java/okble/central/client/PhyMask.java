package okble.central.client;

import java.util.ArrayList;

import static android.bluetooth.BluetoothDevice.PHY_LE_1M_MASK;
import static android.bluetooth.BluetoothDevice.PHY_LE_2M_MASK;
import static android.bluetooth.BluetoothDevice.PHY_LE_CODED_MASK;

public enum PhyMask {
    LE_1M(PHY_LE_1M_MASK),
    LE_2M(PHY_LE_2M_MASK),
    LE_CODED(PHY_LE_CODED_MASK);

    private final int value;
    PhyMask(final int val){
        this.value = val;
    }
    public int value(){
        return this.value;
    }

    public static int value(PhyMask... masks){
        int val = 0;
        final int len = masks == null ? 0 : masks.length;
        if(len > 0){
            for(PhyMask item : masks){
                if(item != null){
                    val = val | item.value;
                }
            }
        }
        return val;
    }


    public static PhyMask[] valueOf(final int value){
        final boolean _1M = (PHY_LE_1M_MASK & value) > 0;
        final boolean _2M = (PHY_LE_2M_MASK & value) > 0;
        final boolean _CODED = (PHY_LE_CODED_MASK & value) > 0;
        final ArrayList<PhyMask> list = new ArrayList<PhyMask>(3);
        if(_1M){
            list.add(LE_1M);
        }
        if(_2M){
            list.add(LE_2M);
        }
        if(_CODED){
            list.add(LE_CODED);
        }
        return (PhyMask[])list.toArray();
    }

}
