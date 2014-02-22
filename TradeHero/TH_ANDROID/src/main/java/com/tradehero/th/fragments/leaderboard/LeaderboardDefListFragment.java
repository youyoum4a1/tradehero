package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: tho Date: 10/17/13 Time: 7:21 PM Copyright (c) TradeHero */
public class LeaderboardDefListFragment extends BaseLeaderboardFragment
{
    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    protected DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList> leaderboardDefListCacheFetchListener;
    protected DTOCache.GetOrFetchTask<LeaderboardDefListKey, LeaderboardDefKeyList> leaderboardDefListCacheFetchTask;

    private LeaderboardDefListAdapter leaderboardDefListAdapter;
    private ListView contentListView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        leaderboardDefListCacheFetchListener = new LeaderboardDefListViewFragmentDefKeyListListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_def_listview, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        contentListView = (ListView) view.findViewById(android.R.id.list);
    }

    @Override public void onResume()
    {
        Bundle args = getArguments();

        leaderboardDefListAdapter =
                new LeaderboardDefListAdapter(getActivity(), getActivity().getLayoutInflater(), null, R.layout.leaderboard_definition_item_view);
        contentListView.setAdapter(leaderboardDefListAdapter);
        contentListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Object item = leaderboardDefListAdapter.getItem(position);
                if (item instanceof LeaderboardDefKey)
                {
                    LeaderboardDefDTO dto = leaderboardDefCache.get().get((LeaderboardDefKey) item);
                    if (dto != null)
                    {
                        pushLeaderboardListViewFragment(dto);
                    }
                }
            }
        });
        updateLeaderboardDefListKey(args);
        refresh();
        super.onResume();
    }

    @Override public void onPause()
    {
        if (contentListView != null)
        {
            contentListView.setOnItemClickListener(null);
        }
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        detachLeaderboardDefListCacheFetchTask();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        leaderboardDefListCacheFetchListener = null;
        super.onDestroy();
    }

    protected void detachLeaderboardDefListCacheFetchTask()
    {
        if (leaderboardDefListCacheFetchTask != null)
        {
            leaderboardDefListCacheFetchTask.setListener(null);
        }
        leaderboardDefListCacheFetchTask = null;
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
        LeaderboardDefListKey key = new LeaderboardDefListKey(bundle);
        detachLeaderboardDefListCacheFetchTask();
        leaderboardDefListCacheFetchTask  = leaderboardDefListCache.get().getOrFetch(key, leaderboardDefListCacheFetchListener);
        leaderboardDefListCacheFetchTask.execute();
    }

    protected void handleDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value, boolean fromCache)
    {
        if (leaderboardDefListAdapter != null)
        {
            leaderboardDefListAdapter.setItems(value);
            leaderboardDefListAdapter.notifyDataSetChanged();
        }
    }

    protected class LeaderboardDefListViewFragmentDefKeyListListener implements DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList>
    {
        public LeaderboardDefListViewFragmentDefKeyListListener()
        {
            super();
        }

        @Override public void onDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value, boolean fromCache)
        {
            handleDTOReceived(key, value, fromCache);
        }

        @Override public void onErrorThrown(LeaderboardDefListKey key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key));
            Timber.e("Error fetching the leaderboard def key list %s", key, error);
        }
    }
}
