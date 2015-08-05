package com.tradehero.common.log;

import javax.inject.Inject;
import timber.log.Timber;

public class SystemOutTree extends Timber.Tree
{
    //<editor-fold desc="Constructors">
    @Inject public SystemOutTree()
    {
        super();
    }
    //</editor-fold>

    @Override protected void log(int priority, String tag, String message, Throwable throwable)
    {
        System.out.println(priority + "," + tag + ": " + message);
        if (throwable != null)
        {
            throwable.printStackTrace(System.out);
        }
    }
}
