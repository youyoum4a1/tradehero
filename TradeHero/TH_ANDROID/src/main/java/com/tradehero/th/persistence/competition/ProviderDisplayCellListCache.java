package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.competition.ProviderDisplayCellDTOList;
import com.tradehero.th.api.competition.ProviderDisplayCellIdList;
import com.tradehero.th.api.competition.key.ProviderDisplayCellListKey;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class ProviderDisplayCellListCache
        extends StraightCutDTOCacheNew<ProviderDisplayCellListKey, ProviderDisplayCellDTOList, ProviderDisplayCellIdList>
{
    public static final int DEFAULT_MAX_SIZE = 50;

    @NotNull private final ProviderServiceWrapper providerServiceWrapper;
    @NotNull private final ProviderDisplayCellCache providerDisplayCellCache;

    //<editor-fold desc="Constructors">
    @Inject public ProviderDisplayCellListCache(
            @NotNull ProviderServiceWrapper providerServiceWrapper,
            @NotNull ProviderDisplayCellCache providerDisplayCellCache)
    {
        super(DEFAULT_MAX_SIZE);
        this.providerServiceWrapper = providerServiceWrapper;
        this.providerDisplayCellCache = providerDisplayCellCache;
    }
    //</editor-fold>

    @Override @NotNull public ProviderDisplayCellDTOList fetch(@NotNull ProviderDisplayCellListKey key) throws Throwable
    {
        return providerServiceWrapper.getDisplayCells(key);
    }

    @NotNull @Override protected ProviderDisplayCellIdList cutValue(@NotNull ProviderDisplayCellListKey key,
            @NotNull ProviderDisplayCellDTOList value)
    {
        providerDisplayCellCache.put(value);
        return value.createKeys();
    }

    @Nullable @Override protected ProviderDisplayCellDTOList inflateValue(@NotNull ProviderDisplayCellListKey key,
            @Nullable ProviderDisplayCellIdList cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        ProviderDisplayCellDTOList value = providerDisplayCellCache.get(cutValue);
        if (value.hasNullItem())
        {
            return null;
        }
        return value;
    }
}
