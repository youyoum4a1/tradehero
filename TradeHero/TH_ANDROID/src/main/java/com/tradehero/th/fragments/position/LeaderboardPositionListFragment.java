package com.tradehero.th.fragments.position;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.position.GetLeaderboardPositionsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.persistence.leaderboard.position.GetLeaderboardPositionsCache;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 11/6/13 Time: 12:57 PM Copyright (c) TradeHero */
public class LeaderboardPositionListFragment extends PositionListFragment
{
    public static final String TAG = LeaderboardPositionListFragment.class.getSimpleName();

    @Inject Lazy<GetLeaderboardPositionsCache> getLeaderboardPositionsCache;

    private DTOCache.Listener<LeaderboardMarkUserId, GetLeaderboardPositionsDTO> getLeaderboardPositionsCacheListener;
    private DTOCache.GetOrFetchTask<LeaderboardMarkUserId, GetLeaderboardPositionsDTO> fetchGetPositionsDTOTask;

    private LeaderboardMarkUserId leaderboardMarkUserId;
    private boolean timeRestricted;

    @Override protected void createPositionItemAdapter()
    {
        timeRestricted = getArguments().getBoolean(LeaderboardDefDTO.LEADERBOARD_DEF_TIME_RESTRICTED, false);
        if (positionItemAdapter != null)
        {
            positionItemAdapter.setCellListener(null);
        }
        positionItemAdapter = new LeaderboardPositionItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                R.layout.position_item_header,
                R.layout.position_locked_item,
                R.layout.position_open_in_period,
                R.layout.position_closed_in_period,
                R.layout.position_quick_nothing,
                timeRestricted);
        positionItemAdapter.setCellListener(this);
    }

    @Override public void onResume()
    {
        this.leaderboardMarkUserId = new LeaderboardMarkUserId((int) getArguments().getLong(LeaderboardMarkUserId.BUNDLE_KEY));

        String periodStart = getArguments().getString(LeaderboardUserDTO.LEADERBOARD_PERIOD_START_STRING);
        THLog.d(TAG, "Period Start: " + periodStart);

        super.onResume();
    }

    @Override protected void fetchSimplePage()
    {
        if (ownedPortfolioId != null && ownedPortfolioId.isValid())
        {
            if (getLeaderboardPositionsCacheListener == null)
            {
                getLeaderboardPositionsCacheListener = createGetLeaderboardPositionsCacheListener();
            }
            if (fetchGetPositionsDTOTask != null)
            {
                fetchGetPositionsDTOTask.setListener(null);
            }
            fetchGetPositionsDTOTask = getLeaderboardPositionsCache.get().getOrFetch(leaderboardMarkUserId, getLeaderboardPositionsCacheListener);
            fetchGetPositionsDTOTask.execute();
        }
    }

    private DTOCache.Listener<LeaderboardMarkUserId, GetLeaderboardPositionsDTO> createGetLeaderboardPositionsCacheListener()
    {
        return new DTOCache.Listener<LeaderboardMarkUserId, GetLeaderboardPositionsDTO>()
        {
            @Override public void onDTOReceived(LeaderboardMarkUserId key, GetLeaderboardPositionsDTO value)
            {
                if (key.equals(leaderboardMarkUserId))
                {
                    linkWith(value, true);
                }
            }

            @Override public void onErrorThrown(LeaderboardMarkUserId key, Throwable error)
            {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    @SuppressWarnings("unchecked")
    private void linkWith(GetLeaderboardPositionsDTO leaderboardPositionsDTO, boolean andDisplay)
    {
        if (leaderboardPositionsDTO != null && ownedPortfolioId != null)
        {
            createPositionItemAdapter();
            positionItemAdapter.setPositions(leaderboardPositionsDTO.positions, ownedPortfolioId.getPortfolioId());
            restoreExpandingStates();
            if (positionsListView != null)
            {
                positionsListView.setAdapter(positionItemAdapter);
            }
        }

        if (andDisplay)
        {
            display();
        }
    }
}
