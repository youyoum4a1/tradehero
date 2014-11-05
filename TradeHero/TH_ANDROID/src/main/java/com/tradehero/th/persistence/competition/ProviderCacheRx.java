package com.tradehero.th.persistence.competition;

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
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton @UserCache
public class ProviderCacheRx extends BaseFetchDTOCacheRx<ProviderId, ProviderDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 50;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 4;

    @NotNull private final ProviderServiceWrapper providerServiceWrapper;
    @NotNull private final WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory;

    //<editor-fold desc="Constructors">
    @Inject public ProviderCacheRx(
            @NotNull ProviderServiceWrapper providerServiceWrapper,
            @NotNull WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.providerServiceWrapper = providerServiceWrapper;
        this.warrantSpecificKnowledgeFactory = warrantSpecificKnowledgeFactory;
    }
    //</editor-fold>

    @Override @NotNull public Observable<ProviderDTO> fetch(@NotNull final ProviderId key)
    {
        return providerServiceWrapper.getProviderRx(key);
    }

    @Override public void onNext(@NotNull ProviderId key, @NotNull ProviderDTO value)
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
