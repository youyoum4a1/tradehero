package com.tradehero.th.utils.dagger;

import android.app.Application;
import android.content.Context;
import com.squareup.picasso.Picasso;
import com.tradehero.common.billing.googleplay.IABInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABServiceConnector;
import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.common.persistence.CacheHelper;
import com.tradehero.th.R;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.form.AbstractUserAvailabilityRequester;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioUtil;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.base.THUser;
import com.tradehero.th.billing.googleplay.PurchaseRestorerRequiredMilestone;
import com.tradehero.th.billing.googleplay.THIABInventoryFetcher;
import com.tradehero.th.billing.googleplay.THIABLogicHolder;
import com.tradehero.th.billing.googleplay.THIABPurchaseConsumer;
import com.tradehero.th.billing.googleplay.THIABPurchaseFetcher;
import com.tradehero.th.billing.googleplay.THIABPurchaseReporter;
import com.tradehero.th.billing.googleplay.THIABPurchaser;
import com.tradehero.th.billing.googleplay.THInventoryFetchMilestone;
import com.tradehero.th.fragments.WebViewFragment;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.billing.THIABUserInteractor;
import com.tradehero.th.fragments.leaderboard.BaseLeaderboardFragment;
import com.tradehero.th.fragments.leaderboard.FriendLeaderboardMarkUserListViewFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefListViewFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefView;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserItemView;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListView;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListViewFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserLoader;
import com.tradehero.th.fragments.portfolio.PortfolioListFragment;
import com.tradehero.th.fragments.portfolio.PortfolioListItemAdapter;
import com.tradehero.th.fragments.portfolio.PortfolioListItemView;
import com.tradehero.th.fragments.portfolio.PushablePortfolioListFragment;
import com.tradehero.th.fragments.portfolio.header.CurrentUserPortfolioHeaderView;
import com.tradehero.th.fragments.portfolio.header.OtherUserPortfolioHeaderView;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.LockedPositionItem;
import com.tradehero.th.fragments.position.PositionClosedView;
import com.tradehero.th.fragments.position.PositionInPeriodClosedView;
import com.tradehero.th.fragments.position.PositionInPeriodOpenView;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.position.PositionOpenView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomClosedView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomInPeriodClosedView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomInPeriodOpenView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomOpenView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.security.AddToWatchListFragment;
import com.tradehero.th.fragments.security.ChartFragment;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.security.StockInfoValueFragment;
import com.tradehero.th.fragments.security.YahooNewsFragment;
import com.tradehero.th.fragments.settings.AboutFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.settings.SettingsPayPalFragment;
import com.tradehero.th.fragments.settings.SettingsProfileFragment;
import com.tradehero.th.fragments.settings.SettingsTransactionHistoryFragment;
import com.tradehero.th.fragments.social.follower.FollowerListItemView;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerInfoFetcher;
import com.tradehero.th.fragments.social.follower.FollowerPayoutManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroListItemView;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerInfoFetcher;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineItemView;
import com.tradehero.th.fragments.trade.BuySellConfirmFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.FreshQuoteHolder;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trade.TradeListHeaderView;
import com.tradehero.th.fragments.trade.TradeListItemAdapter;
import com.tradehero.th.fragments.trade.TradeListItemView;
import com.tradehero.th.fragments.trade.TradeListOverlayHeaderView;
import com.tradehero.th.fragments.trending.SearchPeopleItemView;
import com.tradehero.th.fragments.trending.SearchStockPeopleFragment;
import com.tradehero.th.fragments.trending.SecurityItemView;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorAllFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorBasicFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorPriceFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorVolumeFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.loaders.SearchStockPageListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.models.alert.SecurityAlertAssistant;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.models.push.UrbanAirshipPushNotificationManager;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.YahooEngine;
import com.tradehero.th.network.service.AlertPlanService;
import com.tradehero.th.network.service.AlertService;
import com.tradehero.th.network.service.FollowerService;
import com.tradehero.th.network.service.LeaderboardService;
import com.tradehero.th.network.service.MarketService;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.network.service.PositionService;
import com.tradehero.th.network.service.ProviderService;
import com.tradehero.th.network.service.QuoteService;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.network.service.SessionService;
import com.tradehero.th.network.service.SocialService;
import com.tradehero.th.network.service.TradeService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.network.service.WatchlistService;
import com.tradehero.th.network.service.YahooNewsService;
import com.tradehero.th.persistence.DTOCacheUtil;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListRetrievedAsyncMilestone;
import com.tradehero.th.persistence.leaderboard.LeaderboardManager;
import com.tradehero.th.persistence.portfolio.OwnedPortfolioFetchAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.timeline.TimelineManager;
import com.tradehero.th.persistence.timeline.TimelineStore;
import com.tradehero.th.persistence.user.AbstractUserStore;
import com.tradehero.th.persistence.user.UserManager;
import com.tradehero.th.persistence.user.UserProfileFetchAssistant;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.persistence.user.UserStore;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistRetrievedMilestone;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.FacebookUtils;
import com.tradehero.th.utils.LinkedInUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.TwitterUtils;
import com.tradehero.th.widget.MarkdownTextView;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.user.ProfileCompactView;
import com.tradehero.th.widget.user.ProfileView;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.ocpsoft.prettytime.PrettyTime;

/** Created with IntelliJ IDEA. User: tho Date: 9/16/13 Time: 5:36 PM Copyright (c) TradeHero */
@Module(
        injects =
        {
                com.tradehero.th.base.Application.class,

                AuthenticationActivity.class,
                DashboardActivity.class,

                UserProfileFetchAssistant.class,
                OwnedPortfolioFetchAssistant.class,
                SecurityAlertAssistant.class,

                SettingsTransactionHistoryFragment.class,
                SettingsPayPalFragment.class,
                SettingsProfileFragment.class,
                SettingsFragment.class,
                AboutFragment.class,
                EmailSignInFragment.class,
                ServerValidatedUsernameText.UserAvailabilityRequester.class,
                ServerValidatedUsernameText.class,
                TrendingFragment.class,
                TrendingFilterSelectorBasicFragment.class,
                TrendingFilterSelectorVolumeFragment.class,
                TrendingFilterSelectorPriceFragment.class,
                TrendingFilterSelectorAllFragment.class,
                SecurityListPagedLoader.class,
                SecurityItemView.class,
                SearchStockPeopleFragment.class,
                SearchPeopleItemView.class,
                FreshQuoteHolder.class,
                BuySellFragment.class,
                BuySellConfirmFragment.class,
                BuySellConfirmFragment.BuySellAsyncTask.class,
                TimelineFragment.class,
                MeTimelineFragment.class,
                PushableTimelineFragment.class,
                PushableTimelineFragment.PushableTimelineTHIABUserInteractor.class,
                MarkdownTextView.class,

                YahooNewsFragment.class,
                ChartFragment.class,
                StockInfoValueFragment.class,
                StockInfoFragment.class,
                PortfolioListFragment.class,
                PushablePortfolioListFragment.class,
                PortfolioListItemView.class,
                PortfolioListItemAdapter.class,

                PositionListFragment.class,
                PositionListFragment.PositionListTHIABUserInteractor.class,
                LeaderboardPositionListFragment.class,
                CurrentUserPortfolioHeaderView.class,
                OtherUserPortfolioHeaderView.class,

                PositionOpenView.class,
                PositionInPeriodOpenView.class,

                PositionClosedView.class,
                PositionInPeriodClosedView.class,
                PositionPartialTopView.class,
                PositionPartialBottomOpenView.class,
                PositionPartialBottomInPeriodOpenView.class,
                PositionPartialBottomClosedView.class,
                PositionPartialBottomInPeriodClosedView.class,
                LockedPositionItem.class,

                TradeListFragment.class,
                TradeListItemAdapter.class,
                TradeListItemView.class,
                TradeListOverlayHeaderView.class,
                TradeListHeaderView.class,

                StoreScreenFragment.class,
                HeroManagerFragment.class,
                HeroListItemView.class,
                FollowerManagerFragment.class,
                FollowerManagerInfoFetcher.class,
                FollowerPayoutManagerFragment.class,
                FollowerListItemView.class,

                AbstractUserAvailabilityRequester.class,
                SearchStockPageListLoader.class,
                TimelineListLoader.class,

                UserManager.class,
                TimelineManager.class,

                UserStore.class,
                TimelineStore.class,
                TimelineStore.Factory.class,

                DatabaseCache.class,
                CacheHelper.class,

                TimelineFragment.class,
                TimelineItemView.class,
                ProfileView.class,
                ProfileCompactView.class,

                LeaderboardCommunityFragment.class,
                LeaderboardDefListViewFragment.class,

                LeaderboardDefView.class,
                LeaderboardManager.class,
                LeaderboardMarkUserLoader.class,
                LeaderboardMarkUserListViewFragment.class,
                BaseLeaderboardFragment.class,
                LeaderboardMarkUserItemView.class,
                LeaderboardMarkUserListView.class,
                FriendLeaderboardMarkUserListViewFragment.class,

                WebViewFragment.class,

                IABServiceConnector.class,
                IABInventoryFetcher.class,
                THIABPurchaseFetcher.class,
                THIABInventoryFetcher.class,
                THIABPurchaser.class,
                THIABPurchaseFetcher.class,
                THIABPurchaseReporter.class,
                THIABLogicHolder.class,
                THIABPurchaseConsumer.class,
                THInventoryFetchMilestone.class,
                IABSKUListRetrievedAsyncMilestone.class,
                PortfolioCompactListRetrievedMilestone.class,
                UserProfileRetrievedMilestone.class,
                PurchaseRestorerRequiredMilestone.class,
                THIABUserInteractor.class,
                StoreScreenFragment.StoreScreenTHIABUserInteractor.class,
                HeroManagerFragment.HeroManagerTHIABUserInteractor.class,
                HeroManagerInfoFetcher.class,
                BuySellFragment.BuySellTHIABUserInteractor.class,

                AddToWatchListFragment.class,
                UserWatchlistPositionCache.class,
                WatchlistRetrievedMilestone.class,
        },
        staticInjections =
        {
                THUser.class,
                NumberDisplayUtils.class,
                DisplayablePortfolioDTO.class,
                DisplayablePortfolioUtil.class,
                DTOCacheUtil.class,
        },
        library = true // TEMP
)
public class TradeHeroModule
{
    public static final String TAG = TradeHeroModule.class.getSimpleName();

    private final Application application;
    private final NetworkEngine engine;
    private final YahooEngine yahooEngine;
    private final LruMemFileCache lruFileCache;

    public TradeHeroModule(NetworkEngine engine, YahooEngine yahooEngine, Application application, LruMemFileCache lruFileCache)
    {
        this.application = application;
        this.engine = engine;
        this.yahooEngine = yahooEngine;
        this.lruFileCache = lruFileCache;
    }

    //<editor-fold desc="API service endpoints">
    @Provides @Singleton UserService provideUserService()
    {
        return engine.createService(UserService.class);
    }

    @Provides @Singleton SessionService provideSessionService()
    {
        return engine.createService(SessionService.class);
    }

    @Provides @Singleton SecurityService provideSecurityService()
    {
        return engine.createService(SecurityService.class);
    }

    @Provides @Singleton UserTimelineService provideUserTimelineService()
    {
        return engine.createService(UserTimelineService.class);
    }

    @Provides @Singleton QuoteService provideQuoteService()
    {
        return engine.createService(QuoteService.class);
    }

    @Provides @Singleton PortfolioService providePortfolioService()
    {
        return engine.createService(PortfolioService.class);
    }

    @Provides @Singleton PositionService providePositionService()
    {
        return engine.createService(PositionService.class);
    }

    @Provides @Singleton TradeService provideTradeService()
    {
        return engine.createService(TradeService.class);
    }

    @Provides @Singleton LeaderboardService provideLeaderboardService()
    {
        return engine.createService(LeaderboardService.class);
    }

    @Provides @Singleton ProviderService provideProviderService()
    {
        return engine.createService(ProviderService.class);
    }

    @Provides @Singleton MarketService provideMarketService()
    {
        return engine.createService(MarketService.class);
    }

    @Provides @Singleton FollowerService provideFollowerService()
    {
        return engine.createService(FollowerService.class);
    }

    @Provides @Singleton AlertService provideAlertService()
    {
        return engine.createService(AlertService.class);
    }

    @Provides @Singleton AlertPlanService provideAlertPlanService()
    {
        return engine.createService(AlertPlanService.class);
    }

    @Provides @Singleton SocialService provideSocialService()
    {
        return engine.createService(SocialService.class);
    }

    @Provides @Singleton YahooNewsService provideYahooNewsService()
    {
        return yahooEngine.createService(YahooNewsService.class);
    }

    @Provides @Singleton WatchlistService provideWatchlistService()
    {
        return engine.createService(WatchlistService.class);
    }
    //</editor-fold>

    //<editor-fold desc="Utilities">
    @Provides @Singleton FacebookUtils provideFacebookUtils(Context context)
    {
        return new FacebookUtils(context, context.getString(R.string.FACEBOOK_APP_ID));
    }

    @Provides @Singleton TwitterUtils provideTwitterUtils(Context context)
    {
        return new TwitterUtils(
                context.getString(R.string.TWITTER_CONSUMER_KEY),
                context.getString(R.string.TWITTER_CONSUMER_SECRET));
    }

    @Provides @Singleton LinkedInUtils provideLinkedInUtils(Context context)
    {
        return new LinkedInUtils(
                context.getString(R.string.LINKEDIN_CONSUMER_KEY),
                context.getString(R.string.LINKEDIN_CONSUMER_SECRET));
    }
    //</editor-fold>

    @Provides Context provideContext()
    {
        return application.getApplicationContext();
    }

    @Provides @Singleton CurrentUserBaseKeyHolder provideCurrentUserBaseKeyHolder()
    {
        return new CurrentUserBaseKeyHolder();
    }

    @Provides PrettyTime providePrettyTime()
    {
        return new PrettyTime();
    }

    @Provides @Singleton Picasso providePicasso()
    {
        Picasso mPicasso = new Picasso.Builder(application)
                //.downloader(new UrlConnectionDownloader(getContext()))
                .memoryCache(lruFileCache)
                .build();
        mPicasso.setDebugging(Constants.PICASSO_DEBUG);
        return mPicasso;
    }

    @Provides @Singleton AbstractUserStore provideUserStore(UserStore store)
    {
        return store;
    }

    @Provides @Singleton PushNotificationManager providePushNotificationManager()
    {
        return new UrbanAirshipPushNotificationManager();
    }
}
