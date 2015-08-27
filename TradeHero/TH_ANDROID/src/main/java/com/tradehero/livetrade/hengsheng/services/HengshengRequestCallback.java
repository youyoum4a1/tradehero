package com.tradehero.livetrade.hengsheng.services;

import com.tradehero.livetrade.hengsheng.data.HengshengBaseDTO;

import retrofit.Callback;
import retrofit.client.Response;

/**
 * Created by Sam on 15/8/25.
 */
public abstract class HengshengRequestCallback<T> implements Callback<T> {

    abstract public void sessionTimeout();

    abstract public void hengshengSuccess(HengshengBaseDTO hengshengBaseDTO, Response response);

    abstract public void hengshengError(HengshengBaseDTO hengshengBaseDTO, Response response);

    @Override
    public void success(T t, Response response) {
        HengshengBaseDTO baseDTO = (HengshengBaseDTO) t;
        if (baseDTO.error_code != null) {
            hengshengError(baseDTO, response);
        }
        else {
            hengshengSuccess(baseDTO, response);
        }
    }
}
