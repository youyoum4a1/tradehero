package com.tradehero.th.models.intent;

import android.content.Context;
import android.os.Handler;
import com.tradehero.th.activities.CurrentActivityHolder;

/**
 * Created by xavier on 2/25/14.
 */
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
