package com.tradehero.th.fragments.leaderboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefKeyList;
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
    implements BaseFragment.ArgumentsChangeListener, DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList>
{
    private static final String TAG = LeaderboardDefListViewFragment.class.getName();

    private LeaderboardDefListAdapter leaderboardDefListAdapter;

    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        updateLeaderboardDefListKey(getArguments());
        leaderboardDefListAdapter = new LeaderboardDefListAdapter(getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_def_item);
        setListAdapter(leaderboardDefListAdapter);
    }

    //@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    //{
    //    updateLeaderboardDefListKey(getArguments());
    //    leaderboardDefListAdapter = new LeaderboardDefListAdapter(getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_def_item);
    //    setListAdapter(leaderboardDefListAdapter);
    //    return super.onCreateView(inflater, container, savedInstanceState);
    //}

    private void updateLeaderboardDefListKey(Bundle bundle)
    {
        LeaderboardDefListKey key = new LeaderboardDefListKey(bundle);
        leaderboardDefListCache.get().getOrFetch(key, false, this).execute();
    }

    @Override public void onArgumentsChanged(Bundle args)
    {
        updateLeaderboardDefListKey(args);
        setArguments(args);
    }

    @Override public void onDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value)
    {
        List<LeaderboardDefDTO> leaderboardDefItems = leaderboardDefCache.get().getOrFetch(value);
        leaderboardDefListAdapter.setItems(leaderboardDefItems);
        leaderboardDefListAdapter.notifyDataSetChanged();
    }
}
