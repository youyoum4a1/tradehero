package com.tradehero.th.fragments.competition;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.HelpVideoDTOList;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.key.HelpVideoListKey;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.persistence.competition.HelpVideoListCacheRx;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

@Routable(
        "providers/:providerId/helpVideos"
)
public class ProviderVideoListFragment extends CompetitionFragment
{
    @Inject HelpVideoListCacheRx helpVideoListCache;

    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(R.id.help_videos_list) AbsListView videoListView;
    @InjectView(R.id.help_video_list_screen) BetterViewAnimator helpVideoListScreen;

    private HelpVideoDTOList helpVideoDTOs;
    private ProviderVideoAdapter providerVideoAdapter;
    private int currentDisplayedChild;
    @NonNull protected SubscriptionList subscriptions;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        subscriptions = new SubscriptionList();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_help_video_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        providerVideoAdapter = new ProviderVideoAdapter(getActivity(), R.layout.help_video_item_view);
        videoListView.setAdapter(providerVideoAdapter);
        videoListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
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

        HelpVideoListKey key = new HelpVideoListKey(providerId);
        subscriptions.add(AndroidObservable.bindFragment(this, helpVideoListCache.get(key))
                .subscribe(createVideoListCacheObserver()));
    }

    @Override public void onPause()
    {
        currentDisplayedChild = helpVideoListScreen.getDisplayedChildLayoutId();
        super.onPause();
    }

    @Override public void onStop()
    {
        subscriptions.unsubscribe();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        providerVideoAdapter = null;
        videoListView.setEmptyView(null);
        videoListView.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override protected void linkWith(@NonNull ProviderDTO providerDTO, boolean andDisplay)
    {
        super.linkWith(providerDTO, andDisplay);
        if (andDisplay)
        {
            displayActionBarTitle();
        }
    }

    private void linkWith(HelpVideoDTOList videoDTOs)
    {
        this.helpVideoDTOs = videoDTOs;
        updateAdapter();
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
        if (providerDTO == null || providerDTO.name == null)
        {
            setActionBarTitle("");
        }
        else
        {
            setActionBarTitle(getString(R.string.competition_help_video_title, providerDTO.name));
        }
    }

    private void launchVideo(@NonNull HelpVideoDTO videoDTO)
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
            navigator.get().pushFragment(WebViewFragment.class, bundle);
        }
    }

    protected Observer<Pair<HelpVideoListKey, HelpVideoDTOList>> createVideoListCacheObserver()
    {
        return new ProviderVideoListFragmentVideoListCacheObserver();
    }

    protected class ProviderVideoListFragmentVideoListCacheObserver implements Observer<Pair<HelpVideoListKey, HelpVideoDTOList>>
    {
        @Override public void onNext(Pair<HelpVideoListKey, HelpVideoDTOList> pair)
        {
            onFinished();
            if (videoListView != null)
            {
                videoListView.setEmptyView(emptyView);
            }

            linkWith(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            onFinished();
            THToast.show(getString(R.string.error_fetch_help_video_list_info));
            Timber.d("Error fetching the list of help videos", e);
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
