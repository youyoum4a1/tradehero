package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.SocialAlertDialogRxUtil;
import dagger.Lazy;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;

abstract public class SocialConnectSettingViewHolder
        extends UserProfileCheckBoxSettingViewHolder
{
    @NonNull protected final SocialAlertDialogRxUtil socialAlertDialogRxUtil;
    @NonNull protected final SocialServiceWrapper socialServiceWrapper;
    @NonNull protected final Lazy<? extends SocialAuthenticationProvider> socialAuthenticationProvider;
    @NonNull protected final UserProfileDTOUtil userProfileDTOUtil;
    @NonNull protected final String authToken;
    @Nullable protected Subscription sequenceSubscription;

    //<editor-fold desc="Constructors">
    protected SocialConnectSettingViewHolder(
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull SocialAlertDialogRxUtil socialAlertDialogRxUtil,
            @NonNull SocialServiceWrapper socialServiceWrapper,
            @NonNull Lazy<? extends SocialAuthenticationProvider> socialAuthenticationProvider,
            @NonNull UserProfileDTOUtil userProfileDTOUtil,
            @NonNull String authToken)
    {
        super(currentUserId, userProfileCache, userServiceWrapper);
        this.socialAlertDialogRxUtil = socialAlertDialogRxUtil;
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
        unsubscribe(sequenceSubscription);
        super.destroyViews();
    }

    @Override protected boolean changeStatus(boolean enable)
    {
        Activity activityContext = null;
        DashboardPreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            activityContext = preferenceFragmentCopy.getActivity();
        }
        if (activityContext != null)
        {
            Observable<UserProfileDTO> sequence;
            if (enable)
            {
                sequence = linkRx(activityContext);
            }
            else if (isMainLogin())
            {
                sequence = socialAlertDialogRxUtil.popErrorUnlinkDefaultAccount(activityContext)
                        .flatMap(pair -> Observable.empty());
            }
            else
            {
                sequence = confirmUnLinkRx(activityContext);
            }
            unsubscribe(sequenceSubscription);
            sequenceSubscription = sequence.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ChangedStatusObserver(activityContext));
        }
        return false;
    }

    @NonNull protected Observable<UserProfileDTO> linkRx(@NonNull Activity activityContext)
    {
        ProgressDialog progressDialog = ProgressDialogUtil.show(
                activityContext,
                getSocialNetworkEnum().nameResId,
                getLinkingDialogMessage());
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);
        return socialAuthenticationProvider.get()
                .socialLink(activityContext)
                .finallyDo(progressDialog::dismiss);
    }

    @NonNull protected Observable<UserProfileDTO> confirmUnLinkRx(@NonNull Context activityContext)
    {
        return socialAlertDialogRxUtil.popConfirmUnlinkAccount(
                activityContext,
                getSocialNetworkEnum())
                .filter(pair -> pair.second.equals(DialogInterface.BUTTON_POSITIVE))
                .flatMap(pair -> effectUnlinkRx(activityContext));
    }

    @NonNull protected Observable<UserProfileDTO> effectUnlinkRx(@NonNull Context activityContext)
    {
        ProgressDialog progressDialog = ProgressDialogUtil.show(
                activityContext,
                getSocialNetworkEnum().nameResId,
                getUnlinkingProgressDialogMessage());
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);

        return socialServiceWrapper.disconnectRx(
                currentUserId.toUserBaseKey(),
                new SocialNetworkFormDTO(getSocialNetworkEnum()))
                .finallyDo(progressDialog::dismiss);
    }

    protected class ChangedStatusObserver implements Observer<UserProfileDTO>
    {
        @NonNull private final Context activityContext;

        //<editor-fold desc="Constructors">
        protected ChangedStatusObserver(@NonNull Context activityContext)
        {
            this.activityContext = activityContext;
        }
        //</editor-fold>

        @Override public void onNext(UserProfileDTO args)
        {
            updateStatus(args);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            socialAlertDialogRxUtil.popErrorSocialAuth(activityContext, e)
                    .subscribe(Actions.empty(), Actions.empty());
        }
    }

    @NonNull abstract protected SocialNetworkEnum getSocialNetworkEnum();

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
