package com.tradehero.th.network.service;

import com.tradehero.th.api.live.TradingAvailableDTO;
import retrofit.http.GET;
import rx.Observable;

public interface LiveServiceRx
{
    @GET("/available")
    Observable<TradingAvailableDTO> isAvailable();
}
