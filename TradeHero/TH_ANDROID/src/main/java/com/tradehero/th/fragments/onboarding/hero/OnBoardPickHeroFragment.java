package com.tradehero.th.fragments.onboarding.hero;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PerPagedLeaderboardKey;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.social.friend.BatchFollowFormDTO;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OnBoardPickHeroFragment extends BaseFragment
{
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject LeaderboardCache leaderboardCache;
    @NotNull OnBoardPickHeroViewHolder viewHolder;
    @Nullable DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> leaderboardCacheListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        viewHolder = new OnBoardPickHeroViewHolder(getActivity());
        leaderboardCacheListener = createLeaderboardCacheListener();
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
        fetchSelectUsers();
    }

    @Override public void onStop()
    {
        detachLeaderboardCache();
        doFollow();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        viewHolder.detachView();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        leaderboardCacheListener = null;
        super.onDestroy();
    }

    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> createLeaderboardCacheListener()
    {
        return new OnboardPickHeroLeaderboardCacheListener();
    }

    protected class OnboardPickHeroLeaderboardCacheListener implements DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO>
    {
        @Override public void onDTOReceived(@NotNull LeaderboardKey key, @NotNull LeaderboardDTO value)
        {
            viewHolder.setUsers(value.users);
        }

        @Override public void onErrorThrown(@NotNull LeaderboardKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_leaderboard_info);
        }
    }

    protected void fetchSelectUsers()
    {
        // TODO change to proper API when ready
        detachLeaderboardCache();
        LeaderboardKey mostSkilledKey = new PerPagedLeaderboardKey(LeaderboardDefKeyKnowledge.MOST_SKILLED_ID, null, 10);
        leaderboardCache.register(
                mostSkilledKey,
                leaderboardCacheListener);
        leaderboardCache.getOrFetchAsync(mostSkilledKey);
    }

    protected void detachLeaderboardCache()
    {
        leaderboardCache.unregister(leaderboardCacheListener);
    }

    public void doFollow()
    {
        List<LeaderboardUserDTO> selected = viewHolder.getSelectedHeroes();
        if (selected.size() > 0)
        {
            userServiceWrapper.followBatchFree(
                    new BatchFollowFormDTO(
                            selected,
                            new UserBaseDTO()),
                    null);
        }
    }
}
