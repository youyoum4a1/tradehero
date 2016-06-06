package com.androidth.general.fragments.discussion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;
import android.support.annotation.Nullable;
import com.androidth.general.common.annotation.ViewVisibilityValue;
import com.androidth.general.R;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;
import com.androidth.general.models.discussion.UserDiscussionAction;
import com.androidth.general.widget.VotePair;
import rx.Observable;
import rx.subjects.PublishSubject;

public class DiscussionActionButtonsView extends LinearLayout
{
    public static final boolean HAS_DOWN_VOTE = true;
    private static final boolean DEFAULT_SHOW_MORE = false;

    @Bind(R.id.vote_pair) @Nullable protected VotePair votePair;
    @Bind(R.id.discussion_action_button_comment_count) @Nullable CompoundButton commentCount;
    @Bind(R.id.discussion_action_button_share) @Nullable View shareButton;
    @Bind(R.id.discussion_action_button_more) @Nullable View moreButton;

    protected boolean downVote = HAS_DOWN_VOTE;
    private boolean showMore = DEFAULT_SHOW_MORE;
    protected AbstractDiscussionCompactDTO discussionDTO;
    @NonNull protected PublishSubject<UserDiscussionAction> userActionBehavior;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public DiscussionActionButtonsView(Context context)
    {
        super(context);
        this.userActionBehavior = PublishSubject.create();
    }

    @SuppressWarnings("UnusedDeclaration")
    public DiscussionActionButtonsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.userActionBehavior = PublishSubject.create();
    }

    @SuppressWarnings("UnusedDeclaration")
    public DiscussionActionButtonsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.userActionBehavior = PublishSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.bind(this);
        display();
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.unbind(this);
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<UserDiscussionAction> getUserActionObservable()
    {
        Observable<UserDiscussionAction> voteObservable;
        if (votePair != null)
        {
            voteObservable = votePair.getUserActionObservable();
        }
        else
        {
            voteObservable = Observable.empty();
        }
        return userActionBehavior.mergeWith(voteObservable);
    }

    public void setDownVote(boolean downVote)
    {
        this.downVote = downVote;
        if (votePair != null)
        {
            votePair.setDownVote(downVote);
        }
    }

    public void setShowMore(boolean showMore)
    {
        this.showMore = showMore;
        displayMoreButton();
    }

    public void linkWith(AbstractDiscussionCompactDTO discussionDTO)
    {
        this.discussionDTO = discussionDTO;
        displayVotePair();
        displayCommentCount();
        displayMoreButton();
    }

    public void display()
    {
        displayVotePair();
        displayCommentCount();
        displayMoreButton();
    }

    protected void displayVotePair()
    {
        if (votePair != null)
        {
            votePair.display(discussionDTO);
        }
    }

    protected void displayCommentCount()
    {
        if (commentCount != null && discussionDTO != null)
        {
            commentCount.setText(String.valueOf(discussionDTO.commentCount));
            commentCount.setChecked(discussionDTO.commentCount > 0);
        }
    }

    public void setCommentCountVisible(@ViewVisibilityValue int visible)
    {
        if (commentCount != null)
        {
            commentCount.setVisibility(visible);
        }
    }

    protected void displayMoreButton()
    {
        if (moreButton != null)
        {
            moreButton.setVisibility(showMore ? View.VISIBLE : View.GONE);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.discussion_action_button_comment_count) @Nullable
    protected void handleCommentButtonClicked(View view)
    {
        userActionBehavior.onNext(new CommentUserAction(discussionDTO));
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.discussion_action_button_share) @Nullable
    protected void handleShareButtonClicked(View view)
    {
        userActionBehavior.onNext(new ShareUserAction(discussionDTO));
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.discussion_action_button_more) @Nullable
    protected void handleMoreButtonClicked(View view)
    {
        userActionBehavior.onNext(new MoreUserAction(discussionDTO));
    }

    public static class CommentUserAction extends UserDiscussionAction
    {
        public CommentUserAction(@NonNull AbstractDiscussionCompactDTO discussionDTO)
        {
            super(discussionDTO);
        }
    }

    public static class ShareUserAction extends UserDiscussionAction
    {
        public ShareUserAction(@NonNull AbstractDiscussionCompactDTO discussionDTO)
        {
            super(discussionDTO);
        }
    }

    public static class MoreUserAction extends UserDiscussionAction
    {
        public MoreUserAction(@NonNull AbstractDiscussionCompactDTO discussionDTO)
        {
            super(discussionDTO);
        }
    }
}
