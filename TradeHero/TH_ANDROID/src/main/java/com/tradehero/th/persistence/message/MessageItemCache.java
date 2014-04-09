package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.messages.MessageDTO;
import com.tradehero.th.api.messages.MessageKey;
import com.tradehero.th.api.messages.MessageKeyList;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.api.notification.NotificationKey;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by wangliang on 14-4-4.
 *
 * message item
 */
@Singleton
public class MessageItemCache extends StraightDTOCache<MessageKey, MessageDTO>
{
    MessageServiceWrapper messageServiceWrapper;

    @Inject
    public MessageItemCache(@SingleCacheMaxSize IntPreference maxSize,MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageServiceWrapper = messageServiceWrapper;
    }

    @Override protected MessageDTO fetch(MessageKey key) throws Throwable
    {
        return null;
    }

    public List<MessageDTO> getMessages(Collection<MessageKey> list){
        if (list != null)
        {
            List<MessageDTO> result = new ArrayList<>(list.size());
            for(MessageKey key:list)
            {
                MessageDTO messageDTO = get(key);
                result.add(messageDTO);
            }
            return result;
        }
        return null;
    }
}
