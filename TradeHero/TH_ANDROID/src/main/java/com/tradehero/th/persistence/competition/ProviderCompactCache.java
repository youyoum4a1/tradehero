package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.competition.ProviderCompactDTO;
import com.tradehero.th.api.competition.ProviderCompactDTOList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.models.security.WarrantSpecificKnowledgeFactory;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class ProviderCompactCache extends StraightCutDTOCacheNew<ProviderId, ProviderCompactDTO, ProviderCompactCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final Lazy<ProviderListCache> providerListCache;
    @NotNull private final Lazy<PortfolioCompactCache> portfolioCompactCache;
    @NotNull private final WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory;

    //<editor-fold desc="Constructors">
    @Inject public ProviderCompactCache(
            @NotNull Lazy<ProviderListCache> providerListCache,
            @NotNull Lazy<PortfolioCompactCache> portfolioCompactCache,
            @NotNull WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory)
    {
        super(DEFAULT_MAX_SIZE);
        this.providerListCache = providerListCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.warrantSpecificKnowledgeFactory = warrantSpecificKnowledgeFactory;
    }
    //</editor-fold>

    @Override @NotNull public ProviderCompactDTO fetch(@NotNull ProviderId key) throws Throwable
    {
        // Just have the list cache download them all
        providerListCache.get().getOrFetchSync(new ProviderListKey(ProviderListKey.ALL_PROVIDERS));
        // By then, the list cache has updated this cache
        ProviderCompactDTO value = get(key);
        if (value == null)
        {
            throw new NullPointerException("Unavailable ProviderCompactDTO.id=" + key.key);
        }
        return value;
    }

    @Override @Nullable public ProviderCompactDTO put(@NotNull ProviderId key, @NotNull ProviderCompactDTO value)
    {
        warrantSpecificKnowledgeFactory.add(value);
        return super.put(key, value);
    }

    @NotNull @Override protected ProviderCompactCutDTO cutValue(@NotNull ProviderId key, @NotNull ProviderCompactDTO value)
    {
        return new ProviderCompactCutDTO(value, portfolioCompactCache.get());
    }

    @Nullable @Override protected ProviderCompactDTO inflateValue(@NotNull ProviderId key, @Nullable ProviderCompactCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.create(portfolioCompactCache.get());
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public ProviderCompactDTOList get(@Nullable List<ProviderId> providerIds)
    {
        if (providerIds == null)
        {
            return null;
        }

        ProviderCompactDTOList fleshedValues = new ProviderCompactDTOList();

        for (@NotNull ProviderId providerId: providerIds)
        {
            fleshedValues.add(get(providerId));
        }

        return fleshedValues;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<ProviderCompactDTO> put(@Nullable List<? extends ProviderCompactDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<ProviderCompactDTO> previousValues = new ArrayList<>();

        for (@NotNull ProviderCompactDTO providerCompactDTO: values)
        {
            previousValues.add(put(providerCompactDTO.getProviderId(), providerCompactDTO));
        }

        return previousValues;
    }
}
