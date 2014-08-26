package com.tradehero.th.fragments.onboarding.hero;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListType;
import com.tradehero.th.api.users.SuggestHeroesListType;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.persistence.leaderboard.LeaderboardUserListCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OnBoardPickHeroFragment extends BaseFragment
{
    @Inject LeaderboardUserListCache leaderboardUserListCache;
    @NotNull OnBoardPickHeroViewHolder viewHolder;
    @Nullable ExchangeSectorSecurityListType exchangeSectorSecurityListType;
    @Nullable DTOCacheNew.Listener<SuggestHeroesListType, LeaderboardUserDTOList> leaderboardUserListCacheListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        viewHolder = new OnBoardPickHeroViewHolder(getActivity());
        leaderboardUserListCacheListener = createLeaderboardCacheListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.onboard_select_hero, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewHolder.attachView(view);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchSuggestedUsers();
    }

    @Override public void onStop()
    {
        detachLeaderboardUserListCache();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        viewHolder.detachView();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        leaderboardUserListCacheListener = null;
        super.onDestroy();
    }

    public void setExchangeSectorSecurityListType(
            @Nullable ExchangeSectorSecurityListType exchangeSectorSecurityListType)
    {
        this.exchangeSectorSecurityListType = exchangeSectorSecurityListType;
        fetchSuggestedUsers();
    }

    protected DTOCacheNew.Listener<SuggestHeroesListType, LeaderboardUserDTOList> createLeaderboardCacheListener()
    {
        return new OnboardPickHeroLeaderboardCacheListener();
    }

    protected class OnboardPickHeroLeaderboardCacheListener implements DTOCacheNew.Listener<SuggestHeroesListType, LeaderboardUserDTOList>
    {
        @Override public void onDTOReceived(@NotNull SuggestHeroesListType key, @NotNull LeaderboardUserDTOList value)
        {
            viewHolder.setUsers(value);
        }

        @Override public void onErrorThrown(@NotNull SuggestHeroesListType key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_leaderboard_info);
        }
    }

    protected void fetchSuggestedUsers()
    {
        if (exchangeSectorSecurityListType != null)
        {
            SuggestHeroesListType key = new SuggestHeroesListType(
                    exchangeSectorSecurityListType.exchangeId,
                    exchangeSectorSecurityListType.sectorId,
                    1, null);
            detachLeaderboardUserListCache();
            leaderboardUserListCache.register(
                    key,
                    leaderboardUserListCacheListener);
            leaderboardUserListCache.getOrFetchAsync(key);
        }
    }

    protected void detachLeaderboardUserListCache()
    {
        leaderboardUserListCache.unregister(leaderboardUserListCacheListener);
    }

    public LeaderboardUserDTOList getSelectedHeroes()
    {
        return viewHolder.getSelectedHeroes();
    }
}
