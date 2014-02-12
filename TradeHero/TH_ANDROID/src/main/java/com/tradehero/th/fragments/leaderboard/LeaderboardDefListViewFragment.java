package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/17/13 Time: 7:21 PM Copyright (c) TradeHero */
public class LeaderboardDefListViewFragment extends BaseLeaderboardFragment
        implements DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList>
{
    private static final String TAG = LeaderboardDefListViewFragment.class.getName();

    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;

    private LeaderboardDefListAdapter leaderboardDefListAdapter;
    private ListView contentListView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_def_listview, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        contentListView = (ListView) view.findViewById(android.R.id.list);
    }

    @Override public void onResume()
    {
        Bundle args = getArguments();

        leaderboardDefListAdapter =
                new LeaderboardDefListAdapter(getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_definition_item_view);
        contentListView.setAdapter(leaderboardDefListAdapter);
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Object item = leaderboardDefListAdapter.getItem(position);
                if (item instanceof LeaderboardDefKey)
                {
                    LeaderboardDefDTO dto = leaderboardDefCache.get().get((LeaderboardDefKey) item);
                    if (dto != null)
                    {
                        pushLeaderboardListViewFragment(dto);
                    }
                }
            }
        });
        updateLeaderboardDefListKey(args);
        refresh();
        super.onResume();
    }

    protected void refresh()
    {
        if (leaderboardDefListAdapter != null)
        {
            leaderboardDefListAdapter.notifyDataSetChanged();
        }
    }

    private void updateLeaderboardDefListKey(Bundle bundle)
    {
        LeaderboardDefListKey key = new LeaderboardDefListKey(bundle);
        leaderboardDefListCache.get().getOrFetch(key, false, this).execute();
    }

    @Override public void onDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value, boolean fromCache)
    {
        if (leaderboardDefListAdapter != null)
        {
            leaderboardDefListAdapter.setItems(value);
            leaderboardDefListAdapter.notifyDataSetChanged();
        }
    }

    @Override public void onErrorThrown(LeaderboardDefListKey key, Throwable error)
    {
        THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key));
        THLog.e(TAG, "Error fetching the leaderboard def key list " + key, error);
    }
}
