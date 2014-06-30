package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class MessageHeaderCache extends StraightDTOCacheNew<MessageHeaderId, MessageHeaderDTO>
{
    @NotNull private final MessageServiceWrapper messageServiceWrapper;

    @Inject
    public MessageHeaderCache(
            @SingleCacheMaxSize IntPreference maxSize,
            @NotNull MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageServiceWrapper = messageServiceWrapper;
    }

    @Override @NotNull public MessageHeaderDTO fetch(@NotNull MessageHeaderId key) throws Throwable
    {
        return messageServiceWrapper.getMessageHeader(key);
    }
}
