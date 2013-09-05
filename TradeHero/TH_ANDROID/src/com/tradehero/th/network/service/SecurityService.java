package com.tradehero.th.network.service;

import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.network.CallbackWithSpecificNotifiers;
import java.util.List;
import retrofit.Callback;
import retrofit.http.GET;

/** Created with IntelliJ IDEA. User: xavier Date: 9/4/13 Time: 5:50 PM To change this template use File | Settings | File Templates. */
public interface SecurityService
{
    @GET("/securities/trending/")
    void getTrendingSecurities(Callback<List<SecurityCompactDTO>> callback);
}
