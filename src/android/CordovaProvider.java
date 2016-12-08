package com.plugin.barcode;

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

    private IBarcodeReaderManager _barcodeReaderManager;
    private CordovaInterface _cordovaInterface;


    public CordovaProvider(CordovaInterface cordovaInterface){
        _cordovaInterface = cordovaInterface;
    }
    public void start(final org.apache.cordova.CallbackContext callbackContext){
        try {
            _barcodeReaderManager = new BarcodeReaderManager(_cordovaInterface.getActivity());

            _barcodeReaderManager.setOnReadyCallback(new CallbackContext() {
                @Override
                public void onReady() {
                    Log.d(getClass().getSimpleName(), "Sensor Ready");
                    _barcodeReaderManager.setScannerDevice(1);
                    try {
                        Log.d(getClass().getSimpleName(), "Starting Scan...");
                        _barcodeReaderManager.start();
                    } catch (Exception ex) {
                        Log.e(getClass().getSimpleName(), ex.toString());
                        callbackContext.error(ex.getMessage());
                    }
                }
                @Override
                public void onScanResult(String data) {
                    try {
                        Log.d(getClass().getSimpleName(), "ScanResult: " + data);
                        PluginResult result = new PluginResult(PluginResult.Status.OK, data);
                        result.setKeepCallback(true);
                        callbackContext.sendPluginResult(result);
                        Log.d(getClass().getSimpleName(), "Stopping scan...: " + data);
                        _barcodeReaderManager.stop();
                        _barcodeReaderManager.close();
                    }catch (Exception ex){
                        ex.printStackTrace();
                        callbackContext.error(ex.getMessage());
                    }
                }
            });
        }
        catch (Exception ex){
            ex.printStackTrace();
            callbackContext.error(ex.getMessage());
        }
    }
}
