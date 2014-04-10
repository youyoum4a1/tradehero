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

/**
 * Created by wangliang on 14-4-4.
 *
 * message item
 */
@Singleton
public class MessageHeaderCache extends StraightDTOCache<MessageHeaderId, MessageHeaderDTO>
{
    MessageServiceWrapper messageServiceWrapper;

    @Inject
    public MessageHeaderCache(@SingleCacheMaxSize IntPreference maxSize, MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageServiceWrapper = messageServiceWrapper;
    }

    // TODO implement a fetch on server side
    @Override protected MessageHeaderDTO fetch(MessageHeaderId key) throws Throwable
    {
        throw new IllegalArgumentException("This cache has no service to fetch");
    }

    public MessageHeaderDTOList getMessages(Collection<MessageHeaderId> list)
    {
        if (list != null)
        {
            MessageHeaderDTOList result = new MessageHeaderDTOList(list.size());
            for (MessageHeaderId key : list)
            {
                MessageHeaderDTO messageHeaderDTO = get(key);
                result.add(messageHeaderDTO);
            }
            return result;
        }
        return null;
    }
}
