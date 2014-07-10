package com.tradehero.th.network.service;

import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioListReceived;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioMarked;
import com.tradehero.th.models.portfolio.DTOProcessorPortfolioReceived;
import com.tradehero.th.models.user.DTOProcessorAddCash;
import com.tradehero.th.models.user.DTOProcessorUpdateUserProfile;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

@Singleton public class PortfolioServiceWrapper
{
    @NotNull private final PortfolioService portfolioService;
    @NotNull private final PortfolioServiceAsync portfolioServiceAsync;
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @NotNull private final Lazy<PortfolioCompactCache> portfolioCompactCache;
    @NotNull private final Lazy<PortfolioCache> portfolioCache;

    //<editor-fold desc="Constructors">
    @Inject public PortfolioServiceWrapper(
            @NotNull PortfolioService portfolioService,
            @NotNull PortfolioServiceAsync portfolioServiceAsync,
            @NotNull UserProfileCache userProfileCache,
            @NotNull Lazy<PortfolioCompactListCache> portfolioCompactListCache,
            @NotNull Lazy<PortfolioCompactCache> portfolioCompactCache,
            @NotNull Lazy<PortfolioCache> portfolioCache)
    {
        super();
        this.portfolioService = portfolioService;
        this.portfolioServiceAsync = portfolioServiceAsync;
        this.userProfileCache = userProfileCache;
        this.portfolioCompactListCache = portfolioCompactListCache;
        this.portfolioCompactCache = portfolioCompactCache;
        this.portfolioCache = portfolioCache;
    }
    //</editor-fold>

    //<editor-fold desc="DTO Processors">
    protected DTOProcessor<PortfolioCompactDTO> createPortfolioCompactReceivedProcessor(@NotNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorPortfolioReceived<>(userBaseKey);
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

    @NotNull public MiddleCallback<PortfolioCompactDTOList> getPortfolios(
            @NotNull UserBaseKey userBaseKey,
            @Nullable Boolean includeWatchList,
            @Nullable Callback<PortfolioCompactDTOList> callback)
    {
        MiddleCallback<PortfolioCompactDTOList> middleCallback = new BaseMiddleCallback<>(
                callback,
                createPortfolioCompactListReceivedProcessor(userBaseKey));
        portfolioServiceAsync.getPortfolios(userBaseKey.key, includeWatchList, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

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

    @NotNull public BaseMiddleCallback<PortfolioDTO> getPortfolio(
            @NotNull OwnedPortfolioId ownedPortfolioId,
            @Nullable Callback<PortfolioDTO> callback)
    {
        BaseMiddleCallback<PortfolioDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createPortfolioReceivedProcessor(ownedPortfolioId.getUserBaseKey()));
        this.portfolioServiceAsync.getPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Reset Cash">
    protected DTOProcessor<UserProfileDTO> createUpdateProfileProcessor()
    {
        return new DTOProcessorUpdateUserProfile(userProfileCache);
    }

    @NotNull public UserProfileDTO resetPortfolio(
            @NotNull OwnedPortfolioId ownedPortfolioId,
            GooglePlayPurchaseDTO purchaseDTO)
    {
        return createUpdateProfileProcessor().process(this.portfolioService.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO));
    }

    @NotNull public MiddleCallback<UserProfileDTO> resetPortfolio(
            @NotNull OwnedPortfolioId ownedPortfolioId,
            GooglePlayPurchaseDTO purchaseDTO,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createUpdateProfileProcessor());
        this.portfolioServiceAsync.resetPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Add Cash">
    protected DTOProcessor<UserProfileDTO> createAddCashProcessor(OwnedPortfolioId ownedPortfolioId)
    {
        return new DTOProcessorAddCash(userProfileCache,
                portfolioCompactListCache.get(),
                portfolioCompactCache.get(),
                portfolioCache.get(),
                ownedPortfolioId);
    }

    @NotNull public UserProfileDTO addCash(
            @NotNull OwnedPortfolioId ownedPortfolioId,
            GooglePlayPurchaseDTO purchaseDTO)
    {
        return createAddCashProcessor(ownedPortfolioId).process(this.portfolioService.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO));
    }

    @NotNull public MiddleCallback<UserProfileDTO> addCash(
            @NotNull OwnedPortfolioId ownedPortfolioId,
            GooglePlayPurchaseDTO purchaseDTO,
            @Nullable Callback<UserProfileDTO> callback)
    {
        MiddleCallback<UserProfileDTO> middleCallback = new BaseMiddleCallback<>(callback, createAddCashProcessor(ownedPortfolioId));
        this.portfolioServiceAsync.addCash(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, purchaseDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Mark One User Portfolio">
    protected DTOProcessor<PortfolioDTO> createPortfolioMarkedProcessor(@NotNull UserBaseKey userBaseKey)
    {
        return new DTOProcessorPortfolioMarked(userBaseKey, portfolioCache.get());
    }

    @NotNull public PortfolioDTO markPortfolio(
            @NotNull OwnedPortfolioId ownedPortfolioId)
    {
        return createPortfolioMarkedProcessor(ownedPortfolioId.getUserBaseKey()).process(
                this.portfolioService.markPortfolio(
                        ownedPortfolioId.userId,
                        ownedPortfolioId.portfolioId));
    }

    @NotNull public BaseMiddleCallback<PortfolioDTO> markPortfolio(
            @NotNull OwnedPortfolioId ownedPortfolioId,
            @Nullable Callback<PortfolioDTO> callback)
    {
        BaseMiddleCallback<PortfolioDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createPortfolioMarkedProcessor(ownedPortfolioId.getUserBaseKey()));
        this.portfolioServiceAsync.markPortfolio(ownedPortfolioId.userId, ownedPortfolioId.portfolioId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
