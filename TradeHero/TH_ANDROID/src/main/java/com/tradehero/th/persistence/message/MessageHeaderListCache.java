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
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by wangliang on 14-4-4.
 */
@Singleton
public class MessageHeaderListCache extends StraightDTOCache<MessageListKey, MessageHeaderIdList>
{
    MessageHeaderCache messageHeaderCache;
    MessageServiceWrapper messageServiceWrapper;

    @Inject
    public MessageHeaderListCache(@ListCacheMaxSize IntPreference maxSize, MessageHeaderCache messageHeaderCache,
            MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageHeaderCache = messageHeaderCache;
        this.messageServiceWrapper = messageServiceWrapper;
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
}
