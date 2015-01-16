package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.security.SecurityItemView;
import com.tradehero.th.fragments.security.SecurityListRxFragment;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.BehaviorSubject;

abstract public class TrendingBaseFragment extends SecurityListRxFragment<SecurityItemView>
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;

    @NonNull BehaviorSubject<TrendingTabType> trendingTabTypeBehaviorSubject;

    //<editor-fold desc="Constructors">
    protected TrendingBaseFragment()
    {
        super();
        trendingTabTypeBehaviorSubject = BehaviorSubject.create();
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        trendingTabTypeBehaviorSubject.onCompleted();
        super.onDestroy();
    }

    @NonNull Observable<TrendingTabType> getRequestedTrendingTabTypeObservable()
    {
        return trendingTabTypeBehaviorSubject.asObservable();
    }

    public void pushSearchIn()
    {
        Bundle args = new Bundle();
        navigator.get().pushFragment(SecuritySearchFragment.class, args);
    }
}
