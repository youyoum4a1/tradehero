package com.tradehero.th.fragments.position;

import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.adapters.position.AbstractPositionItemAdapter;
import com.tradehero.th.adapters.position.InPeriodPositionItemAdapter;
import com.tradehero.th.api.leaderboard.position.GetLeaderboardPositionsDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.persistence.leaderboard.position.GetLeaderboardPositionsCache;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 11/6/13 Time: 12:57 PM Copyright (c) TradeHero */
public class InPeriodPositionListFragment extends PositionListFragment
{
    @Inject Lazy<GetLeaderboardPositionsCache> getLeaderboardPositionsCache;

    private DTOCache.Listener<LeaderboardMarkUserId, GetLeaderboardPositionsDTO> getLeaderboardPositionsCacheListener;
    private DTOCache.GetOrFetchTask<GetLeaderboardPositionsDTO> fetchGetPositionsDTOTask;

    private LeaderboardMarkUserId leaderboardMarkUserId;
    private GetLeaderboardPositionsDTO leaderboardPositionsDTO;

    @Override protected void createPositionItemAdapter()
    {
        positionItemAdapter = new InPeriodPositionItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                R.layout.position_item_header,
                R.layout.position_locked_item,
                R.layout.position_open_in_period,
                R.layout.position_closed_in_period,
                R.layout.position_quick_nothing);
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
                fetchGetPositionsDTOTask.forgetListener(true);
            }
            this.leaderboardMarkUserId = new LeaderboardMarkUserId(ownedPortfolioId.userId);
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

    private void linkWith(GetLeaderboardPositionsDTO leaderboardPositionsDTO, boolean andDisplay)
    {

        this.leaderboardPositionsDTO = leaderboardPositionsDTO;
        if (this.leaderboardPositionsDTO != null)
        {
            positionItemAdapter.setPositions(leaderboardPositionsDTO.positions, ownedPortfolioId.getPortfolioId());
            restoreExpandingStates();
        }

        if (andDisplay)
        {
            display();
        }
    }

    @Override public void display()
    {
        super.display();

        // display
    }
}
