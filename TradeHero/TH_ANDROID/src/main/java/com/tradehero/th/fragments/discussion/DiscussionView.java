package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 5:47 PM Copyright (c) TradeHero
 */
public class DiscussionView extends RelativeLayout
        implements DTOView<DiscussionDTO>
{
    @InjectView(R.id.timeline_user_profile_name) TextView username;
    @InjectView(R.id.timeline_item_content) TextView content;
    @InjectView(R.id.timeline_user_profile_picture) ImageView avatar;
    @InjectView(R.id.timeline_time) TextView time;

    @Inject Provider<PrettyTime> prettyTime;
    @Inject Lazy<Picasso> picasso;
    @Inject @ForUserPhoto Transformation peopleIconTransformation;

    //@InjectView(R.id.timeline_action_button_vote_up) TextView voteUp;
    //@InjectView(R.id.timeline_action_button_vote_down) TextView voteDown;
    //@InjectView(R.id.timeline_action_button_more) TextView more;

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
}
