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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

@Singleton public class HelpVideoListCache extends StraightDTOCache<HelpVideoListKey, HelpVideoIdList>
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

    @Override protected HelpVideoIdList fetch(@NotNull HelpVideoListKey key) throws Throwable
    {
        return putInternal(key, providerServiceWrapper.getHelpVideos(key));
    }

    @Contract("_, null -> null; _, !null -> !null")
    protected HelpVideoIdList putInternal(@NotNull HelpVideoListKey key, @Nullable List<HelpVideoDTO> fleshedValues)
    {
        HelpVideoIdList helpVideoIds = null;
        if (fleshedValues != null)
        {
            helpVideoIds = new HelpVideoIdList();
            @NotNull HelpVideoId helpVideoId;
            for (@NotNull HelpVideoDTO providerDTO: fleshedValues)
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
