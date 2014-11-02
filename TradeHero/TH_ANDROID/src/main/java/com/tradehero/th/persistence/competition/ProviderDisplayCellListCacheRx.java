package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.competition.ProviderDisplayCellDTOList;
import com.tradehero.th.api.competition.key.ProviderDisplayCellListKey;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class ProviderDisplayCellListCacheRx
        extends BaseFetchDTOCacheRx<ProviderDisplayCellListKey, ProviderDisplayCellDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 5;

    @NotNull private final ProviderServiceWrapper providerServiceWrapper;
    @NotNull private final ProviderDisplayCellCacheRx providerDisplayCellCache;

    //<editor-fold desc="Constructors">
    @Inject public ProviderDisplayCellListCacheRx(
            @NotNull ProviderServiceWrapper providerServiceWrapper,
            @NotNull ProviderDisplayCellCacheRx providerDisplayCellCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.providerServiceWrapper = providerServiceWrapper;
        this.providerDisplayCellCache = providerDisplayCellCache;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<ProviderDisplayCellDTOList> fetch(@NotNull ProviderDisplayCellListKey key)
    {
        return providerServiceWrapper.getDisplayCellsRx(key);
    }

    @Override public void onNext(@NotNull ProviderDisplayCellListKey key, @NotNull ProviderDisplayCellDTOList value)
    {
        providerDisplayCellCache.put(value);
        super.onNext(key, value);
    }
}
