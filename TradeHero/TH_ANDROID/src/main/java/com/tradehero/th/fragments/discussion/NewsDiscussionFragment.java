package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsCache;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.NewsItemDTOKey;
import com.tradehero.th.fragments.news.NewsDiscussionListLoader;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.misc.exception.THException;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 11:48 AM Copyright (c) TradeHero
 */
public class NewsDiscussionFragment extends AbstractDiscussionFragment
{
    @Inject NewsCache newsCache;
    private CommentView discussionItemView;
    private NewsItemDTOKey newsItemDTOKey;
    private DTOCache.Listener<NewsItemDTOKey, NewsItemDTO> newsFetchListener;
    private DTOCache.GetOrFetchTask<NewsItemDTOKey, NewsItemDTO> newsFetchTask;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_discussion, container, false);
        discussionItemView = (CommentView) inflater.inflate(R.layout.news_discussion_comment_item, null);

        ButterKnife.inject(this, view);

        newsFetchListener = new NewsFetchListener();
        return view;
    }

    @Override public void onDestroyView()
    {
        detachNewsFetchTask();

        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void detachNewsFetchTask()
    {
        if (newsFetchTask != null)
        {
            newsFetchTask.setListener(null);
        }
        newsFetchTask = null;
    }

    @Override protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        if (discussionKey instanceof NewsItemDTOKey)
        {
            linkWith((NewsItemDTOKey) discussionKey, true);
        }

        super.linkWith(discussionKey, andDisplay);
    }

    protected void linkWith(NewsItemDTOKey newsItemDTOKey, boolean andDisplay)
    {
        this.newsItemDTOKey = newsItemDTOKey;

        detachNewsFetchTask();
        newsFetchTask = newsCache.getOrFetch(newsItemDTOKey, false, newsFetchListener);
        newsFetchTask.execute();
    }

    private void linkWith(NewsItemDTO newsItemDTO, boolean andDisplay)
    {
        if (andDisplay)
        {
            discussionItemView.display(newsItemDTO);
        }
    }

    @Override protected ListLoader<DiscussionDTO> createDiscussionLoader()
    {
        return new NewsDiscussionListLoader(getActivity(), newsItemDTOKey);
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    private class NewsFetchListener implements DTOCache.Listener<NewsItemDTOKey, NewsItemDTO>
    {
        @Override public void onDTOReceived(NewsItemDTOKey key, NewsItemDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(NewsItemDTOKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
