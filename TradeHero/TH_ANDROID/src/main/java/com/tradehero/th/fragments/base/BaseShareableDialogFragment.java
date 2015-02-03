package com.tradehero.th.fragments.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.Optional;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.SocialLinkToggleButton;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.auth.AuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelperNew;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.dialog.AlertButtonClickedFilterFunc1;
import com.tradehero.th.rx.dialog.AlertDialogOnSubscribe;
import com.tradehero.th.rx.view.ViewArrayObservable;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.SocialAlertDialogRxUtil;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.view.ViewObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.internal.util.SubscriptionList;

public class BaseShareableDialogFragment extends BaseDialogFragment
{
    @Inject SocialSharePreferenceHelperNew socialSharePreferenceHelperNew;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject protected UserProfileDTOUtil userProfileDTOUtil;
    @Inject protected SocialAlertDialogRxUtil socialAlertDialogRxUtil;
    @Inject @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviders;

    @InjectView(R.id.btn_share_wechat) public ToggleButton mBtnShareWeChat;
    Subscription weChatLinkingSubscription;
    @Optional @InjectViews({
            R.id.btn_share_fb,
            R.id.btn_share_li,
            R.id.btn_share_tw,
            R.id.btn_share_wb})
    SocialLinkToggleButton[] socialLinkingButtons;
    Subscription socialLinkingSubscription;
    @NonNull protected SubscriptionList subscriptions;

    @Nullable protected UserProfileDTO userProfileDTO;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        subscriptions = new SubscriptionList();
        socialSharePreferenceHelperNew.reload();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        registerWeChatButton();
        registerSocialButtons();
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchUserProfile();
    }

    @Override public void onStop()
    {
        subscriptions.unsubscribe();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        unsubscribeWeChatButton();
        unsubscribeSocialLinkingButtons();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    //<editor-fold desc="User Profile">
    protected void fetchUserProfile()
    {
        subscriptions.add(AppObservable.bindFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey()))
                .subscribe(createUserProfileCacheObserver()));
    }

    protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new BaseShareableDialogUserProfileCacheObserver();
    }

    protected class BaseShareableDialogUserProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            linkWith(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(new THException(e));
        }
    }

    protected void linkWith(UserProfileDTO updatedUserProfileDTO)
    {
        this.userProfileDTO = updatedUserProfileDTO;
        for (ToggleButton toggleButton : socialLinkingButtons)
        {
            if (toggleButton != null)
            {
                toggleButton.setEnabled(true);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="WeChat button">
    private void unsubscribeWeChatButton()
    {
        Subscription subscription = weChatLinkingSubscription;
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    private void registerWeChatButton()
    {
        mBtnShareWeChat.setChecked(initialShareButtonState(SocialNetworkEnum.WB));
        unsubscribeWeChatButton();
        weChatLinkingSubscription = AppObservable.bindFragment(this, ViewObservable.clicks(mBtnShareWeChat, false))
                .subscribe(
                        event -> socialSharePreferenceHelperNew.updateSocialSharePreference(
                                SocialNetworkEnum.WECHAT,
                                ((ToggleButton) event.view()).isChecked()),
                        e -> THToast.show(new THException(e)));
    }
    //</editor-fold>

    //<editor-fold desc="Social buttons">
    protected void unsubscribeSocialLinkingButtons()
    {
        Subscription subscriptionCopy = socialLinkingSubscription;
        if (subscriptionCopy != null)
        {
            subscriptionCopy.unsubscribe();
        }
    }

    private void registerSocialButtons()
    {
        for (SocialLinkToggleButton toggleButton : socialLinkingButtons)
        {
            if (toggleButton != null)
            {
                toggleButton.setChecked(initialShareButtonState(toggleButton.getSocialNetworkEnum()));
            }
        }
        unsubscribeSocialLinkingButtons();
        socialLinkingSubscription = AppObservable.bindFragment(
                this,
                createCheckedLinkingObservable()
                        .flatMap(socialLinkToggleButton -> {
                            final SocialNetworkEnum socialNetwork = socialLinkToggleButton.getSocialNetworkEnum();
                            Boolean socialLinked = isSocialLinked(socialNetwork);
                            if (socialLinked != null && socialLinked)
                            {
                                return Observable.just(Pair.create(socialLinkToggleButton, userProfileDTO));
                            }
                            return createSocialAuthObservable(socialLinkToggleButton, socialNetwork);
                        }))
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        AlertDialogUtil.dismissProgressDialog();
                        registerSocialButtons();
                    }
                })
                .subscribe(new Action1<Pair<SocialLinkToggleButton, UserProfileDTO>>()
                           {
                               @Override public void call(Pair<SocialLinkToggleButton, UserProfileDTO> args)
                               {
                                   setPublishEnable(args.first.getSocialNetworkEnum());
                                   linkWith(args.second);
                               }
                           },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable e)
                            {
                                socialAlertDialogRxUtil.popErrorSocialAuth(getActivity(), e).subscribe(Actions.empty(), Actions.empty());
                            }
                        }
                );
    }

    protected Observable<SocialLinkToggleButton> createCheckedLinkingObservable()
    {
        return ViewArrayObservable.clicks(socialLinkingButtons, false)
                .map(event -> {
                    SocialLinkToggleButton socialLinkToggleButton = (SocialLinkToggleButton) event.view();
                    if (!socialLinkToggleButton.isChecked())
                    {
                        socialSharePreferenceHelperNew.updateSocialSharePreference(
                                socialLinkToggleButton.getSocialNetworkEnum(),
                                false);
                    }
                    return socialLinkToggleButton;
                })
                .filter(CompoundButton::isChecked)
                .doOnNext(button -> button.setChecked(false));
    }

    protected Observable<Pair<SocialLinkToggleButton, UserProfileDTO>> createSocialAuthObservable(
            @NonNull final SocialLinkToggleButton socialLinkToggleButton,
            @NonNull final SocialNetworkEnum socialNetwork)
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
        alertBuilder.setIcon(R.drawable.th_app_logo)
                .setCancelable(true)
                .setTitle(getString(R.string.link, socialNetwork.getName()))
                .setMessage(getString(R.string.link_description, socialNetwork.getName()));
        AlertDialogOnSubscribe.Builder onSubscribeBuilder = new AlertDialogOnSubscribe.Builder(alertBuilder);
        onSubscribeBuilder.setPositiveButton(getString(R.string.link_now))
                .setNegativeButton(getString(R.string.later));

        return socialAlertDialogRxUtil.popNeedToLinkSocial(getActivity(), socialNetwork)
                .filter(new AlertButtonClickedFilterFunc1(DialogInterface.BUTTON_POSITIVE))
                .doOnNext(dialogResult -> AlertDialogUtil.showProgressDialog(
                        getActivity(),
                        getString(
                                R.string.authentication_connecting_to,
                                getString(socialNetwork.nameResId))))
                .flatMap(dialogInterfaceIntegerPair -> {
                    AuthenticationProvider socialAuthenticationProvider = authenticationProviders.get(socialNetwork);
                    return ((SocialAuthenticationProvider) socialAuthenticationProvider)
                            .socialLink(getActivity())
                            .map(userProfileDTO1 -> Pair.create(socialLinkToggleButton, userProfileDTO1));
                })
                .doOnCompleted(AlertDialogUtil::dismissProgressDialog);
    }
    //</editor-fold>

    protected boolean initialShareButtonState(@NonNull SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelperNew.isShareEnabled(
                socialNetworkEnum,
                isSocialLinkedOr(socialNetworkEnum, false));
    }

    public boolean isSocialLinkedOr(SocialNetworkEnum socialNetwork, boolean orElse)
    {
        Boolean socialLinked = isSocialLinked(socialNetwork);
        return socialLinked != null ? socialLinked : orElse;
    }

    @Nullable public Boolean isSocialLinked(SocialNetworkEnum socialNetwork)
    {
        UserProfileDTO userProfileCopy = userProfileDTO;
        if (socialNetwork == SocialNetworkEnum.WECHAT || userProfileCopy == null)
        {
            return null;
        }
        return userProfileDTOUtil.checkLinkedStatus(userProfileCopy, socialNetwork);
    }

    public void setPublishEnable(@NonNull SocialNetworkEnum socialNetwork)
    {
        socialSharePreferenceHelperNew.updateSocialSharePreference(socialNetwork, true);
        for (SocialLinkToggleButton toggleButton : socialLinkingButtons)
        {
            if (toggleButton != null && toggleButton.getSocialNetworkEnum().equals(socialNetwork))
            {
                toggleButton.setChecked(true);
            }
        }
    }

    protected boolean shareForTransaction(@NonNull SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelperNew.isShareEnabled(socialNetworkEnum, isSocialLinkedOr(socialNetworkEnum, true));
    }

    protected void saveShareSettings()
    {
        socialSharePreferenceHelperNew.save();
    }

    @NonNull protected List<SocialNetworkEnum> getEnabledSharePreferences()
    {
        return socialSharePreferenceHelperNew.getAllEnabledSharePreferences();
    }
}
