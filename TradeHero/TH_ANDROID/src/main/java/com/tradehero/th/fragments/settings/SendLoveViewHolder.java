package com.tradehero.th.fragments.settings;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SendLoveViewHolder extends OneSettingViewHolder
{
    @NotNull private final AlertDialogUtil alertDialogUtil;
    @NotNull private final CurrentActivityHolder currentActivityHolder;

    //<editor-fold desc="Constructors">
    @Inject public SendLoveViewHolder(@NotNull AlertDialogUtil alertDialogUtil, CurrentActivityHolder currentActivityHolder)
    {
        super();
        this.alertDialogUtil = alertDialogUtil;
        this.currentActivityHolder = currentActivityHolder;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_send_love;
    }

    @Override protected void handlePrefClicked()
    {
        AskForReviewDialogFragment.showReviewDialog(
                ((SherlockFragmentActivity) currentActivityHolder.getCurrentActivity()).getSupportFragmentManager());
    }
}
