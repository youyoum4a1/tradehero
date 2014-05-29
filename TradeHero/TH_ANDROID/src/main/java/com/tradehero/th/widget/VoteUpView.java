package com.tradehero.th.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.VoteDirection;

public class VoteUpView extends VoteView
{
    //<editor-fold desc="Constructors">
    public VoteUpView(Context context)
    {
        super(context);
    }

    public VoteUpView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public VoteUpView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(AbstractDiscussionDTO discussionDTO)
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
        return Color.BLUE;
    }
}
