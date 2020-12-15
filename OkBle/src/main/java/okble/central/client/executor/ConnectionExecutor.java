package okble.central.client.executor;

import android.bluetooth.BluetoothGatt;
import android.os.SystemClock;

import okble.central.client.ClientOperation;
import okble.central.client.ConnectionOptions;
import okble.central.client.util.EventFilterOfTime;
import okble.central.client.GattConnector;
import okble.central.client.GattOperationResult;
import okble.central.client.OkBleEvent;
import okble.central.client.util.ExecutorProxy;
import okble.central.client.util.OkBleEventFilter;
import okble.central.client.OkBleTask0;
import okble.central.client.exception.GattOperationFailedException;
import okble.central.client.exception.GattOperationTimeoutException;
import okble.central.client.exception.GattStatusException;
import okble.central.client.exception.OkBleException;
import okble.central.client.request.ConnectionRequest;
import okble.central.client.request.Request;
import okble.central.client.util.GattUtils;
import okble.central.client.util.UiExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static okble.central.client.exception.OkBleException.CONNECTION_LOCK_ACQUIRE_FAILED;

final class ConnectionExecutor extends RequestExecutor {

    @Override
    public void executeRequest(ClientOperation client, OkBleTask0 task, Request<?> request0) {
        final ConnectionRequest request = (ConnectionRequest)request0;
        final ConnectionOptions options = request.connectionOptions();
        final GattConnector gattConnector = request.gattConnector();

        if(task.client().isConnected()){
            task.callOnSuccess(null, false);
            return;
        }

        final int retryCount0 = options.connectionRetryCount();
        final int retryCount = retryCount0 < 0 ? 0 : retryCount0;
        final long connectGattDelay = options.connectGattDelay();
        final long connectGattInterval = options.connectGattInterval();
        final long connectGattTimeout = options.connectGattTimeout();
        final long discoverServicesDelay = options.discoverServicesDelay();
        final long discoverServicesTimeout = options.discoverServicesTimeout();

        client.doOpen();
        final Semaphore lock = client.connLock();
        client.setConnecting();
        for(int i=0; i<retryCount+1; i++){
            try{
                final long delay = (i == 0) ? connectGattDelay : connectGattInterval;
                SystemClock.sleep(delay);
                final boolean acquired;
                try{
                    acquired = lock.tryAcquire(1,5000L, TimeUnit.MILLISECONDS);
                }catch (InterruptedException ex){
                    if(retryCount == i){
                        //call failed
                        client.setDisconnected();
                        task.callOnFailed(new OkBleException("can not acquire connection lock!",ex), false);
                        break;
                    }
                    continue;
                }
                if(!acquired){
                    if(retryCount == i){
                        //call failed
                        client.setDisconnected();
                        task.callOnFailed(new OkBleException(CONNECTION_LOCK_ACQUIRE_FAILED, "can not acquire connection lock!"), false);
                        break;
                    }
                    continue;
                }

                BluetoothGatt gatt = null;
                try{
                    gatt = connectGatt(client, gattConnector, connectGattTimeout);

                    SystemClock.sleep(discoverServicesDelay);
                    discoverServices(client, gatt, discoverServicesTimeout);

                    //call success
                    client.setConnected(gatt);
                    task.callOnSuccess(null, false);
                    break;

                }catch (OkBleException ex){
                    close(gatt);

                    final int code = ex.code();
                    if(retryCount == i || code == OkBleException.CLIENT_CLOSED ||
                            code == OkBleException.CLIENT_RELEASED  ||
                            code == OkBleException.BLUETOOTH_NOT_ENABLED){
                        //call failed
                        client.setDisconnected();
                        task.callOnFailed(ex, false);
                        break;
                    }
                }
            }finally {
                lock.release(1);
            }
        }
    }


    private void discoverServices(final ClientOperation client, final BluetoothGatt gatt, final long discoverServicesTimeout) throws OkBleException {
        boolean opSuccess = false;
        try{
            final long opTime = System.currentTimeMillis();
            final boolean success = ExecutorProxy.newProxy(client,mUiExecutor).doDiscoverServices(gatt);
            if(!success){
                throw new GattOperationFailedException("discover services return false!");
            }
            final OkBleEvent event = pipeReadNotThrow(getDiscoverServicesFilter(opTime), discoverServicesTimeout);
            if(event == null){
                throw new GattOperationTimeoutException("discover services timeout!");
            }
            if(event.value() == OkBleEvent.DISCOVER_SERVICES){

                final GattOperationResult result = event.data();
                if(result.status() == BluetoothGatt.GATT_SUCCESS){
                    opSuccess = true;
                }else {
                    throw new GattStatusException(result.status(), "discover services failed!");
                }
            }

        }catch (Exception ex){
            if(ex instanceof OkBleException){
                throw ex;
            }else{
                throw new OkBleException(ex);
            }
        }finally {
            if(!opSuccess){

            }
        }
    }


    private final UiExecutor mUiExecutor = new UiExecutor();

    private BluetoothGatt connectGatt(ClientOperation client, GattConnector gattConnector, long connectGattTimeout) throws OkBleException {
        BluetoothGatt gatt = null;
        boolean opSuccess = false;
        try{
            final long opTime = System.currentTimeMillis();
            gatt = ExecutorProxy.newProxy(client,mUiExecutor).doConnectGatt(gattConnector);
            if(gatt == null){
                throw new GattOperationFailedException("GattConnector:connectGatt return null!");
            }

            final OkBleEvent event = pipeReadNotThrow(getConnectGattFilter(opTime), connectGattTimeout);
            if(event == null){
                throw new GattOperationTimeoutException("connect gatt timeout!");
            }
            if(event.value() == OkBleEvent.CONNECTION_STATE_CONNECTED){
                opSuccess = true;
                return gatt;
            }else if(event.value() == OkBleEvent.CONNECTION_STATE_DISCONNECTED){
                final GattOperationResult result = event.data();
                if(result.status() == BluetoothGatt.GATT_SUCCESS){
                    throw new OkBleException(OkBleException.GATT_DISCONNECTED, "gatt disconnected!");
                }else{
                    throw new GattStatusException(result.status(), "connect gatt failed!");
                }
            }else{
                throw new OkBleException("connect gatt failed!");
            }
        }catch (Exception ex){
            if(ex instanceof OkBleException){
                throw ex;
            }else{
                throw new OkBleException(ex);
            }
        }finally {
            if(!opSuccess){
                close(gatt);
            }
        }
    }

    private static void close(BluetoothGatt gatt){
        GattUtils.safeDisconnect(gatt);
        GattUtils.safeClose(gatt);
    }


    private static OkBleEventFilter getConnectGattFilter(final long time){
        return EventFilterOfTime.from(time,OkBleEvent.CONNECTION_STATE_CONNECTED, OkBleEvent.CONNECTION_STATE_DISCONNECTED);
    }


    private static OkBleEventFilter getDiscoverServicesFilter(final long time){
        return EventFilterOfTime.from(time,OkBleEvent.DISCOVER_SERVICES);
    }

}
