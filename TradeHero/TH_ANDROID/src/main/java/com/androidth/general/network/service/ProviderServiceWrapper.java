package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import android.util.Pair;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.competition.CompetitionPreSeasonDTO;
import com.androidth.general.api.competition.CompetitionPreseasonShareFormDTO;
import com.androidth.general.api.competition.HelpVideoDTOList;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderDTOList;
import com.androidth.general.api.competition.ProviderDisplayCellDTOList;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.ProviderPrizePoolDTO;
import com.androidth.general.api.competition.key.BasicProviderSecurityListType;
import com.androidth.general.api.competition.key.BasicProviderSecurityV2ListType;
import com.androidth.general.api.competition.key.HelpVideoListKey;
import com.androidth.general.api.competition.key.ProviderDisplayCellListKey;
import com.androidth.general.api.competition.key.ProviderSecurityListType;
import com.androidth.general.api.competition.key.SearchProviderSecurityListType;
import com.androidth.general.api.competition.key.WarrantProviderSecurityListType;
import com.androidth.general.api.competition.key.WarrantUnderlyersProviderSecurityListType;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.security.SecurityCompactDTOList;
import com.androidth.general.api.security.SecurityCompositeDTO;
import com.androidth.general.api.security.WarrantType;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.models.BaseDTOListProcessor;
import com.androidth.general.models.portfolio.DTOProcessorPortfolioReceived;
import com.androidth.general.models.provider.DTOProcessorProviderReceived;
import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.tencent.mm.sdk.platformtools.Log;

import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Action1;

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

    //<editor-fold desc="Get Provider Securities">
    @NonNull public Observable<SecurityCompositeDTO> getProviderSecuritiesV2Rx(@NonNull BasicProviderSecurityV2ListType key)
    {
        Observable<SecurityCompositeDTO> received = this.providerServiceRx.getSecuritiesV2(
                    key.providerId.key);

        received.doOnNext(new Action1<SecurityCompositeDTO>() {
            @Override
            public void call(SecurityCompositeDTO securityCompositeDTO) {
                Log.d("getSecuritiesV2", "Onnext "+securityCompositeDTO.toString());
            }
        }).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.d("getSecuritiesV2", "onError "+throwable.getLocalizedMessage());
            }
        }).subscribe(new Action1<SecurityCompositeDTO>()
        {
            @Override public void call(SecurityCompositeDTO result)
            {
                Log.d("getSecuritiesV2", "onsubscribe"+result.toString());
            }
        });

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
