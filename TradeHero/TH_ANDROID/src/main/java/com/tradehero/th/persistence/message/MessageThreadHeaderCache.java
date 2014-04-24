package com.tradehero.th.persistence.message;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MessageThreadHeaderCache extends StraightDTOCache<UserBaseKey, MessageHeaderDTO>
{
    MessageServiceWrapper messageServiceWrapper;

    @Inject
    public MessageThreadHeaderCache(@SingleCacheMaxSize IntPreference maxSize,
            MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageServiceWrapper = messageServiceWrapper;
    }

    @Override protected MessageHeaderDTO fetch(UserBaseKey key) throws Throwable
    {
        return messageServiceWrapper.getMessageThread(key);
    }
}
