cordova.define("cordova-plugin-zoop.ZoopAPI", function(require, exports, module) {
    var exec = require('cordova/exec');

    exports.initialize = function () {
        exec(null, null, 'ZoopAPI', 'initialize', []);
    };

    exports.enableDeviceBluetoothAdapter = function (error) {
        exec(null, error, 'ZoopAPI', 'enableDeviceBluetoothAdapter', []);
    };

    exports.startTerminalsDiscovery = function (error) {
        var success = function(result){
          if (result.method){
            var evt = new CustomEvent(result.method, {
              detail: result.data
            });
            document.dispatchEvent(evt);
          }
        };
        exec(success, error, 'ZoopAPI', 'startTerminalsDiscovery', []);
    };

    exports.requestZoopDeviceSelection = function(device, error){
      exec(null, error, 'ZoopAPI', 'requestZoopDeviceSelection', [device]);
    }

    exports.charge = function (valueToCharge, paymentOption, numberOfInstallments, marketplaceId,
        sellerId, publishableKey, success, error) {
        exec(success, error, 'ZoopAPI', 'charge', [valueToCharge, paymentOption,
            numberOfInstallments, marketplaceId, sellerId, publishableKey]);
    };

});