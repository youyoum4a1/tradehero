package com.tradehero.th.fragments.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.auth.AuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.auth.SocialAuthenticationProvider;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelperNew;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.rx.AlertDialogObserver;
import com.tradehero.th.utils.AlertDialogUtil;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;

public class BaseShareableDialogFragment extends BaseDialogFragment
{
    @Inject SocialSharePreferenceHelperNew socialSharePreferenceHelperNew;
    @Inject protected AlertDialogUtil alertDialogUtil;
    @Inject UserProfileCache userProfileCache;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileDTOUtil userProfileDTOUtil;
    @Inject @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviders;

    @Optional @InjectView(R.id.btn_share_fb) public ToggleButton mBtnShareFb;
    @InjectView(R.id.btn_share_li) public ToggleButton mBtnShareLn;
    @Optional @InjectView(R.id.btn_share_tw) public ToggleButton mBtnShareTw;
    @InjectView(R.id.btn_share_wb) public ToggleButton mBtnShareWb;
    @InjectView(R.id.btn_share_wechat) public ToggleButton mBtnShareWeChat;

    @Optional @InjectViews({
            R.id.btn_share_fb,
            R.id.btn_share_li,
            R.id.btn_share_tw,
            R.id.btn_share_wb})
    ToggleButton[] socialLinkingButtons;

    public AlertDialog mSocialLinkingDialog;

    @Nullable protected UserProfileDTO userProfileDTO;

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        linkWith(userProfileCache.get(currentUserId.toUserBaseKey()));
        setPublishToShareBySetting();
        initSocialButtons();
    }

    private void initSocialButtons()
    {
        initSocialButton(mBtnShareFb, SocialNetworkEnum.FB);
        initSocialButton(mBtnShareTw, SocialNetworkEnum.TW);
        initSocialButton(mBtnShareLn, SocialNetworkEnum.LN);
        initSocialButton(mBtnShareWeChat, SocialNetworkEnum.WECHAT, createCheckedChangeListenerForWechat());
        initSocialButton(mBtnShareWb, SocialNetworkEnum.WB);
    }

    public boolean isSocialLinkedOr(SocialNetworkEnum socialNetwork, boolean orElse)
    {
        @Nullable Boolean socialLinked = isSocialLinked(socialNetwork);
        return socialLinked != null ? socialLinked : orElse;
    }

    private void initSocialButton(CompoundButton socialButton, SocialNetworkEnum socialNetworkEnum)
    {
        initSocialButton(socialButton, socialNetworkEnum, createCheckedChangeListener());
    }

    private void initSocialButton(CompoundButton socialButton, SocialNetworkEnum socialNetworkEnum,
            CompoundButton.OnCheckedChangeListener onCheckedChangedListener)
    {
        if (socialButton != null)
        {
            socialButton.setTag(socialNetworkEnum);
            socialButton.setChecked(initialShareButtonState(socialNetworkEnum));
            socialButton.setOnCheckedChangeListener(onCheckedChangedListener);
        }
    }

    protected boolean initialShareButtonState(@NotNull SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelperNew.isShareEnabled(
                socialNetworkEnum,
                isSocialLinkedOr(socialNetworkEnum, false));
    }

    @Nullable public Boolean isSocialLinked(SocialNetworkEnum socialNetwork)
    {
        if (socialNetwork == SocialNetworkEnum.WECHAT)
        {
            return null;
        }
        UserProfileDTO userProfileCopy = userProfileDTO;
        if (userProfileCopy != null)
        {
            return userProfileDTOUtil.checkLinkedStatus(userProfileCopy, socialNetwork);
        }
        return null;
    }

    public CompoundButton getFacebookShareButton()
    {
        return mBtnShareFb;
    }

    public CompoundButton getTwitterShareButton()
    {
        return mBtnShareTw;
    }

    public CompoundButton getLinkedInShareButton()
    {
        return mBtnShareLn;
    }

    public CompoundButton getWeiboShareButton()
    {
        return mBtnShareWb;
    }

    public CompoundButton getWeChatShareButton()
    {
        return mBtnShareWeChat;
    }

    public void setPublishEnable(SocialNetworkEnum socialNetwork)
    {
        socialSharePreferenceHelperNew.updateSocialSharePreference(socialNetwork, true);
        switch (socialNetwork)
        {
            case FB:
                mBtnShareFb.setChecked(true);
                break;
            case TW:
                mBtnShareTw.setChecked(true);
                break;
            case LN:
                mBtnShareLn.setChecked(true);
                break;
            case WB:
                mBtnShareWb.setChecked(true);
                break;
        }
    }

    protected boolean shareForTransaction(@NotNull SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelperNew.isShareEnabled(socialNetworkEnum, isSocialLinkedOr(socialNetworkEnum, true));
    }

    public void setPublishToShareBySetting()
    {
        socialSharePreferenceHelperNew.load();
    }

    public void onSuccessSocialLink(UserProfileDTO userProfileDTO, SocialNetworkEnum socialNetworkEnum)
    {
        linkWith(userProfileDTO);
        setPublishEnable(socialNetworkEnum);
    }

    protected void linkWith(UserProfileDTO updatedUserProfileDTO)
    {
        this.userProfileDTO = updatedUserProfileDTO;
    }

    protected void saveShareSettings()
    {
        socialSharePreferenceHelperNew.save();
    }

    @NotNull protected List<SocialNetworkEnum> getEnabledSharePreferences()
    {
        return socialSharePreferenceHelperNew.getAllEnabledSharePreferences();
    }

    public AlertDialog getSocialLinkingDialog()
    {
        return mSocialLinkingDialog;
    }

    public void askToLinkAccountToSocial(final SocialNetworkEnum socialNetwork)
    {
        mSocialLinkingDialog = alertDialogUtil.popWithOkCancelButton(
                getActivity(),
                getActivity().getApplicationContext().getString(R.string.link, socialNetwork.getName()),
                getActivity().getApplicationContext().getString(R.string.link_description, socialNetwork.getName()),
                R.string.link_now,
                R.string.later,
                new DialogInterface.OnClickListener()//Ok
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        linkSocialNetwork(socialNetwork);
                    }
                },
                new DialogInterface.OnClickListener()//Cancel
                {
                    @Override public void onClick(DialogInterface dialogInterface, int i)
                    {
                        alertDialogUtil.dismissProgressDialog();
                    }
                },
                new DialogInterface.OnDismissListener()
                {
                    @Override public void onDismiss(DialogInterface dialogInterface)
                    {
                        destroySocialLinkDialog();
                    }
                }
        );
    }

    private void linkSocialNetwork(@NotNull final SocialNetworkEnum socialNetworkEnum)
    {
        alertDialogUtil.showProgressDialog(
                getActivity(),
                getString(
                        R.string.authentication_connecting_to,
                        getString(socialNetworkEnum.nameResId)));
        AuthenticationProvider socialAuthenticationProvider = authenticationProviders.get(socialNetworkEnum);
        AndroidObservable.bindFragment(
                this,
                ((SocialAuthenticationProvider) socialAuthenticationProvider)
                        .socialLink(getActivity()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new AlertDialogObserver<UserProfileDTO>(getActivity(), alertDialogUtil)
                {
                    @Override public void onNext(UserProfileDTO args)
                    {
                        onSuccessSocialLink(args, socialNetworkEnum);
                    }

                    @Override public void onCompleted()
                    {
                        alertDialogUtil.dismissProgressDialog();
                    }

                    @Override public void onError(Throwable e)
                    {
                        super.onError(e);
                        alertDialogUtil.dismissProgressDialog();
                    }
                });
    }

    @Override public void onDestroyView()
    {
        destroySocialLinkDialog();
        super.onDestroyView();
    }

    private void destroySocialLinkDialog()
    {
        if (mSocialLinkingDialog != null && mSocialLinkingDialog.isShowing())
        {
            mSocialLinkingDialog.dismiss();
        }
        mSocialLinkingDialog = null;
    }

    private CompoundButton.OnCheckedChangeListener createCheckedChangeListenerForWechat()
    {
        return new CompoundButton.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (!compoundButton.isPressed())
                {
                    return;
                }
                SocialNetworkEnum networkEnum = (SocialNetworkEnum) compoundButton.getTag();
                socialSharePreferenceHelperNew.updateSocialSharePreference(networkEnum, isChecked);
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener createCheckedChangeListener()
    {
        return new CompoundButton.OnCheckedChangeListener()
        {
            @Override public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked)
            {
                if (!compoundButton.isPressed())
                {
                    return;
                }
                SocialNetworkEnum networkEnum = (SocialNetworkEnum) compoundButton.getTag();
                Boolean socialLinked = isSocialLinked(networkEnum);
                if (isChecked && (socialLinked == null || !socialLinked))
                {
                    if (socialLinked != null)
                    {
                        askToLinkAccountToSocial(networkEnum);
                    }
                    isChecked = false;
                }

                compoundButton.setChecked(isChecked);
                socialSharePreferenceHelperNew.updateSocialSharePreference(networkEnum, isChecked);
            }
        };
    }
}
