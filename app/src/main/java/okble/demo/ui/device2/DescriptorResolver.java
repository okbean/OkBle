package okble.demo.ui.device2;

import android.bluetooth.BluetoothGattDescriptor;
import android.text.TextUtils;

import java.util.HashMap;

import okble.central.client.util.Hex;
import okble.demo.util.UuidUtils;

public final class DescriptorResolver {

    public static String resolveName(BluetoothGattDescriptor descriptor){
        final String name = sDescriptors.get(descriptor.getUuid().toString().toLowerCase());
        return TextUtils.isEmpty(name) ?  "Unknown Descriptor" : name;
    }

    public static String resolveUuid(BluetoothGattDescriptor descriptor){
        final String val = UuidUtils.getShortUuid(descriptor.getUuid());
        return TextUtils.isEmpty(val) ?  descriptor.getUuid().toString() : ("0x" + val.toUpperCase());
    }




    public static String resolveData(final String uuid, byte[] data){
        if(data != null && data.length > 0){
            return "(0x)" + Hex.toString(data, '-');
        }
        return "";
    }


    private static HashMap<String, String> sDescriptors = new HashMap<String, String>();
    static {
        sDescriptors.put("00002900-0000-1000-8000-00805f9b34fb", "Characteristic Extended Properties");
        sDescriptors.put("00002901-0000-1000-8000-00805f9b34fb", "Characteristic User Description");
        sDescriptors.put("00002902-0000-1000-8000-00805f9b34fb", "Client Characteristic Configuration");
        sDescriptors.put("00002903-0000-1000-8000-00805f9b34fb", "Server Characteristic Configuration");
        sDescriptors.put("00002904-0000-1000-8000-00805f9b34fb", "Characteristic Presentation Format");
        sDescriptors.put("00002905-0000-1000-8000-00805f9b34fb", "Characteristic Aggregate Format");
        sDescriptors.put("00002906-0000-1000-8000-00805f9b34fb", "Valid Range");
        sDescriptors.put("00002907-0000-1000-8000-00805f9b34fb", "External Report Reference Descriptor");
        sDescriptors.put("00002908-0000-1000-8000-00805f9b34fb", "Report Reference Descriptor");
    }
}
