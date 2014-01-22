package com.tradehero.th.fragments.leaderboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.tradehero.th.api.leaderboard.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.LeaderboardDefExchangeListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefKeyList;
import com.tradehero.th.api.leaderboard.LeaderboardDefListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefMostSkilledListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefSectorListKey;
import com.tradehero.th.api.leaderboard.LeaderboardDefTimePeriodListKey;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.competition.ProviderListRetrievedMilestone;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefCache;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;

public class LeaderboardCommunityFragment extends BaseLeaderboardFragment
        implements DTOCache.Listener<LeaderboardDefListKey, LeaderboardDefKeyList>
{
    private static final String TAG = LeaderboardCommunityFragment.class.getName();

    @Inject protected Lazy<LeaderboardDefListCache> leaderboardDefListCache;
    @Inject protected Lazy<LeaderboardDefCache> leaderboardDefCache;
    @Inject protected Lazy<ProviderListCache> providerListCache;
    @Inject protected Lazy<ProviderCache> providerCache;
    @Inject protected Picasso picasso;
    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

    private boolean fetched = false;
    private ImageButton firstProviderLink;
    private ListView mostSkilledListView;
    private ListView timePeriodListView;
    private ListView sectorListView;
    private LeaderboardDefListAdapter mostSkilledListAdapter;
    private LeaderboardDefListAdapter timePeriodListAdapter;

    @Inject protected ProviderListRetrievedMilestone providerListRetrievedMilestone;
    private Milestone.OnCompleteListener providerListRetrievedListener;
    private ProviderDTO firstProvider;
    private THIntentPassedListener thIntentPassedListener;
    private WebViewFragment webFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.leaderboard_community_screen, container, false);
        initViews(view);
        return view;
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        this.providerListRetrievedListener = new LeaderboardCommunityFragmentProviderListRetrievedListener();
        this.providerListRetrievedMilestone.setOnCompleteListener(this.providerListRetrievedListener);
    }

    @Override public void onStart()
    {
        super.onStart();
        this.providerListRetrievedMilestone.launch();
    }

    @Override public void onResume()
    {
        super.onResume();
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
        sectorListView.setAdapter(null);
    }

    private void prepareAdapters()
    {
        leaderboardDefListCache.get().getOrFetch(new LeaderboardDefMostSkilledListKey(), false, this).execute();
    }

    private void initViews(View view)
    {
        this.firstProviderLink = (ImageButton) view.findViewById(R.id.btn_first_provider);
        if (this.firstProviderLink != null)
        {
            this.firstProviderLink.setOnClickListener(new LeaderboardCommunityFragmentProviderLinkClickListener());
        }
        mostSkilledListView = (ListView) view.findViewById(R.id.leaderboard_most_skilled);
        timePeriodListView = (ListView) view.findViewById(R.id.leaderboard_time_period);
        sectorListView = (ListView) view.findViewById(R.id.leaderboard_sector);

        ListView[] listViews = new ListView[] { mostSkilledListView, timePeriodListView, sectorListView };
        for (ListView listView: listViews)
        {
            listView.setEmptyView(view.findViewById(android.R.id.empty));
        }

        this.thIntentPassedListener = new LeaderboardCommunityTHIntentPassedListener();
    }

    @Override public void onStop()
    {
        super.onStop();
        fetched = false;
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
        if (this.firstProviderLink != null)
        {
            this.firstProviderLink.setOnClickListener(null);
        }
        this.firstProviderLink = null;

        this.thIntentPassedListener = null;

        super.onDestroyView();
    }

    private AdapterView.OnItemClickListener createLeaderboardItemClickListener()
    {
        return new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                LeaderboardDefDTO dto = (LeaderboardDefDTO) adapterView.getAdapter().getItem(position);

                if (dto != null)
                {
                    pushLeaderboardListViewFragment(dto);
                }
            }
        };
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        int defaultSortFlags =
                //LeaderboardSortType.HeroQuotient.getFlag() |
                LeaderboardSortType.Roi.getFlag();
        getArguments().putInt(LeaderboardSortType.BUNDLE_FLAG, defaultSortFlags);

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

    //<editor-fold desc="DTOCache Listeners">
    @Override public void onDTOReceived(LeaderboardDefListKey key, LeaderboardDefKeyList value)
    {
        // hide loading
        if (!fetched)
        {
            fetched = true;
            leaderboardDefListCache.get().getOrFetch(new LeaderboardDefTimePeriodListKey(), false, this).execute();
            leaderboardDefListCache.get().getOrFetch(new LeaderboardDefSectorListKey(), false, this).execute();
            leaderboardDefListCache.get().getOrFetch(new LeaderboardDefExchangeListKey(), false, this).execute();
        }

        if (value != null)
        {
            if (key instanceof LeaderboardDefMostSkilledListKey)
            {
                initMostSkilledListView(key, value);
            }
            else if (key instanceof LeaderboardDefTimePeriodListKey)
            {
                initTimePeriodListView(key, value);
            }
            else if (key instanceof LeaderboardDefSectorListKey && (value.size() > 0))
            {
                LeaderboardDefDTO sectorDto = initDefaultLeaderboardDefDTOForSector();
                addItemToSectorSection(sectorDto);
            }
            else if (key instanceof LeaderboardDefExchangeListKey)
            {
                LeaderboardDefDTO sectorDto = initDefaultLeaderboardDefDTOForExchange();
                addItemToSectorSection(sectorDto);
            }
        }
    }

    @Override public void onErrorThrown(LeaderboardDefListKey key, Throwable error)
    {
        THToast.show(getString(R.string.error_fetch_leaderboard_def_list_key));
        THLog.e(TAG, "Error fetching the leaderboard def key list " + key, error);
    }
    //</editor-fold>

    //<editor-fold desc="Init some default LeaderboardDefDTOs - Hardcoded">
    private LeaderboardDefDTO initDefaultLeaderboardDefDTOForExchange()
    {
        LeaderboardDefDTO dto = new LeaderboardDefDTO();
        dto.id = LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID;
        dto.name = getString(R.string.leaderboard_by_exchange);
        return dto;
    }

    private LeaderboardDefDTO initDefaultLeaderboardDefDTOForSector()
    {
        LeaderboardDefDTO dto = new LeaderboardDefDTO();
        dto.id = LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID;
        dto.name = getString(R.string.leaderboard_by_sector);
        return dto;
    }
    //</editor-fold>

    //<editor-fold desc="ListView adapters creation">
    private void addItemToSectorSection(LeaderboardDefDTO dto)
    {
        if (sectorListView.getAdapter() == null)
        {
            List<LeaderboardDefDTO> sectorDefDTOs = new ArrayList<>();
            sectorListView.setAdapter(
                    new LeaderboardDefListAdapter(getActivity(), getActivity().getLayoutInflater(), sectorDefDTOs, R.layout.leaderboard_def_item));
        }
        LeaderboardDefListAdapter sectorListViewAdapter = (LeaderboardDefListAdapter) sectorListView.getAdapter();
        sectorListViewAdapter.addItem(dto);
        sectorListViewAdapter.notifyDataSetChanged();
        sectorListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                LeaderboardDefDTO dto = (LeaderboardDefDTO) adapterView.getItemAtPosition(position);
                if (dto != null)
                {
                    switch (dto.id)
                    {
                        case LeaderboardDefDTO.LEADERBOARD_DEF_SECTOR_ID:
                        {
                            Bundle bundle = new Bundle(getArguments());
                            (new LeaderboardDefSectorListKey()).putParameters(bundle);
                            bundle.putString(LeaderboardDefListViewFragment.TITLE, getString(R.string.leaderboard_sector));
                            bundle.putInt(BaseLeaderboardFragment.CURRENT_SORT_TYPE, getCurrentSortType().getFlag());
                            getNavigator().pushFragment(LeaderboardDefListViewFragment.class, bundle);
                        } break;
                        case LeaderboardDefDTO.LEADERBOARD_DEF_EXCHANGE_ID:
                        {
                            Bundle bundle = new LeaderboardDefExchangeListKey().getArgs();
                            bundle.putString(LeaderboardDefListViewFragment.TITLE, getString(R.string.leaderboard_exchange));
                            bundle.putInt(BaseLeaderboardFragment.CURRENT_SORT_TYPE, getCurrentSortType().getFlag());
                            getNavigator().pushFragment(LeaderboardDefListViewFragment.class, bundle);
                        } break;
                    }
                }
            }
        });
    }

    private LeaderboardDefListAdapter createTimePeriodListAdapter(List<LeaderboardDefDTO> timePeriodItems)
    {
        // sort time period items by number of days
        Collections.sort(timePeriodItems, new Comparator<LeaderboardDefDTO>()
        {
            @Override public int compare(LeaderboardDefDTO lhs, LeaderboardDefDTO rhs)
            {
                if (lhs == rhs) return 0;
                else if (lhs == null) return -1;
                else if (rhs == null) return 1;
                else if (lhs.toDateDays == null) return -1;
                else if (rhs.toDateDays == null) return 1;
                else if (lhs.toDateDays.equals(rhs.toDateDays)) return 0;
                else return (lhs.toDateDays > rhs.toDateDays) ? 1 : -1;
            }
        });
        return new LeaderboardDefListAdapter(
                getActivity(), getActivity().getLayoutInflater(), timePeriodItems, R.layout.leaderboard_def_item);
    }

    private LeaderboardDefMostSkilledListAdapter createMostSkilledListAdapter(List<LeaderboardDefDTO> values)
    {
        return new LeaderboardDefMostSkilledListAdapter(
                getActivity(), getActivity().getLayoutInflater(), values, R.layout.leaderboard_def_item);
    }
    //</editor-fold>

    //<editor-fold desc="ListViews creation">
    private void initMostSkilledListView(LeaderboardDefListKey key, LeaderboardDefKeyList value)
    {
        try
        {
            mostSkilledListAdapter = createMostSkilledListAdapter(leaderboardDefCache.get().getOrFetch(value));
            mostSkilledListView.setAdapter(mostSkilledListAdapter);
        }
        catch (Throwable throwable)
        {
            onErrorThrown(key, throwable);
        }
        mostSkilledListView.setOnItemClickListener(createLeaderboardItemClickListener());
    }

    private void initTimePeriodListView(LeaderboardDefListKey key, LeaderboardDefKeyList value)
    {
        try
        {
            timePeriodListAdapter = createTimePeriodListAdapter(leaderboardDefCache.get().getOrFetch(value));
            timePeriodListView.setAdapter(timePeriodListAdapter);
        }
        catch (Throwable throwable)
        {
            onErrorThrown(key, throwable);
        }
        timePeriodListView.setOnItemClickListener(createLeaderboardItemClickListener());
    }
    //</editor-fold>

    //<editor-fold desc="Sorting">
    @Override protected void initSortTypeFromArguments()
    {
        // I'm the leaderboard master screen, one can change my sort type :))
    }

    @Override protected void onCurrentSortTypeChanged()
    {
        if (timePeriodListAdapter != null)
        {
            timePeriodListAdapter.setSortType(getCurrentSortType());
            timePeriodListAdapter.notifyDataSetChanged();
        }

        if (mostSkilledListAdapter != null)
        {
            mostSkilledListAdapter.setSortType(getCurrentSortType());
            mostSkilledListAdapter.notifyDataSetChanged();
        }
    }
    //</editor-fold>

    private void displayFirstCompetitionProvider(List<ProviderId> providerIds)
    {
        if (providerIds != null && providerIds.size() > 0)
        {
            displayFirstProviderButton(this.providerCache.get().get(providerIds.get(0)));
        }
    }

    private void displayFirstProviderButton(ProviderDTO providerDTO)
    {
        this.firstProvider = providerDTO;
        if (firstProvider != null && firstProviderLink != null)
        {
            firstProviderLink.setVisibility(View.VISIBLE);
            this.picasso.load(firstProvider.getStatusSingleImageUrl()).into(firstProviderLink);
        }
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>

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

    private class LeaderboardCommunityFragmentProviderLinkClickListener implements View.OnClickListener
    {
        @Override public void onClick(View view)
        {
            if (firstProvider != null && firstProvider.isUserEnrolled)
            {
                Bundle args = new Bundle();
                args.putBundle(MainCompetitionFragment.BUNDLE_KEY_PROVIDER_ID, firstProvider.getProviderId().getArgs());
                OwnedPortfolioId associatedPortfolioId = new OwnedPortfolioId(currentUserBaseKeyHolder.getCurrentUserBaseKey(), firstProvider.associatedPortfolio);
                args.putBundle(MainCompetitionFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, associatedPortfolioId.getArgs());
                navigator.pushFragment(MainCompetitionFragment.class, args);
            }
            else if (firstProvider != null)
            {
                Bundle args = new Bundle();
                args.putString(WebViewFragment.BUNDLE_KEY_URL, ProviderConstants.getLandingPage(
                        firstProvider.getProviderId(),
                        currentUserBaseKeyHolder.getCurrentUserBaseKey()));
                webFragment = (WebViewFragment) navigator.pushFragment(WebViewFragment.class, args);
                webFragment.setThIntentPassedListener(thIntentPassedListener);
            }
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
}
