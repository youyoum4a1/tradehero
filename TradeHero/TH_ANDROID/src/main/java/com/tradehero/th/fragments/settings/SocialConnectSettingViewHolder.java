package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.tradehero.th.models.share.SocialShareHelper;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.SocialAlertDialogRxUtil;
import java.util.concurrent.CancellationException;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;

abstract public class SocialConnectSettingViewHolder
        extends UserProfileCheckBoxSettingViewHolder
{
    @NonNull protected final SocialServiceWrapper socialServiceWrapper;
    @NonNull protected final String authToken;
    @NonNull protected final SocialShareHelper socialShareHelper;
    @Nullable protected Subscription sequenceSubscription;

    //<editor-fold desc="Constructors">
    protected SocialConnectSettingViewHolder(
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull UserServiceWrapper userServiceWrapper,
            @NonNull SocialServiceWrapper socialServiceWrapper,
            @NonNull String authToken,
            @NonNull SocialShareHelper socialShareHelper)
    {
        super(currentUserId, userProfileCache, userServiceWrapper);
        this.socialServiceWrapper = socialServiceWrapper;
        this.authToken = authToken;
        this.socialShareHelper = socialShareHelper;
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
                sequence = linkRx();
            }
            else if (isMainLogin())
            {
                sequence = SocialAlertDialogRxUtil.popErrorUnlinkDefaultAccount(activityContext)
                        .flatMap(pair -> Observable.empty());
            }
            else
            {
                sequence = confirmUnLinkRx(activityContext);
            }
            unsubscribe(sequenceSubscription);
            final Activity finalActivityContext = activityContext;
            sequenceSubscription = sequence.observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::updateStatus,
                            e -> onChangeStatusError(finalActivityContext, e));
        }
        return false;
    }

    @NonNull protected Observable<UserProfileDTO> linkRx()
    {
        return socialShareHelper.handleNeedToLink(getSocialNetworkEnum());
    }

    @NonNull protected Observable<UserProfileDTO> confirmUnLinkRx(@NonNull Context activityContext)
    {
        return SocialAlertDialogRxUtil.popConfirmUnlinkAccount(
                activityContext,
                getSocialNetworkEnum())
                .filter(OnDialogClickEvent::isPositive)
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

    @Override protected void updateStatus(@NonNull UserProfileDTO userProfileDTO)
    {
        showIsMainLogin();
        CheckBoxPreference clickablePrefCopy = clickablePref;
        if (clickablePrefCopy != null)
        {
            clickablePrefCopy.setChecked(
                    UserProfileDTOUtil.checkLinkedStatus(userProfileDTO, getSocialNetworkEnum()));
        }
    }

    protected void onChangeStatusError(@NonNull Context activityContext, @NonNull Throwable e)
    {
        if (!(e instanceof CancellationException))
        {
            SocialAlertDialogRxUtil.popErrorSocialAuth(activityContext, e)
                    .subscribe(Actions.empty(), Actions.empty());
        }
    }

    @NonNull abstract protected SocialNetworkEnum getSocialNetworkEnum();

    @StringRes abstract protected int getUnlinkingProgressDialogMessage();

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
