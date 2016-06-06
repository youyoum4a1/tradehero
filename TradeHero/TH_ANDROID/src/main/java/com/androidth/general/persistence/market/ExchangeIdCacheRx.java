package com.androidth.general.persistence.market;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.SystemCache;
import com.androidth.general.api.market.ExchangeCompactDTO;
import com.androidth.general.api.market.ExchangeIntegerId;
import com.androidth.general.api.market.ExchangeStringId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @SystemCache
public class ExchangeIdCacheRx extends BaseDTOCacheRx<ExchangeStringId, ExchangeIntegerId>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeIdCacheRx(@NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NonNull List<ExchangeCompactDTO> exchangeCompactDTOs)
    {
        for (ExchangeCompactDTO exchangeCompactDTO: exchangeCompactDTOs)
        {
            onNext(exchangeCompactDTO.getExchangeStringId(), exchangeCompactDTO.getExchangeIntegerId());
        }
    }
}
