package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import java.util.List;

public class LeaderboardDefListFragment extends LeaderboardDefFragment
{
    private ArrayDTOAdapter<LeaderboardDefDTO, LeaderboardDefView> leaderboardDefListAdapter;
    @InjectView(android.R.id.list) protected ListView contentListView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        leaderboardDefListAdapter = new ArrayDTOAdapter<>(
                getActivity(),
                R.layout.leaderboard_definition_item_view);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.leaderboard_def_listview, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        contentListView.setAdapter(leaderboardDefListAdapter);
        contentListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
    }


    @Override public void onDestroy()
    {
        leaderboardDefListAdapter = null;
        super.onDestroy();
    }

    @Override protected void onLeaderboardDefListLoaded(List<LeaderboardDefDTO> leaderboardDefDTOs)
    {
        leaderboardDefListAdapter.setItems(leaderboardDefDTOs);
    }

    @Override public void onDestroyView()
    {
        contentListView.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @OnItemClick(android.R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        pushLeaderboardListViewFragment((LeaderboardDefDTO) parent.getItemAtPosition(position));
    }
}
