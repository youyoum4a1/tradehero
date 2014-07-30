package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.th.R;
import com.tradehero.th.persistence.prefs.ResetHelpScreens;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class ResetHelpScreensViewHolder extends OneSettingViewHolder
{
    @NotNull private final BooleanPreference resetHelpScreen;
    @NotNull private final ProgressDialogUtil progressDialogUtil;
    private ProgressDialog progressDialog;

    //<editor-fold desc="Constructors">
    @Inject public ResetHelpScreensViewHolder(
            @NotNull @ResetHelpScreens BooleanPreference resetHelpScreen,
            @NotNull ProgressDialogUtil progressDialogUtil)
    {
        this.resetHelpScreen = resetHelpScreen;
        this.progressDialogUtil = progressDialogUtil;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_misc_reset_help_screens;
    }

    @Override protected void handlePrefClicked()
    {
        resetHelpScreen.delete();
        if (progressDialog == null)
        {
            progressDialog = progressDialogUtil.show(preferenceFragment.getActivity(),
                    preferenceFragment.getString(R.string.settings_misc_reset_help_screen),
                    "");
        }
        else
        {
            progressDialog.show();
        }
        preferenceFragment.getView().postDelayed(new Runnable()
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
