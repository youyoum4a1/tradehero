package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.HelpVideoIdList;
import com.tradehero.th.api.competition.key.HelpVideoId;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class HelpVideoListCache extends StraightDTOCacheNew<HelpVideoListKey, HelpVideoIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NotNull private final ProviderServiceWrapper providerServiceWrapper;
    @NotNull private final HelpVideoCache helpVideoCache;

    //<editor-fold desc="Constructors">
    @Inject public HelpVideoListCache(
            @NotNull ProviderServiceWrapper providerServiceWrapper,
            @NotNull HelpVideoCache helpVideoCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.providerServiceWrapper = providerServiceWrapper;
        this.helpVideoCache = helpVideoCache;
    }
    //</editor-fold>

    @Override @NotNull public HelpVideoIdList fetch(@NotNull HelpVideoListKey key) throws Throwable
    {
        return putInternal(key, providerServiceWrapper.getHelpVideos(key));
    }

    @NotNull protected HelpVideoIdList putInternal(@NotNull HelpVideoListKey key, @NotNull List<HelpVideoDTO> fleshedValues)
    {
        HelpVideoIdList helpVideoIds = new HelpVideoIdList();
        @NotNull HelpVideoId helpVideoId;
        for (@NotNull HelpVideoDTO providerDTO: fleshedValues)
        {
            helpVideoId = providerDTO.getHelpVideoId();
            helpVideoIds.add(helpVideoId);
            helpVideoCache.put(helpVideoId, providerDTO);
        }
        put(key, helpVideoIds);
        return helpVideoIds;
    }
}
