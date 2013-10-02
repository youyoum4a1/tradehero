package com.tradehero.th.persistence.security;

import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.position.AbstractSecurityPositionDetailStore;
import com.tradehero.th.persistence.position.SecurityPositionDetailQuery;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 9/26/13 Time: 5:36 PM Copyright (c) TradeHero */
@Singleton
public class SecurityStoreManager
{
    @Inject DatabaseCache dbCache;
    @Inject AbstractSecurityPositionDetailStore securityPositionDetailStore;
    @Inject AbstractSecurityCompactStore securityCompactStore;

    private ReentrantLock positionDetailLock = new ReentrantLock();
    private ReentrantLock compactLock = new ReentrantLock();

    public SecurityPositionDetailDTO getPositionDetail(SecurityId securityId, boolean forceReload) throws IOException
    {
        positionDetailLock.lock();
        try
        {
            return getPositionDetailInternal(new SecurityPositionDetailQuery(securityId), forceReload);
        }
        finally
        {
            positionDetailLock.unlock();
        }
    }

    private SecurityPositionDetailDTO getPositionDetailInternal(SecurityPositionDetailQuery query, boolean forceReload) throws IOException
    {
        securityPositionDetailStore.setQuery(query);
        return (forceReload ? dbCache.requestAndStore(securityPositionDetailStore) : dbCache.loadOrRequest(securityPositionDetailStore)).get(0);
    }

    public List<SecurityCompactDTO> getTrending(boolean forceReload) throws IOException
    {
        compactLock.lock();
        try
        {
            return getCompactsInternal(new SecurityTrendingQuery(), forceReload);
        }
        finally
        {
            compactLock.unlock();
        }
    }

    public List<SecurityCompactDTO> searchCompacts(SecuritySearchQuery query, boolean forceReload) throws IOException
    {
        // TODO avoid locking this way, in order to improve reactivity.
        compactLock.lock();
        try
        {
            return getCompactsInternal(query, forceReload);
        }
        finally
        {
            compactLock.unlock();
        }
    }

    private List<SecurityCompactDTO> getCompactsInternal(SecurityQuery query, boolean forceReload) throws IOException
    {
        securityCompactStore.setQuery(query);
        return forceReload ? dbCache.requestAndStore(securityCompactStore) : dbCache.loadOrRequest(securityCompactStore);
    }
}
