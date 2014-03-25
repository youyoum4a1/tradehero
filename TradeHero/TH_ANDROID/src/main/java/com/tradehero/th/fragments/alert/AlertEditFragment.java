package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.alert.MiddleCallbackUpdateAlertCompact;
import com.tradehero.th.persistence.alert.AlertCache;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by xavier on 2/7/14.
 */
public class AlertEditFragment extends BaseAlertEditFragment
{
    public static final String BUNDLE_KEY_ALERT_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".alertId";

    protected AlertId alertId;
    @Inject protected AlertCache alertCache;
    protected DTOCache.Listener<AlertId, AlertDTO> alertCacheListener;
    protected DTOCache.GetOrFetchTask<AlertId, AlertDTO> alertCacheFetchTask;
    protected MiddleCallbackUpdateAlertCompact middleCallbackUpdateAlertCompactDTO;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        createAlertCacheListener();
    }

    protected void createAlertCacheListener()
    {
        alertCacheListener = new DTOCache.Listener<AlertId, AlertDTO>()
        {
            @Override public void onDTOReceived(AlertId key, AlertDTO value, boolean fromCache)
            {
                linkWith(value, true);
            }

            @Override public void onErrorThrown(AlertId key, Throwable error)
            {
                THToast.show(new THException(error));
                Timber.e("Failed to get alertDTO", error);
            }
        };
    }

    @Override public void onResume()
    {
        super.onResume();
        linkWith(new AlertId(getArguments().getBundle(BUNDLE_KEY_ALERT_ID_BUNDLE)), true);
    }

    @Override public void onDestroy()
    {
        alertCacheListener = null;
        detachAlertCacheFetchTask();
        detachMiddleCallbackUpdate();
        super.onDestroy();
    }

    protected void detachAlertCacheFetchTask()
    {
        if (alertCacheFetchTask != null)
        {
            alertCacheFetchTask.setListener(null);
        }
        alertCacheFetchTask = null;
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
        alertCacheFetchTask = alertCache.getOrFetch(alertId, alertCacheListener);
        alertCacheFetchTask.execute();
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
}
