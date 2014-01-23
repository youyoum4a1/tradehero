package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.View;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionId;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.persistence.competition.CompetitionCache;
import com.tradehero.th.persistence.competition.ProviderCache;
import javax.inject.Inject;

/**
 * Created by xavier on 1/23/14.
 */
public class CompetitionLeaderboardMarkUserListViewFragment extends LeaderboardMarkUserListViewFragment
{
    public static final String TAG = CompetitionLeaderboardMarkUserListViewFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_PROVIDER_ID = CompetitionLeaderboardMarkUserListViewFragment.class.getName() + ".providerId";
    public static final String BUNDLE_KEY_COMPETITION_ID = CompetitionLeaderboardMarkUserListViewFragment.class.getName() + ".competitionId";

    protected CompetitionLeaderboardTimedHeader headerView;
    @Inject ProviderCache providerCache;
    protected ProviderDTO providerDTO;
    @Inject CompetitionCache competitionCache;
    protected CompetitionDTO competitionDTO;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ProviderId providerId = new ProviderId(getArguments().getBundle(BUNDLE_KEY_PROVIDER_ID));
        providerDTO = providerCache.get(providerId);
        THLog.d(TAG, "providerDTO " + providerDTO);

        CompetitionId competitionId = new CompetitionId(getArguments().getBundle(BUNDLE_KEY_COMPETITION_ID));
        competitionDTO = competitionCache.get(competitionId);
        THLog.d(TAG, "competitionDTO " + competitionDTO);
    }

    @Override protected int getHeaderViewResId()
    {
        return R.layout.leaderboard_listview_header_competition;
    }

    @Override protected void initHeaderView(View headerView)
    {
        super.initHeaderView(headerView);
        this.headerView = (CompetitionLeaderboardTimedHeader) headerView;
        this.headerView.linkWith(providerDTO, true);
        this.headerView.setCompetitionDTO(competitionDTO);
    }

    @Override public void onDestroyView()
    {
        this.headerView = null;
        super.onDestroyView();
    }
}
