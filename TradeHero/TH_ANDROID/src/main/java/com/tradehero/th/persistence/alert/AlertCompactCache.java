package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.StraightCutDTOCacheNew;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class AlertCompactCache extends StraightCutDTOCacheNew<AlertId, AlertCompactDTO, AlertCompactCutDTO>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    @NotNull private final Lazy<SecurityCompactCache> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCompactCache(@NotNull Lazy<SecurityCompactCache> securityCompactCache)
    {
        this(DEFAULT_MAX_SIZE, securityCompactCache);
    }
    
    public AlertCompactCache(int maxSize, @NotNull Lazy<SecurityCompactCache> securityCompactCache)
    {
        super(maxSize);
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @Override public AlertCompactDTO fetch(@NotNull AlertId key) throws Throwable
    {
        throw new IllegalStateException("No fetcher on this cache");
    }

    @Override @NotNull protected AlertCompactCutDTO cutValue(
            @NotNull AlertId key,
            @NotNull AlertCompactDTO value)
    {
        return new AlertCompactCutDTO(value, securityCompactCache.get());
    }

    @Override @Nullable protected AlertCompactDTO inflateValue(@NotNull AlertId key, @Nullable AlertCompactCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        return cutValue.create(securityCompactCache.get());
    }

    @Contract("_, null -> null; _, !null -> !null")
    public ArrayList<AlertCompactDTO> put(@NotNull UserBaseKey userBaseKey, @Nullable List<AlertCompactDTO> values)
    {
        if (values == null)
        {
            return null;
        }

        ArrayList<AlertCompactDTO> previous = new ArrayList<>();
        for (AlertCompactDTO alertCompactDTO : values)
        {
            previous.add(put(new AlertId(userBaseKey, alertCompactDTO.id), alertCompactDTO));
        }
        return previous;
    }
}