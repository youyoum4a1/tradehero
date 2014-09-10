package com.tradehero.th.fragments.settings;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class SendLoveViewHolder extends OneSettingViewHolder
{
    @NotNull private final CurrentActivityHolder currentActivityHolder;

    //<editor-fold desc="Constructors">
    @Inject public SendLoveViewHolder(
            @NotNull CurrentActivityHolder currentActivityHolder)
    {
        super();
        this.currentActivityHolder = currentActivityHolder;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_send_love;
    }

    @Override protected void handlePrefClicked()
    {
        Activity currentActivity = currentActivityHolder.getCurrentActivity();
        if (currentActivity != null)
        {
            AskForReviewDialogFragment.showReviewDialog(
                    ((SherlockFragmentActivity) currentActivity).getSupportFragmentManager());
        }
    }
}
