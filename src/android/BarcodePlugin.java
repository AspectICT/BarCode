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
        if(action.equals("start")){
            _cordovaProvider.start(callbackContext);
        }
        return true;
    }

    public boolean getMethod(String action, JSONArray args, org.apache.cordova.CallbackContext callbackContext) {
        try {
            if (args.length() > 0) {
                Method method = CordovaProvider.class.getDeclaredMethod(action, JSONArray.class, org.apache.cordova.CallbackContext.class);
                method.invoke(_cordovaProvider, args, callbackContext);
            } else {
                Method method = CordovaProvider.class.getDeclaredMethod(action, org.apache.cordova.CallbackContext.class);
                method.invoke(_cordovaProvider, callbackContext);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

