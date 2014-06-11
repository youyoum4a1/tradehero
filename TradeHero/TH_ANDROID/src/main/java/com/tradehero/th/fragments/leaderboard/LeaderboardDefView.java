package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import dagger.Lazy;
import javax.inject.Inject;

public class LeaderboardDefView extends AbstractLeaderboardDefView
        implements DTOView<LeaderboardDefKey>
{
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;

    private LeaderboardDefKey leaderboardDefKey;

    //<editor-fold desc="Constructors">
    public LeaderboardDefView(Context context)
    {
        super(context);
    }

    public LeaderboardDefView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeaderboardDefView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(LeaderboardDefKey dto)
    {
        this.leaderboardDefKey = dto;
        if (leaderboardDefKey != null)
        {
            linkWith(leaderboardDefCache.get().get(leaderboardDefKey), true);
        }
    }
}
