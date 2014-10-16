package com.tradehero.th.fragments.base;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.ToggleButton;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.Optional;
import com.tradehero.common.persistence.DTOCacheNew;
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
import com.tradehero.th.fragments.base.dialog.AlertDialogOkCancelOnSubscribe;
import com.tradehero.th.fragments.base.dialog.DialogResult;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelperNew;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.rx.AlertDialogObserver;
import com.tradehero.th.rx.view.CompoundButtonIsCheckedFunc1;
import com.tradehero.th.rx.view.CompoundButtonSetCheckedAction1;
import com.tradehero.th.rx.view.ViewArrayObservable;
import com.tradehero.th.utils.AlertDialogUtil;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.EmptyObserver;

public class BaseShareableDialogFragment extends BaseDialogFragment
{
    @Inject SocialSharePreferenceHelperNew socialSharePreferenceHelperNew;
    @Inject protected AlertDialogUtil alertDialogUtil;
    @Inject protected CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    @Inject protected UserProfileDTOUtil userProfileDTOUtil;
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

    @Nullable protected UserProfileDTO userProfileDTO;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userProfileCacheListener = createUserProfileCacheListener();
        socialSharePreferenceHelperNew.reload();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
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
        detachUserProfileCache();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        unsubscribeWeChatButton();
        unsubscribeSocialLinkingButtons();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        userProfileCacheListener = null;
        super.onDestroy();
    }

    //<editor-fold desc="User Profile">
    protected void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new BaseShareableDialogUserProfileCacheListener();
    }

    protected class BaseShareableDialogUserProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(new THException(error));
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
        weChatLinkingSubscription = AndroidObservable.bindFragment(this, ViewObservable.clicks(mBtnShareWeChat, false))
                .subscribe(new EmptyObserver<ToggleButton>()
                {
                    @Override public void onNext(ToggleButton toggleButton)
                    {
                        super.onNext(toggleButton);
                        socialSharePreferenceHelperNew.updateSocialSharePreference(SocialNetworkEnum.WECHAT, toggleButton.isChecked());
                    }
                });
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
        socialLinkingSubscription = AndroidObservable.bindFragment(
                this,
                createCheckedLinkingObservable()
                        .flatMap(new Func1<SocialLinkToggleButton, Observable<Pair<SocialLinkToggleButton, UserProfileDTO>>>()
                        {
                            @Override public Observable<Pair<SocialLinkToggleButton, UserProfileDTO>> call(
                                    final SocialLinkToggleButton socialLinkToggleButton)
                            {
                                final SocialNetworkEnum socialNetwork = socialLinkToggleButton.getSocialNetworkEnum();
                                Boolean socialLinked = isSocialLinked(socialNetwork);
                                if (socialLinked != null && socialLinked)
                                {
                                    return Observable.just(Pair.create(socialLinkToggleButton, userProfileDTO));
                                }
                                return createSocialAuthObservable(socialLinkToggleButton, socialNetwork);
                            }
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AlertDialogObserver<Pair<SocialLinkToggleButton, UserProfileDTO>>(getActivity(), alertDialogUtil)
                {
                    @Override public void onNext(Pair<SocialLinkToggleButton, UserProfileDTO> args)
                    {
                        setPublishEnable(args.first.getSocialNetworkEnum());
                        linkWith(args.second);
                    }

                    @Override public void onCompleted()
                    {
                        alertDialogUtil.dismissProgressDialog();
                        registerSocialButtons();
                    }

                    @Override public void onError(Throwable e)
                    {
                        super.onError(e);
                        alertDialogUtil.dismissProgressDialog();
                        registerSocialButtons();
                    }
                });
    }

    protected Observable<SocialLinkToggleButton> createCheckedLinkingObservable()
    {
        return ViewArrayObservable.clicks(socialLinkingButtons, false)
                .doOnNext(new Action1<SocialLinkToggleButton>()
                {
                    @Override public void call(SocialLinkToggleButton socialLinkToggleButton)
                    {
                        if (!socialLinkToggleButton.isChecked())
                        {
                            socialSharePreferenceHelperNew.updateSocialSharePreference(
                                    socialLinkToggleButton.getSocialNetworkEnum(),
                                    false);
                        }
                    }
                })
                .filter(new CompoundButtonIsCheckedFunc1())
                .doOnNext(new CompoundButtonSetCheckedAction1(false));
    }

    protected Observable<Pair<SocialLinkToggleButton, UserProfileDTO>> createSocialAuthObservable(
            @NotNull final SocialLinkToggleButton socialLinkToggleButton,
            @NotNull final SocialNetworkEnum socialNetwork)
    {
        return Observable.create(new AlertDialogOkCancelOnSubscribe(alertDialogUtil,
                getActivity(),
                getActivity().getString(R.string.link, socialNetwork.getName()),
                getActivity().getString(R.string.link_description, socialNetwork.getName()),
                R.string.link_now,
                R.string.later))
                .filter(new Func1<DialogResult, Boolean>()
                {
                    @Override public Boolean call(DialogResult dialogResult)
                    {
                        return dialogResult.equals(DialogResult.OK);
                    }
                })
                .doOnNext(new Action1<DialogResult>()
                {
                    @Override public void call(DialogResult dialogResult)
                    {
                        alertDialogUtil.showProgressDialog(
                                getActivity(),
                                getString(
                                        R.string.authentication_connecting_to,
                                        getString(socialNetwork.nameResId)));
                    }
                })
                .flatMap(new Func1<DialogResult, Observable<Pair<SocialLinkToggleButton, UserProfileDTO>>>()
                {
                    @Override public Observable<Pair<SocialLinkToggleButton, UserProfileDTO>> call(DialogResult dialogResult)
                    {
                        AuthenticationProvider socialAuthenticationProvider = authenticationProviders.get(socialNetwork);
                        return ((SocialAuthenticationProvider) socialAuthenticationProvider)
                                .socialLink(getActivity())
                                .map(new Func1<UserProfileDTO, Pair<SocialLinkToggleButton, UserProfileDTO>>()
                                {
                                    @Override public Pair<SocialLinkToggleButton, UserProfileDTO> call(UserProfileDTO userProfileDTO)
                                    {
                                        return Pair.create(socialLinkToggleButton, userProfileDTO);
                                    }
                                });
                    }
                });
    }
    //</editor-fold>

    protected boolean initialShareButtonState(@NotNull SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelperNew.isShareEnabled(
                socialNetworkEnum,
                isSocialLinkedOr(socialNetworkEnum, false));
    }

    public boolean isSocialLinkedOr(SocialNetworkEnum socialNetwork, boolean orElse)
    {
        @Nullable Boolean socialLinked = isSocialLinked(socialNetwork);
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

    public void setPublishEnable(@NotNull SocialNetworkEnum socialNetwork)
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

    protected boolean shareForTransaction(@NotNull SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelperNew.isShareEnabled(socialNetworkEnum, isSocialLinkedOr(socialNetworkEnum, true));
    }

    protected void saveShareSettings()
    {
        socialSharePreferenceHelperNew.save();
    }

    @NotNull protected List<SocialNetworkEnum> getEnabledSharePreferences()
    {
        return socialSharePreferenceHelperNew.getAllEnabledSharePreferences();
    }
}
