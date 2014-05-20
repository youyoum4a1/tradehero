package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.HelpVideoIdList;
import com.tradehero.th.api.competition.key.HelpVideoId;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class HelpVideoListCache extends StraightDTOCache<HelpVideoListKey, HelpVideoIdList>
{
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
        Timber.d("fetch %s", key);
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
