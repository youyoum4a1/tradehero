package com.tradehero.th.fragments.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;


public class LeaderboardDefListAdapter extends ArrayDTOAdapter<LeaderboardDefKey, LeaderboardDefView>
{
    private LeaderboardSortType sortType;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;

    public LeaderboardDefListAdapter(Context context, LayoutInflater inflater, List<LeaderboardDefKey> items, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
        setItems(items);
        DaggerUtils.inject(this);
    }

    public void setSortType(LeaderboardSortType sortType)
    {
        if (items != null)
        {
            for (LeaderboardDefKey leaderboardDefKey: items)
            {
                leaderboardDefCache.get().get(leaderboardDefKey).put(LeaderboardSortType.TAG, sortType);
            }
        }
        this.sortType = sortType;
    }

    @Override protected void fineTune(int position, LeaderboardDefKey dto, LeaderboardDefView dtoView)
    {

    }
}
