package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.RecipientTypedMessageListKey;
import com.tradehero.th.api.pagination.ReadablePaginatedDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class MessageHeaderListCache extends StraightDTOCacheNew<MessageListKey, MessageHeaderIdList>
{
    @NotNull final private MessageHeaderCache messageHeaderCache;
    @NotNull final private MessageServiceWrapper messageServiceWrapper;
    @NotNull final private UserProfileCache userProfileCache;
    @NotNull final private CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public MessageHeaderListCache(
            @ListCacheMaxSize IntPreference maxSize,
            @NotNull MessageHeaderCache messageHeaderCache,
            @NotNull MessageServiceWrapper messageServiceWrapper,
            @NotNull UserProfileCache userProfileCache,
            @NotNull CurrentUserId currentUserId)
    {
        super(maxSize.get());
        this.messageHeaderCache = messageHeaderCache;
        this.messageServiceWrapper = messageServiceWrapper;
        this.userProfileCache = userProfileCache;
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @Override @NotNull public MessageHeaderIdList fetch(@NotNull MessageListKey key) throws Throwable
    {
        return putInternal(messageServiceWrapper.getMessageHeaders(key));
    }

    private MessageHeaderIdList putInternal(@Nullable ReadablePaginatedDTO<MessageHeaderDTO> data)
    {
        // update user profile cache
        updateUnreadMessageThreadCount(data);

        if (data != null && data.getData() != null)
        {
            List<MessageHeaderDTO> list = data.getData();

            MessageHeaderIdList keyList = new MessageHeaderIdList();
            for (MessageHeaderDTO messageHeaderDTO : list)
            {
                MessageHeaderId messageHeaderId = messageHeaderDTO.getDTOKey();
                keyList.add(messageHeaderId);
                messageHeaderCache.put(messageHeaderId, messageHeaderDTO);
            }

            return keyList;
        }

        return null;
    }

    /**
     *
     * @param data
     */
    private void updateUnreadMessageThreadCount(ReadablePaginatedDTO<MessageHeaderDTO> data)
    {
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null)
        {
            userProfileDTO.unreadMessageThreadsCount =  data.unread;
        }
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
        for (Map.Entry<MessageListKey, CacheValue<MessageListKey, MessageHeaderIdList>> entry : new HashMap<>(snapshot()).entrySet())
        {
            if (entry.getValue().getValue() != null && entry.getValue().getValue().contains(messageHeaderId))
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
