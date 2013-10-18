package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.BaseListFragment;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 10/17/13 Time: 7:21 PM Copyright (c) TradeHero */
public class LeaderboardDefListViewFragment extends BaseListFragment
    implements BaseFragment.ArgumentsChangeListener, DTOCache.Listener<LeaderboardDefListKey,List<LeaderboardDefKey>>
{
    private static final String TAG = LeaderboardDefListViewFragment.class.getName();

    private LeaderboardDefListAdapter leaderboardDefListAdapter;

    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        updateLeaderboardDefListKey();
        leaderboardDefListAdapter = new LeaderboardDefListAdapter(getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_def_item);
        setListAdapter(leaderboardDefListAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void updateLeaderboardDefListKey()
    {
        LeaderboardDefListKey key = new LeaderboardDefListKey(getArguments());
        leaderboardDefListCache.get().getOrFetch(key, false, this).execute();
    }

    @Override public void onArgumentsChanged(Bundle args)
    {
        updateLeaderboardDefListKey();
    }

    @Override public void onDTOReceived(LeaderboardDefListKey key, List<LeaderboardDefKey> value)
    {
        List<LeaderboardDefDTO> leaderboardDefItems = leaderboardDefCache.get().getOrFetch(value);
        leaderboardDefListAdapter.setItems(leaderboardDefItems);
        leaderboardDefListAdapter.notifyDataSetChanged();
    }
}
