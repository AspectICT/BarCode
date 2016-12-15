package com.plugin.barcode;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.symbol.emdk.barcode.ScannerConfig;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;

import java.util.List;

/**
 * Created by Gerjan on 11-11-2016.
 */

public class CordovaProvider {


    private CordovaInterface _cordovaInterface;
    private BarcodeReaderService _barcodeService;
    private org.apache.cordova.CallbackContext _onScanResultCallbackContext;

    public CordovaProvider(CordovaInterface cordovaInterface) {
        _cordovaInterface = cordovaInterface;
        Activity context = cordovaInterface.getActivity();
        context.bindService(new Intent(context.getBaseContext(), BarcodeReaderService.class), mConnection, Context.BIND_AUTO_CREATE);
    }



    public void start(final org.apache.cordova.CallbackContext callbackContext) {
        try {
            _barcodeService.start(callbackContext);
            callbackContext.success();
        }
        catch (Exception ex){
            callbackContext.error(ex.toString());
            ex.printStackTrace();
        }
    }

    public void stop(final org.apache.cordova.CallbackContext callbackContext) {
        try {
            _barcodeService.stop(callbackContext);
            callbackContext.success();
        }
        catch (Exception ex){
            callbackContext.error(ex.toString());
            ex.printStackTrace();
        }
    }

    public void initialize(final org.apache.cordova.CallbackContext callbackContext){
        callbackContext.success();
    }

    public void onScanResult(final org.apache.cordova.CallbackContext callbackContext){
        _onScanResultCallbackContext = callbackContext;
    }


    public void onScanResult(String data) {
        PluginResult result = new PluginResult(PluginResult.Status.OK, data);
        result.setKeepCallback(true);
        _onScanResultCallbackContext.sendPluginResult(result);
        Log.d(getClass().getSimpleName(), "ScanResult: " + data);
    }

    public Context getCurrentContext(){
        return _cordovaInterface.getActivity();
    }

    private void onServiceReady(BarcodeReaderService service) {
        _barcodeService = service;
        _barcodeService.cordovaProvider = this;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            BarcodeReaderService.LocalBinder binder = (BarcodeReaderService.LocalBinder) service;
            onServiceReady(binder.getService());
        }

        public void onServiceDisconnected(ComponentName name) {
            Log.d(getClass().getSimpleName(), "onServiceDisconnected: " + name);
        }

    };
}
