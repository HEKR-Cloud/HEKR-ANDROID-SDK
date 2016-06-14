;(function() {
	if (window.WebViewJavascriptBridge) {
		return;
	}
	//console.log('first------------');
	var messageHandlers = {};
	var responseCallbacks = {};
	var uniqueId = 1;

	function init(messageHandler) {
		if (WebViewJavascriptBridge._messageHandler) { throw new Error('WebViewJavascriptBridge.init called twice'); }
		WebViewJavascriptBridge._messageHandler = messageHandler;
	}

	function send(data, responseCallback) {
		_doSend({ data:data }, responseCallback);
	}

	function registerHandler(handlerName, handler) {
		messageHandlers[handlerName] = handler;
	}

	function callHandler(handlerName, data, responseCallback) {
		_doSend({ handlerName:handlerName, data:data }, responseCallback);
	}

	function _doSend(message, responseCallback) {
		if (responseCallback) {
			var callbackId = 'cb_'+(uniqueId++)+'_'+new Date().getTime();
			responseCallbacks[callbackId] = responseCallback;
			message['callbackId'] = callbackId;
		}
		_WebViewJavascriptBridge._handleMessageFromJs(JSON.stringify(message.data)||null,message.responseId||null,
		message.responseData||null,message.callbackId||null,message.handlerName||null);

	}

	function _dispatchMessageFromJava(messageJSON) {
		var message = JSON.parse(messageJSON);
		var messageHandler;

		if (message.responseId) {
			var responseCallback = responseCallbacks[message.responseId];
			if (!responseCallback) { return; }
			responseCallback(message.responseData);
			delete responseCallbacks[message.responseId];
		} else {
			var responseCallback;
			if (message.callbackId) {
				var callbackResponseId = message.callbackId;
				responseCallback = function(responseData) {
					_doSend({ responseId:callbackResponseId, responseData:responseData });
				}
			}

			var handler = WebViewJavascriptBridge._messageHandler;
			if (message.handlerName) {
				handler = messageHandlers[message.handlerName];
			}
			try {
				handler(message.data, responseCallback);
			} catch(exception) {
				if (typeof console != 'undefined') {
					console.log("WebViewJavascriptBridge: WARNING: javascript handler threw.", message, exception);
					console.log(message.data);
					console.log(exception);
				}
			}
		}
	}

	function _handleMessageFromJava(messageJSON) {
		_dispatchMessageFromJava(messageJSON);
	}

	function _initHekrSDK(bridge) {
        function _send(command,devTid,callback){
          bridge.callHandler('send',{'tid':devTid,'command':command},function(ret){
            callback(ret.obj,ret.error);
          });
        }
        function _recv(filter,handle){
          function createGuid(){
            return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
              var r = Math.random()*16|0, v = c === 'x' ? r : (r&0x3|0x8);
              return v.toString(16);
            });
          }
          var uuid = createGuid();
          window.Hekr.messageHandels[uuid] = handle;
          bridge.callHandler('recv',{'filter':filter,'funcId':uuid},function(ret){
          });
        }
        function _callRecvHandle(data){
          if(data.funcId && window.Hekr.messageHandels.hasOwnProperty(data.funcId)){
            var func = window.Hekr.messageHandels[data.funcId];
            func && func(data.obj);
          }
        }
        function _configSearch(ssid,pwd,callback){
          bridge.configHandle = callback;
          bridge.callHandler('configSearch',{'ssid':ssid,'pwd':pwd},function(ret){
          });
        }
        function _defaultConfigSearchHandle(dev){
        }
        function _callConfigSearchHandle(data){
            var func = bridge.configHandle;
            func && func(data.obj);
        }
        function _cancelConfigSearch(){
          bridge.configHandle = _defaultConfigSearchHandle;
          bridge.callHandler('cancelConfigSearch',{},function(ret){
          });
        }
        function _currentUser(callback){
          bridge.callHandler('currentUser',{},function(ret){
            callback(ret.obj);
          });
        }
        function _logout(){
          bridge.callHandler('logout',{},function(ret){
          });
        }
        function _setUserHandle(callback){
          window.Hekr.userHandel = function(ret){
            callback(ret.obj);
          };
        }
        function _defaultUserHandel(user){
        }
        function _close(animation){
          bridge.callHandler('close',{'animation':animation},function(ret){
          });
        }
        function _closeAll(animation){
          bridge.callHandler('closeAll',{'animation':animation},function(ret){
          });
        }
        function _currentSSID(callback){
          bridge.callHandler('currentSSID',{},function(res){
            callback(res.obj);
          });
        }
        function _open(schameurl){
          bridge.callHandler('open',{'url':schameurl},function(res){
          });
        }
        function _QRScan(title,callback){
          bridge.callHandler('qrScan',{"title":title},function(res){
            callback(res.code,res.error);
          });
        }
        function _backTo(path,animation){
          bridge.callHandler('backTo',{'path':path,'animation':animation},function(res){});
        }
        function _login(userName,password,callback){
          bridge.callHandler('login',{"userName":userName,"password":password},function(res){
            callback(res.obj,res.error);
          });
        }
        function _saveConfig(obj){
          bridge.callHandler('saveConfig',{"obj":obj},function(res){
          });
        }
        function _getConfig(callback){
          bridge.callHandler('getConfig',{},function(res){
            callback(res.obj);
          });
        }
        function _notify(data){
          bridge.callHandler('notify',{"obj":data},function(res){
          });
        }
        function _defaultNotifyHandel(data){
        }
        function _setNotifyHandel(handle){
          window.Hekr.notifyHandel = handle;
        }
        function _takePhoto(camera,callback){
          bridge.callHandler('takePhoto',{"camera":camera},function(res){
            callback(res.obj,res.error);
          });
        }
        function _setNetHandle(handle){
          window.Hekr.netChangeHandel = handle;
        }
        function _defaultNetHandle(){
        }
        window.Hekr = {
          plantform:"Android",
          messageHandels:{},
          configHandle:_defaultConfigSearchHandle,
          userHandel:_defaultUserHandel,
          netChangeHandel:_defaultNetHandle,
          notifyHandel:_defaultNotifyHandel,
          send:_send,
          recv:_recv,
          configSearch:_configSearch,
          cancelConfigSearch:_cancelConfigSearch,
          currentUser:_currentUser,
          login:_login,
          logout:_logout,
          setUserHandle:_setUserHandle,
          close:_close,
          closeAll:_closeAll,
          currentSSID:_currentSSID,
          open:_open,
          QRScan:_QRScan,
          backTo:_backTo,
          saveConfig:_saveConfig,
          getConfig:_getConfig,
          setNotifyHandel:_setNotifyHandel,
          notify:_notify,
          takePhoto:_takePhoto,
          setNetHandle:_setNetHandle
        }
        bridge.init(function(message, responseCallback) {
          responseCallback({});
        })
        bridge.registerHandler('onConfigSearch',function(data,callback){
          _callConfigSearchHandle(data);
          callback && callback({});
        })
        bridge.registerHandler('onRecv',function(data,callback){
          _callRecvHandle(data);
          callback && callback({});
        })
        bridge.registerHandler('onUserChange',function(data,callback){
          Hekr.userHandel(data);
          callback && callback({});
        })
        bridge.registerHandler('onNotify',function(data,callback){
          Hekr.notifyHandel(data.obj);
          callback && callback({});
        })
        bridge.registerHandler('onNetChange',function(data,callback){
          Hekr.netChangeHandel();
          callback && callback({});
        })
        window.close = function(){Hekr.close(true);}
      }

	//export
	window.WebViewJavascriptBridge = {
		init: init,
		send: send,
		registerHandler: registerHandler,
		callHandler: callHandler,
		_handleMessageFromJava: _handleMessageFromJava
	}

	_initHekrSDK(WebViewJavascriptBridge);

	//dispatch event
	var doc = document;
	var readyEvent = doc.createEvent('Events');
	readyEvent.initEvent('HekrSDKReady');
	readyEvent.bridge = WebViewJavascriptBridge;
	doc.dispatchEvent(readyEvent);
	//console.log('end------------');
})();
