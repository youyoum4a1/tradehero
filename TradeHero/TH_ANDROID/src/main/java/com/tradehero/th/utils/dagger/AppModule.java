package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshExpandableListView;
import com.tradehero.chinabuild.MainTabFragmentCompetition;
import com.tradehero.chinabuild.MainTabFragmentDiscovery;
import com.tradehero.chinabuild.MainTabFragmentLearning;
import com.tradehero.chinabuild.MainTabFragmentStockGod;
import com.tradehero.chinabuild.MainTabFragmentTrade;
import com.tradehero.chinabuild.dialog.SecurityDetailDialogLayout;
import com.tradehero.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.chinabuild.dialog.TimeLineCommentDialogLayout;
import com.tradehero.chinabuild.dialog.TimeLineDetailDialogLayout;
import com.tradehero.chinabuild.dialog.TimeLineReportDialogLayout;
import com.tradehero.chinabuild.fragment.BindEmailSignUpFragment;
import com.tradehero.chinabuild.fragment.BindGuestUserFragment;
import com.tradehero.chinabuild.fragment.InputInviteCodeFragment;
import com.tradehero.chinabuild.fragment.InviteFriendsFragment;
import com.tradehero.chinabuild.fragment.LoginSuggestDialogFragment;
import com.tradehero.chinabuild.fragment.MyEditAccountFragment;
import com.tradehero.chinabuild.fragment.MyEditNameFragment;
import com.tradehero.chinabuild.fragment.MyProfileFragment;
import com.tradehero.chinabuild.fragment.MySocialFragment;
import com.tradehero.chinabuild.fragment.SettingFragment;
import com.tradehero.chinabuild.fragment.SettingsAboutUsFragment;
import com.tradehero.chinabuild.fragment.ShareDialogFragment;
import com.tradehero.chinabuild.fragment.ShareSellDialogFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionCollegeFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionCreateFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionDetailFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionDiscussFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionDiscussionSendFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionMainFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionMineFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionSearchFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionsFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryEssentialFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryLearningFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryNewsFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryRecentNewsFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryRewardFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoverySquareFragment;
import com.tradehero.chinabuild.fragment.discovery.DiscoveryStockGodNewsFragment;
import com.tradehero.chinabuild.fragment.discovery.NewsDetailFragment;
import com.tradehero.chinabuild.fragment.leaderboard.StockGodListBaseFragment;
import com.tradehero.chinabuild.fragment.message.DiscoveryDiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.NotificationFragment;
import com.tradehero.chinabuild.fragment.message.SecurityDiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.fragment.portfolio.PortfolioFragment;
import com.tradehero.chinabuild.fragment.portfolio.PositionDetailFragment;
import com.tradehero.chinabuild.fragment.search.SearchFragment;
import com.tradehero.chinabuild.fragment.search.SearchUniteFragment;
import com.tradehero.chinabuild.fragment.security.BasePurchaseManagerFragment;
import com.tradehero.chinabuild.fragment.security.BuySaleSecurityFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDiscussOrNewsFragment;
import com.tradehero.chinabuild.fragment.stocklearning.AnswerQuestionFragment;
import com.tradehero.chinabuild.fragment.stocklearning.AnswersSummaryFragment;
import com.tradehero.chinabuild.fragment.stocklearning.OneQuestionFragment;
import com.tradehero.chinabuild.fragment.stocklearning.PublicClassFragment;
import com.tradehero.chinabuild.fragment.stocklearning.QuestionsFragment;
import com.tradehero.chinabuild.fragment.stocklearning.StockLearningHistoryFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfChinaConceptFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfHotHoldFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfMineFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfRisePercentFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfTypeBaseFragment;
import com.tradehero.chinabuild.fragment.userCenter.MyMainPage;
import com.tradehero.chinabuild.fragment.userCenter.MyMainSubPage;
import com.tradehero.chinabuild.fragment.userCenter.SettingMineFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserAccountPage;
import com.tradehero.chinabuild.fragment.userCenter.UserFansListFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserHeroesListFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.fragment.web.WebViewFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.persistence.CacheHelper;
import com.tradehero.th.activities.GuideActivity;
import com.tradehero.th.adapters.CNPersonTradePositionListAdapter;
import com.tradehero.th.adapters.CompetitionListAdapter;
import com.tradehero.th.adapters.LeaderboardListAdapter;
import com.tradehero.th.adapters.MyTradePositionListAdapter;
import com.tradehero.th.adapters.NewsItemAdapter;
import com.tradehero.th.adapters.NotificationListAdapter;
import com.tradehero.th.adapters.PositionTradeListAdapter;
import com.tradehero.th.adapters.SearchUserListAdapter;
import com.tradehero.th.adapters.SecurityListAdapter;
import com.tradehero.th.adapters.SecuritySearchListAdapter;
import com.tradehero.th.adapters.SecurityTimeLineDiscussOrNewsAdapter;
import com.tradehero.th.adapters.TimeLineBaseAdapter;
import com.tradehero.th.adapters.TimeLineDetailDiscussSecItem;
import com.tradehero.th.adapters.UserFriendsListAdapter;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.adapters.VideoGridAdapter;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.authentication.EmailSignUpFragment;
import com.tradehero.th.fragments.authentication.PasswordResetFragment;
import com.tradehero.th.fragments.authentication.SignInFragment;
import com.tradehero.th.fragments.competition.macquarie.MacquarieWarrantItemViewAdapter;
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
import com.tradehero.th.fragments.news.NewsDialogLayout;
import com.tradehero.th.fragments.news.NewsHeadlineFragment;
import com.tradehero.th.fragments.news.NewsHeadlineViewLinear;
import com.tradehero.th.fragments.news.NewsItemCompactViewHolder;
import com.tradehero.th.fragments.news.NewsItemViewHolder;
import com.tradehero.th.fragments.news.NewsViewLinear;
import com.tradehero.th.fragments.news.ShareDialogLayout;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.LeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomClosedView;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomInPeriodViewHolder;
import com.tradehero.th.fragments.position.partial.PositionPartialBottomOpenView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.fragments.position.view.PositionLockedView;
import com.tradehero.th.fragments.security.SecurityActionListLinear;
import com.tradehero.th.fragments.security.SecurityItemView;
import com.tradehero.th.fragments.security.SecurityItemViewAdapter;
import com.tradehero.th.fragments.security.SecuritySearchFragment;
import com.tradehero.th.fragments.security.SecuritySearchProviderFragment;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.security.StockInfoValueFragment;
import com.tradehero.th.fragments.security.WarrantInfoValueFragment;
import com.tradehero.th.fragments.security.WarrantSecurityItemView;
import com.tradehero.th.fragments.security.WatchlistEditFragment;
import com.tradehero.th.fragments.share.ShareDestinationSetAdapter;
import com.tradehero.th.fragments.social.PeopleSearchFragment;
import com.tradehero.th.fragments.social.friend.FriendsInvitationFragment;
import com.tradehero.th.fragments.social.friend.SocialFriendsFragmentWeibo;
import com.tradehero.th.fragments.social.message.AbstractPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.fragments.social.message.PrivateDiscussionView;
import com.tradehero.th.fragments.social.message.PrivateMessageBubbleViewLinear;
import com.tradehero.th.fragments.social.message.ReplyPrivateMessageFragment;
import com.tradehero.th.fragments.timeline.TimelineItemViewLinear;
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
import com.tradehero.th.fragments.watchlist.WatchlistItemView;
import com.tradehero.th.fragments.watchlist.WatchlistPortfolioHeaderView;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.loaders.SearchStockPageListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.loaders.security.macquarie.MacquarieSecurityListPagedLoader;
import com.tradehero.th.models.ModelsModule;
import com.tradehero.th.models.chart.ChartModule;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.models.user.SimplePremiumFollowUserAssistant;
import com.tradehero.th.network.NetworkModule;
import com.tradehero.th.persistence.prefs.LanguageCode;
import com.tradehero.th.persistence.prefs.PreferenceModule;
import com.tradehero.th.persistence.timeline.TimelineManager;
import com.tradehero.th.persistence.timeline.TimelineStore;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.ui.UIModule;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.metrics.MetricsModule;
import com.tradehero.th.widget.MarkdownTextView;
import com.tradehero.th.widget.TradeHeroProgressBar;
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

                        SearchStockPageListLoader.class,
                        TimelineListLoader.class,

                        TimelineManager.class,

                        TimelineStore.class,
                        TimelineStore.Factory.class,

                        CacheHelper.class,

                        TimelineItemViewLinear.class,

                        PeopleSearchFragment.class,

                        WebViewFragment.class,
                        UserWatchlistPositionCache.class,
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
                        SocialFriendsFragmentWeibo.class,
                        GuideActivity.class,

                        //China Build Start
                        MainTabFragmentTrade.class,
                        MainTabFragmentLearning.class,
                        SettingMineFragment.class,
                        MainTabFragmentDiscovery.class,
                        MainTabFragmentStockGod.class,
                        MainTabFragmentCompetition.class,

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
                        BasePurchaseManagerFragment.class,
                        DiscussSendFragment.class,
                        BuySaleSecurityFragment.class,

                        SecurityDetailDialogLayout.class,
                        TimeLineDetailDialogLayout.class,
                        TimeLineCommentDialogLayout.class,
                        TimeLineReportDialogLayout.class,

                        CompetitionMineFragment.class,
                        CompetitionCreateFragment.class,
                        CompetitionSearchFragment.class,
                        CompetitionsFragment.class,
                        CompetitionDetailFragment.class,

                        CompetitionListAdapter.class,
                        CompetitionSecuritySearchFragment.class,

                        PortfolioFragment.class,
                        UserMainPage.class,
                        MyMainPage.class,
                        MyMainSubPage.class,
                        SecurityDiscussOrNewsFragment.class,
                        InviteFriendsFragment.class,
                        InputInviteCodeFragment.class,
                        UserTimeLineAdapter.class,
                        MyProfileFragment.class,
                        MyEditNameFragment.class,
                        MyEditAccountFragment.class,
                        MySocialFragment.class,
                        UserHeroesListFragment.class,
                        UserFansListFragment.class,
                        UserAccountPage.class,

                        BindGuestUserFragment.class,
                        BindEmailSignUpFragment.class,
                        ShareSellDialogFragment.class,
                        SecurityDiscussSendFragment.class,
                        DiscoveryDiscussSendFragment.class,
                        SettingsAboutUsFragment.class,
                        PositionDetailFragment.class,
                        VideoGridAdapter.class,

                        ShareDialogFragment.class,
                        ShareSheetDialogLayout.class,
                        PositionTradeListAdapter.class,
                        WebViewFragment.class,
                        NotificationFragment.class,
                        NotificationListAdapter.class,
                        TimeLineItemDetailFragment.class,
                        TimeLineDetailDiscussSecItem.class,
                        CompetitionCollegeFragment.class,
                        CompetitionMainFragment.class,
                        CompetitionDiscussFragment.class,
                        CompetitionDiscussionSendFragment.class,
                        NewsItemAdapter.class,

                        LoginSuggestDialogFragment.class,

                        //Stock Learning
                        PublicClassFragment.class,
                        QuestionsFragment.class,
                        AnswersSummaryFragment.class,
                        StockLearningHistoryFragment.class,
                        OneQuestionFragment.class,
                        AnswerQuestionFragment.class,
                        //Stock Learning

                        SignInFragment.class,
                        EmailSignUpFragment.class,

                        //China Build End
                },
        staticInjections =
                {
                        THUser.class,
                },
        complete = false,
        library = true
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
