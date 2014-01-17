package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.HelpVideoId;
import com.tradehero.th.api.competition.HelpVideoIdList;
import com.tradehero.th.api.competition.HelpVideoListKey;
import com.tradehero.th.persistence.competition.HelpVideoListCache;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Created by xavier on 1/16/14.
 */
public class ProviderVideoListFragment extends CompetitionFragment
{
    public static final String TAG = ProviderVideoListFragment.class.getSimpleName();

    private AbsListView videoListView;
    @Inject protected HelpVideoListCache helpVideoListCache;
    private HelpVideoIdList helpVideoIds;
    private DTOCache.Listener<HelpVideoListKey, HelpVideoIdList> helpVideoListCacheListener;
    private DTOCache.GetOrFetchTask<HelpVideoListKey, HelpVideoIdList> helpVideoListFetchTask;
    private ProviderVideoAdapter providerVideoAdapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        this.helpVideoListCacheListener = new ProviderVideoListFragmentVideoListCacheListener();
        this.providerVideoAdapter = new ProviderVideoAdapter(getActivity(), getActivity().getLayoutInflater(), 0 /* TODO */);
        this.providerVideoAdapter.setItems(new ArrayList<HelpVideoId>());

        // TODO inflate
        if (this.videoListView != null)
        {
            this.videoListView.setAdapter(this.providerVideoAdapter);
            this.videoListView.setOnItemClickListener(new ProviderVideoListFragmentItemClickListener());
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override public void onResume()
    {
        super.onResume();

        detachListVideoFetchTask();
        this.helpVideoListFetchTask = this.helpVideoListCache.getOrFetch(new HelpVideoListKey(this.providerId), this.helpVideoListCacheListener);
        this.helpVideoListFetchTask.execute();
    }

    @Override public void onDestroyView()
    {
        detachListVideoFetchTask();
        this.helpVideoListCacheListener = null;
        this.providerVideoAdapter = null;
        if (this.videoListView != null)
        {
            this.videoListView.setOnItemClickListener(null);
        }
        this.videoListView = null;
        super.onDestroyView();
    }

    private void detachListVideoFetchTask()
    {
        if (this.helpVideoListFetchTask != null)
        {
            this.helpVideoListFetchTask.setListener(null);
        }
        this.helpVideoListFetchTask = null;
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    private void linkWith(HelpVideoIdList helpVideoIds, boolean andDisplay)
    {
        this.helpVideoIds = helpVideoIds;
        if (andDisplay)
        {
            updateAdapter();
        }
    }

    private void updateAdapter()
    {
        this.providerVideoAdapter.setItems(this.helpVideoIds);
        this.providerVideoAdapter.notifyDataSetChanged();
    }

    private void launchVideo(HelpVideoId helpVideoId)
    {
        // TODO
    }

    private class ProviderVideoListFragmentVideoListCacheListener implements DTOCache.Listener<HelpVideoListKey, HelpVideoIdList>
    {
        @Override public void onDTOReceived(HelpVideoListKey key, HelpVideoIdList value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(HelpVideoListKey key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_help_video_list_info));
            THLog.e(TAG, "Error fetching the list of help videos " + key, error);
        }
    }

    private class ProviderVideoListFragmentItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
        {
            // It is not testing for availability on purpose
            launchVideo(ProviderVideoListFragment.this.helpVideoIds.get(position));
        }
    }
}
