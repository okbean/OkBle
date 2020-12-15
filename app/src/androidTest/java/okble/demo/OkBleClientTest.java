package okble.demo;

import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.UUID;

import okble.central.client.CharacteristicData;
import okble.central.client.ConnectionPriority;
import okble.central.client.ConnectionState;
import okble.central.client.Mtu;
import okble.central.client.NotificationType;
import okble.central.client.OkBleClient;
import okble.central.client.OkBleTask;
import okble.central.client.OnConnectionStateChangedListener;
import okble.central.client.OnDataReceivedListener;
import okble.central.client.Phy;
import okble.central.client.PhyMask;
import okble.central.client.PhyOptions;
import okble.central.client.Priority;
import okble.central.client.Rssi;
import okble.central.client.WriteType;
import okble.central.client.request.ConnectionRequest;
import okble.central.client.request.DisableNotificationRequest;
import okble.central.client.request.EnableNotificationRequest;
import okble.central.client.request.ReadCharacteristicRequest;
import okble.central.client.request.ReadDescriptorRequest;
import okble.central.client.request.ReadPhyRequest;
import okble.central.client.request.ReadRemoteRssiRequest;
import okble.central.client.request.SetMtuRequest;
import okble.central.client.request.SetPreferredPhyRequest;
import okble.central.client.request.SetConnectionPriorityRequest;
import okble.central.client.request.WriteCharacteristicForResponseRequest;
import okble.central.client.request.WriteCharacteristicRequest;
import okble.central.client.request.WriteDescriptorRequest;
import okble.central.client.util.Hex;
import okble.demo.util.OnePacketReceiverFactory;
import okble.demo.util.UuidUtils;

import static android.bluetooth.BluetoothDevice.PHY_LE_1M_MASK;
import static android.bluetooth.BluetoothDevice.PHY_LE_2M_MASK;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.JVM)
public class OkBleClientTest {
    private final static String TAG = ":" + OkBleClientTest.class.getSimpleName();

    private final static String MY_DEVICE_ADDRESS = "4C:59:9A:28:92:0A";
    private final static String HUAWEI_DEVICE_ADDRESS = "20:DA:22:1D:64:A0";
    private final static String TESTED_DEVICE_ADDRESS = MY_DEVICE_ADDRESS;

    private static OkBleClient sClient = null;
    @Before
    public void init(){
        if(sClient == null){
            Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            sClient = new OkBleClient.Builder()
                    .context(appContext)
                    .debuggable(true)
                    .device(TESTED_DEVICE_ADDRESS)
                    .build();
            sClient.addOnDataReceivedListener(new OnDataReceivedListener(){
                @Override
                public void onDataReceived(OkBleClient client, CharacteristicData data) {
                    Log.i(TAG, "onDataReceived: (0x) " + Hex.toString(data.data(), '-'));
                }
            });
            sClient.addOnConnectionStateChangedListener(new OnConnectionStateChangedListener(){
                @Override
                public void onConnectionStateChanged(OkBleClient client, ConnectionState newState, ConnectionState lastState) {
                    Log.i(TAG, "onConnectionStateChanged." + "newState:" + newState.toString() + " lastState:" + lastState.toString());

                }
            });
            sClient.connect();
        }
    }

    @Test
    public void testConnection(){
        final ConnectionRequest req = new ConnectionRequest.Builder().build();
        final OkBleTask<Void> task = sClient.newTask(req);
        final long start = System.currentTimeMillis();
        task.enqueue().await();
        final long end = System.currentTimeMillis();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        Log.i(TAG, "testConnection success!" + " Complete in " + (end-start) + " millis!");
    }




    @Test
    public void testReadCharacteristic_for_device_name(){
        final UUID service = UuidUtils.fromShortHex("1800");
        final UUID characteristic =  UuidUtils.fromShortHex("2a00");
        final ReadCharacteristicRequest req = new ReadCharacteristicRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .build();
        final OkBleTask<byte[]> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        final byte[] data = task.getResult();
        final String val = new String(data);
        assertTrue((data != null));
        assertTrue(val.length() > 0);
        Log.i(TAG, "testReadCharacteristic_for_device_name success! device name:" + val);
    }

    @Test
    public void testReadCharacteristic_for_device_appearance(){
        final UUID service = UuidUtils.fromShortHex("1800");
        final UUID characteristic =  UuidUtils.fromShortHex("2a01");
        final ReadCharacteristicRequest req = new ReadCharacteristicRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .build();
        final OkBleTask<byte[]> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        final byte[] data = task.getResult();
        assertTrue((data != null));
        Log.i(TAG, "testReadCharacteristic_for_device_appearance success! device appearance:(0x)" + Hex.toString(data, '-'));
    }



    @Test
    public void testWriteCharacteristic_for_huawei_device(){
        final UUID service = UuidUtils.fromShortHex("fe86");
        final UUID characteristic =  UuidUtils.fromShortHex("fe01");
        final WriteCharacteristicRequest req = new WriteCharacteristicRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .writeType(WriteType.WriteNoResponse)
                .packetSenderFactory("testWriteReadCharacteristic_for_huawei_device".getBytes(), 20)
                .build();
        final OkBleTask<Void> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        Log.i(TAG, "testWriteCharacteristic_for_huawei_device success!");

    }



    @Test
    public void testWriteCharacteristic_for_my_device(){
        final UUID service = UuidUtils.fromShortHex("6006");
        final UUID characteristic =  UuidUtils.fromShortHex("8001");

        final WriteCharacteristicRequest req = new WriteCharacteristicRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .writeType(WriteType.WriteNoResponse)
                .packetSenderFactory(
                                new byte[]{(byte)0x6F, (byte)0x0C, (byte)0x71,(byte)0x01, (byte)0x00,  (byte)0x00 , (byte)0x8F},
                                20)
                .build();
        final OkBleTask<Void> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        Log.i(TAG, "testWriteCharacteristic_for_my_device success!");

    }



    @Test
    public void testWriteCharacteristicForResponse_for_my_device(){

        final UUID service = UuidUtils.fromShortHex("6006");
        final UUID characteristic =  UuidUtils.fromShortHex("8001");

        final UUID responseService = UuidUtils.fromShortHex("6006");
        final UUID responseCharacteristic =  UuidUtils.fromShortHex("8002");

        final boolean success = enableNotification(responseService, responseCharacteristic);
        assertTrue(success);

        final WriteCharacteristicForResponseRequest<byte[]> req = new WriteCharacteristicForResponseRequest.Builder<byte[]>()
                .service(service)
                .characteristic(characteristic)
                .responseService(responseService)
                .responseCharacteristic(responseCharacteristic)
                .writeType(WriteType.WriteNoResponse)
                .packetSenderFactory(
                                new byte[]{(byte)0x6F, (byte)0x0C, (byte)0x71,(byte)0x01, (byte)0x00,  (byte)0x00 , (byte)0x8F},
                                20)
                .packetReceiverFactory(new OnePacketReceiverFactory())
                .build();
        final OkBleTask<byte[]> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        final byte[] data = task.getResult();
        assertTrue(data != null && data.length > 0 );
        final String val = Hex.toString(data, '-');
        Log.i(TAG, "testWriteCharacteristicForResponse_for_my_device success! data:(0x)" + val);
    }




    private boolean enableNotification(final UUID service, final UUID characteristic){
        final EnableNotificationRequest req = new EnableNotificationRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .notificationType(NotificationType.Notification)
                .build();
        final OkBleTask<Void> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        return task.isSuccess();
    }


    @Test
    public void testDisableNotification(){
        final UUID service = UuidUtils.fromShortHex("1801");
        final UUID characteristic =  UuidUtils.fromShortHex("2a05");
        final DisableNotificationRequest req = new DisableNotificationRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .build();
        final OkBleTask<Void> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());


        final UUID service2 = UuidUtils.fromShortHex("1801");
        final UUID characteristic2 =  UuidUtils.fromShortHex("2a05");
        final UUID descriptor2 =  UuidUtils.fromShortHex("2902");
        final ReadDescriptorRequest req2 = new ReadDescriptorRequest.Builder()
                .service(service2)
                .characteristic(characteristic2)
                .descriptor(descriptor2)
                .build();
        final OkBleTask<byte[]> task2 = sClient.newTask(req2);
        byte[] data = null;
        try{
            data = task2.execute();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        tryPrintStackTrace(task2);
        assertTrue(task2.isSuccess());
        assertTrue((data != null));
        assertArrayEquals(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE, data);
        Log.i(TAG, "testDisableNotification success! value:" + Hex.toString(data, '-'));


    }

    @Test
    public void testEnableIndication(){
        final UUID service = UuidUtils.fromShortHex("1801");
        final UUID characteristic =  UuidUtils.fromShortHex("2a05");
        final EnableNotificationRequest req = new EnableNotificationRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .notificationType(NotificationType.Indication)
                .build();
        final OkBleTask<Void> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());


        final UUID service2 = UuidUtils.fromShortHex("1801");
        final UUID characteristic2 =  UuidUtils.fromShortHex("2a05");
        final UUID descriptor2 =  UuidUtils.fromShortHex("2902");
        final ReadDescriptorRequest req2 = new ReadDescriptorRequest.Builder()
                .service(service2)
                .characteristic(characteristic2)
                .descriptor(descriptor2)
                .build();
        final OkBleTask<byte[]> task2 = sClient.newTask(req2);
        task2.enqueue().await();
        tryPrintStackTrace(task2);
        assertTrue(task2.isSuccess());
        final byte[] data = task2.getResult();
        assertTrue((data != null));
        assertArrayEquals(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, data);
        Log.i(TAG, "testEnableIndication success! value:" + Hex.toString(data, '-'));


    }


    @Test
    public void testReadDescriptor(){
        final UUID service = UuidUtils.fromShortHex("1801");
        final UUID characteristic =  UuidUtils.fromShortHex("2a05");
        final UUID descriptor =  UuidUtils.fromShortHex("2902");
        final ReadDescriptorRequest req = new ReadDescriptorRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .descriptor(descriptor)
                .build();
        final OkBleTask<byte[]> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        final byte[] data = task.getResult();
        assertTrue((data != null));
        Log.i(TAG, "testReadDescriptor success! value:" + Hex.toString(data, '-'));
    }


    @Test
    public void testWriteDescriptor(){
        final UUID service = UuidUtils.fromShortHex("1801");
        final UUID characteristic =  UuidUtils.fromShortHex("2a05");
        final UUID descriptor =  UuidUtils.fromShortHex("2902");
        final WriteDescriptorRequest req = new WriteDescriptorRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .descriptor(descriptor)
                .packetSenderFactory(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE)
                .build();
        final OkBleTask<Void> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        Log.i(TAG, "testWriteDescriptor success! " );
    }


    @Test
    public void testSetMtu(){
        final SetMtuRequest req = new SetMtuRequest.Builder()
                .mtu(512).build();
        final OkBleTask<Mtu> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        final Mtu val = task.getResult();
        Log.i(TAG, "testSetMtu success! Mtu:" + val);

    }


    @Test
    public void setConnectionPriority(){
        final SetConnectionPriorityRequest req = new SetConnectionPriorityRequest.Builder()
                .connectionPriority(ConnectionPriority.HIGH)
                .build();
        final OkBleTask<Void> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        Log.i(TAG, "testUpdateConnectionPriority success!");
    }


    @Test
    public void testReadRemoteRssi(){
        final ReadRemoteRssiRequest req = new ReadRemoteRssiRequest.Builder()
                .build();
        final OkBleTask<Rssi> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        final Rssi val = task.getResult();

        Log.i(TAG, "testReadRemoteRssi success! Rssi:" + val);
    }


    @Test
    public void testReadPhy(){
        final ReadPhyRequest req = new ReadPhyRequest.Builder()
                .build();
        final OkBleTask<Phy> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        final Phy val = task.getResult();
        Log.i(TAG, "testReadPhy success! Phy:" + val);
    }


    @Test
    public void testSetPreferredPhy(){
        final SetPreferredPhyRequest req = new SetPreferredPhyRequest.Builder()
                .phyOptions(PhyOptions.S2)
                .rxPhy(PhyMask.LE_1M)
                .txPhy(PhyMask.LE_2M)
                .build();
        final OkBleTask<Phy> task = sClient.newTask(req);
        task.enqueue().await();
        tryPrintStackTrace(task);
        assertTrue(task.isSuccess());
        final Phy val = task.getResult();
        Log.i(TAG, "testSetPreferredPhy success! Phy:" + val);
    }


    @Test
    public void testPhyMask(){
        PhyMask val0 = PhyMask.valueOf(7);
        assertTrue(val0 == PhyMask.LE_1M_OR_2M_OR_CODED);

        PhyMask val1 = PhyMask.valueOf(PHY_LE_1M_MASK);
        assertTrue(val1 == PhyMask.LE_1M);

        PhyMask val2 = PhyMask.valueOf(PHY_LE_1M_MASK | PHY_LE_2M_MASK);
        assertTrue(val2 == PhyMask.LE_1M_OR_2M);

        PhyMask val3 = PhyMask.valueOf(0);
        assertTrue(val3 == null);
    }




    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("okble.demo", appContext.getPackageName());
    }

    private void doConnect0(){
        if(!sClient.isConnected()){
            final ConnectionRequest req = new ConnectionRequest.Builder().build();
            OkBleTask<Void> task = sClient.newTask(req);
            task.enqueue(Priority.high(1000)).await();
            tryPrintStackTrace(task);
        }
    }

    private final static void tryPrintStackTrace(final OkBleTask task){
        if(task.isComplete() && task.isFailed()){
            task.getException().printStackTrace();
        }
    }
}