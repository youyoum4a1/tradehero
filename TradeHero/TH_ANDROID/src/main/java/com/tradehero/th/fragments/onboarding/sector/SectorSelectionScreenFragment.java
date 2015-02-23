package com.tradehero.th.fragments.onboarding.sector;

import android.annotation.SuppressLint;
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
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.api.market.SectorDTO;
import com.tradehero.th.api.market.SectorDTOList;
import com.tradehero.th.api.market.SectorId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.onboarding.OnBoardEmptyOrItemAdapter;
import com.tradehero.th.models.market.ExchangeSectorCompactKey;
import com.tradehero.th.persistence.market.ExchangeSectorCompactListCacheRx;
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
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;

public class SectorSelectionScreenFragment extends DashboardFragment
{
    private static final int MAX_SELECTABLE_SECTORS = 3;
    private static final int MAX_TOP_STOCKS = 6;

    @Inject ExchangeSectorCompactListCacheRx exchangeSectorCompactListCache;

    @InjectView(android.R.id.list) ListView sectorList;
    @InjectView(android.R.id.button1) View nextButton;
    @NonNull ArrayAdapter<OnBoardSectorDTO> sectorAdapter;
    @NonNull Map<SectorId, SectorDTO> knownSectors;
    @NonNull Set<SectorId> selectedSectors;
    @NonNull BehaviorSubject<SectorDTOList> selectedSectorsSubject;

    public SectorSelectionScreenFragment()
    {
        knownSectors = new HashMap<>();
        selectedSectors = new HashSet<>();
        selectedSectorsSubject = BehaviorSubject.create();
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sectorAdapter = new OnBoardEmptyOrItemAdapter<>(
                getActivity(),
                R.layout.on_board_sector_item_view,
                R.layout.on_board_empty_sector);
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        // We can reuse the same list
        return inflater.inflate(R.layout.on_board_map_exchange, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        sectorList.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.on_board_sector_header, null), "title", false);
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
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected void fetchSectorInfo()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                exchangeSectorCompactListCache.getOne(new ExchangeSectorCompactKey()))
                .subscribe(
                        new Action1<Pair<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO>>()
                        {
                            @Override public void call(Pair<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO> pair)
                            {
                                for (SectorDTO sectorDTO : pair.second.sectors)
                                {
                                    knownSectors.put(sectorDTO.getSectorId(), sectorDTO);
                                }
                                List<OnBoardSectorDTO> onBoardSectors = new ArrayList<>();
                                for (SectorDTO sector : pair.second.sectors)
                                {
                                    onBoardSectors.add(new OnBoardSectorDTO(false, sector));
                                }
                                sectorAdapter.clear();
                                sectorAdapter.addAll(onBoardSectors);
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to load exchanges")));
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(android.R.id.list)
    protected void onSectorClicked(AdapterView<?> parent, View view, int position, long id)
    {
        OnBoardSectorDTO dto = (OnBoardSectorDTO) parent.getItemAtPosition(position);
        if (!dto.selected && selectedSectors.size() >= MAX_SELECTABLE_SECTORS)
        {
            THToast.show(getString(R.string.sector_selected_max, MAX_SELECTABLE_SECTORS));
        }
        else
        {
            dto.selected = !dto.selected;
            if (dto.selected)
            {
                selectedSectors.add(dto.sector.getSectorId());
            }
            else
            {
                selectedSectors.remove(dto.sector.getSectorId());
            }
            sectorAdapter.notifyDataSetChanged();
        }
        displayNextButton();
    }

    protected void displayNextButton()
    {
        nextButton.setEnabled(selectedSectors.size() > 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(android.R.id.button1)
    protected void onNextClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        SectorDTOList selectedDTOs = new SectorDTOList();
        for (SectorId selected : selectedSectors)
        {
            selectedDTOs.add(knownSectors.get(selected));
        }
        selectedSectorsSubject.onNext(selectedDTOs);
    }

    @NonNull public Observable<SectorDTOList> getSelectedSectorsObservable()
    {
        return selectedSectorsSubject.asObservable();
    }
}
