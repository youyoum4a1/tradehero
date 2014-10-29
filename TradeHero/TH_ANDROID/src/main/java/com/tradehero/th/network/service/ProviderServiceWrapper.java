package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.HelpVideoDTOList;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderDisplayCellDTOList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.key.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.api.competition.key.ProviderDisplayCellListKey;
import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.competition.key.SearchProviderSecurityListType;
import com.tradehero.th.api.competition.key.WarrantProviderSecurityListType;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.provider.DTOProcessorProviderCompactListReceived;
import com.tradehero.th.models.provider.DTOProcessorProviderCompactReceived;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton public class ProviderServiceWrapper
{
    @NotNull private final ProviderService providerService;
    @NotNull private final ProviderServiceAsync providerServiceAsync;
    @NotNull private final ProviderServiceRx providerServiceRx;
    @NotNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public ProviderServiceWrapper(
            @NotNull ProviderService providerService,
            @NotNull ProviderServiceAsync providerServiceAsync,
            @NotNull ProviderServiceRx providerServiceRx,
            @NotNull CurrentUserId currentUserId)
    {
        super();
        this.providerService = providerService;
        this.providerServiceAsync = providerServiceAsync;
        this.providerServiceRx = providerServiceRx;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    //<editor-fold desc="Get Providers">
    private DTOProcessor<ProviderDTO> createProcessorProviderCompactReceived()
    {
        return new DTOProcessorProviderCompactReceived(currentUserId);
    }

    private DTOProcessor<ProviderDTOList> createProcessorProviderCompactListReceived()
    {
        return new DTOProcessorProviderCompactListReceived(createProcessorProviderCompactReceived());
    }

    @NotNull public ProviderDTOList getProviders()
    {
        return createProcessorProviderCompactListReceived().process(
                this.providerService.getProviders());
    }

    @NotNull public MiddleCallback<ProviderDTOList> getProviders(@Nullable Callback<ProviderDTOList> callback)
    {
        MiddleCallback<ProviderDTOList> middleCallback = new BaseMiddleCallback<>(
                callback,
                createProcessorProviderCompactListReceived());
        this.providerServiceAsync.getProviders(middleCallback);
        return middleCallback;
    }

    @NotNull public Observable<ProviderDTOList> getProvidersRx()
    {
        return this.providerServiceRx.getProviders()
                .doOnNext(providerDTOList -> {
                    for (ProviderDTO providerDTO : providerDTOList)
                    {
                        if (providerDTO != null)
                        {
                            PortfolioCompactDTO associatedPortfolio = providerDTO.associatedPortfolio;
                            if (associatedPortfolio != null)
                            {
                                associatedPortfolio.userId = currentUserId.get();
                            }
                        }
                    }
                });
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
            throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
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
            throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Help Videos">
    public HelpVideoDTOList getHelpVideos(@NotNull HelpVideoListKey helpVideoListKey)
    {
        return this.getHelpVideos(helpVideoListKey.getProviderId());
    }

    @NotNull public MiddleCallback<HelpVideoDTOList> getHelpVideos(
            @NotNull HelpVideoListKey helpVideoListKey,
            @Nullable Callback<HelpVideoDTOList> callback)
    {
        return this.getHelpVideos(helpVideoListKey.getProviderId(), callback);
    }

    @NotNull public Observable<HelpVideoDTOList> getHelpVideosRx(@NotNull HelpVideoListKey helpVideoListKey)
    {
        return this.getHelpVideosRx(helpVideoListKey.getProviderId());
    }

    public HelpVideoDTOList getHelpVideos(@NotNull ProviderId providerId)
    {
        return this.providerService.getHelpVideos(providerId.key);
    }

    @NotNull public MiddleCallback<HelpVideoDTOList> getHelpVideos(
            @NotNull ProviderId providerId,
            @Nullable Callback<HelpVideoDTOList> callback)
    {
        MiddleCallback<HelpVideoDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        this.providerServiceAsync.getHelpVideos(providerId.key, middleCallback);
        return middleCallback;
    }

    @NotNull public Observable<HelpVideoDTOList> getHelpVideosRx(@NotNull ProviderId providerId)
    {
        return this.providerServiceRx.getHelpVideos(providerId.key);
    }
    //</editor-fold>

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
