package com.tradehero.th.fragments.alert;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.security.SecurityId;
import javax.inject.Inject;
import rx.android.observables.AndroidObservable;

public class AlertCreateFragment extends BaseAlertEditFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".securityId";

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
    }

    @NonNull public static SecurityId getSecurityId(@NonNull Bundle args)
    {
        return new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE));
    }

    @Override public void onResume()
    {
        super.onResume();
        linkWith(getSecurityId(getArguments()), true);
        linkWith(getDummyInitialAlertDTO(), true);
    }

    protected AlertDTO getDummyInitialAlertDTO()
    {
        AlertDTO dummy = new AlertDTO();
        dummy.active = true;
        dummy.priceMovement = 0.1d;
        dummy.upOrDown = true;
        return dummy;
    }

    protected void displayActionBarTitle()
    {
        setActionBarTitle(R.string.stock_alert_add_alert);
    }

    protected void saveAlertProper(AlertFormDTO alertFormDTO)
    {
        AndroidObservable.bindFragment(this, alertServiceWrapper.get().createAlertRx(
                currentUserId.toUserBaseKey(),
                alertFormDTO))
                .subscribe(createAlertUpdateObserver());
    }
}
