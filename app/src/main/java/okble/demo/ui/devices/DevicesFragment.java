package okble.demo.ui.devices;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okble.central.scanner.BleScanResult;
import okble.central.scanner.OkBleScanner;
import okble.demo.R;
import okble.demo.ui.device.DeviceActivity;
import okble.demo.ui.device2.DeviceActivity2;
import okble.demo.util.Utils;

public class DevicesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, DevicesAdapter.OnItemClickListener{

    DevicesViewModel mViewModel;

    @BindView(R.id.list_devices)
    RecyclerView mListView;
    @BindView(R.id.list_empty_view)
    View mEmptyView;
    @BindView(R.id.tv_prompt)
    TextView mPrompt;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(DevicesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.bind(this, root);

        mListView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mListView.addItemDecoration(new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL));
        final RecyclerView.ItemAnimator animator = mListView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        final DevicesAdapter adapter = new DevicesAdapter(this, mViewModel.getDevices());
        adapter.setOnItemClickListener(this);
        mListView.setAdapter(adapter);

        mViewModel.getDevices().observe(getViewLifecycleOwner(), newDevices -> {
            final DevicesViewModel.ScanState state = mViewModel.getScanState().getValue();
            if(newDevices == null || newDevices.size()<=0){
                if(state == DevicesViewModel.ScanState.None ||
                        state == DevicesViewModel.ScanState.Complete){
                    mEmptyView.setVisibility(View.VISIBLE);
                }
            }else{
                mEmptyView.setVisibility(View.GONE);
            }
        });

        mViewModel.getScanState().observe(getViewLifecycleOwner(), newState -> {
            if(newState == DevicesViewModel.ScanState.None ||
                    newState == DevicesViewModel.ScanState.Complete){
                mSwipeRefreshView.setRefreshing(false);
                final List<BleScanResult> list = mViewModel.getDevices().getValue();
                if(list != null && list.size() > 0){
                    mEmptyView.setVisibility(View.GONE);
                }else{
                    mEmptyView.setVisibility(View.VISIBLE);
                }
            }else if(newState == DevicesViewModel.ScanState.Scanning){
                mSwipeRefreshView.setRefreshing(true);
                mEmptyView.setVisibility(View.GONE);
            }
        });

        mSwipeRefreshView.setOnRefreshListener(this);

        return root;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        if(Utils.isDeviceScanEnabled(getActivity())){
            mViewModel.startScan();
        }else{

        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        mViewModel.stopScan();
    }


    @Override
    public void onItemClick(BleScanResult device) {
        OkBleScanner.getDefault().stopScan();
        DeviceActivity2.launch(getActivity(), device.device());
    }

    @Override
    public void onRefresh() {
        if(!Utils.isBluetoothEnabled()){
            Utils.requestEnableBluetooth(getActivity());
        }else if(!Utils.hasLocationPermissions(getActivity())){
            Utils.promptRequestLocationPermissions(getActivity(), 1024);
        }else if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.M &&
                !Utils.isLocationSettingsEnabled(getActivity())){
            promptEnableLocationSettings();
        }else{
            mViewModel.startScan();
            return;
        }
        mSwipeRefreshView.setRefreshing(false);
    }

    private void promptEnableLocationSettings(){
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.text_prompt_location_setting)
                .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.requestLocationSourcesSettings(getActivity(), 1025);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .create().show();
    }

}