package com.tradehero.common.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.helper.SamsungIapHelper;

/**
 * Created by xavier on 3/27/14.
 */
public class BaseSamsungActor
{
    private int activityRequestCode;
    protected SamsungIapHelper mIapHelper;
    protected final int mode;

    public BaseSamsungActor(Context context, int mode)
    {
        super();
        mIapHelper = SamsungIapHelper.getInstance(context, mode);
        this.mode = mode;
    }

    public int getRequestCode()
    {
        return activityRequestCode;
    }

    public void setRequestCode(int requestCode)
    {
        this.activityRequestCode = requestCode;
    }
}
