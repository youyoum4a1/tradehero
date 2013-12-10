package com.tradehero.common.milestone;

import android.content.Context;

/**
 * Created with IntelliJ IDEA. User: tho Date: 12/10/13 Time: 2:34 PM Copyright (c) TradeHero
 */
public abstract class ContextMilestone extends BaseMilestone
{
    private final Context context;

    public ContextMilestone(Context context)
    {
        super();
        this.context = context;
    }

    protected Context getContext()
    {
        return context;
    }
}
