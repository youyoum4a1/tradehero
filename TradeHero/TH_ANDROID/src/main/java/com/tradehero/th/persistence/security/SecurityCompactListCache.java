package com.tradehero.th.persistence.security;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.security.SearchSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.common.persistence.StraightDTOCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 5:04 PM To change this template use File | Settings | File Templates. */
@Singleton
public class SecurityCompactListCache extends StraightDTOCache<String, SecurityListType, List<SecurityId>>
{
    public static final String TAG = SecurityCompactListCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 50;

    @Inject protected Lazy<SecurityService> securityService;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;

    //<editor-fold desc="Constructors">
    public SecurityCompactListCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected List<SecurityId> fetch(SecurityListType key)
    {
        THLog.d(TAG, "fetch " + key);
        try
        {
            if (key instanceof TrendingSecurityListType)
            {
                return putInternal(key, fetch((TrendingSecurityListType) key));
            }
            if (key instanceof SearchSecurityListType)
            {
                return putInternal(key, fetch((SearchSecurityListType) key));
            }
            throw new IllegalArgumentException("Unhandled type " + key.getClass().getName());
        }
        catch (RetrofitError retrofitError)
        {
            BasicRetrofitErrorHandler.handle(retrofitError);
            THLog.e(TAG, "Error requesting key " + key.toString(), retrofitError);
        }
        return null;
    }

    @Override public List<SecurityId> getOrFetch(SecurityListType key, boolean force)
    {
        THLog.d(TAG, "getOrFetch " + key);
        return super.getOrFetch(key, force);
    }

    @Override public List<SecurityId> get(SecurityListType key)
    {
        THLog.d(TAG, "get " + key);
        return super.get(key);
    }

    protected List<SecurityCompactDTO> fetch(TrendingSecurityListType key) throws RetrofitError
    {
        return securityService.get().getTrendingSecurities();
    }

    protected List<SecurityCompactDTO> fetch(SearchSecurityListType key) throws RetrofitError
    {
        return securityService.get().searchSecurities(
                key.getSearchString(),
                key.getPage(),
                key.getPerPage());
    }

    protected List<SecurityId> putInternal(SecurityListType key, List<SecurityCompactDTO> fleshedValues)
    {
        List<SecurityId> securityIds = null;
        if (fleshedValues != null)
        {
            securityIds = new ArrayList<>();
            SecurityId securityId;
            for(SecurityCompactDTO securityCompactDTO: fleshedValues)
            {
                securityId = securityCompactDTO.getSecurityId();
                securityIds.add(securityId);
                securityCompactCache.get().put(securityId, securityCompactDTO);
            }
            put(key, securityIds);
        }
        return securityIds;
    }
}
