package com.androidth.general.fragments.contestcenter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderDTOList;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.competition.key.ProviderListKey;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.common.widget.BetterViewAnimator;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.fragments.competition.CompetitionWebViewFragment;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.fragments.web.BaseWebViewIntentFragment;
import com.androidth.general.models.intent.THIntent;
import com.androidth.general.models.intent.THIntentPassedListener;
import com.androidth.general.models.intent.competition.ProviderIntent;
import com.androidth.general.models.intent.competition.ProviderPageIntent;
import com.androidth.general.persistence.competition.ProviderListCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCompactListCacheRx;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public abstract class ContestCenterBaseFragment extends DashboardFragment
{
    @Inject ProviderListCacheRx providerListCache;
    @Inject PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject CurrentUserId currentUserId;
    @Inject ProviderUtil providerUtil;

    @Bind(R.id.contest_center_content_screen) BetterViewAnimator contest_center_content_screen;
    @Bind(android.R.id.list) ListView contestListView;

    public ContestItemAdapter contestListAdapter;
    @IdRes private int currentDisplayedChildLayoutId;
    public ProviderDTOList providerDTOs;
    private BaseWebViewIntentFragment webFragment;
    private THIntentPassedListener thIntentPassedListener;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.thIntentPassedListener = new LeaderboardCommunityTHIntentPassedListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.contest_center_content_screen, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onStart()
    {
        super.onStart();
        contestListView.setOnScrollListener(fragmentElements.get().getListViewScrollListener());
        if (currentDisplayedChildLayoutId != 0)
        {
            setContestCenterScreen(currentDisplayedChildLayoutId);
        }
        loadContestData();
    }

    @Override public void onResume()
    {
        super.onResume();
        detachWebFragment();
        loadContestData();
    }

    @Override public void onStop()
    {
        currentDisplayedChildLayoutId = contest_center_content_screen.getDisplayedChildLayoutId();
        contestListView.setOnScrollListener(null);
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.thIntentPassedListener = null;
        detachWebFragment();
        super.onDestroy();
    }

    private void loadContestData()
    {
        // get the data
        fetchProviderIdList();
    }

    private void fetchProviderIdList()
    {
        //onStopSubscriptions.add(AppObservable.bindSupportFragment(this, providerListCache.get(new ProviderListKey()))
        //        .observeOn(AndroidSchedulers.mainThread())
        //        .subscribe(
        //                new Action1<Pair<ProviderListKey, ProviderDTOList>>()
        //                {
        //                    @Override public void call(Pair<ProviderListKey, ProviderDTOList> pair)
        //                    {
        //                        providerDTOs = pair.second;
        //                        sortProviderByVip();
        //                        recreateAdapter();
        //                    }
        //                },
        //                new Action1<Throwable>()
        //                {
        //                    @Override public void call(Throwable throwable)
        //                    {
        //                        setContestCenterScreen(R.id.error);
        //                        THToast.show(getString(R.string.error_fetch_provider_info_list));
        //                        Timber.e("Failed retrieving the list of competition providers", throwable);
        //                    }
        //                }));

        onStopSubscriptions.add(AppObservable.bindSupportFragment(this, providerListCache.fetch(new ProviderListKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                new Action1<ProviderDTOList>()
                {
                    @Override public void call(ProviderDTOList latestProviderDTOs)
                    {
                        providerDTOs = latestProviderDTOs;
                        sortProviderByVip();
                        recreateAdapter();
                    }
                },
                new Action1<Throwable>()
                {
                    @Override public void call(Throwable throwable)
                    {
                        setContestCenterScreen(R.id.error);
                        THToast.show(getString(R.string.error_fetch_provider_info_list));
                        Timber.e("Failed retrieving the list of competition providers", throwable);
                    }
                }));
    }

    /** make sure vip provider is in the front of the list */
    private void sortProviderByVip()
    {
        if (providerDTOs == null) return;
        Collections.sort(providerDTOs, new Comparator<ProviderDTO>()
        {
            @Override public int compare(ProviderDTO lhs, ProviderDTO rhs)
            {
                if (lhs.vip == null ? rhs.vip == null : lhs.vip.equals(rhs.vip))
                {
                    return 0;
                }
                else if (lhs.vip == null)
                {
                    return -1;
                }
                else if (rhs.vip == null)
                {
                    return 1;
                }
                else
                {
                    return lhs.vip ? 1 : -1;
                }
            }
        });
    }

    protected ContestItemAdapter createAdapter()
    {
        return new ContestItemAdapter(
                getActivity(),
                R.layout.contest_competition_item_view,
                R.layout.contest_content_item_view);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.error)
    protected void handleErrorClicked()
    {
        setContestCenterScreen(R.id.progress);
        loadContestData();
    }

    public void setContestCenterScreen(@IdRes int viewId)
    {
        if (contest_center_content_screen != null)
        {
            contest_center_content_screen.setDisplayedChildByLayoutId(viewId);
        }
    }

    private void detachWebFragment()
    {
        if (this.webFragment != null)
        {
            this.webFragment.setThIntentPassedListener(null);
        }
        this.webFragment = null;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(android.R.id.list)
    public void onContestListItemClick(AdapterView<?> adapterView, View view, int position, long id)
    {
        Object item = adapterView.getItemAtPosition(position);
        if (item instanceof ProviderContestPageDTO)
        {
            handleCompetitionItemClicked(((ProviderContestPageDTO) item).providerDTO);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled item type " + item);
        }
    }

    private void handleCompetitionItemClicked(ProviderDTO providerDTO)
    {
        if (navigator == null)
        {
            return;
        }
        if (providerDTO != null && providerDTO.isUserEnrolled)
        {
            Bundle args = new Bundle();
            MainCompetitionFragment.putProviderId(args, providerDTO.getProviderId());
            navigator.get().pushFragment(MainCompetitionFragment.class, args);
        }
        else if (providerDTO != null)
        {
            // HACK Just in case the user eventually enrolls
            portfolioCompactListCache.invalidate(currentUserId.toUserBaseKey());
            Bundle args = new Bundle();
            CompetitionWebViewFragment.putUrl(args, providerUtil.getLandingPage(
                    providerDTO.getProviderId()
            ));
            webFragment = navigator.get().pushFragment(CompetitionWebViewFragment.class, args);
            webFragment.setThIntentPassedListener(thIntentPassedListener);
            setActionBarTitle("");
            setActionBarColor(providerDTO.hexColor);
            setActionBarImage(providerDTO.navigationLogoUrl);
        }
    }
    private boolean setActionBarImage(String url){
        try {
            ActionBar actionBar = getSupportActionBar();
            ImageView imageView = new ImageView(getContext());
            Observable<Bitmap> observable = Observable.defer(()->{
                try {
                    return Observable.just(Picasso.with(getContext()).load(url).get());
                } catch (IOException e) {
                    e.printStackTrace();
                    return Observable.error(e);
                }
            });

            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(bitmap -> {
                int height = (int)(actionBar.getHeight()*0.6);
                int bitmapHt = bitmap.getHeight();
                int bitmapWd = bitmap.getWidth();
                int width = height * (bitmapWd / bitmapHt);
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
                imageView.setImageBitmap(bitmap);
                ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.MATCH_PARENT, Gravity.CENTER);
                actionBar.setCustomView(imageView, layoutParams);
                actionBar.setElevation(5);
                actionBar.setDisplayOptions(actionBar.getDisplayOptions() | ActionBar.DISPLAY_SHOW_CUSTOM);
            }, throwable -> {
                Log.e("Error",""+throwable.getMessage());
            });

            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    protected class LeaderboardCommunityTHIntentPassedListener implements THIntentPassedListener
    {
        @Override public void onIntentPassed(THIntent thIntent)
        {
            Timber.d("LeaderboardCommunityTHIntentPassedListener " + thIntent);
            if (thIntent instanceof ProviderIntent)
            {
                // Just in case the user has enrolled
                portfolioCompactListCache.invalidate(currentUserId.toUserBaseKey());
            }

            if (thIntent instanceof ProviderPageIntent)
            {
                Timber.d("Intent is ProviderPageIntent");
                if (webFragment != null)
                {
                    Timber.d("Passing on %s", ((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                    webFragment.loadUrl(((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                }
                else
                {
                    Timber.d("WebFragment is null");
                }
            }
            else
            {
                Timber.w("Unhandled intent %s", thIntent);
            }
        }
    }

    abstract public void recreateAdapter();

    @NonNull public abstract ContestCenterTabType getCCTabType();
}
