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
import com.tradehero.th.api.leaderboard.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/17/13 Time: 7:21 PM Copyright (c) TradeHero */
public class LeaderboardDefListViewFragment extends AbstractLeaderboardFragment
    implements
        DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList>
{
    private static final String TAG = LeaderboardDefListViewFragment.class.getName();

    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;

    private LeaderboardDefListAdapter leaderboardDefListAdapter;
    private ListView contentListView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_def_listview, container, false);
        contentListView = (ListView) view.findViewById(android.R.id.list);
        return view;
    }

    @Override public void onResume()
    {
        Bundle args = getArguments();

        updateLeaderboardDefListKey(args);

        leaderboardDefListAdapter = new LeaderboardDefListAdapter(getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_def_item);
        contentListView.setAdapter(leaderboardDefListAdapter);
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                LeaderboardDefDTO dto = (LeaderboardDefDTO) leaderboardDefListAdapter.getItem(position);
                if (dto != null)
                {
                    pushLeaderboardListViewFragment(dto);
                }
            }
        });

        super.onResume();
    }


    private void updateLeaderboardDefListKey(Bundle bundle)
    {
        LeaderboardDefListKey key = new LeaderboardDefListKey(bundle);
        leaderboardDefListCache.get().getOrFetch(key, false, this).execute();
    }

    @Override public void onDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value)
    {
        List<LeaderboardDefDTO> leaderboardDefItems = null;
        try
        {
            leaderboardDefItems = leaderboardDefCache.get().getOrFetch(value);
        }
        catch (Throwable error)
        {
            onErrorThrown(key, error);
        }
        leaderboardDefListAdapter.setItems(leaderboardDefItems);
        leaderboardDefListAdapter.notifyDataSetChanged();
    }

    @Override public void onErrorThrown(LeaderboardDefListKey key, Throwable error)
    {
        THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key));
        THLog.e(TAG, "Error fetching the leaderboard def key list " + key, error);
    }

}
