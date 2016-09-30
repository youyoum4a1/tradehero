package com.androidth.general.network;

public class LiveNetworkConstants
{
    // TODO https
    public static final String TRADEHERO_LIVE_ENDPOINT = "https://live.tradehero.mobi/";

    public static final String TRADEHERO_LIVE_1B_ENDPOINT = "https://devlive.tradehero.mobi/";

    //"http://192.168.1.10:62998/";
//    "http://192.168.8.100:62998/"
    //https://live.tradehero.mobi/
    public static final String TRADEHERO_LIVE_API_ENDPOINT = TRADEHERO_LIVE_ENDPOINT + "api/";

    public static final String TRADEHERO_LIVE_1B_API_ENDPOINT = TRADEHERO_LIVE_1B_ENDPOINT + "api/";

    public static final String CLIENT_NOTIFICATION_HUB_NAME = "clientnotificationhub";
    public static final String PORTFOLIO_HUB_NAME = "portfoliohub";
    public static final String PROXY_METHOD_ADD_TO_GROUPS = "AddToGroups";
    public static final String PROXY_METHOD_ADD_TO_GROUP = "AddToGroup";

    public static final String PROXY_METHOD_UPDATE_POSITIONS = "UpdatePositions";
    public static final String PROXY_METHOD_UPDATE_PROFILE = "UpdatePortfolio";
}
