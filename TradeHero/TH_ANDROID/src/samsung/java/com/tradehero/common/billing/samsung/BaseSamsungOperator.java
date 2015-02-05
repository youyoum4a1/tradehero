package com.tradehero.common.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.helper.SamsungIapHelper;

public class BaseSamsungOperator
{
    @NonNull protected final Context context;
    protected final int mode;

    //<editor-fold desc="Constructors">
    public BaseSamsungOperator(@NonNull Context context, int mode)
    {
        this.context = context;
        this.mode = mode;
    }
    //</editor-fold>

    @NonNull protected SamsungIapHelper getSamsungIapHelper()
    {
        return SamsungIapHelper.getInstance(context, mode);
    }
}
