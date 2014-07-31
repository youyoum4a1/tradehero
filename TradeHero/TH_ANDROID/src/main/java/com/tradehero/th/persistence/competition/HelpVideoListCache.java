package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.competition.HelpVideoDTOList;
import com.tradehero.th.api.competition.HelpVideoIdList;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class HelpVideoListCache extends StraightCutDTOCacheNew<HelpVideoListKey, HelpVideoDTOList, HelpVideoIdList>
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

    @Override @NotNull public HelpVideoDTOList fetch(@NotNull HelpVideoListKey key) throws Throwable
    {
        return providerServiceWrapper.getHelpVideos(key);
    }

    @NotNull @Override protected HelpVideoIdList cutValue(@NotNull HelpVideoListKey key, @NotNull HelpVideoDTOList value)
    {
        helpVideoCache.put(value);
        return value.createKeys();
    }

    @Nullable @Override protected HelpVideoDTOList inflateValue(@NotNull HelpVideoListKey key, @Nullable HelpVideoIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        HelpVideoDTOList value = helpVideoCache.get(cutValue);
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
