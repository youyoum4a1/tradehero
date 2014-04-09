package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.messages.MessageDTO;
import com.tradehero.th.api.messages.MessageKey;
import com.tradehero.th.api.messages.MessageKeyList;
import com.tradehero.th.api.messages.PagedTypeMessageKey;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.api.notification.NotificationKeyList;
import com.tradehero.th.api.notification.NotificationListKey;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.ListCacheMaxSize;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by wangliang on 14-4-4.
 */
@Singleton
public class MessageListCache extends StraightDTOCache<PagedTypeMessageKey, MessageKeyList>
{

    static final int PER_PAGE = 42;
    MessageItemCache messageItemCache;
    MessageServiceWrapper messageServiceWrapper;


    @Inject
    public MessageListCache(@ListCacheMaxSize IntPreference maxSize,MessageItemCache messageItemCache,MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageItemCache = messageItemCache;
        this.messageServiceWrapper = messageServiceWrapper;
    }

    @Override protected MessageKeyList fetch(PagedTypeMessageKey key) throws Throwable
    {
        PaginatedDTO<MessageDTO> data = messageServiceWrapper.getMessages(key.getPage(), PER_PAGE);
        return putInernal(data);
    }


    private MessageKeyList putInernal(PaginatedDTO<MessageDTO> data)
    {
        if (data != null && data.getData() != null)
        {
            List<MessageDTO> list = data.getData();

            MessageKeyList keyList = new MessageKeyList();
            for (MessageDTO messageDTO: list)
            {
                MessageKey messageKey = messageDTO.getDTOKey();
                keyList.add(messageKey);

                messageItemCache.put(messageKey, messageDTO);
            }

            return keyList;
        }

        return null;

    }
}
