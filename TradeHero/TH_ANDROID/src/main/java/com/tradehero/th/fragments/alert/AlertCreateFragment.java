package com.tradehero.th.fragments.alert;

import com.actionbarsherlock.app.ActionBar;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.retrofit.MiddleCallback;

public class AlertCreateFragment extends BaseAlertEditFragment
{
    public static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".securityId";

    private MiddleCallback<AlertCompactDTO> middleCallbackCreateAlertCompactDTO;

    @Override public void onResume()
    {
        super.onResume();
        linkWith(new SecurityId(getArguments().getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE)), true);
        linkWith(getDummyInitialAlertDTO(), true);
    }

    @Override public void onStop()
    {
        detachMiddleCallbackCreate();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
    }

    protected void detachMiddleCallbackCreate()
    {
        if (middleCallbackCreateAlertCompactDTO != null)
        {
            middleCallbackCreateAlertCompactDTO.setPrimaryCallback(null);
        }
        middleCallbackCreateAlertCompactDTO = null;
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
            actionBar.setTitle(R.string.stock_alert_add_alert);
        }
    }

    protected void saveAlertProper(AlertFormDTO alertFormDTO)
    {
        detachMiddleCallbackCreate();
        middleCallbackCreateAlertCompactDTO = alertServiceWrapper.get().createAlert(
                currentUserId.toUserBaseKey(),
                alertFormDTO,
                createAlertUpdateCallback());
    }
}
