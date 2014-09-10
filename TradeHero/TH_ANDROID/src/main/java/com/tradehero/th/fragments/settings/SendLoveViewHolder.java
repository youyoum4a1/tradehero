package com.tradehero.th.fragments.settings;

import android.app.Activity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.th.R;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class SendLoveViewHolder extends OneSettingViewHolder
{
    @NotNull private final Provider<Activity> activityHolder;

    //<editor-fold desc="Constructors">
    @Inject public SendLoveViewHolder(
            @NotNull Provider<Activity> activityHolder)
    {
        super();
        this.activityHolder = activityHolder;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_send_love;
    }

    @Override protected void handlePrefClicked()
    {
        Activity currentActivity = activityHolder.get();
        if (currentActivity != null)
        {
            AskForReviewDialogFragment.showReviewDialog(
                    ((SherlockFragmentActivity) currentActivity).getSupportFragmentManager());
        }
    }
}
