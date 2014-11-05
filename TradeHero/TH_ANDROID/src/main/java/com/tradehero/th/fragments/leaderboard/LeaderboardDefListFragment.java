package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
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
import butterknife.OnItemClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKeyFactory;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public class LeaderboardDefListFragment extends BaseLeaderboardFragment
{
    @Inject protected Lazy<LeaderboardDefListCacheRx> leaderboardDefListCache;
    @Inject protected LeaderboardDefListKeyFactory leaderboardDefListKeyFactory;
    @Nullable protected Subscription leaderboardDefListCacheFetchSubscription;

    private ArrayAdapter<LeaderboardDefDTO> leaderboardDefListAdapter;
    @InjectView(android.R.id.list) protected ListView contentListView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        leaderboardDefListAdapter = new ArrayDTOAdapterNew<LeaderboardDefDTO, LeaderboardDefView>(
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
        refresh();
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

    protected void refresh()
    {
        if (leaderboardDefListAdapter != null)
        {
            leaderboardDefListAdapter.notifyDataSetChanged();
        }
    }

    private void updateLeaderboardDefListKey(Bundle bundle)
    {
        unsubscribe(leaderboardDefListCacheFetchSubscription);
        LeaderboardDefListKey key = leaderboardDefListKeyFactory.create(bundle);
        leaderboardDefListCacheFetchSubscription = AndroidObservable.bindFragment(
                this,
                leaderboardDefListCache.get().get(key))
                .subscribe(createLeaderboardDefKeyListObserver());
    }

    protected Observer<Pair<LeaderboardDefListKey, LeaderboardDefDTOList>> createLeaderboardDefKeyListObserver()
    {
        return new LeaderboardDefListViewFragmentDefKeyListObserver();
    }

    protected class LeaderboardDefListViewFragmentDefKeyListObserver implements Observer<Pair<LeaderboardDefListKey, LeaderboardDefDTOList>>
    {
        @Override public void onNext(Pair<LeaderboardDefListKey, LeaderboardDefDTOList> pair)
        {
            handleDTOReceived(pair.first, pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key));
            Timber.e("Error fetching the leaderboard def key list", e);
        }
    }

    protected void handleDTOReceived(LeaderboardDefListKey key, LeaderboardDefDTOList value)
    {
        if (leaderboardDefListAdapter != null)
        {
            leaderboardDefListAdapter.clear();
            leaderboardDefListAdapter.addAll(value);
            leaderboardDefListAdapter.notifyDataSetChanged();
        }
    }
}
