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
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.CommentKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 5:47 PM Copyright (c) TradeHero
 */
public class CommentItemView extends AbstractDiscussionItemView<CommentKey>
{
    @InjectView(R.id.timeline_user_profile_name) TextView username;
    @InjectView(R.id.timeline_user_profile_picture) ImageView avatar;

    @InjectView(R.id.timeline_action_button_more) TextView more;

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
        DaggerUtils.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override protected void linkWith(AbstractDiscussionDTO abstractDiscussionDTO, boolean andDisplay)
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
            R.id.timeline_action_button_more
    })
    void onItemClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.timeline_user_profile_picture:
            case R.id.timeline_user_profile_name:
                openOtherTimeline();
                break;
            case R.id.timeline_action_button_more:
                //PopupMenu popUpMenu = createActionPopupMenu();
                //popUpMenu.show();
                break;
        }
    }

    @Optional @OnClick({
            R.id.timeline_action_button_comment
    })
    void onCommentClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.timeline_action_button_comment:
                openDiscussion();
                break;
        }
    }

    private void openDiscussion()
    {
        if (discussionDTO != null)
        {
            getNavigator().pushFragment(NewsDiscussionFragment.class, discussionDTO.getDiscussionKey().getArgs());
        }
    }

    private void displayUsername(UserBaseDTO user)
    {
        username.setText(user.displayName);
    }


    private void displayUserProfilePicture(UserBaseDTO user)
    {
        if (user.picture != null)
        {
            displayDefaultUserProfilePicture();
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

    private Navigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }
}
