package com.tradehero.th.fragments.onboarding.last;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.persistence.security.SecurityCompactListCacheRx;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

public class OnBoardLastFragment extends BaseFragment
{
    @Inject SecurityCompactListCacheRx securityCompactListCache;
    @InjectView(R.id.favorite_gallery) Gallery favoriteGallery;
    @NonNull BehaviorSubject<Pair<SecurityId, Class<? extends DashboardFragment>>> fragmentRequestedBehavior;
    Observable<SecurityCompactDTOList> selectedSecuritiesObservable;

    private OnboardFavoriteAdapter favoriteAdapter;

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
        favoriteAdapter = new OnboardFavoriteAdapter(getActivity());
        favoriteGallery.setAdapter(favoriteAdapter);
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

    @NonNull public Observable<Pair<SecurityId, Class<? extends DashboardFragment>>> getFragmentRequestedObservable()
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
                                favoriteAdapter.appendTail(list);
                                favoriteAdapter.notifyDataSetChanged();
                                if (list.size() > 1) {
                                    favoriteGallery.setSelection(1);
                                }
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to load exchanges")));
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(android.R.id.button1)
    protected void buySharesButtonClicked(View view)
    {
        SecurityCompactDTO dto = favoriteAdapter.getItem(favoriteGallery.getSelectedItemPosition());
        Pair pair = new Pair<SecurityId, Class<? extends DashboardFragment>>(dto.getSecurityId(), BuySellStockFragment.class);
        fragmentRequestedBehavior.onNext(pair);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(android.R.id.button2)
    protected void buySharesLaterButtonClicked(View view)
    {
        Pair pair = new Pair<SecurityId, Class<? extends DashboardFragment>>(null, MeTimelineFragment.class);
        fragmentRequestedBehavior.onNext(pair);
    }
}
