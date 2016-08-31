package com.androidth.general.network.service;

import android.util.Log;

import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.retrofit.RequestHeaders;
import com.androidth.general.utils.Constants;
import com.google.gson.JsonElement;

import javax.inject.Inject;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.InvalidStateException;
import microsoft.aspnet.signalr.client.MessageReceivedHandler;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.StateChangedCallback;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class SignalRManager {

    private CurrentUserId currentUserId;
    private HubConnection connection;
    private HubProxy hubProxy;

    //step 1, initialize
    public SignalRManager(RequestHeaders requestHeaders, CurrentUserId currentUserId, String hubName){
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        this.currentUserId = currentUserId;
        this.connection = new HubConnection(LiveNetworkConstants.TRADEHERO_LIVE_ENDPOINT);
        this.connection.setCredentials(new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader(Constants.AUTHORIZATION, requestHeaders.headerTokenLive());
                request.addHeader(Constants.USER_ID, currentUserId.get().toString());
            }
        });
        this.hubProxy = this.connection.createHubProxy(hubName);
    }

    public HubProxy getCurrentProxy(){
        return this.hubProxy;
    }

    public HubConnection getCurrentConnection(){
        return this.connection;
    }

    public boolean start(){
        try {
            connection.start();
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

//    public void subscribeOn(String eventName, SubscriptionHandler subscriptionHandler) {
//        proxy.on(eventName, subscriptionHandler);
//    }
//    public void subscribeOn(String eventName, SubscriptionHandler1<Object> subscriptionHandler) {
//        proxy.on(eventName, subscriptionHandler, Object.class);
//    }
//    public void subscribeOn(String eventName, SubscriptionHandler2<Object, Object> subscriptionHandler) {
//        proxy.on(eventName, subscriptionHandler, Object.class, Object.class);
//    }

//    public void initWithEvent(String hubName,
//                              String eventName,
//                              SubscriptionHandler1<Object> handler,
//                              Class<?> classParameter,
//                              Class<?> classSubscribed) {
//
//        try {
//            hubProxy = this.connection.createHubProxy(hubName);//step 1, configure hub connection
//
//        } catch (InvalidStateException e) {
//            //already connected, no need to create hub proxy
//            Log.v("SignalR", "No need to create "+hubName);
//            e.printStackTrace();
//        }
//
//        try {
//            //step 3, configure handlers
//            switch (eventName) {
//                case "SetValidationStatus":
//                    hubProxy.on(eventName, emailVerifiedDTO -> {
//                        Log.v("SignalR", "SignalRManager proxy on");
//                        if (((EmailVerifiedDTO) emailVerifiedDTO).isValidated()) {
//                            handler.run(emailVerifiedDTO);
//                        }
//
//                    }, classParameter);
////                    hubProxy.subscribe(classSubscribed);
//                    break;
//
//                case "UpdatePortfolio":
//                    hubProxy.on(eventName, updatedProfile -> {
//
//                        Log.v("SignalR", "signalr Update portfolio ");
//                        handler.run(updatedProfile);
//
//                    }, classParameter);
////                    hubProxy.subscribe(classSubscribed);
//                    break;
//
//                case "UpdatePositions":
//                    hubProxy.on(eventName, positionsList -> {
//
//                        Log.v("SignalR", "signalr Update positions ");
//                        handler.run(positionsList);
//
//                    }, classParameter);
////                    hubProxy.subscribe(classSubscribed);
//                    break;
//
//                case "UpdateQuote":
//                    hubProxy.on(eventName, positionsList -> {
//
//                        Log.v("SignalR", "signalr Update quote ");
//                        handler.run(positionsList);
//
//                    }, classParameter);
//                    break;
//                default:
//                    break;
//            }
//
//        } catch (Exception e) {
//            Log.v("", e.getLocalizedMessage());
//        }
//    }

    //step 2, start connection
    public void startConnection(String invokeWith, String[] arg){
        if (invokeWith != null) {
            //step 2, setup connection
            this.connection.start().done(new Action<Void>() {
                @Override
                public void run(Void aVoid) throws Exception {
                    Log.v("SignalR", "signalr Proxy invoked started "+invokeWith);
                    hubProxy.invoke(invokeWith, arg, currentUserId.get());
                }
            });
            this.connection.reconnecting(new Runnable() {
                @Override
                public void run() {
                    Log.v("SignalR", "signalr Proxy reconnecting");
                }
            });
            this.connection.reconnected(new Runnable() {
                @Override
                public void run() {
                    Log.v("SignalR", "signalr Proxy invoked reconnected");
                    hubProxy.invoke(invokeWith, arg, currentUserId.get());
                }
            });
            this.connection.received(new MessageReceivedHandler() {
                @Override
                public void onMessageReceived(JsonElement jsonElement) {
                    Log.v("SignalR", "Received! "+jsonElement);
                }
            });
            this.connection.error(new ErrorCallback() {
                @Override
                public void onError(Throwable throwable) {
                    Log.v("SignalR", "ERROR! "+throwable.getMessage());
                }
            });
            this.connection.closed(new Runnable() {
                @Override
                public void run() {
                    Log.v("SignalR", "Closed!");
                }
            });
            this.connection.stateChanged(new StateChangedCallback() {
                @Override
                public void stateChanged(ConnectionState connectionState, ConnectionState connectionState1) {
                    Log.v("SignalR", "State changed "+connectionState +":"+connectionState1);
                }
            });

        } else {
            Log.v("SignalR", "signalr Proxy started");
            this.connection.start();
        }

    }
}

//
//class THhubConnection extends HubConnection{
//    @Inject CurrentUserId currentUserId;
//    @Inject RequestHeaders requestHeaders;
//
//    static {
//        Platform.loadPlatformComponent(new AndroidPlatformComponent());
//    }
//    public THhubConnection(String url) {
//        super(url);
//        setCredentials();
//    }
//
//    public static void setCredentials(){
//
//    }
//    @Override
//    protected void onClosed() {
//        super.onClosed();
////        super.start();
//    }
//
//}

