package com.tradehero.th.base;

import android.support.annotation.NonNull;
import com.tradehero.common.log.CrashReportingTree;
import timber.log.Timber;

public class TimberUtil
{
    @NonNull public static Timber.Tree createTree()
    {
        return new CrashReportingTree();
    }
}
