package com.tradehero.common.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseRequestCodeActor;

abstract public class BaseSamsungActorRx extends BaseRequestCodeActor
{
    @NonNull protected final Context context;
    protected final int mode;

    //<editor-fold desc="Constructors">
    public BaseSamsungActorRx(
            int requestCode,
            @NonNull Context context,
            int mode)
    {
        super(requestCode);
        this.context = context;
        this.mode = mode;
    }
    //</editor-fold>
}
