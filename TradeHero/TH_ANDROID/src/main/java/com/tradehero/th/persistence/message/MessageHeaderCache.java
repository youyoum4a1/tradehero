package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTOList;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import java.util.Collection;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MessageHeaderCache extends StraightDTOCache<MessageHeaderId, MessageHeaderDTO>
{
    MessageServiceWrapper messageServiceWrapper;
    UserBaseKey userBaseKey;

    @Inject
    public MessageHeaderCache(@SingleCacheMaxSize IntPreference maxSize, MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageServiceWrapper = messageServiceWrapper;
    }

    // TODO implement a fetch on server side
    @Override protected MessageHeaderDTO fetch(MessageHeaderId key) throws Throwable
    {
        return messageServiceWrapper.getMessageHeader(key.key, userBaseKey);
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

    public void setUserBaseKey(UserBaseKey userBaseKey)
    {
        this.userBaseKey = userBaseKey;
    }
}
