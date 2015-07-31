package com.tradehero.common.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.helper.SamsungIapHelper;

public class BaseSamsungOperator
{
    @NonNull protected final Context context;
    @SamsungBillingMode protected final int mode;

    //<editor-fold desc="Constructors">
    public BaseSamsungOperator(@NonNull Context context, @SamsungBillingMode int mode)
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
