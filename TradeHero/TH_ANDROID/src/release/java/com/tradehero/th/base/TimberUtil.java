package com.tradehero.th.base;

import com.tradehero.common.log.CrashReportingTree;
import timber.log.Timber;

public class TimberUtil
{
    public static Timber.Tree createTree()
    {
        return new CrashReportingTree();
    }
}
