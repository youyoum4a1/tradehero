package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.competition.key.SearchProviderSecurityListType;
import com.tradehero.th.api.competition.key.WarrantProviderSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.provider.DTOProcessorReceivedProviderDTOList;
import com.tradehero.th.api.competition.specific.ProviderSpecificKnowledgeFactory;
import com.tradehero.th.api.competition.specific.ProviderSpecificResourcesFactory;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class ProviderServiceWrapper
{
    @NotNull private final ProviderService providerService;
    @NotNull private final ProviderServiceAsync providerServiceAsync;
    @NotNull private final ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory;
    @NotNull private final ProviderSpecificResourcesFactory providerSpecificResourcesFactory;

    //<editor-fold desc="Constructors">
    @Inject public ProviderServiceWrapper(
            @NotNull ProviderService providerService,
            @NotNull ProviderServiceAsync providerServiceAsync,
            @NotNull ProviderSpecificKnowledgeFactory providerSpecificKnowledgeFactory,
            @NotNull ProviderSpecificResourcesFactory providerSpecificResourcesFactory)
    {
        super();
        this.providerService = providerService;
        this.providerServiceAsync = providerServiceAsync;
        this.providerSpecificKnowledgeFactory = providerSpecificKnowledgeFactory;
        this.providerSpecificResourcesFactory = providerSpecificResourcesFactory;
    }
    //</editor-fold>

    //<editor-fold desc="Get Providers">
    protected DTOProcessor<ProviderDTOList> createDTOProcessorReceivedProviderDTOList()
    {
        return new DTOProcessorReceivedProviderDTOList(
                providerSpecificKnowledgeFactory,
                providerSpecificResourcesFactory);
    }

    public ProviderDTOList getProviders()
    {
        return createDTOProcessorReceivedProviderDTOList().process(this.providerService.getProviders());
    }

    @NotNull public MiddleCallback<ProviderDTOList> getProviders(@Nullable Callback<ProviderDTOList> callback)
    {
        MiddleCallback<ProviderDTOList> middleCallback = new BaseMiddleCallback<>(callback, createDTOProcessorReceivedProviderDTOList());
        this.providerServiceAsync.getProviders(middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Provider Securities">
    public SecurityCompactDTOList getProviderSecurities(@NotNull ProviderSecurityListType key)
    {
        SecurityCompactDTOList received;
        if (key instanceof SearchProviderSecurityListType)
        {
            SearchProviderSecurityListType searchKey = (SearchProviderSecurityListType) key;
            received = this.providerService.searchSecurities(
                    searchKey.providerId.key,
                    searchKey.searchString,
                    searchKey.getPage(),
                    searchKey.perPage);
        }
        else if (key instanceof BasicProviderSecurityListType)
        {
            received = this.providerService.getSecurities(
                    key.getProviderId().key,
                    key.getPage(),
                    key.perPage);
        }
        else if (key instanceof WarrantProviderSecurityListType)
        {
            received = this.providerService.getWarrantUnderlyers(
                    key.getProviderId().key,
                    key.getPage(),
                    key.perPage);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
        }
        return received;
    }

    @NotNull public MiddleCallback<SecurityCompactDTOList> getProviderSecurities(
            @NotNull ProviderSecurityListType key,
            @Nullable Callback<SecurityCompactDTOList> callback)
    {
        MiddleCallback<SecurityCompactDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        if (key instanceof SearchProviderSecurityListType)
        {
            SearchProviderSecurityListType searchKey = (SearchProviderSecurityListType) key;
            this.providerServiceAsync.searchSecurities(
                    searchKey.providerId.key,
                    searchKey.searchString,
                    searchKey.getPage(),
                    searchKey.perPage,
                    middleCallback);
        }
        else if (key instanceof BasicProviderSecurityListType)
        {
            this.providerServiceAsync.getSecurities(
                    key.getProviderId().key,
                    key.getPage(),
                    key.perPage,
                    middleCallback);
        }
        else if (key instanceof WarrantProviderSecurityListType)
        {
            this.providerServiceAsync.getWarrantUnderlyers(
                    key.getProviderId().key,
                    key.getPage(),
                    key.perPage,
                    middleCallback);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Help Videos">
    public List<HelpVideoDTO> getHelpVideos(@NotNull HelpVideoListKey helpVideoListKey)
    {
        return this.getHelpVideos(helpVideoListKey.getProviderId());
    }

    @NotNull public MiddleCallback<List<HelpVideoDTO>> getHelpVideos(
            @NotNull HelpVideoListKey helpVideoListKey,
            @Nullable Callback<List<HelpVideoDTO>> callback)
    {
        return this.getHelpVideos(helpVideoListKey.getProviderId(), callback);
    }

    public List<HelpVideoDTO> getHelpVideos(@NotNull ProviderId providerId)
    {
        return this.providerService.getHelpVideos(providerId.key);
    }

    @NotNull public MiddleCallback<List<HelpVideoDTO>> getHelpVideos(
            @NotNull ProviderId providerId,
            @Nullable Callback<List<HelpVideoDTO>> callback)
    {
        MiddleCallback<List<HelpVideoDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        this.providerServiceAsync.getHelpVideos(providerId.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
