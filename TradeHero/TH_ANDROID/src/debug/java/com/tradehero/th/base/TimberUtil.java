package com.ayondo.academy.base;

import android.support.annotation.NonNull;
import timber.log.Timber;

public class TimberUtil
{
    @NonNull public static Timber.Tree createTree()
    {
        return new Timber.DebugTree();
    }
}
