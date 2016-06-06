package com.androidth.general.persistence.alert;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.alert.AlertId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @UserCache
public class AlertCompactCacheRx extends BaseDTOCacheRx<AlertId, AlertCompactDTO>
{
    public static final int DEFAULT_MAX_VALUE_SIZE = 100;

    @NonNull private final Lazy<SecurityCompactCacheRx> securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject public AlertCompactCacheRx(
            @NonNull Lazy<SecurityCompactCacheRx> securityCompactCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(DEFAULT_MAX_VALUE_SIZE, dtoCacheUtil);
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @Override public void onNext(@NonNull AlertId key, @NonNull AlertCompactDTO value)
    {
        if (value.security != null)
        {
            securityCompactCache.get().onNext(value.security.getSecurityId(), value.security);
        }
        super.onNext(key, value);
    }

    public void onNext(@NonNull UserBaseKey userBaseKey, @NonNull List<AlertCompactDTO> values)
    {
        for (AlertCompactDTO alertCompactDTO : values)
        {
            onNext(new AlertId(userBaseKey, alertCompactDTO.id), alertCompactDTO);
        }
    }
}