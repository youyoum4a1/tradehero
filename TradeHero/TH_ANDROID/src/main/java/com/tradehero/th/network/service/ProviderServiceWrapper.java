package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.*;
import com.tradehero.th.api.competition.key.*;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.provider.DTOProcessorProviderCompactReceived;
import com.tradehero.th.models.provider.DTOProcessorProviderListReceived;
import com.tradehero.th.models.provider.DTOProcessorProviderReceived;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class ProviderServiceWrapper
{
    @NotNull private final ProviderService providerService;
    @NotNull private final ProviderServiceAsync providerServiceAsync;
    @NotNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public ProviderServiceWrapper(
            @NotNull ProviderService providerService,
            @NotNull ProviderServiceAsync providerServiceAsync,
            @NotNull CurrentUserId currentUserId)
    {
        super();
        this.providerService = providerService;
        this.providerServiceAsync = providerServiceAsync;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    //<editor-fold desc="Get Providers">
    private DTOProcessor<ProviderCompactDTO> createProcessorProviderCompactReceived()
    {
        return new DTOProcessorProviderCompactReceived(currentUserId);
    }

    private DTOProcessor<ProviderDTO> createProcessorProviderReceived()
    {
        return new DTOProcessorProviderReceived(createProcessorProviderCompactReceived());
    }


    private DTOProcessor<ProviderDTOList> createProcessorProviderListReceived()
    {
        return new DTOProcessorProviderListReceived(createProcessorProviderReceived());
    }

    @NotNull public ProviderDTOList getProviders()
    {
        return createProcessorProviderListReceived().process(
                this.providerService.getProviders());
    }

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
    public HelpVideoDTOList getHelpVideos(@NotNull HelpVideoListKey helpVideoListKey)
    {
        return this.getHelpVideos(helpVideoListKey.getProviderId());
    }

    public HelpVideoDTOList getHelpVideos(@NotNull ProviderId providerId)
    {
        return this.providerService.getHelpVideos(providerId.key);
    }

    //<editor-fold desc="Get Display Cells">
    public ProviderDisplayCellDTOList getDisplayCells(@NotNull ProviderDisplayCellListKey providerDisplayCellListKey)
    {
        return this.getDisplayCells(providerDisplayCellListKey.getProviderId());
    }

    @NotNull public MiddleCallback<ProviderDisplayCellDTOList> getDisplayCells(
            @NotNull ProviderDisplayCellListKey providerDisplayCellListKey,
            @Nullable Callback<ProviderDisplayCellDTOList> callback)
    {
        return this.getDisplayCells(providerDisplayCellListKey.getProviderId(), callback);
    }

    public ProviderDisplayCellDTOList getDisplayCells(@NotNull ProviderId providerId)
    {
        return this.providerService.getDisplayCells(providerId.key);
    }

    @NotNull public MiddleCallback<ProviderDisplayCellDTOList> getDisplayCells(
            @NotNull ProviderId providerId,
            @Nullable Callback<ProviderDisplayCellDTOList> callback)
    {
        MiddleCallback<ProviderDisplayCellDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        this.providerServiceAsync.getDisplayCells(providerId.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>


}
