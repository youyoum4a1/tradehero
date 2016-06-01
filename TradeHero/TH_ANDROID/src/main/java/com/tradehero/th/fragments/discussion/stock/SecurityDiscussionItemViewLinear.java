package com.ayondo.academy.fragments.discussion.stock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.fragments.discussion.DiscussionActionButtonsView;
import com.ayondo.academy.fragments.discussion.DiscussionItemViewLinear;
import com.ayondo.academy.models.discussion.UserDiscussionAction;
import rx.Observable;
import rx.functions.Func1;

public class SecurityDiscussionItemViewLinear
        extends DiscussionItemViewLinear
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

    @NonNull @Override public Observable<UserDiscussionAction> getUserActionObservable()
    {
        return super.getUserActionObservable()
                .map(new Func1<UserDiscussionAction, UserDiscussionAction>()
                {
                    @Override public UserDiscussionAction call(UserDiscussionAction userDiscussionAction)
                    {
                        if (userDiscussionAction instanceof DiscussionActionButtonsView.CommentUserAction)
                        {
                            return new CommentUserAction(userDiscussionAction.discussionDTO);
                        }
                        return userDiscussionAction;
                    }
                });
    }

    public static class CommentUserAction extends UserDiscussionAction
    {
        public CommentUserAction(@NonNull AbstractDiscussionCompactDTO discussionDTO)
        {
            super(discussionDTO);
        }
    }
}
