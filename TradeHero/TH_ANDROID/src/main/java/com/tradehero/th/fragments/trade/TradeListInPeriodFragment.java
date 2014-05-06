package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionCache;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

public class TradeListInPeriodFragment extends AbstractTradeListFragment<PositionInPeriodDTO>
{
    public static final String BUNDLE_KEY_OWNED_LEADERBOARD_POSITION_ID_BUNDLE = AbstractTradeListFragment.class.getName() + ".ownedLeaderboardPositionId";

    protected OwnedLeaderboardPositionId ownedLeaderboardPositionId;
    @Inject protected Lazy<LeaderboardPositionCache> leaderboardPositionCache;

    @Override protected void createAdapter()
    {
        adapter = new TradeListItemInPeriodAdapter(getActivity(), getActivity().getLayoutInflater());
    }

    @Override public void onResume()
    {
        super.onResume();
        Bundle args = getArguments();
        if (args != null)
        {
            Bundle ownedLeaderboardPositionIdBundle = args.getBundle(BUNDLE_KEY_OWNED_LEADERBOARD_POSITION_ID_BUNDLE);
            if (ownedLeaderboardPositionIdBundle != null)
            {
                linkWith(new OwnedLeaderboardPositionId(ownedLeaderboardPositionIdBundle), true);
            }
            else
            {
                Timber.d("ownedLeaderboardPositionIdBundle is null");
            }
        }
        else
        {
            Timber.d("args is null");
        }
    }

    public void linkWith(OwnedLeaderboardPositionId ownedLeaderboardPositionId, boolean andDisplay)
    {
        this.ownedLeaderboardPositionId = ownedLeaderboardPositionId;
        linkWith(leaderboardPositionCache.get().get(ownedLeaderboardPositionId), andDisplay);
        fetchTrades();

        if (andDisplay)
        {
            display();
        }
    }
}
