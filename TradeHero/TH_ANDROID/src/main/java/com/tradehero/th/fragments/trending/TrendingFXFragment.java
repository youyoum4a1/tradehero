package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingFxSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.fxonboard.FxOnboardDialogFragment;
import com.tradehero.th.fragments.security.SecurityItemView;
import com.tradehero.th.fragments.security.SecurityItemViewAdapterNew;
import com.tradehero.th.fragments.security.SecurityListRxFragment;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.trade.BuySellFXFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.observers.EmptyObserver;

//@Routable("trending-securities")
public class TrendingFXFragment extends SecurityListRxFragment<SecurityItemView>
        implements WithTutorial
{
    private static final int MS_DELAY_FOR_QUOTE_FETCH = 5000;

    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;

    @InjectView(R.id.btn_enroll) View btnEnroll;

    @Nullable private Subscription checkEnrollmentSubscription;
    @Nullable private Subscription waitForEnrolledSubscription;
    @Nullable private Subscription fetchFxPriceSubscription; // For some reason, if we use the SubscriptionList for fetchFxPrice, it unsubscribes when we come back from buy sell
    @Nullable FxOnboardDialogFragment onboardDialogFragment;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_fx_trending, container, false);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(R.string.trending_header);
        inflater.inflate(R.menu.search_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_search:
                pushSearchIn();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStart()
    {
        super.onStart();
        waitForEnrolled();
    }

    @Override public void onStop()
    {
        unsubscribe(checkEnrollmentSubscription);
        unsubscribe(waitForEnrolledSubscription);
        unsubscribe(fetchFxPriceSubscription);
        super.onStop();
    }

    @Override @NonNull protected SecurityItemViewAdapterNew createItemViewAdapter()
    {
        return new SecurityItemViewAdapterNew(getActivity(), R.layout.trending_fx_item);
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @OnClick(R.id.btn_enroll)
    protected void handleBtnEnrollClicked(View view)
    {
        checkFXPortfolio();
    }

    private void waitForEnrolled()
    {
        unsubscribe(waitForEnrolledSubscription);
        waitForEnrolledSubscription = AndroidObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .doOnNext(pair -> btnEnroll.setVisibility(pair.second.fxPortfolio == null ? View.VISIBLE : View.GONE))
                .filter(pair -> pair.second.fxPortfolio != null)
                .subscribe(new EmptyObserver<Pair<UserBaseKey, UserProfileDTO>>()
                {
                    @Override
                    public void onNext(Pair<UserBaseKey, UserProfileDTO> args)
                    {
                        // In effect, we are waiting for the enrolled profile
                        unsubscribe(waitForEnrolledSubscription);
                        btnEnroll.setVisibility(View.GONE);
                        scheduleRequestData();
                        fetchFXPrice();
                    }
                });
    }

    private void checkFXPortfolio()
    {
        unsubscribe(checkEnrollmentSubscription);
        checkEnrollmentSubscription = AndroidObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .take(1)
                .subscribe(new EmptyObserver<Pair<UserBaseKey, UserProfileDTO>>()
                {
                    @Override
                    public void onNext(Pair<UserBaseKey, UserProfileDTO> args)
                    {
                        if (args.second.fxPortfolio == null && onboardDialogFragment == null)
                        {
                            onboardDialogFragment = FxOnboardDialogFragment.showOnBoardDialog(getActivity().getFragmentManager());
                            onboardDialogFragment.getDismissedObservable()
                                    .subscribe(
                                            dialog -> {
                                                onboardDialogFragment = null;
                                            },
                                            error -> THToast.show(new THException(error))
                                    );
                        }
                    }
                });
    }

    private void fetchFXPrice()
    {
        unsubscribe(fetchFxPriceSubscription);
        fetchFxPriceSubscription = AndroidObservable.bindFragment(
                this,
                securityServiceWrapper.getFXSecuritiesAllPriceRx()
                        .repeatWhen(observable -> observable.delay(MS_DELAY_FOR_QUOTE_FETCH, TimeUnit.MILLISECONDS)))
                .subscribe(
                        this::handlePricesReceived,
                        error -> THToast.show(R.string.error_fetch_fx_list_price));
    }

    private void handlePricesReceived(List<QuoteDTO> list)
    {
        ((SecurityItemViewAdapterNew) itemViewAdapter).updatePrices(getActivity(), list);
        itemViewAdapter.notifyDataSetChanged();
    }

    @Override @NonNull protected DTOCacheRx<SecurityListType, SecurityCompactDTOList> getCache()
    {
        return securityCompactListCache;
    }

    @Override public boolean canMakePagedDtoKey()
    {
        return true;
    }

    @NonNull @Override public SecurityListType makePagedDtoKey(int page)
    {
        return new TrendingFxSecurityListType(page);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_trending_screen;
    }

    public void pushSearchIn()
    {
        Bundle args = new Bundle();
        navigator.get().pushFragment(SecuritySearchFragment.class, args);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Object item = parent.getItemAtPosition(position);
        if (item instanceof SecurityCompactDTO)
        {
            handleSecurityItemOnClick((SecurityCompactDTO) item);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled item " + item);
        }
    }

    private void handleSecurityItemOnClick(SecurityCompactDTO securityCompactDTO)
    {
        Bundle args = new Bundle();
        BuySellFXFragment.putSecurityId(args, securityCompactDTO.getSecurityId());

        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();

        if (ownedPortfolioId != null)
        {
            BuySellFXFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        }

        navigator.get().pushFragment(BuySellFXFragment.class, args);
    }

    @Override public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            checkFXPortfolio();
        }
    }
}
