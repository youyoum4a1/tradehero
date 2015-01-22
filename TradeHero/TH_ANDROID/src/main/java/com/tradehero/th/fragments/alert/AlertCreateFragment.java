package com.tradehero.th.fragments.alert;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.security.SecurityId;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;

public class AlertCreateFragment extends BaseAlertEditFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".securityId";

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    protected Subscription saveAlertSubscription;

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
        linkWith(getSecurityId(getArguments()));
        linkWith(getDummyInitialAlertDTO());
    }

    @Override public void onStop()
    {
        unsubscribe(saveAlertSubscription);
        saveAlertSubscription = null;
        super.onStop();
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

    @NonNull protected Observable<AlertCompactDTO> saveAlertProperRx(AlertFormDTO alertFormDTO)
    {
        return alertServiceWrapper.get().createAlertRx(
                currentUserId.toUserBaseKey(),
                alertFormDTO);
    }
}
