package com.tradehero.th.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import com.tradehero.th.R;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class SendLoveViewHolder extends OneSettingViewHolder
{
    @NotNull private final AlertDialogUtil alertDialogUtil;

    //<editor-fold desc="Constructors">
    @Inject public SendLoveViewHolder(@NotNull AlertDialogUtil alertDialogUtil)
    {
        super();
        this.alertDialogUtil = alertDialogUtil;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_send_love;
    }

    @Override protected void handlePrefClicked()
    {
        final String appName = Constants.PLAYSTORE_APP_ID;
        try
        {
            preferenceFragment.startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
        }
        catch (android.content.ActivityNotFoundException anfe)
        {
            try
            {
                preferenceFragment.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
            }
            catch (Exception e)
            {
                Timber.e(e, "Cannot send to Google Play store");
                alertDialogUtil.popWithNegativeButton(
                        preferenceFragment.getActivity(),
                        R.string.webview_error_no_browser_for_intent_title,
                        R.string.webview_error_no_browser_for_intent_description,
                        R.string.cancel);
            }
        }
    }
}
