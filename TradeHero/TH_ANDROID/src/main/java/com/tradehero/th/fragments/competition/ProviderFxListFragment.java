package com.tradehero.th.fragments.competition;

import android.support.annotation.NonNull;
import com.tradehero.th.R;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.fragments.security.FXSecurityPagedViewDTOAdapter;
import com.tradehero.th.fragments.security.ProviderSecurityListRxFragment;
import com.tradehero.th.fragments.trending.TrendingFXFragment;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.rx.ToastAction;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class ProviderFxListFragment extends ProviderSecurityListRxFragment
{
    @Inject SecurityServiceWrapper securityServiceWrapper;

    @Override public void onStart()
    {
        super.onStart();
        fetchFXPrice();
    }

    @NonNull @Override protected FXSecurityPagedViewDTOAdapter createItemViewAdapter()
    {
        return new FXSecurityPagedViewDTOAdapter(
                getActivity(),
                R.layout.trending_fx_item);
    }

    private void fetchFXPrice()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                securityServiceWrapper.getFXSecuritiesAllPriceRx()
                        .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>()
                        {
                            @Override public Observable<?> call(Observable<? extends Void> observable)
                            {
                                return observable.delay(TrendingFXFragment.MS_DELAY_FOR_QUOTE_FETCH, TimeUnit.MILLISECONDS);
                            }
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<QuoteDTO>>()
                        {
                            @Override public void call(List<QuoteDTO> quoteDTOs)
                            {
                                ProviderFxListFragment.this.handlePricesReceived(quoteDTOs);
                            }
                        },
                        new ToastAction<Throwable>(getString(R.string.error_fetch_fx_list_price))));
    }

    private void handlePricesReceived(@NonNull List<QuoteDTO> list)
    {
        ((FXSecurityPagedViewDTOAdapter) itemViewAdapter).updatePrices(list);
        ((FXSecurityPagedViewDTOAdapter) itemViewAdapter).notifyDataSetChanged();
    }
}
