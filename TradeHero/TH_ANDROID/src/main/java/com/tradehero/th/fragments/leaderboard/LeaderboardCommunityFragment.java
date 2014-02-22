package com.tradehero.th.fragments.leaderboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.squareup.picasso.Picasso;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdList;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

public class LeaderboardCommunityFragment extends BaseLeaderboardFragment
    implements WithTutorial
{
    private DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList> leaderboardDefFetchListener;
    protected DTOCache.GetOrFetchTask<LeaderboardDefListKey, LeaderboardDefKeyList> leaderboardDefListFetchTask;

    @Inject Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject Lazy<ProviderListCache> providerListCache;
    @Inject Lazy<ProviderCache> providerCache;
    @Inject Picasso picasso;
    @Inject CurrentUserId currentUserId;
    @Inject ProviderUtil providerUtil;

    @InjectView(R.id.community_screen) BetterViewAnimator communityScreen;
    @InjectView(android.R.id.list) StickyListHeadersListView leaderboardDefListView;

    private THIntentPassedListener thIntentPassedListener;
    private WebViewFragment webFragment;
    private LeaderboardCommunityAdapter leaderboardDefListAdapter;
    private AdapterView.OnItemClickListener leaderboardCommunityListOnClickListener;
    private int currentDisplayedChildLayoutId;
    private DTOCache.Listener<ProviderListKey, ProviderIdList> providerListCallback;
    private DTOCache.GetOrFetchTask<ProviderListKey, ProviderIdList> providerListFetchTask;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        leaderboardCommunityListOnClickListener = createOnItemClickListener();
        leaderboardDefFetchListener = createDefKeyListListener();
        providerListCallback = createProviderIdListListener();
    }

    private AdapterView.OnItemClickListener createOnItemClickListener()
    {
        return new AdapterView.OnItemClickListener()
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
    }

    private DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList> createDefKeyListListener()
    {
        return new DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList>()
        {
            @Override public void onDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value, boolean fromCache)
            {
                handleLeaderboardDefKeyListReceived();
            }

            @Override public void onErrorThrown(LeaderboardDefListKey key, Throwable error)
            {
                THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key));
                Timber.e("Error fetching the leaderboard def key list %s", key, error);
            }
        };
    }

    private DTOCache.Listener<ProviderListKey, ProviderIdList> createProviderIdListListener()
    {
        return new DTOCache.Listener<ProviderListKey, ProviderIdList>()
        {
            @Override public void onDTOReceived(ProviderListKey key, ProviderIdList value, boolean fromCache)
            {
                displayCompetitionProviders(value);
            }

            @Override public void onErrorThrown(ProviderListKey key, Throwable error)
            {
                THToast.show(getString(R.string.error_fetch_provider_info_list));
                Timber.e("Failed retrieving the list of competition providers", error);
            }
        };
    }

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
    }

    @Override public void onResume()
    {
        super.onResume();

        // show either progress bar or def list, whichever last seen on this screen
        if (currentDisplayedChildLayoutId != 0)
        {
            communityScreen.setDisplayedChildByLayoutId(currentDisplayedChildLayoutId);
        }

        // prepare adapter for this screen
        prepareAdapters();

        // get the data
        if (providerListFetchTask != null)
        {
            providerListFetchTask.setListener(null);
        }
        providerListFetchTask = providerListCache.get().getOrFetch(new ProviderListKey(), providerListCallback);
        providerListFetchTask.execute();

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
        currentDisplayedChildLayoutId = communityScreen.getDisplayedChildLayoutId();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
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

        leaderboardDefListFetchTask = leaderboardDefListCache.get().getOrFetch(
                LeaderboardDefListKey.getCommunity(), leaderboardDefFetchListener);
        leaderboardDefListFetchTask.execute();
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

    @Override public void onDestroyView()
    {
        this.thIntentPassedListener = null;

        if (leaderboardDefListView != null)
        {
            leaderboardDefListView.setOnItemClickListener(null);
            leaderboardDefListView = null;
        }

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        leaderboardCommunityListOnClickListener = null;
        leaderboardDefFetchListener = null;
        providerListCallback = null;

        super.onDestroy();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setTitle(getString(R.string.leaderboard_community_leaderboards));
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

    private void displayCompetitionProviders(List<ProviderId> providerIds)
    {
        leaderboardDefListAdapter.setCompetitionItems(providerIds);
    }

    private void handleCompetitionItemClicked(ProviderDTO providerDTO)
    {
        if (providerDTO != null && providerDTO.isUserEnrolled)
        {
            Bundle args = new Bundle();
            args.putBundle(MainCompetitionFragment.BUNDLE_KEY_PROVIDER_ID, providerDTO.getProviderId().getArgs());
            OwnedPortfolioId associatedPortfolioId =
                    new OwnedPortfolioId(currentUserId.toUserBaseKey(), providerDTO.associatedPortfolio);
            args.putBundle(MainCompetitionFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, associatedPortfolioId.getArgs());
            getNavigator().pushFragment(MainCompetitionFragment.class, args);
        }
        else if (providerDTO != null)
        {
            Bundle args = new Bundle();
            args.putString(WebViewFragment.BUNDLE_KEY_URL, providerUtil.getLandingPage(
                    providerDTO.getProviderId(),
                    currentUserId.toUserBaseKey()));
            args.putBoolean(WebViewFragment.BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, false);
            webFragment = (WebViewFragment) getNavigator().pushFragment(WebViewFragment.class, args);
            webFragment.setThIntentPassedListener(thIntentPassedListener);
        }
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_leaderboard_community;
    }

    private class LeaderboardCommunityTHIntentPassedListener implements THIntentPassedListener
    {
        @Override public void onIntentPassed(THIntent thIntent)
        {
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

    private void handleLeaderboardDefKeyListReceived()
    {
        communityScreen.setDisplayedChildByLayoutId(android.R.id.list);
        leaderboardDefListAdapter.notifyDataSetChanged();
    }

    //<editor-fold desc="Navigation">
    private void pushLeaderboardDefSector()
    {
        Bundle bundle = new Bundle(getArguments());
        (LeaderboardDefListKey.getSector()).putParameters(bundle);
        bundle.putString(LeaderboardDefListFragment.BUNDLE_KEY_LEADERBOARD_DEF_TITLE, getString(R.string.leaderboard_community_sector));
        getNavigator().pushFragment(LeaderboardDefListFragment.class, bundle);
    }

    private void pushLeaderboardDefExchange()
    {
        Bundle bundle = new Bundle(getArguments());
        (LeaderboardDefListKey.getExchange()).putParameters(bundle);
        bundle.putString(LeaderboardDefListFragment.BUNDLE_KEY_LEADERBOARD_DEF_TITLE, getString(R.string.leaderboard_community_exchange));
        getNavigator().pushFragment(LeaderboardDefListFragment.class, bundle);
    }
    //</editor-fold>

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>
}
