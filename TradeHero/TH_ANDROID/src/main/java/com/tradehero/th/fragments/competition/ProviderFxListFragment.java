package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.fragments.trade.BuySellFXFragment;
import com.tradehero.th.fragments.trending.TrendingFXFragment;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

public class ProviderFxListFragment extends ProviderSecurityListFragment
{
    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Nullable private Subscription fetchFxPriceSubscription;

    @Override
    public void onStart()
    {
        super.onStart();
        fetchFXPrice();
    }

    @Override
    public void onStop()
    {
        unsubscribe(fetchFxPriceSubscription);
        super.onStop();
    }

    @Override protected ListAdapter createSecurityItemViewAdapter()
    {
        return new SimpleSecurityItemViewAdapter(
                getActivity(),
                R.layout.trending_fx_item);
    }

    @Override protected AdapterView.OnItemClickListener createOnItemClickListener()
    {
        return new OnFxViewClickListener();
    }

    private class OnFxViewClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) parent.getItemAtPosition(position);

            Bundle args = new Bundle();
            BuySellFXFragment.putSecurityId(args, securityCompactDTO.getSecurityId());

            OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();

            if (ownedPortfolioId != null)
            {
                BuySellFXFragment.putApplicablePortfolioId(args, ownedPortfolioId);
            }

            navigator.get().pushFragment(BuySellFXFragment.class, args);
        }
    }

    private void fetchFXPrice()
    {
        unsubscribe(fetchFxPriceSubscription);
        fetchFxPriceSubscription = AndroidObservable.bindFragment(
                this,
                securityServiceWrapper.getFXSecuritiesAllPriceRx()
                        .repeatWhen(observable -> observable.delay(TrendingFXFragment.MS_DELAY_FOR_QUOTE_FETCH, TimeUnit.MILLISECONDS)))
                .subscribe(
                        this::handlePricesReceived,
                        error -> THToast.show(R.string.error_fetch_fx_list_price));
    }

    private void handlePricesReceived(@NonNull List<QuoteDTO> list)
    {
        ((SimpleSecurityItemViewAdapter) securityItemViewAdapter).updatePrices(getActivity(), list);
        securityItemViewAdapter.notifyDataSetChanged();
    }
}
