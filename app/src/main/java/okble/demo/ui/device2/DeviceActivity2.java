package okble.demo.ui.device2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUIPagerAdapter;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import java.util.concurrent.atomic.AtomicReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import okble.central.client.ConnectionPriority;
import okble.central.client.ConnectionState;
import okble.central.client.Mtu;
import okble.central.client.Phy;
import okble.central.client.Rssi;
import okble.central.client.util.Hex;
import okble.demo.R;
import okble.demo.util.UuidUtils;

public class DeviceActivity2 extends AppCompatActivity implements GattDataAdapter.OnItemClickListener {
    private final static String TAG = DeviceActivity2.class.getSimpleName();

    public static void launch(final Activity ctx, final BluetoothDevice device){
        final Intent intent = new Intent(ctx, DeviceActivity2.class);
        intent.putExtra("device", device);
        ctx.startActivity(intent);
    }

    @BindView(R.id.pager)
    QMUIViewPager mViewPager;
    @BindView(R.id.tabs)
    QMUITabSegment mTabSegment;
    @BindView(R.id.topbar)
    QMUITopBar mTopBar;

    Button mButton;
    private DeviceViewModel mViewModel;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device2);
        ButterKnife.bind(this, this);
        mViewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        final Intent intent = getIntent();
        final BluetoothDevice device = intent == null ?
                null : intent.getParcelableExtra("device");
        if(device == null){
            finish();
            return;
        }
        final String name = device.getName();
        final String address = device.getAddress();
        mTopBar.setTitle((name == null ? "N/A": name));
        mTopBar.setSubTitle( address );
        mTopBar.addLeftBackImageButton().setOnClickListener((v)->{
            finish();
        });
        mButton = mTopBar.addRightTextButton("", 0);
        setupViewModel(device);

        initGattRecyclerView();
        initLogRecyclerView();
        initGroupListView();
        initPagers();

        mViewModel.connect();
    }

    private void setupViewModel(final BluetoothDevice device){
        mViewModel.setDevice(device);
        mViewModel.getConnectionState().observe(this, newState->{
            String val = "";
             View.OnClickListener listener = null;
            if(newState == ConnectionState.Connected){
                val = "CONNECTED";
                listener = (v)->{
                    mViewModel.close();
                };
            }else if(newState == ConnectionState.Connecting){
                val = "CONNECTING";
                listener = (v)->{
                    //mViewModel.close();
                };
            }else if(newState == ConnectionState.Disconnected){
                val = "DISCONNECTED";
                listener = (v)->{
                    mViewModel.connect();
                };
            }else{

            }
            mButton.setClickable(true);
            mButton.setText(val);
            mButton.setOnClickListener(listener);
        });
    }


    View mListViewLayout;
    QMUIGroupListView mGroupListView;
    private void initGroupListView(){
        mListViewLayout = LayoutInflater.from(this).inflate(R.layout.layout_requests, null);
        mGroupListView = mListViewLayout.findViewById(R.id.groupListView);
        final int size = QMUIDisplayHelper.dp2px(this, 4);
        final QMUIGroupListView.Section section = QMUIGroupListView.newSection(this)
                .setTitle("")
                .setDescription("")
                .setLeftIconSize(size, ViewGroup.LayoutParams.WRAP_CONTENT);

        addReadDeviceName(section);
        addReadDeviceAppearance(section);

        addEnableIndication(section);
        addDisableIndication(section);

        addWriteDescriptor(section);
        addReadDescriptor(section);

        addSetMtu(section);
        addReadRssi(section);

        addReadPhy(section);
        addSetConnectionPriority(section);

        section.addTo(mGroupListView);
    }

    private GattDataAdapter mGattDataAdapter = null;
    private RecyclerView mGattRecyclerView = null;

    private LogAdapter mLogAdapter = null;
    private RecyclerView mLogRecyclerView = null;

    private void initGattRecyclerView(){
        final RecyclerView gattRecyclerView = new RecyclerView(this);
        final SimpleItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        gattRecyclerView.setItemAnimator(itemAnimator);
        gattRecyclerView.setLayoutManager(layoutManager);
        gattRecyclerView.setItemViewCacheSize(10);
        final GattDataAdapter gattAdapter = new GattDataAdapter(this, mViewModel, this);
        gattAdapter.setOnItemClickListener(this);
        gattRecyclerView.setAdapter(gattAdapter);
        mGattDataAdapter = gattAdapter;
        mGattRecyclerView = gattRecyclerView;
    }

    private void initLogRecyclerView(){
        final RecyclerView logRecyclerView = new RecyclerView(this);
        final SimpleItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        logRecyclerView.setItemAnimator(itemAnimator);
        logRecyclerView.setLayoutManager(layoutManager);
        logRecyclerView.setItemViewCacheSize(10);
        final LogAdapter logAdapter = new LogAdapter(this, mViewModel, this);
        logRecyclerView.setAdapter(logAdapter);
        mLogAdapter = logAdapter;
        mLogRecyclerView = logRecyclerView;
    }

    private void initPagers() {
        final QMUIPagerAdapter pagerAdapter = new QMUIPagerAdapter() {

            @Override
            protected Object hydrate(ViewGroup container, int position) {
                if(position == 0){
                    return mGattRecyclerView;
                }else if(position == 1){
                    return mListViewLayout;
                }else if(position == 2){
                    return mLogRecyclerView;
                }
                return null;
            }

            @Override
            protected void populate(ViewGroup container, Object item, int position) {
                container.addView((View) item);
            }

            @Override
            protected void destroy(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "CLIENT";
                    case 1:
                        return "REQUESTS";
                    case 2:
                        return "LOG";
                    default:
                        return "NULL";
                }
            }
        };
        mViewPager.setAdapter(pagerAdapter);
        mTabSegment.setupWithViewPager(mViewPager);
    }


    @Override
    public void onResume(){
        super.onResume();
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        mViewModel.close();
    }


    @Override
    public void onItemClick(View itemView, int pos) {
        final GattData item = mGattDataAdapter.getItem(pos);
        if(item == null){
            return;
        }
        if(item.isService()){
            return;

        }else if(item.isCharacteristic()){
            BluetoothGattCharacteristic characteristic = item.get();
            mViewModel.readCharacteristic(characteristic);
        }else if(item.isDescriptor()){
            BluetoothGattDescriptor descriptor = item.get();
            mViewModel.readDescriptor(descriptor);
        }
    }

    private void promptReadCharacteristic(final BluetoothGattCharacteristic characteristic){
        final String[] items = {"READ"};
        final Dialog dialog = new AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            mViewModel.readCharacteristic(characteristic);
                        }else if(which == 1){

                        }
                    }
                })
                .setCancelable(true).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void promptReadDescriptor(final BluetoothGattDescriptor descriptor){
        final boolean isCccd = UuidUtils.isCccd(descriptor.getUuid());
        final String[] items = {"READ"};
        final Dialog dialog = new AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            mViewModel.readDescriptor(descriptor);
                        }else if(which == 1){
                            if(isCccd){

                            }else {

                            }
                        }
                    }
                })
                .setCancelable(true).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }




    private void addReadDeviceName(final QMUIGroupListView.Section section){
        final boolean[] requesting = new boolean[1];
        final QMUICommonListItemView item =
                mGroupListView.createItemView("Read Characteristic(Device Name)");
        item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        item.setOrientation(QMUICommonListItemView.VERTICAL);
        item.setDetailText(" ");
        section.addItemView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requesting[0]){
                    return;
                }
                requesting[0] = true;
                item.setDetailText("Requesting...");
                mViewModel.readDeviceName();
            }
        });
        mViewModel.getReadDeviceNameTask().observe(this, task->{
            if(task != null){
                requesting[0] = false;
                if(task.isSuccess()){
                    final byte[] val = task.getResult();
                    item.setDetailText("Device Name:" + new String(val));
                }else{

                    item.setDetailText("Request Failed!"+ getMessage(task.getException()));
                }
            }
        });
    }

    private final static String getMessage(final Exception ex){
        final String msg = ex == null ? "" : ex.getMessage();
        final String val = msg == null ? "" : msg;
        return val;
    }

    private void addReadRssi(final QMUIGroupListView.Section section){
        final boolean[] requesting = new boolean[1];
        final QMUICommonListItemView item =
                mGroupListView.createItemView("Read Remote Rssi");
        item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        item.setOrientation(QMUICommonListItemView.VERTICAL);
        item.setDetailText(" ");
        section.addItemView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requesting[0]){
                    return;
                }
                requesting[0] = true;
                item.setDetailText("Requesting...");
                mViewModel.readRemoteRssi();
            }
        });
        mViewModel.getReadRemoteRssiTask().observe(this, task->{
            if(task != null){
                requesting[0] = false;
                if(task.isSuccess()){
                    final Rssi val = task.getResult();
                    item.setDetailText("Rssi:" + val.value());
                }else{
                    item.setDetailText("Request Failed!"+ getMessage(task.getException()));
                }
            }
        });
    }



    private void addReadPhy(final QMUIGroupListView.Section section){
        final boolean[] requesting = new boolean[1];
        final QMUICommonListItemView item =
                mGroupListView.createItemView("Read Phy");
        item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        item.setOrientation(QMUICommonListItemView.VERTICAL);
        item.setDetailText(" ");
        section.addItemView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requesting[0]){
                    return;
                }
                requesting[0] = true;
                item.setDetailText("Requesting...");
                mViewModel.readPhy();
            }
        });
        mViewModel.getReadPhyTask().observe(this, task->{
            if(task != null){
                requesting[0] = false;
                if(task.isSuccess()){
                    final Phy val = task.getResult();
                    item.setDetailText(String.format("Rx:%d, Tx:%d", val.rx(), val.tx()));
                }else{
                    item.setDetailText("Request Failed! " + getMessage(task.getException()));
                }
            }
        });
    }



    private void addReadDeviceAppearance(final QMUIGroupListView.Section section){
        final boolean[] requesting = new boolean[1];
        final QMUICommonListItemView item =
                mGroupListView.createItemView("Read Characteristic(Appearance)");
        item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        item.setOrientation(QMUICommonListItemView.VERTICAL);
        item.setDetailText(" ");
        section.addItemView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requesting[0]){
                    return;
                }
                requesting[0] = true;
                item.setDetailText("Requesting...");
                mViewModel.readDeviceAppearance();
            }
        });
        mViewModel.getReadDeviceAppearanceTask().observe(this, task->{
            if(task != null){
                requesting[0] = false;
                if(task.isSuccess()){
                    final byte[] val = task.getResult();
                    item.setDetailText(String.format("Device Appearance:(0x)%s", Hex.toString(val, '-')));
                }else{
                    item.setDetailText("Request Failed!"+ getMessage(task.getException()));
                }
            }
        });
    }


    private void addEnableIndication(final QMUIGroupListView.Section section){
        final boolean[] requesting = new boolean[1];
        final QMUICommonListItemView item =
                mGroupListView.createItemView("Enable Indication(UUID 0x2a05)");
        item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        item.setOrientation(QMUICommonListItemView.VERTICAL);
        item.setDetailText(" ");
        section.addItemView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requesting[0]){
                    return;
                }
                requesting[0] = true;
                item.setDetailText("Requesting...");
                mViewModel.enableIndication();
            }
        });
        mViewModel.getEnableIndicationTask().observe(this, task->{
            if(task != null){
                requesting[0] = false;
                if(task.isSuccess()){
                    item.setDetailText("Request Success!");
                }else{
                    item.setDetailText("Request Failed!"+ getMessage(task.getException()));
                }
            }
        });
    }


    private void addDisableIndication(final QMUIGroupListView.Section section){
        final boolean[] requesting = new boolean[1];
        final QMUICommonListItemView item =
                mGroupListView.createItemView("Disable Indication(UUID 0x2a05)");
        item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        item.setOrientation(QMUICommonListItemView.VERTICAL);
        item.setDetailText(" ");
        section.addItemView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requesting[0]){
                    return;
                }
                requesting[0] = true;
                item.setDetailText("Requesting...");
                mViewModel.disableIndication();
            }
        });
        mViewModel.getDisableIndicationTask().observe(this, task->{
            if(task != null){
                requesting[0] = false;
                if(task.isSuccess()){
                    item.setDetailText("Request Success!");
                }else{
                    item.setDetailText("Request Failed!"+ getMessage(task.getException()));
                }
            }
        });
    }


    private void addWriteDescriptor(final QMUIGroupListView.Section section){
        final boolean[] requesting = new boolean[1];
        final QMUICommonListItemView item =
                mGroupListView.createItemView("Write Descriptor(UUID 0x2902)");
        item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        item.setOrientation(QMUICommonListItemView.VERTICAL);
        item.setDetailText(" ");
        section.addItemView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWriteDescriptor(item, requesting);
            }
        });
        mViewModel.getWriteDescriptorTask().observe(this, task->{
            if(task != null){
                requesting[0] = false;
                if(task.isSuccess()){
                    item.setDetailText("Request Success!");
                }else{
                    item.setDetailText("Request Failed!"+ getMessage(task.getException()));
                }
            }
        });
    }

    private void addWriteDescriptor(final QMUICommonListItemView item, final boolean[] requesting){
        if(requesting[0]){
            return;
        }
        final String[] items = {"(0x)00-00", "(0x)01-00" ,"(0x)02-00"};
        final byte[][] datas = {BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE,
                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE,
                BluetoothGattDescriptor.ENABLE_INDICATION_VALUE };
        final Dialog dialog = new AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requesting[0] = true;
                        item.setDetailText("Requesting...");
                        mViewModel.writeDescriptor(datas[which]);
                    }
                })
                .setCancelable(true).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }




    private void addReadDescriptor(final QMUIGroupListView.Section section){
        final boolean[] requesting = new boolean[1];
        final QMUICommonListItemView item =
                mGroupListView.createItemView("Read Descriptor(UUID 0x2902)");
        item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        item.setOrientation(QMUICommonListItemView.VERTICAL);
        item.setDetailText(" ");
        section.addItemView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(requesting[0]){
                    return;
                }
                requesting[0] = true;
                item.setDetailText("Requesting...");
                mViewModel.readDescriptor();
            }
        });
        mViewModel.getReadDescriptorTask().observe(this, task->{
            if(task != null){
                requesting[0] = false;
                if(task.isSuccess()){
                    final byte[] val = task.getResult();
                    item.setDetailText(String.format("Value:(0x)%s", Hex.toString(val, '-')));
                }else{
                    item.setDetailText("Request Failed!"+ getMessage(task.getException()));
                }
            }
        });
    }


    private void addSetMtu(final QMUIGroupListView.Section section){
        final boolean[] requesting = new boolean[1];
        final QMUICommonListItemView item =
                mGroupListView.createItemView("Set Mtu");
        item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        item.setOrientation(QMUICommonListItemView.VERTICAL);
        item.setDetailText(" ");
        section.addItemView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSetMtu(item, requesting);
            }
        });
        mViewModel.getSetMtuTaskTask().observe(this, task->{
            if(task != null){
                requesting[0] = false;
                if(task.isSuccess()){
                    final Mtu val = task.getResult();
                    item.setDetailText("Mtu:" + val.value());
                }else{
                    item.setDetailText("Request Failed!"+ getMessage(task.getException()));
                }
            }
        });
    }

    private void addSetMtu(final QMUICommonListItemView item, final boolean[] requesting){
        if(requesting[0]){
            return;
        }
        final String[] items = {"23", "32" ,"50", "64", "100","128","200","250",
                "256","300", "400", "512", "517"};
        final Dialog dialog = new AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requesting[0] = true;
                        item.setDetailText("Requesting...");
                        int mtu = 100;
                        try{
                            mtu = Integer.parseInt(items[which]);
                        }catch (Exception ex){

                        }
                        mViewModel.setMtu(mtu);
                    }
                })
                .setCancelable(true).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }



    private void addWriteCharacteristic(final QMUIGroupListView.Section section){
        final boolean[] requesting = new boolean[1];
        final QMUICommonListItemView item =
                mGroupListView.createItemView("Write Characteristic(S:0xfe86-C:0xfe01)");
        item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        item.setOrientation(QMUICommonListItemView.VERTICAL);
        item.setDetailText(" ");
        section.addItemView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWriteCharacteristic(item, requesting);
            }
        });
        mViewModel.getWriteCharacteristicForHuaweiDeviceTask().observe(this, task->{
            if(task != null){
                requesting[0] = false;
                if(task.isSuccess()){
                    item.setDetailText("Request Success!");
                }else{
                    item.setDetailText("Request Failed!"+ getMessage(task.getException()));
                }
            }
        });
    }

    private void addWriteCharacteristic(final QMUICommonListItemView item, final boolean[] requesting){
        if(requesting[0]){
            return;
        }
        final AtomicReference<QMUIDialog.EditTextDialogBuilder> ref = new AtomicReference();
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(this)
                .setTitle("WriteCharacteristic")
                .addAction(R.string.btn_cancel, new QMUIDialogAction.ActionListener(){
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction(R.string.btn_ok, new QMUIDialogAction.ActionListener(){
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        final String str = ref.get().getEditText().getText().toString();
                        if(!TextUtils.isEmpty(str)){
                            requesting[0] = true;
                            item.setDetailText("Requesting...");
                            mViewModel.writeCharacteristicForHuaweiDevice(str.getBytes());
                        }
                        dialog.dismiss();
                    }
                });
        builder.create().show();
        ref.set(builder);
    }







    private void addSetConnectionPriority(final QMUIGroupListView.Section section){
        final boolean[] requesting = new boolean[1];
        final QMUICommonListItemView item =
                mGroupListView.createItemView("Set Connection Priority");
        item.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        item.setOrientation(QMUICommonListItemView.VERTICAL);
        item.setDetailText(" ");
        section.addItemView(item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSetConnectionPriority(item, requesting);
            }
        });
        mViewModel.getSetConnectionPriorityTask().observe(this, task->{
            if(task != null){
                requesting[0] = false;
                if(task.isSuccess()){
                    item.setDetailText("Request Success!");
                }else{
                    item.setDetailText("Request Failed!"+ getMessage(task.getException()));
                }
            }
        });
    }

    private void addSetConnectionPriority(final QMUICommonListItemView item, final boolean[] requesting){
        if(requesting[0]){
            return;
        }
        final String[] items = {"BALANCED", "HIGH" ,"LOW_POWER" };
        final ConnectionPriority[] datas = {ConnectionPriority.BALANCED, ConnectionPriority.HIGH ,ConnectionPriority.LOW_POWER };
        final Dialog dialog = new AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requesting[0] = true;
                        item.setDetailText("Requesting...");
                        mViewModel.setConnectionPriority(datas[which]);
                    }
                })
                .setCancelable(true).create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
