package okble.demo.ui.device2;

import android.bluetooth.BluetoothGattCharacteristic;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import okble.demo.R;

public class CharacteristicViewHolder extends GattViewHolder{

    @BindView(R.id.name)
    TextView mTextTv;

    @BindView(R.id.uuid)
    TextView mUuidTv;

    @BindView(R.id.properties)
    TextView mPropertiesTv;

    @BindView(R.id.value)
    TextView mValueTv;

    public CharacteristicViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void render(GattData data, final byte[] value){
        final BluetoothGattCharacteristic characteristic = data.get();

        mTextTv.setText(CharacteristicResolver.resolveName(characteristic));

        mUuidTv.setText("UUID: " + CharacteristicResolver.resolveUuid(characteristic));

        mPropertiesTv.setText("Properties: " + CharacteristicResolver.resolveProperties(characteristic));

        final String val = CharacteristicResolver.resolveData(data.uuid().toString(), value);
        mValueTv.setText("Value: ");
        if(!TextUtils.isEmpty(val)){
            mValueTv.append(val);
        }
    }


}
