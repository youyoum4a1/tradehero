package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeStringId;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class ExchangeIdCache extends StraightDTOCacheNew<ExchangeStringId, ExchangeIntegerId>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeIdCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override @NotNull public ExchangeIntegerId fetch(@NotNull ExchangeStringId key) throws Throwable
    {
        throw new IllegalArgumentException("Cannot fetch here");
    }

    public void put(@Nullable List<ExchangeCompactDTO> exchangeCompactDTOs)
    {
        if (exchangeCompactDTOs == null)
        {
            return;
        }

        for (@NotNull ExchangeCompactDTO exchangeCompactDTO: exchangeCompactDTOs)
        {
            put(exchangeCompactDTO.getExchangeStringId(), exchangeCompactDTO.getExchangeIntegerId());
        }
    }
}
