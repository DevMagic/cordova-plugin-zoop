exports.initialize = function () {
  cordova.exec(null, null, 'ZoopAPI', 'initialize', []);
};

exports.enableDeviceBluetoothAdapter = function (error) {
  cordova.exec(null, error, 'ZoopAPI', 'enableDeviceBluetoothAdapter', []);
};

var commonCallback = function (result) {
  if (result.method) {
    var evt = new CustomEvent(result.method, {
      detail: result.data
    });
    window.dispatchEvent(evt);
  }
};

exports.startTerminalsDiscovery = function (error) {
  cordova.exec(commonCallback, error, 'ZoopAPI', 'startTerminalsDiscovery', []);
};

exports.finishTerminalDiscovery = function () {
  cordova.exec(null, null, 'ZoopAPI', 'finishTerminalDiscovery', []);
};

exports.requestZoopDeviceSelection = function (device, error) {
  cordova.exec(null, error, 'ZoopAPI', 'requestZoopDeviceSelection', [device]);
};

exports.charge = function (valueToCharge, paymentOption, numberOfInstallments, marketplaceId,
                           sellerId, publishableKey) {
  var callback = function (result) {
    if (result.method) {
      var evt = new CustomEvent(result.method, {
        detail: result.data
      });
      window.dispatchEvent(evt);
    }
  };
  cordova.exec(callback, callback, 'ZoopAPI', 'charge', [valueToCharge, paymentOption,
    numberOfInstallments, marketplaceId, sellerId, publishableKey]);
};
