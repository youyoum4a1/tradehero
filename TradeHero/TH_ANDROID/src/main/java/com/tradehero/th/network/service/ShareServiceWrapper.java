package com.tradehero.th.network.service;

import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by palmer on 14/11/21.
 */

@Singleton
public class ShareServiceWrapper {

    @NotNull private final ShareServiceAsync shareServiceAsync;

    @Inject public ShareServiceWrapper(@NotNull ShareServiceAsync shareServiceAsync){
        this.shareServiceAsync = shareServiceAsync;
    }

    public void getShareEndPoint(Callback<String> callback){
        shareServiceAsync.getShareEndPoint(callback);
    }
}
