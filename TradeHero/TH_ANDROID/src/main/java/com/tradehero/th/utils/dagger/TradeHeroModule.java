package com.tradehero.th.utils.dagger;

import android.app.Application;
import android.content.Context;
import com.squareup.picasso.Cache;
import com.squareup.picasso.Picasso;
import com.tradehero.common.cache.DatabaseCache;
import com.tradehero.common.cache.LruMemFileCache;
import com.tradehero.common.persistence.CacheHelper;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.form.AbstractUserAvailabilityRequester;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.WebViewFragment;
import com.tradehero.th.fragments.authentication.EmailSignInFragment;
import com.tradehero.th.fragments.leaderboard.AbstractLeaderboardFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardCommunityFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefListViewFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardDefView;
import com.tradehero.th.fragments.leaderboard.LeaderboardListViewFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserItemView;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserLoader;
import com.tradehero.th.fragments.portfolio.PortfolioListFragment;
import com.tradehero.th.fragments.portfolio.PushablePortfolioListFragment;
import com.tradehero.th.fragments.position.InPeriodPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.security.ChartFragment;
import com.tradehero.th.fragments.security.StockInfoFragment;
import com.tradehero.th.fragments.security.StockInfoValueFragment;
import com.tradehero.th.fragments.security.YahooNewsFragment;
import com.tradehero.th.fragments.settings.AboutFragment;
import com.tradehero.th.fragments.settings.SettingsFragment;
import com.tradehero.th.fragments.settings.SettingsPayPalFragment;
import com.tradehero.th.fragments.settings.SettingsTransactionHistoryFragment;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.fragments.trade.BuySellConfirmFragment;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.trade.FreshQuoteHolder;
import com.tradehero.th.fragments.trade.TradeListFragment;
import com.tradehero.th.fragments.trending.SearchStockPeopleFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorBasicFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorPriceFragment;
import com.tradehero.th.fragments.trending.TrendingFilterSelectorVolumeFragment;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.loaders.SearchStockPageItemListLoader;
import com.tradehero.th.loaders.TimelinePagedItemListLoader;
import com.tradehero.th.network.NetworkEngine;
import com.tradehero.th.network.YahooEngine;
import com.tradehero.th.network.service.LeaderboardService;
import com.tradehero.th.network.service.MarketService;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.network.service.PositionService;
import com.tradehero.th.network.service.ProviderService;
import com.tradehero.th.network.service.QuoteService;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.network.service.TradeService;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.network.service.UserTimelineService;
import com.tradehero.th.network.service.YahooNewsService;
import com.tradehero.th.persistence.leaderboard.LeaderboardManager;
import com.tradehero.th.persistence.portfolio.OwnedPortfolioFetchAssistant;
import com.tradehero.th.persistence.timeline.TimelineManager;
import com.tradehero.th.persistence.timeline.TimelineStore;
import com.tradehero.th.persistence.user.AbstractUserStore;
import com.tradehero.th.persistence.user.UserManager;
import com.tradehero.th.persistence.user.UserProfileFetchAssistant;
import com.tradehero.th.persistence.user.UserStore;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.widget.MarkdownTextView;
import com.tradehero.th.widget.ServerValidatedUsernameText;
import com.tradehero.th.widget.leaderboard.LeaderboardMarkUserListView;
import com.tradehero.th.widget.portfolio.PortfolioListItemView;
import com.tradehero.th.widget.portfolio.header.CurrentUserPortfolioHeaderView;
import com.tradehero.th.widget.portfolio.header.OtherUserPortfolioHeaderView;
import com.tradehero.th.widget.position.LockedPositionItem;
import com.tradehero.th.widget.position.PositionClosedView;
import com.tradehero.th.widget.position.PositionInPeriodClosedView;
import com.tradehero.th.widget.position.PositionInPeriodOpenView;
import com.tradehero.th.widget.position.PositionOpenView;
import com.tradehero.th.widget.position.partial.PositionPartialBottomClosedView;
import com.tradehero.th.widget.position.partial.PositionPartialBottomInPeriodClosedView;
import com.tradehero.th.widget.position.partial.PositionPartialBottomInPeriodOpenView;
import com.tradehero.th.widget.position.partial.PositionPartialBottomOpenView;
import com.tradehero.th.widget.position.partial.PositionPartialTopView;
import com.tradehero.th.widget.timeline.TimelineItemView;
import com.tradehero.th.widget.trade.TradeListHeaderView;
import com.tradehero.th.widget.trade.TradeListItemView;
import com.tradehero.th.widget.trade.TradeListOverlayHeaderView;
import com.tradehero.th.widget.trending.SearchPeopleItemView;
import com.tradehero.th.widget.trending.SecurityItemView;
import com.tradehero.th.widget.user.ProfileCompactView;
import com.tradehero.th.widget.user.ProfileView;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;
import org.ocpsoft.prettytime.PrettyTime;

/** Created with IntelliJ IDEA. User: tho Date: 9/16/13 Time: 5:36 PM Copyright (c) TradeHero */
@Module(
        injects =
        {
                UserProfileFetchAssistant.class,
                OwnedPortfolioFetchAssistant.class,

                SettingsTransactionHistoryFragment.class,
                SettingsPayPalFragment.class,
                SettingsFragment.class,
                AboutFragment.class,
                EmailSignInFragment.class,
                ServerValidatedUsernameText.UserAvailabilityRequester.class,
                ServerValidatedUsernameText.class,
                TrendingFragment.class,
                TrendingFilterSelectorBasicFragment.class,
                TrendingFilterSelectorVolumeFragment.class,
                TrendingFilterSelectorPriceFragment.class,
                SecurityItemView.class,
                SearchStockPeopleFragment.class,
                SearchPeopleItemView.class,
                BuySellFragment.class,
                TimelineFragment.class,
                MeTimelineFragment.class,
                PushableTimelineFragment.class,
                MarkdownTextView.class,

                TrendingFragment.class,
                FreshQuoteHolder.class,
                BuySellConfirmFragment.class,
                YahooNewsFragment.class,
                ChartFragment.class,
                StockInfoValueFragment.class,
                StockInfoFragment.class,
                PortfolioListFragment.class,
                PushablePortfolioListFragment.class,
                PortfolioListItemView.class,

                PositionListFragment.class,
                InPeriodPositionListFragment.class,
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
                TradeListItemView.class,
                TradeListOverlayHeaderView.class,
                TradeListHeaderView.class,

                AbstractUserAvailabilityRequester.class,
                SearchStockPageItemListLoader.class,
                TimelinePagedItemListLoader.class,

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
                LeaderboardListViewFragment.class,
                AbstractLeaderboardFragment.class,
                LeaderboardMarkUserItemView.class,
                LeaderboardMarkUserListView.class,

                WebViewFragment.class,
        },
        staticInjections =
        {
                THUser.class,
                NumberDisplayUtils.class,
        },
        library = true // TEMP while there is no MarketService user
)
public class TradeHeroModule
{
    public static final String TAG = TradeHeroModule.class.getSimpleName();

    private final Application application;
    private final NetworkEngine engine;
    private final YahooEngine yahooEngine;

    public TradeHeroModule(NetworkEngine engine, YahooEngine yahooEngine, Application application)
    {
        this.application = application;
        this.engine = engine;
        this.yahooEngine = yahooEngine;
    }

    @Provides @Singleton UserService provideUserService()
    {
        return engine.createService(UserService.class);
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

    @Provides @Singleton YahooNewsService provideYahooNewsService()
    {
        return yahooEngine.createService(YahooNewsService.class);
    }

    @Provides @Singleton AbstractUserStore provideUserStore(UserStore store)
    {
        return store;
    }

    @Provides @Singleton Picasso providePicasso()
    {
        Cache lruFileCache = null;
        try
        {
            lruFileCache = new LruMemFileCache(application);
            THLog.i(TAG, "Memory cache size " + lruFileCache.maxSize());
        }
        catch (Exception e)
        {
            THLog.e(TAG, "Failed to create LRU", e);
        }

        Picasso mPicasso = new Picasso.Builder(application)
                //.downloader(new UrlConnectionDownloader(getContext()))
                .memoryCache(lruFileCache)
                .build();
        //mPicasso.setDebugging(true);
        return mPicasso;
    }

    @Provides Context provideContext()
    {
        return application.getApplicationContext();
    }

    @Provides /*@Named("CurrentUser")*/ UserBaseDTO provideSomeUserBaseDTO()
    {
        return THUser.getCurrentUserBase();
    }

    @Provides @Named("CurrentUser") UserBaseDTO provideCurrentUserBaseDTO()
    {
        return THUser.getCurrentUserBase();
    }

    @Provides @Singleton CurrentUserBaseKeyHolder provideCurrentUserBaseKeyHolder()
    {
        return new CurrentUserBaseKeyHolder();
    }

    @Provides PrettyTime providePrettyTime()
    {
        return new PrettyTime();
    }
}
