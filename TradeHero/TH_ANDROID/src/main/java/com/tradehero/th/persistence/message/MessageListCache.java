package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.MessageDTO;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.MessageId;
import com.tradehero.th.api.discussion.MessageIdList;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by wangliang on 14-4-4.
 */
@Singleton
public class MessageListCache extends StraightDTOCache<MessageListKey, MessageIdList>
{
    MessageItemCache messageItemCache;
    MessageServiceWrapper messageServiceWrapper;

    @Inject
    public MessageListCache(@ListCacheMaxSize IntPreference maxSize, MessageItemCache messageItemCache, MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageItemCache = messageItemCache;
        this.messageServiceWrapper = messageServiceWrapper;
    }

    @Override protected MessageIdList fetch(MessageListKey key) throws Throwable
    {
        PaginatedDTO<MessageDTO> data = messageServiceWrapper.getMessages(key);
        return putInternal(data);
    }

    private MessageIdList putInternal(PaginatedDTO<MessageDTO> data)
    {
        if (data != null && data.getData() != null)
        {
            List<MessageDTO> list = data.getData();

            MessageIdList keyList = new MessageIdList();
            for (MessageDTO messageDTO : list)
            {
                MessageId messageId = messageDTO.getDTOKey();
                keyList.add(messageId);
                messageItemCache.put(messageId, messageDTO);
            }

            return keyList;
        }

        return null;
    }
}
