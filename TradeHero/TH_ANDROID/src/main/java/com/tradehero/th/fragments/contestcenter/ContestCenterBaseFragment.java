package com.tradehero.th.fragments.contestcenter;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderIntent;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import dagger.Lazy;
import java.util.Collections;
import java.util.Comparator;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

public abstract class ContestCenterBaseFragment extends DashboardFragment
{
    @Inject Lazy<ProviderListCacheRx> providerListCache;
    @Inject protected PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject CurrentUserId currentUserId;
    @Inject ProviderUtil providerUtil;

    @InjectView(R.id.contest_center_content_screen) BetterViewAnimator contest_center_content_screen;
    @InjectView(android.R.id.list) StickyListHeadersListView contestListView;

    public ContestItemAdapter contestListAdapter;
    @IdRes private int currentDisplayedChildLayoutId;
    public ProviderDTOList providerDTOs;
    private BaseWebViewFragment webFragment;
    private THIntentPassedListener thIntentPassedListener;
    protected UserProfileDTO currentUserProfileDTO;

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
        ButterKnife.inject(this, view);
        return view;
    }

    protected Observer<Pair<ProviderListKey, ProviderDTOList>> createProviderIdListObserver()
    {
        return new LeaderboardCommunityProviderListObserver();
    }

    protected class LeaderboardCommunityProviderListObserver implements Observer<Pair<ProviderListKey, ProviderDTOList>>
    {
        @Override public void onNext(Pair<ProviderListKey, ProviderDTOList> pair)
        {
            providerDTOs = pair.second;
            sortProviderByVip();
            recreateAdapter();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            handleFailToReceiveLeaderboardDefKeyList();
            THToast.show(getString(R.string.error_fetch_provider_info_list));
            Timber.e("Failed retrieving the list of competition providers", e);
        }
    }

    /** make sure vip provider is in the front of the list */
    private void sortProviderByVip()
    {
        if (providerDTOs == null) return;
        Collections.sort(providerDTOs, new Comparator<ProviderDTO>()
        {
            @Override public int compare(ProviderDTO lhs, ProviderDTO rhs)
            {
                if (lhs.vip == rhs.vip)
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

    /**
     * TODO to show user detail of the error
     */
    private void handleFailToReceiveLeaderboardDefKeyList()
    {
        setContestCenterScreen(R.id.error);
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

    private void loadContestData()
    {
        // get the data
        fetchProviderIdList();
    }

    private void fetchProviderIdList()
    {
        AndroidObservable.bindFragment(this, providerListCache.get().get(new ProviderListKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createProviderIdListObserver());
    }

    @Override public void onStart()
    {
        super.onStart();
        contestListView.setOnItemClickListener(createItemClickListener());
        contestListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
        if (currentDisplayedChildLayoutId != 0)
        {
            setContestCenterScreen(currentDisplayedChildLayoutId);
        }
    }

    @Override public void onStop()
    {
        currentDisplayedChildLayoutId = contest_center_content_screen.getDisplayedChildLayoutId();
        contestListView.setOnItemClickListener(null);
        contestListView.setOnScrollListener(null);
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.thIntentPassedListener = null;
        detachWebFragment();
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
        loadContestData();
        detachWebFragment();
    }

    private void detachWebFragment()
    {
        if (this.webFragment != null)
        {
            this.webFragment.setThIntentPassedListener(null);
        }
        this.webFragment = null;
    }

    protected AdapterView.OnItemClickListener createItemClickListener()
    {
        return new ContestOnItemClickListener();
    }

    protected class ContestOnItemClickListener implements AdapterView.OnItemClickListener
    {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
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
                    providerDTO.getProviderId(),
                    currentUserId.toUserBaseKey()));
            CompetitionWebViewFragment.putIsOptionMenuVisible(args, true);
            webFragment = navigator.get().pushFragment(CompetitionWebViewFragment.class, args);
            webFragment.setThIntentPassedListener(thIntentPassedListener);
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

    @NotNull public abstract ContestCenterTabType getCCTabType();
}
