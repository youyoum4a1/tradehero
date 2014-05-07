package com.tradehero.th.network.service;

import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
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
import retrofit.RetrofitError;

@Singleton public class SecurityServiceWrapper
{
    private final SecurityService securityService;
    private final ProviderServiceWrapper providerServiceWrapper;
    private final SecurityPositionDetailCache securityPositionDetailCache;
    private final SecurityCompactCache securityCompactCache;
    private final UserProfileCache userProfileCache;
    private final CurrentUserId currentUserId;

    @Inject public SecurityServiceWrapper(
            SecurityService securityService,
            ProviderServiceWrapper providerServiceWrapper,
            SecurityPositionDetailCache securityPositionDetailCache,
            SecurityCompactCache securityCompactCache,
            UserProfileCache userProfileCache,
            CurrentUserId currentUserId)
    {
        super();
        this.securityService = securityService;
        this.providerServiceWrapper = providerServiceWrapper;
        this.securityPositionDetailCache = securityPositionDetailCache;
        this.securityCompactCache = securityCompactCache;
        this.userProfileCache = userProfileCache;
        this.currentUserId = currentUserId;
    }

    public MiddleCallback<Map<Integer, SecurityCompactDTO>> getMultipleSecurities(List<Integer> ids,
            Callback<Map<Integer, SecurityCompactDTO>> callback)
            throws RetrofitError
    {
        String securityIds = StringUtils.join(",", ids);
        MiddleCallback<Map<Integer, SecurityCompactDTO>> multipleSecurityFetchMiddleCallback = new
                BaseMiddleCallback<>(callback, createMultipleSecurityProcessor());
        securityService.getMultipleSecurities(securityIds, multipleSecurityFetchMiddleCallback);

        return multipleSecurityFetchMiddleCallback;
    }

    //<editor-fold desc="Routing SecurityListType">
    public List<SecurityCompactDTO> getSecurities(SecurityListType key)
            throws RetrofitError
    {
        if (key instanceof TrendingSecurityListType)
        {
            return getTrendingSecurities((TrendingSecurityListType) key);
        }
        else if (key instanceof SearchSecurityListType)
        {
            return searchSecurities((SearchSecurityListType) key);
        }
        else if (key instanceof ProviderSecurityListType)
        {
            return providerServiceWrapper.getProviderSecurities((ProviderSecurityListType) key);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    public void getSecurities(SecurityListType key, Callback<List<SecurityCompactDTO>> callback)
    {
        if (key instanceof TrendingSecurityListType)
        {
            getTrendingSecurities((TrendingSecurityListType) key, callback);
        }
        else if (key instanceof SearchSecurityListType)
        {
            searchSecurities((SearchSecurityListType) key, callback);
        }
        else if (key instanceof ProviderSecurityListType)
        {
            providerServiceWrapper.getProviderSecurities((ProviderSecurityListType) key, callback);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
        }
    }

    public List<SecurityCompactDTO> getTrendingSecurities(TrendingSecurityListType key)
            throws RetrofitError
    {
        if (key instanceof TrendingBasicSecurityListType)
        {
            return getTrendingSecuritiesBasic((TrendingBasicSecurityListType) key);
        }
        else if (key instanceof TrendingPriceSecurityListType)
        {
            return getTrendingSecuritiesByPrice((TrendingPriceSecurityListType) key);
        }
        else if (key instanceof TrendingVolumeSecurityListType)
        {
            return getTrendingSecuritiesByVolume((TrendingVolumeSecurityListType) key);
        }
        else if (key instanceof TrendingAllSecurityListType)
        {
            return getTrendingSecuritiesAllInExchange((TrendingAllSecurityListType) key);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }

    public void getTrendingSecurities(TrendingSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key instanceof TrendingBasicSecurityListType)
        {
            getTrendingSecuritiesBasic((TrendingBasicSecurityListType) key, callback);
        }
        else if (key instanceof TrendingPriceSecurityListType)
        {
            getTrendingSecuritiesByPrice((TrendingPriceSecurityListType) key, callback);
        }
        else if (key instanceof TrendingVolumeSecurityListType)
        {
            getTrendingSecuritiesByVolume((TrendingVolumeSecurityListType) key, callback);
        }
        else if (key instanceof TrendingAllSecurityListType)
        {
            getTrendingSecuritiesAllInExchange((TrendingAllSecurityListType) key, callback);
        }
        throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
    }
    //</editor-fold>

    //<editor-fold desc="Get Basic Trending">
    public List<SecurityCompactDTO> getTrendingSecuritiesBasic(TrendingBasicSecurityListType key)
            throws RetrofitError
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.getPage() == null)
            {
                return this.securityService.getTrendingSecurities();
            }
            else if (key.perPage == null)
            {
                return this.securityService.getTrendingSecurities("", key.getPage());
            }
            return this.securityService.getTrendingSecurities("", key.getPage(), key.perPage);
        }
        else if (key.getPage() == null)
        {
            return this.securityService.getTrendingSecurities(key.exchange);
        }
        else if (key.perPage == null)
        {
            return this.securityService.getTrendingSecurities(key.exchange, key.getPage());
        }
        return this.securityService.getTrendingSecurities(key.exchange, key.getPage(), key.perPage);
    }

    public void getTrendingSecuritiesBasic(TrendingBasicSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.getPage() == null)
            {
                this.securityService.getTrendingSecurities(callback);
            }
            else if (key.perPage == null)
            {
                this.securityService.getTrendingSecurities("", key.getPage(), callback);
            }
            else
            {
                this.securityService.getTrendingSecurities("", key.getPage(), key.perPage, callback);
            }
        }
        else if (key.getPage() == null)
        {
            this.securityService.getTrendingSecurities(key.exchange, callback);
        }
        else if (key.perPage == null)
        {
            this.securityService.getTrendingSecurities(key.exchange, key.getPage(), callback);
        }
        else
        {
            this.securityService.getTrendingSecurities(key.exchange, key.getPage(), key.perPage, callback);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Get Trending By Price">
    public List<SecurityCompactDTO> getTrendingSecuritiesByPrice(TrendingPriceSecurityListType key)
            throws RetrofitError
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.getPage() == null)
            {
                return this.securityService.getTrendingSecuritiesByPrice();
            }
            else if (key.perPage == null)
            {
                return this.securityService.getTrendingSecuritiesByPrice("", key.getPage());
            }
            return this.securityService.getTrendingSecuritiesByPrice("", key.getPage(), key.perPage);
        }
        else if (key.getPage() == null)
        {
            return this.securityService.getTrendingSecuritiesByPrice(key.exchange);
        }
        else if (key.perPage == null)
        {
            return this.securityService.getTrendingSecuritiesByPrice(key.exchange, key.getPage());
        }
        return this.securityService.getTrendingSecuritiesByPrice(key.exchange, key.getPage(), key.perPage);
    }

    public void getTrendingSecuritiesByPrice(TrendingPriceSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.getPage() == null)
            {
                this.securityService.getTrendingSecuritiesByPrice(callback);
            }
            else if (key.perPage == null)
            {
                this.securityService.getTrendingSecuritiesByPrice("", key.getPage(), callback);
            }
            else
            {
                this.securityService.getTrendingSecuritiesByPrice("", key.getPage(), key.perPage, callback);
            }
        }
        else if (key.getPage() == null)
        {
            this.securityService.getTrendingSecuritiesByPrice(key.exchange, callback);
        }
        else if (key.perPage == null)
        {
            this.securityService.getTrendingSecuritiesByPrice(key.exchange, key.getPage(), callback);
        }
        else
        {
            this.securityService.getTrendingSecuritiesByPrice(key.exchange, key.getPage(), key.perPage, callback);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Get Trending By Volume">
    public List<SecurityCompactDTO> getTrendingSecuritiesByVolume(TrendingVolumeSecurityListType key)
            throws RetrofitError
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.getPage() == null)
            {
                return this.securityService.getTrendingSecuritiesByVolume();
            }
            else if (key.perPage == null)
            {
                return this.securityService.getTrendingSecuritiesByVolume("", key.getPage());
            }
            return this.securityService.getTrendingSecuritiesByVolume("", key.getPage(), key.perPage);
        }
        else if (key.getPage() == null)
        {
            return this.securityService.getTrendingSecuritiesByVolume(key.exchange);
        }
        else if (key.perPage == null)
        {
            return this.securityService.getTrendingSecuritiesByVolume(key.exchange, key.getPage());
        }
        return this.securityService.getTrendingSecuritiesByVolume(key.exchange, key.getPage(), key.perPage);
    }

    public void getTrendingSecuritiesByVolume(TrendingVolumeSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.getPage() == null)
            {
                this.securityService.getTrendingSecuritiesByVolume(callback);
            }
            else if (key.perPage == null)
            {
                this.securityService.getTrendingSecuritiesByVolume("", key.getPage(), callback);
            }
            else
            {
                this.securityService.getTrendingSecuritiesByVolume("", key.getPage(), key.perPage, callback);
            }
        }
        else if (key.getPage() == null)
        {
            this.securityService.getTrendingSecuritiesByVolume(key.exchange, callback);
        }
        else if (key.perPage == null)
        {
            this.securityService.getTrendingSecuritiesByVolume(key.exchange, key.getPage(), callback);
        }
        else
        {
            this.securityService.getTrendingSecuritiesByVolume(key.exchange, key.getPage(), key.perPage, callback);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Get Trending For All">
    public List<SecurityCompactDTO> getTrendingSecuritiesAllInExchange(TrendingAllSecurityListType key)
            throws RetrofitError
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.getPage() == null)
            {
                return this.securityService.getTrendingSecuritiesAllInExchange();
            }
            else if (key.perPage == null)
            {
                return this.securityService.getTrendingSecuritiesAllInExchange("", key.getPage());
            }
            return this.securityService.getTrendingSecuritiesAllInExchange("", key.getPage(), key.perPage);
        }
        else if (key.getPage() == null)
        {
            return this.securityService.getTrendingSecuritiesAllInExchange(key.exchange);
        }
        else if (key.perPage == null)
        {
            return this.securityService.getTrendingSecuritiesAllInExchange(key.exchange, key.getPage());
        }
        return this.securityService.getTrendingSecuritiesAllInExchange(key.exchange, key.getPage(), key.perPage);
    }

    public void getTrendingSecuritiesAllInExchange(TrendingAllSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.exchange.equals(TrendingSecurityListType.ALL_EXCHANGES))
        {
            if (key.getPage() == null)
            {
                this.securityService.getTrendingSecuritiesAllInExchange(callback);
            }
            else if (key.perPage == null)
            {
                this.securityService.getTrendingSecuritiesAllInExchange("", key.getPage(), callback);
            }
            else
            {
                this.securityService.getTrendingSecuritiesAllInExchange("", key.getPage(), key.perPage, callback);
            }
        }
        else if (key.getPage() == null)
        {
            this.securityService.getTrendingSecuritiesAllInExchange(key.exchange, callback);
        }
        else if (key.perPage == null)
        {
            this.securityService.getTrendingSecuritiesAllInExchange(key.exchange, key.getPage(), callback);
        }
        else
        {
            this.securityService.getTrendingSecuritiesAllInExchange(key.exchange, key.getPage(), key.perPage, callback);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Search Securities">
    public List<SecurityCompactDTO> searchSecurities(SearchSecurityListType key)
            throws RetrofitError
    {
        if (key.getPage() == null)
        {
            return this.securityService.searchSecurities(key.searchString);
        }
        else if (key.perPage == null)
        {
            return this.securityService.searchSecurities(key.searchString, key.getPage());
        }
        return this.securityService.searchSecurities(key.searchString, key.getPage(), key.perPage);
    }

    public void searchSecurities(SearchSecurityListType key,
            Callback<List<SecurityCompactDTO>> callback)
    {
        if (key.getPage() == null)
        {
            this.securityService.searchSecurities(key.searchString, callback);
        }
        else if (key.perPage == null)
        {
            this.securityService.searchSecurities(key.searchString, key.getPage(), callback);
        }
        else
        {
            this.securityService.searchSecurities(key.searchString, key.getPage(), key.perPage, callback);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Get Security">
    public SecurityPositionDetailDTO getSecurity(SecurityId securityId)
            throws RetrofitError
    {
        return this.securityService.getSecurity(securityId.exchange, securityId.securitySymbol);
    }

    public void getSecurity(SecurityId securityId, Callback<SecurityPositionDetailDTO> callback)
    {
        this.securityService.getSecurity(securityId.exchange, securityId.securitySymbol, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Buy Security">
    public SecurityPositionDetailDTO buy(SecurityId securityId, TransactionFormDTO transactionFormDTO)
            throws RetrofitError
    {
        return this.securityService.buy(securityId.exchange, securityId.securitySymbol, transactionFormDTO);
    }

    public void buy(SecurityId securityId, TransactionFormDTO transactionFormDTO, Callback<SecurityPositionDetailDTO> callback)
    {
        this.securityService.buy(securityId.exchange, securityId.securitySymbol, transactionFormDTO, callback);
    }
    //</editor-fold>

    //<editor-fold desc="Sell Security">
    public SecurityPositionDetailDTO sell(SecurityId securityId, TransactionFormDTO transactionFormDTO)
            throws RetrofitError
    {
        return this.securityService.sell(securityId.exchange, securityId.securitySymbol, transactionFormDTO);
    }

    public void sell(SecurityId securityId, TransactionFormDTO transactionFormDTO, Callback<SecurityPositionDetailDTO> callback)
    {
        this.securityService.sell(securityId.exchange, securityId.securitySymbol, transactionFormDTO, callback);
    }
    //</editor-fold>

    public MiddleCallback<SecurityPositionDetailDTO> doTransaction(
            SecurityId securityId,
            TransactionFormDTO transactionFormDTO,
            boolean isBuy,
            Callback<SecurityPositionDetailDTO> securityPositionDetailDTOCallback)
    {
        MiddleCallback<SecurityPositionDetailDTO> securityMiddleCallback =
                new BaseMiddleCallback<>(securityPositionDetailDTOCallback, createSecurityPositionProcessor(securityId));

        if (isBuy)
        {
            this.securityService.buy(securityId.exchange, securityId.securitySymbol, transactionFormDTO, securityMiddleCallback);
        }
        else
        {
            this.securityService.sell(securityId.exchange, securityId.securitySymbol, transactionFormDTO, securityMiddleCallback);
        }

        return securityMiddleCallback;
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
}
