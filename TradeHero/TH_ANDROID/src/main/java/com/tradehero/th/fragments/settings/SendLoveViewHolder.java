package com.tradehero.th.fragments.settings;

import android.app.Activity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.th.R;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SendLoveViewHolder extends OneSettingViewHolder
{
    @NotNull private final Activity activity;

    //<editor-fold desc="Constructors">
    @Inject public SendLoveViewHolder(
            @NotNull Activity activity)
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
                ((SherlockFragmentActivity) activity).getSupportFragmentManager());
    }
}
