package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.preference.PreferenceFragment;
import android.view.View;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.persistence.prefs.ResetHelpScreens;
import javax.inject.Inject;

public class ResetHelpScreensViewHolder extends OneSettingViewHolder
{
    @NonNull private final BooleanPreference resetHelpScreen;

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
        ProgressDialog progressDialog = null;
        View view = null;
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        resetHelpScreen.set(true);
        if (preferenceFragmentCopy != null)
        {
            view = preferenceFragmentCopy.getView();
            Context activityContext = preferenceFragmentCopy.getActivity();
            if (activityContext != null)
            {
                progressDialog = ProgressDialog.show(activityContext,
                        preferenceFragmentCopy.getString(R.string.settings_misc_reset_help_screen),
                        "",
                        true);
            }
        }

        if (view != null && progressDialog != null)
        {
            final ProgressDialog finalProgressDialog = progressDialog;
            view.postDelayed(new Runnable()
            {
                @Override public void run()
                {
                    finalProgressDialog.hide();
                }
            }, 500);
        }
    }
}
