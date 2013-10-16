package com.tradehero.th.persistence.security;

import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.BasicRetrofitErrorHandler;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class SecurityCompactCache extends StraightDTOCache<String, SecurityId, SecurityCompactDTO>
{
    public static final String TAG = SecurityCompactCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 1000;

    @Inject protected Lazy<SecurityService> securityService;
    @Inject protected Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject protected Lazy<SecurityIdCache> securityIdCache;

    //<editor-fold desc="Constructors">
    @Inject public SecurityCompactCache()
    {
        super(DEFAULT_MAX_SIZE);
    }
    //</editor-fold>

    @Override protected SecurityCompactDTO fetch(SecurityId key)
    {
        SecurityCompactDTO securityCompactDTO = null;
        SecurityPositionDetailDTO securityPositionDetailDTO = null;
        try
        {
            securityPositionDetailDTO = securityService.get().getSecurity(key.exchange, key.securitySymbol);
        }
        catch (RetrofitError retrofitError)
        {
            BasicRetrofitErrorHandler.handle(retrofitError);
            THLog.e(TAG, "Error requesting key " + key.toString(), retrofitError);
        }

        if (securityPositionDetailDTO != null)
        {
            securityPositionDetailCache.get().put(key, securityPositionDetailDTO);
            securityCompactDTO = securityPositionDetailDTO.security;
        }

        return securityCompactDTO;
    }

    public List<SecurityCompactDTO> getOrFetch(List<SecurityId> securityIds)
    {
        if (securityIds == null)
        {
            return null;
        }

        List<SecurityCompactDTO> securityCompactDTOList = new ArrayList<>();
        if (securityIds != null)
        {
            for(SecurityId securityId: securityIds)
            {
                securityCompactDTOList.add(getOrFetch(securityId, false));
            }
        }
        return securityCompactDTOList;
    }

    @Override public SecurityCompactDTO put(SecurityId key, SecurityCompactDTO value)
    {
        // We save the correspondence between int id and exchange/symbol for future reference
        securityIdCache.get().put(value.getSecurityIntegerId(), key);

        return super.put(key, value);
    }
}
