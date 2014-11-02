package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.SystemCache;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeStringId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @SystemCache
public class ExchangeIdCacheRx extends BaseDTOCacheRx<ExchangeStringId, ExchangeIntegerId>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeIdCacheRx(@NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
    }
    //</editor-fold>

    public void onNext(@NotNull List<ExchangeCompactDTO> exchangeCompactDTOs)
    {
        for (@NotNull ExchangeCompactDTO exchangeCompactDTO: exchangeCompactDTOs)
        {
            onNext(exchangeCompactDTO.getExchangeStringId(), exchangeCompactDTO.getExchangeIntegerId());
        }
    }
}
