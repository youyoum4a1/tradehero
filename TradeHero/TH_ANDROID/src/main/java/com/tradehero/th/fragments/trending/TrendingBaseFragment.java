package com.ayondo.academy.fragments.trending;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.fragments.security.SecurityListRxFragment;
import com.ayondo.academy.fragments.security.SecuritySearchFragment;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.BehaviorSubject;

abstract public class TrendingBaseFragment extends SecurityListRxFragment
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;

    @NonNull protected BehaviorSubject<TrendingTabType> trendingTabTypeBehaviorSubject;

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

    @NonNull protected Observable<TrendingTabType> getRequestedTrendingTabTypeObservable()
    {
        return trendingTabTypeBehaviorSubject.asObservable();
    }

    public void pushSearchIn()
    {
        Bundle args = new Bundle();
        populateArgumentForSearch(args);
        navigator.get().pushFragment(SecuritySearchFragment.class, args);
    }

    protected void populateArgumentForSearch(@NonNull Bundle args)
    {
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            SecuritySearchFragment.putApplicablePortfolioId(args, applicablePortfolioId);
        }
    }
}
