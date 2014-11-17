package com.tradehero.common.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.tradehero.common.billing.BaseRequestCodeActor;

abstract public class BaseSamsungActor extends BaseRequestCodeActor
{
    @NonNull protected SamsungIapHelper mIapHelper;
    protected final int mode;

    //<editor-fold desc="Constructors">
    public BaseSamsungActor(
            int requestCode,
            @NonNull Context context, int mode)
    {
        super(requestCode);
        mIapHelper = SamsungIapHelper.getInstance(context, mode);
        this.mode = mode;
    }
    //</editor-fold>
}
