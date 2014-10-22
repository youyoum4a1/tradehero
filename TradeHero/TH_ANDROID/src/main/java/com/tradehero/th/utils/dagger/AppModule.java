package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.tradehero.common.billing.googleplay.IABBillingAvailableTester;
import com.tradehero.common.billing.googleplay.IABBillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABServiceConnector;
import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.common.persistence.CacheHelper;
import com.tradehero.th.activities.ActivityModule;
import com.tradehero.th.activities.GuideActivity;
import com.tradehero.th.adapters.CompetitionListAdapter;
import com.tradehero.th.adapters.LeaderboardListAdapter;
import com.tradehero.th.adapters.MyTradePositionListAdapter;
import com.tradehero.th.adapters.NotificationListAdapter;
import com.tradehero.th.adapters.PositionTradeListAdapter;
import com.tradehero.th.adapters.SecurityListAdapter;
import com.tradehero.th.adapters.SecuritySearchListAdapter;
import com.tradehero.th.adapters.SecurityTimeLineDiscussOrNewsAdapter;
import com.tradehero.th.adapters.TimeLineBaseAdapter;
import com.tradehero.th.adapters.UserFriendsListAdapter;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import com.tradehero.th.billing.BillingModule;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.alert.AlertCreateFragment;
import com.tradehero.th.fragments.alert.AlertEditFragment;
import com.tradehero.th.fragments.alert.AlertManagerFragment;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.billing.StoreScreenFragment;
import com.tradehero.th.fragments.chinabuild.MainTabFragmentCompetition;
import com.tradehero.th.fragments.chinabuild.MainTabFragmentDiscovery;
import com.tradehero.th.fragments.chinabuild.MainTabFragmentMe;
import com.tradehero.th.fragments.chinabuild.MainTabFragmentStockGod;
import com.tradehero.th.fragments.chinabuild.MainTabFragmentTrade;
import com.tradehero.th.fragments.chinabuild.dialog.SecurityDetailDialogLayout;
import com.tradehero.th.fragments.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.th.fragments.chinabuild.fragment.BindEmailSignUpFragment;
import com.tradehero.th.fragments.chinabuild.fragment.BindGuestUserFragment;
import com.tradehero.th.fragments.chinabuild.fragment.InputInviteCodeFragment;
import com.tradehero.th.fragments.chinabuild.fragment.InviteFriendsFragment;
import com.tradehero.th.fragments.chinabuild.fragment.LoginSuggestDialogFragment;
import com.tradehero.th.fragments.chinabuild.fragment.MyEditAccountFragment;
import com.tradehero.th.fragments.chinabuild.fragment.MyEditNameFragment;
import com.tradehero.th.fragments.chinabuild.fragment.MyProfileFragment;
import com.tradehero.th.fragments.chinabuild.fragment.MySocialFragment;
import com.tradehero.th.fragments.chinabuild.fragment.SettingFragment;
import com.tradehero.th.fragments.chinabuild.fragment.SettingsAboutUsFragment;
import com.tradehero.th.fragments.chinabuild.fragment.ShareDialogFragment;
import com.tradehero.th.fragments.chinabuild.fragment.ShareSellDialogFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionAllFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionBaseFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionCollegeFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionCreateFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionDetailFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionMineFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionSearchFragment;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.th.fragments.chinabuild.fragment.discovery.DiscoveryHotTopicFragment;
import com.tradehero.th.fragments.chinabuild.fragment.discovery.DiscoveryRecentNewsFragment;
import com.tradehero.th.fragments.chinabuild.fragment.discovery.DiscoveryStockGodNewsFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.DiscoveryDiscussSendFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.NotificationFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.SecurityDiscussSendFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard.AbsLeaderboardFragment;
import com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard.LeaderboardFromExchangeFragment;
import com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard.LeaderboardFromIndustryFragment;
import com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard.StockGodListBaseFragment;
import com.tradehero.th.fragments.chinabuild.fragment.moreLeaderboard.StockGodListMoreFragment;
import com.tradehero.th.fragments.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.th.fragments.chinabuild.fragment.portfolio.PositionDetailFragment;
import com.tradehero.th.fragments.chinabuild.fragment.search.SearchFragment;
import com.tradehero.th.fragments.chinabuild.fragment.security.BuySaleSecurityFragment;
import com.tradehero.th.fragments.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.th.fragments.chinabuild.fragment.security.SecurityDiscussOrNewsFragment;
import com.tradehero.th.fragments.chinabuild.fragment.test.FragmentTest01;
import com.tradehero.th.fragments.chinabuild.fragment.test.FragmentTest02;
import com.tradehero.th.fragments.chinabuild.fragment.test.FragmentTest03;
import com.tradehero.th.fragments.chinabuild.fragment.test.WebViewSimpleFragment;
import com.tradehero.th.fragments.chinabuild.fragment.trade.TradeOfChinaConceptFragment;
import com.tradehero.th.fragments.chinabuild.fragment.trade.TradeOfHotHoldFragment;
import com.tradehero.th.fragments.chinabuild.fragment.trade.TradeOfMineFragment;
import com.tradehero.th.fragments.chinabuild.fragment.trade.TradeOfRisePercentFragment;
import com.tradehero.th.fragments.chinabuild.fragment.trade.TradeOfTypeBaseFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserAccountPage;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserFriendsListFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.fragments.chinabuild.listview.SecurityExpandableListView;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.macquarie.MacquarieWarrantItemViewAdapter;
import com.tradehero.th.fragments.contestcenter.ContestCenterActiveFragment;
import com.tradehero.th.fragments.contestcenter.ContestCenterBaseFragment;
import com.tradehero.th.fragments.contestcenter.ContestCenterFragment;
import com.tradehero.th.fragments.contestcenter.ContestCenterJoinedFragment;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.AbstractDiscussionFragment;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemViewHolder;
import com.tradehero.th.fragments.discussion.DiscussionItemViewHolder;
import com.tradehero.th.fragments.discussion.DiscussionItemViewLinear;
import com.tradehero.th.fragments.discussion.DiscussionSetAdapter;
import com.tradehero.th.fragments.discussion.PrivateDiscussionSetAdapter;
import com.tradehero.th.fragments.discussion.SingleViewDiscussionSetAdapter;
import com.tradehero.th.fragments.discussion.TimelineItemViewHolder;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionFragment;
import com.tradehero.th.fragments.home.HomeFragment;
import com.tradehero.th.fragments.home.HomeWebView;
import com.tradehero.th.fragments.leaderboard.BaseLeaderboardFragment;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserItemView;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListClosedFragment;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserListOnGoingFragment;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardMarkUserOwnRankingView;
import com.tradehero.th.fragments.leaderboard.CompetitionLeaderboardTimedHeader;
import com.tradehero.th.fragments.leaderboard.FriendLeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefView;
import com.tradehero.th.fragments.leaderboard.LeaderboardFriendsItemView;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserItemView;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListAdapter;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListView;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserLoader;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserOwnRankingView;
import com.tradehero.th.fragments.leaderboard.filter.LeaderboardFilterFragment;
import com.tradehero.th.fragments.leaderboard.main.CommunityLeaderboardDefView;
import com.tradehero.th.fragments.leaderboard.main.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.location.LocationListFragment;
import com.tradehero.th.fragments.news.NewsDialogLayout;
import com.tradehero.th.fragments.news.NewsHeadlineFragment;
import com.tradehero.th.fragments.news.NewsHeadlineViewLinear;
import com.tradehero.th.fragments.news.NewsItemCompactViewHolder;
import com.tradehero.th.fragments.news.NewsItemViewHolder;
import com.tradehero.th.fragments.news.NewsViewLinear;
import com.tradehero.th.fragments.news.ShareDialogLayout;
import com.tradehero.th.fragments.portfolio.PortfolioListFragment;
import com.tradehero.th.fragments.portfolio.PortfolioListItemAdapter;
import com.tradehero.th.fragments.portfolio.PortfolioListItemView;
import com.tradehero.th.fragments.portfolio.SimpleOwnPortfolioListItemAdapter;
import com.tradehero.th.fragments.portfolio.header.OtherUserPortfolioHeaderView;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomClosedView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomInPeriodViewHolder;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomOpenView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.security.ChartFragment;
import com.tradehero.th.fragments.security.SecurityActionListLinear;
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
import com.tradehero.th.fragments.settings.SettingsReferralCodeFragment;
import com.tradehero.th.fragments.settings.UserFriendDTOView;
import com.tradehero.th.fragments.share.ShareDestinationSetAdapter;
import com.tradehero.th.fragments.social.AllRelationsFragment;
import com.tradehero.th.fragments.social.PeopleSearchFragment;
import com.tradehero.th.fragments.social.RelationsListItemView;
import com.tradehero.th.fragments.social.follower.AllFollowerFragment;
import com.tradehero.th.fragments.social.follower.FollowerListItemView;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerInfoFetcher;
import com.tradehero.th.fragments.social.follower.FollowerPayoutManagerFragment;
import com.tradehero.th.fragments.social.follower.FreeFollowerFragment;
import com.tradehero.th.fragments.social.follower.PremiumFollowerFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.social.friend.SocialFriendsFragmentLinkedIn;
import com.tradehero.th.fragments.social.friend.SocialFriendsFragmentWeibo;
import com.tradehero.th.fragments.social.hero.AllHeroFragment;
import com.tradehero.th.fragments.social.hero.FreeHeroFragment;
import com.tradehero.th.fragments.social.hero.HeroListItemView;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
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
import com.tradehero.th.fragments.trade.AbstractTransactionDialogFragment;
import com.tradehero.th.fragments.trade.BuyDialogFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.FreshQuoteHolder;
import com.tradehero.th.fragments.trade.SellDialogFragment;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trade.view.TradeListItemView;
import com.tradehero.th.fragments.translation.TranslatableLanguageListFragment;
import com.tradehero.th.fragments.trending.SearchPeopleItemView;
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
                MetricsModule.class,
                ModelsModule.class,
                UserModule.class,
                PreferenceModule.class,
                ChartModule.class,
                ActivityModule.class,
                BillingModule.class,
                PushModule.class,
        },
        injects =
                {
                        DashboardNavigator.class,
                        com.tradehero.th.base.Application.class,
                        SettingsProfileFragment.class,
                        ProfileInfoView.class,
                        SimplePremiumFollowUserAssistant.class,
                        PremiumFollowUserAssistant.class,
                        SettingsFragment.class,
                        TranslatableLanguageListFragment.class,
                        LocationListFragment.class,
                        SettingsReferralCodeFragment.class,
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
                        SearchPeopleItemView.class,
                        FreshQuoteHolder.class,
                        BuySellFragment.class,
                        AbstractTransactionDialogFragment.class,
                        BuyDialogFragment.class,
                        SellDialogFragment.class,
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
                        CompetitionLeaderboardPositionListFragment.class,
                        OtherUserPortfolioHeaderView.class,

                        PositionPartialTopView.class,
                        PositionPartialBottomClosedView.class,
                        PositionPartialBottomOpenView.class,
                        PositionLockedView.class,
                        PositionPartialBottomInPeriodViewHolder.class,

                        TradeListFragment.class,
                        TradeListItemView.class,

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

                        TimelineManager.class,

                        TimelineStore.class,
                        TimelineStore.Factory.class,

                        DatabaseCache.class,
                        CacheHelper.class,

                        TimelineFragment.class,
                        TimelineItemViewLinear.class,
                        UserProfileCompactViewHolder.class,
                        UserProfileDetailViewHolder.class,

                        LeaderboardCommunityFragment.class,
                        PeopleSearchFragment.class,
                        LeaderboardDefListFragment.class,
                        LeaderboardDefView.class,
                        CommunityLeaderboardDefView.class,
                        LeaderboardMarkUserLoader.class,
                        LeaderboardMarkUserListFragment.class,
                        BaseLeaderboardFragment.class,
                        LeaderboardMarkUserItemView.class,
                        CompetitionLeaderboardMarkUserItemView.class,
                        CompetitionLeaderboardMarkUserOwnRankingView.class,
                        LeaderboardMarkUserListAdapter.class,
                        LeaderboardMarkUserListView.class,
                        LeaderboardMarkUserOwnRankingView.class,
                        FriendLeaderboardMarkUserListFragment.class,
                        CompetitionLeaderboardMarkUserListFragment.class,
                        CompetitionLeaderboardMarkUserListClosedFragment.class,
                        CompetitionLeaderboardMarkUserListOnGoingFragment.class,
                        LeaderboardFilterFragment.class,
                        CompetitionLeaderboardTimedHeader.class,

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

                        AlertManagerFragment.class,
                        AlertEditFragment.class,
                        AlertCreateFragment.class,

                        InviteFriendFragment.class,

                        UserFriendDTOView.class,
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
                        LeaderboardFriendsItemView.class,
                        FriendsInvitationFragment.class,
                        ContestCenterFragment.class,
                        ContestCenterBaseFragment.class,
                        ContestCenterActiveFragment.class,
                        ContestCenterJoinedFragment.class,
                        //SocialFriendsFragmentFacebook.class,
                        //SocialFriendsFragmentTwitter.class,
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

                        DiscoveryHotTopicFragment.class,
                        DiscoveryStockGodNewsFragment.class,
                        DiscoveryRecentNewsFragment.class,

                        SearchFragment.class,
                        SettingFragment.class,

                        FragmentTest01.class,
                        FragmentTest02.class,
                        FragmentTest03.class,

                        SecurityListView.class,
                        SecurityExpandableListView.class,

                        LeaderboardListAdapter.class,
                        UserFriendsListAdapter.class,
                        SecurityListAdapter.class,
                        SecuritySearchListAdapter.class,
                        MyTradePositionListAdapter.class,

                        UserTimeLineAdapter.class,
                        SecurityTimeLineDiscussOrNewsAdapter.class,
                        TimeLineBaseAdapter.class,
                        PositionTradeListAdapter.class,

                        AbsLeaderboardFragment.class,
                        LeaderboardFromExchangeFragment.class,
                        LeaderboardFromIndustryFragment.class,

                        SecurityDetailFragment.class,
                        DiscussSendFragment.class,
                        BuySaleSecurityFragment.class,

                        SecurityDetailDialogLayout.class,

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
                        WebViewSimpleFragment.class,
                        NotificationFragment.class,
                        NotificationListAdapter.class,
                        TimeLineItemDetailFragment.class,
                        CompetitionCollegeFragment.class,

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
