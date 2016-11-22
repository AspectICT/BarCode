package com.plugin.barcode;

import com.symbol.emdk.barcode.ScannerConfig;

/**
 * Created by Gerjan on 11-11-2016.
 */

public class CordovaProvider {
    private IBarcodeReaderManager _barcodeReaderManager;

    public void initialize(){
       /*try {
            _barcodeReaderManager = new BarcodeReaderManager();
            _barcodeReaderManager.getAvailableDevices();
            _barcodeReaderManager.setScannerDevice(1);
            ScannerConfig scannerConfig = new ScannerConfig();
            scannerConfig.decoderParams.code39.enabled = true;
            _barcodeReaderManager.setConfig(scannerConfig);
        }
        catch (Exception ex){

        }*/
    }
}
