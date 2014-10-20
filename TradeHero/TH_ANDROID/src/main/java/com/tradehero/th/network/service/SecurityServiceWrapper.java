package com.tradehero.th.network.service;

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
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
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
    @NotNull private final SecurityCompactCache securityCompactCache;
    @NotNull private final SecurityPositionDetailCache securityPositionDetailCache;
    @NotNull private final PortfolioCache portfolioCache;
    @NotNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public SecurityServiceWrapper(
            @NotNull SecurityService securityService,
            @NotNull SecurityServiceAsync securityServiceAsync,
            @NotNull SecurityServiceRx securityServiceRx,
            @NotNull ProviderServiceWrapper providerServiceWrapper,
            @NotNull SecurityCompactCache securityCompactCache,
            @NotNull SecurityPositionDetailCache securityPositionDetailCache,
            @NotNull PortfolioCache portfolioCache,
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
    @NotNull private DTOProcessor<Map<Integer, SecurityCompactDTO>> createMultipleSecurityProcessor()
    {
        return new DTOProcessorMultiSecurities(securityCompactCache);
    }

    public Map<Integer, SecurityCompactDTO> getMultipleSecurities(@NotNull SecurityIntegerIdList ids)
    {
        return createMultipleSecurityProcessor().process(
                securityService.getMultipleSecurities(ids.getCommaSeparated()));
    }

    @NotNull public MiddleCallback<Map<Integer, SecurityCompactDTO>> getMultipleSecurities(
            @NotNull SecurityIntegerIdList ids,
            @Nullable Callback<Map<Integer, SecurityCompactDTO>> callback)
    {
        MiddleCallback<Map<Integer, SecurityCompactDTO>> middleCallback = new
                BaseMiddleCallback<>(callback, createMultipleSecurityProcessor());
        securityServiceAsync.getMultipleSecurities(ids.getCommaSeparated(), middleCallback);

        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Securities">
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

    @NotNull public MiddleCallback<SecurityCompactDTOList> getSecurities(
            @NotNull SecurityListType key,
            @Nullable Callback<SecurityCompactDTOList> callback)
    {
        MiddleCallback<SecurityCompactDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        if (key instanceof TrendingSecurityListType)
        {
            TrendingSecurityListType trendingKey = (TrendingSecurityListType) key;
            if (trendingKey instanceof TrendingBasicSecurityListType)
            {
                this.securityServiceAsync.getTrendingSecurities(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage,
                        middleCallback);
            }
            else if (trendingKey instanceof TrendingPriceSecurityListType)
            {
                this.securityServiceAsync.getTrendingSecuritiesByPrice(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage,
                        middleCallback);
            }
            else if (trendingKey instanceof TrendingVolumeSecurityListType)
            {
                this.securityServiceAsync.getTrendingSecuritiesByVolume(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage,
                        middleCallback);
            }
            else if (trendingKey instanceof TrendingAllSecurityListType)
            {
                this.securityServiceAsync.getTrendingSecuritiesAllInExchange(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage,
                        middleCallback);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled type " + ((Object) trendingKey).getClass().getName());
            }
        }
        else if (key instanceof SearchSecurityListType)
        {
            SearchSecurityListType searchKey = (SearchSecurityListType) key;
            this.securityServiceAsync.searchSecurities(
                    searchKey.searchString,
                    searchKey.getPage(),
                    searchKey.perPage,
                    middleCallback);
        }
        else if (key instanceof ProviderSecurityListType)
        {
            return providerServiceWrapper.getProviderSecurities((ProviderSecurityListType) key, callback);
        }
        else if (key instanceof ExchangeSectorSecurityListType)
        {
            ExchangeSectorSecurityListType exchangeKey = (ExchangeSectorSecurityListType) key;
            this.securityServiceAsync.getBySectorAndExchange(
                    exchangeKey.exchangeId == null ? null: exchangeKey.exchangeId.key,
                    exchangeKey.sectorId == null ? null : exchangeKey.sectorId.key,
                    key.page,
                    key.perPage,
                    middleCallback);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
        }
        return middleCallback;
    }

    public Observable<SecurityCompactDTO> getSecuritiesRx(@NotNull SecurityListType key)
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
            //else if (trendingKey instanceof TrendingPriceSecurityListType)
            //{
            //    received =  this.securityServiceRx.getTrendingSecuritiesByPrice(
            //            trendingKey.exchange,
            //            trendingKey.getPage(),
            //            trendingKey.perPage);
            //}
            //else if (trendingKey instanceof TrendingVolumeSecurityListType)
            //{
            //    received =  this.securityServiceRx.getTrendingSecuritiesByVolume(
            //            trendingKey.exchange,
            //            trendingKey.getPage(),
            //            trendingKey.perPage);
            //}
            //else if (trendingKey instanceof TrendingAllSecurityListType)
            //{
            //    received =  this.securityServiceRx.getTrendingSecuritiesAllInExchange(
            //            trendingKey.exchange,
            //            trendingKey.getPage(),
            //            trendingKey.perPage);
            //}
            else
            {
                throw new IllegalArgumentException("Unhandled type " + ((Object) trendingKey).getClass().getName());
            }
        }
        //else if (key instanceof SearchSecurityListType)
        //{
        //    SearchSecurityListType searchKey = (SearchSecurityListType) key;
        //    received =  this.securityServiceRx.searchSecurities(
        //            searchKey.searchString,
        //            searchKey.getPage(),
        //            searchKey.perPage);
        //}
        //else if (key instanceof ProviderSecurityListType)
        //{
        //    received =  providerServiceWrapper.getProviderSecurities((ProviderSecurityListType) key);
        //}
        //else if (key instanceof ExchangeSectorSecurityListType)
        //{
        //    ExchangeSectorSecurityListType exchangeKey = (ExchangeSectorSecurityListType) key;
        //    received = this.securityService.getBySectorAndExchange(
        //            exchangeKey.exchangeId == null ? null: exchangeKey.exchangeId.key,
        //            exchangeKey.sectorId == null ? null : exchangeKey.sectorId.key,
        //            key.page,
        //            key.perPage);
        //}
        else
        {
            throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
        }
        return received
                .flatMap(new Func1<SecurityCompactDTOList, Observable<SecurityCompactDTO>>()
                {
                    @Override public Observable<SecurityCompactDTO> call(
                            @NotNull SecurityCompactDTOList securityCompactDTOs)
                    {
                        return Observable.from(securityCompactDTOs);
                    }
                });
    }
    //</editor-fold>

    //<editor-fold desc="Get Security">
    @NotNull protected DTOProcessor<SecurityPositionDetailDTO> createSecurityPositionDetailDTOProcessor(@NotNull SecurityId securityId)
    {
        return new DTOProcessorSecurityPositionDetailReceived(securityId, currentUserId.toUserBaseKey());
    }

    public SecurityPositionDetailDTO getSecurity(@NotNull SecurityId securityId)
    {
        return createSecurityPositionDetailDTOProcessor(securityId).process(
                this.securityService.getSecurity(securityId.getExchange(), securityId.getPathSafeSymbol()));
    }

    @NotNull public MiddleCallback<SecurityPositionDetailDTO> getSecurity(
            @NotNull SecurityId securityId,
            @Nullable Callback<SecurityPositionDetailDTO> callback)
    {
        MiddleCallback<SecurityPositionDetailDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createSecurityPositionDetailDTOProcessor(securityId));
        this.securityServiceAsync.getSecurity(securityId.getExchange(), securityId.getPathSafeSymbol(), middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Buy Security">
    @NotNull private DTOProcessor<SecurityPositionTransactionDTO> createSecurityPositionTransactionUpdatedProcessor(@NotNull SecurityId securityId)
    {
        return new DTOProcessorSecurityPositionTransactionUpdated(
                securityId,
                currentUserId.toUserBaseKey(),
                securityPositionDetailCache,
                portfolioCache);
    }

    public SecurityPositionTransactionDTO buy(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO)
    {
        return createSecurityPositionTransactionUpdatedProcessor(securityId).process(
                this.securityService.buy(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO));
    }

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
    //</editor-fold>

    //<editor-fold desc="Sell Security">
    public SecurityPositionTransactionDTO sell(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO)
    {
        return createSecurityPositionTransactionUpdatedProcessor(securityId).process(
                this.securityService.sell(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO));
    }

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
    //</editor-fold>

    //<editor-fold desc="Buy or Sell Security">
    public SecurityPositionTransactionDTO doTransaction(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO,
            boolean isBuy)
    {
        if (isBuy)
        {
            return buy(securityId, transactionFormDTO);
        }
        return sell(securityId, transactionFormDTO);
    }

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
