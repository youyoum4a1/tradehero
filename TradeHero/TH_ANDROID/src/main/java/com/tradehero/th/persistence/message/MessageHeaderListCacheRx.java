package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.BaseFetchDTOCacheRx;
import com.tradehero.common.persistence.DTOCacheUtilRx;
import com.tradehero.common.persistence.UserCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.common.utils.CollectionUtils;
import com.tradehero.th.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.RecipientTypedMessageListKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;

@Singleton @UserCache
public class MessageHeaderListCacheRx extends BaseFetchDTOCacheRx<MessageListKey, ReadablePaginatedMessageHeaderDTO>
{
    @NotNull final private MessageHeaderCacheRx messageHeaderCache;
    @NotNull final private MessageServiceWrapper messageServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public MessageHeaderListCacheRx(
            @ListCacheMaxSize IntPreference maxSize,
            @NotNull MessageHeaderCacheRx messageHeaderCache,
            @NotNull MessageServiceWrapper messageServiceWrapper,
            @NotNull DTOCacheUtilRx dtoCacheUtil)
    {
        super(maxSize.get(), 5, 5, dtoCacheUtil);
        this.messageHeaderCache = messageHeaderCache;
        this.messageServiceWrapper = messageServiceWrapper;
    }
    //</editor-fold>

    @Override @NotNull protected Observable<ReadablePaginatedMessageHeaderDTO> fetch(@NotNull MessageListKey key)
    {
        return messageServiceWrapper.getMessageHeadersRx(key);
    }

    @Override public void onNext(@NotNull MessageListKey key, @NotNull ReadablePaginatedMessageHeaderDTO value)
    {
        messageHeaderCache.onNext(value.getData());
        super.onNext(key, value);
    }

    public void invalidateWithRecipient(@Nullable UserBaseKey userBaseKey)
    {
        for (MessageListKey messageListKey : new ArrayList<>(snapshot().keySet()))
        {
            if (messageListKey instanceof RecipientTypedMessageListKey &&
                    ((RecipientTypedMessageListKey) messageListKey).recipientId.equals(userBaseKey))
            {
                invalidate(messageListKey);
            }
        }
    }

    /**
     * Invalidate the keys where the parameter is listed in the value.
     * @param messageHeaderId
     */
    public void invalidateKeysThatList(@NotNull MessageHeaderId messageHeaderId)
    {
        for (Map.Entry<MessageListKey, ReadablePaginatedMessageHeaderDTO> entry : new HashMap<>(snapshot()).entrySet())
        {
            if (CollectionUtils.contains(
                    entry.getValue().getData(),
                    value -> value.getDTOKey().equals(messageHeaderId)))
            {
                invalidateSameListing(entry.getKey());
            }
        }
    }

    /**
     * Invalidates the keys that are part of the same listing as the key.
     * @param key
     */
    public void invalidateSameListing(@NotNull MessageListKey key)
    {
        for (MessageListKey entry : new ArrayList<>(snapshot().keySet()))
        {
            if (key.equalListing(entry))
            {
                invalidate(entry);
            }
        }
    }
}
