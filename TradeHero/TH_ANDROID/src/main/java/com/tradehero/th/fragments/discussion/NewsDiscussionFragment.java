package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionKey;
import com.tradehero.th.api.news.NewsItemDTOKey;
import com.tradehero.th.fragments.news.NewsDiscussionListLoader;
import com.tradehero.th.loaders.ListLoader;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 11:48 AM Copyright (c) TradeHero
 */
public class NewsDiscussionFragment extends AbstractDiscussionFragment
{
    private DiscussionView discussionItemView;
    private NewsItemDTOKey newsItemDTOKey;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_discussion, container, false);
        discussionItemView = (DiscussionView) inflater.inflate(R.layout.news_discussion_comment_item, null);

        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        if (discussionItemView != null)
        {
            discussionList.addHeaderView(discussionItemView);
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        linkWith(new NewsItemDTOKey(discussionKey), true);

        super.linkWith(discussionKey, andDisplay);
    }

    protected void linkWith(NewsItemDTOKey timelineItemDTOKey, boolean andDisplay)
    {
        this.newsItemDTOKey = timelineItemDTOKey;
        if (andDisplay)
        {
            //discussionItemView.display(newsItemDTOKey);
        }
    }

    @Override protected DiscussionKey getDiscussionKeyFromBundle(Bundle arguments)
    {
        return new NewsItemDTOKey(super.getDiscussionKeyFromBundle(arguments));
    }

    @Override protected ListLoader<DiscussionDTO> createDiscussionLoader()
    {
        return new NewsDiscussionListLoader(getActivity(), newsItemDTOKey);
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
