package com.androidth.general.models.discussion;

import com.androidth.general.base.THApp;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.persistence.message.MessageHeaderListCacheRx;
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
