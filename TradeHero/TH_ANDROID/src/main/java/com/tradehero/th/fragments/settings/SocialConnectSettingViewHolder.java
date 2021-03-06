package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialNetworkFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.AlertDialogObserver;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

abstract public class SocialConnectSettingViewHolder
        extends UserProfileCheckBoxSettingViewHolder
{
    @NonNull protected final AlertDialogUtil alertDialogUtil;
    @NonNull protected final SocialServiceWrapper socialServiceWrapper;
    @NonNull protected final Lazy<? extends SocialAuthenticationProvider> socialAuthenticationProvider;
    @NonNull protected final UserProfileDTOUtil userProfileDTOUtil;
    @NonNull protected final String authToken;
    @Nullable protected MiddleCallback<UserProfileDTO> middleCallbackDisconnect;
    @Nullable protected AlertDialog unlinkConfirmDialog;
    @Nullable protected Subscription linkingSubscription;
    @Nullable protected Subscription unLinkingSubscription;

    //<editor-fold desc="Constructors">
    protected SocialConnectSettingViewHolder(
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull AlertDialogUtil alertDialogUtil,
            @NonNull SocialServiceWrapper socialServiceWrapper,
            @NonNull Lazy<? extends SocialAuthenticationProvider> socialAuthenticationProvider,
            @NonNull UserProfileDTOUtil userProfileDTOUtil,
            @NonNull String authToken)
    {
        super(currentUserId, userProfileCache, progressDialogUtil, userServiceWrapper);
        this.alertDialogUtil = alertDialogUtil;
        this.socialServiceWrapper = socialServiceWrapper;
        this.socialAuthenticationProvider = socialAuthenticationProvider;
        this.userProfileDTOUtil = userProfileDTOUtil;
        this.authToken = authToken;
    }
    //</editor-fold>

    @Override public void initViews(@NonNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        Preference clickablePrefCopy = clickablePref;
        if (clickablePrefCopy != null)
        {
            clickablePrefCopy.setOrder(preferenceFragment.getResources().getInteger(getOrderIntResId()));
        }
        showIsMainLogin();
    }

    @IntegerRes protected abstract int getOrderIntResId();

    @Override public void destroyViews()
    {
        unsubscribeLinking();
        unsubscribeUnLinking();
        detachMiddleServerDisconnectCallback();
        hideUnlinkConfirmDialog();
        super.destroyViews();
    }

    protected void detachMiddleServerDisconnectCallback()
    {
        MiddleCallback<UserProfileDTO> middleCallbackDisconnectCopy = middleCallbackDisconnect;
        if (middleCallbackDisconnectCopy != null)
        {
            middleCallbackDisconnectCopy.setPrimaryCallback(null);
        }
    }

    protected void unsubscribeLinking()
    {
        if (linkingSubscription != null)
        {
            linkingSubscription.unsubscribe();
        }
        linkingSubscription = null;
    }

    protected void unsubscribeUnLinking()
    {
        if (unLinkingSubscription != null)
        {
            unLinkingSubscription.unsubscribe();
        }
        unLinkingSubscription = null;
    }

    protected void hideUnlinkConfirmDialog()
    {
        AlertDialog unlinkConfirmDialogCopy = unlinkConfirmDialog;
        if (unlinkConfirmDialogCopy != null)
        {
            unlinkConfirmDialogCopy.dismiss();
        }
        unlinkConfirmDialog = null;
    }

    @Override protected boolean changeStatus(boolean enable)
    {
        Activity activityContext = null;
        DashboardPreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            activityContext = preferenceFragmentCopy.getActivity();
        }
        Timber.d("Sharing is asked to change");
        if (activityContext != null && enable)
        {
            dismissProgress();
            progressDialog = progressDialogUtil.show(
                    activityContext,
                    getSocialNetworkEnum().nameResId,
                    getLinkingDialogMessage());
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(true);
            unsubscribeLinking();
            linkingSubscription = socialAuthenticationProvider.get()
                    .socialLink(activityContext)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ChangedStatusObserver(activityContext, alertDialogUtil));
        }
        else if (activityContext != null)
        {
            if (isMainLogin())
            {
                dismissProgress();
                alertDialogUtil.popWithNegativeButton(
                        activityContext,
                        R.string.app_name,
                        R.string.authentication_unlink_fail_message,
                        R.string.ok);
            }
            else
            {
                popConfirmUnlinkDialog();
            }
        }
        return false;
    }

    protected void popConfirmUnlinkDialog()
    {
        Context activityContext = null;
        if (preferenceFragment != null)
        {
            activityContext = preferenceFragment.getActivity();
        }
        if (activityContext != null)
        {
            hideUnlinkConfirmDialog();
            unlinkConfirmDialog = alertDialogUtil.popWithOkCancelButton(
                    activityContext,
                    activityContext.getString(R.string.authentication_unlink_confirm_dialog_title, getSocialNetworkName()),
                    activityContext.getString(R.string.authentication_unlink_confirm_dialog_message, getSocialNetworkName()),
                    R.string.authentication_unlink_confirm_dialog_button_ok,
                    R.string.cancel,
                    new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                            effectUnlink();
                        }
                    });
        }
    }

    protected void effectUnlink()
    {
        Context activityContext = null;
        if (preferenceFragment != null)
        {
            activityContext = preferenceFragment.getActivity();
        }
        if (activityContext != null)
        {
            dismissProgress();
            progressDialog = progressDialogUtil.show(
                    activityContext,
                    getSocialNetworkEnum().nameResId,
                    getUnlinkingProgressDialogMessage());
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(true);

            unsubscribeUnLinking();
            unLinkingSubscription = socialServiceWrapper.disconnectRx(
                    currentUserId.toUserBaseKey(),
                    new SocialNetworkFormDTO(getSocialNetworkEnum()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ChangedStatusObserver(activityContext, alertDialogUtil));
        }
    }

    protected class ChangedStatusObserver extends AlertDialogObserver<UserProfileDTO>
    {
        //<editor-fold desc="Constructors">
        protected ChangedStatusObserver(@NonNull Context activityContext,
                @NonNull AlertDialogUtil alertDialogUtil)
        {
            super(activityContext, alertDialogUtil);
        }
        //</editor-fold>

        @Override public void onNext(UserProfileDTO args)
        {
            updateStatus(args);
        }

        @Override public void onCompleted()
        {
            dismissProgress();
        }

        @Override public void onError(Throwable e)
        {
            dismissProgress();
            super.onError(e);
        }
    }

    @NonNull abstract protected SocialNetworkEnum getSocialNetworkEnum();

    @Nullable protected String getSocialNetworkName()
    {
        return getString(getSocialNetworkEnum().nameResId);
    }

    @StringRes abstract protected int getLinkingDialogMessage();

    @StringRes abstract protected int getUnlinkingProgressDialogMessage();

    @Override protected void updateStatus(@NonNull UserProfileDTO userProfileDTO)
    {
        showIsMainLogin();
        CheckBoxPreference clickablePrefCopy = clickablePref;
        if (clickablePrefCopy != null)
        {
            clickablePrefCopy.setChecked(
                    userProfileDTOUtil.checkLinkedStatus(userProfileDTO, getSocialNetworkEnum()));
        }
    }

    protected void showIsMainLogin()
    {
        CheckBoxPreference clickablePrefCopy = clickablePref;
        if (clickablePrefCopy != null)
        {
            boolean mainLogin = isMainLogin();
            clickablePrefCopy.setEnabled(!mainLogin);
            if (mainLogin)
            {
                clickablePrefCopy.setSummary(R.string.authentication_setting_is_current_login);
            }
            else
            {
                clickablePrefCopy.setSummary(null);
            }
        }
    }

    protected boolean isMainLogin()
    {
        return authToken.startsWith(getSocialNetworkEnum().getAuthHeader());
    }
}
