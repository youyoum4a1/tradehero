package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.timeline.form.PublishableFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelperNew;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import rx.Observable;

public class DiscussionPostActionButtonsView extends LinearLayout
{
    @InjectView(R.id.btn_share_fb) ToggleButton mFacebookShareButton;
    @InjectView(R.id.btn_share_tw) ToggleButton mTwitterShareButton;
    @InjectView(R.id.btn_share_li) ToggleButton mLinkedInShareButton;
    @InjectView(R.id.btn_share_wb) ToggleButton mWeiboShareButton;
    @InjectView(R.id.btn_share_wechat) ToggleButton mWechatShareButton;
    @InjectView(R.id.btn_location) ToggleButton mLocationShareButton;
    @InjectView(R.id.switch_share_public) ToggleButton mIsPublic;
    @InjectView(R.id.mention_widget) MentionActionButtonsView mentionActionButtonsView;

    @Inject UserProfileCacheRx userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject SocialSharePreferenceHelperNew socialSharePreferenceHelperNew;
    @Inject DashboardNavigator navigator;

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
            initSocialButton(mFacebookShareButton, SocialNetworkEnum.FB);
            initSocialButton(mTwitterShareButton, SocialNetworkEnum.TW);
            initSocialButton(mLinkedInShareButton, SocialNetworkEnum.LN);
            initSocialButton(mWechatShareButton, SocialNetworkEnum.WECHAT, createCheckedChangeListenerForWechat());
            initSocialButton(mWeiboShareButton, SocialNetworkEnum.WB);
        }
    }

    private void initSocialButton(CompoundButton compoundButton, SocialNetworkEnum socialNetworkEnum)
    {
        initSocialButton(compoundButton, socialNetworkEnum, createCheckedChangeListener());
    }

    private void initSocialButton(CompoundButton compoundButton, SocialNetworkEnum socialNetworkEnum, CompoundButton.OnCheckedChangeListener onCheckedChangeListener)
    {
        compoundButton.setChecked(initialSocialShareCheckedState(socialNetworkEnum));
        compoundButton.setTag(socialNetworkEnum);
        compoundButton.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    private boolean initialSocialShareCheckedState(SocialNetworkEnum socialNetworkEnum)
    {
        return socialSharePreferenceHelperNew.isShareEnabled(socialNetworkEnum, isSocialLinked(socialNetworkEnum));
    }

    private boolean isSocialLinked(@NonNull SocialNetworkEnum socialNetworkEnum)
    {
        UserProfileDTO userProfileDTO = userProfileCache.getCachedValue(currentUserId.toUserBaseKey());

        if (userProfileDTO != null)
        {
            switch (socialNetworkEnum)
            {
                case FB:
                    return userProfileDTO.fbLinked;
                case TW:
                    return userProfileDTO.twLinked;
                case LN:
                    return userProfileDTO.liLinked;
                case WB:
                    return userProfileDTO.wbLinked;
                default:
                    return false;
            }
        }
        return false;
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

    private CompoundButton.OnCheckedChangeListener createCheckedChangeListener()
    {
        return (compoundButton, isChecked) -> {
            SocialNetworkEnum socialNetworkEnum = (SocialNetworkEnum) compoundButton.getTag();
            if (socialNetworkEnum != null)
            {
                if(isChecked && !isSocialLinked(socialNetworkEnum))
                {
                    askToLinkSocial(socialNetworkEnum);
                    isChecked = false;
                }
                socialSharePreferenceHelperNew.updateSocialSharePreference(socialNetworkEnum, isChecked);
                compoundButton.setChecked(isChecked);
            }
        };
    }

    private CompoundButton.OnCheckedChangeListener createCheckedChangeListenerForWechat()
    {
        return (compoundButton, isChecked) -> {
            SocialNetworkEnum socialNetworkEnum = (SocialNetworkEnum) compoundButton.getTag();
            if (socialNetworkEnum != null)
            {
                socialSharePreferenceHelperNew.updateSocialSharePreference(socialNetworkEnum, isChecked);
                compoundButton.setChecked(isChecked);
            }
        };
    }

    private void askToLinkSocial(SocialNetworkEnum socialNetworkEnum)
    {
        AlertDialogUtil.popWithOkCancelButton(
                getContext(),
                getContext().getString(R.string.link, socialNetworkEnum.getName()),
                String.format(getContext().getString(R.string.link_description), socialNetworkEnum.getName()),
                R.string.link_now,
                R.string.later,
                (dialog, which) -> openSettingScreen(),
                null
        );
    }

    private void openSettingScreen()
    {
        navigator.pushFragment(SettingsFragment.class);
    }

    public void populate(PublishableFormDTO publishableFormDTO)
    {
        publishableFormDTO.publishToFb = mFacebookShareButton.isChecked();
        publishableFormDTO.publishToTw = mTwitterShareButton.isChecked();
        publishableFormDTO.publishToLi = mLinkedInShareButton.isChecked();
        publishableFormDTO.publishToWb = mWeiboShareButton.isChecked();

        publishableFormDTO.isPublic = mIsPublic.isChecked();

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
        mFacebookShareButton.setVisibility(GONE);
        mTwitterShareButton.setVisibility(GONE);
        mLinkedInShareButton.setVisibility(GONE);
        mWechatShareButton.setVisibility(GONE);
        mWeiboShareButton.setVisibility(GONE);
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
