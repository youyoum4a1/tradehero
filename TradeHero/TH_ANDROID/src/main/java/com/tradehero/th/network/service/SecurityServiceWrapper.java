package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOFactory;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.security.key.SearchSecurityListType;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingAllSecurityListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.key.TrendingPriceSecurityListType;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.api.security.key.TrendingVolumeSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.security.DTOProcessorSecurityCompactListReceived;
import com.tradehero.th.models.security.DTOProcessorSecurityPosition;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.StringUtils;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

@Singleton public class SecurityServiceWrapper
{
    private final SecurityService securityService;
    private final SecurityServiceAsync securityServiceAsync;
    private final ProviderServiceWrapper providerServiceWrapper;
    private final SecurityPositionDetailCache securityPositionDetailCache;
    private final SecurityCompactCache securityCompactCache;
    private final SecurityCompactDTOFactory securityCompactDTOFactory;
    private final UserProfileCache userProfileCache;
    private final CurrentUserId currentUserId;

    @Inject public SecurityServiceWrapper(
            SecurityService securityService,
            SecurityServiceAsync securityServiceAsync,
            ProviderServiceWrapper providerServiceWrapper,
            SecurityPositionDetailCache securityPositionDetailCache,
            SecurityCompactCache securityCompactCache,
            SecurityCompactDTOFactory securityCompactDTOFactory,
            UserProfileCache userProfileCache,
            CurrentUserId currentUserId)
    {
        super();
        this.securityService = securityService;
        this.securityServiceAsync = securityServiceAsync;
        this.providerServiceWrapper = providerServiceWrapper;
        this.securityPositionDetailCache = securityPositionDetailCache;
        this.securityCompactCache = securityCompactCache;
        this.securityCompactDTOFactory = securityCompactDTOFactory;
        this.userProfileCache = userProfileCache;
        this.currentUserId = currentUserId;
    }

    public MiddleCallback<Map<Integer, SecurityCompactDTO>> getMultipleSecurities(List<Integer> ids,
            Callback<Map<Integer, SecurityCompactDTO>> callback)
    {
        String securityIds = StringUtils.join(",", ids);
        MiddleCallback<Map<Integer, SecurityCompactDTO>> multipleSecurityFetchMiddleCallback = new
                BaseMiddleCallback<>(callback, createMultipleSecurityProcessor());
        securityServiceAsync.getMultipleSecurities(securityIds, multipleSecurityFetchMiddleCallback);

        return multipleSecurityFetchMiddleCallback;
    }

    //<editor-fold desc="Get Securities">
    public List<SecurityCompactDTO> getSecurities(SecurityListType key)
    {
        List<SecurityCompactDTO> received;
        if (key instanceof TrendingSecurityListType)
        {
            TrendingSecurityListType trendingKey = (TrendingSecurityListType) key;
            if (trendingKey instanceof TrendingBasicSecurityListType)
            {
                received = this.securityService.getTrendingSecurities(
                        trendingKey.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES) ? "" : trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingPriceSecurityListType)
            {
                received =  this.securityService.getTrendingSecuritiesByPrice(
                        trendingKey.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES) ? "" : trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingVolumeSecurityListType)
            {
                received =  this.securityService.getTrendingSecuritiesByVolume(
                        trendingKey.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES) ? "" : trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else if (trendingKey instanceof TrendingAllSecurityListType)
            {
                received =  this.securityService.getTrendingSecuritiesAllInExchange(
                        trendingKey.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES) ? "" : trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled type " + trendingKey.getClass().getName());
            }
        }
        else if (key instanceof SearchSecurityListType)
        {
            SearchSecurityListType searchKey = (SearchSecurityListType) key;
            received =  this.securityService.searchSecurities(searchKey.searchString, searchKey.getPage(), searchKey.perPage);
        }
        else if (key instanceof ProviderSecurityListType)
        {
            received =  providerServiceWrapper.getProviderSecurities((ProviderSecurityListType) key);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
        }
        return createSecurityListProcessor().process(received);
    }

    public MiddleCallback<List<SecurityCompactDTO>> getSecurities(SecurityListType key, Callback<List<SecurityCompactDTO>> callback)
    {
        MiddleCallback<List<SecurityCompactDTO>> middleCallback = new BaseMiddleCallback<>(callback, createSecurityListProcessor());
        if (key instanceof TrendingSecurityListType)
        {
            TrendingSecurityListType trendingKey = (TrendingSecurityListType) key;
            if (trendingKey instanceof TrendingBasicSecurityListType)
            {
                this.securityServiceAsync.getTrendingSecurities(
                        trendingKey.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES) ? "" : trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage,
                        middleCallback);
            }
            else if (trendingKey instanceof TrendingPriceSecurityListType)
            {
                this.securityServiceAsync.getTrendingSecuritiesByPrice(
                        trendingKey.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES) ? "" : trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage,
                        middleCallback);
            }
            else if (trendingKey instanceof TrendingVolumeSecurityListType)
            {
                this.securityServiceAsync.getTrendingSecuritiesByVolume(
                        trendingKey.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES) ? "" : trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage,
                        middleCallback);
            }
            else if (trendingKey instanceof TrendingAllSecurityListType)
            {
                this.securityServiceAsync.getTrendingSecuritiesAllInExchange(
                        trendingKey.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES) ? "" : trendingKey.exchange,
                        trendingKey.getPage(),
                        trendingKey.perPage,
                        middleCallback);
            }
            throw new IllegalArgumentException("Unhandled type " + trendingKey.getClass().getName());
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
        else
        {
            throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
        }
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Security">
    public SecurityPositionDetailDTO getSecurity(SecurityId securityId)
    {
        return this.securityService.getSecurity(securityId.exchange, securityId.securitySymbol);
    }

    public MiddleCallback<SecurityPositionDetailDTO> getSecurity(SecurityId securityId, Callback<SecurityPositionDetailDTO> callback)
    {
        MiddleCallback<SecurityPositionDetailDTO> middleCallback = new BaseMiddleCallback<>(callback);
        this.securityServiceAsync.getSecurity(securityId.exchange, securityId.securitySymbol, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Buy Security">
    public SecurityPositionDetailDTO buy(SecurityId securityId, TransactionFormDTO transactionFormDTO)
    {
        return createSecurityPositionProcessor(securityId).process(
                this.securityService.buy(securityId.exchange, securityId.securitySymbol, transactionFormDTO));
    }

    public MiddleCallback<SecurityPositionDetailDTO> buy(SecurityId securityId, TransactionFormDTO transactionFormDTO, Callback<SecurityPositionDetailDTO> callback)
    {
        MiddleCallback<SecurityPositionDetailDTO> middleCallback = new BaseMiddleCallback<>(callback, createSecurityPositionProcessor(securityId));
        this.securityServiceAsync.buy(securityId.exchange, securityId.securitySymbol, transactionFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Sell Security">
    public SecurityPositionDetailDTO sell(SecurityId securityId, TransactionFormDTO transactionFormDTO)
    {
        return createSecurityPositionProcessor(securityId).process(
                this.securityService.sell(securityId.exchange, securityId.securitySymbol, transactionFormDTO));
    }

    public MiddleCallback<SecurityPositionDetailDTO> sell(SecurityId securityId, TransactionFormDTO transactionFormDTO, Callback<SecurityPositionDetailDTO> callback)
    {
        MiddleCallback<SecurityPositionDetailDTO> middleCallback = new BaseMiddleCallback<>(callback, createSecurityPositionProcessor(securityId));
        this.securityServiceAsync.sell(securityId.exchange, securityId.securitySymbol, transactionFormDTO, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Buy or Sell Security">
    public MiddleCallback<SecurityPositionDetailDTO> doTransaction(
            SecurityId securityId,
            TransactionFormDTO transactionFormDTO,
            boolean isBuy,
            Callback<SecurityPositionDetailDTO> callback)
    {
        if (isBuy)
        {
            return buy(securityId, transactionFormDTO, callback);
        }
        return sell(securityId, transactionFormDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="DTO Processors">
    private DTOProcessor<List<SecurityCompactDTO>> createSecurityListProcessor()
    {
        return new DTOProcessorSecurityCompactListReceived(securityCompactDTOFactory);
    }

    private DTOProcessor<SecurityPositionDetailDTO> createSecurityPositionProcessor(SecurityId securityId)
    {
        return new DTOProcessorSecurityPosition(
                securityPositionDetailCache,
                userProfileCache,
                currentUserId,
                securityId);
    }

    private DTOProcessor<Map<Integer, SecurityCompactDTO>> createMultipleSecurityProcessor()
    {
        return new DTOProcessor<Map<Integer, SecurityCompactDTO>>()
        {
            @Override public Map<Integer, SecurityCompactDTO> process(Map<Integer, SecurityCompactDTO> value)
            {
                for (Map.Entry<Integer, SecurityCompactDTO> securityEntry: value.entrySet())
                {
                    if (securityEntry.getKey() != null && securityEntry.getKey() != 0)
                    {
                        SecurityCompactDTO securityCompactDTO = securityEntry.getValue();
                        SecurityId securityId = new SecurityId(securityCompactDTO.exchange, securityCompactDTO.symbol);
                        securityCompactCache.put(securityId, securityCompactDTO);
                    }
                }
                return value;
            }
        };
    }
    //</editor-fold>
}
