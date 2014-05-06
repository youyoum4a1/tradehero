package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeStringId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton public class ExchangeIdCache extends StraightDTOCache<ExchangeStringId, ExchangeIntegerId>
{
    public static final String TAG = ExchangeIdCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeIdCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected ExchangeIntegerId fetch(ExchangeStringId key) throws Throwable
    {
        throw new IllegalArgumentException("Cannot fetch here");
    }

    public void put(List<ExchangeDTO> exchangeDTOs)
    {
        if (exchangeDTOs == null)
        {
            return;
        }

        for (ExchangeDTO exchangeDTO: exchangeDTOs)
        {
            put(exchangeDTO.getExchangeStringId(), exchangeDTO.getExchangeIntegerId());
        }
    }
}
