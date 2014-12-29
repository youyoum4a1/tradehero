package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKeyFactory;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCacheRx;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

public abstract class LeaderboardDefFragment extends BaseLeaderboardFragment
{
    @Inject protected LeaderboardDefListKeyFactory leaderboardDefListKeyFactory;
    @Nullable protected Subscription leaderboardDefListCacheFetchSubscription;
    @Inject Lazy<LeaderboardDefListCacheRx> leaderboardDefListCache;

    protected abstract void onLeaderboardDefListLoaded(List<LeaderboardDefDTO> leaderboardDefDTOs);

    @Override public void onResume()
    {
        updateLeaderboardDefListKey(getArguments());
        super.onResume();
    }

    private void updateLeaderboardDefListKey(Bundle bundle)
    {
        unsubscribe(leaderboardDefListCacheFetchSubscription);
        LeaderboardDefListKey key = leaderboardDefListKeyFactory.create(bundle);
        Observable<List<LeaderboardDefDTO>> leaderboardDefObservable = leaderboardDefListCache.get().get(key)
                .map(pair -> pair.second);

        leaderboardDefListCacheFetchSubscription = AndroidObservable.bindFragment(
                this,
                leaderboardDefObservable)
                .subscribe(
                        this::onLeaderboardDefListLoaded,
                        (e) -> THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key)));
    }

    @Override public void onDestroyView()
    {
        unsubscribe(leaderboardDefListCacheFetchSubscription);
        leaderboardDefListCacheFetchSubscription = null;
        super.onDestroyView();
    }
}
