package com.tradehero.common.billing.alipay.service;

import com.tradehero.th.api.alipay.OrderIdFormDTO;
import com.tradehero.th.api.alipay.OrderStatusDTO;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by alex on 14-4-12.
 */
public interface AlipayService
{
    @POST("/alipay/createOrder") void getOrderId(
            @Body OrderIdFormDTO orderIdFormDTO,
            Callback<String> callback);

    @GET("/alipay/order/{orderId}") void checkWithServer(
            @Path("orderId") String orderId,
            Callback<OrderStatusDTO> callback);
}
