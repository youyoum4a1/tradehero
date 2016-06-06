package com.androidth.general.persistence.message;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.BaseFetchDTOCacheRx;
import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.common.persistence.UserCache;
import com.androidth.general.common.persistence.prefs.IntPreference;
import com.androidth.general.api.discussion.MessageHeaderDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.network.service.MessageServiceWrapper;
import com.androidth.general.persistence.SingleCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton @UserCache
public class MessageThreadHeaderCacheRx extends BaseFetchDTOCacheRx<UserBaseKey, MessageHeaderDTO>
{
    @NonNull private final MessageServiceWrapper messageServiceWrapper;
    @NonNull private final MessageHeaderCacheRx messageHeaderCache;

    //<editor-fold desc="Constructors">
    @Inject public MessageThreadHeaderCacheRx(
            @SingleCacheMaxSize IntPreference maxSize,
            @NonNull MessageServiceWrapper messageServiceWrapper,
            @NonNull MessageHeaderCacheRx messageHeaderCache,
            @NonNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize.get(), dtoCacheUtil);
        this.messageServiceWrapper = messageServiceWrapper;
        this.messageHeaderCache = messageHeaderCache;
    }
    //</editor-fold>

    @Override @NonNull protected Observable<MessageHeaderDTO> fetch(@NonNull UserBaseKey key)
    {
        return messageServiceWrapper.getMessageThreadRx(key);
    }

    @Override public void onNext(@NonNull UserBaseKey key, @NonNull MessageHeaderDTO value)
    {
        messageHeaderCache.onNext(value.getDTOKey(), value);
        super.onNext(key, value);
    }
}
