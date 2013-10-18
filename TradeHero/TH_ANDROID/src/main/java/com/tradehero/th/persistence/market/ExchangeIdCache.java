package com.tradehero.th.persistence.market;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeStringId;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/18/13 Time: 6:39 PM To change this template use File | Settings | File Templates. */
@Singleton public class ExchangeIdCache extends StraightDTOCache<String, ExchangeStringId, ExchangeIntegerId>
{
    public static final String TAG = ExchangeIdCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    //<editor-fold desc="Constructors">
    @Inject public ExchangeIdCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected ExchangeIntegerId fetch(ExchangeStringId key)
    {
        throw new IllegalArgumentException("Cannot fetch here");
    }
}
