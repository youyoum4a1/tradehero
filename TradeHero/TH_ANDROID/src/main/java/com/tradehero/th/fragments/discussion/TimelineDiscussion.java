package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.timeline.TimelineItemDTOKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.timeline.TimelineItemView;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.timeline.TimelineCache;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 11:48 AM Copyright (c) TradeHero
 */
public class TimelineDiscussion extends DashboardFragment
{
    @InjectView(android.R.id.list) ListView commentList;
    @InjectView(R.id.timeline_discussion_comment) EditText comment;

    @Inject TimelineCache timelineCache;
    @Inject DiscussionServiceWrapper discussionServiceWrapper;

    private TimelineItemView timelineItemView;
    private DiscussionListAdapter discussionListAdapter;
    private TimelineItemDTOKey timelineItemDTOKey;
    private View commentListStatusView;
    private MiddleCallback<DiscussionDTO> discussionMiddleCallback;

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
                .initLoader(discussionListAdapter.getLoaderId(), null, discussionListAdapter.getLoaderCallback());
    }

    private void linkWith(TimelineItemDTOKey timelineItemDTOKey, boolean andDisplay)
    {
        if (timelineItemView != null)
        {
            timelineItemView.display(timelineCache.get(timelineItemDTOKey));
        }

        discussionListAdapter = createCommentListAdapter();
        commentList.setAdapter(discussionListAdapter);
    }

    @Override public void onResume()
    {
        super.onResume();

        Timber.d("Timeline item id: %d", timelineItemDTOKey.key);
    }

    private DiscussionListAdapter createCommentListAdapter()
    {
        DiscussionListAdapter adapter = new DiscussionListAdapter(getActivity(), getActivity().getLayoutInflater(),
                timelineItemDTOKey.key, R.layout.timeline_discussion_comment_item);
        adapter.setDTOLoaderCallback(new LoaderDTOAdapter.ListLoaderCallback<DiscussionDTO>()
        {
            @Override protected void onLoadFinished(ListLoader<DiscussionDTO> loader, List<DiscussionDTO> data)
            {

            }

            @Override protected ListLoader<DiscussionDTO> onCreateLoader(Bundle args)
            {
                return createTimelineDiscussionLoader();
            }
        });
        return adapter;
    }

    private ListLoader<DiscussionDTO> createTimelineDiscussionLoader()
    {
        return new DiscussionListLoader(getActivity(), timelineItemDTOKey);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.discussion);
    }

    @Override public void onDestroyView()
    {
        detachCommentSubmitMiddleCallback();

        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @OnClick(R.id.timeline_discussion_comment_post) void postComment()
    {
        detachCommentSubmitMiddleCallback();
        DiscussionDTO discussionDTO = new DiscussionDTO();
        discussionDTO.text = getEditingComment();
        discussionDTO.inReplyToType = DiscussionType.TIMELINE_ITEM;
        discussionDTO.inReplyToId = timelineItemDTOKey.key;

        comment.setText(null);
        discussionMiddleCallback = discussionServiceWrapper.createDiscussion(discussionDTO, createCommentSubmitCallback());
    }

    private void detachCommentSubmitMiddleCallback()
    {
        if (discussionMiddleCallback != null)
        {
            discussionMiddleCallback.setPrimaryCallback(null);
        }
        discussionMiddleCallback = null;
    }

    private Callback<DiscussionDTO> createCommentSubmitCallback()
    {
        return new CommentSubmitCallback();
    }

    private String getEditingComment()
    {
        return comment.getText().toString();
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    private class CommentSubmitCallback implements Callback<DiscussionDTO>
    {
        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            Timber.d("Comment submitted successfully: %s", discussionDTO.text);
        }

        @Override public void failure(RetrofitError error)
        {
            THToast.show(new THException(error));
        }
    }
}
