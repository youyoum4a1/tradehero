package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.key.CommentKey;
import rx.Observable;

public class CommentItemViewLinear extends DiscussionItemViewLinear<CommentKey>
{
    @SuppressWarnings("UnusedDeclaration")
    public CommentItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    protected void openDiscussion()
    {
        if (abstractDiscussionCompactDTO != null)
        {
            Bundle args = new Bundle();
            NewsDiscussionFragment.putDiscussionKey(args, abstractDiscussionCompactDTO.getDiscussionKey());
            getNavigator().pushFragment(NewsDiscussionFragment.class, args);
        }
    }

    @NonNull @Override protected Observable<DiscussionActionButtonsView.UserAction> handleUserAction(
            DiscussionActionButtonsView.UserAction userAction)
    {
        if (userAction instanceof DiscussionActionButtonsView.CommentUserAction)
        {
            openDiscussion();
            return Observable.empty();
        }
        return super.handleUserAction(userAction);
    }
}
