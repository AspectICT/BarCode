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
        _barcodeReaderManager = new BarcodeReaderManager(cordovaProvider.getCurrentContext());
        _barcodeReaderManager.setOnReadyCallback(this);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _mBinder;
    }

    @Override
    public void onReady() {
        _barcodeReaderManager.getAvailableDevices();
        _barcodeReaderManager.setScannerDevice(1);
        Log.d(getClass().getSimpleName(), "Loaded");
    }

    public void start() throws Exception {
        _barcodeReaderManager.start();
    }

    public void stop() throws Exception {
        _barcodeReaderManager.stop();
    }

    @Override
    public void onScanResult(String data) {
        cordovaProvider.onScanResult(data);
    }

    @Override
    public void onDestroy() {
        try {
            _barcodeReaderManager.stop();
            _barcodeReaderManager.close();
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
