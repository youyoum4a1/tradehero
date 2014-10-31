package com.tradehero.th.persistence.alert;

import com.tradehero.common.persistence.BaseDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @UserCache
public class AlertCompactCacheRx extends BaseDTOCacheRx<AlertId, AlertCompactDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;
    public static final int DEFAULT_MAX_SUBJECT_SIZE = 10;

    @NotNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCompactCacheRx(
            @NotNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, DEFAULT_MAX_SUBJECT_SIZE, dtoCacheUtil);
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @Override public void onNext(@NotNull AlertId key, @NotNull AlertCompactDTO value)
    {
        if (value.security != null)
        {
            securityCompactCache.get().onNext(value.security.getSecurityId(), value.security);
        }
        super.onNext(key, value);
    }

    public void onNext(@NotNull UserBaseKey userBaseKey, @NotNull List<AlertCompactDTO> values)
    {
        for (@NotNull AlertCompactDTO alertCompactDTO : values)
        {
            onNext(new AlertId(userBaseKey, alertCompactDTO.id), alertCompactDTO);
        }
    }
}