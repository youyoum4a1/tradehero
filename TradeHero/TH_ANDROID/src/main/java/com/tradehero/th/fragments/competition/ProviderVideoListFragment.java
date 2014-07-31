package com.tradehero.th.fragments.competition;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.activities.WebViewActivity;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.HelpVideoDTOList;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.persistence.competition.HelpVideoCache;
import com.tradehero.th.persistence.competition.HelpVideoListCache;
import java.net.URLEncoder;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class ProviderVideoListFragment extends CompetitionFragment
{
    @Inject HelpVideoListCache helpVideoListCache;
    @Inject HelpVideoCache helpVideoCache;

    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(R.id.help_videos_list) AbsListView videoListView;
    @InjectView(R.id.help_video_list_screen) BetterViewAnimator helpVideoListScreen;

    private HelpVideoDTOList helpVideoDTOs;
    private DTOCacheNew.Listener<HelpVideoListKey, HelpVideoDTOList> helpVideoListCacheListener;
    private ProviderVideoAdapter providerVideoAdapter;
    private int currentDisplayedChild;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        helpVideoListCacheListener = createVideoListCacheListener();
    }

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
        providerVideoAdapter = new ProviderVideoAdapter(getActivity(), R.layout.help_video_item_view);
        videoListView.setAdapter(providerVideoAdapter);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
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
        HelpVideoListKey key = new HelpVideoListKey(providerId);
        helpVideoListCache.register(key, helpVideoListCacheListener);
        helpVideoListCache.getOrFetchAsync(key, false);
    }

    @Override public void onPause()
    {
        currentDisplayedChild = helpVideoListScreen.getDisplayedChildLayoutId();
        super.onPause();
    }

    @Override public void onStop()
    {
        detachListVideoFetchTask();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        providerVideoAdapter = null;
        videoListView.setEmptyView(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        helpVideoListCacheListener = null;
        super.onDestroy();
    }

    private void detachListVideoFetchTask()
    {
        helpVideoListCache.unregister(helpVideoListCacheListener);
    }

    @Override protected void linkWith(@NotNull ProviderDTO providerDTO, boolean andDisplay)
    {
        super.linkWith(providerDTO, andDisplay);
        if (andDisplay)
        {
            displayActionBarTitle();
        }
    }

    private void linkWith(HelpVideoDTOList videoDTOs, boolean andDisplay)
    {
        this.helpVideoDTOs = videoDTOs;
        if (andDisplay)
        {
            updateAdapter();
        }
    }

    private void updateAdapter()
    {
        providerVideoAdapter.clear();
        if (helpVideoDTOs != null)
        {
            providerVideoAdapter.addAll(helpVideoDTOs);
        }
        providerVideoAdapter.notifyDataSetChanged();
    }

    private void displayActionBarTitle()
    {
        if (providerDTO != null
                && providerDTO.specificResources != null
                && providerDTO.specificResources.helpVideoListFragmentTitleResId > 0)
        {
            setActionBarTitle(providerDTO.specificResources.helpVideoListFragmentTitleResId);
        }
        else if (providerDTO == null || providerDTO.name == null)
        {
            setActionBarTitle("");
        }
        else
        {
            setActionBarTitle(providerDTO.name);
        }
    }

    private void launchVideo(@NotNull HelpVideoDTO videoDTO)
    {
        try
        {
            openVideoInChromeBrowser(videoDTO);
        }
        catch (ActivityNotFoundException e)
        {
            Timber.e("Failed to start Chrome Browser", e);
            // TODO In the vague hope it will work
            openVideoWithInApp(videoDTO);
        }

        // openVideoWithInApp(videoDTO);
    }

    /**
     * We have problem outOfMemory with this way, don't use it
     * @param cachedHelpVideo
     */
    private void openVideoWithInApp(HelpVideoDTO cachedHelpVideo)
    {
        Intent intent = new Intent(getActivity(), WebViewActivity.class);
        intent.putExtra(WebViewActivity.HTML_DATA, cachedHelpVideo.embedCode);
        startActivity(intent);
    }

    private void openVideoInChromeBrowser(HelpVideoDTO cachedHelpVideo) throws ActivityNotFoundException
    {
        Intent i = new Intent();

        i.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
        i.setAction(Intent.ACTION_VIEW);

        String dataUri = "data:text/html," + URLEncoder.encode(cachedHelpVideo.embedCode).replaceAll("\\+","%20");
        i.setData(Uri.parse(dataUri));

        startActivity(i);
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

    protected DTOCacheNew.Listener<HelpVideoListKey, HelpVideoDTOList> createVideoListCacheListener()
    {
        return new ProviderVideoListFragmentVideoListCacheListener();
    }

    protected class ProviderVideoListFragmentVideoListCacheListener implements DTOCacheNew.Listener<HelpVideoListKey, HelpVideoDTOList>
    {
        @Override public void onDTOReceived(@NotNull HelpVideoListKey key, @NotNull HelpVideoDTOList value)
        {
            onFinished();
            if (videoListView != null)
            {
                videoListView.setEmptyView(emptyView);
            }

            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull HelpVideoListKey key, @NotNull Throwable error)
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

    @OnItemClick(R.id.help_videos_list)
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        // It is not testing for availability on purpose
        launchVideo((HelpVideoDTO) adapterView.getItemAtPosition(position));
    }
}
