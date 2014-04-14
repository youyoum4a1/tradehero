package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderIdList;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by WangLiang on 14-4-4.
 */
@Singleton
public class MessageHeaderListCache extends StraightDTOCache<MessageListKey, MessageHeaderIdList>
{
    private MessageHeaderCache messageHeaderCache;
    private MessageServiceWrapper messageServiceWrapper;
    private Set<Integer> deletedMessageIds;

    @Inject
    public MessageHeaderListCache(@ListCacheMaxSize IntPreference maxSize, MessageHeaderCache messageHeaderCache,
            MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageHeaderCache = messageHeaderCache;
        this.messageServiceWrapper = messageServiceWrapper;
    }

    /**
     * filter the deleted message before returning the data.
     * @param key
     * @return
     */
    @Override public MessageHeaderIdList get(MessageListKey key)
    {
        MessageHeaderIdList messageHeaderIds = super.get(key);
        if (messageHeaderIds != null && deletedMessageIds != null && deletedMessageIds.size() > 0)
        {
            MessageHeaderIdList filteredMessageHeaderIdList = new MessageHeaderIdList();
                for(MessageHeaderId id:messageHeaderIds)
            {
                if (!deletedMessageIds.contains(id.key))
                {
                    filteredMessageHeaderIdList.add(id);
                }

            }
            return filteredMessageHeaderIdList;
        }
        return messageHeaderIds;
    }

    @Override protected MessageHeaderIdList fetch(MessageListKey key) throws Throwable
    {
        PaginatedDTO<MessageHeaderDTO> data = messageServiceWrapper.getMessages(key);
        return putInternal(data);
    }

    private MessageHeaderIdList putInternal(PaginatedDTO<MessageHeaderDTO> data)
    {
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
     * Save the id of message that has been deleted.
     * @param messageId
     */
    public void markMessageDeleted(int messageId)
    {
        if (deletedMessageIds == null)
        {
            deletedMessageIds = new HashSet<>();
        }
        deletedMessageIds.add(messageId);

    }

    /**
     * Get the id list of messages which have been deleted.
     * @return
     */
    public Set<Integer> getDeletedMessageIds()
    {
        return deletedMessageIds;
    }
}
