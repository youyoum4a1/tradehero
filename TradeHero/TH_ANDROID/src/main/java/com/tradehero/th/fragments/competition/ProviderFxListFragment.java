package com.tradehero.th.fragments.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.fragments.security.ProviderSecurityListRxFragment;
import com.tradehero.th.fragments.security.SecurityPagedViewDTOAdapter;
import com.tradehero.th.fragments.trending.TrendingFXFragment;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.android.observables.AndroidObservable;

public class ProviderFxListFragment extends ProviderSecurityListRxFragment
{
    @Inject SecurityServiceWrapper securityServiceWrapper;

    @Override public void onStart()
    {
        super.onStart();
        fetchFXPrice();
    }

    @NonNull @Override protected SecurityPagedViewDTOAdapter createItemViewAdapter()
    {
        return new SecurityPagedViewDTOAdapter(
                getActivity(),
                R.layout.trending_fx_item);
    }

    private void fetchFXPrice()
    {
        subscriptions.add(AndroidObservable.bindFragment(
                this,
                securityServiceWrapper.getFXSecuritiesAllPriceRx()
                        .repeatWhen(observable -> observable.delay(TrendingFXFragment.MS_DELAY_FOR_QUOTE_FETCH, TimeUnit.MILLISECONDS)))
                .subscribe(
                        this::handlePricesReceived,
                        error -> THToast.show(R.string.error_fetch_fx_list_price)));
    }

    private void handlePricesReceived(@NonNull List<QuoteDTO> list)
    {
        ((SecurityPagedViewDTOAdapter) itemViewAdapter).updatePrices(list);
        ((SecurityPagedViewDTOAdapter) itemViewAdapter).notifyDataSetChanged();
    }
}
