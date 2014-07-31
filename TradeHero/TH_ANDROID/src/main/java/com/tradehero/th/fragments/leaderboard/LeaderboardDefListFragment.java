package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapterNew;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKeyFactory;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class LeaderboardDefListFragment extends BaseLeaderboardFragment
{
    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject protected LeaderboardDefListKeyFactory leaderboardDefListKeyFactory;
    protected DTOCacheNew.Listener<LeaderboardDefListKey, LeaderboardDefDTOList> leaderboardDefListCacheFetchListener;

    private ArrayAdapter<LeaderboardDefDTO> leaderboardDefListAdapter;
    @InjectView(android.R.id.list) protected ListView contentListView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        leaderboardDefListCacheFetchListener = createLeaderboardDefKeyListListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_def_listview, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        leaderboardDefListAdapter = new ArrayDTOAdapterNew<LeaderboardDefDTO, LeaderboardDefView>(
                        getActivity(),
                        R.layout.leaderboard_definition_item_view);
        contentListView.setAdapter(leaderboardDefListAdapter);
    }

    @Override public void onResume()
    {
        updateLeaderboardDefListKey(getArguments());
        refresh();
        super.onResume();
    }

    @Override public void onDestroyView()
    {
        detachLeaderboardDefListCacheFetchTask();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected void detachLeaderboardDefListCacheFetchTask()
    {
        leaderboardDefListCache.get().unregister(leaderboardDefListCacheFetchListener);
    }

    @OnItemClick(android.R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Object item = parent.getItemAtPosition(position);
        if (item instanceof LeaderboardDefKey)
        {
            LeaderboardDefDTO dto = leaderboardDefCache.get().get((LeaderboardDefKey) item);
            if (dto != null)
            {
                pushLeaderboardListViewFragment(dto);
            }
        }
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
        detachLeaderboardDefListCacheFetchTask();
        LeaderboardDefListKey key = leaderboardDefListKeyFactory.create(bundle);
        leaderboardDefListCache.get().register(key, leaderboardDefListCacheFetchListener);
        leaderboardDefListCache.get().getOrFetchAsync(key);
    }

    protected DTOCacheNew.Listener<LeaderboardDefListKey, LeaderboardDefDTOList> createLeaderboardDefKeyListListener()
    {
        return new LeaderboardDefListViewFragmentDefKeyListListener();
    }

    protected class LeaderboardDefListViewFragmentDefKeyListListener implements DTOCacheNew.Listener<LeaderboardDefListKey, LeaderboardDefDTOList>
    {
        @Override public void onDTOReceived(@NotNull LeaderboardDefListKey key, @NotNull LeaderboardDefDTOList value)
        {
            handleDTOReceived(key, value);
        }

        @Override public void onErrorThrown(@NotNull LeaderboardDefListKey key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key));
            Timber.e("Error fetching the leaderboard def key list %s", key, error);
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
