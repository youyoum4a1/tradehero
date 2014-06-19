package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.timeline.form.PublishableFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class DiscussionPostActionButtonsView extends LinearLayout
{
    @InjectView(R.id.btn_share_fb) ToggleButton mFacebookShareButton;
    @InjectView(R.id.btn_share_tw) ToggleButton mTwitterShareButton;
    @InjectView(R.id.btn_share_li) ToggleButton mLinkedInShareButton;
    @InjectView(R.id.btn_share_wb) ToggleButton mWbShareButton;
    @InjectView(R.id.btn_location) ToggleButton mLocationShareButton;
    @InjectView(R.id.switch_share_public) ToggleButton mIsPublic;

    @InjectView(R.id.btn_mention) TextView mMention;
    @InjectView(R.id.btn_security_tag) TextView mSecurityTag;

    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject AlertDialogUtil alertDialogUtil;

    //<editor-fold desc="Constructors">
    public DiscussionPostActionButtonsView(Context context)
    {
        super(context);
    }

    public DiscussionPostActionButtonsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DiscussionPostActionButtonsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        DaggerUtils.inject(this);
        initSocialBtnStatus();
    }

    private void initSocialBtnStatus()
    {
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        mFacebookShareButton.setChecked(userProfileDTO.fbLinked);
        mTwitterShareButton.setChecked(userProfileDTO.twLinked);
        mLinkedInShareButton.setChecked(userProfileDTO.liLinked);
        mWbShareButton.setChecked(userProfileDTO.wbLinked);
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

    @OnClick({
            R.id.btn_share_fb,
            R.id.btn_share_tw,
            R.id.btn_share_li,
            R.id.btn_share_wb
    })
    void onSocialNetworkActionButtonClicked(CompoundButton view)
    {
        SocialNetworkEnum socialNetwork = null;
        boolean ableToShare = false;
        UserProfileDTO userProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        switch (view.getId())
        {
            case R.id.btn_share_fb:
                socialNetwork = SocialNetworkEnum.FB;
                ableToShare = userProfileDTO != null && userProfileDTO.fbLinked;
                break;
            case R.id.btn_share_tw:
                socialNetwork = SocialNetworkEnum.TW;
                ableToShare = userProfileDTO != null && userProfileDTO.twLinked;
                break;
            case R.id.btn_share_li:
                socialNetwork = SocialNetworkEnum.LN;
                ableToShare = userProfileDTO != null && userProfileDTO.liLinked;
                break;
            case R.id.btn_share_wb:
                socialNetwork = SocialNetworkEnum.WB;
                ableToShare = userProfileDTO != null && userProfileDTO.wbLinked;
                break;
        }

        if (socialNetwork != null && !ableToShare && view.isChecked())
        {
            view.setChecked(false);
            alertDialogUtil.popWithOkCancelButton(
                    getContext(),
                    getContext().getString(R.string.link, socialNetwork.getName()),
                    String.format(getContext().getString(R.string.link_description), socialNetwork.getName()),
                    R.string.link_now,
                    R.string.later,
                    new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialog, int which)
                        {
                            openSettingScreen();
                        }
                    },
                    null
            );
        }
    }

    private void openSettingScreen()
    {
        getNavigator().pushFragment(SettingsFragment.class);
    }

    private Navigator getNavigator()
    {
        return ((NavigatorActivity) getContext()).getNavigator();
    }

    public void populate(PublishableFormDTO publishableFormDTO)
    {
        publishableFormDTO.publishToFb = mFacebookShareButton.isChecked();
        publishableFormDTO.publishToTw = mTwitterShareButton.isChecked();
        publishableFormDTO.publishToLi = mLinkedInShareButton.isChecked();
        publishableFormDTO.publishToWb = mWbShareButton.isChecked();

        publishableFormDTO.isPublic = mIsPublic.isChecked();

        // TODO to be done
        publishableFormDTO.geo_alt = null;
        publishableFormDTO.geo_lat = null;
        publishableFormDTO.geo_long = null;
    }


    //<editor-fold desc="To be used in future, we should encapsulate searching for people and stock within this view, instead of doing it in the parent fragment">
    public static interface OnMentionListener
    {
        void onMentioned(UserBaseKey userBaseKey);
    }

    public static interface OnSecurityTaggedListener
    {
        void onTagged(SecurityId securityId);
    }
    //</editor-fold>
}
