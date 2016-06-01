package com.ayondo.academy.fragments.onboarding.sector;

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
import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.tradehero.common.utils.THToast;
import com.ayondo.academy.R;
import com.ayondo.academy.adapters.DTOAdapterNew;
import com.ayondo.academy.api.market.SectorDTO;
import com.ayondo.academy.api.market.SectorDTOList;
import com.ayondo.academy.api.market.SectorId;
import com.ayondo.academy.api.market.SectorListType;
import com.ayondo.academy.fragments.base.BaseFragment;
import com.ayondo.academy.fragments.onboarding.OnBoardHeaderLinearView;
import com.ayondo.academy.persistence.market.SectorListCacheRx;
import com.ayondo.academy.rx.TimberAndToastOnErrorAction1;
import java.util.ArrayList;
import java.util.Collection;
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
import rx.subjects.PublishSubject;

public class SectorSelectionScreenFragment extends BaseFragment
{
    private static final String BUNDLE_KEY_INITIAL_SECTORS = SectorSelectionScreenFragment.class.getName() + ".initialSectors";
    private static final String BUNDLE_KEY_HAD_INITIAL_SELECTED_SECTOR =
            SectorSelectionScreenFragment.class.getName() + ".hadInitialSelectedSector";
    private static final int MAX_SELECTABLE_SECTORS = 3;
    private static final int DEFAULT_TOP_N_STOCKS = 6;

    @Inject SectorListCacheRx sectorListCache;

    @Bind(android.R.id.list) ListView sectorList;
    @Bind(android.R.id.button1) View nextButton;
    protected OnBoardHeaderLinearView headerView;
    ArrayAdapter<SelectableSectorDTO> sectorAdapter;
    @NonNull Map<SectorId, SectorDTO> knownSectors;
    boolean hadInitialExchangeSelected;
    @NonNull Set<SectorId> selectedSectors;
    @NonNull BehaviorSubject<SectorDTOList> selectedSectorsSubject;
    @NonNull PublishSubject<Boolean> nextClickedSubject;

    public static void putRequisites(@NonNull Bundle args,
            boolean hadInitialExchangeSelected,
            @Nullable List<SectorId> sectorIds)
    {
        args.putBoolean(BUNDLE_KEY_HAD_INITIAL_SELECTED_SECTOR, hadInitialExchangeSelected);
        if (sectorIds != null)
        {
            int[] ids = new int[sectorIds.size()];
            for (int index = 0; index < sectorIds.size(); index++)
            {
                ids[index] = sectorIds.get(index).key;
            }
            args.putIntArray(BUNDLE_KEY_INITIAL_SECTORS, ids);
        }
    }

    private static boolean getHadInitialSelectedSector(@NonNull Bundle args)
    {
        return args.getBoolean(BUNDLE_KEY_HAD_INITIAL_SELECTED_SECTOR);
    }

    @NonNull private static Set<SectorId> getInitialSectors(@NonNull Bundle args)
    {
        Set<SectorId> sectorIds = new HashSet<>();
        if (args.containsKey(BUNDLE_KEY_INITIAL_SECTORS))
        {
            for(int id : args.getIntArray(BUNDLE_KEY_INITIAL_SECTORS))
            {
                sectorIds.add(new SectorId(id));
            }
        }
        return sectorIds;
    }

    public SectorSelectionScreenFragment()
    {
        knownSectors = new HashMap<>();
        selectedSectors = new HashSet<>();
        selectedSectorsSubject = BehaviorSubject.create();
        nextClickedSubject = PublishSubject.create();
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        sectorAdapter = new DTOAdapterNew<>(activity, R.layout.on_board_sector_item_view);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        hadInitialExchangeSelected = getHadInitialSelectedSector(getArguments());
        selectedSectors = getInitialSectors(getArguments());
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        headerView = (OnBoardHeaderLinearView) LayoutInflater.from(getActivity()).inflate(R.layout.on_board_sector_header, null);
        // We can reuse the same list
        return inflater.inflate(R.layout.on_board_map_exchange, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        sectorList.addHeaderView(headerView, "title", false);
        sectorList.setAdapter(sectorAdapter);
        displayNextButton();
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchSectorInfo();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        sectorAdapter = null;
        super.onDetach();
    }

    protected void fetchSectorInfo()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                sectorListCache.getOne(new SectorListType(DEFAULT_TOP_N_STOCKS)))
                .retryWhen(headerView.isRetryClickedAfterFailed())
                .map(new Func1<Pair<SectorListType, SectorDTOList>, List<SelectableSectorDTO>>()
                {
                    @Override public List<SelectableSectorDTO> call(Pair<SectorListType, SectorDTOList> pair)
                    {
                        return createSelectables(pair.second);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<SelectableSectorDTO>>()
                        {
                            @Override public void call(List<SelectableSectorDTO> onBoardSectors)
                            {
                                headerView.displayRetry(false);
                                sectorAdapter.setNotifyOnChange(false);
                                sectorAdapter.clear();
                                sectorAdapter.addAll(onBoardSectors);
                                sectorAdapter.notifyDataSetChanged();
                                sectorAdapter.setNotifyOnChange(true);
                                displayNextButton();
                                informSelectedSectors();
                            }
                        },
                        new TimberAndToastOnErrorAction1("Failed to load sectors")));
    }

    @NonNull List<SelectableSectorDTO> createSelectables(@NonNull Collection<SectorDTO> sectors)
    {
        List<SelectableSectorDTO> onBoardSectors = new ArrayList<>();
        int count = MAX_SELECTABLE_SECTORS;
        for (SectorDTO sector : sectors)
        {
            knownSectors.put(sector.getSectorId(), sector);
            if (!hadInitialExchangeSelected && count > 0)
            {
                selectedSectors.add(sector.getSectorId());
            }
            onBoardSectors.add(new SelectableSectorDTO(sector, selectedSectors.contains(sector.getSectorId())));
            count--;
        }
        if (sectors.size() > 0)
        {
            hadInitialExchangeSelected = true;
        }
        return onBoardSectors;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(android.R.id.list)
    protected void onSectorClicked(AdapterView<?> parent, View view, int position, long id)
    {
        SelectableSectorDTO dto = (SelectableSectorDTO) parent.getItemAtPosition(position);
        if (!dto.selected && selectedSectors.size() >= MAX_SELECTABLE_SECTORS)
        {
            THToast.show(getString(R.string.on_board_sector_selected_max, MAX_SELECTABLE_SECTORS));
        }
        else
        {
            dto.selected = !dto.selected;
            if (dto.selected)
            {
                selectedSectors.add(dto.value.getSectorId());
            }
            else
            {
                selectedSectors.remove(dto.value.getSectorId());
            }
            ((OnBoardSectorItemView) view).display(dto);

            informSelectedSectors();
        }
        displayNextButton();
    }

    protected void informSelectedSectors()
    {
        SectorDTOList selectedDTOs = new SectorDTOList();
        for (SectorId selected : selectedSectors)
        {
            selectedDTOs.add(knownSectors.get(selected));
        }
        selectedSectorsSubject.onNext(selectedDTOs);
    }

    protected void displayNextButton()
    {
        nextButton.setEnabled(selectedSectors.size() > 0);
    }

    @SuppressWarnings("unused")
    @OnClick(android.R.id.button2)
    protected void onBackClicked(View view)
    {
        nextClickedSubject.onNext(false);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(android.R.id.button1)
    protected void onNextClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        nextClickedSubject.onNext(true);
    }

    @NonNull public Observable<SectorDTOList> getSelectedSectorsObservable()
    {
        return selectedSectorsSubject.asObservable();
    }

    @NonNull public Observable<Boolean> getNextClickedObservable()
    {
        return nextClickedSubject.asObservable();
    }
}
