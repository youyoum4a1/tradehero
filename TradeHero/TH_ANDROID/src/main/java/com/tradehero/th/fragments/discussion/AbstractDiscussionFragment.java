package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import javax.inject.Inject;

abstract public class AbstractDiscussionFragment extends BasePurchaseManagerFragment
{
    public static final String DISCUSSION_KEY_BUNDLE_KEY = AbstractDiscussionFragment.class.getName() + ".discussionKey";

    @InjectView(R.id.discussion_view) protected DiscussionView discussionView;

    @Inject protected DiscussionKeyFactory discussionKeyFactory;

    private DiscussionKey discussionKey;

    public static void putDiscussionKey(Bundle args, DiscussionKey discussionKey)
    {
        args.putBundle(DISCUSSION_KEY_BUNDLE_KEY, discussionKey.getArgs());
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        ButterKnife.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override protected void initViews(View view)
    {
        discussionView.setCommentPostedListener(createCommentPostedListener());
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
    }

    @Override public void onResume()
    {
        super.onResume();

        if (discussionKey == null && getArguments().containsKey(DISCUSSION_KEY_BUNDLE_KEY))
        {
            discussionKey = discussionKeyFactory.fromBundle(getArguments().getBundle(DISCUSSION_KEY_BUNDLE_KEY));
        }
        if (discussionKey != null)
        {
            linkWith(discussionKey, true);
        }
    }

    public DiscussionKey getDiscussionKey()
    {
        return discussionKey;
    }

    protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        this.discussionKey = discussionKey;
        if (andDisplay && discussionView != null)
        {
            discussionView.display(discussionKey);
        }
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
    }

    protected void handleCommentPostFailed(Exception exception)
    {
    }

    protected PostCommentView.CommentPostedListener createCommentPostedListener()
    {
        return new AbstractDiscussionCommentPostedListener();
    }

    protected class AbstractDiscussionCommentPostedListener implements PostCommentView.CommentPostedListener
    {
        @Override public void success(DiscussionDTO discussionDTO)
        {
            handleCommentPosted(discussionDTO);
        }

        @Override public void failure(Exception exception)
        {
            handleCommentPostFailed(exception);
        }
    }
}
