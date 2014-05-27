package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemView;
import com.tradehero.th.fragments.news.NewsDialogLayout;
import com.tradehero.th.fragments.news.NewsTranslationSelfDisplayer;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.translation.TranslationCache;
import com.tradehero.th.persistence.translation.TranslationKey;
import com.tradehero.th.widget.VotePair;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import javax.inject.Inject;

public class SecurityDiscussionItemView
        extends AbstractDiscussionItemView<DiscussionKey>
        implements View.OnClickListener
{
    @InjectView(R.id.discussion_user_picture) ImageView discussionUserPicture;
    @InjectView(R.id.user_profile_name) TextView userProfileName;
    @InjectView(R.id.vote_pair) VotePair discussionVotePair;

    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userProfilePictureTransformation;
    @Inject TranslationCache translationCache;

    private DiscussionDTO discussionDTO;
    private UserBaseDTO userBaseDTO;
    private DTOCache.GetOrFetchTask<TranslationKey, TranslationResult> translationTask;

    //<editor-fold desc="Constructors">
    public SecurityDiscussionItemView(Context context)
    {
        super(context);
    }

    public SecurityDiscussionItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SecurityDiscussionItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        discussionUserPicture.setOnClickListener(this);
        if (discussionVotePair != null)
        {
            discussionVotePair.setDownVote(false);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        detachTranslationTask();
        resetView();
        discussionUserPicture.setOnClickListener(null);
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    private void detachTranslationTask()
    {
        if (translationTask != null)
        {
            translationTask.setListener(null);
        }
        translationTask = null;
    }

    @Override
    protected void linkWith(AbstractDiscussionDTO abstractDiscussionDTO, boolean andDisplay)
    {
        super.linkWith(abstractDiscussionDTO, andDisplay);
        ButterKnife.inject(this);
        if (abstractDiscussionDTO instanceof DiscussionDTO)
        {
            discussionDTO = (DiscussionDTO) abstractDiscussionDTO;
        }
        else
        {
            discussionDTO = null;
        }

        if (discussionDTO != null)
        {
            linkWith(discussionDTO.user, andDisplay);
        }
        else
        {
            linkWith((UserBaseDTO) null, andDisplay);
        }

        if (andDisplay)
        {
            if (this.discussionDTO != null && discussionVotePair != null)
            {
                discussionVotePair.display(discussionDTO);
            }
            else
            {
                resetView();
            }
        }
    }

    @OnClick(R.id.discussion_action_button_comment_count) void onActionButtonCommentCountClicked()
    {
        Bundle args = new Bundle();
        args.putBundle(SecurityDiscussionCommentFragment.DISCUSSION_KEY_BUNDLE_KEY,
                discussionKey.getArgs());
        getNavigator().pushFragment(SecurityDiscussionCommentFragment.class, args);
    }

    //TODO very bad way
    @OnClick(R.id.discussion_action_button_more) void showShareDialog()
    {
        View contentView = LayoutInflater.from(getContext())
                .inflate(R.layout.sharing_translation_dialog_layout, null);
        THDialog.DialogCallback callback = (THDialog.DialogCallback) contentView;
        ((NewsDialogLayout) contentView).setNewsData(discussionDTO,
                WeChatMessageType.Discussion);
        ((NewsDialogLayout) contentView).setMenuClickedListener(
                createNewsDialogMenuClickedListener());
        THDialog.showUpDialog(getContext(), contentView, callback);
    }

    protected void handleTranslationRequested()
    {
        detachTranslationTask();
        TranslationKey key = discussionDTO.createTranslationKey("zh");
        translationTask =
                new NewsTranslationSelfDisplayer(getContext(), translationCache, null)
                        .launchTranslation(key);
    }

    private void linkWith(UserBaseDTO user, boolean andDisplay)
    {
        this.userBaseDTO = user;

        if (andDisplay)
        {
            if (userBaseDTO != null)
            {
                displayUser();
            }
            else
            {
                resetUserView();
            }
        }
    }

    private void displayUser()
    {
        displayUsername();

        displayProfilePicture();
    }

    private void displayUsername()
    {
        if (userProfileName != null)
        {
            userProfileName.setText(userBaseDTO.displayName);
        }
    }

    private void resetUserProfileName()
    {
        if (userProfileName != null)
        {
            userProfileName.setText(null);
        }
    }

    private void resetUserView()
    {
        resetUserProfileName();

        resetUserProfilePicture();
    }

    private void resetUserProfilePicture()
    {
        cancelProfilePictureRequest();
        if (discussionUserPicture != null)
        {
            picasso.load(R.drawable.superman_facebook)
                    .transform(userProfilePictureTransformation)
                    .into(discussionUserPicture);
        }
    }

    private void displayProfilePicture()
    {
        resetUserProfilePicture();
        if (userBaseDTO.picture != null && discussionUserPicture != null)
        {
            picasso.load(userBaseDTO.picture)
                    .transform(userProfilePictureTransformation)
                    .into(discussionUserPicture);
        }
    }

    private void resetView()
    {
        resetUserView();
    }

    private void cancelProfilePictureRequest()
    {
        picasso.cancelRequest(discussionUserPicture);
    }

    @Override public void onClick(View v)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, userBaseDTO.id);
        getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
    }

    @Override protected SecurityId getSecurityId()
    {
        // TODO there has to be a SecurityId here
        throw new IllegalStateException("It has no securityId");
    }

    protected void pushSettingsForConnect(SocialShareFormDTO socialShareFormDTO)
    {
        Bundle args = new Bundle();
        SettingsFragment.putSocialNetworkToConnect(args, socialShareFormDTO);
        ((DashboardActivity) getContext()).getDashboardNavigator().pushFragment(SettingsFragment.class, args);
    }

    protected NewsDialogLayout.OnMenuClickedListener createNewsDialogMenuClickedListener()
    {
        return new SecurityDiscussionItemViewDialogMenuClickedListener();
    }

    private class SecurityDiscussionItemViewDialogMenuClickedListener implements NewsDialogLayout.OnMenuClickedListener
    {
        @Override public void onTranslationRequestedClicked()
        {
            handleTranslationRequested();
        }

        @Override public void onShareConnectRequested(SocialShareFormDTO socialShareFormDTO)
        {
            pushSettingsForConnect(socialShareFormDTO);
        }

        @Override public void onShareRequestedClicked()
        {
            // TODO
        }
    }
}
