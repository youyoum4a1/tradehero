package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageDTO;
import com.tradehero.th.api.discussion.key.MessageId;
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
public class MessageItemCache extends StraightDTOCache<MessageId, MessageDTO>
{
    MessageServiceWrapper messageServiceWrapper;

    @Inject
    public MessageItemCache(@SingleCacheMaxSize IntPreference maxSize, MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageServiceWrapper = messageServiceWrapper;
    }

    // TODO implement a fetch on server side
    @Override protected MessageDTO fetch(MessageId key) throws Throwable
    {
        throw new IllegalArgumentException("This cache has no service to fetch");
    }

    public List<MessageDTO> getMessages(Collection<MessageId> list)
    {
        if (list != null)
        {
            List<MessageDTO> result = new ArrayList<>(list.size());
            for (MessageId key : list)
            {
                MessageDTO messageDTO = get(key);
                result.add(messageDTO);
            }
            return result;
        }
        return null;
    }
}
