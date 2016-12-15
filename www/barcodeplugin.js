/*global cordova, module*/
var Promise = (function () {
    function Promise() {
        this.onSuccesListener = function () { };
        this.onErrorListener = function () { };
        this.onFinalizeListener = function () { };
        this.onRejectListener = function () { };
        this.onNotifyListener = function () { };
    }
    Promise.prototype.then = function (successCallback, errorCallback, notifyCallback) {
        if (successCallback != undefined) {
            this.onSuccesListener = function (promiseValue) { return successCallback(promiseValue); };
        }
        if (errorCallback != undefined) {
            this.onErrorListener = function (reason) { return errorCallback(reason); };
        }
        if (notifyCallback != undefined) {
            this.onNotifyListener = function (state) { return errorCallback(state); };
        }
        return this;
    };
    Promise.prototype.catch = function (onRejected) {
        if (onRejected != undefined) {
            this.onRejectListener = function (reason) { return onRejected(reason); };
        }
        return this;
    };
    Promise.prototype.finally = function (finallyCallback) {
        this.onFinalizeListener = function () { return finallyCallback(); };
    };
    Promise.prototype.resolve = function (value) {
        if (value != undefined) {
            this.onSuccesListener(value);
            this.onFinalizeListener();
        }
        else {
            this.onSuccesListener();
        }
    };
    Promise.prototype.reject = function (reason) {
        if (reason != undefined) {
            this.onRejectListener(reason);
            this.onFinalizeListener();
        }
        else {
            this.onRejectListener();
        }
    };
    Promise.prototype.notify = function (state) {
        if (state != undefined) {
            this.onNotifyListener(state);
        }
        else {
            this.onNotifyListener();
        }
    };
    return Promise;
})();
// js interface to plugin
module.exports = {
	start: function () {
		var promise = new Promise();
        cordova.exec(function(){
			promise.resolve();
		}, function(ex){
			promise.reject(ex);
		}, "BarcodePlugin", "start", []);
		return promise;
    },
	stop: function () {
		var promise = new Promise();
        cordova.exec(function(){
			promise.resolve();
		}, function(ex){
			promise.reject(ex);
		}, "BarcodePlugin", "stop", []);
		return promise;
    },
	initialize: function () {
		var promise = new Promise();
        cordova.exec(function(){
			promise.resolve();
		}, function(ex){
			promise.reject(ex);
		}, "BarcodePlugin", "initialize", []);
		return promise;
    },
	onScanResult: function () {
		var promise = new Promise();
        cordova.exec(function(res){
			promise.resolve(res);
		}, function(ex){
			promise.reject(ex);
		}, "BarcodePlugin", "onScanResult", []);
		return promise;
    } 
};
