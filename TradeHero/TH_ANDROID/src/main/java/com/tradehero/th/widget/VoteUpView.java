package com.ayondo.academy.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.VoteDirection;

public class VoteUpView extends VoteView
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public VoteUpView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public VoteUpView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public VoteUpView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(AbstractDiscussionCompactDTO discussionDTO)
    {
        if (discussionDTO != null)
        {
            setValue(discussionDTO.upvoteCount);
            setChecked(discussionDTO.voteDirection == VoteDirection.UpVote.value);
        }
        else
        {
            setValue(R.integer.messages_initial_vote_count);
            setChecked(false);
        }
    }

    @Override protected int getCheckedColor()
    {
        return getResources().getColor(R.color.tradehero_blue);
    }
}
