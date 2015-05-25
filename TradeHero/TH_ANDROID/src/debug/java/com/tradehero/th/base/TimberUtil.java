package com.tradehero.th.base;

import com.tradehero.common.log.EasyDebugTree;
import timber.log.Timber;

public class TimberUtil
{
    public static Timber.Tree createTree()
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
