package com.tradehero.th.persistence;

import com.tradehero.th.api.competition.AdDTO;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.security.WarrantSpecificKnowledgeFactory;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.mockito.Mockito.mock;

@Module(
        library = true,
        complete = false,
        overrides = true
)
public class PersistenceTestModule
{
    @Provides @Singleton public ProviderCache provideStubProviderCache(ProviderCacheStub providerCacheStub)
    {
        return providerCacheStub;
    }

    public static class ProviderCacheStub extends ProviderCache
    {
        private final ProviderDTO mockProviderDTO;

        @Inject public ProviderCacheStub(@NotNull Lazy<ProviderListCache> providerListCache,
                @NotNull CurrentUserId currentUserId,
                @NotNull WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory)
        {
            super(providerListCache, currentUserId, warrantSpecificKnowledgeFactory);
            mockProviderDTO = new ProviderDTO();
            mockProviderDTO.associatedPortfolio = mock(PortfolioCompactDTO.class);
            mockProviderDTO.advertisements = new ArrayList<>();
            mockProviderDTO.advertisements.add(new AdDTO());
        }

        @Nullable @Override public ProviderDTO get(@Nullable ProviderId providerId)
        {
            if (providerId != null && providerId.key != null)
            {
                mockProviderDTO.id = providerId.key;
                return mockProviderDTO;
            }
            return null;
        }
    }
}
