package com.tradehero.th.persistence.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.models.security.WarrantSpecificKnowledgeFactory;
import com.tradehero.th.network.service.ProviderServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class ProviderCacheRx extends BaseFetchDTOCacheRx<ProviderId, ProviderDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;

    @NonNull private final ProviderServiceWrapper providerServiceWrapper;
    @NonNull private final WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory;

    //<editor-fold desc="Constructors">
    @Inject public ProviderCacheRx(
            @NonNull ProviderServiceWrapper providerServiceWrapper,
            @NonNull WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.providerServiceWrapper = providerServiceWrapper;
        this.warrantSpecificKnowledgeFactory = warrantSpecificKnowledgeFactory;
    }
    //</editor-fold>

    @Override @NonNull public Observable<ProviderDTO> fetch(@NonNull final ProviderId key)
    {
        return providerServiceWrapper.getProviderRx(key);
    }

    @Override public void onNext(@NonNull ProviderId key, @NonNull ProviderDTO value)
    {
        warrantSpecificKnowledgeFactory.add(value);
        super.onNext(key, value);
    }

    public void onNext(List<? extends  ProviderDTO> providers)
    {
        for (ProviderDTO provider : providers)
        {
            onNext(provider.getProviderId(), provider);
        }
    }
}
