package org.apache.cordova.plugin;

import android.util.Log;

import com.zoop.zoopandroidsdk.TerminalListManager;
import com.zoop.zoopandroidsdk.ZoopTerminalPayment;
import com.zoop.zoopandroidsdk.commons.TypeTerminalKeyEnum;
import com.zoop.zoopandroidsdk.commons.TypeTerminalKeyErrorEnum;
import com.zoop.zoopandroidsdk.terminal.ApplicationDisplayListener;
import com.zoop.zoopandroidsdk.terminal.DeviceSelectionListener;
import com.zoop.zoopandroidsdk.terminal.TerminalMessageType;
import com.zoop.zoopandroidsdk.terminal.TerminalPaymentListener;
import com.zoop.zoopandroidsdk.terminal.ZoopTerminalKeyValidatorListener;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Vector;

import static org.apache.cordova.PluginResult.Status.OK;


/**
* This class echoes a string called from JavaScript.
*/
public class ZoopAPI extends CordovaPlugin implements DeviceSelectionListener, TerminalPaymentListener, ApplicationDisplayListener, ZoopTerminalKeyValidatorListener {

    private CallbackContext terminalDiscoveryCallback;
    private CallbackContext callback;
    private TerminalListManager _terminalListManager;

    private TerminalListManager getTerminalListManager(){
        if (_terminalListManager == null)
            _terminalListManager = new TerminalListManager(this, cordova.getContext());
        return _terminalListManager;
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        this.callback = callbackContext;
        if (action.equals("initialize")) {
            cordova.getThreadPool().execute(this::initializeZoopAPI);
            return true;
        } else if (action.equals("startTerminalsDiscovery")) {
            if (this.terminalDiscoveryCallback != null)
                this.terminalDiscoveryCallback.success();
            this.terminalDiscoveryCallback = callbackContext;
            cordova.getThreadPool().execute(this::startTerminalsDiscovery);
            return true;
        } else if (action.equals("enableDeviceBluetoothAdapter")){
            cordova.getThreadPool().execute(this::enableDeviceBluetoothAdapter);
            return true;
        } else if (action.equals("charge")) {
            ChargeArgs chargeArgs = new ChargeArgs(args);
            cordova.getThreadPool().execute(() -> {
                try {
                    charge(chargeArgs);
                } catch (Exception e) {
                    e.printStackTrace();
                    callback.error(e.getMessage());
                }
            });
            return true;
        } else if (action.equals("requestZoopDeviceSelection")){
            cordova.getThreadPool().execute(() -> requestZoopDeviceSelection(args));
            return true;
        }
        return false;
    }

    private void requestZoopDeviceSelection(JSONArray args) {
        Log.i("ZoopAPI", ">>> requestZoopDeviceSelection");
        try {
            Log.d("ZoopAPI", args.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject device = ((JSONObject) args.get(0));
            getTerminalListManager().requestZoopDeviceSelection(device);
        } catch (JSONException e) {
            terminalDiscoveryCallback.error(e.getMessage());
        }
    }

    private void enableDeviceBluetoothAdapter() {
        getTerminalListManager().enableDeviceBluetoothAdapter();
    }

    @Override
    public void onDestroy() {
        Log.i("ZoopAPI", "onDestroy");
        if (this._terminalListManager != null)
            getTerminalListManager().finishTerminalDiscovery();
        super.onDestroy();
    }

    private void charge(ChargeArgs args) throws Exception {
        Log.i("ZoopAPI", ">>> charge");
        Log.d("ZoopAPI", "JSON " + getTerminalListManager().getCurrentSelectedZoopTerminal().toString(2));
        JSONArray jsA = new JSONArray(getTerminalListManager().getAvailableZoopTerminalDevices());
        getTerminalListManager().checkTerminalCompatibility(getTerminalListManager().getCurrentSelectedZoopTerminal(), this);

        Log.d("ZoopAPI", "JSA " + jsA.toString(2));

        ZoopTerminalPayment zoopTerminalPayment = new ZoopTerminalPayment();
        Log.d("ZoopAPI", "Minimun " + zoopTerminalPayment.getMinimumChargeValue().doubleValue());
        zoopTerminalPayment.setTerminalPaymentListener(this);
        zoopTerminalPayment.setApplicationDisplayListener(this);

        zoopTerminalPayment.charge(
                BigDecimal.valueOf(args.valueToCharge),
                args.paymentOption,
                args.iNumberOfInstallments,
                args.marketplaceId,
                args.sellerId,
                args.publishableKey);
    }

    private void startTerminalsDiscovery() {
        Log.i("ZoopAPI", ">>> startTerminalsDiscovery");
        try {
            getTerminalListManager().startTerminalsDiscovery();
        } catch (Exception e){
            callback.error("Hey dev, try to initialize the API first - " + e.getMessage());
        }
    }

    private void initializeZoopAPI() {
        Log.i("ZoopAPI", ">>> initializeZoopAPI");
        try {
            com.zoop.zoopandroidsdk.ZoopAPI.initialize(cordova.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // DeviceSelectionListener
    @Override
    public void showDeviceListForUserSelection(Vector<JSONObject> vector) {
        Log.i("ZoopAPI", ">>> showDeviceListForUserSelection");
        JSONArray deviceList = new JSONArray(vector);

        PluginResult result = new PluginResult(OK, getResult(
                "DeviceSelectionListener",
                "showDeviceListForUserSelection",
                deviceList));
        result.setKeepCallback(true);
        terminalDiscoveryCallback.sendPluginResult(result);
    }

    // DeviceSelectionListener
    @Override
    public void updateDeviceListForUserSelection(JSONObject jsonObject, Vector<JSONObject> vector, int i) {
        Log.i("ZoopAPI", ">>> updateDeviceListForUserSelection");
        PluginResult result = new PluginResult(OK, getResult(
                "DeviceSelectionListener",
                "updateDeviceListForUserSelection",
                jsonObject));
        result.setKeepCallback(true);
        terminalDiscoveryCallback.sendPluginResult(result);
    }

    // DeviceSelectionListener
    @Override
    public void bluetoothIsNotEnabledNotification() {
        Log.i("ZoopAPI", ">>> bluetoothIsNotEnabledNotification");

        PluginResult result = new PluginResult(OK, getResult(
                "DeviceSelectionListener",
                "bluetoothIsNotEnabledNotification",
                null));
        result.setKeepCallback(true);
        terminalDiscoveryCallback.sendPluginResult(result);
    }

    // DeviceSelectionListener
    @Override
    public void deviceSelectedResult(JSONObject jsonObject, Vector<JSONObject> vector, int i) {
        Log.i("ZoopAPI", ">>> deviceSelectedResult");

        JSONObject data = new JSONObject();
        try {
            data.put("selected", jsonObject);
            data.put("devices", new JSONArray(vector));
            data.put("i", i);
        } catch (JSONException e) {
            terminalDiscoveryCallback.error(e.getMessage());
        }
        PluginResult result = new PluginResult(OK, getResult(
                "DeviceSelectionListener",
                "deviceSelectedResult",
                data));
        result.setKeepCallback(true);
        terminalDiscoveryCallback.sendPluginResult(result);
    }


    // TerminalPaymentListener
    @Override
    public void paymentFailed(JSONObject data) {
        Log.i("ZoopAPI", ">>> paymentFailed");
        callback.success(getResult(
                "TerminalPaymentListener",
                "paymentFailed",
                data));
    }

    // TerminalPaymentListener
    @Override
    public void paymentDuplicated(JSONObject data) {
        Log.i("ZoopAPI", ">>> paymentDuplicated");
        PluginResult result = new PluginResult(OK, getResult(
                "TerminalPaymentListener",
                "paymentDuplicated",
                data));
        callback.sendPluginResult(result);

    }

    // TerminalPaymentListener
    @Override
    public void paymentSuccessful(JSONObject data) {
        try {
            Log.e("Zoop", "paymentSuccessful " + data.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callback.success(getResult("TerminalPaymentListener", "paymentSuccessful", data));
    }

    // TerminalPaymentListener
    @Override
    public void paymentAborted() {
        Log.i("Zoop", "paymentAborted");
    }

    // TerminalPaymentListener
    @Override
    public void cardholderSignatureRequested() {
        Log.i("Zoop", "cardholderSignatureRequested");
    }

    // TerminalPaymentListener
    @Override
    public void currentChargeCanBeAbortedByUser(boolean b) {
        Log.i("Zoop", "currentChargeCanBeAbortedByUser " + Boolean.toString(b));
    }

    // TerminalPaymentListener
    @Override
    public void signatureResult(int i) {
        Log.i("Zoop", "signatureResult " + Integer.toString(i));
    }

    // ApplicationDisplayListener
    @Override
    public void showMessage(String s, TerminalMessageType terminalMessageType) {
        Log.i("Zoop", "showmEssage " + s + " " + terminalMessageType.toString());

        JSONObject data = null;
        try {
            data = new JSONObject();
            data.put("message", s);
            data.put("terminalMessageType", terminalMessageType.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PluginResult result = new PluginResult(OK, getResult("ApplicationDisplayListener", "showMessage", data));
        result.setKeepCallback(true);
        callback.sendPluginResult(result);
    }

    // ApplicationDisplayListener
    @Override
    public void showMessage(String s, TerminalMessageType terminalMessageType, String s1) {
        Log.i("Zoop", "showmEssage " + s + " " + terminalMessageType.toString() + " " + s1 );

        JSONObject data = new JSONObject();
        try {
            data.put("message", s);
            data.put("message2", s1);
            data.put("terminalMessageType", terminalMessageType.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PluginResult result = new PluginResult(OK, getResult("ApplicationDisplayListener", "showMessage", data));
        result.setKeepCallback(true);
        callback.sendPluginResult(result);
    }

    private JSONObject getResult(String interfaceName, String method, Object data) {
        JSONObject js = new JSONObject();
        try {
            js.put("class", interfaceName);
            js.put("method", method);
            js.put("data", data);
        }catch (JSONException err){
            err.printStackTrace();
        }
        return js;
    }

    @Override
    public void compatibilityResult(TypeTerminalKeyEnum typeTerminalKeyEnum, JSONObject jsonObject) {
        Log.d("ZoopAPI", "compatibilityResult");
        Log.d("ZoopAPI", typeTerminalKeyEnum.name());
        try {
            Log.d("ZoopAPI", jsonObject.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void compatibilityError(TypeTerminalKeyErrorEnum typeTerminalKeyErrorEnum, String s) {
        Log.d("ZoopAPI", "compatibilityError");
        Log.d("ZoopAPI", typeTerminalKeyErrorEnum.name());
        Log.d("ZoopAPI", s);
    }

    public class ChargeArgs{
        double valueToCharge;
        int paymentOption;
        int iNumberOfInstallments;
        String marketplaceId;
        String sellerId;
        String publishableKey;

        public ChargeArgs(JSONArray args){
            try {
                valueToCharge = args.getDouble(0);
                paymentOption = args.getInt(1);
                iNumberOfInstallments = args.getInt(2);
                marketplaceId = args.getString(3);
                sellerId = args.getString(4);
                publishableKey = args.getString(5);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
