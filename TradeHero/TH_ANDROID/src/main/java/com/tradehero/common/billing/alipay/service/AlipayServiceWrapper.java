package com.tradehero.common.billing.alipay.service;

import com.tradehero.th.api.alipay.OrderIdFormDTO;
import com.tradehero.th.api.alipay.OrderStatusDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

/**
 * Created by alex on 14-4-4.
 */
@Singleton public class AlipayServiceWrapper
{
    private final AlipayService alipayService;

    @Inject public AlipayServiceWrapper(AlipayService alipayService)
    {
        super();
        this.alipayService = alipayService;
    }

    public MiddleCallback<String> getOrderId(OrderIdFormDTO orderIdFormDTO,
            Callback<String> callback)
    {
        MiddleCallback<String> middleCallback = new MiddleCallback<>(callback);
        alipayService.getOrderId(orderIdFormDTO, callback);
        return middleCallback;
    }

    public MiddleCallback<OrderStatusDTO> checkWithServer(String orderId,
            Callback<OrderStatusDTO> callback)
    {
        MiddleCallback<OrderStatusDTO> middleCallback = new MiddleCallback<>(callback);
        alipayService.checkWithServer(orderId, callback);
        return middleCallback;
    }
}
