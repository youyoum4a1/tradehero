package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import java.util.List;
import javax.inject.Inject;

public class LeaderboardFragment extends DashboardFragment
        implements View.OnClickListener, DTOCache.Listener<LeaderboardDefListKey,List<LeaderboardDefDTO>>
{
    private static final String TAG = LeaderboardFragment.class.getName();

    @Inject protected LeaderboardDefListCache leaderboardDefListCache;
    @Inject protected ProviderListCache providerCache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_screen, container, false);

        LeaderboardDefListKey lbDefKey = new LeaderboardDefListKey();
        //leaderboardDefListCache.getOrFetch(lbDefKey, false, this).execute();
        //providerCache.getOrFe

        Button btnMostSkilled = (Button) view.findViewById(R.id.leaderboard_most_skilled);
        btnMostSkilled.setOnClickListener(this);

        return view;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.leaderboard_menu, menu);
        getSherlockActivity().getSupportActionBar().setTitle(getString(R.string.leaderboards));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO switch sorting type for leaderboard
        switch (item.getItemId())
        {
            case R.id.leaderboard_sort_by_hero_quotient:
                break;
            case R.id.leaderboard_sort_by_roi:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override public void onClick(View view)
    {
        // create bundle for the ranking board
        switch (view.getId())
        {
            case R.id.leaderboard_most_skilled:
                break;

        }

        // navigate to ranking board
        navigator.pushFragment(LeaderboardRankingListViewFragment.class);
    }

    @Override public void onDTOReceived(LeaderboardDefListKey key, List<LeaderboardDefDTO> value)
    {
        THLog.d(TAG, value.size() + "");
    }
}
