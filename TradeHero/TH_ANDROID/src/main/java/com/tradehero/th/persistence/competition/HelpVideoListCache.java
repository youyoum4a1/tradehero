package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.key.HelpVideoId;
import com.tradehero.th.api.competition.HelpVideoIdList;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:04 PM To change this template use File | Settings | File Templates. */
@Singleton public class HelpVideoListCache extends StraightDTOCache<HelpVideoListKey, HelpVideoIdList>
{
    public static final String TAG = HelpVideoListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 50;

    @Inject protected ProviderServiceWrapper providerServiceWrapper;
    @Inject protected HelpVideoCache helpVideoCache;

    //<editor-fold desc="Constructors">
    @Inject public HelpVideoListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected HelpVideoIdList fetch(HelpVideoListKey key) throws Throwable
    {
        THLog.d(TAG, "fetch " + key);
        return putInternal(key, providerServiceWrapper.getHelpVideos(key));
    }

    protected HelpVideoIdList putInternal(HelpVideoListKey key, List<HelpVideoDTO> fleshedValues)
    {
        HelpVideoIdList helpVideoIds = null;
        if (fleshedValues != null)
        {
            helpVideoIds = new HelpVideoIdList();
            HelpVideoId helpVideoId;
            for (HelpVideoDTO providerDTO: fleshedValues)
            {
                helpVideoId = providerDTO.getHelpVideoId();
                helpVideoIds.add(helpVideoId);
                helpVideoCache.put(helpVideoId, providerDTO);
            }
            put(key, helpVideoIds);
        }
        return helpVideoIds;
    }
}
