package okble.demo.ui.device2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import okble.demo.R;


public class LogAdapter extends RecyclerView.Adapter<LogViewHolder> {
    private List<LogInfo> mData;
    private final Context mContext;
    private LayoutInflater mInflater;
    private OnItemClickListener mClickListener;
    private OnItemLongClickListener mLongClickListener;
    private final DeviceViewModel mViewModel;
    private LifecycleOwner mLifecycleOwner;
    public LogAdapter(Context ctx, final DeviceViewModel viewModel, LifecycleOwner owner) {
        mViewModel = viewModel;
        mLifecycleOwner = owner;
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
        mData = new ArrayList<>(256);
        setupViewModel();

    }

    private void setupViewModel(){
        mViewModel.getLogInfo().observe(mLifecycleOwner, data->{
            if(data != null){
                add(data);
            }
        });

    }

    public void setData(List<LogInfo> list) {
        mData = (list != null) ? list : new ArrayList<LogInfo>();
        notifyDataSetChanged();
    }

    public void clear(){
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = mInflater.inflate(R.layout.item_log_info, parent, false);
        final LogViewHolder holder = new LogViewHolder(itemView);
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
    public void onBindViewHolder(LogViewHolder holder, int position) {
        final LogInfo item = getItem(position);
        holder.render(item);
    }


    public LogInfo getItem(int pos){
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
        return 0;
    }


    public void add(int pos,LogInfo item) {
        mData.add(pos, item);
        notifyItemInserted(pos);
    }

    public void add(LogInfo item) {
        mData.add(item);
        notifyDataSetChanged();
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