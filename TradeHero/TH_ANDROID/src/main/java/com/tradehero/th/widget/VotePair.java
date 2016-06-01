package com.ayondo.academy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.VoteDirection;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.discussion.UserDiscussionAction;
import rx.Observable;
import rx.subjects.PublishSubject;

public class VotePair extends LinearLayout
{
    @Bind(R.id.timeline_action_button_vote_up) VoteView voteUp;
    @Bind(R.id.timeline_action_button_vote_down) VoteView voteDown;

    private AbstractDiscussionCompactDTO discussionDTO;
    private boolean downVote = false;
    @NonNull private final PublishSubject<UserDiscussionAction> userActionSubject;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public VotePair(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        userActionSubject = PublishSubject.create();
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        HierarchyInjector.inject(context, this);
        if (attrs != null)
        {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.VotePair);
            downVote = a.getBoolean(R.styleable.VotePair_downVote, false);
            a.recycle();
        }
    }
    //</editor-fold>

    @NonNull public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    private void updateDownVoteVisibility()
    {
        if (voteDown != null)
        {
            voteDown.setVisibility(downVote ? VISIBLE : GONE);
        }
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
        updateDownVoteVisibility();
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("UnusedDeclaration") @OnClick({
            R.id.timeline_action_button_vote_up,
            R.id.timeline_action_button_vote_down
    })
    public void onItemClicked(View view)
    {
        if (discussionDTO == null)
        {
            // TODO inform player about lack of information
            return;
        }
        VoteDirection direction;
        switch (view.getId())
        {
            case R.id.timeline_action_button_vote_up:
                direction = voteUp.isChecked() ? VoteDirection.UpVote : VoteDirection.UnVote;
                fakeUpdateForVoteUp(direction);
                discussionDTO.voteDirection = direction.value;
                userActionSubject.onNext(new UserAction(discussionDTO, direction));
                break;
            case R.id.timeline_action_button_vote_down:
                if (voteDown.isChecked())
                {
                    //voteUp.setChecked(false);
                }
                direction = voteDown.isChecked() ? VoteDirection.DownVote : VoteDirection.UnVote;
                discussionDTO.voteDirection = direction.value;
                userActionSubject.onNext(new UserAction(discussionDTO, direction));
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

    public void setDownVote(boolean downVote)
    {
        this.downVote = downVote;

        updateDownVoteVisibility();
    }

    public void display(AbstractDiscussionCompactDTO discussionDTO)
    {
        this.discussionDTO = discussionDTO;
        if (voteUp != null)
        {
            // We need to make these tests because view are detached from window in
            // disparate order
            // https://www.crashlytics.com/tradehero/android/apps/com.ayondo.academy/issues/5360b347e3de5099ba24841d
            voteUp.display(discussionDTO);
        }
        if (voteDown != null)
        {
            voteDown.display(discussionDTO);
        }
    }

    public static class UserAction extends UserDiscussionAction
    {
        @NonNull public final VoteDirection voteDirection;

        public UserAction(@NonNull AbstractDiscussionCompactDTO discussionDTO, @NonNull VoteDirection voteDirection)
        {
            super(discussionDTO);
            this.voteDirection = voteDirection;
        }
    }
}
