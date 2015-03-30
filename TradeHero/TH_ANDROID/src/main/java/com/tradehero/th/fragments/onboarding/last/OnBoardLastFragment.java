package com.tradehero.th.fragments.onboarding.last;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectViews;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.security.SecurityItemView;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trending.TrendingMainFragment;
import com.tradehero.th.persistence.security.SecurityCompactListCacheRx;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

public class OnBoardLastFragment extends DashboardFragment
{
    @Inject SecurityCompactListCacheRx securityCompactListCache;

    @InjectViews({R.id.stock_1, R.id.stock_2, R.id.stock_3}) SecurityItemView[] stocks;
    @NonNull BehaviorSubject<Class<? extends DashboardFragment>> fragmentRequestedBehavior;
    Observable<SecurityCompactDTOList> selectedSecuritiesObservable;

    public OnBoardLastFragment()
    {
        fragmentRequestedBehavior = BehaviorSubject.create();
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.on_board_last_fragment, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchSecuritiesInfo();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @NonNull public Observable<Class<? extends DashboardFragment>> getFragmentRequestedObservable()
    {
        return fragmentRequestedBehavior.asObservable();
    }

    public void setSelectedSecuritiessObservable(@NonNull Observable<SecurityCompactDTOList> selectedSecuritiesObservable)
    {
        this.selectedSecuritiesObservable = selectedSecuritiesObservable;
    }

    public void fetchSecuritiesInfo()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                selectedSecuritiesObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<SecurityCompactDTOList>()
                        {
                            @Override public void call(SecurityCompactDTOList list)
                            {
                                for (int index = 0; index < Math.min(list.size(), stocks.length); index++)
                                {
                                    stocks[index].display(list.get(index));
                                    stocks[index].setVisibility(View.VISIBLE);
                                }
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to load exchanges")));
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(android.R.id.button1)
    protected void buySharesButtonClicked(View view)
    {
        fragmentRequestedBehavior.onNext(TrendingMainFragment.class);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(android.R.id.button2)
    protected void buySharesLaterButtonClicked(View view)
    {
        fragmentRequestedBehavior.onNext(MeTimelineFragment.class);
    }
}
