package com.tradehero.th.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.MetaHelper;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class VotePair extends LinearLayout
{
    @InjectView(R.id.timeline_action_button_vote_up) VoteView voteUp;
    @InjectView(R.id.timeline_action_button_vote_down) VoteView voteDown;

    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;

    private MiddleCallback<DiscussionDTO> voteCallback;
    private AbstractDiscussionDTO discussionDTO;
    private boolean downVote = false;

    public static interface OnVoteListener
    {
        void onVoteSuccess(DiscussionDTO discussionDTO);
    }

    private OnVoteListener onVoteListener;

    public void setOnVoteListener(OnVoteListener onVoteListener)
    {
        this.onVoteListener = onVoteListener;
    }

    //<editor-fold desc="Constructors">
    public VotePair(Context context)
    {
        super(context);
    }

    public VotePair(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public VotePair(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs);
    }
    //</editor-fold>

    private void init(AttributeSet attrs)
    {
        if (attrs != null)
        {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.VotePair);
            downVote = a.getBoolean(R.styleable.VotePair_downVote, false);
            a.recycle();
        }
    }

    private void updateDownVoteVisibility()
    {
        voteDown.setVisibility(downVote ? VISIBLE : GONE);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        updateDownVoteVisibility();
    }

    @Override protected void onDetachedFromWindow()
    {
        detachVoteMiddleCallback();

        ButterKnife.reset(this);

        super.onDetachedFromWindow();
    }

    protected void detachVoteMiddleCallback()
    {
        if (voteCallback != null)
        {
            voteCallback.setPrimaryCallback(null);
        }
        voteCallback = null;
    }

    @OnClick({
            R.id.timeline_action_button_vote_up,
            R.id.timeline_action_button_vote_down
    })
    public void onItemClicked(View view)
    {
        if(!MetaHelper.isNetworkAvailable(getContext()))
        {
            THToast.show(getContext().getString(R.string.network_error));
            return;
        }
        if (discussionDTO == null)
        {
            // TODO inform player about lack of information
            return;
        }
        switch (view.getId())
        {
            case R.id.timeline_action_button_vote_up:
                boolean targetVoteUp = voteUp.isChecked();
                Timber.d("%s onItemClicked voteDirection:%s voteUp checked:%s, content:%s",
                        discussionDTO.hashCode(),
                        discussionDTO.voteDirection,
                        voteUp.isChecked(),
                        discussionDTO.text);
                fakeUpdateForVoteUp(targetVoteUp ? VoteDirection.UpVote : VoteDirection.UnVote);
                updateVoting(targetVoteUp ? VoteDirection.UpVote : VoteDirection.UnVote);

                break;
            case R.id.timeline_action_button_vote_down:
                if (voteDown.isChecked())
                {
                    //voteUp.setChecked(false);
                }
                updateVoting(voteDown.isChecked() ? VoteDirection.DownVote : VoteDirection.UnVote);
                break;
        }
    }

    private void fakeUpdateForVoteUp(VoteDirection targetVoteDirection)
    {
        if (targetVoteDirection == VoteDirection.UpVote)
        {
            voteUp.setValue(discussionDTO.upvoteCount + 1);
            voteUp.setChecked(true);

        }
        else if (targetVoteDirection == VoteDirection.UnVote)
        {
            int count = discussionDTO.upvoteCount - 1;
            voteUp.setValue(count < 0 ? 0:count);
            voteUp.setChecked(false);
        }
    }

    protected class VoteCallback implements Callback<DiscussionDTO>
    {
        private AbstractDiscussionDTO discussionDTO;
        private VoteDirection targetVoteDirection;

        public VoteCallback(VoteDirection voteDirection)
        {
            this.discussionDTO = VotePair.this.discussionDTO;
            this.targetVoteDirection = voteDirection;
        }

        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            if (this.discussionDTO == null || VotePair.this.discussionDTO == null)
            {
                Timber.e("VoteCallback success but discussionDTO is null");
                return;
            }
            if (this.discussionDTO.id != discussionDTO.id)
            {
                Timber.e("VoteCallback success but id is not the same");
                return;
            }
            VoteDirection returnedVoteDirection = VoteDirection.fromValue(discussionDTO.voteDirection);
            if (this.discussionDTO.id == VotePair.this.discussionDTO.id)
            {
                //means the same item
                if (targetVoteDirection != returnedVoteDirection)
                {   //server may return the wrong voteDirection
                    discussionDTO.voteDirection = targetVoteDirection.value;
                    Timber.e("targetVoteDirection(%s) and returnedVoteDirection(%s) not the same",targetVoteDirection,returnedVoteDirection);
                }
                discussionDTO.populateVote(VotePair.this.discussionDTO);
                Timber.d("VoteCallback success and item is the same. voteDirection:%s",VotePair.this.discussionDTO.voteDirection);
                display(VotePair.this.discussionDTO);
                // TODO update cached timeline item
                Timber.d("Success");
                if (onVoteListener != null)
                {
                    onVoteListener.onVoteSuccess(discussionDTO);
                }
            }
            else
            {
                if (targetVoteDirection != returnedVoteDirection)
                {   //server ma
                    discussionDTO.voteDirection = targetVoteDirection.value;
                    Timber.e("targetVoteDirection(%s) and returnedVoteDirection(%s) not the same",targetVoteDirection,returnedVoteDirection);
                }
                discussionDTO.populateVote(this.discussionDTO);
                //do nothing
                Timber.e("VoteCallback success and item is not the same");
            }
        }

        @Override public void failure(RetrofitError error)
        {
            Timber.e("VoteCallback Failure");
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
        detachVoteMiddleCallback();
        voteCallback = discussionServiceWrapper.get().vote(discussionVoteKey, new VoteCallback(voteDirection));
    }

    public boolean hasDownVote()
    {
        return downVote;
    }

    public void setDownVote(boolean downVote)
    {
        this.downVote = downVote;

        updateDownVoteVisibility();
    }

    private DiscussionType getDiscussionType()
    {
        if (discussionDTO != null && discussionDTO.getDiscussionKey() != null)
        {
            return discussionDTO.getDiscussionKey().getType();
        }

        throw new IllegalStateException("Unknown discussion type");
    }

    public void display(AbstractDiscussionDTO discussionDTO)
    {
        this.discussionDTO = discussionDTO;
        voteUp.display(discussionDTO);
        voteDown.display(discussionDTO);
    }
}
