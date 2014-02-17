package com.tradehero.th.fragments.competition;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.key.HelpVideoId;
import com.tradehero.th.api.competition.HelpVideoIdList;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.persistence.competition.HelpVideoCache;
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
    @Inject protected HelpVideoCache helpVideoCache;
    private HelpVideoIdList helpVideoIds;
    private DTOCache.Listener<HelpVideoListKey, HelpVideoIdList> helpVideoListCacheListener;
    private DTOCache.GetOrFetchTask<HelpVideoListKey, HelpVideoIdList> helpVideoListFetchTask;
    private ProviderVideoAdapter providerVideoAdapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_help_video_list, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
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

        HelpVideoIdList cachedHelpVideos = this.helpVideoListCache.get(new HelpVideoListKey(this.providerId));
        if (cachedHelpVideos != null)
        {
            linkWith(cachedHelpVideos, true);
            if (this.progressBar != null)
            {
                this.progressBar.setVisibility(View.GONE);
            }
        }
        else
        {
            this.helpVideoListFetchTask = this.helpVideoListCache.getOrFetch(new HelpVideoListKey(this.providerId), this.helpVideoListCacheListener);
            if (this.progressBar != null)
            {
                this.progressBar.setVisibility(View.VISIBLE);
            }
            this.helpVideoListFetchTask.execute();
        }
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
            if (providerSpecificResourcesDTO != null && providerSpecificResourcesDTO.helpVideoListFragmentTitleResId > 0)
            {
                this.actionBar.setTitle(providerSpecificResourcesDTO.helpVideoListFragmentTitleResId);
            }
            else if (this.providerDTO == null || this.providerDTO.name == null)
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
        HelpVideoDTO cachedHelpVideo = helpVideoCache.get(helpVideoId);
        if (cachedHelpVideo == null)
        {
            THLog.d(TAG, "There is no Help Video in cache for id " + helpVideoId);
            THToast.show(R.string.error_fetch_help_video_info);
            return;
        }

        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(cachedHelpVideo.videoUrl));
        //intent.setType("video/*");

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, cachedHelpVideo.embedCode);
        intent.putExtra(Intent.EXTRA_HTML_TEXT, cachedHelpVideo.embedCode);
        intent.setType("text/html");

        if (getActivity().getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY).size() == 0)
        {
            THLog.d(TAG, "There is no package that can play the video for id " + helpVideoId + ", dto " + cachedHelpVideo);
            THToast.show(R.string.error_help_video_no_package_available_to_play);
            return;
        }

        THLog.d(TAG, "Launching video intent on " + cachedHelpVideo.embedCode);
        startActivity(intent);
    }

    private class ProviderVideoListFragmentVideoListCacheListener implements DTOCache.Listener<HelpVideoListKey, HelpVideoIdList>
    {
        @Override public void onDTOReceived(HelpVideoListKey key, HelpVideoIdList value, boolean fromCache)
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
