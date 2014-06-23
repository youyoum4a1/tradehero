package com.tradehero.th.persistence.competition;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.security.WarrantSpecificKnowledgeFactory;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class ProviderCache extends StraightDTOCache<ProviderId, ProviderDTO>
{
    public static final int DEFAULT_MAX_SIZE = 1000;

    @NotNull private final Lazy<ProviderListCache> providerListCache;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory;

    //<editor-fold desc="Constructors">
    @Inject public ProviderCache(
            @NotNull Lazy<ProviderListCache> providerListCache,
            @NotNull CurrentUserId currentUserId,
            @NotNull WarrantSpecificKnowledgeFactory warrantSpecificKnowledgeFactory)
    {
        super(DEFAULT_MAX_SIZE);
        this.providerListCache = providerListCache;
        this.currentUserId = currentUserId;
        this.warrantSpecificKnowledgeFactory = warrantSpecificKnowledgeFactory;
    }
    //</editor-fold>

    @Override protected ProviderDTO fetch(@NotNull ProviderId key) throws Throwable
    {
        // Just have the list cache download them all
        providerListCache.get().fetch(new ProviderListKey(ProviderListKey.ALL_PROVIDERS));
        // By then, the list cache has updated this cache
        return get(key);
    }

    @Override @Nullable public ProviderDTO put(@NotNull ProviderId key, @NotNull ProviderDTO value)
    {
        OwnedPortfolioId associatedPortfolioId = value.getAssociatedOwnedPortfolioId(currentUserId.toUserBaseKey());
        if (associatedPortfolioId != null)
        {
            warrantSpecificKnowledgeFactory.add(key, associatedPortfolioId);
        }
        return super.put(key, value);
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<ProviderDTO> getOrFetch(@Nullable List<ProviderId> providerIds) throws Throwable
    {
        if (providerIds == null)
        {
            return null;
        }

        List<ProviderDTO> providerDTOList = new ArrayList<>();
        for (@NotNull ProviderId providerId : providerIds)
        {
            providerDTOList.add(getOrFetch(providerId, false));
        }
        return providerDTOList;
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
    public List<ProviderDTO> put(@Nullable List<ProviderDTO> values)
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
