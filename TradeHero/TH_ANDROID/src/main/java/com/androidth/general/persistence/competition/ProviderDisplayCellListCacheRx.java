package com.androidth.general.persistence.competition;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.competition.ProviderDisplayCellDTOList;
import com.androidth.general.api.competition.key.ProviderDisplayCellListKey;
import com.androidth.general.network.service.ProviderServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class ProviderDisplayCellListCacheRx
        extends BaseFetchDTOCacheRx<ProviderDisplayCellListKey, ProviderDisplayCellDTOList>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final ProviderServiceWrapper providerServiceWrapper;
    @NonNull private final ProviderDisplayCellCacheRx providerDisplayCellCache;

    //<editor-fold desc="Constructors">
    @Inject public ProviderDisplayCellListCacheRx(
            @NonNull ProviderServiceWrapper providerServiceWrapper,
            @NonNull ProviderDisplayCellCacheRx providerDisplayCellCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.providerServiceWrapper = providerServiceWrapper;
        this.providerDisplayCellCache = providerDisplayCellCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<ProviderDisplayCellDTOList> fetch(@NonNull ProviderDisplayCellListKey key)
    {
        return providerServiceWrapper.getDisplayCellsRx(key);
    }

    @Override public void onNext(@NonNull ProviderDisplayCellListKey key, @NonNull ProviderDisplayCellDTOList value)
    {
        providerDisplayCellCache.put(value);
        super.onNext(key, value);
    }
}
