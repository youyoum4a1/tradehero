package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.fx.FXChartDTO;
import com.tradehero.th.api.fx.FXChartGranularity;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.security.SecurityIntegerIdList;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListTypeNew;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingAllSecurityListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.key.TrendingFxSecurityListType;
import com.tradehero.th.api.security.key.TrendingPriceSecurityListType;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.api.security.key.TrendingVolumeSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.security.DTOProcessorMultiSecurities;
import com.tradehero.th.models.security.DTOProcessorSecurityPositionTransactionUpdated;
import com.tradehero.th.persistence.portfolio.PortfolioCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

@Singleton public class SecurityServiceWrapper
{
    @NonNull private final SecurityServiceRx securityServiceRx;
    @NonNull private final ProviderServiceWrapper providerServiceWrapper;
    @NonNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;
    @NonNull private final Lazy<PortfolioCacheRx> portfolioCache;
    @NonNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public SecurityServiceWrapper(
            @NonNull SecurityServiceRx securityServiceRx,
            @NonNull ProviderServiceWrapper providerServiceWrapper,
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull Lazy<PortfolioCacheRx> portfolioCache,
            @NonNull CurrentUserId currentUserId)
    {
        super();
        this.securityServiceRx = securityServiceRx;
        this.providerServiceWrapper = providerServiceWrapper;
        this.securityCompactCache = securityCompactCache;
        this.portfolioCache = portfolioCache;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    //<editor-fold desc="Get Multiple Securities">
    @NonNull public Observable<Map<Integer, SecurityCompactDTO>> getMultipleSecuritiesRx(@NonNull SecurityIntegerIdList ids)
    {
        return securityServiceRx.getMultipleSecurities(ids.getCommaSeparated())
                .map(new DTOProcessorMultiSecurities(securityCompactCache.get()));
    }
    //</editor-fold>

    //<editor-fold desc="Get Securities">
    @NonNull public Observable<SecurityCompactDTOList> getSecuritiesRx(@NonNull SecurityListType key)
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
                received = this.securityServiceRx.getTrendingSecuritiesByPrice(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingVolumeSecurityListType)
            {
                received = this.securityServiceRx.getTrendingSecuritiesByVolume(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingAllSecurityListType)
            {
                received = this.securityServiceRx.getTrendingSecuritiesAllInExchange(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled type " + ((Object) trendingKey).getClass().getName());
            }
        }
        else if (key instanceof TrendingFxSecurityListType)
        {
            // FIXME when the server offers pagination
            if (key.page != null && key.page == 1)
            {
                received = this.securityServiceRx.getFXSecurities();
            }
            else
            {
                received = Observable.just(new SecurityCompactDTOList());
            }
        }
        else if (key instanceof SearchSecurityListType)
        {
            SearchSecurityListType searchKey = (SearchSecurityListType) key;
            received = this.securityServiceRx.searchSecurities(
                    searchKey.searchString,
                    searchKey.getPage(),
                    searchKey.perPage);
        }
        else if (key instanceof ProviderSecurityListType)
        {
            received = providerServiceWrapper.getProviderSecuritiesRx((ProviderSecurityListType) key);
        }
        else if (key instanceof ExchangeSectorSecurityListType)
        {
            ExchangeSectorSecurityListType exchangeKey = (ExchangeSectorSecurityListType) key;
            received = this.securityServiceRx.getBySectorAndExchange(
                    exchangeKey.exchangeId == null ? null : exchangeKey.exchangeId.key,
                    exchangeKey.sectorId == null ? null : exchangeKey.sectorId.key,
                    key.page,
                    key.perPage);
        }
        else if (key instanceof ExchangeSectorSecurityListTypeNew)
        {
            ExchangeSectorSecurityListTypeNew exchangeKey = (ExchangeSectorSecurityListTypeNew) key;
            received = this.securityServiceRx.getBySectorsAndExchanges(
                    exchangeKey.getCommaSeparatedExchangeIds(),
                    exchangeKey.getCommaSeparatedSectorIds(),
                    exchangeKey.getPage(),
                    exchangeKey.perPage);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + ((Object) key).getClass().getName());
        }
        return received;
    }
    //</editor-fold>

    //<editor-fold desc="Get Security">
    @NonNull public Observable<SecurityCompactDTO> getSecurityCompactRx(@NonNull SecurityId securityId)
    {
        return securityServiceRx.getCompactSecurity(
                securityId.getExchange(),
                securityId.getSecuritySymbol());
    }

    @NonNull public Observable<SecurityCompactDTO> getSecurityRx(@NonNull final SecurityIntegerId securityIntegerId)
    {
        SecurityIntegerIdList list = new SecurityIntegerIdList();
        list.add(securityIntegerId);
        return securityServiceRx.getMultipleSecurities(list.getCommaSeparated())
                .flatMap(new Func1<Map<Integer, SecurityCompactDTO>, Observable<SecurityCompactDTO>>()
                {
                    @Override public Observable<SecurityCompactDTO> call(Map<Integer, SecurityCompactDTO> map)
                    {
                        SecurityCompactDTO inMap = map.get(securityIntegerId.key);
                        if (inMap == null)
                        {
                            return Observable.empty();
                        }
                        return Observable.just(inMap);
                    }
                });
    }
    //</editor-fold>

    @NonNull public Observable<OwnedPortfolioIdList> getApplicablePortfolioIdsRx(
            @NonNull SecurityId securityId)
    {
        return securityServiceRx.getApplicablePortfolioIds(
                securityId.getExchange(),
                securityId.getSecuritySymbol());
    }

    @NonNull public Observable<PositionDTOList> getSecurityPositions(@NonNull SecurityId securityId)
    {
        return securityServiceRx.getPositions(
                securityId.getExchange(),
                securityId.getSecuritySymbol());
    }

    //<editor-fold desc="Buy Security">
    @NonNull public Observable<SecurityPositionTransactionDTO> buyRx(
            @NonNull SecurityId securityId,
            @NonNull TransactionFormDTO transactionFormDTO)
    {
        Observable<SecurityPositionTransactionDTO> buyResult;
        if (securityId.getExchange().equals(SecurityUtils.FX_EXCHANGE))
        {
            buyResult = this.securityServiceRx.buyFx(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO);
        }
        else
        {
            buyResult = this.securityServiceRx.buy(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO);
        }
        return buyResult
                .map(new DTOProcessorSecurityPositionTransactionUpdated(
                        securityId,
                        currentUserId.toUserBaseKey(),
                        portfolioCache.get()));
    }
    //</editor-fold>

    //<editor-fold desc="Sell Security">
    @NonNull public Observable<SecurityPositionTransactionDTO> sellRx(
            @NonNull SecurityId securityId,
            @NonNull TransactionFormDTO transactionFormDTO)
    {
        Observable<SecurityPositionTransactionDTO> sellResult;
        if (securityId.getExchange().equals(SecurityUtils.FX_EXCHANGE))
        {
            sellResult = this.securityServiceRx.sellFx(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO);
        }
        else
        {
            sellResult = this.securityServiceRx.sell(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO);
        }
        return sellResult
                .map(new DTOProcessorSecurityPositionTransactionUpdated(
                        securityId,
                        currentUserId.toUserBaseKey(),
                        portfolioCache.get()));
    }
    //</editor-fold>

    //<editor-fold desc="Buy or Sell Security">
    @NonNull public Observable<SecurityPositionTransactionDTO> doTransactionRx(
            @NonNull SecurityId securityId,
            @NonNull TransactionFormDTO transactionFormDTO,
            boolean isBuy)
    {
        if (isBuy)
        {
            return buyRx(securityId, transactionFormDTO);
        }
        return sellRx(securityId, transactionFormDTO);
    }
    //</editor-fold>

    //<editor-fold desc="Get FX KChart">
    @NonNull public Observable<FXChartDTO> getFXHistory(
            @NonNull SecurityId securityId,
            @NonNull FXChartGranularity granularity)
    {
        return securityServiceRx.getFXHistory(securityId.getSecuritySymbol(), granularity.code);
    }
    //</editor-fold>

    //<editor-fold desc="Get FX All Price">
    @NonNull public Observable<List<QuoteDTO>> getFXSecuritiesAllPriceRx()
    {
        return securityServiceRx.getFXSecuritiesAllPrice();
    }
    //</editor-fold>
}
