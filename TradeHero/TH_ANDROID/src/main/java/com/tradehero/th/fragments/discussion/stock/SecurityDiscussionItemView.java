package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemView;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.VotePair;
import javax.inject.Inject;

/**
 * Created by thonguyen on 4/4/14.
 */
public class SecurityDiscussionItemView extends AbstractDiscussionItemView<DiscussionKey>
{
    @InjectView(R.id.discussion_user_picture) ImageView discussionUserPicture;
    @InjectView(R.id.vote_pair) VotePair discussionVotePair;

    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userProfilePictureTransformation;

    private DiscussionDTO discussionDTO;

    private UserBaseDTO userBaseDTO;

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
        DaggerUtils.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        resetView();

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override protected void linkWith(AbstractDiscussionDTO abstractDiscussionDTO, boolean andDisplay)
    {
        super.linkWith(abstractDiscussionDTO, andDisplay);

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
            if (this.discussionDTO != null)
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
        args.putBundle(SecurityDiscussionCommentFragment.DISCUSSION_KEY_BUNDLE_KEY, discussionKey.getArgs());
        getNavigator().pushFragment(SecurityDiscussionCommentFragment.class, args);
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
        displayProfilePicture();
    }

    private void resetUserView()
    {
        picasso.load(R.drawable.superman_facebook)
                .transform(userProfilePictureTransformation)
                .into(discussionUserPicture);
    }

    private void displayProfilePicture()
    {
        cancelProfilePictureRequest();
        picasso.load(userBaseDTO.picture)
                .transform(userProfilePictureTransformation)
                .into(discussionUserPicture);
    }

    private void resetView()
    {
        resetUserView();
    }

    private void cancelProfilePictureRequest()
    {
        picasso.cancelRequest(discussionUserPicture);
    }
}
