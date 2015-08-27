package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.SecurityOrderDTO;
import com.tradehero.livetrade.ActualSecurityListDTO;
import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.*;
import com.tradehero.th.api.security.key.*;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.security.DTOProcessorMultiSecurities;
import com.tradehero.th.models.security.DTOProcessorSecurityPositionReceived;
import com.tradehero.th.models.security.DTOProcessorSecurityPositionUpdated;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton public class SecurityServiceWrapper
{
    @NotNull private final SecurityService securityService;
    @NotNull private final SecurityServiceAsync securityServiceAsync;
    @NotNull private final ProviderServiceWrapper providerServiceWrapper;
    @NotNull private final SecurityPositionDetailCache securityPositionDetailCache;
    @NotNull private final SecurityCompactCache securityCompactCache;
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public SecurityServiceWrapper(
            @NotNull SecurityService securityService,
            @NotNull SecurityServiceAsync securityServiceAsync,
            @NotNull ProviderServiceWrapper providerServiceWrapper,
            @NotNull SecurityPositionDetailCache securityPositionDetailCache,
            @NotNull SecurityCompactCache securityCompactCache,
            @NotNull UserProfileCache userProfileCache,
            @NotNull CurrentUserId currentUserId)
    {
        super();
        this.securityService = securityService;
        this.securityServiceAsync = securityServiceAsync;
        this.providerServiceWrapper = providerServiceWrapper;
        this.securityPositionDetailCache = securityPositionDetailCache;
        this.securityCompactCache = securityCompactCache;
        this.userProfileCache = userProfileCache;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    //<editor-fold desc="Get Multiple Securities">
    @NotNull private DTOProcessor<Map<Integer, SecurityCompactDTO>> createMultipleSecurityProcessor()
    {
        return new DTOProcessorMultiSecurities(securityCompactCache);
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

    public synchronized SecurityCompactDTOList processFromExtraData(SecurityCompactExtraDTOList received)
    {
        SecurityCompactDTOList retList = new SecurityCompactDTOList();
        if (received != null)
        {
            for (int i = 0; i < received.size(); i++)
            {
                SecurityCompactDTO security = received.get(i).security;
                security.marketCapRefUSD = received.get(i).marketCapRefUSD;
                security.watchCount = received.get(i).watchCount;
                security.holdCount = received.get(i).holdCount;
                security.searchCount = received.get(i).searchCount;
                retList.add(security);
            }
        }
        return retList;
    }

    //<editor-fold desc="Get Securities">
    public synchronized SecurityCompactDTOList getSecurities(@NotNull SecurityListType key)
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
                received = this.securityService.getTrendingSecuritiesByPrice(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingVolumeSecurityListType)
            {
                received = this.securityService.getTrendingSecuritiesByVolume(
                        trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingAllSecurityListType)
            {
                SecurityCompactExtraDTOList data = null;
                if (((TrendingAllSecurityListType) trendingKey).type == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_WATCH)
                {
                    data = this.securityService.getTrendingSecuritiesAllInExchangeWatch(
                            trendingKey.exchange,
                            trendingKey.getPage(),
                            trendingKey.perPage);
                }
                else if (((TrendingAllSecurityListType) trendingKey).type == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_HOLD)
                {
                    data = this.securityService.getTrendingSecuritiesAllInExchangeHold(
                            trendingKey.exchange,
                            trendingKey.getPage(),
                            trendingKey.perPage);
                }
                else if (((TrendingAllSecurityListType) trendingKey).type == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_CHINA_CONCEPT)
                {
                    data = this.securityService.getTrendingSecuritiesAllInExchangeChinaConcept(
                            trendingKey.exchange,
                            trendingKey.getPage(),
                            trendingKey.perPage);
                }
                else if (((TrendingAllSecurityListType) trendingKey).type == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_COMPETITION)
                {
                    SecurityCompactDTOList dataCompetition = this.securityService.getTrendingSecuritiesAllInCompetition(
                            ((TrendingAllSecurityListType) trendingKey).competitionId,
                            trendingKey.getPage(),
                            trendingKey.perPage);
                    return dataCompetition;
                }

                else if (((TrendingAllSecurityListType) trendingKey).type == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_SEARCH)
                {
                    SecurityCompactDTOList dataCompetition = this.securityService.getTrendingSecuritiesAllInCompetitionSearch(
                            ((TrendingAllSecurityListType) trendingKey).competitionId,
                            ((TrendingAllSecurityListType) trendingKey).q,
                            trendingKey.getPage(),
                            trendingKey.perPage);
                    return dataCompetition;
                }
                else if (((TrendingAllSecurityListType) trendingKey).type == TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_RISE_PERCENT)
                {
                    data = this.securityService.getTrendingSecuritiesAllInRisePercent(
                            trendingKey.exchange,
                            trendingKey.getPage(),
                            trendingKey.perPage);
                }
                received = processFromExtraData(data);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled type " + trendingKey.getClass().getName());
            }
        }
        else if (key instanceof SearchSecurityListType)
        {
            SearchSecurityListType searchKey = (SearchSecurityListType) key;
            received = this.securityService.searchSecurities(
                    searchKey.searchString,
                    searchKey.getPage(),
                    searchKey.perPage);
        }
        else if(key instanceof SearchHotSecurityListType)//热门股票搜索
        {
            SecurityCompactExtraDTOList data = null;
            SearchHotSecurityListType searchKey = (SearchHotSecurityListType) key;
            data = this.securityService.searchHotSecurities(
                    searchKey.getPage(),
                    searchKey.perPage);
            received = processFromExtraData(data);
        }
        else if (key instanceof ProviderSecurityListType)
        {
            received = providerServiceWrapper.getProviderSecurities((ProviderSecurityListType) key);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
        }
        return received;
    }
    //</editor-fold>

    //<editor-fold desc="Get Security">
    @NotNull protected DTOProcessor<SecurityPositionDetailDTO> createSecurityPositionDetailDTOProcessor(@NotNull SecurityId securityId)
    {
        return new DTOProcessorSecurityPositionReceived(securityId, currentUserId);
    }

    public SecurityPositionDetailDTO getSecurity(@NotNull SecurityId securityId)
    {
        return createSecurityPositionDetailDTOProcessor(securityId).process(
                this.securityService.getSecurity(securityId.getExchange(), securityId.getPathSafeSymbol()));
    }


    //<editor-fold desc="Buy Security">
    @NotNull private DTOProcessor<SecurityPositionDetailDTO> createSecurityPositionUpdatedProcessor(@NotNull SecurityId securityId)
    {
        return new DTOProcessorSecurityPositionUpdated(
                securityPositionDetailCache,
                userProfileCache,
                currentUserId,
                securityId);
    }

    public SecurityPositionDetailDTO buy(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO)
    {
        return createSecurityPositionUpdatedProcessor(securityId).process(
                this.securityService.buy(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO));
    }

    @NotNull public MiddleCallback<SecurityPositionDetailDTO> buy(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO,
            @Nullable Callback<SecurityPositionDetailDTO> callback)
    {
        MiddleCallback<SecurityPositionDetailDTO> middleCallback = new BaseMiddleCallback<>(callback, createSecurityPositionUpdatedProcessor(
                securityId));
        this.securityServiceAsync.buy(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Sell Security">
    public SecurityPositionDetailDTO sell(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO)
    {
        return createSecurityPositionUpdatedProcessor(securityId).process(
                this.securityService.sell(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO));
    }

    @NotNull public MiddleCallback<SecurityPositionDetailDTO> sell(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO,
            @Nullable Callback<SecurityPositionDetailDTO> callback)
    {
        MiddleCallback<SecurityPositionDetailDTO> middleCallback = new BaseMiddleCallback<>(callback, createSecurityPositionUpdatedProcessor(
                securityId));
        this.securityServiceAsync.sell(securityId.getExchange(), securityId.getSecuritySymbol(), transactionFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    @NotNull public MiddleCallback<SecurityPositionDetailDTO> doTransaction(
            @NotNull SecurityId securityId,
            @NotNull TransactionFormDTO transactionFormDTO,
            boolean isBuy,
            @Nullable Callback<SecurityPositionDetailDTO> callback)
    {
        if (isBuy)
        {
            return buy(securityId, transactionFormDTO, callback);
        }
        return sell(securityId, transactionFormDTO, callback);
    }
    //</editor-fold>

    public void order(int portfolioId, String exchange, String symbol, int quantity, double price, Callback<Response> callback){
        SecurityOrderDTO securityOrderDTO = new SecurityOrderDTO();
        securityOrderDTO.portfolioId = portfolioId;
        securityOrderDTO.exchange = exchange;
        securityOrderDTO.symbol = symbol;
        securityOrderDTO.quantity = quantity;
        securityOrderDTO.price = price;
        securityServiceAsync.order(securityOrderDTO, callback);
    }

    public void sell(String exchange, String symbol, TransactionFormDTO transactionFormDTO, Callback<SecurityPositionDetailDTO> callback){
        securityServiceAsync.sell(exchange, symbol, transactionFormDTO, callback);
    }

    public void buy(String exchange, String symbol, TransactionFormDTO transactionFormDTO, Callback<SecurityPositionDetailDTO> callback){
        securityServiceAsync.buy(exchange, symbol, transactionFormDTO, callback);
    }

    public void searchSecuritySHESHA(String q, int page, int perPage,
                                     Callback<ActualSecurityListDTO> callback){
        securityServiceAsync.searchSecuritySHESHA(q, page, perPage, callback);
    }
}
