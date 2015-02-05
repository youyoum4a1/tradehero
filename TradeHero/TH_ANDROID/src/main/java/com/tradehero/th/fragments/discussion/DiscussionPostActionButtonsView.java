package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.timeline.form.PublishableFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.share.SocialShareHelper;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelperNew;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Actions;

public class DiscussionPostActionButtonsView extends LinearLayout
{
    @InjectView(R.id.btn_share_fb) ToggleButton facebookShareButton;
    @InjectView(R.id.btn_share_tw) ToggleButton twitterShareButton;
    @InjectView(R.id.btn_share_li) ToggleButton linkedInShareButton;
    @InjectView(R.id.btn_share_wb) ToggleButton weiboShareButton;
    @InjectView(R.id.btn_share_wechat) ToggleButton weChatShareButton;
    @InjectView(R.id.btn_location) ToggleButton locationShareButton;
    @InjectView(R.id.switch_share_public) ToggleButton isPublic;
    @InjectView(R.id.mention_widget) MentionActionButtonsView mentionActionButtonsView;

    @Inject UserProfileCacheRx userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject SocialSharePreferenceHelperNew socialSharePreferenceHelperNew;
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
        ButterKnife.inject(this);
        HierarchyInjector.inject(this);
        initSocialBtnStatus();
    }

    private void initSocialBtnStatus()
    {
        if (!isInEditMode())
        {
            socialSharePreferenceHelperNew.reload();
            initSocialButton(facebookShareButton, SocialNetworkEnum.FB);
            initSocialButton(twitterShareButton, SocialNetworkEnum.TW);
            initSocialButton(linkedInShareButton, SocialNetworkEnum.LN);
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
        return socialSharePreferenceHelperNew.isShareEnabled(socialNetworkEnum, isSocialLinked(socialNetworkEnum));
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
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnCheckedChanged({
            R.id.btn_share_fb,
            R.id.btn_share_li,
            R.id.btn_share_tw,
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
            socialSharePreferenceHelperNew.updateSocialSharePreference(socialNetworkEnum, isChecked);
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
            socialSharePreferenceHelperNew.updateSocialSharePreference(socialNetworkEnum, isChecked);
            compoundButton.setChecked(isChecked);
        }
    }

    private void askToLinkSocial(
            @NonNull CompoundButton compoundButton,
            @NonNull SocialNetworkEnum socialNetworkEnum)
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
                        Actions.empty());
    }

    public void populate(@NonNull PublishableFormDTO publishableFormDTO)
    {
        publishableFormDTO.publishToFb = facebookShareButton.isChecked();
        publishableFormDTO.publishToTw = twitterShareButton.isChecked();
        publishableFormDTO.publishToLi = linkedInShareButton.isChecked();
        publishableFormDTO.publishToWb = weiboShareButton.isChecked();

        publishableFormDTO.isPublic = isPublic.isChecked();

        // TODO to be done
        publishableFormDTO.geo_alt = null;
        publishableFormDTO.geo_lat = null;
        publishableFormDTO.geo_long = null;
    }

    public boolean isShareEnabled(@NonNull SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelperNew.isShareEnabled(socialNetworkEnum, isSocialLinked(socialNetworkEnum));
    }

    public void onPostDiscussion()
    {
        socialSharePreferenceHelperNew.save();
    }

    public void hideSocialButtons()
    {
        facebookShareButton.setVisibility(GONE);
        twitterShareButton.setVisibility(GONE);
        linkedInShareButton.setVisibility(GONE);
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
