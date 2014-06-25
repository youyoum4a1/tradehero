package com.tradehero.th.models.alert;

import android.os.AsyncTask;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.alert.AlertIdList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import com.tradehero.th.utils.DaggerUtils;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * This assistant provides a mapping from securityId to alertId.
 * It is able to fetch the relevant data if necessary.
 */
public class SecurityAlertAssistant
{
    @Inject AlertCompactListCache alertCompactListCache;
    @Inject AlertCompactCache alertCompactCache;

    private boolean populated;
    private boolean failed;
    private UserBaseKey userBaseKey;
    private final Map<SecurityId, AlertId> securitiesWithAlerts;
    private WeakReference<OnPopulatedListener> onPopulatedListener = new WeakReference<>(null);
    private AsyncTask<Void, Void, Void> populateTask;

    //<editor-fold desc="Constructors">
    public SecurityAlertAssistant()
    {
        super();
        securitiesWithAlerts = new HashMap<>();
        DaggerUtils.inject(this);
    }
    //</editor-fold>

    public void onDestroy()
    {
        if (populateTask != null)
        {
            populateTask.cancel(false);
        }
        populateTask = null;
    }

    public boolean isPopulated()
    {
        return populated;
    }

    public boolean isFailed()
    {
        return failed;
    }

    public UserBaseKey getUserBaseKey()
    {
        return userBaseKey;
    }

    public void setUserBaseKey(UserBaseKey userBaseKey)
    {
        this.userBaseKey = userBaseKey;
    }

    public OnPopulatedListener getOnPopulatedListener()
    {
        return onPopulatedListener.get();
    }

    /**
     * The listener needs to be strongly referenced elsewhere
     * @param onPopulatedListener
     */
    public void setOnPopulatedListener(OnPopulatedListener onPopulatedListener)
    {
        this.onPopulatedListener = new WeakReference<>(onPopulatedListener);
    }

    protected void notifyPopulated()
    {
        populated = true;
        failed = false;
        OnPopulatedListener populatedListener = getOnPopulatedListener();
        if (populatedListener != null)
        {
            populatedListener.onPopulated(this);
        }
    }

    protected void notifyPopulateFailed(Throwable error)
    {
        populated = false;
        failed = true;
        OnPopulatedListener populatedListener = getOnPopulatedListener();
        if (populatedListener != null)
        {
            populatedListener.onPopulateFailed(this, error);
        }
    }

    public void populate()
    {
        populated = false;
        failed = false;
        securitiesWithAlerts.clear();
        if (populateTask != null)
        {
            populateTask.cancel(false);
        }
        populateTask = new AsyncTask<Void, Void, Void>()
        {
            private Throwable error;

            @Override protected Void doInBackground(Void... voids)
            {
                try
                {
                    requestAlertListFromCache();
                }
                catch (Throwable throwable)
                {
                    error = throwable;
                }
                return null;
            }

            @Override protected void onPostExecute(Void aVoid)
            {
                if (error != null)
                {
                    notifyPopulateFailed(error);
                }
                else
                {
                    notifyPopulated();
                }
            }
        };
        populateTask.execute();
    }

    protected void requestAlertListFromCache() throws Throwable
    {
        populate(alertCompactListCache.getOrFetchSync(userBaseKey));
    }

    protected void populate(AlertIdList alertIds)
    {
        if (alertIds != null)
        {
            AlertCompactDTO alertCompactDTO;
            for (AlertId alertId : alertIds)
            {
                alertCompactDTO = alertCompactCache.get(alertId);
                if (alertCompactDTO != null && alertCompactDTO.security != null)
                {
                    securitiesWithAlerts.put(alertCompactDTO.security.getSecurityId(), alertId);
                }
                else
                {
                    Timber.d("populate: AlertId %s had a null alertCompact of securityCompact", alertId);
                }
            }
        }
        else
        {
            Timber.d("populate: alertIds were null");
        }
    }

    public AlertId getAlertId(SecurityId securityId)
    {
        return securitiesWithAlerts.get(securityId);
    }

    public static interface OnPopulatedListener
    {
        void onPopulated(SecurityAlertAssistant securityAlertAssistant);
        void onPopulateFailed(SecurityAlertAssistant securityAlertAssistant, Throwable error);
    }
}
