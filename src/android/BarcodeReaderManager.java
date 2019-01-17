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
    private int _deviceId = 0;
    private int _defaultIndex = 0;
    private String _triggerType = "hard";
    private boolean ready = false;
    private boolean _isScanning = false;


    public BarcodeReaderManager(Context context) {
        EMDKResults results = EMDKManager.getEMDKManager(context, this);
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Log.d(getClass().getSimpleName(), "Found EMDK EMDKResults.STATUS_CODE.SUCCESS");
        }
    }
    public void setOnReadyCallback(IObserver observer){
        _onReadyObserver = observer;
        if(ready){
            _onReadyObserver.onReady();
        }
    }
    
    public Scanner getScanner() 
    {
        return _scanner;
    }

    public void initializeScanner(String triggerType, int deviceId) 
    {
        triggerType = triggerType.toLowerCase();
        _deviceId = deviceId;

        Log.d(getClass().getSimpleName(), "Scanner Initializing. TriggerType=" + triggerType + ", deviceId=" + deviceId);

        if (_scanner == null) 
        {
            if ((_deviceList != null) && (_deviceList.size() != 0)) 
            {
                _scanner = _barcodeManager.getDevice(_deviceList.get(deviceId));

                if(triggerType == "hard") 
                {
                    _scanner.triggerType = Scanner.TriggerType.HARD;
                } 
                else if(triggerType == "soft_once") 
                {
                    _scanner.triggerType = Scanner.TriggerType.SOFT_ONCE;
                } 
                else if(triggerType == "soft_always") 
                {
                    _scanner.triggerType = Scanner.TriggerType.SOFT_ALWAYS;
                }
            } 
            else 
            {
               return;
            }

            _scanner.addDataListener(this);
            _scanner.addStatusListener(this);

            try 
            {
                _scanner.enable();
            } catch (ScannerException e) 
            {
                e.printStackTrace();
            }
            
            Log.d(getClass().getSimpleName(), "Scanner Initialized!");
        }
    }

    public void deInitializeScanner() 
    {
        if (_scanner != null) 
        {
            try 
            {
                _scanner.cancelRead();
                _scanner.disable();
            } 
            catch (ScannerException e) 
            {
                e.printStackTrace();
            }

            _scanner.removeDataListener(this);
            _scanner.removeStatusListener(this);

            try 
            {
                _scanner.release();
            } 
            catch (ScannerException e) 
            {
                e.printStackTrace();
            }
            _scanner = null;
            
            Log.d(getClass().getSimpleName(), "Scanner deInitialized ");   
        } else 
        {
            Log.d(getClass().getSimpleName(), "Scanner is not found");   
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
        ready = true;
        if(_onReadyObserver != null){
            _onReadyObserver.onReady();
        }
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

    public void start() throws Exception 
    {
        _isScanning = true;
        try 
        {
            while (_scanner == null) 
            {
                Log.d(getClass().getSimpleName(), "Waiting for scanner started up");
            }
            
            ScannerConfig config = _scanner.getConfig();

            // Dit is de parameter die ervoor zorgt dat barcode type Interleaved 2 of 5 enabled is, 
            // dus geaccepteerd wordt door de scanner.
            config.decoderParams.i2of5.enabled = true;

            _scanner.setConfig(config);

            if (_scanner.isEnabled()) {
                _scanner.read();
            } else {
                throw new Exception("Device not Enabled");
            }
            
            Log.d(getClass().getSimpleName(), "Started Scanning");   
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            throw e;
        }
    }

    public void stop() throws Exception 
    {
        Log.d(getClass().getSimpleName(), "Stop Scanning"); 
        _isScanning = false;
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

    public void close()
    {
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
            scannerName = _deviceList.get(_deviceId).getFriendlyName();
        }

        if (scannerName.equalsIgnoreCase(scannerNameExtScanner)) {
            switch (connectionState) {
                case CONNECTED:
                    deInitializeScanner();
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
                    if(_isScanning) {
                        try {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            _scanner.read();
                        } catch (ScannerException e) {
                            e.printStackTrace();
                        }
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
