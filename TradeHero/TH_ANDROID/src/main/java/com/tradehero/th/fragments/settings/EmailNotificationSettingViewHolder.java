package com.tradehero.th.fragments.settings;

import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th2.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;

class EmailNotificationSettingViewHolder extends UserProfileCheckBoxSettingViewHolder
{
    //<editor-fold desc="Constructors">
    @Inject EmailNotificationSettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull UserServiceWrapper userServiceWrapper)
    {
        super(currentUserId, userProfileCache, progressDialogUtil, userServiceWrapper);
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_notifications_email;
    }

    @Override protected void updateStatus(@NotNull UserProfileDTO userProfileDTO)
    {
        if (clickablePref != null)
        {
            clickablePref.setChecked(userProfileDTO.emailNotificationsEnabled);
        }
    }

    @Override protected boolean changeStatus(boolean enable)
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            progressDialog = progressDialogUtil.show(preferenceFragmentCopy.getActivity(),
                    R.string.settings_notifications_email_alert_title,
                    R.string.settings_notifications_email_alert_message);
            detachMiddleCallback();
            middleCallbackUpdateUserProfile =
                    userServiceWrapper.updateProfilePropertyEmailNotifications(
                            currentUserId.toUserBaseKey(), enable,
                            createUserProfileCallback());
        }
        return false;
    }

    protected Callback<UserProfileDTO> createUserProfileCallback()
    {
        return new UserProfileUpdateCallback();
    }
}
