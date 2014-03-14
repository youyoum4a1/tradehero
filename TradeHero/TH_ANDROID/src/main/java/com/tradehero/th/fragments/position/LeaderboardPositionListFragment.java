package com.tradehero.th.fragments.position;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.position.GetLeaderboardPositionsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.fragments.trade.TradeListInPeriodFragment;
import com.tradehero.th.persistence.leaderboard.position.GetLeaderboardPositionsCache;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 11/6/13 Time: 12:57 PM Copyright (c) TradeHero */
public class LeaderboardPositionListFragment
        extends AbstractPositionListFragment<LeaderboardMarkUserId, PositionInPeriodDTO, GetLeaderboardPositionsDTO>
{
    public static final String TAG = LeaderboardPositionListFragment.class.getSimpleName();

    @Inject GetLeaderboardPositionsCache getLeaderboardPositionsCache;

    private DTOCache.Listener<LeaderboardMarkUserId, GetLeaderboardPositionsDTO> getLeaderboardPositionsCacheListener;
    private DTOCache.GetOrFetchTask<LeaderboardMarkUserId, GetLeaderboardPositionsDTO> fetchGetPositionsDTOTask;

    private LeaderboardMarkUserId leaderboardMarkUserId;
    private boolean timeRestricted;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getLeaderboardPositionsCacheListener = createGetLeaderboardPositionsCacheListener();
    }

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

    @Override protected DTOCache.Listener<LeaderboardMarkUserId, GetLeaderboardPositionsDTO> createGetPositionsCacheListener()
    {
        return new GetLeaderboardPositionsListener();
    }

    @Override protected DTOCache.GetOrFetchTask<LeaderboardMarkUserId, GetLeaderboardPositionsDTO> createGetPositionsCacheFetchTask(boolean force)
    {
        return getLeaderboardPositionsCache.getOrFetch(leaderboardMarkUserId, force, getLeaderboardPositionsCacheListener);
    }

    @Override public void onResume()
    {
        this.leaderboardMarkUserId = new LeaderboardMarkUserId((int) getArguments().getLong(LeaderboardMarkUserId.BUNDLE_KEY));

        String periodStart = getArguments().getString(LeaderboardUserDTO.LEADERBOARD_PERIOD_START_STRING);
        THLog.d(TAG, "Period Start: " + periodStart);

        super.onResume();
    }

    @Override public void onDestroyView()
    {
        detachGetLeaderboardPositions();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        getLeaderboardPositionsCacheListener = null;
        super.onDestroy();
    }

    protected void detachGetLeaderboardPositions()
    {
        if (fetchGetPositionsDTOTask != null)
        {
            fetchGetPositionsDTOTask.setListener(null);
        }
        fetchGetPositionsDTOTask = null;
    }

    @Override protected void fetchSimplePage()
    {
        fetchSimplePage(false);
    }

    @Override protected void fetchSimplePage(boolean force)
    {
        if (shownOwnedPortfolioId != null && shownOwnedPortfolioId.isValid())
        {
            detachGetLeaderboardPositions();
            fetchGetPositionsDTOTask = createGetPositionsCacheFetchTask(force);
            fetchGetPositionsDTOTask.execute();
        }
    }

    private DTOCache.Listener<LeaderboardMarkUserId, GetLeaderboardPositionsDTO> createGetLeaderboardPositionsCacheListener()
    {
        return new DTOCache.Listener<LeaderboardMarkUserId, GetLeaderboardPositionsDTO>()
        {
            @Override public void onDTOReceived(LeaderboardMarkUserId key, GetLeaderboardPositionsDTO value, boolean fromCache)
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

    @Override protected void createUserInteractor()
    {
        userInteractor = new LeaderboardPositionListTHIABUserInteractor();
    }

    @Override public void onTradeHistoryClicked(PositionInPeriodDTO clickedPositionDTO)
    {
        // We should not call the super method.
        Bundle args = new Bundle();
        args.putBundle(TradeListInPeriodFragment.BUNDLE_KEY_OWNED_LEADERBOARD_POSITION_ID_BUNDLE, clickedPositionDTO.getLbOwnedPositionId().getArgs());
        getNavigator().pushFragment(TradeListInPeriodFragment.class, args);
    }

    protected class GetLeaderboardPositionsListener extends AbstractGetPositionsListener<LeaderboardMarkUserId, PositionInPeriodDTO, GetLeaderboardPositionsDTO>
    {
        @Override public void onDTOReceived(LeaderboardMarkUserId key, GetLeaderboardPositionsDTO value, boolean fromCache)
        {
            if (key.equals(leaderboardMarkUserId))
            {
                displayProgress(false);
                linkWith(value, true);
            }
        }
    }

    public class LeaderboardPositionListTHIABUserInteractor extends AbstractPositionListTHIABUserInteractor
    {
        public LeaderboardPositionListTHIABUserInteractor()
        {
            super();
        }
    }
}
