package com.tradehero.th.fragments.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.SocialLinkHelper;
import com.tradehero.th.fragments.social.SocialLinkHelperFactory;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelperNew;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class BaseShareableDialogFragment extends BaseDialogFragment
{
    @Inject SocialSharePreferenceHelperNew socialSharePreferenceHelperNew;
    @Inject SocialLinkHelperFactory socialLinkHelperFactory;
    @Inject protected AlertDialogUtil alertDialogUtil;
    @Inject UserProfileCache userProfileCache;
    @Inject protected CurrentUserId currentUserId;

    @Optional @InjectView(R.id.btn_share_fb) protected ToggleButton mBtnShareFb;
    @InjectView(R.id.btn_share_li) protected ToggleButton mBtnShareLn;
    @Optional @InjectView(R.id.btn_share_tw) protected ToggleButton mBtnShareTw;
    @InjectView(R.id.btn_share_wb) protected ToggleButton mBtnShareWb;

    @InjectView(R.id.btn_share_wechat) protected ToggleButton mBtnShareWeChat;

    SocialLinkHelper socialLinkHelper;
    private AlertDialog mSocialLinkingDialog;

    protected UserProfileDTO userProfileDTO;

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
        UserProfileDTO userProfileCopy = userProfileDTO;
        if (userProfileCopy != null)
        {
            switch (socialNetwork)
            {
                case FB:
                    return userProfileCopy.fbLinked;
                case TW:
                    return userProfileCopy.twLinked;
                case LN:
                    return userProfileCopy.liLinked;
                case WB:
                    return userProfileCopy.wbLinked;
                case WECHAT:
                    return null;
                default:
                    Timber.e(new IllegalArgumentException(), "Unhandled socialNetwork.%s", socialNetwork);
                    return false;
            }
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

    private void linkSocialNetwork(SocialNetworkEnum socialNetworkEnum)
    {
        detachSocialLinkHelper();
        socialLinkHelper = socialLinkHelperFactory.buildSocialLinkerHelper(socialNetworkEnum);
        socialLinkHelper.link(new SocialLinkingCallback(socialNetworkEnum));
    }

    @Override public void onDestroyView()
    {
        detachSocialLinkHelper();
        destroySocialLinkDialog();
        super.onDestroyView();
    }

    private void detachSocialLinkHelper()
    {
        if (socialLinkHelper != null)
        {
            socialLinkHelper.setSocialLinkingCallback(null);
            socialLinkHelper = null;
        }
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

    private class SocialLinkingCallback implements retrofit.Callback<UserProfileDTO>
    {
        final SocialNetworkEnum socialNetworkEnum;

        SocialLinkingCallback(final SocialNetworkEnum socialNetworkEnum)
        {
            this.socialNetworkEnum = socialNetworkEnum;
        }

        @Override public void success(UserProfileDTO userProfileDTO, Response response)
        {
            onSuccessSocialLink(userProfileDTO, socialNetworkEnum);
        }

        @Override public void failure(RetrofitError retrofitError)
        {
        }
    }
}
