package com.androidth.general.fragments.competition;

import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
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
import android.widget.Button;
import android.widget.ProgressBar;

import com.androidth.general.R;
import com.androidth.general.activities.DashboardActivity;
import com.androidth.general.fragments.DashboardTabHost;
import com.androidth.general.api.competition.AdDTO;
import com.androidth.general.api.competition.CompetitionDTOList;
import com.androidth.general.api.competition.CompetitionPreSeasonDTO;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderDisplayCellDTOList;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.ProviderPrizePoolDTO;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.api.competition.key.BasicProviderSecurityV2ListType;
import com.androidth.general.api.competition.key.ProviderDisplayCellListKey;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.security.SecurityCompositeDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.OnMovableBottomTranslateListener;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.fragments.competition.zone.AbstractCompetitionZoneListItemView;
import com.androidth.general.fragments.competition.zone.CompetitionZoneLegalMentionsView;
import com.androidth.general.fragments.competition.zone.CompetitionZonePrizePoolView;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneAdvertisementDTO;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneDTO;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneDTOUtil;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneDisplayCellDTO;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneLeaderboardDTO;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneLegalDTO;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZonePortfolioDTO;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZonePreSeasonDTO;
import com.androidth.general.fragments.competition.zone.dto.CompetitionZoneWizardDTO;
import com.androidth.general.fragments.leaderboard.CompetitionLeaderboardMarkUserRecyclerFragment;
import com.androidth.general.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.androidth.general.fragments.social.friend.FriendsInvitationFragment;
import com.androidth.general.fragments.web.BaseWebViewFragment;
import com.androidth.general.fragments.web.BaseWebViewIntentFragment;
import com.androidth.general.fragments.web.WebViewFragment;
import com.androidth.general.fragments.web.WebViewIntentFragment;
import com.androidth.general.models.intent.THIntent;
import com.androidth.general.models.intent.THIntentFactory;
import com.androidth.general.models.intent.THIntentPassedListener;
import com.androidth.general.models.security.ProviderTradableSecuritiesHelper;
import com.androidth.general.network.service.ProviderServiceWrapper;
import com.androidth.general.persistence.competition.CompetitionListCacheRx;
import com.androidth.general.persistence.competition.CompetitionPreseasonCacheRx;
import com.androidth.general.persistence.competition.MyProviderReferralCacheRx;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.competition.ProviderDisplayCellListCacheRx;
import com.androidth.general.persistence.security.SecurityCompositeListCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.utils.GraphicUtil;
import com.androidth.general.utils.broadcast.GAnalyticsProvider;
import com.androidth.general.utils.route.THRouter;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func7;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Routable({
        "providers/:providerId"
})

public class MainCompetitionFragment extends DashboardFragment
{
    private static final String BUNDLE_KEY_PROVIDER_ID = MainCompetitionFragment.class.getName() + ".providerId";
    private static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE =
            MainCompetitionFragment.class.getName() + ".purchaseApplicablePortfolioId";
    public static final String BUNDLE_KEY_ACTION_BAR_NAV_URL = MainCompetitionFragment.class.getName() + ".actionBarNavUrl";
    public static final String BUNDLE_KEY_ACTION_BAR_COLOR = MainCompetitionFragment.class.getName() + ".actionBarColor";

    @Bind(android.R.id.progress) ProgressBar progressBar;
    @Bind(R.id.competition_zone_list) AbsListView listView;
    @Bind(R.id.btn_trade_now) Button btnTradeNow;

    private CompetitionZoneListItemAdapter competitionZoneListItemAdapter;

    private THIntentPassedListener webViewTHIntentPassedListener;
    private BaseWebViewIntentFragment webViewFragment;

    @Inject ProviderCacheRx providerCache;
    @Inject THRouter thRouter;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject CompetitionListCacheRx competitionListCache;
    @Inject ProviderDisplayCellListCacheRx providerDisplayListCellCache;
    @Inject ProviderUtil providerUtil;
    @Inject CompetitionZoneDTOUtil competitionZoneDTOUtil;
    @Inject THIntentFactory thIntentFactory;
    @Inject CompetitionPreseasonCacheRx competitionPreSeasonCacheRx;
    @Inject ProviderServiceWrapper providerServiceWrapper;
    @Inject protected CurrentUserId currentUserId;
    @Inject MyProviderReferralCacheRx myProviderReferralCacheRx;
    ////TODO Change Analytics
    //@Inject Analytics analytics;

    @Inject protected SecurityCompositeListCacheRx securityCompositeListCacheRx;

    @RouteProperty("providerId") Integer routedProviderId;

    protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    public static ActionbarColor actionbarColor;
    protected UserProfileDTO userProfileDTO;
    protected CompetitionDTOList competitionDTOs;
    private ProviderDisplayCellDTOList providerDisplayCellDTOList;
    protected List<ProviderPrizePoolDTO> providerPrizePoolDTOs;
    private List<CompetitionPreSeasonDTO> competitionPreSeasonDTOs;
    private OwnedPortfolioId applicablePortfolioId;

    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
    }

    @NonNull public static ProviderId getProviderId(@NonNull Bundle args)
    {
        Bundle providerBundle = args.getBundle(BUNDLE_KEY_PROVIDER_ID);
        if (providerBundle == null)
        {
            throw new NullPointerException("ProviderId has to be passed");
        }
        return new ProviderId(providerBundle);
    }

    public static void putApplicablePortfolioId(@NonNull Bundle args, @NonNull OwnedPortfolioId ownedPortfolioId)
    {
        args.putBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
    }

    @Nullable private static OwnedPortfolioId getApplicablePortfolioId(@NonNull Bundle args)
    {
        Bundle portfolioBundle = args.getBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE);
        if (portfolioBundle != null)
        {
            return new OwnedPortfolioId(portfolioBundle);
        }
        return null;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {

        thRouter.inject(this);
        if (getArguments() != null && routedProviderId != null)
        {
            putProviderId(getArguments(), new ProviderId(routedProviderId));
        }
        super.onCreate(savedInstanceState);
        this.providerId = getProviderId(getArguments());
        this.webViewTHIntentPassedListener = new MainCompetitionWebViewTHIntentPassedListener();
        competitionZoneListItemAdapter = createAdapter();
        //TODO Change Analytics
        //analytics.fireEvent(new SingleAttributeEvent(AnalyticsConstants.Competition_Home, AnalyticsConstants.ProviderId, String.valueOf(providerId.key)));

        applicablePortfolioId = getApplicablePortfolioId(getArguments());

        if (getActivity() instanceof DashboardActivity) {
            DashboardActivity dashboardActivity = (DashboardActivity)getActivity();
            dashboardActivity.dashboardTabHost.animateHide();
        }
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_main_competition, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
//        this.progressBar.setVisibility(View.VISIBLE);
        this.listView.setOnScrollListener(fragmentElements.get().getListViewScrollListener());

        this.listView.setAdapter(this.competitionZoneListItemAdapter);
        competitionZoneDTOUtil.randomiseAd();
    }

    //<editor-fold desc="ActionBar">
    //View view;
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //view = getSupportActionBar().getCustomView();
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    //</editor-fold>

    @Override public void onStart()
    {
        super.onStart();
        fetchAdapterRequisite();
        registerAdapterUserActions();
    }

    @Override public void onResume()
    {
        super.onResume();
        // We came back into view so we have to forget the web fragment
        if (this.webViewFragment != null)
        {
            this.webViewFragment.setThIntentPassedListener(null);
        }
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(new OnMovableBottomTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                btnTradeNow.setTranslationY(y);
            }
        });
        this.webViewFragment = null;

//        Map<String, String> eventDetails = new HitBuilders.EventBuilder().setCategory("Action").setAction("Competition").build();
        GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.COMP_MAIN_PAGE);
    }

    @Override public void onPause()
    {
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(null);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        this.listView.setOnScrollListener(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.webViewTHIntentPassedListener = null;
        this.competitionZoneListItemAdapter.clear();
        this.competitionZoneListItemAdapter = null;
        //DashboardFragment.bundle = null;
        //getSupportActionBar().setCustomView(view);
        super.onDestroy();
    }

    protected CompetitionZoneListItemAdapter createAdapter()
    {
        if(providerDTO!=null){
            if(!providerDTO.canTradeNow) {
                //show semi live
                return new CompetitionZoneListItemAdapter(
                        getActivity(),
                        R.layout.competition_zone_item,
                        R.layout.competition_zone_ads,
                        R.layout.competition_zone_header,
                        R.layout.competition_zone_legal_mentions);
            }
        }

        return new CompetitionZoneListItemAdapter(
                getActivity(),
                R.layout.competition_zone_item,
                R.layout.competition_zone_ads,
                R.layout.competition_zone_header,
                R.layout.competition_zone_prize_pool,
                R.layout.competition_zone_portfolio,
                R.layout.competition_zone_leaderboard_item,
                R.layout.competition_zone_legal_mentions,
                R.layout.competition_zone_item);
    }

    private void fetchAdapterRequisite()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(this,
                Observable.combineLatest(
                        userProfileCache.get(currentUserId.toUserBaseKey())
                                .map(new PairGetSecond<UserBaseKey, UserProfileDTO>())
                                .startWith(Observable.just(userProfileDTO))
                                .onErrorReturn(throwable -> {
                                    if (userProfileDTO == null)
                                    {
                                        THToast.show(R.string.error_fetch_your_user_profile);
                                    }
                                    Timber.e("Error fetching the profile info", throwable);
                                    return userProfileDTO;
                                }),
                        providerCache.get(this.providerId)
                                .map(new PairGetSecond<ProviderId, ProviderDTO>())
                                .startWith(Observable.just(providerDTO))
                                .onErrorReturn(throwable -> {
                                    if (providerDTO == null)
                                    {
                                        THToast.show(R.string.error_fetch_provider_info);
                                    }
                                    return providerDTO;
                                }),
//                        competitionListCache.get(providerId)
//                                .map(new PairGetSecond<ProviderId, CompetitionDTOList>())
//                                .startWith(Observable.just(competitionDTOs))
//                                .onErrorReturn(throwable -> {
//                                    if (competitionDTOs == null)
//                                    {
//                                        THToast.show(getString(R.string.error_fetch_provider_competition_list));
//                                    }
//                                    Timber.e("Error fetching the list of competition info", throwable);
//                                    return competitionDTOs;
//                                }),
                        competitionListCache.fetch(providerId)
                                .startWith(Observable.just(competitionDTOs))
                                .onErrorReturn(throwable -> {
                                    if (competitionDTOs == null)
                                    {
                                        THToast.show(getString(R.string.error_fetch_provider_competition_list));
                                    }
                                    Timber.e("Error fetching the list of competition info", throwable);
                                    return competitionDTOs;
                                }),
                        providerDisplayListCellCache.get(new ProviderDisplayCellListKey(providerId))
                                .map(new PairGetSecond<ProviderDisplayCellListKey, ProviderDisplayCellDTOList>())
                                .startWith(Observable.just(providerDisplayCellDTOList))
                                .onErrorReturn(new Func1<Throwable, ProviderDisplayCellDTOList>()
                                {
                                    @Override public ProviderDisplayCellDTOList call(Throwable throwable)
                                    {
                                        if (providerDisplayCellDTOList == null)
                                        {
                                            THToast.show(getString(R.string.error_fetch_provider_competition_display_cell_list));
                                        }
                                        Timber.e("Error fetching the list of competition info cell", throwable);
                                        return providerDisplayCellDTOList;
                                    }
                                }),
                        competitionPreSeasonCacheRx.get(providerId)
                                .map(new PairGetSecond<ProviderId, CompetitionPreSeasonDTO>())
                                .map(competitionPreSeasonDTO -> Collections.singletonList(competitionPreSeasonDTO))
                                .startWith(Observable.just(competitionPreSeasonDTOs))
                                .onErrorReturn(throwable -> {
                                    if (!(throwable instanceof RetrofitError)
                                            || ((RetrofitError) throwable).getResponse() == null
                                            || ((RetrofitError) throwable).getResponse().getStatus() != 404)
                                    {
                                        Timber.e(throwable, "Failed fetching preseason for %s", providerId);
                                    }
                                    return Collections.emptyList();
                                }),
                        providerServiceWrapper.getProviderPrizePoolRx(providerId)
                                .map(providerPrizePoolDTO -> Collections.singletonList(providerPrizePoolDTO))
                                .startWith(Observable.just(providerPrizePoolDTOs))
                                .onErrorReturn(throwable -> {
                                    if (providerPrizePoolDTOs == null)
                                    {
                                        // When there is no prize pool, server returns HTTP404, which is a valid response
                                        boolean is404 = false;
                                        if (throwable instanceof RetrofitError)
                                        {
                                            Response response = ((RetrofitError) throwable).getResponse();
                                            is404 = response != null && response.getStatus() == 404;
                                        }
                                        if (!is404)
                                        {
//                                            THToast.show(getString(R.string.error_fetch_provider_prize_pool_info));
                                            Timber.e(throwable, "Error fetching the provider prize pool info");
                                        }
                                    }
                                    return Collections.emptyList();
                                }),
                        securityCompositeListCacheRx.get(new BasicProviderSecurityV2ListType(providerId))
                                .map(new PairGetSecond<BasicProviderSecurityV2ListType, SecurityCompositeDTO>()),
                        new Func7<UserProfileDTO,
                                                        ProviderDTO,
                                                        CompetitionDTOList,
                                                        ProviderDisplayCellDTOList,
                                                        List<CompetitionPreSeasonDTO>,
                                                        List<ProviderPrizePoolDTO>,
                                                        SecurityCompositeDTO,
                                                        List<Pair<Integer, CompetitionZoneDTO>>>()
                        {
                            @Override public List<Pair<Integer, CompetitionZoneDTO>> call(
                                    UserProfileDTO userProfileDTO,
                                    ProviderDTO providerDTO,
                                    CompetitionDTOList competitionDTOs,
                                    ProviderDisplayCellDTOList providerDisplayCellDTOs,
                                    List<CompetitionPreSeasonDTO> competitionPreSeasonDTOs,
                                    List<ProviderPrizePoolDTO> providerPrizePoolDTOs,
                                    SecurityCompositeDTO securityCompositeDTO)
                            {
                                MainCompetitionFragment.this.userProfileDTO = userProfileDTO;
                                MainCompetitionFragment.this.providerDTO = providerDTO;
                                MainCompetitionFragment.this.competitionDTOs = competitionDTOs;
                                MainCompetitionFragment.this.providerDisplayCellDTOList = providerDisplayCellDTOs;
                                MainCompetitionFragment.this.competitionPreSeasonDTOs = competitionPreSeasonDTOs;
                                MainCompetitionFragment.this.providerPrizePoolDTOs = providerPrizePoolDTOs;
                                if(providerDTO.canTradeNow){
                                    return competitionZoneDTOUtil.makeList(
                                            getActivity(),
                                            userProfileDTO,
                                            providerDTO,
                                            competitionDTOs,
                                            providerDisplayCellDTOs,
                                            competitionPreSeasonDTOs,
                                            providerPrizePoolDTOs);

                                }else{
                                    //show semi live
                                    return competitionZoneDTOUtil.makeList(
                                            getActivity(),
                                            providerDTO);
                                }

                            }
                        })
                .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        elements -> {
                            competitionZoneListItemAdapter.setNotifyOnChange(false);
                            competitionZoneListItemAdapter.setElements(elements);
                            competitionZoneListItemAdapter.setNotifyOnChange(true);
                            competitionZoneListItemAdapter.notifyDataSetChanged();
                            displayListView();
                            displayActionBarTitle();
                            displayTradeNowButton();
                        },
                        new TimberAndToastOnErrorAction1("Failed to get requisite")
                ));

        onStopSubscriptions.add(myProviderReferralCacheRx.get(providerId)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if(throwable!=null){
                            new TimberOnErrorAction1(throwable.getMessage());
                        }else{
                            new TimberOnErrorAction1("MainCompetitionFragment error");
                        }
                    }
                })
                .subscribe());
    }

    protected void displayListView()
    {
        Timber.d("displayListView %s %s %s %s", userProfileDTO, providerDTO, competitionDTOs, providerDisplayCellDTOList);
        if (providerDTO != null)
        {
            if (progressBar != null)
            {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void displayActionBarTitle()
    {
        if (this.providerDTO == null || this.providerDTO.name == null)
        {
            setActionBarTitle("");
        }
        else
        {
            setActionBarColor(providerDTO.hexColor);
            if(providerDTO.navigationLogoUrl!=null){
                setActionBarTitle("");
                setActionBarImage(this.providerDTO.navigationLogoUrl);
            }else{
                setActionBarTitle(providerDTO.name);
            }
        }
        /*if(providerDTO!=null){
            Bundle args = new Bundle();
            DashboardFragment.putUrl(args, providerDTO.navigationLogoUrl);
            DashboardFragment.putActionBarColor(args, providerDTO.hexColor);
            DashboardFragment.bundle = args;
        }*/
    }

    private void setActionBarImage(String url){
        setActionBarCustomImage(getActivity(), url, false);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_trade_now)
    public void handleTradeNowClicked(View view)
    {
        Bundle args = new Bundle();
        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
//        ProviderTradableSecuritiesHelper.pushTradableSecuritiesList(navigator.get(), args, ownedPortfolioId, providerDTO.associatedPortfolio,
//                providerId); //old securities screen

        SecurityCompositeDTO securityCompositeDTO = securityCompositeListCacheRx.getCachedValue(new BasicProviderSecurityV2ListType(providerId));
        args.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR, this.providerDTO.hexColor);
        ProviderTradableSecuritiesHelper.pushTradableSecuritiesList(navigator.get(), args, ownedPortfolioId, providerId, securityCompositeDTO);
    }

    private void displayTradeNowButton()
    {
        if (providerDTO != null && btnTradeNow.getVisibility() != View.VISIBLE && providerDTO.canTradeNow)
        {
            btnTradeNow.setVisibility(View.VISIBLE);

            int bgColor = GraphicUtil.parseColor(providerDTO.hexColor);
            StateListDrawable stateListDrawable = GraphicUtil.createStateListDrawable(getActivity(), bgColor);

            GraphicUtil.setBackground(btnTradeNow, stateListDrawable);

            int textColor = GraphicUtil.parseColor(providerDTO.textHexColor, GraphicUtil.getContrastingColor(bgColor));
            btnTradeNow.setTextColor(textColor);
        }
    }

    //<editor-fold desc="Click Handling">
    @OnItemClick(R.id.competition_zone_list)
    protected void onItemClick(AdapterView<?> adapterView,
            @SuppressWarnings("UnusedParameters") View view,
            int i,
            @SuppressWarnings("UnusedParameters") long l)
    {
        Object itemClicked = adapterView.getItemAtPosition(i);
        if (itemClicked == null)
        {
            Timber.e(new NullPointerException("itemClicked was null"), "onItemClient");
        }
        else
        {
            handleItemClicked((CompetitionZoneDTO) itemClicked, i);
        }
    }

    protected void handleItemClicked(@NonNull CompetitionZoneDTO competitionZoneDTO, int i)
    {
        Bundle args = new Bundle();
        if (competitionZoneDTO instanceof CompetitionZonePortfolioDTO)
        {
            // TODO We need to be able to launch async when the portfolio Id is finally not null
            OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
            if (ownedPortfolioId != null)
            {
                CompetitionLeaderboardPositionListFragment.putGetPositionsDTOKey(args, ownedPortfolioId);
                CompetitionLeaderboardPositionListFragment.putShownUser(args, ownedPortfolioId.getUserBaseKey());
                CompetitionLeaderboardPositionListFragment.putApplicablePortfolioId(args, ownedPortfolioId);
                CompetitionLeaderboardPositionListFragment.putProviderId(args, providerId);

                args.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL, providerDTO.navigationLogoUrl);
                args.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR, providerDTO.hexColor);

                if (providerDTO != null && providerDTO.associatedPortfolio != null)
                {
                    CompetitionLeaderboardPositionListFragment.putIsFX(args, providerDTO.associatedPortfolio.assetClass);
                    GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_COMP_PORTFOLIO);
                }
                navigator.get().pushFragment(CompetitionLeaderboardPositionListFragment.class, args);
            }
        }
        else if (competitionZoneDTO instanceof CompetitionZoneWizardDTO)
        {
            String competitionUrl = ((CompetitionZoneWizardDTO) competitionZoneDTO).getWebUrl();
            if (competitionUrl == null)
            {
                competitionUrl = providerUtil.getWizardPage(providerId);
                WizardWebViewFragment.putIsOptionMenuVisible(args, false);
            }

            WizardWebViewFragment.putUrl(args, competitionUrl);
            final OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
            if (applicablePortfolioId != null)
            {
                WizardWebViewFragment.putApplicablePortfolioId(args, applicablePortfolioId);
            }
            onDestroySubscriptions.add(navigator.get().pushFragment(WizardWebViewFragment.class, args)
                    .getUrlObservable()
                    .subscribe(
                            url -> {
                                navigator.get().popFragment();
                                thRouter.open(url + (url.indexOf('?') < 0 ? "?" : "&") + "applicablePortfolioId=" + applicablePortfolioId);
                            },
                            new TimberOnErrorAction1("Failed to listen to Wizard Url")));
        }
        else if (competitionZoneDTO instanceof CompetitionZoneLeaderboardDTO)
        {
            CompetitionZoneLeaderboardDTO dto = (CompetitionZoneLeaderboardDTO) competitionZoneDTO;
            if(dto.isActive()) {
                CompetitionLeaderboardMarkUserRecyclerFragment.putTitle(args, competitionZoneListItemAdapter.getItem(i).title);
                CompetitionLeaderboardMarkUserRecyclerFragment.putProviderId(args, providerId);
                CompetitionLeaderboardMarkUserRecyclerFragment.putCompetition(args, ((CompetitionZoneLeaderboardDTO) competitionZoneDTO).competitionDTO);
                CompetitionLeaderboardMarkUserRecyclerFragment.putLeaderboardType(args,
                        ((CompetitionZoneLeaderboardDTO) competitionZoneDTO).leaderboardType);

                OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
                if (ownedPortfolioId != null) {
                    CompetitionLeaderboardMarkUserRecyclerFragment.putApplicablePortfolioId(args, ownedPortfolioId);
                }

                if (navigator != null) {
                    navigator.get().pushFragment(CompetitionLeaderboardMarkUserRecyclerFragment.class, args);
                }
            }
        }
        else if (competitionZoneDTO instanceof CompetitionZoneLegalDTO)
        {
            // Nothing to do, handled by Listener
        }
        else if (competitionZoneDTO instanceof CompetitionZoneAdvertisementDTO)
        {
            AdDTO adDTO = ((CompetitionZoneAdvertisementDTO) competitionZoneDTO).getAdDTO();
            if (adDTO != null && adDTO.redirectUrl != null)
            {
                String url = providerUtil.appendUserId(adDTO.redirectUrl, '&');
                CompetitionWebViewFragment.putUrl(args, url);
                navigator.get().pushFragment(CompetitionWebViewFragment.class, args);
            }
        }
        else if (competitionZoneDTO instanceof CompetitionZoneDisplayCellDTO)
        {
            handleDisplayCellClicked((CompetitionZoneDisplayCellDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZonePreSeasonDTO)
        {
            CompetitionPreseasonDialogFragment dialog = CompetitionPreseasonDialogFragment.newInstance(providerId);
            dialog.show(getActivity().getSupportFragmentManager(), CompetitionPreseasonDialogFragment.TAG);
        }
        // TODO others?
    }

    private void handleDisplayCellClicked(@NonNull CompetitionZoneDisplayCellDTO competitionZoneDisplayCellDTO)
    {
        String redirectUrl = competitionZoneDisplayCellDTO.extractRedirectUrl(getResources());
        if (redirectUrl != null)
        {
            //thRouter.open(redirectUrl); TODO implement this when router is updated
            Uri uri = Uri.parse(redirectUrl);
            if (thIntentFactory.isHandlableScheme(uri.getScheme()))
            {
                if (uri.getHost().equalsIgnoreCase(getActivity().getString(R.string.intent_host_web)))
                {
                    String url = uri.getQueryParameter("url");
                    if (url != null)
                    {
                        Timber.d("Opening this page: %s", url);
                        Bundle bundle = new Bundle();
                        WebViewIntentFragment.putUrl(bundle, url);

                        if(providerDTO!=null && providerDTO.navigationLogoUrl!=null){
                            bundle.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL, providerDTO.navigationLogoUrl);
                        }

                        this.webViewFragment = navigator.get().pushFragment(WebViewIntentFragment.class, bundle);
                        this.webViewFragment.setThIntentPassedListener(this.webViewTHIntentPassedListener);
                    }
                }
                else
                {
                    //TODO Confirm with tho on how this router works
                    try
                    {

                        THIntent thIntent = thIntentFactory.create(getPassedIntent(redirectUrl));
                        thRouter.open(redirectUrl, getContext());

                    } catch (IndexOutOfBoundsException e)
                    {
                        Timber.e(e, "Failed to create intent with string %s", redirectUrl);
                    }
                }
            }
            else
            {
                Bundle bundle = new Bundle();
                WebViewIntentFragment.putUrl(bundle, redirectUrl);
                if(providerDTO!=null && providerDTO.navigationLogoUrl!=null){
                    bundle.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL, providerDTO.navigationLogoUrl);
                }
                this.webViewFragment = navigator.get().pushFragment(WebViewIntentFragment.class, bundle);
            }
        }
    }

    public Intent getPassedIntent(String url)
    {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    @Nullable public OwnedPortfolioId getApplicablePortfolioId()
    {
        if ((applicablePortfolioId == null) && (providerDTO != null))
        {
            applicablePortfolioId = providerDTO.getAssociatedOwnedPortfolioId();
        }
        return applicablePortfolioId;
    }

    protected void registerAdapterUserActions()
    {
        onStopSubscriptions.add(competitionZoneListItemAdapter.getUserActionObservable()
                .subscribe(
                        userAction -> handleUserAction(userAction),
                        throwable -> Timber.e(throwable, "When listening to adapter clicks")));
    }

    private void handleUserAction(@NonNull AbstractCompetitionZoneListItemView.UserAction userAction)
    {
        if (userAction instanceof CompetitionZonePrizePoolView.UserAction)
        {
            navigator.get().pushFragment(FriendsInvitationFragment.class);
        }
        else if (userAction instanceof CompetitionZoneLegalMentionsView.UserAction)
        {
            CompetitionZoneLegalMentionsView.LinkType linkType =
                    ((CompetitionZoneLegalMentionsView.UserAction) userAction).linkType;
            Bundle args = new Bundle();
            if (linkType.equals(CompetitionZoneLegalMentionsView.LinkType.RULES))
            {
                BaseWebViewFragment.putUrl(args, providerUtil.getRulesPage(providerId));
            }
            else
            {
                BaseWebViewFragment.putUrl(args, providerUtil.getTermsPage(providerId));
            }

            if(providerDTO!=null && providerDTO.navigationLogoUrl!=null){
                args.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL, providerDTO.navigationLogoUrl);
            }

            if (navigator != null)
            {
                navigator.get().pushFragment(BaseWebViewFragment.class, args);
            }
        }
        else if (userAction instanceof AdView.UserAction)
        {
            Bundle bundle = new Bundle();
            String url = ((AdView.UserAction) userAction).adDTO.redirectUrl + String.format("&userId=%d", currentUserId.get());
            WebViewFragment.putUrl(bundle, url);
            navigator.get().pushFragment(WebViewFragment.class, bundle);
        }
    }
    //</editor-fold>

    private class MainCompetitionWebViewTHIntentPassedListener
            extends CompetitionWebFragmentTHIntentPassedListener
    {
        public MainCompetitionWebViewTHIntentPassedListener()
        {
            super();
        }

        @Override protected BaseWebViewIntentFragment getApplicableWebViewFragment()
        {
            return webViewFragment;
        }

        @Override protected OwnedPortfolioId getApplicablePortfolioId()
        {
            return MainCompetitionFragment.this.getApplicablePortfolioId();
        }

        @Override protected ProviderId getProviderId()
        {
            return providerId;
        }

        @Override protected DashboardNavigator getNavigator()
        {
            return navigator.get();
        }

        @Override protected Class<?> getClassToPop()
        {
            return MainCompetitionFragment.class;
        }
    }
}

