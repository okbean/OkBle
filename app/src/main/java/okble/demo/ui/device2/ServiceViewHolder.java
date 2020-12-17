package okble.demo.ui.device2;

import android.bluetooth.BluetoothGattService;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import okble.demo.R;


public class ServiceViewHolder extends GattViewHolder{

    @BindView(R.id.name)
    TextView mTextTv;

    @BindView(R.id.uuid)
    TextView mUuidTv;

    @BindView(R.id.type)
    TextView mTypeTv;

    public ServiceViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    @Override
    public void render(GattData data, final byte[] value){
        final BluetoothGattService service = data.get();
        mTextTv.setText(ServiceResolver.resolveName(service));
        mUuidTv.setText("UUID: " + ServiceResolver.resolveUuid(service));
        mTypeTv.setText(ServiceResolver.resolveType(service));
    }

}
