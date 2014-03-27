package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.VotePair;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 5:47 PM Copyright (c) TradeHero
 */
public class DiscussionView extends LinearLayout
        implements DTOView<DiscussionDTO>
{
    @InjectView(R.id.timeline_user_profile_name) TextView username;
    @InjectView(R.id.timeline_item_content) TextView content;
    @InjectView(R.id.timeline_user_profile_picture) ImageView avatar;
    @InjectView(R.id.timeline_time) TextView time;

    @InjectView(R.id.vote_pair) VotePair votePair;
    @InjectView(R.id.timeline_action_button_more) TextView more;

    @Inject CurrentUserId currentUserId;
    @Inject Provider<PrettyTime> prettyTime;
    @Inject Lazy<Picasso> picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    private DiscussionDTO discussionDTO;

    //<editor-fold desc="Constructors">
    public DiscussionView(Context context)
    {
        super(context);
    }

    public DiscussionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DiscussionView(Context context, AttributeSet attrs, int defStyle)
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

    @Override public void display(DiscussionDTO dto)
    {
        this.discussionDTO = dto;

        if (discussionDTO != null)
        {
            // username
            displayUsername(discussionDTO.user);

            // user profile picture
            displayUserProfilePicture(discussionDTO.user);

            // markup text
            displayComment(discussionDTO);

            // timeline time
            displayCommentTime(discussionDTO);

            votePair.display(discussionDTO);
        }
    }


    @OnClick({
            R.id.timeline_user_profile_name,
            R.id.timeline_user_profile_picture,
            R.id.timeline_action_button_more,
            R.id.timeline_action_button_comment
    })
    public void onItemClicked(View view)
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
            case R.id.timeline_action_button_comment:
                openDiscussion();
                break;
        }
    }

    private void openDiscussion()
    {
        if (discussionDTO != null)
        {
            getNavigator().pushFragment(TimelineDiscussionFragment.class, discussionDTO.getDiscussionKey().getArgs());
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

    private void displayComment(DiscussionDTO item)
    {
        content.setText(item.text);
    }

    private void displayCommentTime(DiscussionDTO item)
    {
        time.setText(prettyTime.get().formatUnrounded(item.createdAtUtc));
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
