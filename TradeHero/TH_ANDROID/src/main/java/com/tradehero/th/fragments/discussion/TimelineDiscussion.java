package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.timeline.TimelineItemDTOKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.timeline.TimelineItemView;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.persistence.timeline.TimelineCache;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 11:48 AM Copyright (c) TradeHero
 */
public class TimelineDiscussion extends DashboardFragment
{
    @InjectView(android.R.id.list) ListView commentList;

    @Inject TimelineCache timelineCache;
    private TimelineItemView timelineItemView;
    private CommentListAdapter commentListAdapter;
    private TimelineItemDTOKey timelineItemDTOKey;
    private View commentListStatusView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_discussion, container, false);
        timelineItemView = (TimelineItemView) inflater.inflate(R.layout.timeline_item_view, null);
        commentListStatusView = inflater.inflate(R.layout.discussion_load_status, null);

        ButterKnife.inject(this, view);

        initView(view);
        return view;
    }

    private void initView(View view)
    {
        if (timelineItemView != null)
        {
            commentList.addHeaderView(timelineItemView);
            commentList.addHeaderView(commentListStatusView);
        }
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (timelineItemDTOKey == null)
        {
            timelineItemDTOKey = new TimelineItemDTOKey(getArguments());
        }
        linkWith(timelineItemDTOKey, true);

        getActivity().getSupportLoaderManager()
                .initLoader(commentListAdapter.getLoaderId(), null, commentListAdapter.getLoaderCallback());
    }

    private void linkWith(TimelineItemDTOKey timelineItemDTOKey, boolean andDisplay)
    {
        if (timelineItemView != null)
        {
            timelineItemView.display(timelineCache.get(timelineItemDTOKey));
        }

        commentListAdapter = createCommentListAdapter();
        commentList.setAdapter(commentListAdapter);
    }

    @Override public void onResume()
    {
        super.onResume();

        Timber.d("Timeline item id: %d", timelineItemDTOKey.key);
    }

    private CommentListAdapter createCommentListAdapter()
    {
        CommentListAdapter adapter = new CommentListAdapter(getActivity(), getActivity().getLayoutInflater(),
                timelineItemDTOKey.key, R.layout.timeline_discussion_comment_item);
        adapter.setDTOLoaderCallback(new LoaderDTOAdapter.ListLoaderCallback<DiscussionDTO>()
        {
            @Override protected void onLoadFinished(ListLoader<DiscussionDTO> loader, List<DiscussionDTO> data)
            {

            }

            @Override protected ListLoader<DiscussionDTO> onCreateLoader(Bundle args)
            {
                return null;
            }
        });
        return adapter;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.discussion);
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
