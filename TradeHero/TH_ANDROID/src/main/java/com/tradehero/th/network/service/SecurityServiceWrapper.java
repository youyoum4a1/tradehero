package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerIdList;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingAllSecurityListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.key.TrendingPriceSecurityListType;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.api.security.key.TrendingVolumeSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.security.DTOProcessorMultiSecurities;
import com.tradehero.th.models.security.DTOProcessorSecurityPositionDetailReceived;
import com.tradehero.th.models.security.DTOProcessorSecurityPositionTransactionUpdated;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.position.SecurityPositionDetailCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import rx.Observable;
import rx.functions.Func1;

@Singleton public class SecurityServiceWrapper
{
    @NotNull private final SecurityService securityService;
    @NotNull private final SecurityServiceAsync securityServiceAsync;
    @NotNull private final SecurityServiceRx securityServiceRx;
    @NotNull private final ProviderServiceWrapper providerServiceWrapper;
    @NotNull private final SecurityCompactCacheRx securityCompactCache;
    @NotNull private final SecurityPositionDetailCacheRx securityPositionDetailCache;
    @NotNull private final PortfolioCacheRx portfolioCache;
    @NotNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public SecurityServiceWrapper(
            @NotNull SecurityService securityService,
            @NotNull SecurityServiceAsync securityServiceAsync,
            @NotNull SecurityServiceRx securityServiceRx,
            @NotNull ProviderServiceWrapper providerServiceWrapper,
            @NotNull SecurityCompactCacheRx securityCompactCache,
            @NotNull SecurityPositionDetailCacheRx securityPositionDetailCache,
            @NotNull PortfolioCacheRx portfolioCache,
            @NotNull CurrentUserId currentUserId)
    {
        super();
        this.securityService = securityService;
        this.securityServiceAsync = securityServiceAsync;
        this.securityServiceRx = securityServiceRx;
        this.providerServiceWrapper = providerServiceWrapper;
        this.securityCompactCache = securityCompactCache;
        this.securityPositionDetailCache = securityPositionDetailCache;
        this.portfolioCache = portfolioCache;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    //<editor-fold desc="Get Multiple Securities">
    @NotNull private DTOProcessorMultiSecurities createMultipleSecurityProcessor()
    {
        return new DTOProcessorMultiSecurities(securityCompactCache);
    }

    public Observable<Map<Integer, SecurityCompactDTO>> getMultipleSecuritiesRx(@NotNull SecurityIntegerIdList ids)
    {
        return securityServiceRx.getMultipleSecurities(ids.getCommaSeparated())
                .doOnNext(createMultipleSecurityProcessor());
    }
    //</editor-fold>

    //<editor-fold desc="Get Securities">
    @Deprecated
    public SecurityCompactDTOList getSecurities(@NotNull SecurityListType key)
    {
        SecurityCompactDTOList received;
        if (key instanceof TrendingSecurityListType)
        {
            TrendingSecurityListType trendingKey = (TrendingSecurityListType) key;
            if (trendingKey instanceof TrendingBasicSecurityListType)
            {
                received = this.securityService.getTrendingSecurities(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingPriceSecurityListType)
            {
                received =  this.securityService.getTrendingSecuritiesByPrice(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingVolumeSecurityListType)
            {
                received =  this.securityService.getTrendingSecuritiesByVolume(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingAllSecurityListType)
            {
                received =  this.securityService.getTrendingSecuritiesAllInExchange(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled type " + ((Object) trendingKey).getClass().getName());
            }
        }
        else if (key instanceof SearchSecurityListType)
        {
            SearchSecurityListType searchKey = (SearchSecurityListType) key;
            received =  this.securityService.searchSecurities(
                    searchKey.searchString,
                    searchKey.getPage(),
                    searchKey.perPage);
        }
        else if (key instanceof ProviderSecurityListType)
        {
            received =  providerServiceWrapper.getProviderSecurities((ProviderSecurityListType) key);
        }
        else if (key instanceof ExchangeSectorSecurityListType)
        {
            ExchangeSectorSecurityListType exchangeKey = (ExchangeSectorSecurityListType) key;
            received = this.securityService.getBySectorAndExchange(
                    exchangeKey.exchangeId == null ? null: exchangeKey.exchangeId.key,
                    exchangeKey.sectorId == null ? null : exchangeKey.sectorId.key,
                    key.page,
                    key.perPage);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
        }
        return received;
    }

    public Observable<SecurityCompactDTOList> getSecuritiesRx(@NotNull SecurityListType key)
    {
        Observable<SecurityCompactDTOList> received;
        if (key instanceof TrendingSecurityListType)
        {
            TrendingSecurityListType trendingKey = (TrendingSecurityListType) key;
            if (trendingKey instanceof TrendingBasicSecurityListType)
            {
                received = this.securityServiceRx.getTrendingSecurities(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingPriceSecurityListType)
            {
                received =  this.securityServiceRx.getTrendingSecuritiesByPrice(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingVolumeSecurityListType)
            {
                received =  this.securityServiceRx.getTrendingSecuritiesByVolume(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingAllSecurityListType)
            {
                received =  this.securityServiceRx.getTrendingSecuritiesAllInExchange(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled type " + ((Object) trendingKey).getClass().getName());
            }
        }
        else if (key instanceof SearchSecurityListType)
        {
            SearchSecurityListType searchKey = (SearchSecurityListType) key;
            received =  this.securityServiceRx.searchSecurities(
                    searchKey.searchString,
                    searchKey.getPage(),
                    searchKey.perPage);
        }
        else if (key instanceof ProviderSecurityListType)
        {
            received =  providerServiceWrapper.getProviderSecuritiesRx((ProviderSecurityListType) key);
        }
        else if (key instanceof ExchangeSectorSecurityListType)
        {
            ExchangeSectorSecurityListType exchangeKey = (ExchangeSectorSecurityListType) key;
            received = this.securityServiceRx.getBySectorAndExchange(
                    exchangeKey.exchangeId == null ? null: exchangeKey.exchangeId.key,
                    exchangeKey.sectorId == null ? null : exchangeKey.sectorId.key,
                    key.page,
                    key.perPage);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
        }
        return received;
    }
    //</editor-fold>

    //<editor-fold desc="Get Security">
    @NotNull protected DTOProcessor<SecurityPositionDetailDTO> createSecurityPositionDetailDTOProcessor(@NotNull SecurityId securityId)
    {
        return new DTOProcessorSecurityPositionDetailReceived(securityId, currentUserId.toUserBaseKey());
    }

    @NotNull public Observable<SecurityPositionDetailDTO> getSecurityRx(
            @NotNull SecurityId securityId)
    {
        return securityServiceRx.getSecurity(
                securityId.getExchange(),
                securityId.getPathSafeSymbol())
                .map(new Func1<SecurityPositionDetailDTO, SecurityPositionDetailDTO>()
                {
                    @Override public SecurityPositionDetailDTO call(SecurityPositionDetailDTO securityPositionDetailDTO)
                    {
                        if (securityPositionDetailDTO.providers != null)
                        {
                            for (@NotNull ProviderDTO providerDTO : securityPositionDetailDTO.providers)
                            {
                                if (providerDTO.associatedPortfolio != null)
                                {
                                    providerDTO.associatedPortfolio.userId = currentUserId.get();
                                }
                            }
                        }

                        return securityPositionDetailDTO;
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Buy Security">
    @NotNull private DTOProcessorSecurityPositionTransactionUpdated createSecurityPositionTransactionUpdatedProcessor(@NotNull SecurityId securityId)
    {
        return new DTOProcessorSecurityPositionTransactionUpdated(
                securityId,
                currentUserId.toUserBaseKey(),
                securityPositionDetailCache,
                portfolioCache);
    }

    @Deprecated
    public SecurityPositionTransactionDTO buy(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO)
    {
        return createSecurityPositionTransactionUpdatedProcessor(securityId).process(
                this.securityService.buy(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO));
    }

    @Deprecated
    @NotNull public MiddleCallback<SecurityPositionTransactionDTO> buy(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO,
            @Nullable Callback<SecurityPositionTransactionDTO> callback)
    {
        MiddleCallback<SecurityPositionTransactionDTO> middleCallback = new BaseMiddleCallback<>(callback, createSecurityPositionTransactionUpdatedProcessor(
                securityId));
        this.securityServiceAsync.buy(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO, middleCallback);
        return middleCallback;
    }

    public Observable<SecurityPositionTransactionDTO> buyRx(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO)
    {
        return this.securityServiceRx.buy(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO)
            .doOnNext(createSecurityPositionTransactionUpdatedProcessor(securityId));
    }
    //</editor-fold>

    //<editor-fold desc="Sell Security">
    @Deprecated
    public SecurityPositionTransactionDTO sell(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO)
    {
        return createSecurityPositionTransactionUpdatedProcessor(securityId).process(
                this.securityService.sell(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO));
    }

    @Deprecated
    @NotNull public MiddleCallback<SecurityPositionTransactionDTO> sell(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO,
            @Nullable Callback<SecurityPositionTransactionDTO> callback)
    {
        MiddleCallback<SecurityPositionTransactionDTO> middleCallback = new BaseMiddleCallback<>(callback, createSecurityPositionTransactionUpdatedProcessor(
                securityId));
        this.securityServiceAsync.sell(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO, middleCallback);
        return middleCallback;
    }

    @NotNull public Observable<SecurityPositionTransactionDTO> sellRx(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO)
    {
        return this.securityServiceRx.sell(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO)
                .doOnNext(createSecurityPositionTransactionUpdatedProcessor(securityId));
    }
    //</editor-fold>

    //<editor-fold desc="Buy or Sell Security">
    public Observable<SecurityPositionTransactionDTO> doTransactionRx(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO,
            boolean isBuy)
    {
        if (isBuy)
        {
            return buyRx(securityId, transactionFormDTO);
        }
        return sellRx(securityId, transactionFormDTO);
    }

    @Deprecated
    @NotNull public MiddleCallback<SecurityPositionTransactionDTO> doTransaction(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO,
            boolean isBuy,
            @Nullable Callback<SecurityPositionTransactionDTO> callback)
    {
        if (isBuy)
        {
            return buy(securityId, transactionFormDTO, callback);
        }
        return sell(securityId, transactionFormDTO, callback);
    }
    //</editor-fold>
}
