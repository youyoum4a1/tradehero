package com.tradehero.th.models.discussion;

import com.tradehero.th.base.THApp;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.message.MessageHeaderListCacheRx;
import javax.inject.Inject;

public class RunnableInvalidateMessageList implements Runnable
{
    @Inject MessageHeaderListCacheRx messageHeaderListCache;

    //<editor-fold desc="Constructors">
    public RunnableInvalidateMessageList()
    {
        HierarchyInjector.inject(THApp.context(), this);
    }
    //</editor-fold>

    @Override public void run()
    {
        messageHeaderListCache.invalidateAll();
    }
}
