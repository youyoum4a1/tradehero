package com.tradehero.th.persistence.alert;

import android.support.v4.util.LruCache;
import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.util.Date;
import javax.inject.Inject;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:40 PM To change this template use File | Settings | File Templates. */
@Singleton public class AlertCompactCache extends PartialDTOCache<AlertId, AlertCompactDTO>
{
    public static final String TAG = AlertCompactCache.class.getSimpleName();
    public static final int DEFAULT_MAX_SIZE = 100;

    private THLruCache<AlertId, AlertCompactCutDTO> lruCache;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCompactCache()
    {
        this(DEFAULT_MAX_SIZE);
    }
    
    public AlertCompactCache(int maxSize)
    {
        super();
        lruCache = new THLruCache<>(maxSize);
    }
    //</editor-fold>

    @Override protected AlertCompactDTO fetch(AlertId key) throws Throwable
    {
        throw new IllegalStateException("No fetcher on this cache");
    }

    @Override public AlertCompactDTO get(AlertId key)
    {
        AlertCompactCutDTO alertCompactCutDTO = this.lruCache.get(key);
        if (alertCompactCutDTO == null)
        {
            return null;
        }
        return alertCompactCutDTO.create(securityCompactCache.get());
    }

    @Override public AlertCompactDTO put(AlertId key, AlertCompactDTO value)
    {
        AlertCompactDTO previous = null;

        if (value.security != null)
        {
            securityCompactCache.get().put(value.security.getSecurityId(), value.security);
        }

        AlertCompactCutDTO previousCut = lruCache.put(
                key,
                new AlertCompactCutDTO(value, securityCompactCache.get()));

        if (previousCut != null)
        {
            previous = previousCut.create(securityCompactCache.get());
        }

        return previous;
    }

    @Override public void invalidate(AlertId key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    private static class AlertCompactCutDTO
    {
        public int id;
        public double targetPrice;
        public Boolean upOrDown;
        public Double priceMovement;
        public boolean active;
        public Date activeUntilDate;

        public SecurityId securityId;

        public AlertCompactCutDTO(AlertCompactDTO alertCompactDTO, SecurityCompactCache securityCompactCache)
        {
            if (alertCompactDTO.security != null)
            {
                securityCompactCache.put(alertCompactDTO.security.getSecurityId(), alertCompactDTO.security);
                this.securityId = alertCompactDTO.security.getSecurityId();
            }
            this.id = alertCompactDTO.id;
            this.targetPrice = alertCompactDTO.targetPrice;
            this.upOrDown = alertCompactDTO.upOrDown;
            this.priceMovement = alertCompactDTO.priceMovement;
            this.active = alertCompactDTO.active;
            this.activeUntilDate = alertCompactDTO.activeUntilDate;
        }

        public AlertCompactDTO create(SecurityCompactCache securityCompactCache)
        {
            AlertCompactDTO compactDTO = new AlertCompactDTO();
            compactDTO.id = this.id;
            compactDTO.targetPrice = this.targetPrice;
            compactDTO.upOrDown = this.upOrDown;
            compactDTO.priceMovement = this.priceMovement;
            compactDTO.active = this.active;
            compactDTO.activeUntilDate = this.activeUntilDate;
            if (securityId != null)
            {
                compactDTO.security = securityCompactCache.get(securityId);
            }
            
            return compactDTO;
        }
    }
}
