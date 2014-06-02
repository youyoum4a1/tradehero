package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.CommentKey;

public class CommentItemView extends DiscussionItemView<CommentKey>
{
    private DiscussionDTO discussionDTO;

    //<editor-fold desc="Constructors">
    public CommentItemView(Context context)
    {
        super(context);
    }

    public CommentItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CommentItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    @Override protected void linkWith(AbstractDiscussionCompactDTO abstractDiscussionDTO, boolean andDisplay)
    {
        super.linkWith(abstractDiscussionDTO, andDisplay);
        this.discussionDTO = (DiscussionDTO) abstractDiscussionDTO;
        if (andDisplay)
        {
        }
    }

    protected void openDiscussion()
    {
        if (discussionDTO != null)
        {
            Bundle args = new Bundle();
            args.putBundle(NewsDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY, discussionDTO.getDiscussionKey().getArgs());
            getNavigator().pushFragment(NewsDiscussionFragment.class, args);
        }
    }

    @Override
    protected AbstractDiscussionCompactItemViewHolder.OnMenuClickedListener createViewHolderMenuClickedListener()
    {
        return new CommentItemViewHolderMenuClickedListener()
        {
            @Override public void onShareButtonClicked()
            {
                // Nothing to do
            }
        };
    }

    abstract protected class CommentItemViewHolderMenuClickedListener extends DiscussionItemViewMenuClickedListener
    {
        @Override public void onCommentButtonClicked()
        {
            openDiscussion();
        }
    }
}
