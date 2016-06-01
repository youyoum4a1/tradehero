package com.ayondo.academy.models.discussion;

import com.ayondo.academy.base.THApp;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.persistence.message.MessageHeaderListCacheRx;
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
