package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.timeline.TimelineItemDTOEnhanced;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/14/14 Time: 4:21 PM Copyright (c) TradeHero
 */
public class VotePair extends LinearLayout
{

    @InjectView(R.id.timeline_action_button_vote_up) VoteView voteUp;
    @InjectView(R.id.timeline_action_button_vote_down) VoteView voteDown;

    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;

    private MiddleCallback<DiscussionDTO> voteCallback;
    private AbstractDiscussionDTO discussionDTO;

    //<editor-fold desc="Constructors">
    public VotePair(Context context)
    {
        super(context);
    }

    public VotePair(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public VotePair(Context context, AttributeSet attrs, int defStyle)
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
        if (voteCallback != null)
        {
            voteCallback.setPrimaryCallback(null);
        }

        ButterKnife.reset(this);

        super.onDetachedFromWindow();
    }

    @OnClick({
            R.id.timeline_action_button_vote_up,
            R.id.timeline_action_button_vote_down
    })
    public void onItemClicked(View view)
    {
        switch (view.getId())
        {
            case R.id.timeline_action_button_vote_up:
                Timber.d("voteUp: %b", voteUp.isChecked());
                if (voteUp.isChecked())
                {
                    voteDown.setChecked(false);
                }
                updateVoting(voteUp.isChecked() ? VoteDirection.UpVote : VoteDirection.Unvote);
                break;
            case R.id.timeline_action_button_vote_down:
                if (voteDown.isChecked())
                {
                    voteUp.setChecked(false);
                }
                updateVoting(voteUp.isChecked() ? VoteDirection.DownVote : VoteDirection.Unvote);
                break;
        }
    }

    private void updateVoting(VoteDirection voteDirection)
    {
        if (discussionDTO == null)
        {
            return;
        }
        DiscussionType discussionType = getDiscussionType();

        DiscussionVoteKey discussionVoteKey = new DiscussionVoteKey(
                discussionType,
                discussionDTO.id,
                voteDirection);
        voteCallback = discussionServiceWrapper.get().voteCallBack(discussionVoteKey,
                new Callback<DiscussionDTO>()
                {
                    @Override public void success(DiscussionDTO discussionDTO, Response response)
                    {
                        discussionDTO.populateVote(VotePair.this.discussionDTO);
                        // TODO update cached timeline item
                        Timber.d("Success");
                    }

                    @Override public void failure(RetrofitError error)
                    {
                        Timber.d("Failure");
                    }
                });
    }

    private DiscussionType getDiscussionType()
    {
        // TODO Server should return type of discussionDTO, while it does not do so at the moment,
        // this hAcK will be used to get type of the dto
        DiscussionType discussionType = null;
        if (discussionDTO instanceof NewsItemDTO)
        {
            discussionType = DiscussionType.NEWS;
        }
        else if (discussionDTO instanceof TimelineItemDTOEnhanced)
        {
            discussionType = DiscussionType.TIMELINE_ITEM;
        }
        else if (discussionDTO instanceof DiscussionDTO)
        {
            discussionType = DiscussionType.COMMENT;
        }
        else
        {
            throw new IllegalStateException("Unknown discussion type");
        }
        return discussionType;
    }

    private void resetVoting()
    {
        if (voteUp != null)
        {
            voteUp.setChecked(false);
        }
        if (voteDown != null)
        {
            voteDown.setChecked(false);
        }
    }

    public void display(AbstractDiscussionDTO discussionDTO)
    {
        this.discussionDTO = discussionDTO;

        resetVoting();

        if (discussionDTO != null)
        {
            voteUp.setValue(discussionDTO.upvoteCount);
            voteDown.setValue(discussionDTO.downvoteCount);

            VoteDirection voteDirection = VoteDirection.fromValue(discussionDTO.voteDirection);
            Timber.d("voteDirection: %s", voteDirection.description);
            switch (voteDirection)
            {
                case DownVote:
                    voteDown.setChecked(true);
                    break;
                case UpVote:
                    voteUp.setChecked(true);
                    break;
                case Unvote:
                    // do nothing
                    break;
            }
        }
    }
}
