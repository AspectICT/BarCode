package com.plugin.barcode;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerConfig;
import com.symbol.emdk.barcode.ScannerException;
import java.util.List;

/**
 * Created by Gerjan on 11-11-2016.
 */
public interface IBarcodeReaderManager 
{
    void close();
    void setOnReadyCallback(IObserver observer);
    List<Device> getAvailableDevices();
    void start() throws Exception;
    void stop() throws ScannerException, Exception;
    Scanner getScanner();
    void deInitializeScanner();
    void initializeScanner(String triggerType, int deviceId);
}