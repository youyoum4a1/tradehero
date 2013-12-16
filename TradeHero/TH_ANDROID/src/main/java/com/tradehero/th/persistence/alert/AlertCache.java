package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.network.service.AlertServiceWrapper;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class AlertCache extends StraightDTOCache<AlertId, AlertDTO>
{
    public static final String TAG = AlertCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    @Inject protected Lazy<AlertServiceWrapper> alertServiceWrapper;
    @Inject protected Lazy<AlertCompactCache> alertCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected AlertDTO fetch(AlertId key) throws Throwable
    {
        return this.alertServiceWrapper.get().getAlert(key);
    }

    @Override public AlertDTO put(AlertId key, AlertDTO value)
    {
        alertCompactCache.get().put(key, value);
        return super.put(key, value);
    }

    public List<AlertDTO> getOrFetch(List<AlertId> followerIds) throws Throwable
    {
        if (followerIds == null)
        {
            return null;
        }

        List<AlertDTO> alertDTOs = new ArrayList<>();
        for (AlertId baseKey: followerIds)
        {
            alertDTOs.add(getOrFetch(baseKey, false));
        }
        return alertDTOs;
    }
}
