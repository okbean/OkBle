package okble.central.client;

import android.bluetooth.BluetoothGattService;

import java.util.List;

final class DefaultServiceValidator implements ServiceValidator{

    public DefaultServiceValidator(){

    }

    @Override
    public boolean onValidateServices(OkBleClient client, List<BluetoothGattService> services) {
        return true;
    }

}