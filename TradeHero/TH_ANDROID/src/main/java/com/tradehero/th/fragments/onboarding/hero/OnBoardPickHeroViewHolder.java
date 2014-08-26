package com.tradehero.th.fragments.onboarding.hero;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class OnBoardPickHeroViewHolder
{
    private static final int PROGRESS_VIEW_ID = 0;
    private static final int LIST_VIEW_ID = 1;

    @InjectView(R.id.switcher) ViewSwitcher switcher;
    @InjectView(android.R.id.list) ListView heroListView;

    @NotNull DTOAdapterNew<SelectableUserDTO> selectedHeroesAdapter;

    //<editor-fold desc="Constructors">
    public OnBoardPickHeroViewHolder(@NotNull Context context)
    {
        super();
        selectedHeroesAdapter = new ArrayDTOAdapterNew<SelectableUserDTO, SelectableUserViewRelative>(
                context,
                R.layout.lbmu_quick_select);
    }
    //</editor-fold>

    void attachView(View view)
    {
        ButterKnife.inject(this, view);
        switcher.setDisplayedChild(PROGRESS_VIEW_ID);
        heroListView.setAdapter(selectedHeroesAdapter);
    }

    void detachView()
    {
        ButterKnife.reset(this);
    }

    void setUsers(@NotNull List<LeaderboardUserDTO> users)
    {
        List<SelectableUserDTO> list = new ArrayList<>();
        for (LeaderboardUserDTO user : users)
        {
            list.add(new SelectableUserDTO(user));
        }
        selectedHeroesAdapter.clear();
        selectedHeroesAdapter.addAll(list);
        selectedHeroesAdapter.notifyDataSetChanged();
        switcher.setDisplayedChild(LIST_VIEW_ID);
    }

    @OnItemClick(android.R.id.list)
    void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        SelectableUserDTO value = (SelectableUserDTO) adapterView.getItemAtPosition(position);
        value.selected = !value.selected;
        selectedHeroesAdapter.notifyDataSetChanged();
    }

    @NotNull LeaderboardUserDTOList getSelectedHeroes()
    {
        LeaderboardUserDTOList selected = new LeaderboardUserDTOList();
        SelectableUserDTO value;
        for (int position = 0; position < selectedHeroesAdapter.getCount(); position++)
        {
            value = selectedHeroesAdapter.getItem(position);
            if (value.selected)
            {
                selected.add(value.value);
            }
        }
        return selected;
    }
}
