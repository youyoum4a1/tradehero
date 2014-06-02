package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.tradehero.common.billing.googleplay.IABBillingAvailableTester;
import com.tradehero.common.billing.googleplay.IABBillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABServiceConnector;
import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.common.persistence.CacheHelper;
import com.tradehero.th.activities.ActivityModule;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import com.tradehero.th.billing.googleplay.THBaseIABInventoryFetcherHolder;
import com.tradehero.th.billing.googleplay.THBaseIABPurchaseReporterHolder;
import com.tradehero.th.billing.googleplay.THIABBillingInteractor;
import com.tradehero.th.billing.googleplay.THIABBillingInventoryFetcher;
import com.tradehero.th.billing.googleplay.THIABLogicHolderFull;
import com.tradehero.th.billing.googleplay.THIABModule;
import com.tradehero.th.billing.googleplay.THIABPurchaseConsumer;
import com.tradehero.th.billing.googleplay.THIABPurchaseFetchMilestone;
import com.tradehero.th.billing.googleplay.THIABPurchaseFetcher;
import com.tradehero.th.billing.googleplay.THIABPurchaseReporter;
import com.tradehero.th.billing.googleplay.THIABPurchaser;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.alert.AlertEditFragment;
import com.tradehero.th.fragments.alert.AlertManagerFragment;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.macquarie.MacquarieWarrantItemViewAdapter;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import com.tradehero.th.fragments.discussion.AbstractDiscussionFragment;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemViewHolder;
import com.tradehero.th.fragments.discussion.DiscussionItemViewLinear;
import com.tradehero.th.fragments.discussion.DiscussionItemViewHolder;
import com.tradehero.th.fragments.discussion.DiscussionSetAdapter;
import com.tradehero.th.fragments.discussion.PrivateDiscussionSetAdapter;
import com.tradehero.th.fragments.discussion.SingleViewDiscussionSetAdapter;
import com.tradehero.th.fragments.discussion.TimelineItemViewHolder;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionFragment;
import com.tradehero.th.fragments.leaderboard.BaseLeaderboardFragment;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserItemView;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListClosedFragment;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListOnGoingFragment;
import com.tradehero.th.fragments.leaderboard.FriendLeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefView;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserItemView;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListAdapter;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListView;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserLoader;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterFragment;
import com.tradehero.th.fragments.news.NewsDialogLayout;
import com.tradehero.th.fragments.news.NewsHeadlineFragment;
import com.tradehero.th.fragments.news.NewsHeadlineViewLinear;
import com.tradehero.th.fragments.news.NewsItemCompactViewHolder;
import com.tradehero.th.fragments.news.ShareDialogLayout;
import com.tradehero.th.fragments.portfolio.PortfolioListFragment;
import com.tradehero.th.fragments.portfolio.PortfolioListItemAdapter;
import com.tradehero.th.fragments.portfolio.PortfolioListItemView;
import com.tradehero.th.fragments.portfolio.SimpleOwnPortfolioListItemAdapter;
import com.tradehero.th.fragments.portfolio.header.OtherUserPortfolioHeaderView;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomClosedView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomInPeriodClosedView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomInPeriodOpenView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomInPeriodViewHolder;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomOpenView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.security.ChartFragment;
import com.tradehero.th.fragments.security.SecurityItemView;
import com.tradehero.th.fragments.security.SecurityItemViewAdapter;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.security.SecuritySearchProviderFragment;
import com.tradehero.th.fragments.security.SecuritySearchWatchlistFragment;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.security.StockInfoValueFragment;
import com.tradehero.th.fragments.security.WarrantInfoValueFragment;
import com.tradehero.th.fragments.security.WarrantSecurityItemView;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.settings.AboutFragment;
import com.tradehero.th.fragments.settings.InviteFriendFragment;
import com.tradehero.th.fragments.settings.ProfileInfoView;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.settings.SettingsProfileFragment;
import com.tradehero.th.fragments.settings.UserFriendDTOView;
import com.tradehero.th.fragments.share.ShareDestinationSetAdapter;
import com.tradehero.th.fragments.social.AllRelationsFragment;
import com.tradehero.th.fragments.social.RelationsListItemView;
import com.tradehero.th.fragments.social.follower.AllFollowerFragment;
import com.tradehero.th.fragments.social.follower.FollowerListItemView;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerInfoFetcher;
import com.tradehero.th.fragments.social.follower.FollowerPayoutManagerFragment;
import com.tradehero.th.fragments.social.follower.FreeFollowerFragment;
import com.tradehero.th.fragments.social.follower.PremiumFollowerFragment;
import com.tradehero.th.fragments.social.hero.AllHeroFragment;
import com.tradehero.th.fragments.social.hero.FreeHeroFragment;
import com.tradehero.th.fragments.social.hero.HeroListItemView;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerInfoFetcher;
import com.tradehero.th.fragments.social.hero.HeroesTabContentFragment;
import com.tradehero.th.fragments.social.hero.PremiumHeroFragment;
import com.tradehero.th.fragments.social.message.AbstractPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.PrivateDiscussionView;
import com.tradehero.th.fragments.social.message.PrivateMessageBubbleViewLinear;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineItemViewLinear;
import com.tradehero.th.fragments.timeline.UserProfileCompactViewHolder;
import com.tradehero.th.fragments.timeline.UserProfileDetailViewHolder;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.FreshQuoteHolder;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trade.TradeListInPeriodFragment;
import com.tradehero.th.fragments.trade.view.TradeListHeaderView;
import com.tradehero.th.fragments.trade.view.TradeListItemView;
import com.tradehero.th.fragments.trade.view.TradeListOverlayHeaderView;
import com.tradehero.th.fragments.trending.SearchPeopleItemView;
import com.tradehero.th.fragments.trending.SearchStockPeopleFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSelectorView;
import com.tradehero.th.fragments.updatecenter.messages.MessageListAdapter;
import com.tradehero.th.fragments.watchlist.WatchlistItemView;
import com.tradehero.th.fragments.watchlist.WatchlistPortfolioHeaderView;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.loaders.SearchStockPageListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.loaders.security.macquarie.MacquarieSecurityListPagedLoader;
import com.tradehero.th.models.alert.MiddleCallbackCreateAlertCompact;
import com.tradehero.th.models.alert.MiddleCallbackUpdateAlertCompact;
import com.tradehero.th.models.chart.ChartModule;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.models.intent.trending.TrendingIntentFactory;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.push.PushModule;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.network.NetworkModule;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListRetrievedAsyncMilestone;
import com.tradehero.th.persistence.leaderboard.LeaderboardManager;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.prefs.LanguageCode;
import com.tradehero.th.persistence.prefs.PreferenceModule;
import com.tradehero.th.persistence.timeline.TimelineManager;
import com.tradehero.th.persistence.timeline.TimelineStore;
import com.tradehero.th.persistence.user.UserManager;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.persistence.user.UserStore;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistRetrievedMilestone;
import com.tradehero.th.ui.UIModule;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.widget.MarkdownTextView;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import dagger.Module;
import dagger.Provides;
import java.util.Locale;
import javax.inject.Singleton;

@Module(
        includes = {
                CacheModule.class,
                GraphicModule.class,
                NetworkModule.class,
                SocialNetworkModule.class,
                UIModule.class,
                UxModule.class,
                UserModule.class,
                PreferenceModule.class,
                ChartModule.class,
                ActivityModule.class,
                THIABModule.class,
                NewsModule.class,
                PushModule.class,
        },
        injects =
                {
                        DashboardNavigator.class,
                        com.tradehero.th.base.Application.class,
                        SettingsProfileFragment.class,
                        ProfileInfoView.class,
                        PremiumFollowUserAssistant.class,
                        SettingsFragment.class,
                        AboutFragment.class,
                        EmailSignInFragment.class,
                        ServerValidatedUsernameText.class,
                        TrendingFragment.class,
                        TrendingFilterSelectorView.class,
                        SecurityListPagedLoader.class,
                        SecuritySearchFragment.class,
                        SecuritySearchWatchlistFragment.class,
                        SecuritySearchProviderFragment.class,
                        MacquarieSecurityListPagedLoader.class,
                        SecurityItemViewAdapter.class,
                        MacquarieWarrantItemViewAdapter.class,
                        SecurityItemView.class,
                        WarrantSecurityItemView.class,
                        SearchStockPeopleFragment.class,
                        SearchPeopleItemView.class,
                        FreshQuoteHolder.class,
                        BuySellFragment.class,
                        TimelineFragment.class,
                        MeTimelineFragment.class,
                        PushableTimelineFragment.class,
                        SimpleOwnPortfolioListItemAdapter.class,
                        MarkdownTextView.class,

                        NewsHeadlineFragment.class,
                        ChartFragment.class,
                        StockInfoValueFragment.class,
                        WarrantInfoValueFragment.class,
                        StockInfoFragment.class,
                        PortfolioListFragment.class,
                        PortfolioListItemView.class,
                        PortfolioListItemAdapter.class,
                        DisplayablePortfolioFetchAssistant.class,

                        PositionListFragment.class,
                        LeaderboardPositionListFragment.class,
                        OtherUserPortfolioHeaderView.class,

                        PositionPartialTopView.class,
                        PositionPartialBottomClosedView.class,
                        PositionPartialBottomInPeriodClosedView.class,
                        PositionPartialBottomInPeriodOpenView.class,
                        PositionPartialBottomOpenView.class,
                        PositionLockedView.class,
                        PositionPartialBottomInPeriodViewHolder.class,

                        TradeListFragment.class,
                        TradeListInPeriodFragment.class,
                        TradeListItemView.class,
                        TradeListOverlayHeaderView.class,
                        TradeListHeaderView.class,

                        StoreScreenFragment.class,
                        HeroManagerFragment.class,
                        HeroListItemView.class,
                        FollowerManagerFragment.class,
                        AllFollowerFragment.class,
                        PremiumFollowerFragment.class,
                        FreeFollowerFragment.class,
                        FollowerManagerInfoFetcher.class,
                        FollowerPayoutManagerFragment.class,
                        FollowerListItemView.class,

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
                        TimelineItemViewLinear.class,
                        UserProfileCompactViewHolder.class,
                        UserProfileDetailViewHolder.class,

                        LeaderboardCommunityFragment.class,
                        LeaderboardDefListFragment.class,

                        LeaderboardDefView.class,
                        LeaderboardManager.class,
                        LeaderboardMarkUserLoader.class,
                        LeaderboardMarkUserListFragment.class,
                        BaseLeaderboardFragment.class,
                        LeaderboardMarkUserItemView.class,
                        CompetitionLeaderboardMarkUserItemView.class,
                        LeaderboardMarkUserListAdapter.class,
                        LeaderboardMarkUserListView.class,
                        FriendLeaderboardMarkUserListFragment.class,
                        CompetitionLeaderboardMarkUserListClosedFragment.class,
                        CompetitionLeaderboardMarkUserListOnGoingFragment.class,
                        LeaderboardFilterFragment.class,

                        WebViewFragment.class,

                        CompetitionWebViewFragment.class,

                        IABServiceConnector.class,
                        IABBillingAvailableTester.class,
                        IABBillingInventoryFetcher.class,
                        THIABPurchaseFetcher.class,
                        THIABBillingInventoryFetcher.class,
                        THIABPurchaser.class,
                        THIABPurchaseReporter.class,
                        THIABLogicHolderFull.class,
                        THIABPurchaseConsumer.class,
                        THBaseIABInventoryFetcherHolder.class,
                        THBaseIABPurchaseReporterHolder.class,
                        THIABPurchaseFetchMilestone.class,
                        IABSKUListRetrievedAsyncMilestone.class,
                        PortfolioCompactListRetrievedMilestone.class,
                        UserProfileRetrievedMilestone.class,
                        THIABBillingInteractor.class,
                        HeroManagerInfoFetcher.class,
                        HeroesTabContentFragment.class,
                        PremiumHeroFragment.class,
                        FreeHeroFragment.class,
                        AllHeroFragment.class,
                        HeroManagerInfoFetcher.class,
                        AllRelationsFragment.class,
                        RelationsListItemView.class,

                        WatchlistEditFragment.class,
                        UserWatchlistPositionCache.class,
                        WatchlistRetrievedMilestone.class,
                        WatchlistPositionFragment.class,
                        WatchlistItemView.class,
                        WatchlistPortfolioHeaderView.class,

                        TrendingIntentFactory.class,
                        ProviderPageIntent.class,

                        AlertManagerFragment.class,
                        AlertEditFragment.class,
                        AlertCreateFragment.class,
                        MiddleCallbackUpdateAlertCompact.class,
                        MiddleCallbackCreateAlertCompact.class,

                        InviteFriendFragment.class,

                        UserFriendDTOView.class,
                        FriendListLoader.class,

                        ShareDialogLayout.class,
                        ShareDestinationSetAdapter.class,
                        NewsDialogLayout.class,
                        NewsHeadlineViewLinear.class,
                        AbstractDiscussionCompactItemViewLinear.class,
                        DiscussionItemViewLinear.class,
                        AbstractDiscussionCompactItemViewHolder.class,
                        AbstractDiscussionItemViewHolder.class,
                        DiscussionItemViewHolder.class,
                        NewsItemCompactViewHolder.class,
                        TimelineItemViewHolder.class,
                        SingleViewDiscussionSetAdapter.class,
                        MessageHeaderDTO.class,
                        MessageListAdapter.class,
                        NewPrivateMessageFragment.class,
                        ReplyPrivateMessageFragment.class,
                        DiscussionSetAdapter.class,
                        PrivateDiscussionView.class,
                        PrivateDiscussionSetAdapter.class,
                        PrivateDiscussionView.PrivateDiscussionViewDiscussionSetAdapter.class,
                        PrivateMessageBubbleViewLinear.class,
                        AbstractDiscussionFragment.class,
                        AbstractPrivateMessageFragment.class,

                        SecurityDiscussionFragment.class,
                        AlertDialogUtil.class,
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
    private final Application application;

    public TradeHeroModule(Application application)
    {
        this.application = application;
    }

    // We should not use like this. Instead use like CurrentActivityHolder
    @Deprecated
    @Provides Context provideContext()
    {
        return application.getApplicationContext();
    }

    @Provides @LanguageCode String provideCurrentLanguageCode(Context context)
    {
        Locale locale = context.getResources().getConfiguration().locale;
        return String.format("%s-%s", locale.getLanguage(), locale.getCountry());
    }

    @Provides @Singleton Application provideApplication()
    {
        return application;
    }
}
