package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.CommentKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import dagger.Lazy;
import javax.inject.Inject;

public class CommentItemView extends AbstractDiscussionItemView<CommentKey>
{
    @InjectView(R.id.timeline_user_profile_name) TextView username;
    @InjectView(R.id.timeline_user_profile_picture) ImageView avatar;

    @InjectView(R.id.discussion_action_button_more) View more;
    @InjectView(R.id.discussion_action_button_share) @Optional View buttonShare;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<Picasso> picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    private DiscussionDTO discussionDTO;

    //<editor-fold desc="Constructors">
    public CommentItemView(Context context)
    {
        super(context);
    }

    public CommentItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CommentItemView(Context context, AttributeSet attrs, int defStyle)
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
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override protected void linkWith(AbstractDiscussionCompactDTO abstractDiscussionDTO, boolean andDisplay)
    {
        super.linkWith(abstractDiscussionDTO, andDisplay);

        if (abstractDiscussionDTO instanceof DiscussionDTO)
        {
            linkWith((DiscussionDTO) abstractDiscussionDTO, andDisplay);
        }
    }

    private void linkWith(DiscussionDTO discussionDTO, boolean andDisplay)
    {
        this.discussionDTO = discussionDTO;

        linkWith(discussionDTO.user, true);

        display(discussionDTO);
    }

    private void display(DiscussionDTO discussionDTO)
    {
    }

    private void linkWith(UserBaseDTO user, boolean andDisplay)
    {
        if (andDisplay && user != null)
        {
            // username
            displayUsername(discussionDTO.user);

            // user profile picture
            displayUserProfilePicture(discussionDTO.user);
        }
    }

    @OnClick({
            R.id.timeline_user_profile_name,
            R.id.timeline_user_profile_picture,
            //R.id.discussion_action_button_share,
            R.id.discussion_action_button_more
    })
    void onItemClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.timeline_user_profile_picture:
            case R.id.timeline_user_profile_name:
                openOtherTimeline();
                break;

            case R.id.discussion_action_button_share:
                // TODO
                break;

            case R.id.discussion_action_button_more:
                //PopupMenu popUpMenu = createActionPopupMenu();
                //popUpMenu.show();
                break;
        }
    }

    @Optional @OnClick(R.id.discussion_action_button_comment_count)
    void onCommentClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.discussion_action_button_comment_count:
                openDiscussion();
                break;
        }
    }

    private void openDiscussion()
    {
        if (discussionDTO != null)
        {
            Bundle args = new Bundle();
            args.putBundle(NewsDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY, discussionDTO.getDiscussionKey().getArgs());
            getNavigator().pushFragment(NewsDiscussionFragment.class, args);
        }
    }

    private void displayUsername(UserBaseDTO user)
    {
        username.setText(user.displayName);
    }

    private void displayUserProfilePicture(UserBaseDTO user)
    {
        displayDefaultUserProfilePicture();
        if (user.picture != null)
        {
            picasso.get()
                    .load(user.picture)
                    .transform(peopleIconTransformation)
                    .placeholder(avatar.getDrawable())
                    .into(avatar);
        }
    }

    private void displayDefaultUserProfilePicture()
    {
        picasso.get()
                .load(R.drawable.superman_facebook)
                .transform(peopleIconTransformation)
                .into(avatar);
    }

    private void openOtherTimeline()
    {
        if (discussionDTO != null)
        {
            UserBaseDTO user = discussionDTO.user;
            if (user != null)
            {
                if (currentUserId.get() != user.id)
                {
                    Bundle bundle = new Bundle();
                    bundle.putInt(TimelineFragment.BUNDLE_KEY_SHOW_USER_ID, user.id);
                    getNavigator().pushFragment(PushableTimelineFragment.class, bundle);
                }
            }
        }
    }

    @Override protected SecurityId getSecurityId()
    {
        throw new IllegalStateException("It has no securityId");
    }
}
