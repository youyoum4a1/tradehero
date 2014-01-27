package com.tradehero.th.utils.dagger;

import android.app.Application;
import android.content.Context;
import com.tradehero.common.billing.googleplay.IABInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABServiceConnector;
import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.common.persistence.CacheHelper;
import com.tradehero.th.api.form.AbstractUserAvailabilityRequester;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.base.THUser;
import com.tradehero.th.billing.googleplay.PurchaseRestorerRequiredMilestone;
import com.tradehero.th.billing.googleplay.THIABInventoryFetcher;
import com.tradehero.th.billing.googleplay.THIABLogicHolder;
import com.tradehero.th.billing.googleplay.THIABPurchaseConsumer;
import com.tradehero.th.billing.googleplay.THIABPurchaseFetcher;
import com.tradehero.th.billing.googleplay.THIABPurchaseReporter;
import com.tradehero.th.billing.googleplay.THIABPurchaser;
import com.tradehero.th.billing.googleplay.THInventoryFetchMilestone;
import com.tradehero.th.fragments.alert.AlertManagerFragment;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.billing.THIABUserInteractor;
import com.tradehero.th.fragments.competition.macquarie.MacquarieWarrantItemViewAdapter;
import com.tradehero.th.fragments.leaderboard.BaseLeaderboardFragment;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListViewFragment;
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
import com.tradehero.th.fragments.security.ChartFragment;
import com.tradehero.th.fragments.security.SecurityItemView;
import com.tradehero.th.fragments.security.SecurityItemViewAdapter;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.security.StockInfoValueFragment;
import com.tradehero.th.fragments.security.WarrantInfoValueFragment;
import com.tradehero.th.fragments.security.WarrantSecurityItemView;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.security.YahooNewsFragment;
import com.tradehero.th.fragments.settings.AboutFragment;
import com.tradehero.th.fragments.settings.InviteFriendFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.settings.SettingsProfileFragment;
import com.tradehero.th.fragments.settings.UserFriendDTOView;
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
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSelectorView;
import com.tradehero.th.fragments.watchlist.WatchlistItemView;
import com.tradehero.th.fragments.watchlist.WatchlistPortfolioHeaderView;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.loaders.SearchStockPageListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.loaders.security.macquarie.MacquarieSecurityListPagedLoader;
import com.tradehero.th.models.intent.trending.TrendingIntentFactory;
import com.tradehero.th.models.push.PushNotificationManager;
import com.tradehero.th.models.push.urbanairship.UrbanAirshipPushNotificationManager;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListRetrievedAsyncMilestone;
import com.tradehero.th.persistence.leaderboard.LeaderboardManager;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.timeline.TimelineManager;
import com.tradehero.th.persistence.timeline.TimelineStore;
import com.tradehero.th.persistence.user.UserManager;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.persistence.user.UserStore;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistRetrievedMilestone;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.widget.MarkdownTextView;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.user.ProfileCompactView;
import com.tradehero.th.widget.user.ProfileView;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/** Created with IntelliJ IDEA. User: tho Date: 9/16/13 Time: 5:36 PM Copyright (c) TradeHero */
@Module(
        includes = {
                ApiModule.class,
                CacheModule.class,
                SocialNetworkModule.class,
                UserModule.class,
                GraphicModule.class,
                UIModule.class,
        },
        injects =
                {
                        com.tradehero.th.base.Application.class,
                        SettingsProfileFragment.class,
                        SettingsFragment.class,
                        AboutFragment.class,
                        EmailSignInFragment.class,
                        ServerValidatedUsernameText.UserAvailabilityRequester.class,
                        ServerValidatedUsernameText.class,
                        TrendingFragment.class,
                        TrendingFilterSelectorView.class,
                        SecurityListPagedLoader.class,
                        MacquarieSecurityListPagedLoader.class,
                        SecurityItemViewAdapter.class,
                        MacquarieWarrantItemViewAdapter.class,
                        SecurityItemView.class,
                        WarrantSecurityItemView.class,
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
                        WarrantInfoValueFragment.class,
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
                        CompetitionLeaderboardMarkUserListViewFragment.class,

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
                        PositionDTOCompactList.class,
                        UserProfileRetrievedMilestone.class,
                        PurchaseRestorerRequiredMilestone.class,
                        THIABUserInteractor.class,
                        StoreScreenFragment.StoreScreenTHIABUserInteractor.class,
                        HeroManagerFragment.HeroManagerTHIABUserInteractor.class,
                        HeroManagerInfoFetcher.class,
                        BuySellFragment.BuySellTHIABUserInteractor.class,

                        WatchlistEditFragment.class,
                        UserWatchlistPositionCache.class,
                        WatchlistRetrievedMilestone.class,
                        WatchlistPositionFragment.class,
                        WatchlistItemView.class,
                        WatchlistPortfolioHeaderView.class,

                        TrendingIntentFactory.class,

                        AlertManagerFragment.class,

                        InviteFriendFragment.class,

                        UserFriendDTOView.class,
                        FriendListLoader.class,
                },
        staticInjections =
                {
                        THUser.class,
                        NumberDisplayUtils.class,
                },
        complete = false,
        library = true // TODO remove this line
)
public class TradeHeroModule
{
    public static final String TAG = TradeHeroModule.class.getSimpleName();

    private final Application application;

    public TradeHeroModule(Application application)
    {
        this.application = application;
    }

    @Provides Context provideContext()
    {
        return application.getApplicationContext();
    }

    @Provides @Singleton PushNotificationManager providePushNotificationManager()
    {
        return new UrbanAirshipPushNotificationManager();
    }
}
