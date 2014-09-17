package com.tradehero.th.fragments.competition;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.HelpVideoDTOList;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.persistence.competition.HelpVideoListCache;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

@Routable(
        "providers/:providerId/helpVideos"
)
public class ProviderVideoListFragment extends CompetitionFragment
{
    @Inject HelpVideoListCache helpVideoListCache;
    @Inject DashboardNavigator navigator;

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
        openWithDefaultApp(videoDTO);
    }

    private void openWithDefaultApp(HelpVideoDTO helpVideoDTO)
    {
        Uri url = Uri.parse(helpVideoDTO.videoUrl);
        Intent videoIntent = new Intent(Intent.ACTION_VIEW, url);
        PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> handlerActivities = packageManager.queryIntentActivities(videoIntent, 0);
        if (handlerActivities.size() > 0)
        {
            getActivity().startActivity(videoIntent);
        }
        else
        {
            Bundle bundle = new Bundle();
            WebViewFragment.putUrl(bundle, helpVideoDTO.videoUrl);
            navigator.pushFragment(WebViewFragment.class, bundle);
        }
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
