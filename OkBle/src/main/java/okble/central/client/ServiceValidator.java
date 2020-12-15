package okble.central.client;

import android.bluetooth.BluetoothGattService;

import java.util.List;

public interface ServiceValidator {

    boolean onValidateServices(OkBleClient client, List<BluetoothGattService> services);

    interface Factory{

        ServiceValidator newServiceValidator();

        Factory DEFAULT = new Factory(){

            @Override
            public ServiceValidator newServiceValidator() {
                return new DefaultServiceValidator();
            }
        };
    }

}
