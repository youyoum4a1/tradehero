package com.tradehero.th.fragments.leaderboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ViewAnimator;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderConstants;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefCommunityListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.competition.ProviderListRetrievedMilestone;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class LeaderboardCommunityFragment extends BaseLeaderboardFragment
{
    private static final String TAG = LeaderboardCommunityFragment.class.getName();

    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;

    protected DTOCache.GetOrFetchTask<LeaderboardDefListKey, LeaderboardDefKeyList> leaderboardDefListFetchTask;

    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject protected Lazy<ProviderListCache> providerListCache;
    @Inject protected Lazy<ProviderCache> providerCache;
    @Inject protected Picasso picasso;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    @InjectView(R.id.community_screen) ViewAnimator communityScreen;
    @InjectView(android.R.id.list) StickyListHeadersListView leaderboardDefListView;
    @Inject protected ProviderListRetrievedMilestone providerListRetrievedMilestone;

    private Milestone.OnCompleteListener providerListRetrievedListener;
    private THIntentPassedListener thIntentPassedListener;
    private WebViewFragment webFragment;
    private LeaderboardCommunityAdapter leaderboardDefListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_community_screen, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        this.providerListRetrievedListener = new LeaderboardCommunityFragmentProviderListRetrievedListener();
        this.providerListRetrievedMilestone.setOnCompleteListener(this.providerListRetrievedListener);
    }

    @Override public void onResume()
    {
        super.onResume();

        this.providerListRetrievedMilestone.launch();
        prepareAdapters();
        // We came back into view so we have to forget the web fragment
        if (this.webFragment != null)
        {
            this.webFragment.setThIntentPassedListener(null);
        }
        this.webFragment = null;
    }

    @Override public void onPause()
    {
        super.onPause();
        leaderboardDefListView.setAdapter(null);
    }

    private void detachLeaderboardDefListCacheFetchMostSkilledTask()
    {
        if (leaderboardDefListFetchTask != null)
        {
            leaderboardDefListFetchTask.setListener(null);
        }
        leaderboardDefListFetchTask = null;
    }

    private void prepareAdapters()
    {
        detachLeaderboardDefListCacheFetchMostSkilledTask();

        if (leaderboardDefListCache.get().get(new LeaderboardDefCommunityListKey()) == null)
        {
            leaderboardDefListFetchTask = leaderboardDefListCache.get().getOrFetch(
                    new LeaderboardDefCommunityListKey(), false, leaderboardDefFetchListener);
            leaderboardDefListFetchTask.execute();
        }
        else
        {
            handleLeaderboardDefKeyListReceived();
        }
    }

    @Override protected void initViews(View view)
    {
        // list of leaderboard definition item
        leaderboardDefListAdapter = new LeaderboardCommunityAdapter(
                getActivity(), getActivity().getLayoutInflater(),
                R.layout.leaderboard_definition_item_view, R.layout.leaderboard_competition_item_view);

        leaderboardDefListView.setAdapter(leaderboardDefListAdapter);
        leaderboardDefListView.setOnItemClickListener(leaderboardCommunityListOnClickListener);

        this.thIntentPassedListener = new LeaderboardCommunityTHIntentPassedListener();
    }

    @Override public void onStop()
    {
        super.onStop();
        detachLeaderboardDefListCacheFetchMostSkilledTask();
    }

    @Override public void onDetach()
    {
        this.providerListRetrievedListener = null;
        if (this.providerListRetrievedMilestone != null)
        {
            this.providerListRetrievedMilestone.setOnCompleteListener(null);
        }
        this.providerListRetrievedMilestone = null;
        super.onDetach();
    }

    @Override public void onDestroyView()
    {
        this.thIntentPassedListener = null;

        if (leaderboardDefListView != null)
        {
            leaderboardDefListView.setOnItemClickListener(null);
        }
        leaderboardDefListView = null;

        super.onDestroyView();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        int defaultSortFlags =
                //LeaderboardSortType.HeroQuotient.getFlag() |
                LeaderboardSortType.Roi.getFlag();
        getArguments().putInt(BUNDLE_KEY_SORT_OPTION_FLAGS, defaultSortFlags);

        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setTitle(getString(R.string.leaderboards));
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO switch sorting type for leaderboard
        switch (item.getItemId())
        {
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    //<editor-fold desc="Sorting">
    @Override protected void initSortTypeFromArguments()
    {
        // I'm the leaderboard master screen, one can change my sort type :))
    }

    @Override protected void onCurrentSortTypeChanged()
    {
    }
    //</editor-fold>

    private void displayFirstCompetitionProvider(List<ProviderId> providerIds)
    {
        leaderboardDefListAdapter.setCompetitionItems(providerIds);
    }
    private class LeaderboardCommunityFragmentProviderListRetrievedListener implements Milestone.OnCompleteListener
    {
        @Override public void onComplete(Milestone milestone)
        {
            displayFirstCompetitionProvider(providerListCache.get().get(new ProviderListKey()));
        }

        @Override public void onFailed(Milestone milestone, Throwable throwable)
        {
            THToast.show(getString(R.string.error_fetch_provider_info_list));
            THLog.e(TAG, "Failed retrieving the list of competition providers", throwable);
        }
    }

    private void handleCompetitionItemClicked(ProviderDTO providerDTO)
    {
        if (providerDTO != null && providerDTO.isUserEnrolled)
        {
            Bundle args = new Bundle();
            args.putBundle(MainCompetitionFragment.BUNDLE_KEY_PROVIDER_ID, providerDTO.getProviderId().getArgs());
            OwnedPortfolioId associatedPortfolioId =
                    new OwnedPortfolioId(currentUserBaseKeyHolder.getCurrentUserBaseKey(), providerDTO.associatedPortfolio);
            args.putBundle(MainCompetitionFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, associatedPortfolioId.getArgs());
            navigator.pushFragment(MainCompetitionFragment.class, args);
        }
        else if (providerDTO != null)
        {
            Bundle args = new Bundle();
            args.putString(WebViewFragment.BUNDLE_KEY_URL, ProviderConstants.getLandingPage(
                    providerDTO.getProviderId(),
                    currentUserBaseKeyHolder.getCurrentUserBaseKey()));
            webFragment = (WebViewFragment) navigator.pushFragment(WebViewFragment.class, args);
            webFragment.setThIntentPassedListener(thIntentPassedListener);
        }
    }

    private class LeaderboardCommunityTHIntentPassedListener implements THIntentPassedListener
    {
        @Override public void onIntentPassed(THIntent thIntent)
        {
            if (thIntent instanceof ProviderPageIntent)
            {
                THLog.d(TAG, "Intent is ProviderPageIntent");
                if (webFragment != null)
                {
                    THLog.d(TAG, "Passing on " + ((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                    webFragment.loadUrl(((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                }
                else
                {
                    THLog.d(TAG, "WebFragment is null");
                }
            }
            else
            {
                THLog.w(TAG, "Unhandled intent " + thIntent);
            }
        }
    }

    private AdapterView.OnItemClickListener leaderboardCommunityListOnClickListener = new AdapterView.OnItemClickListener()
    {
        @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
        {
            Object item = adapterView.getItemAtPosition(position);
            if (item instanceof LeaderboardDefKey)
            {
                LeaderboardDefDTO dto = leaderboardDefCache.get().get((LeaderboardDefKey) item);
                if (dto != null)
                {
                    switch (dto.id)
                    {
                        case LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID:
                            pushLeaderboardDefSector();
                            break;
                        case LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID:
                            pushLeaderboardDefExchange();
                            break;
                        default:
                            pushLeaderboardListViewFragment(dto);
                            break;
                    }
                }
            }
            if (item instanceof ProviderId)
            {
                handleCompetitionItemClicked(providerCache.get().get((ProviderId) item));
            }
        }
    };


    private DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList> leaderboardDefFetchListener = new DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList>()
    {
        @Override public void onDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value)
        {
            handleLeaderboardDefKeyListReceived();
        }

        @Override public void onErrorThrown(LeaderboardDefListKey key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key));
            THLog.e(TAG, "Error fetching the leaderboard def key list " + key, error);
        }
    };

    private void handleLeaderboardDefKeyListReceived()
    {
        communityScreen.setDisplayedChild(1);
        leaderboardDefListAdapter.notifyDataSetChanged();
    }

    //<editor-fold desc="Navigation">
    private void pushLeaderboardDefSector()
    {
        Bundle bundle = new Bundle(getArguments());
        (new LeaderboardDefListKey()).putParameters(bundle);
        bundle.putString(LeaderboardDefListViewFragment.BUNDLE_KEY_LEADERBOARD_DEF_TITLE, getString(R.string.leaderboard_sector));
        bundle.putInt(BaseLeaderboardFragment.BUNDLE_KEY_CURRENT_SORT_TYPE, getCurrentSortType().getFlag());
        getNavigator().pushFragment(LeaderboardDefListViewFragment.class, bundle);
    }

    private void pushLeaderboardDefExchange()
    {
        Bundle bundle = new LeaderboardDefListKey().getArgs();
        bundle.putString(LeaderboardDefListViewFragment.BUNDLE_KEY_LEADERBOARD_DEF_TITLE, getString(R.string.leaderboard_exchange));
        bundle.putInt(BaseLeaderboardFragment.BUNDLE_KEY_CURRENT_SORT_TYPE, getCurrentSortType().getFlag());
        getNavigator().pushFragment(LeaderboardDefListViewFragment.class, bundle);
    }
    //</editor-fold>

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>
}
