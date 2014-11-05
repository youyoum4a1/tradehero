package com.tradehero.th.fragments.news;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import rx.Observer;
import rx.observers.EmptyObserver;

public class NewsHeadlineViewLinear extends AbstractDiscussionCompactItemViewLinear<NewsItemDTOKey>
{
    @Nullable private SecurityId securityId;
    private int backgroundResourceId = -1;

    //<editor-fold desc="Constructors">
    public NewsHeadlineViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @Override protected NewsItemCompactViewHolder createViewHolder()
    {
        return new NewsItemCompactViewHolder<NewsItemCompactDTO>(getContext());
    }

    @Override public void display(NewsItemDTOKey discussionKey)
    {
        super.display(discussionKey);
        linkWith(discussionCache.getValue(discussionKey), true);
        viewHolder.discussionActionButtonsView.setCommentCountVisible(View.GONE);
    }

    public void setNewsBackgroundResource(int resId)
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

    protected void pushDiscussionFragment()
    {
        if (discussionKey != null)
        {

            Bundle args = new Bundle();
            NewsDiscussionFragment.putDiscussionKey(args, discussionKey);
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

    @Override @NonNull
    protected Observer<Pair<DiscussionKey, AbstractDiscussionCompactDTO>> createDiscussionFetchObserver()
    {
        // We are ok with the NewsItemDTO being saved in cache, but we do not want
        // to get it here...
        return new EmptyObserver<>();
    }

    @Override
    protected AbstractDiscussionCompactItemViewHolder.OnMenuClickedListener createViewHolderMenuClickedListener()
    {
        return new NewsHeadlineViewHolderClickedListener()
        {
            @Override public void onShareButtonClicked()
            {
                // Nothing to do
            }

            @Override public void onUserClicked(UserBaseKey userClicked)
            {
                // Nothing to do
            }

            @Override public void onTranslationRequested()
            {
                // Nothing to do
            }
        };
    }

    abstract protected class NewsHeadlineViewHolderClickedListener extends AbstractDiscussionViewHolderClickedListener
        implements NewsItemCompactViewHolder.OnMenuClickedListener
    {
        @Override public void onCommentButtonClicked()
        {
            pushDiscussionFragment();
        }
    }
}
