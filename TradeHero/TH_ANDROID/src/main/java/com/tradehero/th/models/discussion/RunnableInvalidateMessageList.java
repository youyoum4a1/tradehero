package com.tradehero.th.models.discussion;

import com.tradehero.th.persistence.message.MessageHeaderListCacheRx;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class RunnableInvalidateMessageList implements Runnable
{
    @Inject @NonNull MessageHeaderListCacheRx messageHeaderListCache;

    //<editor-fold desc="Constructors">
    @Inject public RunnableInvalidateMessageList(
            @NonNull MessageHeaderListCacheRx messageHeaderListCache)
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
