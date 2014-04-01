package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

/**
 * Created by tho on 4/1/2014.
 */
public class CompetitionLeaderboardMarkUserListAdapter extends BaseAdapter
    implements WrapperListAdapter
{
    private final Context context;
    private final LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter;

    public CompetitionLeaderboardMarkUserListAdapter(Context context, LeaderboardMarkUserListAdapter leaderboardMarkUserListAdapter)
    {
        this.context = context;
        this.leaderboardMarkUserListAdapter = leaderboardMarkUserListAdapter;
    }

    @Override public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
    }

    @Override public ListAdapter getWrappedAdapter()
    {
        return leaderboardMarkUserListAdapter;
    }

    @Override public int getCount()
    {
        return leaderboardMarkUserListAdapter.getCount();
    }

    @Override public Object getItem(int position)
    {
        return leaderboardMarkUserListAdapter.getItem(position);
    }

    @Override public long getItemId(int position)
    {
        return leaderboardMarkUserListAdapter.getItemId(position);
    }

    @Override public View getView(int position, View convertView, ViewGroup parent)
    {
        return leaderboardMarkUserListAdapter.getView(position, convertView, parent);
    }
}
