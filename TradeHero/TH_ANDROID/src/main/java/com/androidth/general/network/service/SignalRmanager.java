package com.androidth.general.network.service;

import android.support.annotation.MainThread;
import android.util.Log;

import com.androidth.general.api.competition.EmailVerifiedDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.fragments.live.ayondo.LiveSignUpStep1AyondoFragment;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.retrofit.RequestHeaders;
import com.androidth.general.utils.Constants;
import com.androidth.general.utils.StringUtils;
import com.androidth.general.widget.validation.EmailValidationDTO;
import com.androidth.general.widget.validation.KYCVerifyButton;
import com.androidth.general.widget.validation.VerifyButtonState;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler2;

/**
 * Created by ayushnvijay on 6/23/16.
 */
public class SignalRManager {

    private HubProxy proxy;
    @Inject static CurrentUserId currentUserId;
    @Inject static RequestHeaders requestHeaders;

    static final THhubConnection connection;
    static {
        connection = new THhubConnection(LiveNetworkConstants.TRADEHERO_LIVE_ENDPOINT);
        connection.setCredentials(request -> {
            request.addHeader(Constants.AUTHORIZATION, requestHeaders.headerTokenLive());
            request.addHeader(Constants.USER_ID,currentUserId.get().toString());
        });
    }
    private SignalRManager(String hubName) {
        this.proxy = connection.createHubProxy(hubName);
    }

    public String[] getSecurityIds(List<SecurityCompactDTO> items){
        Iterator<SecurityCompactDTO> iterator = items.iterator();
        ArrayList<String > stringArray = new ArrayList<>();

        while (iterator.hasNext()) {
            stringArray.add(iterator.next().getResourceId().toString());
        }

        String[] strings = new String[stringArray.size()];
        stringArray.toArray(strings);

        return strings;
    }

    public boolean startWithInvoke(String groupName, List <SecurityCompactDTO> items){
        try {
            connection.start().done(new Action<Void>() {
                @Override
                public void run(Void aVoid) throws Exception {
                    proxy.invoke(groupName, getSecurityIds(items), currentUserId.get());
                }
            });
            return true;
        }
        catch (Exception e){
            return false;
        }
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

    public void subscribeOn(String eventName, SubscriptionHandler subscriptionHandler) {
        proxy.on(eventName, subscriptionHandler);
    }
    public void subscribeOn(String eventName, SubscriptionHandler1<Object> subscriptionHandler) {
        proxy.on(eventName, subscriptionHandler, Object.class);
    }
    public void subscribeOn(String eventName, SubscriptionHandler2<Object, Object> subscriptionHandler) {
        proxy.on(eventName, subscriptionHandler, Object.class, Object.class);
    }

    public static void initWithEvent(String userId, String requestHeader, String hubName, String eventName, KYCVerifyButton kycVerifyButton) {
        THhubConnection connection = new THhubConnection(LiveNetworkConstants.TRADEHERO_LIVE_ENDPOINT);
        connection.setCredentials(request -> {
            request.addHeader(Constants.AUTHORIZATION, requestHeader);
            request.addHeader(Constants.USER_ID, userId);
        });
        HubProxy hubProxy = connection.createHubProxy(hubName);
        try{
             connection.start();
            switch (eventName){
                case "SetValidationStatus":
                    Log.v("", "SignalRManager proxy on");
                    hubProxy.on(eventName, emailVerifiedDTO->{
                        Log.v("", "SignalRManager received "+emailVerifiedDTO.getMessage()+":"+emailVerifiedDTO.isValidated());
                        if(emailVerifiedDTO.isValidated()){
                            updateKycVerifyButton(kycVerifyButton, VerifyButtonState.FINISH);
                        }
                    }, EmailVerifiedDTO.class);
                hubProxy.subscribe(LiveSignUpStep1AyondoFragment.class);
                    break;
                default:
                    break;
            }
        }catch (Exception e){
Log.v("", e.getLocalizedMessage());
        }

    }

    @MainThread
    private static void updateKycVerifyButton(KYCVerifyButton button, VerifyButtonState finish) {
        button.setState(VerifyButtonState.FINISH);
    }
}

//Reconnects itself if connection is lost
class THhubConnection extends HubConnection{
    @Inject CurrentUserId currentUserId;
    @Inject RequestHeaders requestHeaders;

    static {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
    }
    public THhubConnection(String url) {
        super(url);
        setCredentials();
    }

    public static void setCredentials(){

    }
    @Override
    protected void onClosed() {
        super.onClosed();
//        super.start();
    }

}

