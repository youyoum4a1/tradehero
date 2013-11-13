package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.network.service.AlertService;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class AlertCompactCache extends StraightDTOCache<AlertId, AlertCompactDTO>
{
    public static final String TAG = AlertCompactCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    //<editor-fold desc="Constructors">
    @Inject public AlertCompactCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected AlertCompactDTO fetch(AlertId key) throws Throwable
    {
        throw new IllegalStateException("No fetcher on this cache");
    }
}
