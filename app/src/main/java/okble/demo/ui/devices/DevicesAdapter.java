package okble.demo.ui.devices;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okble.central.scanner.BleScanResult;
import okble.demo.R;


public final class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

	private OnItemClickListener onItemClickListener;

	@FunctionalInterface
	public interface OnItemClickListener {
		void onItemClick(final BleScanResult device);
	}

	public void setOnItemClickListener(final OnItemClickListener listener) {
		onItemClickListener = listener;
	}

	private List<BleScanResult> devices;
	public DevicesAdapter(@NonNull final DevicesFragment fragment,
                          @NonNull final LiveData<List<BleScanResult>> devicesLiveData) {
		devicesLiveData.observe(fragment, newDevices -> {
			devices = newDevices;
			notifyDataSetChanged();
		});
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
		final View layoutView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.list_item_device, parent, false);
		return new ViewHolder(layoutView);
	}

	@Override
	public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
		final BleScanResult device = devices.get(position);
		final String deviceName = device.device().getName();
		if (!TextUtils.isEmpty(deviceName)){
			holder.deviceName.setText(deviceName);
		}else{
			holder.deviceName.setText("N/A");
		}
		holder.deviceAddress.setText(device.device().getAddress());

		holder.itemView.setOnClickListener(v -> {
			if (onItemClickListener != null) {
				onItemClickListener.onItemClick(devices.get(position));
			}
		});
	}

	@Override
	public long getItemId(final int position) {
		return devices.get(position).hashCode();
	}

	@Override
	public int getItemCount() {
		return devices != null ? devices.size() : 0;
	}

	public boolean isEmpty() {
		return getItemCount() == 0;
	}

	final static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.device_address)
		TextView deviceAddress;
		@BindView(R.id.device_name)
		TextView deviceName;

		private ViewHolder(@NonNull final View view) {
			super(view);
			ButterKnife.bind(this, view);
		}
	}
}
