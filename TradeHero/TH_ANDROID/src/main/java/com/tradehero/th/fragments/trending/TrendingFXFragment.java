package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingFxSecurityListType;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.security.SecurityPagedViewDTOAdapter;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.trade.BuySellFXFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.rx.ToastAction;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;
import timber.log.Timber;

//@Routable("trending-securities")
public class TrendingFXFragment extends TrendingBaseFragment
        implements WithTutorial
{
    public static final int MS_DELAY_FOR_QUOTE_FETCH = 5000;

    @Inject SecurityServiceWrapper securityServiceWrapper;

    @InjectView(R.id.btn_enroll) View btnEnroll;

    @Nullable private Subscription checkEnrollmentSubscription;
    @Nullable private Subscription waitForEnrolledSubscription;
    @Nullable private Subscription fetchFxPriceSubscription;
    // For some reason, if we use the SubscriptionList for fetchFxPrice, it unsubscribes when we come back from buy sell
    @Nullable FxOnBoardDialogFragment onBoardDialogFragment;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_fx_trending, container, false);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //inflater.inflate(R.menu.search_menu, menu); // Put back when Fx is searchable

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

    @Nullable @Override protected PortfolioCompactDTO getPreferredApplicablePortfolio(@NonNull PortfolioCompactDTOList portfolioCompactDTOs)
    {
        return portfolioCompactDTOs.getDefaultFxPortfolio();
    }

    @Override @NonNull protected SecurityPagedViewDTOAdapter createItemViewAdapter()
    {
        return new SecurityPagedViewDTOAdapter(getActivity(), R.layout.trending_fx_item);
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
        waitForEnrolledSubscription = AppObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .doOnNext(pair -> mProgress.setVisibility(pair.second.fxPortfolio == null ? View.VISIBLE : View.GONE))
                .doOnNext(pair -> btnEnroll.setVisibility(pair.second.fxPortfolio == null ? View.VISIBLE : View.GONE))
                .filter(pair -> pair.second.fxPortfolio != null)
                .subscribe(
                        pair -> {
                            // In effect, we are waiting for the enrolled profile
                            unsubscribe(waitForEnrolledSubscription);
                            mProgress.setVisibility(View.GONE);
                            btnEnroll.setVisibility(View.GONE);
                            scheduleRequestData();
                            fetchFXPrice();
                        },
                        e -> {});
    }

    private void checkFXPortfolio()
    {
        unsubscribe(checkEnrollmentSubscription);
        checkEnrollmentSubscription = AppObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey())
                        .take(1)
                        .map(new PairGetSecond<>()))
                .subscribe(
                        this::handleUserProfileForOnBoardReceived,
                        error -> Timber.e(error, ""));
    }

    protected void handleUserProfileForOnBoardReceived(@NonNull UserProfileDTO userProfileDTO)
    {
        if (userProfileDTO.fxPortfolio == null && onBoardDialogFragment == null)
        {
            onBoardDialogFragment = FxOnBoardDialogFragment.showOnBoardDialog(getActivity().getFragmentManager());
            onBoardDialogFragment.getDismissedObservable()
                    .subscribe(
                            dialog -> onBoardDialogFragment = null,
                            error -> THToast.show(new THException(error))
                    );
            onBoardDialogFragment.getUserActionTypeObservable()
                    .subscribe(
                            action -> {
                                if (action.equals(FxOnBoardDialogFragment.UserActionType.CANCELLED))
                                {
                                    trendingTabTypeBehaviorSubject.onNext(TrendingTabType.STOCK);
                                }
                            },
                            error -> Timber.e(error, "")
                    );
        }
    }

    private void fetchFXPrice()
    {
        unsubscribe(fetchFxPriceSubscription);
        fetchFxPriceSubscription = AppObservable.bindFragment(
                this,
                securityServiceWrapper.getFXSecuritiesAllPriceRx()
                        .repeatWhen(observable -> observable.delay(MS_DELAY_FOR_QUOTE_FETCH, TimeUnit.MILLISECONDS)))
                .subscribe(
                        this::handlePricesReceived,
                        new ToastAction<>(getString(R.string.error_fetch_fx_list_price)));
    }

    private void handlePricesReceived(List<QuoteDTO> list)
    {
        ((SecurityPagedViewDTOAdapter) itemViewAdapter).updatePrices(list);
        ((SecurityPagedViewDTOAdapter) itemViewAdapter).notifyDataSetChanged();
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

    private void handleSecurityItemOnClick(@NonNull SecurityCompactDTO securityCompactDTO)
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

    @Override protected void populateArgumentForSearch(@NonNull Bundle args)
    {
        super.populateArgumentForSearch(args);
        SecuritySearchFragment.putAssetClass(args, AssetClass.FX);
    }
}
