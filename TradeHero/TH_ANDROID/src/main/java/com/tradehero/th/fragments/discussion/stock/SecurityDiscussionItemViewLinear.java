package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemViewHolder;
import com.tradehero.th.fragments.discussion.DiscussionItemViewLinear;

public class SecurityDiscussionItemViewLinear
        extends DiscussionItemViewLinear<DiscussionKey>
{
    //<editor-fold desc="Constructors">
    public SecurityDiscussionItemViewLinear(Context context)
    {
        super(context);
    }

    public SecurityDiscussionItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SecurityDiscussionItemViewLinear(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        viewHolder.setDownVote(false);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        viewHolder.setDownVote(false);
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    void handleActionButtonCommentCountClicked()
    {
        Bundle args = new Bundle();
        SecurityDiscussionCommentFragment.putDiscussionKey(args, discussionKey);
        getNavigator().pushFragment(SecurityDiscussionCommentFragment.class, args);
    }

    @Override
    protected AbstractDiscussionCompactItemViewHolder.OnMenuClickedListener createViewHolderMenuClickedListener()
    {
        return new SecurityDiscussionItemViewMenuClickedListener()
        {
            @Override public void onShareButtonClicked()
            {
                // Nothing to do
            }

            @Override public void onTranslationRequested()
            {
                // Nothing to do
            }
        };
    }

    abstract protected class SecurityDiscussionItemViewMenuClickedListener
        extends DiscussionItemViewMenuClickedListener
            implements AbstractDiscussionItemViewHolder.OnMenuClickedListener
    {
        @Override public void onCommentButtonClicked()
        {
            handleActionButtonCommentCountClicked();
        }
    }
}
