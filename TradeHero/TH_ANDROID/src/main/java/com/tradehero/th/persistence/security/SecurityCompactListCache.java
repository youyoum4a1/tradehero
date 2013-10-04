package com.tradehero.th.persistence.security;

import android.support.v4.util.LruCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SearchSecurityListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.persistence.DTOCache;
import com.tradehero.th.persistence.StraightDTOCache;
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

    @Override protected List<SecurityId> fetch(SecurityListType securityListType)
    {
        try
        {
            if (securityListType instanceof TrendingSecurityListType)
            {
                return putInternal(securityListType, fetch((TrendingSecurityListType) securityListType));
            }
            if (securityListType instanceof SearchSecurityListType)
            {
                return putInternal(securityListType, fetch((SearchSecurityListType) securityListType));
            }
            throw new IllegalArgumentException("Unhandled type " + securityListType.getClass().getName());
        }
        catch (RetrofitError retrofitError)
        {
            BasicRetrofitErrorHandler.handle(retrofitError);
            THLog.e(TAG, "Error requesting key " + securityListType.toString(), retrofitError);
        }
        return null;
    }

    protected List<SecurityCompactDTO> fetch(TrendingSecurityListType trendingSecurityListType) throws RetrofitError
    {
        return securityService.get().getTrendingSecurities();
    }

    protected List<SecurityCompactDTO> fetch(SearchSecurityListType searchSecurityListType) throws RetrofitError
    {
        return securityService.get().searchSecurities(
                searchSecurityListType.getSearchString(),
                searchSecurityListType.getPage(),
                searchSecurityListType.getPerPage());
    }

    protected List<SecurityId> putInternal(SecurityListType securityListType, List<SecurityCompactDTO> securityCompactDTOs)
    {
        List<SecurityId> securityIds = null;
        if (securityCompactDTOs != null)
        {
            securityIds = new ArrayList<>();
            SecurityId securityId;
            for(SecurityCompactDTO securityCompactDTO: securityCompactDTOs)
            {
                securityId = securityCompactDTO.getSecurityId();
                securityIds.add(securityId);
                securityCompactCache.get().put(securityId, securityCompactDTO);
            }
            put(securityListType, securityIds);
        }
        return securityIds;
    }
}
