package com.tradehero.th.fragments.timeline;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 10/23/13 Time: 5:41 PM To change this template use File | Settings | File Templates.
 *
 * This fragment will not be the main, but one that is pushed from elsewhere
 */
public class PushableTimelineFragment extends TimelineFragment
{
    public static final String TAG = PushableTimelineFragment.class.getSimpleName();

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.timeline_menu_pushable_other, menu);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //super.onCreateOptionsMenu(menu, inflater);
        displayActionBarTitle();
    }

    public void displayActionBarTitle()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (profile != null)
        {
            actionBar.setTitle(String.format(getString(R.string.first_last_name_display), profile.firstName, profile.lastName));
        }
        else
        {
            actionBar.setTitle(R.string.loading_loading);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_follow_this_user:
                handleInfoButtonPressed(item);
                break;

            case android.R.id.home:
                handleHomeButtonPressed(item);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleHomeButtonPressed(MenuItem menuItem)
    {
        Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
        navigator.popFragment();
    }

    private void handleInfoButtonPressed(MenuItem menuItem)
    {
        THToast.show("Nothing for now");
    }

    @Override protected void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        super.linkWith(userProfileDTO, andDisplay);
        if (andDisplay)
        {
            displayActionBarTitle();
        }
    }
}
