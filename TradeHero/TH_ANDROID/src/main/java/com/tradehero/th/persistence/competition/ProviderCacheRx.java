package com.ayondo.academy.persistence.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.ayondo.academy.api.competition.ProviderDTO;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.network.service.ProviderServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class ProviderCacheRx extends BaseFetchDTOCacheRx<ProviderId, ProviderDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final ProviderServiceWrapper providerServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public ProviderCacheRx(
            @NonNull ProviderServiceWrapper providerServiceWrapper,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.providerServiceWrapper = providerServiceWrapper;
    }
    //</editor-fold>

    @Override @NonNull public Observable<ProviderDTO> fetch(@NonNull final ProviderId key)
    {
        return providerServiceWrapper.getProviderRx(key);
    }

    public void onNext(@NonNull List<? extends  ProviderDTO> providers)
    {
        for (ProviderDTO provider : providers)
        {
            onNext(provider.getProviderId(), provider);
        }
    }
}
