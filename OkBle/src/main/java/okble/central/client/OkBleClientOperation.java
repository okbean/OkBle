package okble.central.client;

import android.bluetooth.BluetoothGatt;

import java.util.UUID;
import java.util.concurrent.Semaphore;

import okble.central.client.exception.OkBleException;

final class OkBleClientOperation implements ClientOperation{

    private final OkBleClient client;
    public OkBleClientOperation(final OkBleClient client){
        this.client = client;
    }

    @Override
    public Semaphore connLock() {
        return client.connLock();
    }

    @Override
    public void setConnecting() {
        client.setConnecting();
    }

    @Override
    public void setDisconnected() {
        client.setDisconnected();
    }

    @Override
    public void setConnected(BluetoothGatt gatt) throws OkBleException {
        client.setConnected(gatt);
    }

    @Override
    public void doOpen() {
        client.doOpen();
    }

    @Override
    public BluetoothGatt doConnectGatt(GattConnector gattConnector) throws OkBleException {
        return client.doConnectGatt(gattConnector);
    }

    @Override
    public boolean doDiscoverServices(BluetoothGatt gatt) throws OkBleException {
        return client.doDiscoverServices(gatt);
    }

    @Override
    public boolean doWriteCharacteristic(UUID service, UUID characteristic, byte[] value, int writeType) throws OkBleException {
        return client.doWriteCharacteristic(service, characteristic, value, writeType);
    }

    @Override
    public boolean doReadCharacteristic(UUID service, UUID characteristic) throws OkBleException {
        return client.doReadCharacteristic(service, characteristic);
    }

    @Override
    public boolean doReadDescriptor(UUID service, UUID characteristic, UUID descriptor) throws OkBleException {
        return client.doReadDescriptor(service, characteristic, descriptor);
    }

    @Override
    public boolean doWriteDescriptor(UUID service, UUID characteristic, UUID descriptor, byte[] value) throws OkBleException {
        return client.doWriteDescriptor(service, characteristic, descriptor, value);
    }

    @Override
    public boolean doEnableIndication(UUID service, UUID characteristic) throws OkBleException {
        return client.doEnableIndication(service, characteristic);
    }

    @Override
    public boolean doEnableNotification(UUID service, UUID characteristic) throws OkBleException {
        return client.doEnableNotification(service, characteristic);
    }

    @Override
    public boolean doDisableNotification(UUID service, UUID characteristic) throws OkBleException {
        return client.doDisableNotification(service, characteristic);
    }

    @Override
    public boolean doReadPhy() throws OkBleException {
        return client.doReadPhy();
    }

    @Override
    public boolean doSetPreferredPhy(int txPhy, int rxPhy, int phyOptions) throws OkBleException {
        return client.doSetPreferredPhy(txPhy, rxPhy, phyOptions);
    }

    @Override
    public boolean doSetMtu(int mtu) throws OkBleException {
        return client.doSetMtu(mtu);
    }

    @Override
    public boolean doRefresh() throws OkBleException {
        return client.doRefresh();
    }

    @Override
    public boolean doReadRemoteRssi() throws OkBleException {
        return client.doReadRemoteRssi();
    }

    @Override
    public boolean doUpdateConnectionPriority(int connectionPriority) throws OkBleException {
        return client.doUpdateConnectionPriority(connectionPriority);
    }


}
