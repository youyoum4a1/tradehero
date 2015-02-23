package com.tradehero.th.fragments.onboarding.exchange;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTOUtil;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.api.market.MarketRegion;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.market.ExchangeSectorCompactKey;
import com.tradehero.th.persistence.market.ExchangeSectorCompactListCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class ExchangeSelectionScreenFragment extends DashboardFragment
{
    private static final int MAX_SELECTABLE_EXCHANGES = 3;
    private static final int MAX_TOP_STOCKS = 6;

    @Inject ExchangeSectorCompactListCacheRx exchangeSectorCompactListCache;

    MarketRegionMapView mapHeaderView;
    @InjectView(android.R.id.list) ListView exchangeList;
    @InjectView(android.R.id.button1) View nextButton;
    @NonNull OnBoardExchangeAdapter exchangeAdapter;
    @NonNull Map<MarketRegion, List<ExchangeIntegerId>> filedExchangeIds;
    @NonNull Map<ExchangeIntegerId, ExchangeDTO> knownExchanges;
    @NonNull Set<ExchangeIntegerId> selectedExchanges;
    @NonNull BehaviorSubject<ExchangeCompactDTOList> selectedExchangesSubject;

    public ExchangeSelectionScreenFragment()
    {
        filedExchangeIds = new HashMap<>();
        knownExchanges = new HashMap<>();
        selectedExchanges = new HashSet<>();
        selectedExchangesSubject = BehaviorSubject.create();
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        exchangeAdapter = new OnBoardExchangeAdapter(
                getActivity(),
                R.layout.on_board_exchange_item_view,
                R.layout.on_board_empty_exchange);
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mapHeaderView = (MarketRegionMapView) inflater.inflate(R.layout.on_board_market_map_view, null);
        return inflater.inflate(R.layout.on_board_map_exchange, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        exchangeList.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.on_board_started_header, null), "title", false);
        exchangeList.addHeaderView(mapHeaderView, "map", false);
        exchangeList.setAdapter(exchangeAdapter);
        displayNextButton();
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchExchangeInfo();
        registerMapClicks();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected void fetchExchangeInfo()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                exchangeSectorCompactListCache.getOne(new ExchangeSectorCompactKey()))
                .subscribe(
                        new Action1<Pair<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO>>()
                        {
                            @Override public void call(Pair<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO> pair)
                            {
                                filedExchangeIds = ExchangeCompactDTOUtil.filePerRegion(pair.second.exchanges);
                                for (ExchangeDTO exchange : pair.second.exchanges)
                                {
                                    knownExchanges.put(exchange.getExchangeIntegerId(), exchange);
                                }
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to load exchanges")));
    }

    protected void registerMapClicks()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                mapHeaderView.getMarketRegionClickedObservable())
                .subscribe(
                        new Action1<MarketRegion>()
                        {
                            @Override public void call(MarketRegion region)
                            {
                                showRegion(region);
                            }
                        },
                        new ToastOnErrorAction()));
    }

    protected void showRegion(@NonNull MarketRegion region)
    {
        List<ExchangeIntegerId> exchanges = filedExchangeIds.get(region);
        if (exchanges != null)
        {
            exchangeAdapter.clear();
            Set<ExchangeIntegerId> toShow = new LinkedHashSet<>(selectedExchanges);
            toShow.addAll(exchanges);
            onStopSubscriptions.add(AppObservable.bindFragment(
                    this,
                    Observable.from(toShow).map(new Func1<ExchangeIntegerId, ExchangeIntegerId>()
                            {
                                @Override public ExchangeIntegerId call(final ExchangeIntegerId integerId)
                                {
                                    final ExchangeDTO exchange = knownExchanges.get(integerId);
                                    final OnBoardExchangeDTO dto = new OnBoardExchangeDTO(selectedExchanges.contains(integerId), exchange);
                                    exchangeAdapter.add(dto);
                                    return integerId;
                                }
                            }))
                    .subscribe(
                            new Action1<ExchangeIntegerId>()
                            {
                                @Override public void call(ExchangeIntegerId ignored)
                                {
                                    exchangeAdapter.notifyDataSetChanged();
                                }
                            },
                            new TimberOnErrorAction("Failed to fetch region stocks")));
        }
        else
        {
            Timber.e(new Exception(""), "No exchange found for MarketRegion.%s", region);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(android.R.id.list)
    protected void onExchangeClicked(AdapterView<?> parent, View view, int position, long id)
    {
        OnBoardExchangeDTO dto = (OnBoardExchangeDTO) parent.getItemAtPosition(position);
        if (!dto.selected && selectedExchanges.size() >= MAX_SELECTABLE_EXCHANGES)
        {
            THToast.show(getString(R.string.exchange_selected_max, MAX_SELECTABLE_EXCHANGES));
        }
        else
        {
            dto.selected = !dto.selected;
            if (dto.selected)
            {
                selectedExchanges.add(dto.exchange.getExchangeIntegerId());
            }
            else
            {
                selectedExchanges.remove(dto.exchange.getExchangeIntegerId());
            }
            exchangeAdapter.notifyDataSetChanged();
        }
        displayNextButton();
    }

    protected void displayNextButton()
    {
        nextButton.setEnabled(selectedExchanges.size() > 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(android.R.id.button1)
    protected void onNextClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        ExchangeCompactDTOList selectedDTOs = new ExchangeCompactDTOList();
        for (ExchangeIntegerId selected : selectedExchanges)
        {
            selectedDTOs.add(knownExchanges.get(selected));
        }
        selectedExchangesSubject.onNext(selectedDTOs);
    }

    @NonNull public Observable<ExchangeCompactDTOList> getSelectedExchangesObservable()
    {
        return selectedExchangesSubject.asObservable();
    }
}
