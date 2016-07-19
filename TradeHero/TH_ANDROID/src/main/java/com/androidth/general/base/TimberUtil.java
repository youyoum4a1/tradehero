package com.androidth.general.base;

/**
 * Created by jeffgan on 18/7/16.
 */
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
