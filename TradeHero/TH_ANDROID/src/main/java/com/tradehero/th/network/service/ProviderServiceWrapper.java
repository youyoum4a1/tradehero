package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.competition.CompetitionPreSeasonDTO;
import com.ayondo.academy.api.competition.CompetitionPreseasonShareFormDTO;
import com.ayondo.academy.api.competition.HelpVideoDTOList;
import com.ayondo.academy.api.competition.ProviderDTO;
import com.ayondo.academy.api.competition.ProviderDTOList;
import com.ayondo.academy.api.competition.ProviderDisplayCellDTOList;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.competition.ProviderPrizePoolDTO;
import com.ayondo.academy.api.competition.key.BasicProviderSecurityListType;
import com.ayondo.academy.api.competition.key.HelpVideoListKey;
import com.ayondo.academy.api.competition.key.ProviderDisplayCellListKey;
import com.ayondo.academy.api.competition.key.ProviderSecurityListType;
import com.ayondo.academy.api.competition.key.SearchProviderSecurityListType;
import com.ayondo.academy.api.competition.key.WarrantProviderSecurityListType;
import com.ayondo.academy.api.competition.key.WarrantUnderlyersProviderSecurityListType;
import com.ayondo.academy.api.portfolio.PortfolioDTO;
import com.ayondo.academy.api.security.SecurityCompactDTOList;
import com.ayondo.academy.api.security.WarrantType;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.models.BaseDTOListProcessor;
import com.ayondo.academy.models.portfolio.DTOProcessorPortfolioReceived;
import com.ayondo.academy.models.provider.DTOProcessorProviderReceived;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class ProviderServiceWrapper
{
    @NonNull private final ProviderServiceRx providerServiceRx;
    @NonNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public ProviderServiceWrapper(
            @NonNull ProviderServiceRx providerServiceRx,
            @NonNull CurrentUserId currentUserId)
    {
        super();
        this.providerServiceRx = providerServiceRx;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    //<editor-fold desc="Get Providers">
    @NonNull public Observable<ProviderDTOList> getProvidersRx()
    {
        return this.providerServiceRx.getProviders()
                .map(new BaseDTOListProcessor<ProviderDTO, ProviderDTOList>(
                        new DTOProcessorProviderReceived(currentUserId)));
    }
    //</editor-fold>

    //<editor-fold desc="Get Provider">
    @NonNull public Observable<ProviderPrizePoolDTO> getProviderPrizePoolRx(@NonNull ProviderId providerId)
    {
        return this.providerServiceRx.getProviderPrizePool(providerId.key);
    }

    @NonNull public Observable<ProviderDTO> getProviderRx(@NonNull ProviderId providerId)
    {
        return this.providerServiceRx.getProvider(providerId.key)
                .map(new DTOProcessorProviderReceived(currentUserId));
    }
    //</editor-fold>

    //<editor-fold desc="Get Provider Portfolio">
    @NonNull public Observable<PortfolioDTO> getPortfolio(@NonNull ProviderId providerId)
    {
        return this.providerServiceRx.getPortfolio(providerId.key)
                .map(new DTOProcessorPortfolioReceived<PortfolioDTO>(currentUserId.toUserBaseKey()));
    }
    //</editor-fold>

    //<editor-fold desc="Get Provider Securities">
    @NonNull public Observable<SecurityCompactDTOList> getProviderSecuritiesRx(@NonNull ProviderSecurityListType key)
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
                    key.providerId.key,
                    key.getPage(),
                    key.perPage);
        }
        else if (key instanceof WarrantUnderlyersProviderSecurityListType)
        {
            received = this.providerServiceRx.getWarrantUnderlyers(
                    key.providerId.key,
                    key.getPage(),
                    key.perPage);
        }
        else if (key instanceof WarrantProviderSecurityListType)
        {
            WarrantType warrantType = ((WarrantProviderSecurityListType) key).warrantType;
            received = this.providerServiceRx.getProviderWarrants(
                    key.providerId.key,
                    key.getPage(),
                    key.perPage,
                    warrantType != null ? warrantType.shortCode : null);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
        }
        return received;
    }
    //</editor-fold>

    //<editor-fold desc="Get Help Videos">
    @NonNull public Observable<HelpVideoDTOList> getHelpVideosRx(@NonNull HelpVideoListKey helpVideoListKey)
    {
        return this.getHelpVideosRx(helpVideoListKey.getProviderId());
    }

    @NonNull public Observable<HelpVideoDTOList> getHelpVideosRx(@NonNull ProviderId providerId)
    {
        return this.providerServiceRx.getHelpVideos(providerId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Display Cells">
    @NonNull public Observable<ProviderDisplayCellDTOList> getDisplayCellsRx(@NonNull ProviderDisplayCellListKey providerDisplayCellListKey)
    {
        return this.getDisplayCellsRx(providerDisplayCellListKey.getProviderId());
    }

    @NonNull public Observable<ProviderDisplayCellDTOList> getDisplayCellsRx(@NonNull ProviderId providerId)
    {
        return this.providerServiceRx.getDisplayCells(providerId.key);
    }

    @NonNull public Observable<CompetitionPreSeasonDTO> getPreseasonDetails(@NonNull ProviderId providerId)
    {
        return this.providerServiceRx.getPreseasonDetails(providerId.key);
    }

    @NonNull public Observable<BaseResponseDTO> sharePreSeason(@NonNull CompetitionPreseasonShareFormDTO competitionPreseasonShareFormDTO)
    {
        return this.providerServiceRx.sharePreseason(competitionPreseasonShareFormDTO);
    }
    //</editor-fold>
}
