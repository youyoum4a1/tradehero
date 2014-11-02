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
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
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

    public Observable<SecurityCompactDTOList> getProviderSecuritiesRx(@NotNull ProviderSecurityListType key)
    {
        Observable<SecurityCompactDTOList> received;
        if (key instanceof SearchProviderSecurityListType)
        {
            SearchProviderSecurityListType searchKey = (SearchProviderSecurityListType) key;
            received = this.providerServiceRx.searchSecurities(
                    searchKey.providerId.key,
                    searchKey.searchString,
                    searchKey.getPage(),
                    searchKey.perPage);
        }
        else if (key instanceof BasicProviderSecurityListType)
        {
            received = this.providerServiceRx.getSecurities(
                    key.getProviderId().key,
                    key.getPage(),
                    key.perPage);
        }
        else if (key instanceof WarrantProviderSecurityListType)
        {
            received = this.providerServiceRx.getWarrantUnderlyers(
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
    //</editor-fold>

    //<editor-fold desc="Get Help Videos">
    @NotNull public Observable<HelpVideoDTOList> getHelpVideosRx(@NotNull HelpVideoListKey helpVideoListKey)
    {
        return this.getHelpVideosRx(helpVideoListKey.getProviderId());
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

    public Observable<ProviderDisplayCellDTOList> getDisplayCellsRx(@NotNull ProviderDisplayCellListKey providerDisplayCellListKey)
    {
        return this.getDisplayCellsRx(providerDisplayCellListKey.getProviderId());
    }

    public ProviderDisplayCellDTOList getDisplayCells(@NotNull ProviderId providerId)
    {
        return this.providerService.getDisplayCells(providerId.key);
    }

    public Observable<ProviderDisplayCellDTOList> getDisplayCellsRx(@NotNull ProviderId providerId)
    {
        return this.providerServiceRx.getDisplayCells(providerId.key);
    }
    //</editor-fold>
}
