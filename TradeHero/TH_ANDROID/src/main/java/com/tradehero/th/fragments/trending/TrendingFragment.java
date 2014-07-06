package com.tradehero.th.fragments.trending;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import butterknife.InjectView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.thoj.route.Routable;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderIdConstants;
import com.tradehero.th.api.competition.ProviderIdList;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.market.ExchangeCompactDTODescriptionNameComparator;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeCompactDTOUtil;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.TrendingSecurityListType;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.security.SecurityListFragment;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSelectorView;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeBasicDTO;
import com.tradehero.th.fragments.trending.filter.TrendingFilterTypeDTO;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTOList;
import com.tradehero.th.models.time.AppTiming;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.market.ExchangeCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import dagger.Lazy;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

@Routable("trending-securities")
public class TrendingFragment extends SecurityListFragment
        implements WithTutorial
{
    public final static int SECURITY_ID_LIST_LOADER_ID = 2532;

    @Inject Lazy<ExchangeCompactListCache> exchangeCompactListCache;
    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<ProviderCache> providerCache;
    @Inject Lazy<ProviderListCache> providerListCache;
    @Inject CurrentUserId currentUserId;
    @Inject ProviderUtil providerUtil;
    @Inject THLocalyticsSession localyticsSession;
    @Inject ExchangeCompactDTOUtil exchangeCompactDTOUtil;
    @Inject UserBaseDTOUtil userBaseDTOUtil;

    @InjectView(R.id.trending_filter_selector_view) protected TrendingFilterSelectorView filterSelectorView;

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private UserProfileDTO userProfileDTO;

    private DTOCacheNew.Listener<ExchangeListType, ExchangeCompactDTOList> exchangeListTypeCacheListener;
    private ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOs;
    private boolean defaultFilterSelected;
    @NotNull private TrendingFilterTypeDTO trendingFilterTypeDTO;

    private ExtraTileAdapter wrapperAdapter;
    private DTOCacheNew.Listener<ProviderListKey, ProviderIdList> providerListCallback;
    private BaseWebViewFragment webFragment;
    private THIntentPassedListener thIntentPassedListener;
    private final Set<Integer> enrollmentScreenOpened = new HashSet<>();
    private Runnable handleCompetitionRunnable;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO(getActivity().getResources());
        defaultFilterSelected = false;

        exchangeListTypeCacheListener = createExchangeListTypeFetchListener();
        userProfileCacheListener = createUserProfileFetchListener();
        providerListCallback = createProviderListFetchListener();
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

        if (this.filterSelectorView != null)
        {
            this.filterSelectorView.apply(this.trendingFilterTypeDTO);
            this.filterSelectorView.setChangedListener(createTrendingFilterChangedListener());
        }

        thIntentPassedListener = createCompetitionTHIntentPassedListener();
        fetchExchangeList();
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.TabBar_Trade);

        // fetch user
        detachUserProfileCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());

        // fetch provider list for provider tile
        fetchProviderList();

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
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(R.string.trending_header);
        inflater.inflate(R.menu.search_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_search:
                pushSearchIn();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStop()
    {
        detachExchangeListCache();
        detachProviderListTask();
        detachExchangeListCache();
        detachUserProfileCache();
        removeCallbacksIfCan(handleCompetitionRunnable);

        super.onStop();
    }

    @Override public void onDestroyView()
    {
        filterSelectorView.setChangedListener(null);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        handleCompetitionRunnable = null;
        exchangeListTypeCacheListener = null;
        userProfileCacheListener = null;
        thIntentPassedListener = null;
        providerListCallback = null;
        super.onDestroy();
    }

    private void detachProviderListTask()
    {
        providerListCache.get().unregister(providerListCallback);
    }

    private void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    protected void detachExchangeListCache()
    {
        if (exchangeListTypeCacheListener != null)
        {
            exchangeCompactListCache.get().unregister(exchangeListTypeCacheListener);
        }
    }

    protected void fetchProviderList()
    {
        detachProviderListTask();
        providerListCache.get().register(new ProviderListKey(), providerListCallback);
        providerListCache.get().getOrFetchAsync(new ProviderListKey());
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

    private void fetchExchangeList()
    {
        detachExchangeListCache();
        ExchangeListType key = new ExchangeListType();
        exchangeCompactListCache.get().register(key, exchangeListTypeCacheListener);
        exchangeCompactListCache.get().getOrFetchAsync(key);
    }

    private void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        setUpFilterSelectorView();
        refreshAdapterWithTiles(userProfileDTO.activeSurveyImageURL != null);
    }

    private void linkWith(@NotNull ExchangeCompactDTOList exchangeDTOs, boolean andDisplay)
    {
        linkWith(new ExchangeCompactSpinnerDTOList(
                        getResources(),
                        exchangeCompactDTOUtil.filterAndOrderForTrending(
                                exchangeDTOs,
                                new ExchangeCompactDTODescriptionNameComparator<>())),
                andDisplay);
    }

    private void linkWith(@NotNull ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOs, boolean andDisplay)
    {
        this.exchangeCompactSpinnerDTOs = exchangeCompactSpinnerDTOs;
        setUpFilterSelectorView();
    }

    private void setUpFilterSelectorView()
    {
        setDefaultExchange();
        if (filterSelectorView != null)
        {
            if (exchangeCompactSpinnerDTOs != null)
            {
                filterSelectorView.setUpExchangeSpinner(exchangeCompactSpinnerDTOs);
            }
            filterSelectorView.apply(trendingFilterTypeDTO);
        }
    }

    private void setDefaultExchange()
    {
        if (!defaultFilterSelected)
        {
            if (userProfileDTO != null && exchangeCompactSpinnerDTOs != null)
            {
                ExchangeCompactSpinnerDTO initial = userBaseDTOUtil.getInitialExchange(userProfileDTO, exchangeCompactSpinnerDTOs);
                if (initial != null)
                {
                    trendingFilterTypeDTO.exchange = initial;
                    defaultFilterSelected = true;
                }
            }
        }
    }

    @Override @NotNull public TrendingSecurityListType getSecurityListType(int page)
    {
        return trendingFilterTypeDTO.getSecurityListType(trendingFilterTypeDTO.exchange.getApiName(), page, perPage);
    }

    public void pushSearchIn()
    {
        Bundle args = new Bundle();
        getDashboardNavigator().pushFragment(SecuritySearchFragment.class, args);
    }

    //<editor-fold desc="Listeners">
    @Override protected OnItemClickListener createOnItemClickListener()
    {
        return new OnSecurityViewClickListener();
    }

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
                case ProviderIdConstants.PROVIDER_ID_MACQUARIE_WARRANTS:
                    Timber.d("PROVIDER_ID_MACQUARIE_WARRANTS");
                    break;
                default:
                    handleCompetitionItemClicked(providerDTO);
                    break;
            }
        }
    }

    private void handleCompetitionItemClicked(ProviderDTO providerDTO)
    {
        if (providerDTO != null && providerDTO.isUserEnrolled)
        {
            Bundle args = new Bundle();
            MainCompetitionFragment.putProviderId(args, providerDTO.getProviderId());
            MainCompetitionFragment.putApplicablePortfolioId(args, providerDTO.getAssociatedOwnedPortfolioId(currentUserId.toUserBaseKey()));
            getDashboardNavigator().pushFragment(MainCompetitionFragment.class, args);
        }
        else if (providerDTO != null)
        {
            Bundle args = new Bundle();
            args.putString(CompetitionWebViewFragment.BUNDLE_KEY_URL, providerUtil.getLandingPage(
                    providerDTO.getProviderId(),
                    currentUserId.toUserBaseKey()));
            args.putBoolean(CompetitionWebViewFragment.BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, true);
            webFragment = getDashboardNavigator().pushFragment(CompetitionWebViewFragment.class, args);
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
            getDashboardNavigator().pushFragment(WebViewFragment.class, bundle, Navigator.PUSH_UP_FROM_BOTTOM, null);
        }
    }

    private void handleResetPortfolioItemOnClick()
    {
        cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO);
    }

    private void handleExtraCashItemOnClick()
    {
        cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR);
    }

    private void handleEarnCreditItemOnClick()
    {
        getDashboardNavigator().pushFragment(FriendsInvitationFragment.class);
    }

    private void handleSecurityItemOnClick(SecurityCompactDTO securityCompactDTO)
    {
        localyticsSession.tagEvent(LocalyticsConstants.TrendingStock,
                securityCompactDTO.getSecurityId());
        Bundle args = new Bundle();
        args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityCompactDTO.getSecurityId().getArgs());

        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();

        if (ownedPortfolioId != null)
        {
            BuySellFragment.putApplicablePortfolioId(args, ownedPortfolioId);
        }

        getDashboardNavigator().pushFragment(BuySellFragment.class, args);
    }

    protected TrendingFilterSelectorView.OnFilterTypeChangedListener createTrendingFilterChangedListener()
    {
        return new TrendingOnFilterTypeChangedListener();
    }

    protected class TrendingOnFilterTypeChangedListener implements TrendingFilterSelectorView.OnFilterTypeChangedListener
    {
        @Override public void onFilterTypeChanged(TrendingFilterTypeDTO trendingFilterTypeDTO)
        {
            Timber.d("Filter onFilterTypeChanged");
            if (trendingFilterTypeDTO == null)
            {
                Timber.e(new IllegalArgumentException("onFilterTypeChanged trendingFilterTypeDTO cannot be null"),
                        "onFilterTypeChanged trendingFilterTypeDTO cannot be null");
            }
            TrendingFragment.this.trendingFilterTypeDTO = trendingFilterTypeDTO;
            // TODO
            forceInitialLoad();
        }
    }
    //</editor-fold>

    @Override protected void handleSecurityItemReceived(SecurityIdList securityIds)
    {
        if (AppTiming.trendingFilled == 0)
        {
            AppTiming.trendingFilled = System.currentTimeMillis();
        }
        //Timber.d("handleSecurityItemReceived "+securityIds.toString());
        if (securityItemViewAdapter != null)
        {
            // It may have been nullified if coming out
            securityItemViewAdapter.setItems(securityCompactCache.get().get(securityIds));
            refreshAdapterWithTiles(false);
        }

        Timber.d("splash %d, dash %d, trending %d",
                AppTiming.splashCreate - AppTiming.appCreate,
                AppTiming.dashboardCreate - AppTiming.splashCreate,
                AppTiming.trendingFilled - AppTiming.dashboardCreate);
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

    //<editor-fold desc="Exchange List Listener">
    protected DTOCacheNew.Listener<ExchangeListType, ExchangeCompactDTOList> createExchangeListTypeFetchListener()
    {
        return new TrendingExchangeListTypeFetchListener();
    }

    protected class TrendingExchangeListTypeFetchListener implements DTOCacheNew.Listener<ExchangeListType, ExchangeCompactDTOList>
    {
        @Override public void onDTOReceived(ExchangeListType key, ExchangeCompactDTOList value)
        {
            Timber.d("Filter exchangeListTypeCacheListener onDTOReceived");
            linkWith(value, true);
        }

        @Override public void onErrorThrown(ExchangeListType key, Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_exchange_list_info));
            Timber.e("Error fetching the list of exchanges %s", key, error);
        }
    }
    //</editor-fold>

    //<editor-fold desc="User Profile Listener">
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileFetchListener()
    {
        return new TrendingUserProfileFetchListener();
    }

    protected class TrendingUserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
        {
            Timber.d("Retrieve user with surveyUrl=%s", value.activeSurveyImageURL);
            linkWith(value, true);
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_user_profile);
        }
    }
    //</editor-fold>

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_trending_screen;
    }

    //<editor-fold desc="Provider List Listener">
    protected DTOCacheNew.Listener<ProviderListKey, ProviderIdList> createProviderListFetchListener()
    {
        return new TrendingProviderListFetchListener();
    }

    protected class TrendingProviderListFetchListener implements DTOCacheNew.Listener<ProviderListKey, ProviderIdList>
    {
        @Override public void onDTOReceived(ProviderListKey key, ProviderIdList value)
        {
            refreshAdapterWithTiles(true);
            openEnrollmentPageIfNecessary(value);
        }

        @Override public void onErrorThrown(ProviderListKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_provider_competition_list);
        }
    }
    //</editor-fold>

    private void openEnrollmentPageIfNecessary(ProviderIdList providerIds)
    {
        for (ProviderId providerId : providerIds)
        {
            final ProviderDTO providerDTO = providerCache.get().get(providerId);
            if (providerDTO != null && enrollmentScreenOpened != null && !providerDTO.isUserEnrolled && !enrollmentScreenOpened.contains(
                    providerId.key))
            {
                enrollmentScreenOpened.add(providerId.key);

                removeCallbacksIfCan(handleCompetitionRunnable);
                handleCompetitionRunnable = createHandleCompetitionRunnable(providerDTO);
                postIfCan(handleCompetitionRunnable);
                return;
            }
        }
    }

    //<editor-fold desc="Competition Runnable">
    private Runnable createHandleCompetitionRunnable(ProviderDTO providerDTO)
    {
        return new TrendingFragmentHandleCompetitionRunnable(providerDTO);
    }

    private class TrendingFragmentHandleCompetitionRunnable implements Runnable
    {
        private final ProviderDTO providerDTO;

        private TrendingFragmentHandleCompetitionRunnable(ProviderDTO providerDTO)
        {
            this.providerDTO = providerDTO;
        }

        @Override public void run()
        {
            if (!isDetached())
            {
                handleCompetitionItemClicked(providerDTO);
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="Intent Listener">
    protected THIntentPassedListener createCompetitionTHIntentPassedListener()
    {
        return new CompetitionTHIntentPassedListener();
    }

    protected class CompetitionTHIntentPassedListener implements THIntentPassedListener
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
    //</editor-fold>
}
