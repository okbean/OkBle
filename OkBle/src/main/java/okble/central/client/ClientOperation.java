package okble.central.client;

import android.bluetooth.BluetoothGatt;

import java.util.UUID;
import java.util.concurrent.Semaphore;

import okble.central.client.exception.OkBleException;

public interface ClientOperation {

    Semaphore connLock();

    void setConnecting();

    void setDisconnected();

    void setConnected(BluetoothGatt gatt) throws OkBleException;

    void doOpen();

    BluetoothGatt doConnectGatt(final GattConnector gattConnector) throws OkBleException;

    boolean doDiscoverServices(final BluetoothGatt gatt) throws OkBleException;

    boolean doWriteCharacteristic(final UUID service, final UUID characteristic, final byte[] value, final int writeType) throws OkBleException;

    boolean doReadCharacteristic(UUID service, UUID characteristic) throws OkBleException;

    boolean doReadDescriptor(UUID service, UUID characteristic, UUID descriptor) throws OkBleException;

    boolean doWriteDescriptor(UUID service, UUID characteristic, UUID descriptor, byte[] value) throws OkBleException;

    boolean doEnableIndication(UUID service, UUID characteristic) throws OkBleException;

    boolean doEnableNotification(UUID service, UUID characteristic) throws OkBleException;

    boolean doDisableNotification(UUID service, UUID characteristic) throws OkBleException;

    boolean doReadPhy() throws OkBleException;

    boolean doSetPreferredPhy(int txPhy, int rxPhy, int phyOptions) throws OkBleException;

    boolean doSetMtu(final int mtu) throws OkBleException;

    boolean doRefresh() throws OkBleException;

    boolean doReadRemoteRssi() throws OkBleException;

    boolean doSetConnectionPriority(int connectionPriority) throws OkBleException;
}