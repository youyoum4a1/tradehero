package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import com.actionbarsherlock.app.ActionBar;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.alert.AlertCache;
import javax.inject.Inject;

/**
 * Created by xavier on 2/7/14.
 */
public class AlertEditFragment extends BaseAlertEditFragment
{
    public static final String TAG = AlertEditFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_ALERT_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".alertId";

    protected AlertId alertId;
    @Inject protected AlertCache alertCache;
    protected DTOCache.Listener<AlertId, AlertDTO> alertCacheListener;
    protected DTOCache.GetOrFetchTask<AlertId, AlertDTO> alertCacheFetchTask;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        createAlertCacheListener();
    }

    @Override protected void createAlertUpdateCallback()
    {
        alertUpdateCallback = new AlertUpdateCallback();
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
                THLog.e(TAG, "Failed to get alertDTO", error);
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

    @Override protected void saveAlertProper(AlertFormDTO alertFormDTO)
    {
        alertServiceWrapper.get().updateAlert(
                alertId,
                alertFormDTO,
                alertUpdateCallback);
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
            actionBar.setTitle(R.string.edit_alert);
        }
    }

    protected class AlertUpdateCallback extends AlertCreateCallback
    {
        @Override protected void updateCompactListCache(AlertCompactDTO alertCompactDTO)
        {
            // Do nothing on compact list cache on purpose

            alertCache.invalidate(alertCompactDTO.getAlertId(currentUserId.toUserBaseKey()));
        }
    }
}
