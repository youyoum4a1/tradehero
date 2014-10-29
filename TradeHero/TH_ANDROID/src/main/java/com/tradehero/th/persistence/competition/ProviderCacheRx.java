package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.models.security.WarrantSpecificKnowledgeFactory;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

@Singleton public class ProviderCacheRx extends BaseFetchDTOCacheRx<ProviderId, ProviderDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 1000;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NotNull private final Lazy<ProviderListCacheRx> providerListCache;
    @NotNull private final WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory;

    //<editor-fold desc="Constructors">
    @Inject public ProviderCacheRx(
            @NotNull Lazy<ProviderListCacheRx> providerListCache,
            @NotNull WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, DEFAULT_MAX_SUBJECT_SIZE);
        this.providerListCache = providerListCache;
        this.warrantSpecificKnowledgeFactory = warrantSpecificKnowledgeFactory;
    }
    //</editor-fold>

    @Override @NotNull public Observable<ProviderDTO> fetch(@NotNull final ProviderId key)
    {
        return providerListCache.get()
                .get(new ProviderListKey(ProviderListKey.ALL_PROVIDERS))
                .flatMap(providerList -> Observable.from(providerList.second))
                .first(providerDTO -> key.key.equals(providerDTO.id));
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
