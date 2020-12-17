package okble.demo.ui.device2;

import android.bluetooth.BluetoothGattDescriptor;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import okble.demo.R;


public class DescriptorViewHolder extends GattViewHolder{

    @BindView(R.id.name)
    TextView mTextTv;

    @BindView(R.id.uuid)
    TextView mUuidTv;

    @BindView(R.id.value)
    TextView mValueTv;

    public DescriptorViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void render(GattData data, final byte[] value){
        final BluetoothGattDescriptor descriptor = data.get();
        mTextTv.setText(DescriptorResolver.resolveName(descriptor));
        mUuidTv.setText("UUID: " + DescriptorResolver.resolveUuid(descriptor));

        final String val = DescriptorResolver.resolveData(data.uuid().toString(), value);
        mValueTv.setText("Value: ");
        if(!TextUtils.isEmpty(val)){
            mValueTv.append(val);
        }
    }

}
