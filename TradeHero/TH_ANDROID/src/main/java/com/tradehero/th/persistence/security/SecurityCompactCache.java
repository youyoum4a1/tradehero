package com.tradehero.th.persistence.security;

import android.support.v4.util.LruCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.persistence.DTOCache;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton
public class SecurityCompactCache implements DTOCache<String, SecurityId, SecurityCompactDTO>
{
    public static final String TAG = SecurityCompactCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    private LruCache<String, SecurityCompactDTO> lruCache;
    @Inject protected Lazy<SecurityService> securityService;
    @Inject protected Lazy<SecurityPositionDetailCache> securityPositionDetailCache;

    //<editor-fold desc="Constructors">
    public SecurityCompactCache()
    {
        this(DEFAULT_MAX_SIZE);
    }

    public SecurityCompactCache(int maxSize)
    {
        this.lruCache = new LruCache<>(maxSize);
    }
    //</editor-fold>

    public SecurityCompactDTO getOrFetch(SecurityId securityId, boolean force)
    {
        SecurityCompactDTO securityCompactDTO = get(securityId);

        if (force || securityCompactDTO == null)
        {
            SecurityPositionDetailDTO securityPositionDetailDTO = null;
            try
            {
                securityPositionDetailDTO = securityService.get().getSecurity(securityId.exchange, securityId.securitySymbol);
            }
            catch (RetrofitError retrofitError)
            {
                BasicRetrofitErrorHandler.handle(retrofitError);
                THLog.e(TAG, "Error requesting key " + securityId.toString(), retrofitError);
            }

            if (securityPositionDetailDTO != null)
            {
                securityPositionDetailCache.get().put(securityId, securityPositionDetailDTO);
                securityCompactDTO = securityPositionDetailDTO.security;
                put(securityId, securityCompactDTO);
            }
        }

        return securityCompactDTO;
    }

    @Override public SecurityCompactDTO get(SecurityId key)
    {
        return this.lruCache.get(key.makeKey());
    }

    @Override public SecurityCompactDTO put(SecurityId key, SecurityCompactDTO value)
    {
        return this.lruCache.put(key.makeKey(), value);
    }
}
