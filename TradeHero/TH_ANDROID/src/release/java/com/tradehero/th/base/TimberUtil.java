package com.androidth.general.base;

import android.support.annotation.NonNull;
import com.androidth.general.common.log.CrashReportingTree;
import timber.log.Timber;

public class TimberUtil
{
    @NonNull public static Timber.Tree createTree()
    {
        return new CrashReportingTree();
    }
}
