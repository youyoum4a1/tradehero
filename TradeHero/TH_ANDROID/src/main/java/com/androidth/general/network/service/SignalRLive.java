package com.androidth.general.network.service;

import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

/**
 * Created by ayushnvijay on 6/22/16.
 */
public class SignalRLive  {

    public HubConnection setConnection(String url) {
        return new HubConnection(url);
    }

    public HubProxy setProxy(String hubName, HubConnection connection) {
        return connection.createHubProxy(hubName);
    }

}
