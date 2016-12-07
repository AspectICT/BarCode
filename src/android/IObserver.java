package com.plugin.barcode;


/**
 * Created by Gerjan on 7-12-2016.
 */
public interface IObserver {
    void onReady();
    void onScanResult(String data);
}
