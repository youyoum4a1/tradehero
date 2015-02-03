package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.persistence.prefs.ResetHelpScreens;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;

public class ResetHelpScreensViewHolder extends OneSettingViewHolder
{
    @NonNull private final BooleanPreference resetHelpScreen;
    private ProgressDialog progressDialog;

    //<editor-fold desc="Constructors">
    @Inject public ResetHelpScreensViewHolder(
            @NonNull @ResetHelpScreens BooleanPreference resetHelpScreen)
    {
        this.resetHelpScreen = resetHelpScreen;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_misc_reset_help_screens;
    }

    @Override protected void handlePrefClicked()
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        resetHelpScreen.set(true);
        if (progressDialog == null)
        {
            if (preferenceFragmentCopy != null)
            {
                Context activityContext = preferenceFragmentCopy.getActivity();
                if (activityContext != null)
                {
                    progressDialog = ProgressDialogUtil.show(
                            activityContext,
                            preferenceFragmentCopy.getString(R.string.settings_misc_reset_help_screen),
                            "");
                }
            }
        }
        else
        {
            progressDialog.show();
        }
        if (preferenceFragmentCopy != null)
        {
            preferenceFragmentCopy.getView().postDelayed(new Runnable()
            {
                @Override public void run()
                {
                    ProgressDialog progressDialogCopy = progressDialog;
                    if (progressDialogCopy != null)
                    {
                        progressDialogCopy.hide();
                    }
                }
            }, 500);
        }
    }
}
