package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
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
    @InjectView(R.id.discussion_view) DiscussionView discussionView;

    @Inject DiscussionServiceWrapper discussionServiceWrapper;

    private MiddleCallback<DiscussionDTO> discussionMiddleCallback;
    protected DiscussionKey discussionKey;

    private TextView discussionStatus;

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
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
            discussionKey = DiscussionKeyFactory.fromBundle(getArguments());
        }

        linkWith(discussionKey, true);
    }

    protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        if (andDisplay)
        {
            discussionView.display(discussionKey);
        }
    }

    @Override public void onDestroyView()
    {
        detachCommentSubmitMiddleCallback();
        super.onDestroyView();
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
