package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.SecurityListType;
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
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.internal.util.SubscriptionList;
import rx.observers.EmptyObserver;

//@Routable("trending-securities")
public class TrendingFXFragment extends SecurityListRxFragment<SecurityItemView>
        implements WithTutorial
{
    private static final int MS_DELAY_FOR_QUOTE_FETCH = 5000;

    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject SecurityCompactCacheRx securityCompactCache;

    private SubscriptionList subscriptionList;
    private BaseArrayList<SecurityCompactDTO> mData;
    private boolean fxIsShowed = false;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_fx_trending, container, false);
    }

    private void fetchFXList() {
        subscriptionList.add(AndroidObservable.bindFragment(
                this,
                securityServiceWrapper.getFXSecuritiesRx())
                .subscribe(createFXListFetchObserver()));
    }

    private void fetchFXPrice() {
        subscriptionList.add(AndroidObservable.bindFragment(
                this,
                securityServiceWrapper.getFXSecuritiesAllPriceRx()
                        .repeatWhen(observable -> observable.delay(MS_DELAY_FOR_QUOTE_FETCH, TimeUnit.MILLISECONDS)))
                .subscribe(createFXPriceFetchObserver()));
    }

    private void checkFXPortfolio() {
        if (subscriptionList == null)
        {
            subscriptionList = new SubscriptionList();
        }
        subscriptionList.add(AndroidObservable.bindFragment(
                this,
                userProfileCache.get().get(currentUserId.toUserBaseKey()))
                .subscribe(new EmptyObserver<Pair<UserBaseKey, UserProfileDTO>>() {
                    @Override
                    public void onNext(Pair<UserBaseKey, UserProfileDTO> args) {
                        if (args.second.fxPortfolio == null && !fxIsShowed) {
                            fxIsShowed = true;
                            FxOnboardDialogFragment.showOnBoardDialog(getActivity().getFragmentManager());
                        }
                        else if (args.second.fxPortfolio != null)
                        {
                            fetchFXList();
                            fetchFXPrice();
                        }
                    }
                }));
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

    @Override public void onStop()
    {
        if (subscriptionList != null)
        {
            subscriptionList.unsubscribe();
            subscriptionList = null;
        }
        super.onStop();
    }

    @Override @NonNull protected SecurityItemViewAdapterNew createItemViewAdapter()
    {
        return new SecurityItemViewAdapterNew(getActivity(), R.layout.trending_fx_item);
    }

    @NonNull protected Observer<SecurityCompactDTOList> createFXListFetchObserver()
    {
        return new TrendingFXListFetchObserver();
    }

    protected class TrendingFXListFetchObserver implements Observer<SecurityCompactDTOList>
    {
        @Override public void onNext(SecurityCompactDTOList securityCompactDTOList)
        {
            mData = securityCompactDTOList;
            updateAdapter();
            //TODO may run too many times
            securityCompactCache.onNext(securityCompactDTOList);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_fx_list);
        }
    }

    private void updateAdapter() {
        itemViewAdapter.setNotifyOnChange(false);
        itemViewAdapter.clear();
        itemViewAdapter.addPage(0, mData);
        itemViewAdapter.notifyDataSetChanged();
    }

    @NonNull protected Observer<List<QuoteDTO>> createFXPriceFetchObserver()
    {
        return new TrendingFXPriceFetchObserver();
    }

    protected class TrendingFXPriceFetchObserver implements Observer<List<QuoteDTO>>
    {
        @Override public void onNext(List<QuoteDTO> list)
        {
            if (mData.size() == 0)
            {
                return;
            }
            for (SecurityCompactDTO dto : mData)
            {
                for (QuoteDTO price : list)
                {
                    if (dto.id.equals(price.securityId) && dto instanceof FxSecurityCompactDTO)
                    {
                        ((FxSecurityCompactDTO) dto).setAskPrice(getActivity(), price.ask);
                        ((FxSecurityCompactDTO) dto).setBidPrice(getActivity(), price.bid);
                        break;
                    }
                }
            }
            updateAdapter();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_fx_list_price);
        }
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
        return null;
//        return trendingFilterTypeDTO.getSecurityListType(page, perPage);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_trending_screen;
    }

    @Override protected void startAnew()
    {
        super.startAnew();
    }

    public void pushSearchIn()
    {
        Bundle args = new Bundle();
        navigator.get().pushFragment(SecuritySearchFragment.class, args);
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Object item = parent.getItemAtPosition(position);
        View child = parent.getChildAt(position - parent.getFirstVisiblePosition());
        if (item instanceof SecurityCompactDTO)
        {
            handleSecurityItemOnClick((SecurityCompactDTO) item);
        }
        else if (item instanceof TileType)
        {
//            handleExtraTileItemOnClick((TileType) item, child);
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            fxIsShowed = false;
            checkFXPortfolio();
        }
    }
}
