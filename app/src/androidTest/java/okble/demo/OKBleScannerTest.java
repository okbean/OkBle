package okble.demo;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okble.central.scanner.BleScanListener;
import okble.central.scanner.BleScanResult;
import okble.central.scanner.OkBleScanner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.JVM)
public class OKBleScannerTest {
    private final static String TAG = ":" + OKBleScannerTest.class.getSimpleName();

    private final static String MY_DEVICE_ADDRESS = "4C:59:9A:28:92:0A";
    private final static String HUAWEI_DEVICE_ADDRESS = "20:DA:22:1D:64:A0";
    private final static String TESTED_DEVICE_ADDRESS = HUAWEI_DEVICE_ADDRESS;


    @Test
    public void testAsyncScan(){
        final OkBleScanner scanner = OkBleScanner.getDefault();
        final ArrayList<BleScanResult> list = new ArrayList<BleScanResult>();
        scanner.addScanListener(new BleScanListener() {
            @Override
            public void onScanStart(OkBleScanner scanner) {
                Log.d(TAG, "onScanStart");
            }

            @Override
            public void onScanIdle(OkBleScanner scanner) {
                Log.d(TAG, "onScanIdle");
            }

            @Override
            public void onScanning(OkBleScanner scanner) {
                Log.d(TAG, "onScanning");
            }

            @Override
            public void onScanResult(OkBleScanner scanner, BleScanResult result) {
                Log.d(TAG, "onScanResult:" + result);
                list.add(result);
            }

            @Override
            public void onScanComplete(OkBleScanner scanner, int code) {
                Log.d(TAG, "onScanComplete code:" + code);
                scanner.removeScanListener(this);
            }
        });
        final Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        scanner.startScan(ctx);
        SystemClock.sleep(80_000L);
        scanner.stopScan();
        assertTrue(list.size()>0);
    }


    @Test
    public void testSyncScan(){
        final OkBleScanner scanner = OkBleScanner.getDefault();
        final Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();

        final BleScanResult result = scanner.scanForResult(ctx, TESTED_DEVICE_ADDRESS, 60, TimeUnit.SECONDS);
        assertNotNull(result);
    }

}
