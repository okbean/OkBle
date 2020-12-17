# OkBle
BLE客户端框架，使BLE请求像HTTP请求一样简单易用（A library of BLE Client， make the BLE request as easy as HTTP request）


## 新建BLE客户端
```
final OkBleClient client = new OkBleClient.Builder()
        .context(getApplication())
        .debuggable(true)
        .device(device)
        .build();
```
