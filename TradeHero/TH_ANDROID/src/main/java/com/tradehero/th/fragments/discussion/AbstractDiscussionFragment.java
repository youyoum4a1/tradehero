package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by tho on 3/27/2014.
 */
public abstract class AbstractDiscussionFragment extends DashboardFragment
{
    @InjectView(R.id.post_comment_text) EditText comment;

    @InjectView(R.id.discussion_comment_widget) PostCommentView postCommentView;
    @InjectView(android.R.id.list) ListView discussionList;

    @Inject DiscussionServiceWrapper discussionServiceWrapper;

    private MiddleCallback<DiscussionDTO> discussionMiddleCallback;
    protected DiscussionKey discussionKey;
    protected DiscussionListAdapter discussionListAdapter;

    private TextView discussionStatus;

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        View commentListStatusView = LayoutInflater.from(getActivity()).inflate(R.layout.discussion_load_status, null);
        if (commentListStatusView != null)
        {
            discussionStatus = (TextView) commentListStatusView.findViewById(R.id.discussion_load_status);
            discussionList.addHeaderView(commentListStatusView);
        }
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.discussion);
    }

    @Override public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (discussionKey == null)
        {
            discussionKey = getDiscussionKeyFromBundle(getArguments());
        }

        linkWith(discussionKey, true);

        discussionListAdapter = createDiscussionListAdapter();
        discussionList.setAdapter(discussionListAdapter);
        getActivity().getSupportLoaderManager()
                .initLoader(discussionListAdapter.getLoaderId(), null, discussionListAdapter.getLoaderCallback());
    }

    protected DiscussionKey getDiscussionKeyFromBundle(Bundle arguments)
    {
        return DiscussionKeyFactory.fromBundle(getArguments());
    }

    protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
    }

    @Override public void onDestroyView()
    {
        if (discussionListAdapter != null)
        {
            discussionListAdapter.setDTOLoaderCallback(null);
        }
        detachCommentSubmitMiddleCallback();
        super.onDestroyView();
    }

    protected DiscussionListAdapter createDiscussionListAdapter()
    {
        DiscussionListAdapter adapter = new DiscussionListAdapter(
                getActivity(), getActivity().getLayoutInflater(), discussionKey.id, R.layout.timeline_discussion_comment_item);
        adapter.setDTOLoaderCallback(new LoaderDTOAdapter.ListLoaderCallback<DiscussionDTO>()
        {
            @Override protected void onLoadFinished(ListLoader<DiscussionDTO> loader, List<DiscussionDTO> data)
            {
                if (discussionStatus != null)
                {
                    int statusResource = discussionListAdapter.getCount() != 0 ? R.string.discussion_loaded : R.string.discussion_empty;
                    discussionStatus.setText(getString(statusResource));
                }
            }

            @Override protected ListLoader<DiscussionDTO> onCreateLoader(Bundle args)
            {
                return createDiscussionLoader();
            }
        });
        return adapter;
    }

    protected abstract ListLoader<DiscussionDTO> createDiscussionLoader();

    private void detachCommentSubmitMiddleCallback()
    {
        if (discussionMiddleCallback != null)
        {
            discussionMiddleCallback.setPrimaryCallback(null);
        }
        discussionMiddleCallback = null;
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

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
