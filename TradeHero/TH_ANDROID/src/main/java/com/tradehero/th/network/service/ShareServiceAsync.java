package com.tradehero.th.network.service;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by palmer on 14/11/21.
 */
public interface ShareServiceAsync {

    @GET("/social/endpoint") void getShareEndPoint(Callback<String> callback);
}
