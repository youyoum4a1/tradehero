package com.tradehero.th.network.service;

import com.squareup.okhttp.Call;
import com.tradehero.th.api.portfolio.*;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioListReceived;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioReceived;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import retrofit.Callback;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class PortfolioServiceWrapper
{
    @NotNull private final PortfolioService portfolioService;
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @NotNull private final Lazy<PortfolioCompactCache> portfolioCompactCache;
    @NotNull private final Lazy<PortfolioCache> portfolioCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioServiceWrapper(
            @NotNull PortfolioService portfolioService,
            @NotNull UserProfileCache userProfileCache,
            @NotNull Lazy<PortfolioCompactListCache> portfolioCompactListCache,
            @NotNull Lazy<PortfolioCompactCache> portfolioCompactCache,
            @NotNull Lazy<PortfolioCache> portfolioCache)
    {
        super();
        this.portfolioService = portfolioService;
        this.userProfileCache = userProfileCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get User Portfolio List">
    protected DTOProcessor<PortfolioCompactDTOList> createPortfolioCompactListReceivedProcessor(@NotNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorPortfolioListReceived<>(userBaseKey);
    }

    @NotNull public PortfolioCompactDTOList getPortfolios(
            @NotNull UserBaseKey userBaseKey,
            @Nullable Boolean includeWatchList)
    {
        return createPortfolioCompactListReceivedProcessor(userBaseKey).process(
                portfolioService.getPortfolios(userBaseKey.key, includeWatchList));
    }

    //<editor-fold desc="Get One User Portfolio">
    protected DTOProcessor<PortfolioDTO> createPortfolioReceivedProcessor(@NotNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorPortfolioReceived<>(userBaseKey);
    }

    @NotNull public PortfolioDTO getPortfolio(
            @NotNull OwnedPortfolioId ownedPortfolioId)
    {
        return createPortfolioReceivedProcessor(ownedPortfolioId.getUserBaseKey()).process(
                this.portfolioService.getPortfolio(
                        ownedPortfolioId.userId,
                        ownedPortfolioId.portfolioId));
    }

    @NotNull public PortfolioCompactDTO getPortfolioCompact(
            @NotNull PortfolioId key)
    {
        return portfolioService.getPortfolioCompact(key.competitionId);
    }

   public void getMainPortfolio(int userId, Callback<PortfolioDTO> callback){
       portfolioService.getMainPortfolio(userId, callback);
   }

   public void getCompetitionPortfolio(int portfolioId, Callback<PortfolioDTO> callback){
       portfolioService.getCompetitionPortfolio(portfolioId, callback);
   }
}
