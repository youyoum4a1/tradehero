package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForUserPhoto;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.VotePair;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by thonguyen on 4/4/14.
 */
public class SecurityDiscussionItemView extends LinearLayout
    implements DTOView<DiscussionKey>
{
    @InjectView(R.id.discussion_content) TextView discussionContent;
    @InjectView(R.id.discussion_time) TextView discussionTime;
    @InjectView(R.id.discussion_user_picture) ImageView discussionUserPicture;
    @InjectView(R.id.vote_pair) VotePair discussionVotePair;

    @Inject DiscussionCache discussionCache;
    @Inject Picasso picasso;
    @Inject @ForUserPhoto Transformation userProfilePictureTransformation;
    @Inject PrettyTime prettyTime;

    private DiscussionKey discussionKey;
    private DiscussionDTO discussionDTO;

    private DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO> discussionFetchListener;
    private DTOCache.GetOrFetchTask<DiscussionKey, AbstractDiscussionDTO> discussionFetchTask;
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

        discussionFetchListener = new SecurityDiscussionFetchListener();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachDiscussionFetchTask();
        resetView();

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(DiscussionKey discussionKey)
    {
        this.discussionKey = discussionKey;

        fetchDiscussionDetail();
    }

    private void linkWith(AbstractDiscussionDTO abstractDiscussionDTO, boolean andDisplay)
    {
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
                displayContent();
                displayTime();
                displayVotePair();
            }
            else
            {
                resetView();
            }
        }
    }

    private void displayVotePair()
    {
        discussionVotePair.display(discussionDTO);
    }

    private void resetVotePair()
    {
        discussionVotePair.display(null);
    }

    private void displayTime()
    {
        discussionTime.setText(prettyTime.format(discussionDTO.createdAtUtc));
    }

    private void resetTime()
    {
        discussionTime.setText(null);
    }

    private void displayContent()
    {
        discussionContent.setText(discussionDTO.text);
    }

    private void resetContent()
    {
        discussionContent.setText(null);
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

    private void resetUserView()
    {
        picasso.load(R.drawable.superman_facebook)
                .transform(userProfilePictureTransformation)
                .into(discussionUserPicture);
    }

    private void displayUser()
    {
        displayProfilePicture();
    }

    private void resetView()
    {
        resetUserView();
        resetContent();
        resetTime();
        resetVotePair();
    }

    private void displayProfilePicture()
    {
        cancelProfilePictureRequest();
        picasso.load(userBaseDTO.picture)
                .transform(userProfilePictureTransformation)
                .into(discussionUserPicture);
    }

    private void cancelProfilePictureRequest()
    {
        picasso.cancelRequest(discussionUserPicture);
    }

    private void fetchDiscussionDetail()
    {
        detachDiscussionFetchTask();

        discussionFetchTask = discussionCache.getOrFetch(discussionKey, false, discussionFetchListener);
        discussionFetchTask.execute();
    }

    private void detachDiscussionFetchTask()
    {
        if (discussionFetchTask != null)
        {
            discussionFetchTask.setListener(null);
        }
        discussionFetchTask = null;
    }


    private class SecurityDiscussionFetchListener implements DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO>
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
