package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.AlertServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton public class AlertCompactListCache extends StraightDTOCache<UserBaseKey, AlertIdList>
{
    public static final String TAG = AlertCompactListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 50;

    @Inject protected AlertServiceWrapper alertServiceWrapper;
    @Inject protected AlertCompactCache alertCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCompactListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected AlertIdList fetch(UserBaseKey key) throws Throwable
    {
        //THLog.d(TAG, "fetch " + key);
        return putInternal(key, alertServiceWrapper.getAlerts(key));
    }

    protected AlertIdList putInternal(UserBaseKey key, List<AlertCompactDTO> fleshedValues)
    {
        AlertIdList alertIds = null;
        if (fleshedValues != null)
        {
            alertIds = new AlertIdList(key, fleshedValues);
            //alertCompactCache.invalidateAll();
            alertCompactCache.put(key, fleshedValues);
            put(key, alertIds);
        }
        return alertIds;
    }

    @Override public void invalidateAll()
    {
        super.invalidateAll();
    }
}
