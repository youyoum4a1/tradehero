package com.tradehero.common.milestone;

import android.content.Context;

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
