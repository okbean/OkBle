package okble.demo.ui.device2;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okble.demo.R;


public class LogViewHolder extends RecyclerView.ViewHolder{

    private SimpleDateFormat mFormat = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault());

    @BindView(R.id.time)
    TextView mTimeTv;

    @BindView(R.id.message)
    TextView mMessageTv;
    public LogViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void render(LogInfo data){
        mTimeTv.setText(mFormat.format(data.time));
        mMessageTv.setText(data.message);
    }




}
