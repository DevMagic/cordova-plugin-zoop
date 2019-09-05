# cordova-plugin-zoop

This plugin defines `window.ZoopAPI` object, that enable access to the zoop.com.br Mobile Android (& IOS soon).

## Charge

The way to charge someone.

```js
window.ZoopAPI.initialize();

window.addEventListener('showMessage'      , method);
window.addEventListener('paymentFailed'    , method);
window.addEventListener('paymentDuplicated', method);
window.addEventListener('paymentSuccessful', method);

window.ZoopAPI.charge(
    1, //Value to Charge 
    0, //Payment Option
    1, //Number of Installments 
    'marketplaceId', 
    'sellerId', 
    'publishableKey'
);
``` 

## Configure terminal

```js
window.addEventListener('bluetoothIsNotEnabledNotification', function(e){
        window.ZoopAPI.enableDeviceBluetoothAdapter();    
});
window.addEventListener('deviceSelectedResult'             , method);
window.addEventListener('updateDeviceListForUserSelection' , method);
window.addEventListener('showDeviceListForUserSelection'   , method);
window.ZoopAPI.startTerminalsDiscovery(
    function (err) {
      contentEl.innerHTML = '<p>ERR ${err}</p>';
    }
);
```
