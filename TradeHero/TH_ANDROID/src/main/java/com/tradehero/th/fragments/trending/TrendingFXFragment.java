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
import butterknife.Bind;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.AssetClass;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingFxSecurityListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.security.FXSecurityPagedViewDTOAdapter;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.trade.AbstractBuySellFragment;
import com.tradehero.th.fragments.trade.FXMainFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ToastAction;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class TrendingFXFragment extends TrendingBaseFragment
        implements WithTutorial
{
    public static final int MS_DELAY_FOR_QUOTE_FETCH = 5000;

    @Inject SecurityServiceWrapper securityServiceWrapper;

    @Bind(R.id.btn_enroll) View btnEnroll;

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

    @Override @NonNull protected FXSecurityPagedViewDTOAdapter createItemViewAdapter()
    {
        return new FXSecurityPagedViewDTOAdapter(getActivity(), R.layout.trending_fx_item);
    }

    private void waitForEnrolled()
    {
        unsubscribe(waitForEnrolledSubscription);
        waitForEnrolledSubscription = AppObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<UserBaseKey, UserProfileDTO>>()
                        {
                            @Override public void call(Pair<UserBaseKey, UserProfileDTO> pair)
                            {
                                mProgress.setVisibility(pair.second.fxPortfolio == null ? View.VISIBLE : View.GONE);
                                btnEnroll.setVisibility(pair.second.fxPortfolio == null ? View.VISIBLE : View.GONE);
                                if (pair.second.fxPortfolio != null)
                                {
                                    // In effect, we are waiting for the enrolled profile
                                    TrendingFXFragment.this.unsubscribe(waitForEnrolledSubscription);
                                    mProgress.setVisibility(View.GONE);
                                    btnEnroll.setVisibility(View.GONE);
                                    TrendingFXFragment.this.scheduleRequestData();
                                    TrendingFXFragment.this.fetchFXPrice();
                                }
                            }
                        },
                        new EmptyAction1<Throwable>());
    }

    private void fetchFXPrice()
    {
        unsubscribe(fetchFxPriceSubscription);
        fetchFxPriceSubscription = AppObservable.bindFragment(
                this,
                securityServiceWrapper.getFXSecuritiesAllPriceRx()
                        .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>()
                        {
                            @Override public Observable<?> call(Observable<? extends Void> observable)
                            {
                                return observable.delay(MS_DELAY_FOR_QUOTE_FETCH, TimeUnit.MILLISECONDS);
                            }
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<QuoteDTO>>()
                        {
                            @Override public void call(List<QuoteDTO> quoteDTOs)
                            {
                                TrendingFXFragment.this.handlePricesReceived(quoteDTOs);
                            }
                        },
                        new ToastAction<Throwable>(getString(R.string.error_fetch_fx_list_price)));
    }

    private void handlePricesReceived(List<QuoteDTO> list)
    {
        ((FXSecurityPagedViewDTOAdapter) itemViewAdapter).updatePrices(list);
        ((FXSecurityPagedViewDTOAdapter) itemViewAdapter).notifyDataSetChanged();
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
        FXMainFragment.putRequisite(
                args,
                new AbstractBuySellFragment.Requisite(
                        securityCompactDTO.getSecurityId(),
                        getApplicablePortfolioId(),
                        0)); // TODO better

        navigator.get().pushFragment(FXMainFragment.class, args);
    }

    @Override protected void populateArgumentForSearch(@NonNull Bundle args)
    {
        super.populateArgumentForSearch(args);
        SecuritySearchFragment.putAssetClass(args, AssetClass.FX);
    }
}
