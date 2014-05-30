package com.tradehero.th.fragments.news;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemView;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.persistence.news.NewsItemCompactCacheNew;
import javax.inject.Inject;

public class NewsHeadlineView extends AbstractDiscussionItemView<NewsItemDTOKey>
        implements THDialog.OnDialogItemClickListener
{
    @Inject NewsItemCompactCacheNew newsItemCompactCache;

    //<editor-fold desc="Constructors">
    public NewsHeadlineView(Context context)
    {
        super(context);
    }

    public NewsHeadlineView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NewsHeadlineView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        socialShareHelper.setMenuClickedListener(createSocialShareMenuClickedListener());
        viewHolder.setMenuClickedListener(createViewHolderMenuClickedListener());
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        socialShareHelper.setMenuClickedListener(createSocialShareMenuClickedListener());
        viewHolder.setMenuClickedListener(createViewHolderMenuClickedListener());
    }

    @Override protected void onDetachedFromWindow()
    {
        socialShareHelper.onDetach();
        super.onDetachedFromWindow();
        viewHolder.setMenuClickedListener(null);
    }

    @Override protected NewsItemCompactViewHolder createViewHolder()
    {
        return new NewsItemCompactViewHolder<NewsItemCompactDTO>();
    }

    //<editor-fold desc="Related to share dialog">
    @Override
    public void onClick(int whichButton)
    {
        switch (whichButton)
        {
            case 0:
                break;
            case 1:
                break;
        }
    }
    //</editor-fold>

    @Override public void display(NewsItemDTOKey discussionKey)
    {
        super.display(discussionKey);
        linkWith(newsItemCompactCache.get(discussionKey), true);
    }

    @Override protected SecurityId getSecurityId()
    {
        throw new IllegalStateException("It has no securityId");
    }

    @Override protected void pushDiscussionFragment()
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
}
