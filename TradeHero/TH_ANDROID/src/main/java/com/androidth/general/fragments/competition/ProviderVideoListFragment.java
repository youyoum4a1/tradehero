package com.androidth.general.fragments.competition;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.common.widget.BetterViewAnimator;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.androidth.general.R;
import com.androidth.general.api.competition.HelpVideoDTO;
import com.androidth.general.api.competition.HelpVideoDTOList;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.key.HelpVideoListKey;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.fragments.web.WebViewFragment;
import com.androidth.general.persistence.competition.HelpVideoListCacheRx;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.utils.route.THRouter;
import java.util.List;
import javax.inject.Inject;

import butterknife.Unbinder;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

@Routable({
        "providers/:providerId/helpVideos",
        "providers/:providerId/helpVideos/:videoId",
})
public class ProviderVideoListFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = ProviderVideoListFragment.class.getName() + ".providerId";

    @Inject ProviderCacheRx providerCache;
    @Inject HelpVideoListCacheRx helpVideoListCache;
    @Inject THRouter thRouter;

    @BindView(android.R.id.empty) View emptyView;
    @BindView(R.id.help_videos_list) AbsListView videoListView;
    @BindView(R.id.help_video_list_screen) BetterViewAnimator helpVideoListScreen;

    @RouteProperty("providerId") protected Integer routedProviderId;
    @RouteProperty("videoId") Integer routedVideoId;

    protected ProviderId providerId;
    protected ProviderDTO providerDTO;

    private ProviderVideoAdapter providerVideoAdapter;
    private int currentDisplayedChild;
    private ClipboardManager clipboardManager;

    private Unbinder unbinder;

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @NonNull public static ProviderId getProviderId(@NonNull Bundle args)
    {
        return new ProviderId(args.getBundle(BUNDLE_KEY_PROVIDER_ID));
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        providerVideoAdapter = new ProviderVideoAdapter(activity, R.layout.help_video_item_view);
        clipboardManager = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this, getArguments());
        if (routedProviderId != null)
        {
            putProviderId(getArguments(), new ProviderId(routedProviderId));
        }
        this.providerId = getProviderId(getArguments());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_help_video_list, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        helpVideoListScreen.setDisplayedChildByLayoutId(android.R.id.progress);
        videoListView.setAdapter(providerVideoAdapter);
        videoListView.setOnScrollListener(fragmentElements.get().getListViewScrollListener());
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchProviderDTO();
        fetchVideoList();
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
    }

    @Override public void onPause()
    {
        currentDisplayedChild = helpVideoListScreen.getDisplayedChildLayoutId();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        providerVideoAdapter = null;
        videoListView.setEmptyView(null);
        videoListView.setOnScrollListener(null);
        unbinder.unbind();
        super.onDestroyView();
    }

    protected void fetchProviderDTO()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                providerCache.get(this.providerId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<ProviderId, ProviderDTO>>()
                        {
                            @Override public void call(Pair<ProviderId, ProviderDTO> pair)
                            {
                                linkWith(pair.second);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                if (providerDTO == null)
                                {
                                    THToast.show(getString(R.string.error_fetch_provider_info));
                                }
                                Timber.e("Error fetching the provider info", throwable);
                            }
                        }));
    }

    protected void linkWith(@NonNull ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        displayActionBarTitle();
    }

    protected void fetchVideoList()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                helpVideoListCache.getOne(new HelpVideoListKey(providerId)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<HelpVideoListKey, HelpVideoDTOList>>()
                        {
                            @Override public void call(Pair<HelpVideoListKey, HelpVideoDTOList> pair)
                            {
                                linkWith(pair.second);
                            }
                        },
                        new TimberAndToastOnErrorAction1(getString(R.string.error_fetch_help_video_list_info),
                                "Error fetching the list of help videos")));
    }

    private void linkWith(HelpVideoDTOList videoDTOs)
    {
        providerVideoAdapter.setNotifyOnChange(false);
        providerVideoAdapter.clear();
        if (videoDTOs != null)
        {
            providerVideoAdapter.addAll(videoDTOs);
        }
        providerVideoAdapter.setNotifyOnChange(true);
        providerVideoAdapter.notifyDataSetChanged();
        helpVideoListScreen.setDisplayedChildByLayoutId(R.id.help_videos_list);
        videoListView.setEmptyView(emptyView);

        if (routedVideoId != null && videoDTOs != null)
        {
            for (HelpVideoDTO video : videoDTOs)
            {
                if (routedVideoId.equals(video.id))
                {
                    openVideo(video);
                    routedVideoId = null;
                    break;
                }
            }
        }
    }

    private void displayActionBarTitle()
    {
        if (providerDTO == null || providerDTO.name == null)
        {
            setActionBarTitle(getString(R.string.competition_help_video_title, ""));
        }
        else
        {
            setActionBarTitle(getString(R.string.competition_help_video_title, providerDTO.name));
        }
    }

    @SuppressWarnings("unused")
    @OnItemClick(R.id.help_videos_list)
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        HelpVideoDTO videoDTO = (HelpVideoDTO) adapterView.getItemAtPosition(position);
        openVideo(videoDTO);
        ClipData clip = ClipData.newPlainText("Video id", String.format("providerId:%d, videoId:%d", videoDTO.providerId, videoDTO.id));
        clipboardManager.setPrimaryClip(clip);
    }

    protected void openVideo(@NonNull HelpVideoDTO helpVideoDTO)
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

    @SuppressWarnings("unused")
    @OnItemLongClick(R.id.help_videos_list)
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        String videoUrl = ((HelpVideoDTO) parent.getItemAtPosition(position)).videoUrl;
        ClipData clip = ClipData.newPlainText(getString(R.string.settings_primary_referral_code), videoUrl);
        clipboardManager.setPrimaryClip(clip);
        THToast.show(getString(R.string.referral_code_copied_clipboard, videoUrl));
        return true;
    }
}
