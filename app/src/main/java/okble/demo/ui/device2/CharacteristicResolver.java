package okble.demo.ui.device2;

import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okble.central.client.util.Hex;
import okble.demo.util.UuidUtils;

public final class CharacteristicResolver {

    public static String resolveName(BluetoothGattCharacteristic characteristic){
        final String name = sCharacteristics.get(characteristic.getUuid().toString().toLowerCase());
        return TextUtils.isEmpty(name) ?  "Unknown Characteristic" : name;
    }

    public static String resolveUuid(BluetoothGattCharacteristic characteristic){
        final String val = UuidUtils.getShortUuid(characteristic.getUuid());
        return TextUtils.isEmpty(val) ?  characteristic.getUuid().toString() : ("0x" + val.toUpperCase());
    }

    public static String resolveProperties(BluetoothGattCharacteristic characteristic){
        final int properties = characteristic.getProperties();
        final List<String> list = new ArrayList<String>(8);
        if((properties & BluetoothGattCharacteristic.PROPERTY_BROADCAST) > 0){
            list.add("BROADCAST");
        }
        if((properties & BluetoothGattCharacteristic.PROPERTY_READ) > 0){
            list.add("READ");
        }
        if((properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0){
            list.add("WRITE_NO_RESPONSE");
        }
        if((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0){
            list.add("WRITE");
        }

        if((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0){
            list.add("NOTIFY");
        }
        if((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0){
            list.add("INDICATE");
        }
        if((properties & BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE) > 0){
            list.add("SIGNED_WRITE");
        }
        if((properties & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) > 0){
            list.add("EXTENDED_PROPS");
        }
        final StringBuffer sb = new StringBuffer(list.size() * 10 + 2);
        final int len = list.size();
        for(int i=0 ;i<len; i++){
            final String val = list.get(i);
            if(i != 0){
                sb.append(", ");
            }
            sb.append(val);
        }
        return sb.toString();
    }

    public static String resolveData(final String uuid, byte[] data){
        if(data != null && data.length > 0){
            return "(0x)" + Hex.toString(data, '-');
        }
        return "";
    }

    private static HashMap<String, String> sCharacteristics = new HashMap<String, String>();
    static {
        sCharacteristics.put("00002a43-0000-1000-8000-00805f9b34fb", "Alert Category ID");
        sCharacteristics.put("00002a42-0000-1000-8000-00805f9b34fb", "Alert Category ID Bit Mask");
        sCharacteristics.put("00002a06-0000-1000-8000-00805f9b34fb", "Alert Level");
        sCharacteristics.put("00002a44-0000-1000-8000-00805f9b34fb", "Alert Notification Control Point");
        sCharacteristics.put("00002a3f-0000-1000-8000-00805f9b34fb", "Alert Status");
        sCharacteristics.put("00002a01-0000-1000-8000-00805f9b34fb", "Appearance");
        sCharacteristics.put("00002a19-0000-1000-8000-00805f9b34fb", "Battery Level");
        sCharacteristics.put("00002a49-0000-1000-8000-00805f9b34fb", "Blood Pressure Feature");
        sCharacteristics.put("00002a35-0000-1000-8000-00805f9b34fb", "Blood Pressure Measurement");
        sCharacteristics.put("00002a38-0000-1000-8000-00805f9b34fb", "Body Sensor Location");
        sCharacteristics.put("00002a22-0000-1000-8000-00805f9b34fb", "Boot Keyboard Input Report");
        sCharacteristics.put("00002a32-0000-1000-8000-00805f9b34fb", "Boot Keyboard Output Report");
        sCharacteristics.put("00002a33-0000-1000-8000-00805f9b34fb", "Boot Mouse Input Report");
        sCharacteristics.put("00002a5c-0000-1000-8000-00805f9b34fb", "CSC Feature");
        sCharacteristics.put("00002a5b-0000-1000-8000-00805f9b34fb", "CSC Measurement");
        sCharacteristics.put("00002a2b-0000-1000-8000-00805f9b34fb", "Current Time");
        sCharacteristics.put("00002a66-0000-1000-8000-00805f9b34fb", "Cycling Power Control Point");
        sCharacteristics.put("00002a65-0000-1000-8000-00805f9b34fb", "Cycling Power Feature");
        sCharacteristics.put("00002a63-0000-1000-8000-00805f9b34fb", "Cycling Power Measurement");
        sCharacteristics.put("00002a64-0000-1000-8000-00805f9b34fb", "Cycling Power Vector");
        sCharacteristics.put("00002a08-0000-1000-8000-00805f9b34fb", "Date Time");
        sCharacteristics.put("00002a0a-0000-1000-8000-00805f9b34fb", "Day Date Time");
        sCharacteristics.put("00002a09-0000-1000-8000-00805f9b34fb", "Day of Week");
        sCharacteristics.put("00002a00-0000-1000-8000-00805f9b34fb", "Device Name");
        sCharacteristics.put("00002a0d-0000-1000-8000-00805f9b34fb", "DST Offset");
        sCharacteristics.put("00002a0c-0000-1000-8000-00805f9b34fb", "Exact Time 256");
        sCharacteristics.put("00002a26-0000-1000-8000-00805f9b34fb", "Firmware Revision String");
        sCharacteristics.put("00002a51-0000-1000-8000-00805f9b34fb", "Glucose Feature");
        sCharacteristics.put("00002a18-0000-1000-8000-00805f9b34fb", "Glucose Measurement");
        sCharacteristics.put("00002a34-0000-1000-8000-00805f9b34fb", "Glucose Measurement Context");
        sCharacteristics.put("00002a27-0000-1000-8000-00805f9b34fb", "Hardware Revision String");
        sCharacteristics.put("00002a39-0000-1000-8000-00805f9b34fb", "Heart Rate Control Point");
        sCharacteristics.put("00002a37-0000-1000-8000-00805f9b34fb", "Heart Rate Measurement");
        sCharacteristics.put("00002a4c-0000-1000-8000-00805f9b34fb", "HID Control Point");
        sCharacteristics.put("00002a4a-0000-1000-8000-00805f9b34fb", "HID Information");
        sCharacteristics.put("00002a2a-0000-1000-8000-00805f9b34fb", "IEEE 11073-20601 Regulatory Certification Data List");
        sCharacteristics.put("00002a36-0000-1000-8000-00805f9b34fb", "Intermediate Cuff Pressure");
        sCharacteristics.put("00002a1e-0000-1000-8000-00805f9b34fb", "Intermediate Temperature");
        sCharacteristics.put("00002a6b-0000-1000-8000-00805f9b34fb", "LN Control Point");
        sCharacteristics.put("00002a6a-0000-1000-8000-00805f9b34fb", "LN Feature");
        sCharacteristics.put("00002a0f-0000-1000-8000-00805f9b34fb", "Local Time Information");
        sCharacteristics.put("00002a67-0000-1000-8000-00805f9b34fb", "Location and Speed");
        sCharacteristics.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        sCharacteristics.put("00002a21-0000-1000-8000-00805f9b34fb", "Measurement Interval");
        sCharacteristics.put("00002a24-0000-1000-8000-00805f9b34fb", "Model Number String");
        sCharacteristics.put("00002a68-0000-1000-8000-00805f9b34fb", "Navigation");
        sCharacteristics.put("00002a46-0000-1000-8000-00805f9b34fb", "New Alert");
        sCharacteristics.put("00002a04-0000-1000-8000-00805f9b34fb", "Peripheral Preferred Connection Parameters");
        sCharacteristics.put("00002a02-0000-1000-8000-00805f9b34fb", "Peripheral Privacy Flag");
        sCharacteristics.put("00002a50-0000-1000-8000-00805f9b34fb", "PnP ID");
        sCharacteristics.put("00002a69-0000-1000-8000-00805f9b34fb", "Position Quality");
        sCharacteristics.put("00002a4e-0000-1000-8000-00805f9b34fb", "Protocol Mode");
        sCharacteristics.put("00002a03-0000-1000-8000-00805f9b34fb", "Reconnection Address");
        sCharacteristics.put("00002a52-0000-1000-8000-00805f9b34fb", "Record Access Control Point");
        sCharacteristics.put("00002a14-0000-1000-8000-00805f9b34fb", "Reference Time Information");
        sCharacteristics.put("00002a4d-0000-1000-8000-00805f9b34fb", "Report");
        sCharacteristics.put("00002a4b-0000-1000-8000-00805f9b34fb", "Report Map");
        sCharacteristics.put("00002a40-0000-1000-8000-00805f9b34fb", "Ringer Control Point");
        sCharacteristics.put("00002a41-0000-1000-8000-00805f9b34fb", "Ringer Setting");
        sCharacteristics.put("00002a54-0000-1000-8000-00805f9b34fb", "RSC Feature");
        sCharacteristics.put("00002a53-0000-1000-8000-00805f9b34fb", "RSC Measurement");
        sCharacteristics.put("00002a55-0000-1000-8000-00805f9b34fb", "SC Control Point");
        sCharacteristics.put("00002a4f-0000-1000-8000-00805f9b34fb", "Scan Interval Window");
        sCharacteristics.put("00002a31-0000-1000-8000-00805f9b34fb", "Scan Refresh");
        sCharacteristics.put("00002a5d-0000-1000-8000-00805f9b34fb", "Sensor Location");
        sCharacteristics.put("00002a25-0000-1000-8000-00805f9b34fb", "Serial Number String");
        sCharacteristics.put("00002a05-0000-1000-8000-00805f9b34fb", "Service Changed");
        sCharacteristics.put("00002a28-0000-1000-8000-00805f9b34fb", "Software Revision String");
        sCharacteristics.put("00002a47-0000-1000-8000-00805f9b34fb", "Supported New Alert Category");
        sCharacteristics.put("00002a48-0000-1000-8000-00805f9b34fb", "Supported Unread Alert Category");
        sCharacteristics.put("00002a23-0000-1000-8000-00805f9b34fb", "System ID");
        sCharacteristics.put("00002a1c-0000-1000-8000-00805f9b34fb", "Temperature Measurement");
        sCharacteristics.put("00002a1d-0000-1000-8000-00805f9b34fb", "Temperature Type");
        sCharacteristics.put("00002a12-0000-1000-8000-00805f9b34fb", "Time Accuracy");
        sCharacteristics.put("00002a13-0000-1000-8000-00805f9b34fb", "Time Source");
        sCharacteristics.put("00002a16-0000-1000-8000-00805f9b34fb", "Time Update Control Point");
        sCharacteristics.put("00002a17-0000-1000-8000-00805f9b34fb", "Time Update State");
        sCharacteristics.put("00002a11-0000-1000-8000-00805f9b34fb", "Time with DST");
        sCharacteristics.put("00002a0e-0000-1000-8000-00805f9b34fb", "Time Zone");
        sCharacteristics.put("00002a07-0000-1000-8000-00805f9b34fb", "Tx Power Level");
        sCharacteristics.put("00002a45-0000-1000-8000-00805f9b34fb", "Unread Alert Status");
    }
}
