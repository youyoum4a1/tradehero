package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshExpandableListView;
import com.tradehero.chinabuild.MainTabFragmentCompetition;
import com.tradehero.chinabuild.MainTabFragmentDiscovery;
import com.tradehero.chinabuild.MainTabFragmentStockGod;
import com.tradehero.chinabuild.MainTabFragmentTrade;
import com.tradehero.chinabuild.SettingMineFragment;
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
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.chinabuild.fragment.security.BasePurchaseManagerFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailSubDiscussFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailSubNewsFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailSubOptFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDetailSubPositionFragment;
import com.tradehero.chinabuild.fragment.security.SecurityDiscussOrNewsFragment;
import com.tradehero.chinabuild.fragment.security.SecurityOptMockSubBuyFragment;
import com.tradehero.chinabuild.fragment.security.SecurityOptMockSubDelegationFragment;
import com.tradehero.chinabuild.fragment.security.SecurityOptMockSubQueryFragment;
import com.tradehero.chinabuild.fragment.security.SecurityOptMockSubSellFragment;
import com.tradehero.chinabuild.fragment.security.SecurityUserOptFragment;
import com.tradehero.chinabuild.fragment.security.SecurityUserPositionFragment;
import com.tradehero.chinabuild.fragment.stocklearning.AnswerQuestionFragment;
import com.tradehero.chinabuild.fragment.stocklearning.AnswersSummaryFragment;
import com.tradehero.chinabuild.fragment.stocklearning.OneQuestionFragment;
import com.tradehero.chinabuild.fragment.stocklearning.PublicClassFragment;
import com.tradehero.chinabuild.fragment.stocklearning.QuestionsFragment;
import com.tradehero.chinabuild.fragment.stocklearning.StockLearningHistoryFragment;
import com.tradehero.chinabuild.fragment.stocklearning.StockLearningMainFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfChinaConceptFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfHotHoldFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfMineFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfRisePercentFragment;
import com.tradehero.chinabuild.fragment.trade.TradeOfTypeBaseFragment;
import com.tradehero.chinabuild.fragment.userCenter.MyMainPage;
import com.tradehero.chinabuild.fragment.userCenter.MyMainSubPage;
import com.tradehero.chinabuild.fragment.userCenter.UserAccountPage;
import com.tradehero.chinabuild.fragment.userCenter.UserFansListFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserHeroesListFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.fragment.web.WebViewFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.persistence.CacheHelper;
import com.tradehero.firmbargain.SecurityOptActualFragment;
import com.tradehero.firmbargain.SecurityOptActualSubBuyFragment;
import com.tradehero.firmbargain.SecurityOptActualSubSellFragment;
import com.tradehero.firmbargain.SecurityOptActualSubDelegationFragment;
import com.tradehero.firmbargain.SecurityOptActualSubQueryFragment;
import com.tradehero.th.activities.GuideActivity;
import com.tradehero.th.activities.SearchSecurityActualActivity;
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
import com.tradehero.th.fragments.social.friend.SocialFriendsFragmentWeibo;
import com.tradehero.th.fragments.trade.FreshQuoteHolder;
import com.tradehero.th.loaders.FriendListLoader;
import com.tradehero.th.loaders.SearchStockPageListLoader;
import com.tradehero.th.loaders.TimelineListLoader;
import com.tradehero.th.loaders.security.SecurityListPagedLoader;
import com.tradehero.th.loaders.security.macquarie.MacquarieSecurityListPagedLoader;
import com.tradehero.th.models.ModelsModule;
import com.tradehero.th.models.chart.ChartModule;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.models.user.PremiumFollowUserAssistant;
import com.tradehero.th.models.user.SimplePremiumFollowUserAssistant;
import com.tradehero.th.network.NetworkModule;
import com.tradehero.th.network.service.QuoteServiceWrapper;
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
                        EmailSignInFragment.class,
                        PasswordResetFragment.class,
                        SecurityListPagedLoader.class,
                        SearchUnitFragment.class,
                        MacquarieSecurityListPagedLoader.class,
                        FreshQuoteHolder.class,
                        MarkdownTextView.class,
                        QuoteServiceWrapper.QuoteDTORepeatingTaskCallBack.class,
                        QuoteServiceWrapper.QuoteDTOCallBack.class,

                        DisplayablePortfolioFetchAssistant.class,

                        SearchStockPageListLoader.class,
                        TimelineListLoader.class,

                        TimelineManager.class,

                        TimelineStore.class,
                        TimelineStore.Factory.class,

                        CacheHelper.class,

                        WebViewFragment.class,
                        UserWatchlistPositionCache.class,

                        FriendListLoader.class,

                        MessageHeaderDTO.class,
                        AlertDialogUtil.class,
                        SocialFriendsFragmentWeibo.class,
                        GuideActivity.class,

                        //China Build Start
                        MainTabFragmentTrade.class,
                        StockLearningMainFragment.class,
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
                        SecurityUserOptFragment.class,
                        SecurityUserPositionFragment.class,
                        SecurityDetailSubDiscussFragment.class,
                        SecurityDetailSubNewsFragment.class,
                        SecurityDetailSubOptFragment.class,
                        SecurityDetailSubPositionFragment.class,
                        SecurityOptMockSubQueryFragment.class,
                        SecurityOptMockSubDelegationFragment.class,
                        SecurityOptActualSubDelegationFragment.class,
                        SecurityOptActualSubQueryFragment.class,

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
                        OneQuestionFragment.class,
                        //Stock Learning

                        SignInFragment.class,
                        EmailSignUpFragment.class,

                        //Security Opt Mock
                        SecurityOptMockSubSellFragment.class,
                        SecurityOptMockSubBuyFragment.class,
                        //Security Opt Mock

                        //Security Opt Actual
                        SearchSecurityActualActivity.class,
                        SecurityOptActualSubBuyFragment.class,
                        SecurityOptActualSubSellFragment.class,
                        SecurityOptActualFragment.class
                        //Security Opt Actual

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
