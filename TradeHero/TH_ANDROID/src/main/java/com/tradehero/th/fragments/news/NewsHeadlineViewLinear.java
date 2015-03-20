package com.tradehero.th.fragments.news;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.DiscussionActionButtonsView;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import rx.Observable;

public class NewsHeadlineViewLinear extends AbstractDiscussionCompactItemViewLinear<NewsItemCompactDTO>
{
    @Nullable private SecurityId securityId;
    @DrawableRes private int backgroundResourceId = -1;

    //<editor-fold desc="Constructors">
    public NewsHeadlineViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @NonNull @Override protected NewsItemCompactViewHolder createViewHolder()
    {
        return new NewsItemCompactViewHolder<>(getContext());
    }

    @Override public void display(NewsItemCompactDTO discussionKey)
    {
        super.display(discussionKey);
    }

    public void setNewsBackgroundResource(@DrawableRes int resId)
    {
        this.backgroundResourceId = resId;

        if (viewHolder != null)
        {
            viewHolder.setBackgroundResource(resId);
        }
    }

    public void linkWithSecurityId(SecurityId securityId)
    {
        this.securityId = securityId;
    }

    @NonNull @Override protected Observable<DiscussionActionButtonsView.UserAction> handleUserAction(
            DiscussionActionButtonsView.UserAction userAction)
    {
        if (userAction instanceof DiscussionActionButtonsView.CommentUserAction)
        {
            pushDiscussionFragment();
            return Observable.empty();
        }
        return super.handleUserAction(userAction);
    }

    protected void pushDiscussionFragment()
    {
        if (discussionKey != null)
        {

            Bundle args = new Bundle();
            //NewsDiscussionFragment.putDiscussionKey(args, discussionKey);
            if(backgroundResourceId > 0)
            {
                NewsDiscussionFragment.putBackgroundResId(args, backgroundResourceId);
            }

            if(securityId != null)
            {
                NewsDiscussionFragment.putSecuritySymbol(args, securityId.getSecuritySymbol());
            }

            getNavigator().pushFragment(NewsDiscussionFragment.class, args);
        }
    }
}
