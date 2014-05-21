package com.tradehero.th.models.intent;

import android.content.Context;
import com.tradehero.th.activities.CurrentActivityHolder;

public class OpenCurrentActivityHolder extends CurrentActivityHolder
{
    protected Context context;

    public OpenCurrentActivityHolder(Context context)
    {
        super(null);
        setContext(context);
    }

    @Override public Context getCurrentContext()
    {
        return context;
    }

    public void setContext(Context context)
    {
        this.context = context;
    }
}
