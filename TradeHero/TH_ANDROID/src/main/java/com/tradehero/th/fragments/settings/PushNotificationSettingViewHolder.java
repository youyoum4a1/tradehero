package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class PushNotificationSettingViewHolder extends UserProfileCheckBoxSettingViewHolder
{
    @NonNull protected final PushNotificationManager pushNotificationManager;

    @Nullable protected CheckBoxPreference pushNotificationSound;
    @Nullable protected CheckBoxPreference pushNotificationVibrate;

    @Nullable private Subscription updatePropertySubscription;

    //<editor-fold desc="Constructors">
    @Inject public PushNotificationSettingViewHolder(
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull PushNotificationManager pushNotificationManager)
    {
        super(currentUserId, userProfileCache, userServiceWrapper);
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

    @Override public void destroyViews()
    {
        unsubscribe(updatePropertySubscription);
        updatePropertySubscription = null;
        super.destroyViews();
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
            ProgressDialog progressDialog = ProgressDialog.show(
                    preferenceFragmentCopy.getActivity(),
                    preferenceFragmentCopy.getActivity().getString(R.string.settings_notifications_push_alert_title),
                    preferenceFragmentCopy.getActivity().getString(R.string.settings_notifications_push_alert_message),
                    true);
            subscriptions.add(userServiceWrapper.updateProfilePropertyPushNotificationsRx(
                    currentUserId.toUserBaseKey(), enable)
                    .observeOn(AndroidSchedulers.mainThread())
                    .finallyDo(progressDialog::dismiss)
                    .subscribe(
                            this::onProfileUpdated,
                            this::onProfileUpdateFailed));
        }
        return false;
    }
}
