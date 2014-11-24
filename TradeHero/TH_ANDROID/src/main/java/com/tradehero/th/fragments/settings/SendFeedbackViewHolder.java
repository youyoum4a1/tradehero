package com.tradehero.th.fragments.settings;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th.R;
import com.tradehero.th.utils.VersionUtils;
import javax.inject.Inject;

public class SendFeedbackViewHolder extends OneSettingViewHolder
{
    //<editor-fold desc="Constructors">
    @Inject public SendFeedbackViewHolder()
    {
        super();
    }
    //</editor-fold>

    @Override public void initViews(@NonNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        if (clickablePref != null)
        {
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

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_primary_send_feedback;
    }

    @Override protected void handlePrefClicked()
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            preferenceFragmentCopy.startActivity(
                    Intent.createChooser(VersionUtils.getSupportEmailIntent(preferenceFragmentCopy.getActivity()),
                            ""));
        }
    }

    private void handleSendFeedbackLongClicked()
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            preferenceFragmentCopy.startActivity(Intent.createChooser(
                    VersionUtils.getSupportEmailIntent(preferenceFragmentCopy.getActivity(), true), ""));
        }
    }
}
