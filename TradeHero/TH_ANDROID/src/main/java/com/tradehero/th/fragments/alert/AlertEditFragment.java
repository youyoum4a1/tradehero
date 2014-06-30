package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.thm.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.alert.AlertCache;
import javax.inject.Inject;
import timber.log.Timber;

public class AlertEditFragment extends BaseAlertEditFragment
{
    public static final String BUNDLE_KEY_ALERT_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".alertId";

    protected AlertId alertId;
    @Inject protected AlertCache alertCache;
    protected DTOCacheNew.Listener<AlertId, AlertDTO> alertCacheFetchListener;
    protected MiddleCallback<AlertCompactDTO> middleCallbackUpdateAlertCompactDTO;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        alertCacheFetchListener = createAlertCacheListener();
    }

    @Override public void onResume()
    {
        super.onResume();
        linkWith(new AlertId(getArguments().getBundle(BUNDLE_KEY_ALERT_ID_BUNDLE)), true);
    }

    @Override public void onStop()
    {
        detachAlertCacheFetchTask();
        detachMiddleCallbackUpdate();
        super.onStop();
    }

    @Override public void onDestroy()
    {
        alertCacheFetchListener = null;
        super.onDestroy();
    }

    protected void detachAlertCacheFetchTask()
    {
        alertCache.unregister(alertCacheFetchListener);
    }

    protected void detachMiddleCallbackUpdate()
    {
        if (middleCallbackUpdateAlertCompactDTO != null)
        {
            middleCallbackUpdateAlertCompactDTO.setPrimaryCallback(null);
        }
        middleCallbackUpdateAlertCompactDTO = null;
    }

    @Override protected void saveAlertProper(AlertFormDTO alertFormDTO)
    {
        detachMiddleCallbackUpdate();
        middleCallbackUpdateAlertCompactDTO = alertServiceWrapper.get().updateAlert(
                alertId,
                alertFormDTO,
                new AlertCreateCallback());
    }

    protected void linkWith(AlertId alertId, boolean andDisplay)
    {
        this.alertId = alertId;
        detachAlertCacheFetchTask();
        alertCache.register(alertId, alertCacheFetchListener);
        alertCache.getOrFetchAsync(alertId);
        if (andDisplay)
        {
        }
    }

    protected void displayActionBarTitle()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (actionBar != null)
        {
            actionBar.setTitle(R.string.stock_alert_edit_alert);
        }
    }

    protected DTOCacheNew.Listener<AlertId, AlertDTO> createAlertCacheListener()
    {
        return new AlertEditFragmentAlertCacheListener();
    }

    protected class AlertEditFragmentAlertCacheListener implements DTOCacheNew.Listener<AlertId, AlertDTO>
    {
        @Override public void onDTOReceived(AlertId key, AlertDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(AlertId key, Throwable error)
        {
            THToast.show(new THException(error));
            Timber.e("Failed to get alertDTO", error);
        }
    }
}
