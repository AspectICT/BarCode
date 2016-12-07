package com.symbol.barcodesample1.plugin;

/**
 * Created by Gerjan on 7-12-2016.
 */
public interface IObserver {
    void onReady();
    void onScanResult(String data);
}
