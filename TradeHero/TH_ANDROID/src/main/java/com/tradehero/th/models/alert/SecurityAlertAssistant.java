package com.tradehero.th.models.alert;

import android.os.AsyncTask;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertCompactDTOList;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.persistence.alert.AlertCompactCache;
import com.tradehero.th.persistence.alert.AlertCompactListCache;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

/**
 * This assistant provides a mapping from securityId to alertId.
 * It is able to fetch the relevant data if necessary.
 */
public class SecurityAlertAssistant
{
    @NotNull final AlertCompactListCache alertCompactListCache;
    @NotNull final AlertCompactCache alertCompactCache;

    private boolean populated;
    private boolean failed;
    private UserBaseKey userBaseKey;
    @NotNull private final Map<SecurityId, AlertId> securitiesWithAlerts;
    private OnPopulatedListener onPopulatedListener;
    private AsyncTask<Void, Void, Void> populateTask;

    //<editor-fold desc="Constructors">
    @Inject public SecurityAlertAssistant(
            @NotNull AlertCompactListCache alertCompactListCache,
            @NotNull AlertCompactCache alertCompactCache)
    {
        super();
        this.alertCompactListCache = alertCompactListCache;
        this.alertCompactCache = alertCompactCache;
        securitiesWithAlerts = new HashMap<>();
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

    public void setOnPopulatedListener(OnPopulatedListener onPopulatedListener)
    {
        this.onPopulatedListener = onPopulatedListener;
    }

    protected void notifyPopulated()
    {
        populated = true;
        failed = false;
        OnPopulatedListener populatedListener = onPopulatedListener;
        if (populatedListener != null)
        {
            populatedListener.onPopulated(this);
        }
    }

    protected void notifyPopulateFailed(Throwable error)
    {
        populated = false;
        failed = true;
        OnPopulatedListener populatedListener = onPopulatedListener;
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

    protected void populate(@NotNull AlertCompactDTOList alertCompactDTOs)
    {
        for (@NotNull AlertCompactDTO alertCompactDTO : alertCompactDTOs)
        {
            if (alertCompactDTO.security != null)
            {
                securitiesWithAlerts.put(alertCompactDTO.security.getSecurityId(), alertCompactDTO.getAlertId(userBaseKey));
            }
            else
            {
                Timber.d("populate: AlertId %s had a null alertCompact of securityCompact", alertCompactDTO);
            }
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