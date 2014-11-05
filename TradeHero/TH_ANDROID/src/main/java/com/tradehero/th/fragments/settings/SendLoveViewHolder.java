package com.tradehero.th.fragments.settings;

import android.app.Activity;
import com.tradehero.th.R;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class SendLoveViewHolder extends OneSettingViewHolder
{
    @NonNull private final Activity activity;

    //<editor-fold desc="Constructors">
    @Inject public SendLoveViewHolder(
            @NonNull Activity activity)
    {
        super();
        this.activity = activity;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_send_love;
    }

    @Override protected void handlePrefClicked()
    {
        AskForReviewDialogFragment.showReviewDialog(
                activity.getFragmentManager());
    }
}
