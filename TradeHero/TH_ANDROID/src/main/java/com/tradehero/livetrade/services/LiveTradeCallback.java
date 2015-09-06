package com.tradehero.livetrade.services;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public interface LiveTradeCallback<T> {

    void onSuccess(T t);

    void onError(String errorCode, String errorContent);
}
