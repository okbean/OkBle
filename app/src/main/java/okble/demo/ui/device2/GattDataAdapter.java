package okble.demo.ui.device2;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class GattDataAdapter extends RecyclerView.Adapter<GattViewHolder> {
    private List<GattData> mData;
    private final Context mContext;
    private LayoutInflater mInflater;
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;
    private final DeviceViewModel mViewModel;
    private LifecycleOwner mLifecycleOwner;
    public GattDataAdapter(Context ctx, final DeviceViewModel viewModel, LifecycleOwner owner) {
        mViewModel = viewModel;
        mLifecycleOwner = owner;
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
        setupViewModel();
    }

    private void setupViewModel(){
        mViewModel.getGattData().observe(mLifecycleOwner, list->{
            setData(list);
        });

        mViewModel.getConnectionState().observe(mLifecycleOwner, newState->{

        });

        mViewModel.getDescriptorData().observe(mLifecycleOwner, data->{
            if(data != null){
                addUuidData(getKey(data.service(), data.characteristic(), data.descriptor()), data.data());
            }
        });

        mViewModel.getCharacteristicData().observe(mLifecycleOwner, data->{
            if(data != null){
                addUuidData(getKey(data.service(), data.characteristic()), data.data());
            }
        });
    }

    private static String getKey(final String k1, final  String k2, final String k3){
        return k1 + "@" + k2 + "@" + k3;
    }

    private static String getKey(final String k1, final  String k2){
        return k1 + "@" + k2;
    }

    private static String getKey(final UUID k1, final  UUID k2, final UUID k3){
        return k1.toString() + "@" + k2.toString() + "@" + k3.toString();
    }

    private static String getKey(final UUID k1, final  UUID k2){
        return k1.toString() + "@" + k2.toString();
    }

    private static String getKey(final GattData item){
        if(item.isService()){
            final BluetoothGattService service =  item.get();
            return service.getUuid().toString();
        }else if(item.isCharacteristic()){
            final BluetoothGattCharacteristic chars = item.get();
            final UUID c = chars.getUuid();
            final UUID s = chars.getService().getUuid();
            return getKey(s, c);
        }else if(item.isDescriptor()){
            final BluetoothGattDescriptor descriptor = item.get();
            final UUID dUuid = descriptor.getUuid();
            final UUID cUuid = descriptor.getCharacteristic().getUuid();
            final UUID sUuid = descriptor.getCharacteristic().getService().getUuid();
            return getKey(sUuid, cUuid, dUuid);
        }
        return null;
    }

//    private static String getKey(final BluetoothGattDescriptor descriptor){
//        final UUID d = descriptor.getUuid();
//        final UUID c = descriptor.getCharacteristic().getUuid();
//        final UUID s = descriptor.getCharacteristic().getService().getUuid();
//        return getKey(s, c, d);
//    }

    public void setData(List<GattData> list) {
        mData = (list != null) ? list : new ArrayList<GattData>();
        notifyDataSetChanged();
    }

    private HashMap<String,byte[]> mUuidData = new HashMap<String,byte[]>();
    public void addUuidData(String uuid, byte[] data){
        mUuidData.put(uuid, data);
        notifyDataSetChanged();
    }

    public void clear(){
        mData = null;
        mUuidData.clear();
        notifyDataSetChanged();
    }

    @Override
    public GattViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final GattViewHolder holder = GattViewHolder.newViewHolder(parent, viewType);
        if (mClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            });
        }

        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onItemLongClick(holder.itemView, holder.getLayoutPosition());
                    return true;
                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(GattViewHolder holder, int position) {
        final GattData item = getItem(position);
        final byte[] val = getUuidData(item);
        holder.render(item, val);
    }

    private byte[] getUuidData(final GattData item){
        final String key = getKey(item);
        final byte[] val = mUuidData.get(key);
        return val;
    }

    public GattData getItem(int pos){
        final int count = getItemCount();
        if(count <=0 || pos < 0 || pos >= count){
            return null;
        }
        return mData.get(pos);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(final int position) {
        final GattData item = getItem(position);
        return item.type();
    }


    public void add(int pos,GattData item) {
        mData.add(pos, item);
        notifyItemInserted(pos);
    }

    public void delete(int pos) {
        mData.remove(pos);
        notifyItemRemoved(pos);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mLongClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int pos);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int pos);
    }
}