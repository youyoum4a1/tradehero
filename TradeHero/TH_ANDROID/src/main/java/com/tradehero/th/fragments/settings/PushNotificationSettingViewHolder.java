package com.tradehero.th.fragments.settings;

import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.StringRes;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.Callback;

public class PushNotificationSettingViewHolder extends UserProfileCheckBoxSettingViewHolder
{
    @NonNull protected final PushNotificationManager pushNotificationManager;

    @Nullable protected CheckBoxPreference pushNotificationSound;
    @Nullable protected CheckBoxPreference pushNotificationVibrate;

    //<editor-fold desc="Constructors">
    @Inject public PushNotificationSettingViewHolder(
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull PushNotificationManager pushNotificationManager)
    {
        super(currentUserId, userProfileCache, progressDialogUtil, userServiceWrapper);
        this.pushNotificationManager = pushNotificationManager;
    }
    //</editor-fold>

    @Override public void initViews(@NonNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        pushNotificationSound = (CheckBoxPreference) preferenceFragment.findPreference(
                preferenceFragment.getString(R.string.key_settings_notifications_push_alert_sound));
        if (pushNotificationSound != null)
        {
            pushNotificationSound.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener()
                    {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue)
                        {
                            pushNotificationManager.setSoundEnabled((boolean) newValue);
                            return true;
                        }
                    });
        }

        pushNotificationVibrate = (CheckBoxPreference) preferenceFragment.findPreference(
                preferenceFragment.getString(R.string.key_settings_notifications_push_alert_vibrate));
        if (pushNotificationVibrate != null)
        {
            pushNotificationVibrate.setOnPreferenceChangeListener(
                    new Preference.OnPreferenceChangeListener()
                    {
                        @Override
                        public boolean onPreferenceChange(Preference preference, Object newValue)
                        {
                            pushNotificationManager.setVibrateEnabled((boolean) newValue);
                            return true;
                        }
                    });
        }
    }

    @StringRes @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_notifications_push;
    }

    @Override protected void updateStatus(@NonNull UserProfileDTO userProfileDTO)
    {
        if (clickablePref != null)
        {
            clickablePref.setChecked(userProfileDTO.pushNotificationsEnabled);
        }
        if (pushNotificationSound != null)
        {
            pushNotificationSound.setEnabled(userProfileDTO.pushNotificationsEnabled);
        }

        if (pushNotificationVibrate != null)
        {
            pushNotificationVibrate.setEnabled(userProfileDTO.pushNotificationsEnabled);
        }

        if (userProfileDTO.pushNotificationsEnabled)
        {
            pushNotificationManager.enablePush();
        }
        else
        {
            pushNotificationManager.disablePush();
        }
    }

    @Override protected boolean changeStatus(boolean enable)
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            progressDialog = progressDialogUtil.show(preferenceFragmentCopy.getActivity(),
                    R.string.settings_notifications_push_alert_title,
                    R.string.settings_notifications_push_alert_message);
            detachMiddleCallback();
            middleCallbackUpdateUserProfile = userServiceWrapper.updateProfilePropertyPushNotifications(
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
