package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKeyFactory;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCacheRx;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.AndroidObservable;

public class LeaderboardDefListFragment extends BaseLeaderboardFragment
{
    @Inject Lazy<LeaderboardDefListCacheRx> leaderboardDefListCache;
    @Inject protected LeaderboardDefListKeyFactory leaderboardDefListKeyFactory;
    @Nullable protected Subscription leaderboardDefListCacheFetchSubscription;

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
        initViews(view);
    }

    @Override protected void initViews(View view)
    {
        contentListView.setAdapter(leaderboardDefListAdapter);
        contentListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
    }

    @Override public void onResume()
    {
        updateLeaderboardDefListKey(getArguments());
        super.onResume();
    }

    @Override public void onDestroyView()
    {
        unsubscribe(leaderboardDefListCacheFetchSubscription);
        leaderboardDefListCacheFetchSubscription = null;
        contentListView.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        leaderboardDefListAdapter = null;
        super.onDestroy();
    }

    @OnItemClick(android.R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        pushLeaderboardListViewFragment((LeaderboardDefDTO) parent.getItemAtPosition(position));
    }

    private void updateLeaderboardDefListKey(Bundle bundle)
    {
        unsubscribe(leaderboardDefListCacheFetchSubscription);
        LeaderboardDefListKey key = leaderboardDefListKeyFactory.create(bundle);
        Observable<List<LeaderboardDefDTO>> leaderboardDefObservable = leaderboardDefListCache.get().get(key)
                .map(pair -> pair.second);

        leaderboardDefListCacheFetchSubscription = AndroidObservable.bindFragment(
                this,
                leaderboardDefObservable)
                .subscribe(
                        leaderboardDefListAdapter::setItems,
                        (e) -> THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key)));
    }
}
