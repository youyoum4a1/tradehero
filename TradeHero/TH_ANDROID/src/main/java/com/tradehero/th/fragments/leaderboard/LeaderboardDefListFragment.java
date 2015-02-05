package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedViewDTOAdapterImpl;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKeyFactory;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCacheRx;
import dagger.Lazy;
import javax.inject.Inject;

public class LeaderboardDefListFragment extends BaseLeaderboardPagedListRxFragment<
        LeaderboardDefListKey,
        LeaderboardDefDTO,
        LeaderboardDefDTOList,
        LeaderboardDefDTOList>
{
    @Inject Lazy<LeaderboardDefListCacheRx> leaderboardDefListCache;

    @NonNull LeaderboardDefListKey baseKey;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        baseKey = LeaderboardDefListKeyFactory.create(getArguments());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.leaderboard_def_listview, container, false);
    }

    @Override public void onStart()
    {
        super.onStart();
        requestDtos();
    }

    @NonNull @Override protected LeaderboardDefListCacheRx getCache()
    {
        return leaderboardDefListCache.get();
    }

    @NonNull @Override protected PagedViewDTOAdapterImpl<LeaderboardDefDTO, LeaderboardDefView> createItemViewAdapter()
    {
        return new PagedViewDTOAdapterImpl<>(
                getActivity(),
                R.layout.leaderboard_definition_item_view);
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public LeaderboardDefListKey makePagedDtoKey(int page)
    {
        return LeaderboardDefListKeyFactory.create(baseKey, page);
    }

    @Override protected void handleDtoClicked(LeaderboardDefDTO clicked)
    {
        super.handleDtoClicked(clicked);
        pushLeaderboardListViewFragment(clicked);
    }
}
