package okble.central.client.util;

import android.bluetooth.BluetoothGatt;

import java.lang.reflect.Method;

public final class GattUtils {

    public static void safeDisconnect(final BluetoothGatt gatt){
        if(gatt != null){
            try{
                gatt.disconnect();
            }catch (Exception ex){

            }
        }
    }

    public static void safeClose(final BluetoothGatt gatt){
        if(gatt != null){
            try{
                gatt.close();
            }catch (Exception ex){

            }
        }
    }


    public static boolean refresh(final BluetoothGatt gatt){
        try{
            final Method method = refreshMethod();
            final boolean val = (boolean)method.invoke(gatt);
            return val;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }


    private static Method sRefreshMethod = null;
    private static Method refreshMethod() throws NoSuchMethodException {
        if(sRefreshMethod == null){
            final Method method = BluetoothGatt.class.getMethod("refresh");
            sRefreshMethod = method;
        }
        return sRefreshMethod;
    }




    public static boolean requestLeConnectionUpdate(final BluetoothGatt gatt,
                                                    final int minConnectionInterval, final int maxConnectionInterval,
                                                    final int slaveLatency, final int supervisionTimeout,
                                                    final int minConnectionEventLen, final int maxConnectionEventLen){
        try{
            final Method method = requestLeConnectionUpdateMethod();
            final boolean val = (boolean)method.invoke(gatt,
                    minConnectionInterval, maxConnectionInterval,
                    slaveLatency, supervisionTimeout,
                    minConnectionEventLen, maxConnectionEventLen);
            return val;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }


    private static Method sRequestLeConnectionUpdateMethod = null;
    private static Method requestLeConnectionUpdateMethod() throws NoSuchMethodException {
        if(sRequestLeConnectionUpdateMethod == null){
            final Class<?>[]  clazzArr = {int.class,int.class,int.class,int.class,int.class,int.class};
            final Method method = BluetoothGatt.class.getMethod("requestLeConnectionUpdate", clazzArr);
            sRequestLeConnectionUpdateMethod = method;
        }
        return sRequestLeConnectionUpdateMethod;
    }


    private GattUtils(){}
}
