# cordova-plugin-zoop

<p align="center">
  <img src="https://zoop.com.br/wp-content/themes/zoop/img/logo.svg"/>
</p>

> Unofficial Zoop SDK for Cordova 

This plugin defines `window.ZoopAPI` object, that enable access to the zoop.com.br Mobile Android (& IOS soon).

## Installation

```
cordova plugin add https://github.com/DevMagic/cordova-plugin-zoop 
 ```

or  

```
ionic cordova plugin add https://github.com/DevMagic/cordova-plugin-zoop 
 ```
## enableDeviceBluetoothAdapter

Enable bluetooth adapter

```js
window.addEventListener('bluetoothIsNotEnabledNotification', function(){
  window.ZoopAPI.enableDeviceBluetoothAdapter();    
});
```

## Configure terminal

```js
window.addEventListener('bluetoothIsNotEnabledNotification', function(){
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

### *Listeners*

Structure of listener callback. 

```js
function (event){
  event.detail; // Access Data Callback
}
```

`bluetoothIsNotEnabledNotification` 
```
No detail return 
```
`deviceSelectedResult` 
```js
event.detail
{
  "selected": {
    "name": "MP 5-41158364",
    "uri": "btspp://C8:DF:84:1A:F2:F1",
    "communication": "Bluetooth",
    "persistent": true,
    "dateTimeDetected": "Sep 6, 2019 09:14:12",
    "typeTerminal": 3,
    "manufacturer": "PAX"
  },
  "devices": [
    {
      "name": "MP 5-41158364",
      "uri": "btspp://C8:DF:84:1A:F2:F1",
      "communication": "Bluetooth",
      "persistent": true,
      "dateTimeDetected": "Sep 6, 2019 09:14:12",
      "typeTerminal": 3,
      "manufacturer": "PAX"
    },
    {
      "name": "MP 5-42259608",
      "uri": "btspp://C8:DF:84:1C:5B:61",
      "communication": "Bluetooth",
      "persistent": true,
      "dateTimeDetected": "Sep 6, 2019 09:14:12",
      "typeTerminal": 0,
      "manufacturer": "PAX"
    }
  ],
  "i": -1
} 
```
`updateDeviceListForUserSelection`
```
TODO: Document
```
`showDeviceListForUserSelection`
```js
event.detail
[
  {
    "name": "MP 5-41158364",
    "uri": "btspp://C8:DF:84:1A:F2:F1",
    "communication": "Bluetooth",
    "persistent": true,
    "dateTimeDetected": "Sep 6, 2019 09:14:12",
    "typeTerminal": 3,
    "manufacturer": "PAX"
  },
  {
    "name": "MP 5-42259608",
    "uri": "btspp://C8:DF:84:1C:5B:61",
    "communication": "Bluetooth",
    "persistent": true,
    "dateTimeDetected": "Sep 6, 2019 09:14:12",
    "typeTerminal": 0,
    "manufacturer": "PAX"
  }
]
```

## requestZoopDeviceSelection

Set the selected device for usage. 

```js
addEventListener('showDeviceListForUserSelection', function(e){
  if (e.detail.length == 0)
    return;
  window.ZoopAPI.requestZoopDeviceSelection(e.detail[0], function(err){
    contentEl.innerHTML += '<p>Err ' + err + '</p>';
  });  
})
```


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
 
### *Listeners*

Structure of listener callback. 

```js
function (event){
  event.detail; // Access Data Callback
}
```

`showMessage`

```js
event.detail =
{
  "message": "Insira ou passe o Cartão",
  "terminalMessageType": "ACTION_INSERT_CARD"
}
```

`paymentFailed`

```js
TODO: Document
```

`paymentDuplicated`

```js
TODO: Document
```
  
`paymentSuccessful`
```js

{
  "fees": "0.03",
  "metadata": {},
  "installment_plan": null,
  "fee_details": [
    {
      "amount": "0.01",
      "is_gateway_fee": false,
      "prepaid": false,
      "description": "Zoop Funpay Owner Plano padrao Std credit fee and MCC Comércio (PSP)",
      "currency": "BRL",
      "type": "funpay_onwer_padrao_credit_fee_d30"
    },
    {
      "amount": "0.00",
      "is_gateway_fee": false,
      "prepaid": false,
      "description": "Zoop Owner Plano padrao Std credit fee and MCC Comércio (PSP)",
      "currency": "BRL",
      "type": "onwer_padrao_credit_fee_d30"
    },
    {
      "amount": "0.02",
      "is_gateway_fee": false,
      "prepaid": false,
      "description": "Zoop Fee for Owner Plano padrao Std credit fee and MCC Comércio (PSP)",
      "currency": "BRL",
      "type": "zoop_onwer_padrao_credit_fee_d30"
    }
  ],
  "description": null,
  "created_at": "2019-09-05T19:12:37+00:00",
  "source": null,
  "refunds": null,
  "statement_descriptor": "Descrição",
  "pre_authorization": null,
  "discounts": null,
  "updated_at": "2019-09-05T19:12:39+00:00",
  "original_amount": "1.00",
  "captured": true,
  "point_of_sale": {
    "identification_number": "5eb66db1dfb44a87a506215f642341f31",
    "entry_mode": "chip"
  },
  "currency": "BRL",
  "refunded": false,
  "voided": false,
  "id": "323g34324ft32",
  "gateway_authorizer": "cielo",
  "icc_data": "423ft23423324234",
  "location_longitude": null,
  "payment_method": {
    "metadata": {},
    "is_active": false,
    "address": null,
    "last4_digits": "4234",
    "resource": "card",
    "description": null,
    "created_at": "2019-09-05T19:12:37+00:00",
    "is_verified": false,
    "uri": "/v1/marketplaces/64de884234234583a86ba83e8971/cards/299dc2984321342346daac3bc",
    "expiration_year": "2025",
    "first4_digits": "3242",
    "updated_at": "2019-09-05T19:12:37+00:00",
    "is_valid": true,
    "card_brand": "Visa",
    "expiration_month": "5",
    "fingerprint": "31221dfdfsdf",
    "id": "321421321421",
    "verification_checklist": {
      "security_code_check": "pass",
      "address_line1_check": "unchecked",
      "postal_code_check": "unchecked"
    },
    "holder_name": "Foo Bar",
    "customer": "4141421346e287dfee0eabd8dc50"
  },
  "amount": "1.00",
  "resource": "transaction",
  "on_behalf_of": "2ec2a46b4c494cd3a2dccaaf80e6db3a",
  "location_latitude": null,
  "history": [
    {
      "amount": "1.00",
      "response_code": null,
      "operation_type": "created",
      "authorization_nsu": null,
      "authorizer": null,
      "created_at": "2019-09-05 19:12:38",
      "authorizer_id": null,
      "gatewayResponseTime": null,
      "response_message": null,
      "authorization_code": null,
      "id": "3f7ff6754dc8466b97dbdf5f455a13d8",
      "transaction": "3edb017b0a6d4607a0affb02d8e9cb15",
      "status": "succeeded"
    },
    {
      "amount": "1.00",
      "response_code": "00",
      "operation_type": "authorization",
      "authorization_nsu": "000034235",
      "authorizer": "cielo",
      "created_at": "2019-09-05 19:12:39",
      "authorizer_id": "000055017422",
      "gatewayResponseTime": "818",
      "response_message": "",
      "authorization_code": "623841",
      "id": "227694a0e93d42e59920d0695f333415",
      "transaction": "3edb017b0a6d4607a0affb02d8e9cb15",
      "status": "succeeded"
    }
  ],
  "uri": "/v1/marketplaces/64de88e4cc33470ab583a86ba83e8971/transactions/3edb017b0a6d4607a0affb02d8e9cb15",
  "expected_on": "2019-10-07T00:00:00+00:00",
  "arqc": "084723A621F3B0AF",
  "app_transaction_uid": "00156771074369800000000000000000",
  "payment_type": "credit",
  "sales_receipt": "259b0f125b204cce9869820e486bbdf2",
  "transaction_number": "Z623841-000055017422",
  "payment_authorization": {
    "authorizer_id": "000055017422",
    "authorization_nsu": "000034235",
    "authorization_code": "623841"
  },
  "aid": "A000000",
  "rewards": null,
  "status": "succeeded",
  "customer": "515545235"
}
```

 ## Tests
 
 Here is the test runner application
 
 https://github.com/DevMagic/cordova-plugin-zoop-test-runner
