package com.tradehero.th.fragments.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
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
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.share.SocialShareHelper;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelperNew;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.rx.view.ViewArrayObservable;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.SocialAlertDialogRxUtil;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class BaseShareableDialogFragment extends BaseDialogFragment
{
    @Inject SocialSharePreferenceHelperNew socialSharePreferenceHelperNew;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject protected SocialShareHelper socialShareHelper;

    @InjectView(R.id.btn_share_wechat) public ToggleButton mBtnShareWeChat;
    Subscription weChatLinkingSubscription;
    @Optional @InjectViews({
            R.id.btn_share_fb,
            R.id.btn_share_li,
            R.id.btn_share_tw,
            R.id.btn_share_wb})
    SocialLinkToggleButton[] socialLinkingButtons;
    Subscription socialLinkingSubscription;

    @Nullable protected UserProfileDTO userProfileDTO;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
        onStopSubscriptions.add(AppObservable.bindFragment(
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
                        new Action1<OnClickEvent>()
                        {
                            @Override public void call(OnClickEvent event)
                            {
                                socialSharePreferenceHelperNew.updateSocialSharePreference(
                                        SocialNetworkEnum.WECHAT,
                                        ((ToggleButton) event.view()).isChecked());
                            }
                        },
                        new ToastOnErrorAction());
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
                        .flatMap(new Func1<SocialLinkToggleButton, Observable<? extends Pair<SocialLinkToggleButton, UserProfileDTO>>>()
                        {
                            @Override public Observable<? extends Pair<SocialLinkToggleButton, UserProfileDTO>> call(
                                    SocialLinkToggleButton socialLinkToggleButton)
                            {
                                final SocialNetworkEnum socialNetwork = socialLinkToggleButton.getSocialNetworkEnum();
                                Boolean socialLinked = BaseShareableDialogFragment.this.isSocialLinked(socialNetwork);
                                if (socialLinked != null && socialLinked)
                                {
                                    return Observable.just(Pair.create(socialLinkToggleButton, userProfileDTO));
                                }
                                return BaseShareableDialogFragment.this.createSocialAuthObservable(socialLinkToggleButton, socialNetwork);
                            }
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
                                SocialAlertDialogRxUtil.popErrorSocialAuth(getActivity(), e).subscribe(
                                        new EmptyAction1<OnDialogClickEvent>(),
                                        new EmptyAction1<Throwable>());
                            }
                        }
                );
    }

    protected Observable<SocialLinkToggleButton> createCheckedLinkingObservable()
    {
        return ViewArrayObservable.clicks(socialLinkingButtons, false)
                .flatMap(new Func1<OnClickEvent, Observable<SocialLinkToggleButton>>()
                {
                    @Override public Observable<SocialLinkToggleButton> call(OnClickEvent event)
                    {
                        SocialLinkToggleButton button = (SocialLinkToggleButton) event.view();
                        if (!button.isChecked())
                        {
                            socialSharePreferenceHelperNew.updateSocialSharePreference(
                                    button.getSocialNetworkEnum(),
                                    false);
                            return Observable.empty();
                        }
                        button.setChecked(false);
                        return Observable.just(button);
                    }
                });
    }

    protected Observable<Pair<SocialLinkToggleButton, UserProfileDTO>> createSocialAuthObservable(
            @NonNull final SocialLinkToggleButton socialLinkToggleButton,
            @NonNull final SocialNetworkEnum socialNetwork)
    {
        return SocialAlertDialogRxUtil.popNeedToLinkSocial(getActivity(), socialNetwork)
                .flatMap(new Func1<OnDialogClickEvent, Observable<? extends UserProfileDTO>>()
                {
                    @Override public Observable<? extends UserProfileDTO> call(OnDialogClickEvent event)
                    {
                        return socialShareHelper.handleNeedToLink(event, socialNetwork);
                    }
                })
                .map(new Func1<UserProfileDTO, Pair<SocialLinkToggleButton, UserProfileDTO>>()
                {
                    @Override public Pair<SocialLinkToggleButton, UserProfileDTO> call(UserProfileDTO userProfileDTO)
                    {
                        return Pair.create(socialLinkToggleButton, userProfileDTO);
                    }
                });
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
        return UserProfileDTOUtil.checkLinkedStatus(userProfileCopy, socialNetwork);
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
