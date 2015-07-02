package com.tradehero.th.persistence.live;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.th.api.live.IdentityPromptInfoDTO;
import com.tradehero.th.api.live.IdentityPromptInfoKey;
import com.tradehero.th.network.service.LiveServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class IdentityPromptInfoCache extends BaseFetchDTOCacheRx<IdentityPromptInfoKey, IdentityPromptInfoDTO>
{
    private static final int DEFAULT_SIZE = 5;
    private final LiveServiceWrapper liveServiceWrapper;

    @Inject public IdentityPromptInfoCache(@NonNull LiveServiceWrapper liveServiceWrapper, @NonNull DTOCacheUtilRx dtoCacheUtilRx)
    {
        super(DEFAULT_SIZE, dtoCacheUtilRx);
        this.liveServiceWrapper = liveServiceWrapper;
    }

    @NonNull @Override protected Observable<IdentityPromptInfoDTO> fetch(@NonNull IdentityPromptInfoKey key)
    {
        return liveServiceWrapper.getIdentityPromptInfo(key);
    }
}
