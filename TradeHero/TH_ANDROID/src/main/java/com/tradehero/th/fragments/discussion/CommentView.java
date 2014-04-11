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
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.CommentKey;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.VotePair;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 5:47 PM Copyright (c) TradeHero
 */
public class CommentView extends LinearLayout
        implements DTOView<CommentKey>
{
    @InjectView(R.id.timeline_user_profile_name) TextView username;
    @InjectView(R.id.timeline_item_content) TextView content;
    @InjectView(R.id.timeline_user_profile_picture) ImageView avatar;
    @InjectView(R.id.timeline_time) TextView time;

    @InjectView(R.id.vote_pair) VotePair votePair;
    @InjectView(R.id.timeline_action_button_more) TextView more;

    @Inject DiscussionCache discussionCache;
    @Inject CurrentUserId currentUserId;
    @Inject Provider<PrettyTime> prettyTime;
    @Inject Lazy<Picasso> picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    private DiscussionDTO discussionDTO;
    private CommentKey commentKey;
    private DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO> discussionFetchListener;
    private DTOCache.GetOrFetchTask<DiscussionKey, AbstractDiscussionDTO> discussionFetchTask;

    //<editor-fold desc="Constructors">
    public CommentView(Context context)
    {
        super(context);
    }

    public CommentView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);

        discussionFetchListener = new DiscussionFetchListener();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachFetchCommentTask();

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(CommentKey commentKey)
    {
        this.commentKey = commentKey;

        fetchCommentDetail();
    }

    private void fetchCommentDetail()
    {
        detachFetchCommentTask();

        discussionFetchTask = discussionCache.getOrFetch(commentKey, false, discussionFetchListener);
        discussionFetchTask.execute();
    }

    private void detachFetchCommentTask()
    {
        if (discussionFetchTask != null)
        {
            discussionFetchTask.setListener(null);
        }
        discussionFetchTask = null;
    }

    public void linkWith(AbstractDiscussionDTO abstractDiscussionDTO, boolean andDisplay)
    {
        if (abstractDiscussionDTO instanceof DiscussionDTO)
        {
            this.discussionDTO = (DiscussionDTO) abstractDiscussionDTO;

            linkWith(discussionDTO.user, true);

            display(discussionDTO);
        }
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

    private void displayComment(AbstractDiscussionDTO item)
    {
        content.setText(item.text);
    }

    private void displayCommentTime(AbstractDiscussionDTO abstractDiscussionDTO)
    {
        if (abstractDiscussionDTO.createdAtUtc != null)
        {
            time.setText(prettyTime.get().formatUnrounded(abstractDiscussionDTO.createdAtUtc));
        }
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

    public void display(NewsItemDTO newsItemDTO)
    {
        display((AbstractDiscussionDTO) newsItemDTO);
    }

    private void display(AbstractDiscussionDTO abstractDiscussionDTO)
    {
        // markup text
        displayComment(abstractDiscussionDTO);

        // timeline time
        displayCommentTime(abstractDiscussionDTO);

        votePair.display(abstractDiscussionDTO);
    }

    private class DiscussionFetchListener implements DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO>
    {
        @Override public void onDTOReceived(DiscussionKey key, AbstractDiscussionDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(DiscussionKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
