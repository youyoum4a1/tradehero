package com.tradehero.firmbargain.hengsheng.services;

import retrofit.Callback;

/**
 * Created by Sam on 15/8/25.
 */
public interface HengshengRequestCallback<T> extends Callback<T> {

    void sessionTimeout();
}
