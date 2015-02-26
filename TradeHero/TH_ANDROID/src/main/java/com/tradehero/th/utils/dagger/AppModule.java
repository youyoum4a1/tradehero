package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshExpandableListView;
import com.tradehero.chinabuild.*;
import com.tradehero.chinabuild.dialog.*;
import com.tradehero.chinabuild.fragment.*;
import com.tradehero.chinabuild.fragment.competition.*;
import com.tradehero.chinabuild.fragment.discovery.*;
import com.tradehero.chinabuild.fragment.message.*;
import com.tradehero.chinabuild.fragment.moreLeaderboard.StockGodListBaseFragment;
import com.tradehero.chinabuild.fragment.moreLeaderboard.StockGodListMoreFragment;
import com.tradehero.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.chinabuild.fragment.portfolio.PositionDetailFragment;
import com.tradehero.chinabuild.fragment.search.SearchFragment;
import com.tradehero.chinabuild.fragment.search.SearchUniteFragment;
import com.tradehero.chinabuild.fragment.security.BuySaleSecurityFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDiscussOrNewsFragment;
import com.tradehero.chinabuild.fragment.trade.*;
import com.tradehero.chinabuild.fragment.userCenter.UserAccountPage;
import com.tradehero.chinabuild.fragment.userCenter.UserFriendsListFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.fragment.web.WebViewFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.billing.googleplay.IABBillingAvailableTester;
import com.tradehero.common.billing.googleplay.IABBillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABServiceConnector;
import com.tradehero.common.persistence.CacheHelper;
import com.tradehero.th.activities.GuideActivity;
import com.tradehero.th.adapters.*;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import com.tradehero.th.billing.BillingModule;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.authentication.PasswordResetFragment;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.macquarie.MacquarieWarrantItemViewAdapter;
import com.tradehero.th.fragments.discussion.*;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionFragment;
import com.tradehero.th.fragments.home.HomeFragment;
import com.tradehero.th.fragments.home.HomeWebView;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserItemView;
import com.tradehero.th.fragments.news.*;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomClosedView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomInPeriodViewHolder;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomOpenView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.security.*;
import com.tradehero.th.fragments.share.ShareDestinationSetAdapter;
import com.tradehero.th.fragments.social.AllRelationsFragment;
import com.tradehero.th.fragments.social.PeopleSearchFragment;
import com.tradehero.th.fragments.social.RelationsListItemView;
import com.tradehero.th.fragments.social.follower.*;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.social.friend.SocialFriendsFragmentLinkedIn;
import com.tradehero.th.fragments.social.friend.SocialFriendsFragmentWeibo;
import com.tradehero.th.fragments.social.hero.*;
import com.tradehero.th.fragments.social.message.*;
import com.tradehero.th.fragments.timeline.TimelineItemViewLinear;
import com.tradehero.th.fragments.trade.*;
import com.tradehero.th.fragments.trade.view.TradeListItemView;
import com.tradehero.th.fragments.translation.TranslatableLanguageListFragment;
import com.tradehero.th.fragments.trending.SearchPeopleItemView;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.fragments.trending.filter.TrendingFilterSelectorView;
import com.tradehero.th.fragments.updatecenter.messages.MessageListAdapter;
import com.tradehero.th.fragments.watchlist.WatchlistItemView;
import com.tradehero.th.fragments.watchlist.WatchlistPortfolioHeaderView;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.loaders.SearchStockPageListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.loaders.security.macquarie.MacquarieSecurityListPagedLoader;
import com.tradehero.th.models.ModelsModule;
import com.tradehero.th.models.chart.ChartModule;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.push.DefaultIntentReceiver;
import com.tradehero.th.models.push.PushModule;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.models.user.SimplePremiumFollowUserAssistant;
import com.tradehero.th.network.NetworkModule;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListRetrievedAsyncMilestone;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.persistence.prefs.LanguageCode;
import com.tradehero.th.persistence.prefs.PreferenceModule;
import com.tradehero.th.persistence.timeline.TimelineManager;
import com.tradehero.th.persistence.timeline.TimelineStore;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.ui.UIModule;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.metrics.MetricsModule;
import com.tradehero.th.widget.MarkdownTextView;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.Locale;

@Module(
        includes = {
                CacheModule.class,
                GraphicModule.class,
                NetworkModule.class,
                SocialNetworkModule.class,
                UIModule.class,
                MetricsModule.class,
                ModelsModule.class,
                UserModule.class,
                PreferenceModule.class,
                ChartModule.class,
                BillingModule.class,
                PushModule.class,
        },
        injects =
                {
                        DashboardNavigator.class,
                        com.tradehero.th.base.Application.class,
                        TradeHeroProgressBar.class,
                        PullToRefreshExpandableListView.class,
                        SimplePremiumFollowUserAssistant.class,
                        PremiumFollowUserAssistant.class,
                        TranslatableLanguageListFragment.class,
                        EmailSignInFragment.class,
                        PasswordResetFragment.class,
                        TrendingFragment.class,
                        TrendingFilterSelectorView.class,
                        SecurityListPagedLoader.class,
                        SecuritySearchFragment.class,
                        SearchUniteFragment.class,
                        SecuritySearchWatchlistFragment.class,
                        SecuritySearchProviderFragment.class,
                        MacquarieSecurityListPagedLoader.class,
                        SecurityItemViewAdapter.class,
                        MacquarieWarrantItemViewAdapter.class,
                        SecurityItemView.class,
                        WarrantSecurityItemView.class,
                        SearchPeopleItemView.class,
                        FreshQuoteHolder.class,
                        BuySellFragment.class,
                        AbstractTransactionDialogFragment.class,
                        BuyDialogFragment.class,
                        SellDialogFragment.class,
                        MarkdownTextView.class,

                        NewsHeadlineFragment.class,
                        ChartFragment.class,
                        StockInfoValueFragment.class,
                        WarrantInfoValueFragment.class,
                        StockInfoFragment.class,
                        DisplayablePortfolioFetchAssistant.class,

                        PositionListFragment.class,
                        LeaderboardPositionListFragment.class,
                        CompetitionLeaderboardPositionListFragment.class,

                        PositionPartialTopView.class,
                        PositionPartialBottomClosedView.class,
                        PositionPartialBottomOpenView.class,
                        PositionLockedView.class,
                        PositionPartialBottomInPeriodViewHolder.class,

                        TradeListFragment.class,
                        TradeListItemView.class,

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

                        TimelineManager.class,

                        TimelineStore.class,
                        TimelineStore.Factory.class,

                        CacheHelper.class,

                        TimelineItemViewLinear.class,

                        PeopleSearchFragment.class,
                        LeaderboardMarkUserItemView.class,

                        WebViewFragment.class,

                        CompetitionWebViewFragment.class,

                        IABServiceConnector.class,
                        IABBillingAvailableTester.class,
                        IABBillingInventoryFetcher.class,
                        IABSKUListRetrievedAsyncMilestone.class,
                        PortfolioCompactListRetrievedMilestone.class,
                        UserProfileRetrievedMilestone.class,
                        HeroesTabContentFragment.class,
                        PremiumHeroFragment.class,
                        FreeHeroFragment.class,
                        AllHeroFragment.class,
                        AllRelationsFragment.class,
                        RelationsListItemView.class,

                        WatchlistEditFragment.class,
                        UserWatchlistPositionCache.class,
                        WatchlistPositionFragment.class,
                        WatchlistItemView.class,
                        WatchlistPortfolioHeaderView.class,

                        ProviderPageIntent.class,

                        FriendListLoader.class,

                        ShareDialogLayout.class,
                        ShareDestinationSetAdapter.class,
                        NewsDialogLayout.class,
                        SecurityActionListLinear.class,
                        NewsHeadlineViewLinear.class,
                        NewsViewLinear.class,
                        AbstractDiscussionCompactItemViewLinear.class,
                        DiscussionItemViewLinear.class,
                        AbstractDiscussionCompactItemViewHolder.class,
                        AbstractDiscussionItemViewHolder.class,
                        DiscussionItemViewHolder.class,
                        NewsItemCompactViewHolder.class,
                        NewsItemViewHolder.class,
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
                        FriendsInvitationFragment.class,
                        SocialFriendsFragmentLinkedIn.class,
                        SocialFriendsFragmentWeibo.class,
                        HomeFragment.class,
                        HomeWebView.class,
                        GuideActivity.class,

                        //China Build Start
                        MainTabFragmentTrade.class,
                        MainTabFragmentMe.class,
                        MainTabFragmentDiscovery.class,
                        MainTabFragmentStockGod.class,
                        MainTabFragmentCompetition.class,

                        StockGodListMoreFragment.class,
                        StockGodListBaseFragment.class,

                        TradeOfMineFragment.class,
                        TradeOfTypeBaseFragment.class,
                        TradeOfChinaConceptFragment.class,
                        TradeOfRisePercentFragment.class,
                        TradeOfHotHoldFragment.class,

                        DiscoveryStockGodNewsFragment.class,
                        DiscoveryRewardFragment.class,
                        DiscoveryRecentNewsFragment.class,
                        DiscoveryNewsFragment.class,
                        DiscoverySquareFragment.class,
                        DiscoveryEssentialFragment.class,
                        DiscoveryLearningFragment.class,
                        NewsDetailFragment.class,

                        SearchFragment.class,
                        SettingFragment.class,

                        SecurityListView.class,

                        LeaderboardListAdapter.class,
                        UserFriendsListAdapter.class,
                        SearchUserListAdapter.class,
                        SecurityListAdapter.class,
                        SecuritySearchListAdapter.class,
                        MyTradePositionListAdapter.class,
                        CNPersonTradePositionListAdapter.class,

                        UserTimeLineAdapter.class,
                        SecurityTimeLineDiscussOrNewsAdapter.class,
                        TimeLineBaseAdapter.class,
                        PositionTradeListAdapter.class,

                        SecurityDetailFragment.class,
                        DiscussSendFragment.class,
                        BuySaleSecurityFragment.class,

                        SecurityDetailDialogLayout.class,
                        TimeLineDetailDialogLayout.class,
                        TimeLineCommentDialogLayout.class,
                        TimeLineReportDialogLayout.class,

                        CompetitionAllFragment.class,
                        CompetitionMineFragment.class,
                        CompetitionCreateFragment.class,
                        CompetitionSearchFragment.class,
                        CompetitionBaseFragment.class,
                        CompetitionDetailFragment.class,

                        CompetitionListAdapter.class,
                        CompetitionSecuritySearchFragment.class,

                        PortfolioFragment.class,
                        UserMainPage.class,
                        SecurityDiscussOrNewsFragment.class,
                        InviteFriendsFragment.class,
                        InputInviteCodeFragment.class,
                        UserTimeLineAdapter.class,
                        MyProfileFragment.class,
                        MyEditNameFragment.class,
                        MyEditAccountFragment.class,
                        MySocialFragment.class,
                        DefaultIntentReceiver.class,
                        UserFriendsListFragment.class,
                        UserAccountPage.class,

                        BindGuestUserFragment.class,
                        BindEmailSignUpFragment.class,
                        ShareSellDialogFragment.class,
                        SecurityDiscussSendFragment.class,
                        DiscoveryDiscussSendFragment.class,
                        SettingsAboutUsFragment.class,
                        PositionDetailFragment.class,

                        ShareDialogFragment.class,
                        ShareSheetDialogLayout.class,
                        PositionTradeListAdapter.class,
                        WebViewFragment.class,
                        NotificationFragment.class,
                        NotificationListAdapter.class,
                        TimeLineItemDetailFragment.class,
                        TimeLineDetailDiscussSecItem.class,
                        CompetitionCollegeFragment.class,
                        NewsItemAdapter.class,

                        LoginSuggestDialogFragment.class,
                        //China Build End
                },
        staticInjections =
                {
                        THUser.class,
                },
        complete = false,
        library = true // TODO remove this line
)
public class AppModule
{
    private final Application application;

    public AppModule(Application application)
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
