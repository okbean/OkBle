package okble.demo.ui.device2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import okble.demo.R;

public abstract class GattViewHolder extends RecyclerView.ViewHolder{
    public GattViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void render(GattData data, final byte[] value);



    public static GattViewHolder newViewHolder(ViewGroup parent, int viewType){
        GattViewHolder val = null;
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == GattData.SERVICE){
            final View itemView = inflater.inflate(R.layout.item_service, parent, false);
            val = new ServiceViewHolder(itemView);
        }else if(viewType == GattData.CHARACTERISTIC){
            final View itemView = inflater.inflate(R.layout.item_characteristic, parent, false);
            val = new CharacteristicViewHolder(itemView);
        }else if(viewType == GattData.DESCRIPTOR){
            final View itemView = inflater.inflate(R.layout.item_descriptor, parent, false);
            val = new DescriptorViewHolder(itemView);
        }
        return val;
    }
}
