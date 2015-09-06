package com.tradehero.livetrade.thirdPartyServices.hengsheng.services;

import com.tradehero.livetrade.thirdPartyServices.hengsheng.data.HengshengBaseDTO;

import retrofit.Callback;
import retrofit.client.Response;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public abstract class HengshengRequestCallback<T> implements Callback<T> {

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
