package com.plugin.barcode;

import android.content.Context;
import android.util.Log;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.StatusData;

import java.util.ArrayList;
import java.util.List;

public class BarcodeReaderManager implements EMDKManager.EMDKListener, Scanner.DataListener, Scanner.StatusListener, BarcodeManager.ScannerConnectionListener, IBarcodeReaderManager {

    private EMDKManager _emdkManager;
    private BarcodeManager _barcodeManager;
    private Scanner _scanner;
    private List<ScannerInfo> _deviceList = new ArrayList<ScannerInfo>();
    private IObserver _onReadyObserver;
    private ScannerConfig _scannerConfig;
    private int _selectedIndex = 0;
    private int _defaultIndex = 0;


    public BarcodeReaderManager(Context context) {
        EMDKResults results = EMDKManager.getEMDKManager(context, this);
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Log.d(getClass().getSimpleName(), "Found EMDK EMDKResults.STATUS_CODE.SUCCESS");
        }
    }
    public void setOnReadyCallback(IObserver observer){
        _onReadyObserver = observer;
    }
    
    public Scanner getScanner() {
        return _scanner;
    }

    private void resetCurrentDevice() {
        initializeScanner();
        deInitializeScanner();
    }

    private void initializeScanner() {
        if (_scanner == null) {

            if ((_deviceList != null) && (_deviceList.size() != 0)) {
                _scanner = _barcodeManager.getDevice(_deviceList.get(_selectedIndex));
            } else {
               return;
            }

            if (_scanner != null) {
                _scanner.addDataListener(this);
                _scanner.addStatusListener(this);

                try {
                    _scanner.enable();
                } catch (ScannerException e) {
                    e.printStackTrace();
                }
            } else {
                //  textViewStatus.setText("Status: " + "Failed to initialize the scanner device.");
            }
        }
    }

    private void deInitializeScanner() {
        if (_scanner != null) {
            try {
                _scanner.cancelRead();
                _scanner.disable();
            } catch (ScannerException e) {
                e.printStackTrace();
            }
            _scanner.removeDataListener(this);
            _scanner.removeStatusListener(this);
            try {
                _scanner.release();
            } catch (ScannerException e) {
                e.printStackTrace();
            }
            _scanner = null;
        }
    }

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this._emdkManager = emdkManager;
        Log.d(getClass().getSimpleName(), "Connected To EMDK");
        // Acquire the barcode manager resources
        _barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);

        // Add connection listener
        if (_barcodeManager != null) {
            _barcodeManager.addConnectionListener(this);
        }
        _onReadyObserver.onReady();
    }

    public void setScannerDevice(int id) {
        _selectedIndex = id;
        resetCurrentDevice();
    }

    public List<Device> getAvailableDevices() {
        List<Device> friendlyNameList = new ArrayList<Device>();
        if (_barcodeManager != null) {
            _deviceList = _barcodeManager.getSupportedDevicesInfo();
            if ((_deviceList != null) && (_deviceList.size() != 0)) {
                int index = 0;
                for (ScannerInfo scnInfo : _deviceList) {
                    friendlyNameList.add(new Device(index, scnInfo.getFriendlyName()));
                    if (scnInfo.isDefaultScanner()) {
                        _defaultIndex = index;
                    }
                    index++;
                }
            }
        }
        return friendlyNameList;
    }

    public void setConfig(ScannerConfig scannerConfig) throws Exception {
        this._scannerConfig = scannerConfig;
    }

    public void start() throws Exception {
        if (_scanner == null) {
            initializeScanner();
            _scannerConfig = _scanner.getConfig();
            _scanner.triggerType = Scanner.TriggerType.SOFT_ALWAYS;
            _scanner.setConfig(_scannerConfig);
        }
        if (_scanner != null) {
            try {

                if (_scanner.isEnabled()) {
                    _scanner.read();
                } else {
                    throw new Exception("Device not Enabled");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    public void stop() throws ScannerException {
        if (_scanner != null) {
            try {
                _scanner.cancelRead();
            } catch (ScannerException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }

    @Override
    public void onClosed() {
       close();
    }

    public void close(){
        if (_emdkManager != null) {
            // Remove connection listener
            if (_barcodeManager != null) {
                _barcodeManager.removeConnectionListener(this);
                _barcodeManager = null;
            }

            // Release all the resources
            _emdkManager.release();
            _emdkManager = null;
        }
    }

    @Override
    public void onConnectionChange(ScannerInfo scannerInfo, BarcodeManager.ConnectionState connectionState) {
        String scannerName = "";
        Log.d(getClass().getSimpleName(), "Connection Change: " + connectionState);
        String statusExtScanner = connectionState.toString();
        String scannerNameExtScanner = scannerInfo.getFriendlyName();

        if (_deviceList.size() != 0) {
            scannerName = _deviceList.get(_selectedIndex).getFriendlyName();
        }

        if (scannerName.equalsIgnoreCase(scannerNameExtScanner)) {
            switch (connectionState) {
                case CONNECTED:
                    resetCurrentDevice();
                    break;
                case DISCONNECTED:
                    deInitializeScanner();
                    break;
            }
        } else {
        }
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        if ((scanDataCollection != null) && (scanDataCollection.getResult() == ScannerResults.SUCCESS)) {
            ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
            for (ScanDataCollection.ScanData data : scanData) {
                String dataString = data.getData();
                this._onReadyObserver.onScanResult(dataString);
            }
        }
    }

    @Override
    public void onStatus(StatusData statusData) {
        StatusData.ScannerStates state = statusData.getState();
        switch (state) {
            case IDLE:
                    try {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        _scanner.read();
                    } catch (ScannerException e) {
                        e.printStackTrace();
                    }
                    break;
            case WAITING:
                break;
            case SCANNING:
                break;
            case DISABLED:
                break;
            case ERROR:
                break;
            default:
                break;
        }
    }
}
