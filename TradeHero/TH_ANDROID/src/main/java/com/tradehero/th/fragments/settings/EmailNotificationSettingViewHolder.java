package com.tradehero.th.fragments.settings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

class EmailNotificationSettingViewHolder extends UserProfileCheckBoxSettingViewHolder
{
    @Nullable private Subscription changeStatusSubscription;

    //<editor-fold desc="Constructors">
    @Inject EmailNotificationSettingViewHolder(
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull UserServiceWrapper userServiceWrapper)
    {
        super(currentUserId, userProfileCache, userServiceWrapper);
    }
    //</editor-fold>

    @Override public void destroyViews()
    {
        unsubscribe(changeStatusSubscription);
        changeStatusSubscription = null;
        super.destroyViews();
    }

    @StringRes @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_notifications_email;
    }

    @Override protected void updateStatus(@NonNull UserProfileDTO userProfileDTO)
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
            progressDialog = ProgressDialogUtil.show(preferenceFragmentCopy.getActivity(),
                    R.string.settings_notifications_email_alert_title,
                    R.string.settings_notifications_email_alert_message);
            unsubscribe(changeStatusSubscription);
            changeStatusSubscription =
                    userServiceWrapper.updateProfilePropertyEmailNotificationsRx(
                            currentUserId.toUserBaseKey(), enable)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    this::onProfileUpdated,
                                    this::onProfileUpdateFailed);
        }
        return false;
    }
}
