package com.androidth.general.network.service;

import android.util.Log;

import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.retrofit.RequestHeaders;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.utils.Constants;
import com.google.gson.JsonElement;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.ErrorCallback;
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
    public SignalRManager(RequestHeaders requestHeaders, CurrentUserId currentUserId, String hubName) {
        this(requestHeaders, currentUserId, hubName, LiveNetworkConstants.TRADEHERO_LIVE_1B_ENDPOINT);
    }

    public SignalRManager(RequestHeaders requestHeaders, CurrentUserId currentUserId, String hubName, String hubAddress){
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        this.currentUserId = currentUserId;
        this.connection = new HubConnection(hubAddress);
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

    //step 2, start connection
    private void startConnectionNow(String invokeWith, String[] args, boolean withUserId){

        this.connection.start().done(new Action<Void>() {
            @Override
            public void run(Void aVoid) throws Exception {

                if(withUserId){
                    Log.v("SignalR", "signalr Proxy invoked started "+invokeWith + " args="+args);
                    hubProxy.invoke(invokeWith, args, currentUserId.get());
                }else{
                    Log.v("SignalR", "signalr Proxy invoked started without user id");
                    hubProxy.invoke(invokeWith, args);
                }
            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                if(throwable!=null){
                    new TimberOnErrorAction1(throwable.getMessage());
                }else{
                    new TimberOnErrorAction1("SignalRManager connection error");
                }
            }
        });

        this.connection.reconnected(new Runnable() {
            @Override
            public void run() {
                if(withUserId){
                    Log.v("SignalR", "signalr Proxy invoked reconnected "+invokeWith + " args="+args);
                    hubProxy.invoke(invokeWith, args, currentUserId.get());
                }else{
                    Log.v("SignalR", "signalr Proxy invoked reconnected without user id");
                    hubProxy.invoke(invokeWith, args);
                }
            }
        });

        setDefaults();
    }

    //step 2, start connection
    private void startConnectionNow(String invokeWith, String args, boolean withUserId){

        this.connection.start().done(new Action<Void>() {
            @Override
            public void run(Void aVoid) throws Exception {

                if(withUserId){
                    Log.v("SignalR", "signalr Proxy invoked started "+invokeWith + " args="+args);
                    hubProxy.invoke(invokeWith, args, currentUserId.get());
                }else{
                    Log.v("SignalR", "signalr Proxy invoked started without user id");
                    hubProxy.invoke(invokeWith, args);
                }
            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable throwable) {
                if(throwable!=null){
                    new TimberOnErrorAction1(throwable.getMessage());
                }else{
                    new TimberOnErrorAction1("SignalRManager connection error");
                }
            }
        });

        this.connection.reconnected(new Runnable() {
            @Override
            public void run() {
                if(withUserId){
                    Log.v("SignalR", "signalr Proxy invoked reconnected "+invokeWith + " args="+args);
                    hubProxy.invoke(invokeWith, args, currentUserId.get());
                }else{
                    Log.v("SignalR", "signalr Proxy invoked reconnected without user id");
                    hubProxy.invoke(invokeWith, args);
                }
            }
        });

        setDefaults();
    }

    public void startConnectionNow(){
        this.connection.start().done(new Action<Void>() {
            @Override
            public void run(Void aVoid) throws Exception {
                Log.v("SignalR", "signalr Proxy invoked started ");
            }
        });
        this.connection.reconnecting(new Runnable() {
            @Override
            public void run() {
                Log.v("SignalR", "signalr Proxy reconnecting");
            }
        });

        setDefaults();
    }

    public void startConnectionWithUserId(String invokeWith, String args){
        if (invokeWith != null) {
            startConnectionNow(invokeWith, args, true);

        } else {
            Log.v("SignalR", "signalr Proxy started");
            startConnectionNow();
        }
    }

    public void startConnectionWithUserId(String invokeWith, String[] args){
        if (invokeWith != null) {
            startConnectionNow(invokeWith, args, true);

        } else {
            Log.v("SignalR", "signalr Proxy started");
            startConnectionNow();
        }
    }

    public void startConnectionWithoutUserID(String invokeWith, String[] args) {
        if (invokeWith != null) {
            startConnectionNow(invokeWith, args, false);
        }else{
            startConnectionNow();
        }
    }

    private void setDefaults(){
        this.connection.reconnecting(new Runnable() {
            @Override
            public void run() {
                Log.v("SignalR", "signalr Proxy reconnecting");
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
                throwable.printStackTrace();
                if(throwable!=null){
                    Log.v("SignalR", "ERROR! "+throwable.getMessage());
                }else{
                    Log.v("SignalR", "ERROR starting connection");
                }
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
    }
}