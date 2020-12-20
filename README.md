# OkBle
BLE客户端框架，使BLE请求像HTTP请求一样简单易用（A library of BLE Client， make the BLE request as easy as HTTP request）

### 功能和特点
**1/ 提供异步和非异步，阻塞和非阻塞的BLE请求接口**
```
提供多样化的接口，可以选择你习惯和喜欢的
```
**2/ 实现带优先级的BLE请求队列**
```
带优先级的队列在一些场景比较需要，比如来电时候需要马上通知到设备
```
**3/ 支持多设备同时连接**

**4/ 支持读写特征值** 

**5/ 支持读写描述符** 

**6/ 支持大字节数组发送**
```
包括写特征值和写描述符都支持大字节数组发送。
框架自动帮你进行分包数据发送，也可以通过实现框架提供的分包接口进行自定义的分包发送。
通过实现分包接口可以支持文件发送。
```
**7/ 支持打开和关闭通知** 

**8/ 支持设置MTU大小**
 
**9/ 支持读取RSSI**

**10/ 支持读取和设置PHY**

**11/ 支持设置蓝牙连接参数的优先级**


### 新建BLE客户端
```
final OkBleClient client = new OkBleClient.Builder()
        .context(getApplication())
        .debuggable(true)
        .device(device)
        .build();
```

### 连接设备
```
 client.connect();
 
 或者
 
 final ConnectionRequest request = new ConnectionRequest.Builder().build();
 final OkBleTask<Void> task = client.newTask(request);
 task.addOnCompleteListener(new OnCompleteListener<Void>(){
        @Override
        public void onComplete(OkBleTask<Void> task) {
                if(task.isSuccess()){
                    //连接成功
                }else{
                   //连接失败
                }
            }
        }).enqueue();
        
```

### 监听连接状态变化
```
        client.addOnConnectionStateChangedListener(new OnConnectionStateChangedListener(){
            @Override
            public void onConnectionStateChanged(OkBleClient client, ConnectionState newState, ConnectionState lastState) {
                Log.i(TAG, "onConnectionStateChanged." + "newState:" + newState.toString() + " lastState:" + lastState.toString());
                if(newState == ConnectionState.Connected){
                    //已成功
                }else if(newState == ConnectionState.Disconnected){
                    //连接断开
                }else if(newState == ConnectionState.Connecting){
                   //连接中
                }
            }
        });
```


### 断开与设备的连接
```
client.close();
```


### 打开通知
```
        final UUID service = xxx;
        final UUID characteristic =  xxx;
        final EnableNotificationRequest request = new EnableNotificationRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .notificationType(NotificationType.Indication)
                .build();
        final OkBleTask<Void> task = client.newTask(request);
        task.addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(OkBleTask<Void> task) {
                if(task.isSuccess()){
                    //打开通知成功
                }else{
                   //打开通知失败
                }
            }
        }).enqueue(); 
```

### 监听/接收通知数据
```
        client.addOnDataReceivedListener(new OnDataReceivedListener(){
            @Override
            public void onDataReceived(OkBleClient client, CharacteristicData data) {
                Log.i(TAG, "onDataReceived: (0x) " + Hex.toString(data.data(), '-'));
            }
        });
```


### 关闭通知
```
        final UUID service = xxx;
        final UUID characteristic = xxx;
        final DisableNotificationRequest request = new DisableNotificationRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .build();
        final OkBleTask<Void> task = client.newTask(request);
        task.addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(OkBleTask<Void> task) {
                if(task.isSuccess()){
                    //关闭通知成功
                }else{
                   //关闭通知失败
                }
            }
        }).enqueue();
        
```


### 写特征值(发送数据)
```
        final UUID service = xxx;
        final UUID characteristic = xxx; 
        final byte[] data = xxx;
        final WriteCharacteristicRequest req = new WriteCharacteristicRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .writeType(WriteType.WriteNoResponse)
                .packetSenderFactory( data,  20)//每包最大20个字节，超过20个字节将会分包发送
                .build();
        final OkBleTask<Void> task = client.newTask(req); 
        task.addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(OkBleTask<Void> task) {
                if(task.isSuccess()){
                    //发送成功
                }else{
                   //发送失败
                }
            }
        }).enqueue();
        
```

### 写描述符
```
        final UUID service = xxx;
        final UUID characteristic = xxx; 
        final UUID descriptor =  xxx;
        final byte[] data = xxx;
        final ReadDescriptorRequest req = new ReadDescriptorRequest.Builder()
                .service(service)
                .characteristic(characteristic) 
                .descriptor(descriptor)
                .packetSenderFactory( data,  20)//每包最大20个字节，超过20个字节将会分包发送
                .build();
        final OkBleTask<Void> task = client.newTask(req); 
        task.addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(OkBleTask<Void> task) {
                if(task.isSuccess()){
                    //发送成功
                }else{
                   //发送失败
                }
            }
        }).enqueue();
        
```


### 读特征值
```
        final UUID service = xxx;
        final UUID characteristic =  xxx;
        final ReadCharacteristicRequest req = new ReadCharacteristicRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .build();
        final OkBleTask<byte[]> task = client.newTask(req); 
        task.addOnSuccessListener(new OnSuccessListener<byte[]>(){
            @Override
            public void onSuccess(OkBleTask<byte[]> task, byte[] bytes) {
                //读取成功，收到数据bytes
            }
        });

        task.addOnFailedListener(new OnFailedListener<byte[]>(){

            @Override
            public void onFailed(OkBleTask<byte[]> task, OkBleException ex) {
                //读取失败，收到异常ex
            }
        });
        task.enqueue();
```

### 读描述符
```
        final UUID service = xxx;
        final UUID characteristic =  xxx;
        final UUID descriptor = xxx;

        final ReadDescriptorRequest req = new ReadDescriptorRequest.Builder()
                .service(service)
                .characteristic(characteristic)
                .descriptor(descriptor)
                .build();
        final OkBleTask<byte[]> task = client.newTask(req); 
        task.addOnSuccessListener(new OnSuccessListener<byte[]>(){
            @Override
            public void onSuccess(OkBleTask<byte[]> task, byte[] bytes) {
                //读取成功，收到数据bytes
            }
        });

        task.addOnFailedListener(new OnFailedListener<byte[]>(){

            @Override
            public void onFailed(OkBleTask<byte[]> task, OkBleException ex) {
                //读取失败，收到异常ex
            }
        });
        task.enqueue();
```
### 设置MTU
```
        final SetMtuRequest req = new SetMtuRequest.Builder()
                .mtu(512).build();
        final OkBleTask<Mtu> task = client.newTask(req);
        task.addOnCompleteListener(new OnCompleteListener<Mtu>(){
            @Override
            public void onComplete(OkBleTask<Mtu> task) {
                if(task.isSuccess()){
                    //设置成功，返回与设备协商好的MTU，可能比预设的MTU要小
                    final Mtu mtu = task.getResult();
                }else{
                   //设置失败
                }
            }
        }).enqueue();
```


## 如何接入 ##
直接引用OkBle.jar, 或者复制ble目录下的源码到工程，然后在build.gradle添加implementation project(':OkBle')，

或者以下两个步骤：

**1.在工程根目录的build.gradle添加:**
``` 
allprojects {
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
}
```

**2.在依赖OkBle模块的build.gradle添加：**
```
dependencies {
    implementation 'com.github.okbean:OkBle:2.1.4
}
```



