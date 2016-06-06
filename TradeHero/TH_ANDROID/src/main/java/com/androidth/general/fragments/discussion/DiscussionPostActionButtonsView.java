package com.androidth.general.fragments.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.androidth.general.common.fragment.HasSelectedItem;
import com.androidth.general.R;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.timeline.form.PublishableFormDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.api.users.UserProfileDTOUtil;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.share.SocialShareHelper;
import com.androidth.general.models.share.preference.SocialSharePreferenceHelper;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.EmptyAction1;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import rx.Observable;
import rx.functions.Action1;

public class DiscussionPostActionButtonsView extends LinearLayout
{
    @Bind(R.id.btn_share_fb) ToggleButton facebookShareButton;
    //@Bind(R.id.btn_share_tw) ToggleButton twitterShareButton;
    //@Bind(R.id.btn_share_li) ToggleButton linkedInShareButton;
    @Bind(R.id.btn_share_wb) ToggleButton weiboShareButton;
    @Bind(R.id.btn_share_wechat) ToggleButton weChatShareButton;
    @Bind(R.id.btn_location) ToggleButton locationShareButton;
    @Bind(R.id.switch_share_public) ToggleButton isPublic;
    @Bind(R.id.mention_widget) MentionActionButtonsView mentionActionButtonsView;

    @Inject UserProfileCacheRx userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject SocialSharePreferenceHelper socialSharePreferenceHelper;
    @Inject SocialShareHelper socialShareHelper;

    //<editor-fold desc="Constructors">
    public DiscussionPostActionButtonsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        HierarchyInjector.inject(this);
        initSocialBtnStatus();
    }

    private void initSocialBtnStatus()
    {
        if (!isInEditMode())
        {
            socialSharePreferenceHelper.load();
            initSocialButton(facebookShareButton, SocialNetworkEnum.FB);
            //initSocialButton(twitterShareButton, SocialNetworkEnum.TW);
            //initSocialButton(linkedInShareButton, SocialNetworkEnum.LN);
            initSocialButton(weChatShareButton, SocialNetworkEnum.WECHAT);
            initSocialButton(weiboShareButton, SocialNetworkEnum.WB);
        }
    }

    private void initSocialButton(
            @NonNull CompoundButton compoundButton,
            @NonNull SocialNetworkEnum socialNetworkEnum)
    {
        compoundButton.setChecked(initialSocialShareCheckedState(socialNetworkEnum));
        compoundButton.setTag(socialNetworkEnum);
    }

    private boolean initialSocialShareCheckedState(@NonNull SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelper.isShareEnabled(socialNetworkEnum, isSocialLinked(socialNetworkEnum));
    }

    private boolean isSocialLinked(@NonNull SocialNetworkEnum socialNetworkEnum)
    {
        UserProfileDTO userProfileDTO = userProfileCache.getCachedValue(currentUserId.toUserBaseKey());
        return userProfileDTO != null
                && UserProfileDTOUtil.checkLinkedStatus(userProfileDTO, socialNetworkEnum);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnCheckedChanged({
            R.id.btn_share_fb,
            R.id.btn_share_wb,
    })
    public void onSocialNetworkCheckedChanged(@NonNull CompoundButton compoundButton, boolean isChecked)
    {
        SocialNetworkEnum socialNetworkEnum = (SocialNetworkEnum) compoundButton.getTag();
        if (socialNetworkEnum != null)
        {
            if (isChecked && !DiscussionPostActionButtonsView.this.isSocialLinked(socialNetworkEnum))
            {
                DiscussionPostActionButtonsView.this.askToLinkSocial(compoundButton, socialNetworkEnum);
                isChecked = false;
            }
            socialSharePreferenceHelper.updateSocialSharePreference(socialNetworkEnum, isChecked);
            compoundButton.setChecked(isChecked);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnCheckedChanged(R.id.btn_share_wechat)
    public void onWeChatCheckedChanged(@NonNull CompoundButton compoundButton, boolean isChecked)
    {
        SocialNetworkEnum socialNetworkEnum = (SocialNetworkEnum) compoundButton.getTag();
        if (socialNetworkEnum != null)
        {
            socialSharePreferenceHelper.updateSocialSharePreference(socialNetworkEnum, isChecked);
            compoundButton.setChecked(isChecked);
        }
    }

    private void askToLinkSocial(
            @NonNull final CompoundButton compoundButton,
            @NonNull final SocialNetworkEnum socialNetworkEnum)
    {
        socialShareHelper.offerToConnect(socialNetworkEnum)
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO userProfileDTO)
                            {
                                compoundButton.setChecked(UserProfileDTOUtil.checkLinkedStatus(userProfileDTO, socialNetworkEnum));
                            }
                        },
                        new EmptyAction1<Throwable>());
    }

    public void populate(@NonNull PublishableFormDTO publishableFormDTO)
    {
        publishableFormDTO.publishToFb = facebookShareButton.isChecked();
        //publishableFormDTO.publishToTw = twitterShareButton.isChecked();
        //publishableFormDTO.publishToLi = linkedInShareButton.isChecked();
        publishableFormDTO.publishToWb = weiboShareButton.isChecked();

        publishableFormDTO.isPublic = isPublic.isChecked();

        // TODO to be done
        publishableFormDTO.geo_alt = null;
        publishableFormDTO.geo_lat = null;
        publishableFormDTO.geo_long = null;
    }

    public boolean isShareEnabled(@NonNull SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelper.isShareEnabled(socialNetworkEnum, isSocialLinked(socialNetworkEnum));
    }

    public void onPostDiscussion()
    {
        socialSharePreferenceHelper.save();
    }

    public void hideSocialButtons()
    {
        facebookShareButton.setVisibility(GONE);
        //twitterShareButton.setVisibility(GONE);
        //linkedInShareButton.setVisibility(GONE);
        weChatShareButton.setVisibility(GONE);
        weiboShareButton.setVisibility(GONE);
    }

    public void setReturnFragmentName(@NonNull String returnFragmentName)
    {
        MentionActionButtonsView mentionActionButtonsViewCopy = mentionActionButtonsView;
        if (mentionActionButtonsViewCopy != null)
        {
            mentionActionButtonsViewCopy.setReturnFragmentName(returnFragmentName);
        }
    }

    @NonNull public Observable<HasSelectedItem> getSelectedItemObservable()
    {
        return mentionActionButtonsView.getSelectedItemObservable();
    }
}
