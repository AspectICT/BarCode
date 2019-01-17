package com.plugin.barcode;
import com.plugin.barcode.CordovaProvider;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import java.lang.reflect.Method;

public class BarcodePlugin extends CordovaPlugin {
    private CordovaProvider _cordovaProvider;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        _cordovaProvider = new CordovaProvider(cordova);
    }

    @Override
    public boolean execute(String action, JSONArray args, org.apache.cordova.CallbackContext callbackContext) throws JSONException 
    {
        if (action.equals("initialize")) 
        {
            _cordovaProvider.initialize(callbackContext);
        } 
        else if (action.equals("initializeScanner")) 
        {
            _cordovaProvider.initializeScanner(callbackContext, args);
        } 
        else if (action.equals("deInitializeScanner")) 
        {
            _cordovaProvider.deInitializeScanner(callbackContext);
        } 
        else if (action.equals("start")) 
        {
            _cordovaProvider.start(callbackContext);
        } 
        else if (action.equals("stop")) 
        {
            _cordovaProvider.stop(callbackContext);
        } 
        else if (action.equals("onScanResult")) 
        {
            _cordovaProvider.onScanResult(callbackContext);
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

