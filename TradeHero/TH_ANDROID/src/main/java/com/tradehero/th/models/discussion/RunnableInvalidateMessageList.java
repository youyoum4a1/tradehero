package com.tradehero.th.models.discussion;

import com.tradehero.th.persistence.message.MessageHeaderListCacheRx;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class RunnableInvalidateMessageList implements Runnable
{
    @Inject @NotNull MessageHeaderListCacheRx messageHeaderListCache;

    //<editor-fold desc="Constructors">
    @Inject public RunnableInvalidateMessageList(
            @NotNull MessageHeaderListCacheRx messageHeaderListCache)
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
