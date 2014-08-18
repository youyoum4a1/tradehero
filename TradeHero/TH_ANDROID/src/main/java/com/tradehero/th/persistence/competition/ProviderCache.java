package com.tradehero.th.persistence.competition;

import com.android.internal.util.Predicate;
import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
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

@Singleton public class ProviderCache extends StraightCutDTOCacheNew<ProviderId, ProviderDTO, ProviderCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final Lazy<ProviderListCache> providerListCache;
    @NotNull private final Lazy<PortfolioCompactCache> portfolioCompactCache;
    @NotNull private final WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory;

    //<editor-fold desc="Constructors">
    @Inject public ProviderCache(
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

    @Override @NotNull public ProviderDTO fetch(@NotNull final ProviderId key) throws Throwable
    {
        // Just have the list cache download them all
        ProviderDTO value = providerListCache.get().getOrFetchSync(
                new ProviderListKey(ProviderListKey.ALL_PROVIDERS))
                .findFirstWhere(new Predicate<ProviderDTO>()
        {
            @Override public boolean apply(ProviderDTO providerDTO)
            {
                return key.key.equals(providerDTO.id);
            }
        });
        if (value == null)
        {
            throw new NullPointerException("Unavailable ProviderDTO.id=" + key.key);
        }
        return value;
    }

    @Override @Nullable public ProviderDTO put(@NotNull ProviderId key, @NotNull ProviderDTO value)
    {
        warrantSpecificKnowledgeFactory.add(value);
        return super.put(key, value);
    }

    @NotNull @Override protected ProviderCutDTO cutValue(@NotNull ProviderId key, @NotNull ProviderDTO value)
    {
        return new ProviderCutDTO(value, portfolioCompactCache.get());
    }

    @Nullable @Override protected ProviderDTO inflateValue(@NotNull ProviderId key, @Nullable ProviderCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.create(portfolioCompactCache.get());
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public ProviderDTOList get(@Nullable List<ProviderId> providerIds)
    {
        if (providerIds == null)
        {
            return null;
        }

        ProviderDTOList fleshedValues = new ProviderDTOList();

        for (@NotNull ProviderId providerId: providerIds)
        {
            fleshedValues.add(get(providerId));
        }

        return fleshedValues;
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<ProviderDTO> put(@Nullable List<? extends ProviderDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        List<ProviderDTO> previousValues = new ArrayList<>();

        for (@NotNull ProviderDTO providerDTO: values)
        {
            previousValues.add(put(providerDTO.getProviderId(), providerDTO));
        }

        return previousValues;
    }
}
