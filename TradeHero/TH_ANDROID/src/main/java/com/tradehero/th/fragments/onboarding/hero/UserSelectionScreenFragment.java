package com.tradehero.th.fragments.onboarding.hero;

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
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.market.ExchangeSectorListDTO;
import com.tradehero.th.api.users.SuggestHeroesListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.onboarding.OnBoardEmptyOrItemAdapter;
import com.tradehero.th.persistence.leaderboard.LeaderboardUserListCacheRx;
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
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class UserSelectionScreenFragment extends DashboardFragment
{
    private static final int MAX_SELECTABLE_USERS = 5;

    @Inject LeaderboardUserListCacheRx leaderboardUserListCache;

    @InjectView(android.R.id.list) ListView userList;
    @InjectView(android.R.id.button1) View nextButton;
    Observable<ExchangeSectorListDTO> selectedExchangesSectorsObservable;
    @NonNull ArrayAdapter<SelectableUserDTO> userAdapter;
    @NonNull Map<UserBaseKey, LeaderboardUserDTO> knownUsers;
    @NonNull Set<UserBaseKey> selectedUsers;
    @NonNull BehaviorSubject<LeaderboardUserDTOList> selectedUsersSubject;

    public UserSelectionScreenFragment()
    {
        knownUsers = new HashMap<>();
        selectedUsers = new HashSet<>();
        selectedUsersSubject = BehaviorSubject.create();
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userAdapter = new OnBoardEmptyOrItemAdapter<>(
                getActivity(),
                R.layout.on_board_user_item_view,
                R.layout.on_board_empty_user);
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
        userList.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.on_board_user_header, null), "title", false);
        userList.setAdapter(userAdapter);
        displayNextButton();
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchUsersInfo();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    public void setSelectedExchangesSectorsObservable(@NonNull Observable<ExchangeSectorListDTO> selectedExchangesSectorsObservable)
    {
        this.selectedExchangesSectorsObservable = selectedExchangesSectorsObservable;
    }

    public void fetchUsersInfo()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                selectedExchangesSectorsObservable.flatMap(
                        new Func1<ExchangeSectorListDTO, Observable<Pair<SuggestHeroesListType, LeaderboardUserDTOList>>>()
                        {
                            @Override public Observable<Pair<SuggestHeroesListType, LeaderboardUserDTOList>> call(
                                    ExchangeSectorListDTO selectedExchanges)
                            {
                                return leaderboardUserListCache.getOne(new SuggestHeroesListType(
                                        selectedExchanges.exchanges.get(0).getExchangeIntegerId(),
                                        selectedExchanges.sectors.get(0).getSectorId(),
                                        null, null));
                            }
                        }))
                .subscribe(
                        new Action1<Pair<SuggestHeroesListType, LeaderboardUserDTOList>>()
                        {
                            @Override public void call(Pair<SuggestHeroesListType, LeaderboardUserDTOList> pair)
                            {
                                List<SelectableUserDTO> onBoardUsers = new ArrayList<>();
                                for (LeaderboardUserDTO userDTO : pair.second)
                                {
                                    knownUsers.put(userDTO.getBaseKey(), userDTO);
                                    onBoardUsers.add(new SelectableUserDTO(userDTO, false));
                                }
                                userAdapter.clear();
                                userAdapter.addAll(onBoardUsers);
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to load exchanges")));
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(android.R.id.list)
    protected void onUserClicked(AdapterView<?> parent, View view, int position, long id)
    {
        nextButton.setVisibility(View.VISIBLE);
        SelectableUserDTO dto = (SelectableUserDTO) parent.getItemAtPosition(position);
        if (!dto.selected && selectedUsers.size() >= MAX_SELECTABLE_USERS)
        {
            THToast.show(getString(R.string.on_board_user_selected_max, MAX_SELECTABLE_USERS));
        }
        else
        {
            dto.selected = !dto.selected;
            if (dto.selected)
            {
                selectedUsers.add(dto.value.getBaseKey());
            }
            else
            {
                selectedUsers.remove(dto.value.getBaseKey());
            }
            userAdapter.notifyDataSetChanged();
        }
        displayNextButton();
    }

    protected void displayNextButton()
    {
        nextButton.setEnabled(selectedUsers.size() > 0);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(android.R.id.button1)
    protected void onNextClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        LeaderboardUserDTOList selectedDTOs = new LeaderboardUserDTOList();
        for (UserBaseKey selected : selectedUsers)
        {
            selectedDTOs.add(knownUsers.get(selected));
        }
        selectedUsersSubject.onNext(selectedDTOs);
    }

    @NonNull public Observable<LeaderboardUserDTOList> getSelectedUsersObservable()
    {
        return selectedUsersSubject.asObservable();
    }
}
