package com.tradehero.th.fragments.settings;

import android.content.Intent;
import android.preference.Preference;
import com.tradehero.th.R;
import com.tradehero.th.utils.VersionUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class SendFeedbackViewHolder extends BaseSettingViewHolder
{
    protected Preference sendFeedbackPref;

    //<editor-fold desc="Constructors">
    @Inject public SendFeedbackViewHolder()
    {
        super();
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        sendFeedbackPref =
                preferenceFragment.findPreference(preferenceFragment.getString(R.string.key_settings_primary_send_feedback));
        if (sendFeedbackPref != null)
        {
            sendFeedbackPref.setOnPreferenceClickListener(
                    new Preference.OnPreferenceClickListener()
                    {
                        @Override public boolean onPreferenceClick(Preference preference)
                        {
                            handleSendFeedbackClicked();
                            return true;
                        }
                    });

            // TODO
            //sendFeedbackBlock.setOnLongClickListener(new View.OnLongClickListener()
            //{
            //    @Override public boolean onLongClick(View view)
            //    {
            //        handleSendFeedbackLongClicked();
            //        return true;
            //    }
            //});
        }

    }

    @Override public void destroyViews()
    {
        this.sendFeedbackPref = null;
        super.destroyViews();
    }

    private void handleSendFeedbackClicked()
    {
        preferenceFragment.startActivity(
                Intent.createChooser(VersionUtils.getSupportEmailIntent(preferenceFragment.getSherlockActivity()),
                        ""));
    }

    private void handleSendFeedbackLongClicked()
    {
        preferenceFragment.startActivity(Intent.createChooser(
                VersionUtils.getSupportEmailIntent(preferenceFragment.getSherlockActivity(), true), ""));
    }
}
