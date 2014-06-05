package com.tradehero.th.fragments.position;

import android.os.Bundle;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.position.LeaderboardMarkUserId;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.GetPositionsDTOKey;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trade.TradeListInPeriodFragment;
import com.tradehero.th.persistence.position.GetPositionsCache;
import javax.inject.Inject;
import timber.log.Timber;

public class LeaderboardPositionListFragment
        extends AbstractPositionListFragment
{
    @Inject GetPositionsCache getPositionsCache;

    private DTOCache.Listener<GetPositionsDTOKey, GetPositionsDTO>
            getLeaderboardPositionsCacheListener;
    private DTOCache.GetOrFetchTask<GetPositionsDTOKey, GetPositionsDTO> fetchGetPositionsDTOTask;

    private boolean timeRestricted;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getLeaderboardPositionsCacheListener = createGetLeaderboardPositionsCacheListener();
    }

    @Override protected void createPositionItemAdapter()
    {
        timeRestricted =
                getArguments().getBoolean(LeaderboardDefDTO.LEADERBOARD_DEF_TIME_RESTRICTED, false);
        if (positionItemAdapter != null)
        {
            positionItemAdapter.setCellListener(null);
        }
        positionItemAdapter = new LeaderboardPositionItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                getLayoutResIds(),
                timeRestricted);
        positionItemAdapter.setCellListener(this);
    }

    @Override
    protected DTOCache.Listener<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsCacheListener()
    {
        return new GetPositionsListener();
    }

    @Override
    protected DTOCache.GetOrFetchTask<GetPositionsDTOKey, GetPositionsDTO> createGetPositionsCacheFetchTask(
            boolean force)
    {
        return getPositionsCache.getOrFetch((LeaderboardMarkUserId) getPositionsDTOKey, force,
                getLeaderboardPositionsCacheListener);
    }

    protected DTOCache.GetOrFetchTask<GetPositionsDTOKey, GetPositionsDTO> createRefreshPositionsCacheFetchTask()
    {
        return getPositionsCache.getOrFetch((LeaderboardMarkUserId) getPositionsDTOKey, true,
                createRefreshLeaderboardPositionsCacheListener());
    }

    @Override public void onResume()
    {
        String periodStart =
                getArguments().getString(LeaderboardUserDTO.LEADERBOARD_PERIOD_START_STRING);
        Timber.d("Period Start: %s" + periodStart);

        super.onResume();
    }

    @Override public void onStop()
    {
        detachGetLeaderboardPositions();
        super.onStop();
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
        Timber.d("fetchSimplePage");
        fetchSimplePage(false);
    }

    @Override protected void refreshSimplePage()
    {
        detachGetLeaderboardPositions();
        DTOCache.GetOrFetchTask<GetPositionsDTOKey, GetPositionsDTO> fetchGetPositionsDTOTask =
                createRefreshPositionsCacheFetchTask();
        fetchGetPositionsDTOTask.execute();
    }

    @Override protected void fetchSimplePage(boolean force)
    {
        if (getPositionsDTOKey != null && getPositionsDTOKey.isValid())
        {
            detachGetLeaderboardPositions();
            fetchGetPositionsDTOTask = createGetPositionsCacheFetchTask(force);
            fetchGetPositionsDTOTask.execute();
        }
    }

    private DTOCache.Listener<GetPositionsDTOKey, GetPositionsDTO> createGetLeaderboardPositionsCacheListener()
    {
        return new DTOCache.Listener<GetPositionsDTOKey, GetPositionsDTO>()
        {
            @Override public void onDTOReceived(GetPositionsDTOKey key, GetPositionsDTO value,
                    boolean fromCache)
            {
                if (key.equals(getPositionsDTOKey))
                {
                    Timber.d("GetLeaderboardPositionsCacheListener onDTOReceived %s",
                            getPositionsDTOKey);
                    linkWith(value, true);
                    showResultIfNecessary();
                }
                else
                {
                    showErrorView();
                    Timber.e("leaderboardMarkUserId(%s) doesn't match result(%s)",
                            getPositionsDTOKey, key);
                }
            }

            @Override public void onErrorThrown(GetPositionsDTOKey key, Throwable error)
            {
                //To change body of implemented methods use File | Settings | File Templates.
                showErrorView();
                Timber.e(error, "Failed to get positions");
            }
        };
    }

    private DTOCache.Listener<GetPositionsDTOKey, GetPositionsDTO> createRefreshLeaderboardPositionsCacheListener()
    {
        return new DTOCache.Listener<GetPositionsDTOKey, GetPositionsDTO>()
        {
            @Override public void onDTOReceived(GetPositionsDTOKey key, GetPositionsDTO value,
                    boolean fromCache)
            {
                if (!fromCache)
                {
                    linkWith(value, true);
                    showResultIfNecessary();
                }
            }

            @Override public void onErrorThrown(GetPositionsDTOKey key, Throwable error)
            {
                //To change body of implemented methods use File | Settings | File Templates.
                boolean loaded = checkLoadingSuccess();
                if (!loaded)
                {
                    showErrorView();
                }
            }
        };
    }

    @Override public void onTradeHistoryClicked(PositionDTO clickedPositionDTO)
    {
        // We should not call the super method.
        Bundle args = new Bundle();
        if (clickedPositionDTO instanceof PositionInPeriodDTO)
        {
            args.putBundle(
                    TradeListInPeriodFragment.BUNDLE_KEY_OWNED_LEADERBOARD_POSITION_ID_BUNDLE,
                    clickedPositionDTO.getPositionDTOKey().getArgs());
            getDashboardNavigator().pushFragment(TradeListInPeriodFragment.class, args);
        }
        else
        {
            args.putBundle(TradeListFragment.BUNDLE_KEY_OWNED_POSITION_ID_BUNDLE,
                    clickedPositionDTO.getPositionDTOKey().getArgs());
            getDashboardNavigator().pushFragment(TradeListFragment.class, args);
        }
    }

    protected class GetPositionsListener
            extends AbstractGetPositionsListener<GetPositionsDTOKey, GetPositionsDTO>
    {
        @Override public void onDTOReceived(GetPositionsDTOKey key, GetPositionsDTO value,
                boolean fromCache)
        {
            if (key.equals(getPositionsDTOKey))
            {
                Timber.d("GetLeaderboardPositionsListener onDTOReceived");
                //displayProgress(false);
                linkWith(value, true);
                showResultIfNecessary();
            }
            else
            {
                showErrorView();
                Timber.e("leaderboardMarkUserId(%s) doesn't match result(%s)", getPositionsDTOKey,
                        key);
            }
            //TODO if condition false, how to do?
        }
    }
}
