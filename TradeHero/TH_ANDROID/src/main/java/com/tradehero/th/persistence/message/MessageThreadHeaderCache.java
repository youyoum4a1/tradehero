package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.DTOCacheUtilNew;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton @UserCache
public class MessageThreadHeaderCache extends StraightDTOCacheNew<UserBaseKey, MessageHeaderDTO>
{
    @NotNull private final MessageServiceWrapper messageServiceWrapper;
    @NotNull private final MessageHeaderCacheRx messageHeaderCache;

    //<editor-fold desc="Constructors">
    @Inject public MessageThreadHeaderCache(
            @SingleCacheMaxSize IntPreference maxSize,
            @NotNull MessageServiceWrapper messageServiceWrapper,
            @NotNull MessageHeaderCacheRx messageHeaderCache,
            @NotNull DTOCacheUtilNew dtoCacheUtil)
    {
        super(maxSize.get(), dtoCacheUtil);
        this.messageServiceWrapper = messageServiceWrapper;
        this.messageHeaderCache = messageHeaderCache;
    }
    //</editor-fold>

    @Override @NotNull public MessageHeaderDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return messageServiceWrapper.getMessageThread(key);
    }

    @Override public MessageHeaderDTO put(@NotNull UserBaseKey key, @NotNull MessageHeaderDTO value)
    {
        messageHeaderCache.onNext(value.getDTOKey(), value);
        return super.put(key, value);
    }
}
