package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.HelpVideoId;
import com.tradehero.th.api.competition.HelpVideoIdList;
import com.tradehero.th.api.competition.HelpVideoListKey;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.persistence.competition.HelpVideoListCache;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * Created by xavier on 1/16/14.
 */
public class ProviderVideoListFragment extends CompetitionFragment
{
    public static final String TAG = ProviderVideoListFragment.class.getSimpleName();

    private ActionBar actionBar;
    private ProgressBar progressBar;
    private AbsListView videoListView;
    @Inject protected HelpVideoListCache helpVideoListCache;
    private HelpVideoIdList helpVideoIds;
    private DTOCache.Listener<HelpVideoListKey, HelpVideoIdList> helpVideoListCacheListener;
    private DTOCache.GetOrFetchTask<HelpVideoListKey, HelpVideoIdList> helpVideoListFetchTask;
    private ProviderVideoAdapter providerVideoAdapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_help_video_list, container, false);

        this.helpVideoListCacheListener = new ProviderVideoListFragmentVideoListCacheListener();
        this.providerVideoAdapter = new ProviderVideoAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.help_video_item_view);
        this.providerVideoAdapter.setItems(new ArrayList<HelpVideoId>());

        this.progressBar = (ProgressBar) view.findViewById(android.R.id.empty);
        if (this.progressBar != null)
        {
            this.progressBar.setVisibility(View.VISIBLE);
        }
        this.videoListView = (AbsListView) view.findViewById(R.id.help_videos_list);
        if (this.videoListView != null)
        {
            this.videoListView.setAdapter(this.providerVideoAdapter);
            this.videoListView.setOnItemClickListener(new ProviderVideoListFragmentItemClickListener());
        }
        return view;
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        displayActionBarTitle();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
        this.actionBar = null;
    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();

        detachListVideoFetchTask();
        this.helpVideoListFetchTask = this.helpVideoListCache.getOrFetch(new HelpVideoListKey(this.providerId), this.helpVideoListCacheListener);
        if (this.progressBar != null)
        {
            this.progressBar.setVisibility(View.VISIBLE);
        }
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

    @Override protected void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        super.linkWith(providerDTO, andDisplay);
        if (andDisplay)
        {
            displayActionBarTitle();
        }
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

    private void displayActionBarTitle()
    {
        if (this.actionBar != null)
        {
            if (this.providerDTO == null || this.providerDTO.name == null)
            {
                this.actionBar.setTitle("");
            }
            else
            {
                this.actionBar.setTitle(this.providerDTO.name);
            }
        }
    }

    private void launchVideo(HelpVideoId helpVideoId)
    {
        // TODO
    }

    private class ProviderVideoListFragmentVideoListCacheListener implements DTOCache.Listener<HelpVideoListKey, HelpVideoIdList>
    {
        @Override public void onDTOReceived(HelpVideoListKey key, HelpVideoIdList value)
        {
            this.onFinished();
            linkWith(value, true);
        }

        @Override public void onErrorThrown(HelpVideoListKey key, Throwable error)
        {
            this.onFinished();
            THToast.show(getString(R.string.error_fetch_help_video_list_info));
            THLog.e(TAG, "Error fetching the list of help videos " + key, error);
        }

        private void onFinished()
        {
            if (ProviderVideoListFragment.this.progressBar != null)
            {
                ProviderVideoListFragment.this.progressBar.setVisibility(View.GONE);
            }
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
