package okble.demo.ui.devices;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import okble.central.scanner.BleScanListener;
import okble.central.scanner.BleScanResult;
import okble.central.scanner.OkBleScanner;
import okble.central.scanner.ScanConfig;

public class DevicesViewModel extends AndroidViewModel {
    private final static String TAG = DevicesViewModel.class.getSimpleName();

    public DevicesViewModel(@NonNull final Application application) {
        super(application);
        mDevices = new MutableLiveData<>();
        mDevices.setValue(null);

        mScanState = new MutableLiveData<>();
        mScanState.setValue(ScanState.None);
    }

    private MutableLiveData<List<BleScanResult>> mDevices;
    private MutableLiveData<ScanState> mScanState;
    public LiveData<List<BleScanResult>> getDevices() {
        return mDevices;
    }

    public LiveData<ScanState> getScanState() {
        return mScanState;
    }

    private final HashSet<BleScanResult> mCache = new HashSet<BleScanResult>();

    public void startScan(){
        mCache.clear();
        mDevices.setValue(null);
        final OkBleScanner scanner = OkBleScanner.getDefault();
        scanner.stopScan();
        scanner.clearScanListeners();
        final ScanConfig config = new ScanConfig.Builder()
                .scanPeriodCount(1)
                .scanDelay(5_000L)
                .scanInterval(5_000L)
                .scanPeriod(20_000L)
                .build();
        scanner.addScanListener(mBleScanListener);
        scanner.startScan(getApplication(), config);
    }

    public void stopScan(){
        final OkBleScanner scanner = OkBleScanner.getDefault();
        scanner.stopScan();
    }

    private final BleScanListener mBleScanListener = new BleScanListener(){
        @Override
        public void onScanStart(OkBleScanner scanner){
            Log.i(TAG, "onScanStart");
            mScanState.setValue(ScanState.Scanning);
        }
        @Override
        public void onScanIdle(OkBleScanner scanner){
            Log.i(TAG, "onScanIdle : " +   scanner.isScanIdle());
        }
        @Override
        public void onScanning(OkBleScanner scanner){
            Log.i(TAG, "onScanning :" + scanner.isScanning());
        }
        @Override
        public void onScanResult(OkBleScanner scanner, BleScanResult result){
            if(!mCache.contains(result)){
                Log.i(TAG, "onScanResult: " + result);
            }
            mCache.add(result);
            final ArrayList<BleScanResult> devices = new ArrayList<BleScanResult>(mCache);
            mDevices.setValue(devices);
        }
        @Override
        public void onScanComplete(OkBleScanner scanner, int code){
            Log.e(TAG, "onScanComplete: " + code);
            mScanState.setValue(ScanState.Complete);
        }
    };



    @Override
    public void onCleared() {
        final OkBleScanner scanner = OkBleScanner.getDefault();
        scanner.removeScanListener(mBleScanListener);
    }

    public enum ScanState{
        None, Scanning, Complete;
    }

}