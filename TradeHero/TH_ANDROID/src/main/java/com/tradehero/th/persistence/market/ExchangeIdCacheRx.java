package com.ayondo.academy.persistence.market;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.ayondo.academy.api.market.ExchangeCompactDTO;
import com.ayondo.academy.api.market.ExchangeIntegerId;
import com.ayondo.academy.api.market.ExchangeStringId;
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
