package com.tradehero.th.fragments.discussion;

import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created by tho on 3/27/2014.
 */
public abstract class AbstractDiscussionFragment extends DashboardFragment
{
    @InjectView(R.id.discussion_view) DiscussionView discussionView;

    protected DiscussionKey discussionKey;

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.discussion);
    }

    @Override public void onResume()
    {
        super.onResume();

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

    @Override public boolean isTabBarVisible()
    {
        return false;
    }
}
