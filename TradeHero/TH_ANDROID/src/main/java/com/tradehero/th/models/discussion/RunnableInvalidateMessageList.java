package com.tradehero.th.models.discussion;

import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class RunnableInvalidateMessageList implements Runnable
{
    @Inject MessageHeaderListCache messageHeaderListCache;

    //<editor-fold desc="Constructors">
    @Inject public RunnableInvalidateMessageList(
            MessageHeaderListCache messageHeaderListCache)
    {
        this.messageHeaderListCache = messageHeaderListCache;
    }

    public RunnableInvalidateMessageList()
    {
        DaggerUtils.inject(this);
    }
    //</editor-fold>

    @Override public void run()
    {
        messageHeaderListCache.invalidateAll();
    }
}
