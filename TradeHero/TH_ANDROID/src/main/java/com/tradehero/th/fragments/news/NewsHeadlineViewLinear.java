package com.tradehero.th.fragments.news;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.persistence.news.NewsItemCompactCacheNew;
import javax.inject.Inject;

public class NewsHeadlineViewLinear extends AbstractDiscussionCompactItemViewLinear<NewsItemDTOKey>
{
    @Inject NewsItemCompactCacheNew newsItemCompactCache;

    //<editor-fold desc="Constructors">
    public NewsHeadlineViewLinear(Context context)
    {
        super(context);
    }

    public NewsHeadlineViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NewsHeadlineViewLinear(Context context, AttributeSet attrs, int defStyle)
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

    @Override protected NewsItemCompactViewHolder createViewHolder()
    {
        return new NewsItemCompactViewHolder<NewsItemCompactDTO>();
    }

    @Override public void display(NewsItemDTOKey discussionKey)
    {
        super.display(discussionKey);
        linkWith(newsItemCompactCache.get(discussionKey), true);
    }

    protected void pushDiscussionFragment()
    {
        if (discussionKey != null)
        {
            Bundle args = new Bundle();
            args.putBundle(NewsDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY,
                    discussionKey.getArgs());
            getNavigator().pushFragment(NewsDiscussionFragment.class, args);
        }
    }

    @Override
    protected DTOCache.Listener<DiscussionKey, AbstractDiscussionCompactDTO> createDiscussionFetchListener()
    {
        // We are ok with the NewsItemDTO being saved in cache, but we do not want
        // to get it here...
        return null;
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
