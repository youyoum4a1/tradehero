package com.ayondo.academy.fragments.competition;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import com.ayondo.academy.R;
import com.ayondo.academy.api.quote.QuoteDTO;
import com.ayondo.academy.fragments.security.FXSecurityPagedViewDTOAdapter;
import com.ayondo.academy.fragments.security.ProviderSecurityListRxFragment;
import com.ayondo.academy.fragments.trending.TrendingFXFragment;
import com.ayondo.academy.network.service.SecurityServiceWrapper;
import com.ayondo.academy.rx.ToastOnErrorAction1;
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

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        tradeTitleView.setText(R.string.provider_fx_list_tradable);
    }

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
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
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
                        new ToastOnErrorAction1(getString(R.string.error_fetch_fx_list_price))));
    }

    private void handlePricesReceived(@NonNull List<QuoteDTO> list)
    {
        ((FXSecurityPagedViewDTOAdapter) itemViewAdapter).updatePrices(list);
        ((FXSecurityPagedViewDTOAdapter) itemViewAdapter).notifyDataSetChanged();
    }
}
