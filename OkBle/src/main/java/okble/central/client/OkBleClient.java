package okble.central.client;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import okble.central.client.exception.OkBleException;
import okble.central.client.exception.TaskCanceledException;
import okble.central.client.executor.RequestExecutor;
import okble.central.client.executor.RequestExecutors;
import okble.central.client.request.ConnectionRequest;
import okble.central.client.request.Request;
import okble.central.client.util.GattUtils;
import okble.central.client.util.UiExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static okble.central.client.util.BluetoothUtils.isBluetoothEnabled;
import static okble.central.client.util.BluetoothUtils.toBluetoothDevice;
import static okble.central.client.util.GattUtils.safeClose;
import static okble.central.client.util.GattUtils.safeDisconnect;
import static okble.central.client.util.RequestUtils.typeIn;

public final class OkBleClient {

    private final static String TAG = OkBleClient.class.getSimpleName();

    private final static Semaphore sConnLock = new Semaphore(1, true);
    private final static Executor sTaskExecutor = new ThreadPoolExecutor(4, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    private final BluetoothDevice mDevice;
    private final Context mContext;
    private final boolean mDebuggable;
    private final GattConnector.Factory mGattConnectorFactory;
    private final ConnectionOptions.Factory mConnectionOptionsFactory;
    private final ServiceValidator.Factory mServiceValidatorFactory;

    private OkBleClient(Builder builder){
        this.mContext = builder.mContext.getApplicationContext();
        this.mDevice = builder.mDevice;
        this.mDebuggable = builder.mDebuggable;
        this.mGattConnectorFactory = builder.mGattConnectorFactory;
        this.mConnectionOptionsFactory = builder.mConnectionOptionsFactory;
        this.mServiceValidatorFactory = builder.mServiceValidatorFactory;
        scheduleTask();
        registerBluetoothStateReceiver();
    }


    private volatile boolean mExecuting = false;
    private final Object mExecutingLock = new Object();
    private void scheduleTask(){
        sTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final long time = System.currentTimeMillis();
                if(mDebuggable){
                    Log.i(TAG, "executeTask waiting!" + time);
                }
                synchronized (mExecutingLock){
                    mExecuting = true;
                    if(mDebuggable){
                        Log.i(TAG, "executeTask enter!"+ time);
                    }
                    final String threadName = generateTaskThreadName(mDevice);
                    Thread.currentThread().setName(threadName);
                    executeTask();
                    if(mDebuggable){
                        Log.i(TAG, "executeTask leave!"+ time);
                    }
                    mExecuting = false;
                }
            }
        });
    }

    private static String generateTaskThreadName(final BluetoothDevice device){
        final String val = String.format("task@(%s#%s)", device.getName(), device.getAddress())   ;
        return val;
    }

    private RequestTask mCurrentTask;
    private void executeTask(){
        for(;;){
            if(mReleased){
                fireTaskQueueWhenClientReleased();
                break;
            }

            final boolean isBluetoothEnabled = isBluetoothEnabled();
            if(!isBluetoothEnabled){
                fireTaskQueueWhenBluetoothNotEnabled();
            }

            RequestTask requestTask;
            synchronized (mLock){
                requestTask = next();
                if(requestTask == null){
                    if(mClosed){
                        break;
                    }
                    try {
                        mLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
            }
            if(requestTask == null || !requestTask.occupy()){
                continue;
            }

            final Request<?> req = requestTask.request();
            final RequestExecutor executor = requestTask.executor();
            final OkBleTask0<?> task = requestTask.task();
            try{
                if(mDebuggable){
                    Log.d(TAG, String.format("begin execute request:%s, priority:%s", req.type(), requestTask.priority()));
                }
                mCurrentTask = requestTask;
                executor.executeRequest(clientOperation(),task,req);
            }catch (Exception ex){
                ex.printStackTrace();
                task.callOnFailed(ex, false);
            }finally {
                mCurrentTask = null;
                if(!task.isComplete()){
                    task.callOnFailed(new OkBleException("this task was not complete!"), false);
                }
                if(mDebuggable){
                    Log.d(TAG, String.format("end execute request:%s", req.type()));
                }
            }
        }
    }

    private void fireTaskQueueWhenBluetoothNotEnabled(){
        final OkBleException ex = new OkBleException(OkBleException.BLUETOOTH_NOT_ENABLED,"bluetooth is not enabled!");
        fireTaskQueueWithException(ex);
    }
    private void fireTaskQueueWhenClientReleased(){
        final OkBleException ex = getClientReleasedException();
        fireTaskQueueWithException(ex);
    }
    private void fireTaskQueueWithException(final OkBleException ex){
        synchronized (mLock){
            RequestTask rt = mHeadQueue.poll();
            while(rt != null){
                if(!rt.occupy()){
                    continue;
                }
                rt.task().callOnFailed(ex, false);
                rt = mTaskQueue.poll();
            }
            rt = mTaskQueue.poll();
            while(rt != null){
                if(!rt.occupy()){
                    continue;
                }
                rt.task().callOnFailed(ex, false);
                rt = mTaskQueue.poll();
            }
        }
    }


    private void cancelAll(){
        final OkBleException ex = new TaskCanceledException("canceled from task queue!");
        synchronized (mLock){
            RequestTask rt = mTaskQueue.poll();
            while(rt != null){
                if(!rt.occupy()){
                    continue;
                }
                rt.task().callOnCanceled(ex, false);
                rt = mTaskQueue.poll();
            }

            rt = mTaskQueue.poll();
            while(rt != null){
                if(!rt.occupy()){
                    continue;
                }
                rt.task().callOnCanceled(ex, false);
                rt = mTaskQueue.poll();
            }
        }
    }


    public BluetoothDevice device() {
        return this.mDevice;
    }

    public Context context() {
        return this.mContext;
    }

    public boolean debuggable() {
        return this.mDebuggable;
    }

    public List<BluetoothGattService> getServices() {
        return isConnected() && mBluetoothGatt != null ?
                mBluetoothGatt.getServices() :
                Collections.emptyList();
    }


    private final Object mLock = new Object();

    private final PriorityQueue<RequestTask> mTaskQueue = new PriorityQueue<RequestTask>(16, sRequestTaskComparator);
    private final PriorityQueue<RequestTask> mHeadQueue = new PriorityQueue<RequestTask>(2, sRequestTaskComparator);

    private final static Comparator<RequestTask> sRequestTaskComparator = new Comparator<RequestTask>(){
        @Override
        public int compare(RequestTask o1, RequestTask o2) {
            return o1.priority().compareTo(o2.priority());
        }
    };

    private RequestTask next(){
        RequestTask val;
        synchronized (mLock){
            val = mHeadQueue.poll();
            if(val == null){
                val = mTaskQueue.poll();
            }
        }
        return val;
    }


    ClientOperation clientOperation(){
        return new OkBleClientOperation(this);
    }

    Semaphore connLock(){
        return sConnLock;
    }

    void enqueue(final RequestTask task){
        synchronized (mLock){
            mTaskQueue.add(task);
            mLock.notify();
            if(!mExecuting){
                scheduleTask();
            }
            registerBluetoothStateReceiver();
        }
    }

    boolean dequeue(final RequestTask task){
        synchronized (mLock){
            boolean val = mHeadQueue.remove(task);
            if(!val){
                val = mTaskQueue.remove(task);
            }
            return val;
        }
    }


    public void connect(){
        synchronized (mLock){
            if(mReleased){
                return;
            }
            if(!isConnected() && !isConnecting()){
                final ConnectionRequest request = new ConnectionRequest.Builder().build();
                final RequestExecutor handler = RequestExecutors.fromRequestType(Request.Type.ConnectionRequest);
                final OkBleTask0<?> task =  new OkBleTask0(this, handler, request);
                final RequestTask requestTask = new RequestTask(task, handler, request, Priority.highest());

                mHeadQueue.add(requestTask);
                mLock.notify();

                if(!mExecuting){
                    scheduleTask();
                }
                registerBluetoothStateReceiver();
            }
        }
    }


    BluetoothGatt doConnectGatt(final GattConnector gattConnector) throws OkBleException {
        checkReleased();
        checkBluetoothEnabled();
        checkClosed();

        final BluetoothGatt val = gattConnector.connectGatt(mDevice, mContext, mBluetoothGattCallback);
        return val;
    }

    boolean doDiscoverServices(final BluetoothGatt gatt) throws  OkBleException {
        checkReleased();
        checkBluetoothEnabled();
        checkClosed();

        final boolean val = gatt.discoverServices();
        return val;
    }

    boolean doDisconnectGatt() throws OkBleException {
        checkReleased();
        close0(mBluetoothGatt);
        mBluetoothGatt = null;
        return true;
    }

    boolean doWriteCharacteristic(final UUID service, final UUID characteristic, final byte[] value, final int writeType) throws OkBleException{
        checkReleased();
        checkConnected();

        final BluetoothGatt gatt = getBluetoothGatt0();
        final BluetoothGattService gattService = getService0(gatt, service);
        final BluetoothGattCharacteristic gattCharacteristic = getCharacteristic0(gattService, characteristic);

        final int properties = gattCharacteristic.getProperties();
        if ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) == 0
                && (properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) == 0) {
            throw new OkBleException(String.format("characteristic:%s doesn't have write property !", characteristic));
        }

        gattCharacteristic.setWriteType(writeType);
        gattCharacteristic.setValue(value);
        final boolean val = gatt.writeCharacteristic(gattCharacteristic);
        return val;
    }

    boolean doReadCharacteristic(UUID service, UUID characteristic) throws OkBleException{
        checkReleased();
        checkConnected();

        final BluetoothGatt gatt = getBluetoothGatt0();
        final BluetoothGattService gattService = getService0(gatt, service);
        final BluetoothGattCharacteristic gattCharacteristic = getCharacteristic0(gattService, characteristic);

        if ((gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) == 0) {
            throw new OkBleException(String.format("characteristic:%s doesn't have read property !", characteristic));
        }

        final boolean val = gatt.readCharacteristic(gattCharacteristic);
        return val;
    }



    private final static UUID CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    boolean doEnableNotification(UUID service, UUID characteristic) throws OkBleException{
        checkReleased();
        checkConnected();

        final byte[] value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
        final BluetoothGatt gatt = getBluetoothGatt0();
        final BluetoothGattService gattService = getService0(gatt, service);
        final BluetoothGattCharacteristic gattCharacteristic = getCharacteristic0(gattService, characteristic);
        final BluetoothGattDescriptor gattDescriptor = getCccdDescriptor0(gattCharacteristic);
        if((gattCharacteristic.getPermissions() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0){
            throw new OkBleException(String.format("characteristic:%s doesn't have NOTIFY property!", characteristic));
        }

        final boolean setSuccess = gatt.setCharacteristicNotification(gattCharacteristic, true);
        if(!setSuccess){
            throw new OkBleException(String.format("setCharacteristicNotification(characteristic:%s,enable:%s) failed!", characteristic, false));
        }
        gattDescriptor.setValue(value);
        final boolean val = gatt.writeDescriptor(gattDescriptor);
        return val;
    }

    boolean doEnableIndication(UUID service, UUID characteristic) throws OkBleException{
        checkReleased();
        checkConnected();

        final byte[] value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
        final BluetoothGatt gatt = getBluetoothGatt0();
        final BluetoothGattService gattService = getService0(gatt, service);
        final BluetoothGattCharacteristic gattCharacteristic = getCharacteristic0(gattService, characteristic);
        final BluetoothGattDescriptor gattDescriptor = getCccdDescriptor0(gattCharacteristic);

        if((gattCharacteristic.getPermissions() | BluetoothGattCharacteristic.PROPERTY_INDICATE) == 0){
            throw new OkBleException(String.format("characteristic:%s doesn't have INDICATE property!", characteristic));
        }

        final boolean setSuccess = gatt.setCharacteristicNotification(gattCharacteristic, true);
        if(!setSuccess){
            throw new OkBleException(String.format("setCharacteristicNotification(characteristic:%s,enable:%s) failed!", characteristic, true));
        }
        gattDescriptor.setValue(value);
        final boolean val = gatt.writeDescriptor(gattDescriptor);
        return val;

    }

    boolean doDisableNotification(UUID service, UUID characteristic) throws OkBleException{
        checkReleased();
        checkConnected();

        final byte[] value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        final BluetoothGatt gatt = getBluetoothGatt0();
        final BluetoothGattService gattService = getService0(gatt, service);
        final BluetoothGattCharacteristic gattCharacteristic = getCharacteristic0(gattService, characteristic);
        final BluetoothGattDescriptor gattDescriptor = getCccdDescriptor0(gattCharacteristic);

        if(((gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) == 0) &&
                ((gattCharacteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) == 0)){
            throw new OkBleException(String.format("characteristic:%s doesn't have INDICATE or NOTIFY property!", characteristic));
        }

        final boolean setSuccess = gatt.setCharacteristicNotification(gattCharacteristic, false);
        if(!setSuccess){
            throw new OkBleException(String.format("setCharacteristicNotification(characteristic:%s,enable:%s) failed!", characteristic, false));
        }
        gattDescriptor.setValue(value);
        final boolean val = gatt.writeDescriptor(gattDescriptor);
        return val;
    }

    boolean doWriteDescriptor(UUID service, UUID characteristic, UUID descriptor,byte[] value) throws OkBleException{
        checkReleased();
        checkConnected();

        final BluetoothGatt gatt = getBluetoothGatt0();
        final BluetoothGattService gattService = getService0(gatt, service);
        final BluetoothGattCharacteristic gattCharacteristic = getCharacteristic0(gattService, characteristic);
        final BluetoothGattDescriptor gattDescriptor = getDescriptor0(gattCharacteristic, descriptor);
        gattDescriptor.setValue(value);
        final boolean val = gatt.writeDescriptor(gattDescriptor);
        return val;
    }


    boolean doReadDescriptor(UUID service, UUID characteristic, UUID descriptor) throws OkBleException{
        checkReleased();
        checkConnected();

        final BluetoothGatt gatt = getBluetoothGatt0();
        final BluetoothGattService gattService = getService0(gatt, service);
        final BluetoothGattCharacteristic gattCharacteristic = getCharacteristic0(gattService, characteristic);
        final BluetoothGattDescriptor gattDescriptor = getDescriptor0(gattCharacteristic, descriptor);

        final boolean val = gatt.readDescriptor(gattDescriptor);
        return val;
    }

    boolean doReadRemoteRssi() throws OkBleException{
        checkReleased();
        checkConnected();

        final BluetoothGatt gatt = getBluetoothGatt0();
        final boolean val = gatt.readRemoteRssi();
        return val;
    }


    boolean doSetMtu(final int mtu) throws OkBleException{
        if(mtu < 23 || mtu > 517){
            throw new OkBleException(String.format("mtu(%s) should less than 512 and more than 23!", mtu));
        }
        checkReleased();
        checkConnected();

        final BluetoothGatt gatt = getBluetoothGatt0();
        if(Build.VERSION.SDK_INT < 21){
            throw new OkBleException("requestMtu not supported!");
        }
        final boolean val = gatt.requestMtu(mtu) ;
        return val;
    }

    boolean doReadPhy() throws OkBleException{
        checkReleased();
        checkConnected();

        final BluetoothGatt gatt = getBluetoothGatt0();
        if(Build.VERSION.SDK_INT >= 26){
            gatt.readPhy();
            return true;
        }else{
            throw new OkBleException("read phy not supported!");
        }
    }

    boolean doSetPreferredPhy(int txPhy, int rxPhy, int phyOptions) throws OkBleException{
        checkReleased();
        checkConnected();

        final BluetoothGatt gatt = getBluetoothGatt0();
        if(Build.VERSION.SDK_INT >= 26){
            gatt.setPreferredPhy(txPhy,rxPhy,phyOptions);
            return true;
        }else{
            throw new OkBleException("setPreferredPhy is supported after android sdk 26(included)!");
        }
    }

    boolean doRefresh() throws OkBleException{
        checkReleased();
        checkConnected();

        final BluetoothGatt gatt = getBluetoothGatt0();
        final boolean val = GattUtils.refresh(gatt);
        return val;
    }

    boolean doUpdateConnectionPriority(int connectionPriority) throws OkBleException{
        checkReleased();
        checkConnected();

        final BluetoothGatt gatt = getBluetoothGatt0();
        if(Build.VERSION.SDK_INT < 21){
            throw new OkBleException("requestConnectionPriority not supported!");
        }
        final boolean val = gatt.requestConnectionPriority(connectionPriority);
        return val;
    }

    BluetoothGatt getBluetoothGatt0() throws OkBleException{
        final BluetoothGatt gatt = mBluetoothGatt;
        if(gatt == null){
            throw new OkBleException(OkBleException.GATT_NOT_FOUND,
                    String.format("client@%s#%s doesn't have a gatt!",
                            mDevice.getName(), mDevice.getAddress()));
        }
        return gatt;
    }

    private static BluetoothGattService getService0(final BluetoothGatt gatt, final UUID service) throws OkBleException{
        final BluetoothGattService val = gatt.getService(service);
        if(val == null){
            throw new OkBleException(OkBleException.GATT_SERVICE_NOT_FOUND,
                    String.format("service:%s not found!", service));
        }
        return val;
    }

    private static BluetoothGattCharacteristic getCharacteristic0(final BluetoothGattService service, final UUID characteristic) throws OkBleException{
        final BluetoothGattCharacteristic val = service.getCharacteristic(characteristic);
        if(val == null){
            throw new OkBleException(OkBleException.GATT_CHARACTERISTIC_NOT_FOUND,
                    String.format("characteristic:%s of service:%s not found!", characteristic, service.getUuid()));
        }
        return val;
    }

    private static BluetoothGattDescriptor getDescriptor0(final BluetoothGattCharacteristic characteristic, final UUID descriptor) throws OkBleException{
        final BluetoothGattDescriptor val = characteristic.getDescriptor(descriptor);
        if(val == null){
            throw new OkBleException(OkBleException.GATT_DESCRIPTOR_NOT_FOUND,
                    String.format("descriptor:%s of characteristic:%s not found!", descriptor, characteristic.getUuid()));
        }
        return val;
    }

    private static BluetoothGattDescriptor getCccdDescriptor0(final BluetoothGattCharacteristic characteristic) throws OkBleException{
        final UUID descriptor = CCCD_UUID;
        final BluetoothGattDescriptor val = characteristic.getDescriptor(descriptor);
        if(val == null){
            throw new OkBleException(OkBleException.GATT_DESCRIPTOR_NOT_FOUND,
                    String.format("CCCD descriptor:%s of characteristic:%s not found!", descriptor, characteristic.getUuid()));
        }
        return val;
    }


    private void release(){
        synchronized (mLock){
            if(mReleased){
                return;
            }
            mReleased = true;
            close0(mBluetoothGatt);
            mBluetoothGatt = null;
            setDisconnected();
            unregisterBluetoothStateReceiver();
            mLock.notifyAll();
        }
    }

    public void close(){
        synchronized (mLock){
            if(mReleased || mClosed){
                return;
            }
            mClosed = true;
            close0(mBluetoothGatt);
            mBluetoothGatt = null;
            setDisconnected();
            unregisterBluetoothStateReceiver();
            mLock.notifyAll();
        }
    }

    private void closeOnBluetoothNotEnabled(){
        synchronized (mLock){
            if(mReleased || mClosed){
                return;
            }
            close0(mBluetoothGatt);
            mBluetoothGatt = null;
            setDisconnected();
        }
    }


    void doOpen(){
        synchronized (mLock){
            mClosed = false;
        }
    }

    private void close0(final BluetoothGatt gatt){
        safeDisconnect(gatt);
        safeClose(gatt);
    }

    private boolean mClosed = false;
    private boolean mReleased = false;
    private ConnectionState mConnectionState = ConnectionState.Disconnected;
    private BluetoothGatt mBluetoothGatt;

    private boolean isReleased() {
        return mReleased;
    }
    public boolean isConnected(){
        return mConnectionState == ConnectionState.Connected;
    }
    public boolean isConnecting(){
        return mConnectionState == ConnectionState.Connecting;
    }
    public ConnectionState getConnectionState(){
        return mConnectionState;
    }

    void setConnected(BluetoothGatt gatt) throws OkBleException {
        synchronized (mLock){
            checkClosed();
            checkReleased();
            final ConnectionState last = this.mConnectionState;
            this.mConnectionState = ConnectionState.Connected;
            mBluetoothGatt = gatt;
            //notify connected
            if(last != ConnectionState.Connected){
                fireOnConnectionStateChangedListeners(ConnectionState.Connected, last, false);
            }
        }
    }
    void setConnecting(){
        synchronized (mLock){
            final ConnectionState last = this.mConnectionState;
            this.mConnectionState = ConnectionState.Connecting;
            if(last != ConnectionState.Connecting){
                fireOnConnectionStateChangedListeners(ConnectionState.Connecting, last, false);
            }
        }
    }
    void setDisconnected(){
        final ConnectionState last;
        synchronized (mLock){
            last = this.mConnectionState;
            this.mConnectionState = ConnectionState.Disconnected;

        }
        if(last != ConnectionState.Disconnected){
            fireOnConnectionStateChangedListeners(ConnectionState.Disconnected, last, false);
        }
    }

    void setDisconnectedOnConnectionStateChanged(){
        final ConnectionState last;
        synchronized (mLock){
            last = this.mConnectionState;
            if(last != ConnectionState.Disconnected && last != ConnectionState.Connecting){
                this.mConnectionState = ConnectionState.Disconnected;
                fireOnConnectionStateChangedListeners(ConnectionState.Disconnected, last, false);
            }
        }
    }

    private OkBleException getClientReleasedException(){
        final OkBleException ex = new OkBleException(OkBleException.CLIENT_RELEASED,
                String.format("client(%s#%s) is is released!",
                        mDevice.getName(),
                        mDevice.getAddress()));
        return ex;
    }

    void checkReleased() throws OkBleException{
        if(mReleased){
            throw getClientReleasedException();
        }
    }

    private OkBleException getClientClosedException(){
        final OkBleException ex = new OkBleException(OkBleException.CLIENT_CLOSED,
                String.format("client@%s#%s is closed!",
                        mDevice.getName(),
                        mDevice.getAddress()));
        return ex;
    }

    void checkClosed() throws OkBleException{
        if(mClosed){
            throw new OkBleException(OkBleException.CLIENT_CLOSED,
                    String.format("client@%s#%s is closed!",
                            mDevice.getName(),
                            mDevice.getAddress()));
        }
    }

    void checkConnected() throws OkBleException{
        if(!isConnected()){
            throw new OkBleException(OkBleException.CLIENT_NOT_CONNECTED,
                    String.format("client@%s#%s is not connected!",
                            mDevice.getName(),
                            mDevice.getAddress()));
        }
    }

    void checkBluetoothEnabled() throws OkBleException{
        if(!isBluetoothEnabled()){
            throw new OkBleException(OkBleException.BLUETOOTH_NOT_ENABLED, "bluetooth is not enabled!");
        }
    }


//    void checkServicesDiscovered() throws OkBleException{
//        if(!mServicesDiscovered){
//            throw new OkBleException(OkBleException.SERVICES_NOT_DISCOVERED,
//                    String.format("client(%s#%s)'s services has not been discovered!",
//                            mDevice.getName(),
//                            mDevice.getAddress()));
//        }
//    }

    public <R> OkBleTask<R> newTask(Request<R> request){
        return Requests.newTask(this, request);
    }

    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallbackImpl();
    private final class BluetoothGattCallbackImpl extends  BluetoothGattCallback{
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if(mDebuggable){
                Log.d(TAG,  String.format("onConnectionStateChange. status:%d , newState:%d", status, newState));
            }

            final GattOperationResult result = new GattOperationResult(status, gatt, GattConnectionState.of(newState));
            if(status == BluetoothGatt.GATT_SUCCESS){
                if(newState == BluetoothGatt.STATE_CONNECTED){
                    postEvent(OkBleEvent.connectionStateConnected(result), Request.Type.ConnectionRequest);
                }else if(newState == BluetoothGatt.STATE_DISCONNECTED){
                    close0(gatt);
                    setDisconnectedOnConnectionStateChanged();
                    postEvent(OkBleEvent.connectionStateDisconnected(result));
                }
            }else{
                close0(gatt);
                setDisconnectedOnConnectionStateChanged();
                postEvent(OkBleEvent.connectionStateDisconnected(result));
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(mDebuggable){
                Log.d(TAG,  String.format("onServicesDiscovered. status:%d", status));
            }
            final GattOperationResult result = new GattOperationResult(status, gatt, gatt.getServices());
            postEvent(OkBleEvent.discoverServices(result), Request.Type.ConnectionRequest);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            final CharacteristicData data = CharacteristicData.from(characteristic);
            if(mDebuggable){
                Log.d(TAG,  String.format("onCharacteristicChanged. data:%s", data));
            }
            final GattOperationResult result = new GattOperationResult(BluetoothGatt.GATT_SUCCESS, gatt, data);
            postEvent(OkBleEvent.characteristicChanged(result),Request.Type.WriteCharacteristicForResponseRequest);
            fireOnDataReceivedListeners(data, false);
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(mDebuggable){
                Log.d(TAG,  String.format("onCharacteristicWrite. status:%d", status));
            }
            final GattOperationResult result = new GattOperationResult(status, gatt, CharacteristicData.from(characteristic));
            postEvent(OkBleEvent.writeCharacteristic(result));
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            final CharacteristicData data = CharacteristicData.from(characteristic);
            if(mDebuggable){
                Log.d(TAG,  String.format("onCharacteristicRead. status:%d , data:%s", status, data));
            }
            final GattOperationResult result = new GattOperationResult(status, gatt, data);
            postEvent(OkBleEvent.readCharacteristic(result));
        }


        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            final DescriptorData data = DescriptorData.from(descriptor);
            if(mDebuggable){
                Log.d(TAG,  String.format("onDescriptorRead. status:%d , data:%s", status, data));
            }

            final GattOperationResult result = new GattOperationResult(status, gatt, data);
            postEvent(OkBleEvent.readDescriptor(result), Request.Type.ReadDescriptorRequest);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if(mDebuggable){
                Log.d(TAG,  String.format("onDescriptorWrite. status:%d", status));
            }

            final GattOperationResult result = new GattOperationResult(status, gatt, DescriptorData.from(descriptor));
            postEvent(OkBleEvent.writeDescriptor(result));
        }

        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            if(mDebuggable){
                Log.d(TAG,  String.format("onPhyUpdate. status:%d , txPhy:%d , rxPhy:%d", status, txPhy, rxPhy));
            }

            final GattOperationResult result = new GattOperationResult(status, gatt, Phy.of(txPhy, rxPhy));
            postEvent(OkBleEvent.setPreferredPhy(result),Request.Type.SetPreferredPhyRequest);
        }


        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            if(mDebuggable){
                Log.d(TAG,  String.format("onPhyRead. status:%d , txPhy:%d , rxPhy:%d", status, txPhy, rxPhy));
            }
            final GattOperationResult result = new GattOperationResult(status, gatt, Phy.of(txPhy, rxPhy));
            postEvent(OkBleEvent.readPhy(result), Request.Type.ReadPhyRequest);
        }


        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if(mDebuggable){
                Log.d(TAG,  String.format("onMtuChanged. status:%d , mtu:%d", status, mtu));
            }
            final GattOperationResult result = new GattOperationResult(status, gatt, Mtu.of(mtu));
            postEvent(OkBleEvent.setMtu(result), Request.Type.SetMtuRequest);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            if(mDebuggable){
                Log.d(TAG,  String.format("onReadRemoteRssi. status:%d , rssi:%d", status, rssi));
            }
            final GattOperationResult result = new GattOperationResult(status, gatt, Rssi.of(rssi));
            postEvent(OkBleEvent.readRemoteRssi(result), Request.Type.ReadRemoteRssiRequest);

        }

        public void onConnectionUpdated(BluetoothGatt gatt, int interval, int latency, int timeout, int status) {
            final ConnectionParameter data = ConnectionParameter.of(interval, latency, timeout);
            if(mDebuggable){
                Log.d(TAG,  String.format("onConnectionUpdated. status:%d , data:%s", status, data));
            }
            if(status == BluetoothGatt.GATT_SUCCESS){
                fireOnConnectionUpdatedListeners(data);
            }
        }

    };


    private void postEvent(OkBleEvent event){
        final RequestTask task = mCurrentTask;
        if(task != null){
            task.executor().pipe().sink().write(event);
        }
    }
    private void postEvent(OkBleEvent event, Request.Type... types){
        final RequestTask task = mCurrentTask;
        if(task != null && typeIn(task.request().type(), types)){
            task.executor().pipe().sink().write(event);
        }
    }


    private volatile boolean mRegistered = false;
    private void registerBluetoothStateReceiver(){
        if(mRegistered){
            return;
        }
        try{
            mContext.registerReceiver(mBluetoothStateReceiver,
                    new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            mRegistered = true;
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
    private void unregisterBluetoothStateReceiver(){
        if(!mRegistered){
            return;
        }
        try{
            mContext.unregisterReceiver(mBluetoothStateReceiver);
            mRegistered = false;
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private final BroadcastReceiver mBluetoothStateReceiver = new BluetoothStateReceiver();
    private final class BluetoothStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
            final int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);

            if(mDebuggable){
                Log.i(TAG, String.format("BluetoothStateReceiver::onReceive. state:%d , previousState:%d", state, previousState));
            }

            if(state == BluetoothAdapter.STATE_OFF ||
                    state == BluetoothAdapter.STATE_TURNING_OFF){
                closeOnBluetoothNotEnabled();
            }
        }
    }

    private final ArrayList<OnConnectionStateChangedListener> mOnConnectionStateChangedListeners = new ArrayList<>(2);
    public void addOnConnectionStateChangedListener(OnConnectionStateChangedListener listener) {
        if(listener == null){
            return;
        }
        if(!mOnConnectionStateChangedListeners.contains(listener)){
            mOnConnectionStateChangedListeners.add(listener);
        }
    }
    public void removeOnConnectionStateChangedListener(OnConnectionStateChangedListener listener) {
        if(listener == null){
            return;
        }
        mOnConnectionStateChangedListeners.remove(listener);
    }
    private void fireOnConnectionStateChangedListeners(final ConnectionState newState, final ConnectionState lastState, final boolean sync){
        if(mOnConnectionStateChangedListeners.isEmpty()){
            return;
        }
        final ArrayList<OnConnectionStateChangedListener> list = new ArrayList<>(mOnConnectionStateChangedListeners);
        getUiExecutor(sync).execute(new Runnable() {
            @Override
            public void run() {
                for(OnConnectionStateChangedListener l : list){
                    l.onConnectionStateChanged(OkBleClient.this, newState, lastState);
                }
            }
        });
    }


    private final ArrayList<OnDataReceivedListener> mOnDataReceivedListeners = new ArrayList<>(2);
    public void addOnDataReceivedListener(OnDataReceivedListener listener) {
        if(listener == null){
            return;
        }
        if(!mOnDataReceivedListeners.contains(listener)){
            mOnDataReceivedListeners.add(listener);
        }
    }
    public void removeOnDataReceivedListener(OnDataReceivedListener listener) {
        if(listener == null){
            return;
        }
        mOnDataReceivedListeners.remove(listener);
    }
    private void fireOnDataReceivedListeners(final CharacteristicData data, final boolean sync){
        if(mOnDataReceivedListeners.isEmpty()){
            return;
        }
        final ArrayList<OnDataReceivedListener> list = new ArrayList<>(mOnDataReceivedListeners);
        getUiExecutor(sync).execute(new Runnable() {
            @Override
            public void run() {
                for(OnDataReceivedListener l : list){
                    l.onDataReceived(OkBleClient.this, data);
                }
            }
        });
    }


    private final ArrayList<OnConnectionUpdatedListener> mOnConnectionUpdatedListeners = new ArrayList<>(2);
    public void addOnConnectionUpdatedListener(OnConnectionUpdatedListener listener) {
        if(listener == null){
            return;
        }
        if(!mOnConnectionUpdatedListeners.contains(listener)){
            mOnConnectionUpdatedListeners.add(listener);
        }
    }
    public void removeOnConnectionUpdatedListener(OnConnectionUpdatedListener listener) {
        if(listener == null){
            return;
        }
        mOnConnectionUpdatedListeners.remove(listener);
    }
    private void fireOnConnectionUpdatedListeners(final ConnectionParameter connectionParameter){
        if(mOnConnectionUpdatedListeners.isEmpty()){
            return;
        }
        final ArrayList<OnConnectionUpdatedListener> list = new ArrayList<>(mOnConnectionUpdatedListeners);
        getUiExecutor(false).execute(new Runnable() {
            @Override
            public void run() {
                for(OnConnectionUpdatedListener l : list){
                    l.onConnectionUpdated(OkBleClient.this, connectionParameter);
                }
            }
        });
    }



    public static final class Builder {
        private boolean mDebuggable = false;
        private Context mContext;
        private BluetoothDevice mDevice;
        private GattConnector.Factory mGattConnectorFactory = GattConnector.Factory.DEFAULT;
        private ConnectionOptions.Factory mConnectionOptionsFactory = ConnectionOptions.Factory.DEFAULT;
        private ServiceValidator.Factory mServiceValidatorFactory = ServiceValidator.Factory.DEFAULT;

        public Builder context(Context context){
            if(context == null){
                throw new IllegalArgumentException("context can not be null!");
            }
            this.mContext = context.getApplicationContext();
            return this;
        }

        public Builder debuggable(boolean debuggable){
            this.mDebuggable = debuggable;
            return this;
        }

        public Builder device(BluetoothDevice device){
            if(device == null){
                throw new IllegalArgumentException("device can not be null!");
            }
            this.mDevice = device;
            return this;
        }

        public Builder device(String address){
            final BluetoothDevice device = toBluetoothDevice(address);
            if(device == null){
                throw new IllegalArgumentException(String.format("address:%s wrong format!", address));
            }
            return device(device);
        }

        private Builder gattConnectorFactory(GattConnector.Factory factory){
            if(factory == null){
                throw new IllegalArgumentException("factory of GattConnector.Factory can not be null!");
            }
            this.mGattConnectorFactory = factory;
            return this;
        }

        private Builder connectionOptionsFactory(ConnectionOptions.Factory factory){
            if(factory == null){
                throw new IllegalArgumentException("factory of ConnectionOptions.Factory can not be null!");
            }
            this.mConnectionOptionsFactory = factory;
            return this;
        }

        private Builder serviceValidatorFactory(ServiceValidator.Factory factory){
            if(factory == null){
                throw new IllegalArgumentException("factory of ServiceValidator.Factory can not be null!");
            }
            this.mServiceValidatorFactory = factory;
            return this;
        }

//        public Builder retryOnDisconnection(boolean retryOnDisconnection){
//            this.mRetryOnDisconnection = retryOnDisconnection;
//            return this;
//        }
//
//        public Builder reconnectOnDisconnection(boolean reconnectOnDisconnection){
//            this.mReconnectOnDisconnection = reconnectOnDisconnection;
//            return this;
//        }

        public OkBleClient build(){
            if(this.mContext == null){
                throw new IllegalArgumentException("please call context()!");
            }
            if(this.mDevice == null){
                throw new IllegalArgumentException("please call device()!");
            }
            return new OkBleClient(this);
        }
    }

    private final static UiExecutor sSyncUiExecutor = new UiExecutor(true);
    private final static UiExecutor sAsyncUiExecutor = new UiExecutor(false);
    private final static UiExecutor getUiExecutor(boolean sync){
        return sync ? sSyncUiExecutor: sAsyncUiExecutor;
    }
}