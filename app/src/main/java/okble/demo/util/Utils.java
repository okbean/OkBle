package okble.demo.util;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;


import java.util.List;

import okble.demo.R;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public final class Utils {



    public static boolean isBluetoothEnabled(){
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        final boolean val = adapter != null && adapter.isEnabled();
        return val;
    }



    public static boolean isLocationSettingsEnabled(final Context ctx){
        try {
            final int mode = Settings.Secure.getInt(ctx.getContentResolver(), Settings.Secure.LOCATION_MODE);
            return (mode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY) ||
                    (mode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING) ||
                    (mode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public static void requestLocationSourcesSettings(Activity ctx, final int requestCode){
        try{
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            ctx.startActivityForResult(intent, requestCode);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    public static void requestEnableBluetooth(final Activity ctx) {
        try{
            final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ctx.startActivity(enableIntent);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public static boolean hasLocationPermissions(Context ctx){
        final String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        final boolean value =  EasyPermissions.hasPermissions(ctx, perms);
        return value;
    }



    public static boolean isDeviceScanEnabled(final Context ctx){
        return isBluetoothEnabled() &&
                hasLocationPermissions(ctx) &&
                (Build.VERSION.SDK_INT <  Build.VERSION_CODES.M ||
                        isLocationSettingsEnabled(ctx));
    }


    public static void promptRequestLocationPermissions(final Activity ctx, final int requestCode){
        final String[] perms = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        final String rationale = ctx.getString(R.string.easy_permissions_prompt_access_location_permissions_rationale);

        final int confirmRes = R.string.easy_permissions_settings_confirm;
        final int cancelRes = R.string.easy_permissions_settings_cancel;

        final PermissionRequest req = new PermissionRequest.Builder(ctx, requestCode, perms)
                .setRationale(rationale)
                .setPositiveButtonText(confirmRes)
                .setNegativeButtonText(cancelRes)
                .build();

        EasyPermissions.requestPermissions(req);
    }


    public static void promptForSomePermissionPermanentlyDenied(final Activity ctx, final int requestCode, final List<String> perms){
        final String rationale = ctx.getString(R.string.easy_permissions_prompt_manual_setting_required_permissions_rationale);
        final String title = ctx.getString(R.string.easy_permissions_prompt_manual_setting_required_permissions_title);
        final String confirmBtn = ctx.getString(R.string.easy_permissions_settings_confirm);
        final String cancelBtn = ctx.getString(R.string.easy_permissions_settings_cancel);
        new AppSettingsDialog.Builder(ctx)
                .setRationale(rationale)
                .setTitle(title)
                .setPositiveButton(confirmBtn)
                .setNegativeButton(cancelBtn)
                .setRequestCode(requestCode)
                .build()
                .show();
    }


    public static boolean somePermissionPermanentlyDenied(Activity ctx, List<String> perms){
        final boolean value =  EasyPermissions.somePermissionPermanentlyDenied(ctx, perms);
        return value;
    }

    public static void onRequestPermissionsResult(final Activity ctx, final int requestCode,   String[] permissions,  int[] grantResults,   Object... receivers) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, receivers);
    }



    private Utils(){}
}
