package com.tradehero.th.models.intent.interactor;

import android.os.Bundle;
import com.tradehero.th.billing.googleplay.THIABBillingInteractor;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;

public abstract class InteractorIntent extends THIntent
{
    @Override public DashboardTabType getDashboardType()
    {
        return null;
    }

    @Override public void populate(Bundle bundle)
    {
        super.populate(bundle);

        bundle.putInt(THIABBillingInteractor.BUNDLE_KEY_ACTION, getInteractorAction());
    }

    protected abstract int getInteractorAction();
}
