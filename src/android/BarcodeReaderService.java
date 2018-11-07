package com.plugin.barcode;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.symbol.emdk.barcode.ScannerException;

import org.apache.cordova.PluginResult;

/**
 * Created by Gerjan on 15-12-2016.
 */

public class BarcodeReaderService extends Service implements IObserver {

    private final IBinder _mBinder = new LocalBinder();
    private IBarcodeReaderManager _barcodeReaderManager;
    public CordovaProvider cordovaProvider;

    @Override
    public void onCreate() {
        Log.d(getClass().getSimpleName(), "Service Created");
        super.onCreate();
    }

    @Override
    public void onReady() {
        _barcodeReaderManager.getAvailableDevices();
        Log.d(getClass().getSimpleName(), "Loaded");
    }
    
    public void setDevice(int index) {
        _barcodeReaderManager.setScannerDevice(index);
    }

    public void initialize() {
        _barcodeReaderManager = new BarcodeReaderManager(cordovaProvider.getCurrentContext());
        _barcodeReaderManager.setOnReadyCallback(this);
    }

    public void deInitialize() {
        _barcodeReaderManager.deInitializeScanner();
        _barcodeReaderManager = null;
    }

    public void start() throws Exception {
        if(_barcodeReaderManager == null){
            initialize();
        }
        _barcodeReaderManager.start();
        Log.d(getClass().getSimpleName(), "Started Scanning");
    }

    public void stop() throws Exception {
        if(_barcodeReaderManager == null){
            initialize();
        }
        _barcodeReaderManager.stop();
        Log.d(getClass().getSimpleName(), "Stopped Scanning");
    }

    @Override
    public void onScanResult(String data) {
        cordovaProvider.onScanResult(data);
        Log.d(getClass().getSimpleName(), "ScanResult: " + data);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _mBinder;
    }

    @Override
    public void onDestroy() {
        try {
            _barcodeReaderManager.stop();
            _barcodeReaderManager.close();
            Log.d(getClass().getSimpleName(), "Destroyed Service");
        }catch (Exception ex){
            ex.printStackTrace();
        }
        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public BarcodeReaderService getService() {
            return BarcodeReaderService.this;
        }
    }
}
