package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.api.live.LivePortfolioDTO;
import com.tradehero.th.api.live.LivePortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.parcelable.LiveTransactionParcelable;
import com.tradehero.th.network.service.DummyAyondoLiveServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import java.util.Objects;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;

public class LiveTransactionFragment extends DashboardFragment
{
    @Bind(R.id.market_price_spinner) Spinner marketPriceSpinner;
    @Bind(R.id.last_update) TextView lastUpdateTextView;
    @Bind(R.id.market_price) TextView marketPriceTextView;
    @Bind(R.id.margin_value) TextView marginValueTextView;
    @Bind(R.id.cash_available) TextView cashAvailableTextView;
    @Bind(R.id.trade_size) EditText tradeSizeEditText;
    @Bind(R.id.trade_value) EditText tradeValueEditText;
    @Bind(R.id.stop_loss) EditText stopTextEditText;
    @Bind(R.id.take_profit) EditText takeProfitEditText;
    @Bind(R.id.leverage) EditText leverageEditText;

    @Inject Lazy<SecurityCompactCacheRx> securityCompactCacheRx;
    @Inject LivePortfolioId livePortfolioId;
    @Inject DummyAyondoLiveServiceWrapper liveServiceWrapper;

    public LiveTransactionFragment()
    {
        // Required empty public constructor
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view =inflater.inflate(R.layout.fragment_live_transaction, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        Bundle data = getActivity().getIntent().getExtras();
        LiveTransactionParcelable parcelable = data.getParcelable("LiveTransactionParcelable");

        if (parcelable != null)
        {
            fetchSecurityData(parcelable);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_transaction_fragment, new String[]{"Market Price", "Test Field", "Another Test"});
        marketPriceSpinner.setAdapter(adapter);

        setUpEventListener();
    }

    @Override public boolean shouldHandleLiveColor()
    {
        return true;
    }

    private void fetchSecurityData(@NonNull final LiveTransactionParcelable parcelable)
    {
        securityCompactCacheRx.get().get(parcelable.getSecurityId())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new PairGetSecond<SecurityId, SecurityCompactDTO>())
                .subscribe(new Action1<SecurityCompactDTO>()
                {
                    @Override public void call(SecurityCompactDTO securityCompactDTO)
                    {
                        if (parcelable.isTransactionBuy())
                        {
                            setActionBarTitle("Buy " + securityCompactDTO.name);
                            marketPriceTextView.setText(String.format("%.2f", securityCompactDTO.askPrice));
                        }
                        else
                        {
                            setActionBarTitle("Sell " + securityCompactDTO.name);
                            marketPriceTextView.setText(String.format("%.2f", securityCompactDTO.bidPrice));
                        }

                        tradeSizeEditText.setText("0");
                        tradeValueEditText.setText("0");
                        stopTextEditText.setText("0");
                        takeProfitEditText.setText("0");
                        leverageEditText.setText("0");
                    }
                });

        liveServiceWrapper.getLivePortfolioDTO(livePortfolioId)
                .subscribe(new Action1<LivePortfolioDTO>()
                {
                    @Override public void call(LivePortfolioDTO livePortfolioDTO)
                    {
                        cashAvailableTextView.setText(String.format("%.2f", livePortfolioDTO.cashBalanceRefCcy));
                    }
                });
    }

    private void setUpEventListener()
    {
        WidgetObservable.text(tradeValueEditText)
                .subscribe(new Action1<OnTextChangeEvent>()
                {
                    @Override public void call(OnTextChangeEvent onTextChangeEvent)
                    {
                        double cashAvailable = Double.parseDouble(cashAvailableTextView.getText().toString());

                        if (!onTextChangeEvent.text().toString().equals(""))
                        {
                            double currentTradeValue = Double.parseDouble(onTextChangeEvent.text().toString());

                            if (currentTradeValue > cashAvailable)
                            {
                                tradeValueEditText.setText(String.format("%.2f", cashAvailable));
                            }
                        }
                        else
                        {
                            tradeValueEditText.setText("0");
                        }
                    }
                });
    }
}
