package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.PartialDTOCache;
import com.tradehero.common.persistence.THLruCache;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class AlertCompactCache extends PartialDTOCache<AlertId, AlertCompactDTO>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    private final THLruCache<AlertId, AlertCompactCutDTO> lruCache;
    @NotNull private final Lazy<SecurityCompactCache> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCompactCache(@NotNull Lazy<SecurityCompactCache> securityCompactCache)
    {
        this(DEFAULT_MAX_SIZE, securityCompactCache);
    }
    
    public AlertCompactCache(int maxSize, @NotNull Lazy<SecurityCompactCache> securityCompactCache)
    {
        super();
        lruCache = new THLruCache<>(maxSize);
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @Override protected AlertCompactDTO fetch(AlertId key) throws Throwable
    {
        throw new IllegalStateException("No fetcher on this cache");
    }

    @Override @Nullable public AlertCompactDTO get(@NotNull AlertId key)
    {
        AlertCompactCutDTO alertCompactCutDTO = this.lruCache.get(key);
        if (alertCompactCutDTO == null)
        {
            return null;
        }
        return alertCompactCutDTO.create(securityCompactCache.get());
    }

    @Override @Nullable public AlertCompactDTO put(@NotNull AlertId key, @NotNull AlertCompactDTO value)
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

    @Contract("_, null -> null; _, !null -> !null")
    public ArrayList<AlertCompactDTO> put(@NotNull UserBaseKey userBaseKey, @Nullable List<AlertCompactDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        ArrayList<AlertCompactDTO> previous = new ArrayList<>();
        for (AlertCompactDTO alertCompactDTO: values)
        {
            previous.add(put(new AlertId(userBaseKey, alertCompactDTO.id), alertCompactDTO));
        }
        return previous;
    }

    @Override public void invalidate(@NotNull AlertId key)
    {
        lruCache.remove(key);
    }

    @Override public void invalidateAll()
    {
        lruCache.evictAll();
    }

    private static class AlertCompactCutDTO
    {
        public final int id;
        public final double targetPrice;
        public final Boolean upOrDown;
        public final Double priceMovement;
        public final boolean active;
        public final Date activeUntilDate;
        @Nullable public final SecurityId securityId;

        public AlertCompactCutDTO(
                @NotNull AlertCompactDTO alertCompactDTO,
                @NotNull SecurityCompactCache securityCompactCache)
        {
            if (alertCompactDTO.security != null)
            {
                securityCompactCache.put(alertCompactDTO.security.getSecurityId(), alertCompactDTO.security);
                this.securityId = alertCompactDTO.security.getSecurityId();
            }
            else
            {
                this.securityId = null;
            }
            this.id = alertCompactDTO.id;
            this.targetPrice = alertCompactDTO.targetPrice;
            this.upOrDown = alertCompactDTO.upOrDown;
            this.priceMovement = alertCompactDTO.priceMovement;
            this.active = alertCompactDTO.active;
            this.activeUntilDate = alertCompactDTO.activeUntilDate;
        }

        public AlertCompactDTO create(@NotNull SecurityCompactCache securityCompactCache)
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
            else
            {
                compactDTO.security = null;
            }
            
            return compactDTO;
        }
    }
}
