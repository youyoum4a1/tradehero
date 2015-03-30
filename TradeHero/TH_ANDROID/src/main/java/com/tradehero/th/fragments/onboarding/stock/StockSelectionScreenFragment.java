package com.tradehero.th.fragments.onboarding.stock;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeCompactSectorListDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.key.ExchangeSectorSecurityListTypeNew;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.onboarding.OnBoardEmptyOrItemAdapter;
import com.tradehero.th.persistence.security.SecurityCompactListCacheRx;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class StockSelectionScreenFragment extends DashboardFragment
{
    private static final int MAX_SELECTABLE_SECURITIES = 10;

    @Inject SecurityCompactListCacheRx securityCompactListCache;

    @InjectView(android.R.id.list) GridView stockList;
    @InjectView(android.R.id.button1) View nextButton;
    @NonNull ArrayAdapter<SelectableSecurityDTO> stockAdapter;
    @NonNull Map<SecurityId, SecurityCompactDTO> knownStocks;
    @NonNull Set<SecurityId> selectedStocks;
    @NonNull BehaviorSubject<SecurityCompactDTOList> selectedStocksSubject;
    Observable<ExchangeCompactSectorListDTO> selectedExchangesSectorsObservable;

    public StockSelectionScreenFragment()
    {
        knownStocks = new HashMap<>();
        selectedStocks = new HashSet<>();
        selectedStocksSubject = BehaviorSubject.create();
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        stockAdapter = new OnBoardEmptyOrItemAdapter<>(
                getActivity(),
                R.layout.on_board_security_item_view,
                R.layout.on_board_empty_security);
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.on_board_security_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        stockList.setAdapter(stockAdapter);
        displayNextButton();
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchStockInfo();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    public void setSelectedExchangesSectorsObservable(@NonNull Observable<ExchangeCompactSectorListDTO> selectedExchangesSectorsObservable)
    {
        this.selectedExchangesSectorsObservable = selectedExchangesSectorsObservable;
    }

    protected void fetchStockInfo()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                selectedExchangesSectorsObservable.flatMap(new Func1<ExchangeCompactSectorListDTO, Observable<SecurityCompactDTOList>>()
                {
                    @Override public Observable<SecurityCompactDTOList> call(ExchangeCompactSectorListDTO exchangeSectorListDTO)
                    {
                        return securityCompactListCache.getOne(new ExchangeSectorSecurityListTypeNew(
                                exchangeSectorListDTO.exchanges.getExchangeIds(),
                                exchangeSectorListDTO.sectors.getSectorIds(),
                                null, null))
                                .map(new PairGetSecond<SecurityListType, SecurityCompactDTOList>());
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<SecurityCompactDTOList>()
                        {
                            @Override public void call(SecurityCompactDTOList stockList)
                            {
                                List<SelectableSecurityDTO> onBoardStocks = new ArrayList<>();
                                for (SecurityCompactDTO security : stockList)
                                {
                                    knownStocks.put(security.getSecurityId(), security);
                                    onBoardStocks.add(new SelectableSecurityDTO(security, false));
                                }
                                stockAdapter.clear();
                                stockAdapter.addAll(onBoardStocks);
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to load securities")));
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(android.R.id.list)
    protected void onSecurityClicked(AdapterView<?> parent, View view, int position, long id)
    {
        nextButton.setVisibility(View.VISIBLE);
        SelectableSecurityDTO dto = (SelectableSecurityDTO) parent.getItemAtPosition(position);
        if (!dto.selected && selectedStocks.size() >= MAX_SELECTABLE_SECURITIES)
        {
            THToast.show(getString(R.string.on_board_stock_selected_max, MAX_SELECTABLE_SECURITIES));
        }
        else
        {
            dto.selected = !dto.selected;
            if (dto.selected)
            {
                selectedStocks.add(dto.value.getSecurityId());
            }
            else
            {
                selectedStocks.remove(dto.value.getSecurityId());
            }
            stockAdapter.notifyDataSetChanged();
        }
        displayNextButton();
    }

    protected void displayNextButton()
    {
        nextButton.setEnabled(selectedStocks.size() > 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(android.R.id.button1)
    protected void onNextClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        SecurityCompactDTOList selectedDTOs = new SecurityCompactDTOList();
        for (SecurityId selected : selectedStocks)
        {
            selectedDTOs.add(knownStocks.get(selected));
        }
        selectedStocksSubject.onNext(selectedDTOs);
    }

    @NonNull public Observable<SecurityCompactDTOList> getSelectedStocksObservable()
    {
        return selectedStocksSubject.asObservable();
    }
}
