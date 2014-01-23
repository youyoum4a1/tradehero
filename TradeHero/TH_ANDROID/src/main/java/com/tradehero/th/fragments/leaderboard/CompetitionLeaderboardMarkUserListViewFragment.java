package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.widget.time.TimeDisplayViewHolder;
import javax.inject.Inject;

/**
 * Created by xavier on 1/23/14.
 */
public class CompetitionLeaderboardMarkUserListViewFragment extends LeaderboardMarkUserListViewFragment
{
    public static final String TAG = CompetitionLeaderboardMarkUserListViewFragment.class.getSimpleName();

    protected CompetitionLeaderboardTimedHeader headerView;
    @Inject LeaderboardDefCache leaderboardDefCache;
    protected LeaderboardDefDTO leaderboardDefDTO;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        int leaderboardId = getArguments().getInt(BUNDLE_KEY_LEADERBOARD_ID);
        this.leaderboardDefDTO = leaderboardDefCache.get(new LeaderboardDefKey(leaderboardId));
        THLog.d(TAG, "leaderboardId " + leaderboardId);
        THLog.d(TAG, "leaderboardDefDTO " + leaderboardDefDTO);
    }

    @Override protected int getHeaderViewResId()
    {
        return R.layout.leaderboard_listview_header_competition;
    }

    @Override protected void initHeaderView(View headerView)
    {
        super.initHeaderView(headerView);
        this.headerView = (CompetitionLeaderboardTimedHeader) headerView;
        this.headerView.setFutureDateToCountDownTo(leaderboardDefDTO.toUtcRestricted);
    }
}
