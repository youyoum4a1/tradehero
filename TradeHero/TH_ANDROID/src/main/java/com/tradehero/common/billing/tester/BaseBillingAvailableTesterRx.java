package com.tradehero.common.billing.tester;

import com.tradehero.common.billing.BaseRequestCodeActor;

abstract public class BaseBillingAvailableTesterRx
        extends BaseRequestCodeActor
        implements BillingAvailableTesterRx
{
    //<editor-fold desc="Constructors">
    protected BaseBillingAvailableTesterRx(int requestCode)
    {
        super(requestCode);
    }
    //</editor-fold>
}
