package okble.central.client.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.lang.reflect.Method;
import java.util.List;


public final class BluetoothUtils {

    public static BluetoothDevice toBluetoothDevice(final String address){
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        final boolean validate = BluetoothAdapter.checkBluetoothAddress(address);
        BluetoothDevice val = null;
        if(validate){
            val = adapter.getRemoteDevice(address);
        }
        return val;
    }


    public static boolean isBluetoothEnabled(){
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        final boolean val = adapter != null && adapter.isEnabled();
        return val;
    }


    public static boolean createBond(final BluetoothDevice device){
        final int state = device.getBondState();
        if(state == BluetoothDevice.BOND_BONDED){
            return true;
        }
        final boolean val = device.createBond();
        return val;
    }


    public static boolean removeBond(final BluetoothDevice device){
        final int state = device.getBondState();
        if(state == BluetoothDevice.BOND_NONE){
            return true;
        }
        try {
            final Method method = removeBondMethod();
            return (boolean)method.invoke(device);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void removeBond(List<BluetoothDevice> devices){
        if(devices != null){
            for(BluetoothDevice device : devices){
                removeBond(device);
            }
        }
    }


    private static Method sRemoveBondMethod = null;
    private static Method removeBondMethod() throws NoSuchMethodException {
        if(sRemoveBondMethod == null){
            final Method method = BluetoothDevice.class.getMethod("removeBond");
            sRemoveBondMethod = method;
        }
        return sRemoveBondMethod;
    }


    private BluetoothUtils(){}
}
