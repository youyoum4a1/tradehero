package com.tradehero.th.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABBillingAvailableTesterHolder;
import com.tradehero.common.billing.googleplay.exception.IABException;
import javax.inject.Inject;
import javax.inject.Provider;

public class THBaseIABBillingAvailableTesterHolder
    extends BaseIABBillingAvailableTesterHolder<THIABBillingAvailableTester, IABException>
    implements THIABBillingAvailableTesterHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABBillingAvailableTesterHolder(
            @NonNull Provider<THIABBillingAvailableTester> iabBillingAvailableTesterProvider)
    {
        super(iabBillingAvailableTesterProvider);
    }
    //</editor-fold>
}
