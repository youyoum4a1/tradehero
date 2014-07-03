package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.widget.VotePair;

public class DiscussionActionButtonsView extends LinearLayout
{
    public static final boolean HAS_DOWN_VOTE = true;
    private static final boolean DEFAULT_SHOW_MORE = false;

    @InjectView(R.id.vote_pair) @Optional protected VotePair votePair;
    @InjectView(R.id.discussion_action_button_comment_count) @Optional CompoundButton commentCount;
    @InjectView(R.id.discussion_action_button_share) @Optional View shareButton;
    @InjectView(R.id.discussion_action_button_more) @Optional View moreButton;

    protected boolean downVote = HAS_DOWN_VOTE;
    private boolean showMore = DEFAULT_SHOW_MORE;
    private OnButtonClickedListener buttonClickedListener;
    protected AbstractDiscussionCompactDTO discussionDTO;

    //<editor-fold desc="Constructors">
    public DiscussionActionButtonsView(Context context)
    {
        super(context);
    }

    public DiscussionActionButtonsView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DiscussionActionButtonsView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        display();
    }

    @Override protected void onDetachedFromWindow()
    {
        setButtonClickedListener(null);
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void setButtonClickedListener(
            OnButtonClickedListener buttonClickedListener)
    {
        this.buttonClickedListener = buttonClickedListener;
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
    }

    public void linkWith(AbstractDiscussionCompactDTO discussionDTO, boolean andDisplay)
    {
        this.discussionDTO = discussionDTO;
        if (andDisplay)
        {
            displayVotePair();
            displayCommentCount();
            displayMoreButton();
        }
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

    protected void displayMoreButton()
    {
        if (moreButton != null)
        {
            moreButton.setVisibility(showMore ? View.VISIBLE : View.GONE);
        }
    }

    @OnClick(R.id.discussion_action_button_comment_count) @Optional
    protected void handleCommentButtonClicked(View view)
    {
        notifyCommentButtonClicked();
    }

    protected void notifyCommentButtonClicked()
    {
        OnButtonClickedListener buttonClickedListenerCopy = buttonClickedListener;
        if (buttonClickedListenerCopy != null)
        {
            buttonClickedListenerCopy.onCommentButtonClicked();
        }
    }

    @OnClick(R.id.discussion_action_button_share) @Optional
    protected void handleShareButtonClicked(View view)
    {
        notifyShareButtonClicked();
    }

    protected void notifyShareButtonClicked()
    {
        OnButtonClickedListener buttonClickedListenerCopy = buttonClickedListener;
        if (buttonClickedListenerCopy != null)
        {
            buttonClickedListenerCopy.onShareButtonClicked();
        }
    }

    @OnClick(R.id.discussion_action_button_more) @Optional
    protected void handleMoreButtonClicked(View view)
    {
        notifyMoreButtonClicked();
    }

    protected void notifyMoreButtonClicked()
    {
        OnButtonClickedListener buttonClickedListenerCopy = buttonClickedListener;
        if (buttonClickedListenerCopy != null)
        {
            buttonClickedListenerCopy.onMoreButtonClicked();
        }
    }

    public static interface OnButtonClickedListener
    {
        void onCommentButtonClicked();
        void onShareButtonClicked();
        void onMoreButtonClicked();
    }
}
