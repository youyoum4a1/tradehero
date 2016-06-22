package com.androidth.general.network.service;

import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

/**
 * Created by ayushnvijay on 6/22/16.
 */
public interface SignalRInterface {
    public HubConnection setConnection(String url);
    public HubProxy setProxy(String hubName, HubConnection connection);
}
