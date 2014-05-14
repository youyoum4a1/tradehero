package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.api.competition.ProviderIdList;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.ProviderSecurityListFragment;
import com.tradehero.th.fragments.security.SecurityListFragment;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.fragments.settings.InviteFriendFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSelectorView;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeBasicDTO;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTO;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTOFactory;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.models.market.ExchangeDTODescriptionNameComparator;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.market.ExchangeListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import timber.log.Timber;

public class TrendingFragment extends SecurityListFragment
    implements WithTutorial
{
    public static final String BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO = TrendingFragment.class.getName() + ".trendingFilterTypeDTO";
    public final static int SECURITY_ID_LIST_LOADER_ID = 2532;

    @Inject TrendingFilterTypeDTOFactory trendingFilterTypeDTOFactory;
    @Inject Lazy<ExchangeListCache> exchangeListCache;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<ProviderCache> providerCache;
    @Inject Lazy<ProviderListCache> providerListCache;
    @Inject CurrentUserId currentUserId;
    @Inject ProviderUtil providerUtil;
    @Inject THLocalyticsSession localyticsSession;
    @Inject Lazy<ResideMenu> resideMenuLazy;

    private TrendingFilterSelectorView filterSelectorView;
    private TrendingOnFilterTypeChangedListener onFilterTypeChangedListener;
    private TrendingFilterTypeDTO trendingFilterTypeDTO;

    private DTOCache.Listener<ExchangeListType, ExchangeDTOList> exchangeListTypeCacheListener;
    private DTOCache.GetOrFetchTask<ExchangeListType, ExchangeDTOList> exchangeListCacheFetchTask;

    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileFetchListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileFetchTask;

    private ExtraTileAdapter wrapperAdapter;
    private DTOCache.Listener<ProviderListKey, ProviderIdList> providerListCallback;
    private DTOCache.GetOrFetchTask<ProviderListKey, ProviderIdList> providerListFetchTask;
    private BaseWebViewFragment webFragment;
    private THIntentPassedListener thIntentPassedListener;
    private Set<Integer> enrollmentScreenOpened = new HashSet<>();

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Saved instance takes precedence
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO))
        {
            this.trendingFilterTypeDTO = this.trendingFilterTypeDTOFactory.create(savedInstanceState.getBundle(BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO));
        }
        else if (getArguments() != null && getArguments().containsKey(BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO))
        {
            this.trendingFilterTypeDTO = this.trendingFilterTypeDTOFactory.create(getArguments().getBundle(BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO));
        }
        else
        {
            this.trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO();
        }

        createExchangeListTypeCacheListener();

        userProfileFetchListener = new UserProfileFetchListener();
        providerListCallback = new ProviderListFetchListener();
    }

    private void createExchangeListTypeCacheListener()
    {
        exchangeListTypeCacheListener =
                new DTOCache.Listener<ExchangeListType, ExchangeDTOList>()
                {
                    @Override public void onDTOReceived(ExchangeListType key, ExchangeDTOList value, boolean fromCache)
                    {
                        Timber.d("Filter exchangeListTypeCacheListener onDTOReceived");
                        linkWith(value, true);
                    }

                    @Override public void onErrorThrown(ExchangeListType key, Throwable error)
                    {
                        THToast.show(getString(R.string.error_fetch_exchange_list_info));
                        Timber.e("Error fetching the list of exchanges %s", key, error);
                    }
                };
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        super.initViews(view);

        this.onFilterTypeChangedListener = new TrendingOnFilterTypeChangedListener();
        this.filterSelectorView = (TrendingFilterSelectorView) view.findViewById(R.id.trending_filter_selector_view);
        if (this.filterSelectorView != null)
        {
            this.filterSelectorView.apply(this.trendingFilterTypeDTO);
            this.filterSelectorView.setChangedListener(this.onFilterTypeChangedListener);
        }

        thIntentPassedListener = new CompetitionTHIntentPassedListener();
        fetchExchangeList();
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.TabBar_Trade);

        // fetch user
        detachUserFetchTask();
        userProfileFetchTask = userProfileCache.get().getOrFetch(currentUserId.toUserBaseKey(), false, userProfileFetchListener);
        userProfileFetchTask.execute();

        // fetch provider list for provider tile

        detachProviderListTask();
        providerListFetchTask = providerListCache.get().getOrFetch(new ProviderListKey(), providerListCallback);
        providerListFetchTask.execute();

        //update gridView's top padding if filterSelectorView is higher
        filterSelectorView.postDelayed(new Runnable()
        {
            @Override public void run()
            {
                if (filterSelectorView != null)
                {
                    AbsListView listView = getSecurityListView();
                    int height = filterSelectorView.getHeight();
                    if (listView != null && height > 0)
                    {
                        getSecurityListView().setPadding((int) getResources().getDimension(
                                R.dimen.trending_list_padding_left_and_right),
                                height > 103 ? height + 10 : (int) getResources().getDimension(
                                        R.dimen.trending_list_padding_top),
                                (int) getResources().getDimension(
                                        R.dimen.trending_list_padding_left_and_right),
                                (int) getResources().getDimension(
                                        R.dimen.trending_list_padding_bottom));
                    }
                }
            }
        }, 500);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //THLog.i(TAG, "onCreateOptionsMenu");
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE
                | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_USE_LOGO);
        actionBar.setTitle(R.string.trending_header);
        actionBar.setLogo(R.drawable.icon_menu);
        actionBar.setHomeButtonEnabled(true);
        inflater.inflate(R.menu.menu_search_button, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_search:
                pushSearchIn();
                return true;
            case android.R.id.home:
                resideMenuLazy.get().openMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyView()
    {
        detachExchangeListFetchTask();
        detachProviderListTask();
        detachExchangeListFetchTask();
        detachUserFetchTask();
        this.onFilterTypeChangedListener = null;

        if (filterSelectorView != null)
        {
            filterSelectorView.setChangedListener(null);
        }
        filterSelectorView = null;

        super.onDestroyView();
    }

    private void detachProviderListTask()
    {
        if (providerListFetchTask != null)
        {
            providerListFetchTask.setListener(null);
            providerListFetchTask = null;
        }
    }

    private void detachUserFetchTask()
    {
        if (userProfileFetchTask != null)
        {
            userProfileFetchTask.setListener(null);
        }
        userProfileFetchTask = null;
    }

    protected void detachExchangeListFetchTask()
    {
        if (exchangeListCacheFetchTask != null)
        {
            exchangeListCacheFetchTask.setListener(null);
        }
        exchangeListCacheFetchTask = null;
    }

    @Override public void onDestroy()
    {
        exchangeListTypeCacheListener = null;
        userProfileFetchListener = null;
        thIntentPassedListener = null;
        providerListCallback = null;
        super.onDestroy();
    }

    @Override protected OnItemClickListener createOnItemClickListener()
    {
        return new OnSecurityViewClickListener();
    }

    @Override protected ListAdapter createSecurityItemViewAdapter()
    {
        SimpleSecurityItemViewAdapter simpleSecurityItemViewAdapter =
                new SimpleSecurityItemViewAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.trending_security_item);

        //return simpleSecurityItemViewAdapter;
        // use above adapter to disable extra tile on the trending screen
        wrapperAdapter = new ExtraTileAdapter(getActivity(), simpleSecurityItemViewAdapter);
        return wrapperAdapter;
    }

    @Override public int getSecurityIdListLoaderId()
    {
        return SECURITY_ID_LIST_LOADER_ID;
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        Timber.d("onSaveInstanceState");
        super.onSaveInstanceState(outState);
        if (this.trendingFilterTypeDTO != null)
        {
            outState.putBundle(BUNDLE_KEY_TRENDING_FILTER_TYPE_DTO, this.trendingFilterTypeDTO.getArgs());
        }
    }

    private void fetchExchangeList()
    {
        detachExchangeListFetchTask();
        exchangeListCacheFetchTask = exchangeListCache.get().getOrFetch(new ExchangeListType(), exchangeListTypeCacheListener);
        exchangeListCacheFetchTask.execute();
    }

    private void linkWith(ExchangeDTOList exchangeDTOs, boolean andDisplay)
    {
        Timber.d("Filter linkWith linkWith");
        if (filterSelectorView != null && exchangeDTOs != null)
        {
            // We keep only those included in Trending and order by desc / name
            List<ExchangeDTO> exchangeDTOList = new ArrayList<>();
            for (ExchangeDTO exchangeDTO: exchangeDTOs)
            {
                if (exchangeDTO.isIncludedInTrending)
                {
                    exchangeDTOList.add(exchangeDTO);
                }
            }
            Collections.sort(exchangeDTOList, new ExchangeDTODescriptionNameComparator());


            filterSelectorView.setUpExchangeSpinner(exchangeDTOList);
            filterSelectorView.apply(trendingFilterTypeDTO);
        }
    }

    @Override public TrendingSecurityListType getSecurityListType(int page)
    {
        return trendingFilterTypeDTO.getSecurityListType(getUsableExchangeName(), page, perPage);
    }

    protected String getUsableExchangeName()
    {
        if (trendingFilterTypeDTO != null && trendingFilterTypeDTO.exchange != null && trendingFilterTypeDTO.exchange.name != null)
        {
            return trendingFilterTypeDTO.exchange.name;
        }
        return TrendingSecurityListType.ALL_EXCHANGES;
    }

    public void pushSearchIn()
    {
        Bundle args = new Bundle();
        args.putString(SearchStockPeopleFragment.BUNDLE_KEY_RESTRICT_SEARCH_TYPE, TrendingSearchType.STOCKS.name());
        getNavigator().pushFragment(SearchStockPeopleFragment.class, args);
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="Listeners">
    private class OnSecurityViewClickListener implements OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Object item = parent.getItemAtPosition(position);
            View child = parent.getChildAt(position - parent.getFirstVisiblePosition());
            if (item instanceof SecurityCompactDTO)
            {
                handleSecurityItemOnClick((SecurityCompactDTO) item);
            }
            else if (item instanceof TileType)
            {
                handleExtraTileItemOnClick((TileType) item, child);
            }
        }
    }

    private void handleExtraTileItemOnClick(TileType item, View view)
    {
        switch (item)
        {
            case EarnCredit:
                handleEarnCreditItemOnClick();
                break;
            case ExtraCash:
                handleExtraCashItemOnClick();
                break;
            case ResetPortfolio:
                handleResetPortfolioItemOnClick();
                break;
            case Survey:
                handleSurveyItemOnClick();
                break;
            case FromProvider:
                handleProviderTileOnClick(view);
                break;
        }
    }

    private void handleProviderTileOnClick(View view)
    {
        if (view instanceof ProviderTileView)
        {
            int providerId = ((ProviderTileView) view).getProviderId();
            ProviderDTO providerDTO = providerCache.get().get(new ProviderId(providerId));
            switch (providerId)
            {
                case ProviderIdConstants.PROVIDER_ID_PHILLIP_MACQUARIE_WARRANTS:
                    handleCompetitionItemClicked(providerDTO);
                    break;
                case ProviderIdConstants.PROVIDER_ID_MACQUARIE_WARRANTS:
                    Timber.d("PROVIDER_ID_MACQUARIE_WARRANTS");
                    break;
            }
        }
    }

    private void handleCompetitionItemClicked(ProviderDTO providerDTO)
    {
        if (providerDTO != null && providerDTO.isUserEnrolled)
        {
            Bundle args = new Bundle();
            args.putBundle(ProviderSecurityListFragment.BUNDLE_KEY_PROVIDER_ID, providerDTO.getProviderId().getArgs());
            ProviderSecurityListFragment.putApplicablePortfolioId(args, providerDTO.getAssociatedOwnedPortfolioId(currentUserId.toUserBaseKey()));
            getNavigator().pushFragment(ProviderSecurityListFragment.class, args);
        }
        else if (providerDTO != null)
        {
            Bundle args = new Bundle();
            args.putString(CompetitionWebViewFragment.BUNDLE_KEY_URL, providerUtil.getLandingPage(
                    providerDTO.getProviderId(),
                    currentUserId.toUserBaseKey()));
            args.putBoolean(CompetitionWebViewFragment.BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, true);
            webFragment = (BaseWebViewFragment) getNavigator().pushFragment(CompetitionWebViewFragment.class, args);
            webFragment.setThIntentPassedListener(thIntentPassedListener);
        }
    }

    private void handleSurveyItemOnClick()
    {
        UserProfileDTO userProfileDTO = userProfileCache.get().get(currentUserId.toUserBaseKey());
        if (userProfileDTO != null && userProfileDTO.activeSurveyURL != null)
        {
            Bundle bundle = new Bundle();
            bundle.putString(WebViewFragment.BUNDLE_KEY_URL, userProfileDTO.activeSurveyURL);
            getNavigator().pushFragment(WebViewFragment.class, bundle, Navigator.PUSH_UP_FROM_BOTTOM);
        }
    }

    private void handleResetPortfolioItemOnClick()
    {
        if (userInteractor != null)
        {
            // TODO
            //userInteractor.conditionalPopBuyResetPortfolio();
        }
    }

    private void handleExtraCashItemOnClick()
    {
        if (userInteractor != null)
        {
            // TODO
            //userInteractor.conditionalPopBuyVirtualDollars();
        }
    }

    private void handleEarnCreditItemOnClick()
    {
        getNavigator().pushFragment(InviteFriendFragment.class);
    }

    private void handleSecurityItemOnClick(SecurityCompactDTO securityCompactDTO)
    {
        localyticsSession.tagEvent(LocalyticsConstants.TrendingStock,
                securityCompactDTO.getSecurityId());
        Bundle args = new Bundle();
        args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityCompactDTO.getSecurityId().getArgs());
        getNavigator().pushFragment(BuySellFragment.class, args);
    }

    private class TrendingOnFilterTypeChangedListener implements TrendingFilterSelectorView.OnFilterTypeChangedListener
    {
        @Override public void onFilterTypeChanged(TrendingFilterTypeDTO trendingFilterTypeDTO)
        {
            Timber.d("Filter onFilterTypeChanged");
            if (trendingFilterTypeDTO == null)
            {
                Timber.e(new IllegalArgumentException("onFilterTypeChanged trendingFilterTypeDTO cannot be null"), "onFilterTypeChanged trendingFilterTypeDTO cannot be null");
            }
            TrendingFragment.this.trendingFilterTypeDTO = trendingFilterTypeDTO;
            // TODO
            forceInitialLoad();
        }
    }
    //</editor-fold>

    @Override protected void handleSecurityItemReceived(SecurityIdList securityIds)
    {
        if (securityItemViewAdapter != null)
        {
            // It may have been nullified if coming out
            securityItemViewAdapter.setItems(securityCompactCache.get().get(securityIds));
            refreshAdapterWithTiles(false);
        }
    }

    private void refreshAdapterWithTiles(boolean refreshTileTypes)
    {
        // TODO hack, experience some synchronization matter here, generateExtraTiles should be call inside wrapperAdapter
        // when data is changed
        // Note that this is just to minimize the chance of happening, need synchronize the data changes inside super class DTOAdapter
        if (wrapperAdapter != null)
        {
            wrapperAdapter.regenerateExtraTiles(false, refreshTileTypes);
        }

        if (securityItemViewAdapter != null)
        {
            securityItemViewAdapter.notifyDataSetChanged();
        }
    }

    private class UserProfileFetchListener implements DTOCache.Listener<UserBaseKey,UserProfileDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
        {
            Timber.d("Retrieve user with surveyUrl=%s", value.activeSurveyImageURL);
            refreshAdapterWithTiles(value.activeSurveyImageURL != null);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_user_profile);
        }
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_trending_screen;
    }

    private class ProviderListFetchListener implements DTOCache.Listener<ProviderListKey,ProviderIdList>
    {
        @Override public void onDTOReceived(ProviderListKey key, ProviderIdList value, boolean fromCache)
        {
            refreshAdapterWithTiles(true);
            openEnrollmentPageIfNecessary(value);
        }

        @Override public void onErrorThrown(ProviderListKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_provider_competition_list);
        }
    }

    private void openEnrollmentPageIfNecessary(ProviderIdList providerIds)
    {
        for (ProviderId providerId: providerIds)
        {
            final ProviderDTO providerDTO = providerCache.get().get(providerId);
            if (providerDTO != null && enrollmentScreenOpened != null && !providerDTO.isUserEnrolled && !enrollmentScreenOpened.contains(providerId.key))
            {
                enrollmentScreenOpened.add(providerId.key);
                postIfCan(new Runnable()
                {
                    @Override public void run()
                    {
                        if (!isDetached())
                        {
                            handleCompetitionItemClicked(providerDTO);
                        }
                    }
                });
                return;
            }
        }
    }

    private class CompetitionTHIntentPassedListener implements THIntentPassedListener
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
}
