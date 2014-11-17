package com.tradehero.common.billing.samsung;

import android.support.annotation.NonNull;
import com.sec.android.iap.lib.helper.SamsungIapHelper;

public class BaseSamsungOperator
{
    @NonNull protected final SamsungIapHelper mIapHelper;

    //<editor-fold desc="Constructors">
    public BaseSamsungOperator(@NonNull SamsungIapHelper mIapHelper)
    {
        this.mIapHelper = mIapHelper;
    }
    //</editor-fold>
}
