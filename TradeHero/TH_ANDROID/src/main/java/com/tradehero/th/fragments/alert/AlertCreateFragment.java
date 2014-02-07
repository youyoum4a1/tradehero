package com.tradehero.th.fragments.alert;

import com.actionbarsherlock.app.ActionBar;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.security.SecurityId;

/**
 * Created by xavier on 2/7/14.
 */
public class AlertCreateFragment extends BaseAlertEditFragment
{
    public static final String TAG = AlertCreateFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".securityId";

    @Override public void onResume()
    {
        super.onResume();
        linkWith(new SecurityId(getArguments().getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE)), true);
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
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(R.string.add_alert);
        }
    }

    protected void saveAlertProper(AlertFormDTO alertFormDTO)
    {
        alertServiceWrapper.get().createAlert(
                currentUserId.toUserBaseKey(),
                alertFormDTO,
                alertUpdateCallback);
    }
}
