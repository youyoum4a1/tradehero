package com.tradehero.th.fragments.onboarding.exchange;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTOUtil;
import com.tradehero.th.api.market.ExchangeIntegerId;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.market.MarketRegion;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.onboarding.OnBoardEmptyOrItemAdapter;
import com.tradehero.th.persistence.market.ExchangeCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class ExchangeSelectionScreenFragment extends BaseFragment
{
    private static final String BUNDLE_KEY_INITIAL_REGION = ExchangeSelectionScreenFragment.class.getName() + ".initialRegion";
    private static final String BUNDLE_KEY_HAD_INITIAL_SELECTED_EXCHANGE =
            ExchangeSelectionScreenFragment.class.getName() + ".hadInitialSelectedExchange";
    private static final String BUNDLE_KEY_INITIAL_EXCHANGES = ExchangeSelectionScreenFragment.class.getName() + ".initialExchanges";
    private static final int MAX_SELECTABLE_EXCHANGES = 3;
    private static final int MAX_TOP_STOCKS = 6;
    private static final String MAP_ITEM_DTO = "map";

    @Inject ExchangeCompactListCacheRx exchangeCompactListCache;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;

    MarketRegionSwitcherView mapHeaderSwitcherView;
    @InjectView(android.R.id.list) ListView exchangeList;
    @InjectView(android.R.id.button2) View backButton;
    @InjectView(android.R.id.button1) View nextButton;
    ArrayAdapter<SelectableExchangeDTO> exchangeAdapter;
    @Nullable MarketRegion initialRegion;
    boolean hadInitialExchangeSelected;
    @NonNull Map<MarketRegion, List<ExchangeIntegerId>> filedExchangeIds;
    @NonNull Map<ExchangeIntegerId, ExchangeCompactDTO> knownExchanges;
    @NonNull Set<ExchangeIntegerId> selectedExchanges;
    @NonNull BehaviorSubject<MarketRegion> selectedRegionSubject;
    @NonNull BehaviorSubject<ExchangeCompactDTOList> selectedExchangesSubject;
    @NonNull PublishSubject<Boolean> nextClickedSubject;

    public static void putRequisites(@NonNull Bundle args,
            @Nullable MarketRegion initialRegion,
            boolean hadAutoSelectedExchange,
            @Nullable List<ExchangeIntegerId> initialExchangeIds)
    {
        if (initialRegion != null)
        {
            args.putInt(BUNDLE_KEY_INITIAL_REGION, initialRegion.code);
        }
        args.putBoolean(BUNDLE_KEY_HAD_INITIAL_SELECTED_EXCHANGE, hadAutoSelectedExchange);
        if (initialExchangeIds != null)
        {
            int[] list = new int[initialExchangeIds.size()];
            for (int index = 0; index < initialExchangeIds.size(); index++)
            {
                list[index] = initialExchangeIds.get(index).key;
            }
            args.putIntArray(BUNDLE_KEY_INITIAL_EXCHANGES, list);
        }
    }

    @Nullable private static MarketRegion getInitialRegion(@NonNull Bundle args)
    {
        if (args.containsKey(BUNDLE_KEY_INITIAL_REGION))
        {
            return MarketRegion.create(args.getInt(BUNDLE_KEY_INITIAL_REGION));
        }
        return null;
    }

    private static boolean getHadInitialExchangeSelected(@NonNull Bundle args)
    {
        return args.getBoolean(BUNDLE_KEY_HAD_INITIAL_SELECTED_EXCHANGE);
    }

    @NonNull private static Set<ExchangeIntegerId> getInitialExchanges(@NonNull Bundle args)
    {
        int[] list = args.getIntArray(BUNDLE_KEY_INITIAL_EXCHANGES);
        Set<ExchangeIntegerId> initialExchanges = new HashSet<>();
        if (list != null)
        {
            for (int id : list)
            {
                initialExchanges.add(new ExchangeIntegerId(id));
            }
        }
        return initialExchanges;
    }

    public ExchangeSelectionScreenFragment()
    {
        filedExchangeIds = new HashMap<>();
        knownExchanges = new HashMap<>();
        selectedExchanges = new HashSet<>();
        selectedRegionSubject = BehaviorSubject.create();
        selectedExchangesSubject = BehaviorSubject.create();
        nextClickedSubject = PublishSubject.create();
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        exchangeAdapter = new OnBoardEmptyOrItemAdapter<>(
                activity,
                R.layout.on_board_exchange_item_view,
                R.layout.on_board_empty_exchange);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initialRegion = getInitialRegion(getArguments());
        hadInitialExchangeSelected = getHadInitialExchangeSelected(getArguments());
        selectedExchanges = getInitialExchanges(getArguments());
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        mapHeaderSwitcherView = (MarketRegionSwitcherView) inflater.inflate(R.layout.on_board_market_map_switcher, null);
        return inflater.inflate(R.layout.on_board_map_exchange, container, false);
    }

    @SuppressLint("InflateParams")
    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        exchangeList.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.on_board_started_header, null), "title", false);
        exchangeList.addHeaderView(mapHeaderSwitcherView, MAP_ITEM_DTO, true);
        exchangeList.setAdapter(exchangeAdapter);
        backButton.setVisibility(View.GONE);
        displayNextButton();
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchExchangeInfoAndMapClicks();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        exchangeAdapter = null;
        super.onDetach();
    }

    protected void fetchExchangeInfoAndMapClicks()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                Observable.combineLatest(
                        userProfileCache.getOne(currentUserId.toUserBaseKey()),
                        exchangeCompactListCache.getOne(new ExchangeListType(MAX_TOP_STOCKS)),
                        new Func2<Pair<UserBaseKey, UserProfileDTO>, Pair<ExchangeListType, ExchangeCompactDTOList>, ExchangeCompactDTOList>()
                        {
                            @Override public ExchangeCompactDTOList call(
                                    Pair<UserBaseKey, UserProfileDTO> profilePair,
                                    Pair<ExchangeListType, ExchangeCompactDTOList> exchangesPair)
                            {
                                mapHeaderSwitcherView.setCurrentUserProfile(profilePair.second);
                                return exchangesPair.second;
                            }
                        }))
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<ExchangeCompactDTOList, Observable<MarketRegion>>()
                {
                    @Override public Observable<MarketRegion> call(ExchangeCompactDTOList exchanges)
                    {
                        filedExchangeIds = ExchangeCompactDTOUtil.filePerRegion(exchanges);
                        mapHeaderSwitcherView.enable(filedExchangeIds.keySet());
                        for (ExchangeCompactDTO exchange : exchanges)
                        {
                            knownExchanges.put(exchange.getExchangeIntegerId(), exchange);
                        }
                        return mapHeaderSwitcherView.getMarketRegionClickedObservable()
                                .startWith(initialRegion == null ? Observable.<MarketRegion>empty() : Observable.just(initialRegion));
                    }
                })
                .observeOn(Schedulers.computation())
                .distinctUntilChanged()
                .flatMap(new Func1<MarketRegion, Observable<List<SelectableExchangeDTO>>>()
                {
                    @Override public Observable<List<SelectableExchangeDTO>> call(MarketRegion region)
                    {
                        selectedRegionSubject.onNext(region);
                        List<ExchangeIntegerId> exchanges = filedExchangeIds.get(region);
                        if (exchanges == null)
                        {
                            return Observable.empty();
                        }
                        Set<ExchangeIntegerId> toShow = new LinkedHashSet<>(selectedExchanges);
                        toShow.addAll(exchanges);
                        return Observable.just(createSelectables(toShow));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<SelectableExchangeDTO>>()
                        {
                            @Override public void call(List<SelectableExchangeDTO> dtos)
                            {
                                exchangeAdapter.setNotifyOnChange(false);
                                exchangeAdapter.clear();
                                exchangeAdapter.addAll(dtos);
                                exchangeAdapter.setNotifyOnChange(true);
                                exchangeAdapter.notifyDataSetChanged();
                                displayNextButton();
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to load exchanges or register map clicks")));
    }

    @NonNull List<SelectableExchangeDTO> createSelectables(@NonNull Collection<ExchangeIntegerId> toShow)
    {
        List<SelectableExchangeDTO> dtos = new ArrayList<>();
        int index = 0;
        for (ExchangeIntegerId exchangeId : toShow)
        {
            boolean toSelect = (!hadInitialExchangeSelected && index == 0) ||
                    selectedExchanges.contains(exchangeId);
            if (toSelect)
            {
                selectedExchanges.add(exchangeId);
            }
            dtos.add(new SelectableExchangeDTO(
                    knownExchanges.get(exchangeId),
                    toSelect));
            index++;
        }
        hadInitialExchangeSelected = true;
        return dtos;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(android.R.id.list)
    protected void onExchangeClicked(AdapterView<?> parent, View view, int position, long id)
    {
        Object item = parent.getItemAtPosition(position);
        //noinspection StatementWithEmptyBody
        if (item.equals(MAP_ITEM_DTO))
        {
            // Nothing to do, the map was clicked
        }
        else
        {
            SelectableExchangeDTO dto = (SelectableExchangeDTO) item;
            if (!dto.selected && selectedExchanges.size() >= MAX_SELECTABLE_EXCHANGES)
            {
                THToast.show(getString(R.string.on_board_exchange_selected_max, MAX_SELECTABLE_EXCHANGES));
            }
            else
            {
                dto.selected = !dto.selected;
                if (dto.selected)
                {
                    selectedExchanges.add(dto.value.getExchangeIntegerId());
                }
                else
                {
                    selectedExchanges.remove(dto.value.getExchangeIntegerId());
                }
                ((OnBoardExchangeItemView) view).display(dto);

                ExchangeCompactDTOList selectedDTOs = new ExchangeCompactDTOList();
                for (ExchangeIntegerId selected : selectedExchanges)
                {
                    selectedDTOs.add(knownExchanges.get(selected));
                }
                selectedExchangesSubject.onNext(selectedDTOs);
            }
        }
        displayNextButton();
    }

    protected void displayNextButton()
    {
        boolean hasItems = selectedExchanges.size() > 0;
        if (hasItems)
        {
            nextButton.setVisibility(View.VISIBLE);
        }
        nextButton.setEnabled(hasItems);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(android.R.id.button1)
    protected void onNextClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        nextClickedSubject.onNext(true);
    }

    @NonNull public Observable<MarketRegion> getMarketRegionClickedObservable()
    {
        return selectedRegionSubject.asObservable();
    }

    @NonNull public Observable<ExchangeCompactDTOList> getSelectedExchangesObservable()
    {
        return selectedExchangesSubject.asObservable();
    }

    @NonNull public Observable<Boolean> getNextClickedObservable()
    {
        return nextClickedSubject.asObservable();
    }
}
