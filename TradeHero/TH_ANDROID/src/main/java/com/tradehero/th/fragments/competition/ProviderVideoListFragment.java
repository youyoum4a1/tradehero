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
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.activities.WebViewActivity;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.HelpVideoIdList;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.key.HelpVideoId;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.persistence.competition.HelpVideoCache;
import com.tradehero.th.persistence.competition.HelpVideoListCache;
import java.util.ArrayList;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by xavier on 1/16/14.
 */
public class ProviderVideoListFragment extends CompetitionFragment
{
    @Inject HelpVideoListCache helpVideoListCache;
    @Inject HelpVideoCache helpVideoCache;

    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(R.id.help_videos_list) AbsListView videoListView;
    @InjectView(R.id.help_video_list_screen) BetterViewAnimator helpVideoListScreen;

    private ActionBar actionBar;
    private HelpVideoIdList helpVideoIds;
    private DTOCache.Listener<HelpVideoListKey, HelpVideoIdList> helpVideoListCacheListener;
    private DTOCache.GetOrFetchTask<HelpVideoListKey, HelpVideoIdList> helpVideoListFetchTask;
    private ProviderVideoAdapter providerVideoAdapter;
    private int currentDisplayedChild;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_help_video_list, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        ButterKnife.inject(this, view);
        helpVideoListCacheListener = new ProviderVideoListFragmentVideoListCacheListener();
        providerVideoAdapter = new ProviderVideoAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.help_video_item_view);
        providerVideoAdapter.setItems(new ArrayList<HelpVideoId>());

        if (videoListView != null)
        {
            videoListView.setAdapter(providerVideoAdapter);
            videoListView.setOnItemClickListener(new ProviderVideoListFragmentItemClickListener());
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
        actionBar = null;
    }
    //</editor-fold>

    @Override public void onResume()
    {
        super.onResume();

        if (currentDisplayedChild != 0)
        {
            helpVideoListScreen.setDisplayedChildByLayoutId(currentDisplayedChild);
        }

        detachListVideoFetchTask();

        helpVideoListFetchTask = helpVideoListCache.getOrFetch(new HelpVideoListKey(providerId), false, helpVideoListCacheListener);
        helpVideoListFetchTask.execute();
    }

    @Override public void onPause()
    {
        currentDisplayedChild = helpVideoListScreen.getDisplayedChildLayoutId();

        super.onPause();
    }

    @Override public void onDestroyView()
    {
        detachListVideoFetchTask();
        helpVideoListCacheListener = null;
        providerVideoAdapter = null;
        if (videoListView != null)
        {
            videoListView.setOnItemClickListener(null);
            videoListView.setEmptyView(null);
        }
        videoListView = null;
        super.onDestroyView();
    }

    private void detachListVideoFetchTask()
    {
        if (helpVideoListFetchTask != null)
        {
            helpVideoListFetchTask.setListener(null);
        }
        helpVideoListFetchTask = null;
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
        providerVideoAdapter.setItems(helpVideoIds);
        providerVideoAdapter.notifyDataSetChanged();
    }

    private void displayActionBarTitle()
    {
        if (actionBar != null)
        {
            if (providerSpecificResourcesDTO != null && providerSpecificResourcesDTO.helpVideoListFragmentTitleResId > 0)
            {
                actionBar.setTitle(providerSpecificResourcesDTO.helpVideoListFragmentTitleResId);
            }
            else if (providerDTO == null || providerDTO.name == null)
            {
                actionBar.setTitle("");
            }
            else
            {
                actionBar.setTitle(providerDTO.name);
            }
        }
    }

    private void launchVideo(HelpVideoId helpVideoId)
    {
        HelpVideoDTO cachedHelpVideo = helpVideoCache.get(helpVideoId);
        if (cachedHelpVideo == null)
        {
            Timber.d("There is no Help Video in cache for id %d", helpVideoId);
            THToast.show(R.string.error_fetch_help_video_info);
            return;
        }

        // openVideoInExternalPlayer(cachedHelpVideo);

        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra(WebViewActivity.HTML_DATA, cachedHelpVideo.embedCode);
        startActivity(intent);
    }

    private void openVideoInExternalPlayer(HelpVideoDTO cachedHelpVideo)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, cachedHelpVideo.embedCode);
        intent.putExtra(Intent.EXTRA_HTML_TEXT, cachedHelpVideo.embedCode);
        intent.setType("text/html");

        if (getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() == 0)
        {
            Timber.d("There is no package that can play the video for id %d, dto %s", cachedHelpVideo.getHelpVideoId(), cachedHelpVideo);
            THToast.show(R.string.error_help_video_no_package_available_to_play);
            return;
        }

        Timber.d("Launching video intent on %s", cachedHelpVideo.embedCode);
        startActivity(intent);
    }

    private class ProviderVideoListFragmentVideoListCacheListener implements DTOCache.Listener<HelpVideoListKey, HelpVideoIdList>
    {
        @Override public void onDTOReceived(HelpVideoListKey key, HelpVideoIdList value, boolean fromCache)
        {
            onFinished();
            if (videoListView != null)
            {
                videoListView.setEmptyView(emptyView);
            }

            linkWith(value, true);
        }

        @Override public void onErrorThrown(HelpVideoListKey key, Throwable error)
        {
            onFinished();
            THToast.show(getString(R.string.error_fetch_help_video_list_info));
            Timber.d("Error fetching the list of help videos %s", key, error);
        }

        private void onFinished()
        {
            helpVideoListScreen.setDisplayedChildByLayoutId(R.id.help_videos_list);
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
