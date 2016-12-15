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
    public boolean execute(String action, JSONArray args, org.apache.cordova.CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "initialize":
                _cordovaProvider.initialize(callbackContext);
                break;
            case "start":
                _cordovaProvider.start(callbackContext);
                break;
            case "stop":
                _cordovaProvider.stop(callbackContext);
                break;
            case "onScanResult":
                _cordovaProvider.onScanResult(callbackContext);
                break;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

