package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTOList;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class MessageHeaderCache extends StraightDTOCache<MessageHeaderId, MessageHeaderDTO>
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

    @Override protected MessageHeaderDTO fetch(@NotNull MessageHeaderId key) throws Throwable
    {
        return messageServiceWrapper.getMessageHeader(key);
    }

    @Contract("null -> null; !null -> !null")
    public MessageHeaderDTOList getMessages(@Nullable Collection<MessageHeaderId> list)
    {
        if (list != null)
        {
            MessageHeaderDTOList result = new MessageHeaderDTOList(list.size());
            for (@NotNull MessageHeaderId key : list)
            {
                MessageHeaderDTO messageHeaderDTO = get(key);
                result.add(messageHeaderDTO);
            }
            return result;
        }
        return null;
    }
}
