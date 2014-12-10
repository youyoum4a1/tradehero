package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.StocksLeaderboardUserDTO;
import com.tradehero.th.fragments.timeline.UserStatisticView;
import javax.inject.Inject;
import timber.log.Timber;

public class LeaderboardMarkUserStockItemView extends BaseLeaderboardMarkUserItemView<StocksLeaderboardUserDTO>
{
    @Inject Context dummyInject;
    @InjectView(R.id.user_statistic_view) @Optional @Nullable UserStatisticView userStatisticView;

    //<editor-fold desc="Constructors">
    public LeaderboardMarkUserStockItemView(Context context)
    {
        super(context);
    }

    public LeaderboardMarkUserStockItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardMarkUserStockItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void onExpand(boolean expand)
    {
        if (userStatisticView != null && leaderboardItem != null)
        {
            if (expand)
            {
                userStatisticView.display(leaderboardItem);
            }
            else
            {
                userStatisticView.display(null);
                Timber.d("clearExpandAnimation");
            }
        }
    }
}
