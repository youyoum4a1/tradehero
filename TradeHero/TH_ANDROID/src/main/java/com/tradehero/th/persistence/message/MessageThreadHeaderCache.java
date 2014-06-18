package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class MessageThreadHeaderCache extends StraightDTOCache<UserBaseKey, MessageHeaderDTO>
{
    @NotNull private final MessageServiceWrapper messageServiceWrapper;
    @NotNull private final MessageHeaderCache messageHeaderCache;

    @Inject
    public MessageThreadHeaderCache(
            @SingleCacheMaxSize IntPreference maxSize,
            @NotNull MessageServiceWrapper messageServiceWrapper,
            @NotNull MessageHeaderCache messageHeaderCache)
    {
        super(maxSize.get());
        this.messageServiceWrapper = messageServiceWrapper;
        this.messageHeaderCache = messageHeaderCache;
    }

    @Override protected MessageHeaderDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return messageServiceWrapper.getMessageThread(key);
    }

    @Override public MessageHeaderDTO put(@NotNull UserBaseKey key, @NotNull MessageHeaderDTO value)
    {
        messageHeaderCache.put(value.getDTOKey(), value);
        return super.put(key, value);
    }
}
