package okble.demo.ui.device2;

import android.bluetooth.BluetoothGattService;
import android.text.TextUtils;

import java.util.HashMap;

import okble.demo.util.UuidUtils;

public final class ServiceResolver {


    public static String resolveType(BluetoothGattService service){
        final int type = service.getType();
        if(type == BluetoothGattService.SERVICE_TYPE_PRIMARY){
            return "PRIMARY SERVICE";
        }else if(type == BluetoothGattService.SERVICE_TYPE_SECONDARY){
            return "SECONDARY SERVICE";
        }
        return "Unknown Service Type";
    }

    public static String resolveName(BluetoothGattService service){
        final String name = sServices.get(service.getUuid().toString().toLowerCase());
        return TextUtils.isEmpty(name) ?  "Unknown Service" : name;
    }

    public static String resolveUuid(BluetoothGattService service){
        final String val = UuidUtils.getShortUuid(service.getUuid());
        return TextUtils.isEmpty(val) ?  service.getUuid().toString() : ("0x" + val);
    }

    private static HashMap<String, String> sServices = new HashMap<String, String>();
    static {
        sServices.put("00001811-0000-1000-8000-00805f9b34fb", "Alert Notification Service");
        sServices.put("0000180f-0000-1000-8000-00805f9b34fb", "Battery Service");
        sServices.put("00001810-0000-1000-8000-00805f9b34fb", "Blood Pressure");
        sServices.put("00001805-0000-1000-8000-00805f9b34fb", "Current Time Service");
        sServices.put("00001818-0000-1000-8000-00805f9b34fb", "Cycling Power");
        sServices.put("00001816-0000-1000-8000-00805f9b34fb", "Cycling Speed and Cadence");
        sServices.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information");
        sServices.put("00001800-0000-1000-8000-00805f9b34fb", "Generic Access");
        sServices.put("00001801-0000-1000-8000-00805f9b34fb", "Generic Attribute");
        sServices.put("00001808-0000-1000-8000-00805f9b34fb", "Glucose");
        sServices.put("00001809-0000-1000-8000-00805f9b34fb", "Health Thermometer");
        sServices.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate");
        sServices.put("00001812-0000-1000-8000-00805f9b34fb", "Human Interface Device");
        sServices.put("00001802-0000-1000-8000-00805f9b34fb", "Immediate Alert");
        sServices.put("00001803-0000-1000-8000-00805f9b34fb", "Link Loss");
        sServices.put("00001819-0000-1000-8000-00805f9b34fb", "Location and Navigation");
        sServices.put("00001807-0000-1000-8000-00805f9b34fb", "Next DST Change Service");
        sServices.put("0000180e-0000-1000-8000-00805f9b34fb", "Phone Alert Status Service");
        sServices.put("00001806-0000-1000-8000-00805f9b34fb", "Reference Time Update Service");
        sServices.put("00001814-0000-1000-8000-00805f9b34fb", "Running Speed and Cadence");
        sServices.put("00001813-0000-1000-8000-00805f9b34fb", "Scan Parameters");
        sServices.put("00001804-0000-1000-8000-00805f9b34fb", "Tx Power");
    }
}
