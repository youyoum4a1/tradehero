package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.key.FriendsPerPagedLeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.utils.LocalyticsConstants;

/** Created with IntelliJ IDEA. User: tho Date: 11/21/13 Time: 6:26 PM Copyright (c) TradeHero */
public class FriendLeaderboardMarkUserListFragment extends BaseFragment /**LeaderboardMarkUserListFragment*/
{

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_mark_user_listview_2, container, false);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        addTabs();
    }

    public void addTabs() {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        Fragment f0 = new LeaderBorarTabContentFragment(0);
        f0.setArguments(getArguments());

        Fragment f1 = new LeaderBorarTabContentFragment(0);
        f1.setArguments(getArguments());

        Fragment f2 = new LeaderBorarTabContentFragment(0);
        f2.setArguments(getArguments());

        ////Action Bar Tab must have a Callback
        actionBar.addTab(actionBar.newTab().setText("Premium").setTabListener(new TabListener(f0)));
        actionBar.addTab(actionBar.newTab().setText("Free").setTabListener(new TabListener(f1)));
        actionBar.addTab(actionBar.newTab().setText("All").setTabListener(new TabListener(f2)));

    }





    private class TabListener implements ActionBar.TabListener {


        private Fragment mFragment;

        public TabListener(Fragment fragment) {
            mFragment = fragment;
        }

        @Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft)
        {

            ft.add(R.id.fragment_content, mFragment, mFragment.getTag());
        }

        @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            ft.remove(mFragment);
        }

        @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft)
        {
            //Toast.makeText(ActionBarTabs.this, "Reselected!", Toast.LENGTH_SHORT).show();
        }

    }

    public static class LeaderBorarTabContentFragment extends LeaderboardMarkUserListFragment {

        private int page;
        public LeaderBorarTabContentFragment(int page)
        {
            this.page = page;
        }

        @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            View view = inflater.inflate(R.layout.leaderboard_mark_user_listview, container, false);
            initViews(view);
            inflateHeaderView(inflater, container);

            if (leaderboardMarkUserListView != null)
            {
                leaderboardMarkUserListView.setEmptyView(inflateEmptyView(inflater, container));
            }
            return view;
        }


        @Override public void onResume()
        {
            super.onResume();

            localyticsSession.tagEvent(LocalyticsConstants.FriendsLeaderboard_Filter_FoF);
        }

        @Override protected int getMenuResource()
        {
            return R.menu.friend_leaderboard_menu;
        }

        @Override public boolean onOptionsItemSelected(MenuItem item)
        {
            if (leaderboardMarkUserLoader != null)
            {
                boolean oldIncludeFoF = leaderboardMarkUserLoader.isIncludeFoF();
                switch (item.getItemId())
                {
                    case R.id.friend_leaderboard_menu_show_friends_of_friends:
                        if (!oldIncludeFoF)
                        {
                            setFriendOfFriendFilter(true);
                        }
                        return true;
                    case R.id.friend_leaderboard_menu_show_friends_only:
                        if (oldIncludeFoF)
                        {
                            setFriendOfFriendFilter(false);
                        }
                        return true;
                }
            }
            return super.onOptionsItemSelected(item);
        }

        @Override protected PerPagedLeaderboardKey getInitialLeaderboardKey()
        {
            return new FriendsPerPagedLeaderboardKey(leaderboardId, null, null, false);
        }

        @Override protected void saveCurrentFilterKey()
        {
            // Do nothing really
        }

        @Override protected View inflateEmptyView(LayoutInflater inflater, ViewGroup container)
        {
            return inflater.inflate(R.layout.friend_leaderboard_empty_view, container, false);
        }

        private void setFriendOfFriendFilter(boolean isFoF)
        {
            currentLeaderboardKey = new FriendsPerPagedLeaderboardKey(
                    currentLeaderboardKey.key,
                    currentLeaderboardKey.page,
                    currentLeaderboardKey.perPage,
                    isFoF);
            leaderboardMarkUserLoader.setPagedLeaderboardKey(currentLeaderboardKey);
            leaderboardMarkUserLoader.reload();
            //invalidateCachedItemView();
        }



    }
}
