package okble.demo.ui.device;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.UUID;

import okble.central.client.CharacteristicData;
import okble.central.client.ConnectionPriority;
import okble.central.client.ConnectionState;
import okble.central.client.Mtu;
import okble.central.client.NotificationType;
import okble.central.client.OkBleClient;
import okble.central.client.OkBleTask;
import okble.central.client.OnCompleteListener;
import okble.central.client.OnConnectionStateChangedListener;
import okble.central.client.OnDataReceivedListener;
import okble.central.client.Phy;
import okble.central.client.Rssi;
import okble.central.client.WriteType;
import okble.central.client.request.DisableNotificationRequest;
import okble.central.client.request.EnableNotificationRequest;
import okble.central.client.request.ReadCharacteristicRequest;
import okble.central.client.request.ReadDescriptorRequest;
import okble.central.client.request.ReadPhyRequest;
import okble.central.client.request.ReadRemoteRssiRequest;
import okble.central.client.request.SetMtuRequest;
import okble.central.client.request.SetConnectionPriorityRequest;
import okble.central.client.request.WriteCharacteristicRequest;
import okble.central.client.request.WriteDescriptorRequest;
import okble.central.client.util.Hex;
import okble.demo.util.UuidUtils;


public class DeviceViewModel extends AndroidViewModel {
    private final static String TAG = DeviceViewModel.class.getSimpleName();

    public DeviceViewModel(@NonNull final Application application) {
        super(application);
        mConnectionState = new MutableLiveData<ConnectionState>();
        mConnectionState.setValue(ConnectionState.Disconnected);
    }


    private MutableLiveData<ConnectionState> mConnectionState;
    public LiveData<ConnectionState> getConnectionState(){
        return mConnectionState;
    }



    private MutableLiveData<OkBleTask<Rssi>> mReadRemoteRssiTask = new MutableLiveData<>();
    public LiveData<OkBleTask<Rssi>> getReadRemoteRssiTask(){
        return mReadRemoteRssiTask;
    }
    public void readRemoteRssi(){
        mClient.connect();
        final ReadRemoteRssiRequest req = new ReadRemoteRssiRequest.Builder()
                .build();
        final OkBleTask<Rssi> task = mClient.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<Rssi>(){
            @Override
            public void onComplete(OkBleTask<Rssi> task) {
                mReadRemoteRssiTask.setValue(task);
            }
        });
        task.enqueue();
    }


    private MutableLiveData<OkBleTask<Phy>> mReadPhyTask = new MutableLiveData<>();
    public LiveData<OkBleTask<Phy>> getReadPhyTask(){
        return mReadPhyTask;
    }
    public void readPhy(){
        mClient.connect();
        final ReadPhyRequest req = new ReadPhyRequest.Builder()
                .build();
        final OkBleTask<Phy> task = mClient.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<Phy>(){
            @Override
            public void onComplete(OkBleTask<Phy> task) {
                mReadPhyTask.setValue(task);
            }
        });
        task.enqueue();
    }


    private MutableLiveData<OkBleTask<byte[]>> mReadDeviceNameTask = new MutableLiveData<>();
    public LiveData<OkBleTask<byte[]>> getReadDeviceNameTask(){
        return mReadDeviceNameTask;
    }
    public void readDeviceName(){
        mClient.connect();
        final UUID service = UuidUtils.fromShortHex("1800");
        final UUID characteristic =  UuidUtils.fromShortHex("2a00");
        final ReadCharacteristicRequest req = new ReadCharacteristicRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .build();
        final OkBleTask<byte[]> task = mClient.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<byte[]>(){
            @Override
            public void onComplete(OkBleTask<byte[]> task) {
                mReadDeviceNameTask.setValue(task);
            }
        });
        task.enqueue();
    }

    private MutableLiveData<OkBleTask<byte[]>> mReadDeviceAppearanceTask = new MutableLiveData<>();
    public LiveData<OkBleTask<byte[]>> getReadDeviceAppearanceTask(){
        return mReadDeviceAppearanceTask;
    }
    public void readDeviceAppearance(){
        mClient.connect();
        final UUID service = UuidUtils.fromShortHex("1800");
        final UUID characteristic =  UuidUtils.fromShortHex("2a01");
        final ReadCharacteristicRequest req = new ReadCharacteristicRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .build();
        final OkBleTask<byte[]> task = mClient.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<byte[]>(){
            @Override
            public void onComplete(OkBleTask<byte[]> task) {
                mReadDeviceAppearanceTask.setValue(task);
            }
        });
        task.enqueue();
    }



    private MutableLiveData<OkBleTask<Void>> mEnableIndicationTask = new MutableLiveData<>();
    public LiveData<OkBleTask<Void>> getEnableIndicationTask(){
        return mEnableIndicationTask;
    }
    public void enableIndication(){
        mClient.connect();
        final UUID service = UuidUtils.fromShortHex("1801");
        final UUID characteristic =  UuidUtils.fromShortHex("2a05");
        final EnableNotificationRequest req = new EnableNotificationRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .notificationType(NotificationType.Indication)
                .build();
        final OkBleTask<Void> task = mClient.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(OkBleTask<Void> task) {
                mEnableIndicationTask.setValue(task);
            }
        });
        task.enqueue();
    }


    private MutableLiveData<OkBleTask<Void>> mDisableIndicationTask = new MutableLiveData<>();
    public LiveData<OkBleTask<Void>> getDisableIndicationTask(){
        return mDisableIndicationTask;
    }
    public void disableIndication(){
        mClient.connect();
        final UUID service = UuidUtils.fromShortHex("1801");
        final UUID characteristic =  UuidUtils.fromShortHex("2a05");
        final DisableNotificationRequest req = new DisableNotificationRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .build();
        final OkBleTask<Void> task = mClient.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(OkBleTask<Void> task) {
                mDisableIndicationTask.setValue(task);
            }
        });
        task.enqueue();
    }



    private MutableLiveData<OkBleTask<Void>> mWriteDescriptorTask = new MutableLiveData<>();
    public LiveData<OkBleTask<Void>> getWriteDescriptorTask(){
        return mWriteDescriptorTask;
    }
    public void writeDescriptor(final byte[] data){
        mClient.connect();
        final UUID service = UuidUtils.fromShortHex("1801");
        final UUID characteristic =  UuidUtils.fromShortHex("2a05");
        final UUID descriptor =  UuidUtils.fromShortHex("2902");
        final WriteDescriptorRequest req = new WriteDescriptorRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .descriptor(descriptor)
                .packetSenderFactory(data)
                .build();
        final OkBleTask<Void> task = mClient.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(OkBleTask<Void> task) {
                mWriteDescriptorTask.setValue(task);
            }
        });
        task.enqueue();
    }


    private MutableLiveData<OkBleTask<byte[]>> mReadDescriptorTask = new MutableLiveData<>();
    public LiveData<OkBleTask<byte[]>> getReadDescriptorTask(){
        return mReadDescriptorTask;
    }
    public void readDescriptor(){
        mClient.connect();
        final UUID service = UuidUtils.fromShortHex("1801");
        final UUID characteristic =  UuidUtils.fromShortHex("2a05");
        final UUID descriptor =  UuidUtils.fromShortHex("2902");
        final ReadDescriptorRequest req = new ReadDescriptorRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .descriptor(descriptor)
                .build();
        final OkBleTask<byte[]> task = mClient.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<byte[]>(){
            @Override
            public void onComplete(OkBleTask<byte[]> task) {
                mReadDescriptorTask.setValue(task);
            }
        });
        task.enqueue();
    }



    private MutableLiveData<OkBleTask<Mtu>> mSetMtuTask = new MutableLiveData<>();
    public LiveData<OkBleTask<Mtu>> getSetMtuTaskTask(){
        return mSetMtuTask;
    }
    public void setMtu(final int mtu){
        mClient.connect();
        final SetMtuRequest req = new SetMtuRequest.Builder()
                .mtu(mtu).build();
        final OkBleTask<Mtu> task = mClient.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<Mtu>(){
            @Override
            public void onComplete(OkBleTask<Mtu> task) {
                mSetMtuTask.setValue(task);
            }
        });
        task.enqueue();
    }


    private MutableLiveData<OkBleTask<Void>> mSetConnectionPriorityTask = new MutableLiveData<>();
    public LiveData<OkBleTask<Void>> getSetConnectionPriorityTask(){
        return mSetConnectionPriorityTask;
    }
    public void setConnectionPriority(final ConnectionPriority priority){
        mClient.connect();
        final SetConnectionPriorityRequest req = new SetConnectionPriorityRequest.Builder()
                .connectionPriority(priority)
                .build();
        final OkBleTask<Void> task = mClient.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(OkBleTask<Void> task) {
                mSetConnectionPriorityTask.setValue(task);
            }
        });
        task.enqueue();
    }






    private OkBleClient mClient = null;
    public void setDevice(final BluetoothDevice device){
        final OkBleClient client = new OkBleClient.Builder()
                .context(getApplication())
                .debuggable(true)
                .device(device)
                .build();
        client.addOnDataReceivedListener(new OnDataReceivedListener(){
            @Override
            public void onDataReceived(OkBleClient client, CharacteristicData data) {
                Log.i(TAG, "onDataReceived: (0x) " + Hex.toString(data.data(), '-'));
            }
        });
        client.addOnConnectionStateChangedListener(new OnConnectionStateChangedListener(){
            @Override
            public void onConnectionStateChanged(OkBleClient client, ConnectionState newState, ConnectionState lastState) {
                Log.i(TAG, "onConnectionStateChanged." + "newState:" + newState.toString() + " lastState:" + lastState.toString());
                mConnectionState.setValue(newState);
            }
        });
        mClient =  client;
    }



    private MutableLiveData<OkBleTask<Void>> mWriteCharacteristicForHuaweiDeviceTask = new MutableLiveData<>();
    public LiveData<OkBleTask<Void>> getWriteCharacteristicForHuaweiDeviceTask(){
        return mWriteCharacteristicForHuaweiDeviceTask;
    }
    public void writeCharacteristicForHuaweiDevice(final byte[] data){
        mClient.connect();
        final UUID service = UuidUtils.fromShortHex("fe86");
        final UUID characteristic =  UuidUtils.fromShortHex("fe01");
        final WriteCharacteristicRequest req = new WriteCharacteristicRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .writeType(WriteType.WriteNoResponse)
                .packetSenderFactory(data, 20)
                .build();
        final OkBleTask<Void> task = mClient.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(OkBleTask<Void> task) {
                mWriteCharacteristicForHuaweiDeviceTask.setValue(task);
            }
        });
        task.enqueue();
    }




    public void close(){
        if(mClient != null){
            mClient.close();
        }
    }

    public void connect(){
        if(mClient != null){
            mClient.connect();
        }
    }

}