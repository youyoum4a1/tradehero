package com.tradehero.th.base;

import android.app.Application;
import android.support.annotation.NonNull;
import com.tradehero.common.log.EasyDebugTree;
import timber.log.Timber;

public class TimberUtil
{
    public static Timber.Tree createTree(@NonNull Application application)
    {
        return new EasyDebugTree()
        {
            @Override public String createTag()
            {
                return String.format("TradeHero-%s", super.createTag());
            }
        };

    }
}
