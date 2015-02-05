package com.tradehero.th.fragments.discussion.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.fragments.discussion.DiscussionActionButtonsView;
import com.tradehero.th.fragments.discussion.DiscussionItemViewLinear;
import rx.Observable;

public class SecurityDiscussionItemViewLinear
        extends DiscussionItemViewLinear<DiscussionKey>
{
    //<editor-fold desc="Constructors">
    public SecurityDiscussionItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
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

    @NonNull @Override protected Observable<DiscussionActionButtonsView.UserAction> handleUserAction(
            DiscussionActionButtonsView.UserAction userAction)
    {
        if (userAction instanceof DiscussionActionButtonsView.CommentUserAction)
        {
            handleActionButtonCommentCountClicked();
            return Observable.empty();
        }
        return super.handleUserAction(userAction);
    }

    void handleActionButtonCommentCountClicked()
    {
        Bundle args = new Bundle();
        SecurityDiscussionCommentFragment.putDiscussionKey(args, discussionKey);
        if (getNavigator().getCurrentFragment() != null && getNavigator().getCurrentFragment() instanceof SecurityDiscussionCommentFragment)
        {
            return;
        }
        getNavigator().pushFragment(SecurityDiscussionCommentFragment.class, args);
    }
}
