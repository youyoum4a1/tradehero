package com.tradehero.livetrade.services;

/**
 * Created by Sam on 15/8/27.
 */
public interface LiveTradeCallback<T> {

    void onSuccess(T t);

    void onError(String errorCode, String errorContent);
}
