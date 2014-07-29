package com.tradehero.th.fragments.settings;

import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import com.tradehero.th.R;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class SendLoveViewHolder extends BaseSettingViewHolder
{
    @NotNull private final AlertDialogUtil alertDialogUtil;

    protected Preference sendLovePref;

    //<editor-fold desc="Constructors">
    @Inject public SendLoveViewHolder(@NotNull AlertDialogUtil alertDialogUtil)
    {
        super();
        this.alertDialogUtil = alertDialogUtil;
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);

        sendLovePref =
                preferenceFragment.findPreference(preferenceFragment.getString(R.string.key_settings_primary_send_love));
        if (sendLovePref != null)
        {
            sendLovePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
            {
                @Override public boolean onPreferenceClick(Preference preference)
                {
                    handleSendLoveClicked();
                    return true;
                }
            });
        }

    }

    @Override public void destroyViews()
    {
        this.sendLovePref = null;
        super.destroyViews();
    }

    private void handleSendLoveClicked()
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
