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
    private org.apache.cordova.CallbackContext _onReadyCallbackContext;

    public CordovaProvider(CordovaInterface cordovaInterface) 
    {
        Log.d(getClass().getSimpleName(), "CordovaProvider constructor");

        _cordovaInterface = cordovaInterface;
        Activity context = cordovaInterface.getActivity();
        context.bindService(new Intent(context.getBaseContext(), BarcodeReaderService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    public void initialize(org.apache.cordova.CallbackContext callbackContext){
        try 
        {
            Log.d(getClass().getSimpleName(), "initializing CordovaProvider");
            
            // TEST 17-1-2019
            _barcodeService.initialize();
            callbackContext.success();

            // if(_barcodeService != null) 
            // {
            //     Log.d(getClass().getSimpleName(), "initializing barcodeService");
            //     _barcodeService.initialize();
            //     callbackContext.success();
            // } 
            // else 
            // {
            //     Log.d(getClass().getSimpleName(), "barcodeService = NULL");
            //     _onReadyCallbackContext = callbackContext;
            // }
        }
        catch (Exception ex){
            callbackContext.error(ex.toString());
            ex.printStackTrace();
        }
    }

    public void start(org.apache.cordova.CallbackContext callbackContext) {
        try 
        {
            _barcodeService.start();
            callbackContext.success();
        }
        catch (Exception ex){
            callbackContext.error(ex.toString());
            ex.printStackTrace();
        }
    }

    public void stop(org.apache.cordova.CallbackContext callbackContext) {
        try 
        {
             _barcodeService.stop();
            callbackContext.success();
        }
        catch (Exception ex)
        {
            callbackContext.error(ex.toString());
            ex.printStackTrace();
        }
    }

    public void initializeScanner(org.apache.cordova.CallbackContext callbackContext, JSONArray args)
    {
        try 
        {
            String triggerType = args.getJSONObject(0).getString("triggerType");
            int deviceId = args.getJSONObject(0).getInt("deviceId");

            _barcodeService.initializeScanner(triggerType, deviceId);
            callbackContext.success();
        }
        catch (Exception ex){
            callbackContext.error(ex.toString());
            ex.printStackTrace();
        }
    }

    public void deInitializeScanner(org.apache.cordova.CallbackContext callbackContext){
        try {
            _barcodeService.deInitialize();
            callbackContext.success();
        }
        catch (Exception ex){
            callbackContext.error(ex.toString());
            ex.printStackTrace();
        }
    }

    public void onScanResult(org.apache.cordova.CallbackContext callbackContext){
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
        if(_onReadyCallbackContext != null) {
            _onReadyCallbackContext.success();
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() 
    {
        public void onServiceConnected(ComponentName className, IBinder service) 
        {
            BarcodeReaderService.LocalBinder binder = (BarcodeReaderService.LocalBinder) service;
            Log.d(getClass().getSimpleName(), "onServiceReady");
            onServiceReady(binder.getService());
        }

        public void onServiceDisconnected(ComponentName name) 
        {
            Log.d(getClass().getSimpleName(), "onServiceDisconnected: " + name);
        }
    };
}
