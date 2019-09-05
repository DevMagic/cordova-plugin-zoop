cordova.define("cordova-plugin-zoop-tests.tests", function (require, exports, module) {
  /*
   *
   * Licensed to the Apache Software Foundation (ASF) under one
   * or more contributor license agreements.  See the NOTICE file
   * distributed with this work for additional information
   * regarding copyright ownership.  The ASF licenses this file
   * to you under the Apache License, Version 2.0 (the
   * "License"); you may not use this file except in compliance
   * with the License.  You may obtain a copy of the License at
   *
   *   http://www.apache.org/licenses/LICENSE-2.0
   *
   * Unless required by applicable law or agreed to in writing,
   * software distributed under the License is distributed on an
   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   * KIND, either express or implied.  See the License for the
   * specific language governing permissions and limitations
   * under the License.
   *
   */
  /* jshint jasmine: true */
  exports.defineAutoTests = function () {
    describe('ZoopAPI', function () {
      it('should be defined', function () {
        expect(window.ZoopAPI).toBeDefined();
      });

      it('should contain a startTerminalsDiscovery function', function () {
        expect(typeof window.ZoopAPI.startTerminalsDiscovery).toBeDefined();
      });

      it('should contain a charge method', function (done) {
        expect(typeof window.ZoopAPI.charge).toBeDefined();
      });

      describe('startTerminalsDiscovery', function () {

      });
    });
  };

  exports.defineManualTests = function (contentEl, createActionButton) {

    createActionButton('Magic R$1 Charge', function(){
      window.ZoopAPI.initialize();

      addListener('showMessage'      , forShowMessage);
      addListener('paymentFailed'    , forPaymentFailed);
      addListener('paymentDuplicated', forPaymentDuplicated);
      addListener('paymentSuccessful', forPaymentSuccessful);

      window.ZoopAPI.charge(
        1, 0, 1, '-',
        '-', '-'
      );
    });

    createActionButton('Initialize API', function () {
      window.ZoopAPI.initialize();
      contentEl.innerHTML = '<p>Zoop API initialized</p>';
    });

    createActionButton('startTerminalsDiscovery', function () {
      showStartTerminalInstructions();

      addListener('bluetoothIsNotEnabledNotification', forDisabledBluetooth);
      addListener('deviceSelectedResult'             , forDeviceSelectedResult);
      addListener('updateDeviceListForUserSelection' , forUpdateDeviceListForUserSelection);
      addListener('showDeviceListForUserSelection'   , forShowDeviceListForUserSelectionListener );

      window.ZoopAPI.startTerminalsDiscovery(
        function (err) {
          contentEl.innerHTML = '<p>ERR ${err}</p>';
        }
      )
    });

    var createButtonForSelectDevice = function(name, device){
      createActionButton('Activate ' + name, function(){
        contentEl.innerHTML += '<pre>' + device + '</pre>';
        window.ZoopAPI.requestZoopDeviceSelection(device, function(err){
          contentEl.innerHTML += '<p>Err ' + err + '</p>';
        });
      });
    }

    var createButtonForEnableBluetooth = function(){
      if (testBluetooth != 'TODO')
        return;
      createActionButton('Enable Bluetooth', function() {
        window.ZoopAPI.enableDeviceBluetoothAdapter();
        contentEl.innerHTML = "<p>See if bluetooth is enabled</p>"
      });
    };

    var isButtonForChargeCreated = false;
    var createButtonForCharge = function() {
      if (isButtonForChargeCreated)
        return;
      isButtonForChargeCreated = true;
      createActionButton('Charge R$1', function () {

        addListener('showMessage'      , forShowMessage);
        addListener('paymentFailed'    , forPaymentFailed);
        addListener('paymentDuplicated', forPaymentDuplicated);
        addListener('paymentSuccessful', forPaymentSuccessful);

        window.ZoopAPI.charge(
          1, 99, 1, '-',
          '-', '-'
        );
      });
    }

    var testBluetooth = 'TODO';

    var showStartTerminalInstructions = function() {
      contentEl.innerHTML = `
        <p><b>startTerminalsDiscovery</b></p>
        <span>Test the Terminal Discovery Method</span>
        <ul>
          <li>Test Bluetooth (${testBluetooth}): Turn off bluetooth and see if the event appears here</li>
        </ul>
      `;
    };

    function addListener(name, method) {
      document.removeEventListener(name, method);
      document.addEventListener(name, method);
    }

    var forShowMessage = function(e){
      contentEl.innerHTML = `
        <p>Event <i>showMessage</i></p>
        <pre>${JSON.stringify(e.detail, null, 2)}</pre>
      `.concat(contentEl.innerHTML);
    }

    var forPaymentFailed = function(e){
      contentEl.innerHTML = `
        <p>Event <i>paymentFailed</i></p>
        <pre>${JSON.stringify(e.detail, null, 2)}</pre>
      `.concat(contentEl.innerHTML);
    }

    var forPaymentDuplicated = function(e){
      contentEl.innerHTML = `
        <p>Event <i>paymentDuplicated</i></p>
        <pre>${JSON.stringify(e.detail, null, 2)}</pre>
      `.concat(contentEl.innerHTML);
    }

    var forPaymentSuccessful = function(e){
      contentEl.innerHTML = `
        <p>Event <i>paymentSuccessful </i></p>
        <pre>${JSON.stringify(e.detail, null, 2)}</pre>
      `.concat(contentEl.innerHTML);
    }

    var forDisabledBluetooth = function () {
      createButtonForEnableBluetooth();
      testBluetooth = 'DONE';
      showStartTerminalInstructions();
      contentEl.innerHTML += '<p>Event <i>bluetoothIsNotEnabledNotification</i></p>';
    }

    var forDeviceSelectedResult = function(e){
      contentEl.innerHTML += '<p>Event <i>deviceSelectedResult</i></p>';
      contentEl.innerHTML += '<pre>' + JSON.stringify(e.detail, null, 2) + '</pre>';
      createButtonForCharge();
    };

    var deviceIsNotShowing = true;

    var forShowDeviceListForUserSelectionListener = function(e){
      contentEl.innerHTML += '<p>Event <i>showDeviceListForUserSelection</i></p>';
      contentEl.innerHTML += '<p>Device count: ' + e.detail.length + '</p>';
      contentEl.innerHTML += '<pre>' + JSON.stringify(e.detail, null, 2) + '</pre>';
      let devices = JSON.parse(JSON.stringify(e.detail));
      if (deviceIsNotShowing)
        for (var i in devices){
          let device = devices[i];
          createButtonForSelectDevice(device.name, device);
        }
      deviceIsNotShowing = false;
    };

    var forUpdateDeviceListForUserSelection = function(e){
      contentEl.innerHTML += '<p>Event <i>updateDeviceListForUserSelection</i></p>';
      contentEl.innerHTML += '<code>' + JSON.stringify(e.detail, null, 2) + '</code>';
//      contentEl.innerHTML += '<p>Auto selectin the first</p>';
//      window.ZoopAPI.requestZoopDeviceSelection(e.detail[0], function(err){
//        contentEl.innerHTML += '<p>Err ' + err + '</p>';
//      });
    };

  };

});
