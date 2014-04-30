package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.RecipientTypedMessageListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.updatecenter.messages.MessagePaginatedDTO;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import com.tradehero.th.persistence.user.UserProfileCache;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MessageHeaderListCache extends StraightDTOCache<MessageListKey, MessageHeaderIdList>
{
    private MessageHeaderCache messageHeaderCache;
    private MessageServiceWrapper messageServiceWrapper;
    private UserProfileCache userProfileCache;
    private CurrentUserId currentUserId;

    @Inject
    public MessageHeaderListCache(@ListCacheMaxSize IntPreference maxSize, MessageHeaderCache messageHeaderCache,
            MessageServiceWrapper messageServiceWrapper,UserProfileCache userProfileCache,CurrentUserId currentUserId)
    {
        super(maxSize.get());
        this.messageHeaderCache = messageHeaderCache;
        this.messageServiceWrapper = messageServiceWrapper;
        this.userProfileCache = userProfileCache;
        this.currentUserId = currentUserId;
    }

    @Override protected MessageHeaderIdList fetch(MessageListKey key) throws Throwable
    {
        PaginatedDTO<MessageHeaderDTO> data = messageServiceWrapper.getMessageHeaders(key);
        return putInternal(data);
    }

    private MessageHeaderIdList putInternal(PaginatedDTO<MessageHeaderDTO> data)
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
    private void updateUnreadMessageThreadCount(PaginatedDTO<MessageHeaderDTO> data)
    {
        if (data == null || !(data instanceof MessagePaginatedDTO))
        {
            return;
        }
        MessagePaginatedDTO messagePaginatedDTO = (MessagePaginatedDTO)data;
        if (userProfileCache != null && currentUserId != null)
        {
            UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
            if (userProfileDTO != null)
            {
                userProfileDTO.unreadMessageThreadsCount =  messagePaginatedDTO.unread;
            }
        }
    }

    public void invalidateWithRecipient(UserBaseKey userBaseKey)
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
    public void invalidateKeysThatList(MessageHeaderId messageHeaderId)
    {
        for (Map.Entry<MessageListKey, MessageHeaderIdList> entry : new HashMap<>(snapshot()).entrySet())
        {
            if (entry.getValue().contains(messageHeaderId))
            {
                invalidateSameListing(entry.getKey());
            }
        }
    }

    /**
     * Invalidates the keys that are part of the same listing as the key.
     * @param key
     */
    public void invalidateSameListing(MessageListKey key)
    {
        for (MessageListKey entry : new ArrayList<MessageListKey>(snapshot().keySet()))
        {
            if (key.equalListing(entry))
            {
                invalidate(entry);
            }
        }
    }
}
