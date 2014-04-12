package com.tradehero.th.persistence.discussion;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.api.discussion.MessageStatusDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.MessageServiceWrapper;
import com.tradehero.th.persistence.SingleCacheMaxSize;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier2 on 2014/4/10.
 */
@Singleton public class MessageStatusCache extends StraightDTOCache<UserBaseKey, MessageStatusDTO>
{
    private MessageServiceWrapper messageServiceWrapper;

    @Inject public MessageStatusCache(
            @SingleCacheMaxSize IntPreference maxSize,
            MessageServiceWrapper messageServiceWrapper)
    {
        super(maxSize.get());
        this.messageServiceWrapper = messageServiceWrapper;
    }

    @Override protected MessageStatusDTO fetch(UserBaseKey key) throws Throwable
    {
        return messageServiceWrapper.getFreeCount(key);
    }
}
