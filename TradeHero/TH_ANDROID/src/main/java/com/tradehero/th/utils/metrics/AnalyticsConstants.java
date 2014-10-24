package com.tradehero.th.utils.metrics;

public class AnalyticsConstants
{
    /** Analytics Global Release
    public static final String AppLaunch = "AppLaunch";
    public static final String TabBar_Me = "TabBar/Me";
    public static final String TabBar_Trade = "TabBar/Trade";
    public static final String TabBar_Trending = "TabBar/Trending";
    public static final String TabBar_Community = "TabBar/Community";
    public static final String TabBar_Portfolio = "TabBar/Portfolio";
    public static final String TabBar_Store = "TabBar/Store";
    public static final String TabBar_Settings = "TabBar/Settings";
    public static final String SignUp = "SignUp"; //User tap sign up on the landing

    public static final String SignUp_Success = "SignUp/Success"; //User tap sign up on the landing
    public static final String SignIn = "SignIn"; //User tap sign in on the landing

    public static final String Authentication_Facebook = "SignUp/Facebook"; // User tap on sign up via Facebook on sign up screen
    public static final String Authentication_LinkedIn = "SignUp/LinkedIn"; // User tap on sign up via LinkedIn
    public static final String Authentication_Twitter = "SignUp/Twitter"; // User tap on sign up via Twitter
    public static final String SignUp_Back = "SignUp/Back"; // User press back button on sign
    public static final String Settings_PayPal = "Settings/PayPal";
    public static final String Settings_Alipay = "Settings/Alipay";
    public static final String Settings_FAQ = "Settings/FAQ";
    public static final String Settings_TransactionHistory = "Settings/TransactionHistory";
    public static final String Referrals_Settings = "Referrals_Settings";
    public static final String Settings_About = "Settings/About";
    public static final String ResetPortfolioPopUp_Show = "ResetPortfolioPopUp/Show";
    public static final String ResetPortfolioPopUp_Buy = "ResetPortfolioPopUp/Buy";
    public static final String ResetPortfolio_Close = "ResetPortfolio/Close";
    public static final String LowCashPopUp_Show = "LowCashPopUp/Show";
    public static final String LowCashPopUp_Buy = "LowCashPopUp/Buy";
    public static final String LowCashPopUp_Close = "LowCashPopUp/Close";
    public static final String Heroes_BuyMoreCredits = "Heroes/BuyMoreCredits";// Anytime user buy more hero
    public static final String ExtraCash_BuySellScreen = "ExtraCash/BuySellScreen";// Buying extra cash on buy/sell
    public static final String StockAlerts_BuySellScreen = "StockAlerts/BuySellScreen";//
    public static final String StockAlerts_GetAlerts = "StockAlerts/GetAlerts";// Buy stock alerts IAP
    public static final String SearchResult_Stock = "SearchResult/Stock";// stock NASDAQ:AA
    public static final String SearchResult_User = "SearchResult/User";// user
    public static final String Watchlist_List = "Watchlist/List";//
    public static final String Watchlist_Edit = "Watchlist/Edit";// symbol security.qualiﬁ
    public static final String Watchlist_Add = "Watchlist/Add";// symbol security.qualiﬁ
    public static final String Watchlist_Delete = "Watchlist/Delete";// symbol deletedPositio
    public static final String Watchlist_CellSwipe = "Watchlist/CellSwipe";// symbol self.security.q
    public static final String Watchlist_More_Tap = "Watchlist/More/Tap";// symbol self.security.q
    public static final String Positions_Follow = "Positions/Follow";// Following user from positions
    public static final String Position_CellSwipe = "Position/CellSwipe";// symbol
    public static final String Position_More_Tap = "Position/More/Tap";// symbol
    public static final String Leaderboards_ShowLeaderboard = "Leaderboards/ShowLeaderboard";//
    public static final String Leaderboard_Profile = "Leaderboard/Profile";// Tap proﬁle on leaderboard user
    public static final String Leaderboard_Positions = "Leaderboard/Positions";// Tap positins on leaderboard user
    public static final String Leaderboard_Follow = "Leaderboard/Follow";// Tap follow on leaderboard user
    public static final String Leaderboard_Back = "Leaderboard/Back";// Tap back on leaderboard leaderboardId
    public static final String Leaderboard_FilterShow = "Leaderboard/FilterShow";// Leaderboard ﬁlter screen is
    public static final String Leaderboard_FilterChange = "Leaderboard/FilterChange";//
    public static final String Leaderboard_FilterCancel = "Leaderboard/FilterCancel";// User tap cancel on leaderboard
    public static final String Leaderboard_FilterReset = "Leaderboard/FilterReset";// User tap reset on leaderboard
    public static final String Leaderboard_FilterDone = "Leaderboard/FilterDone";//
    public static final String FriendsLeaderboard_Filter_FoF = "FriendsLeaderboard/Filter/FoF";//
    public static final String Leaderboards_DrillDown = "Leaderboards/DrillDown";// User tap on leaderboard under
    public static final String Security_TapChartPeriod_Landscape = "Security/TapChartPeriod/Landscape";//
    public static final String Security_TapChartPeriod_Portrait = "Security/TapChartPeriod/Portrait";//
    public static final String Security_TapPrice = "Security/TapPrice";// symbol self.security.q
    public static final String Security_RotateToLandscapeChart ="Security/RotateToLandscapeChart";//
    public static final String Security_NewDiscussion = "Security/NewDiscussion";// symbol self.security.q
    public static final String Security_Tab_Discussion = "Security/Tab/Discussion";// symbol self.security.q
    public static final String Security_Tab_News = "Security/Tab/News";// symbol self.security.q
    public static final String Security_Tab_Info = "Security/Tab/Info";// symbol self.security.q
    public static final String Proﬁle_Tab_Timeline = "Proﬁle/Tab/Timeline";//
    public static final String Proﬁle_Follow = "Proﬁle/Follow";//
    public static final String Notification_New_Message = "Notification/NewMessgae";//
    public static final String Notification_New_Broadcast = "Notification/NewBroadcast";//
    public static final String Proﬁle_Tab_Portfolios = "Proﬁle/Tab/Portfolios";// Portfolios tab is shown on user
    public static final String Proﬁle_Tab_Stats = "Proﬁle/Tab/Stats";// Stats tab
    public static final String Monitor_Show = "Monitor/Show";// source
    public static final String Monitor_Cancel = "Monitor/Cancel";// source
    public static final String Monitor_EditWatchlist = "Monitor/EditWatchlist";// source
    public static final String Monitor_CreateWatchlist = "Monitor/CreateWatchlist";// source
    public static final String Monitor_Alert = "Monitor/Alert";// source
    public static final String Monitor_Chart = "Monitor/Chart";// source
    public static final String Monitor_BuySell = "Monitor/BuySell";// source
    public static final String Monitor_TradeHistory = "Monitor/TradeHistory";// source
    public static final String StockAlerts_Save = "StockAlerts/Save";// Saving stock alert on stock alert

    public static final String BuySellPanel_Chart = "BuySellPanel/Chart_%s"; // Open the buy sell screen with a chart

    public static final String Default = "Default";//
    public static final String DefaultPriceSelectionMethod = Default;//
    public static final String Email = "Email";//
    public static final String Facebook = "Facebook";//

    public static final String FreeFollow_Success = "FreeFollow/Success";
    public static final String InviteFriends = "InviteFriends";//
    public static final String Leaderboard = "Leaderboard";
    public static final String Linkedin = "Linkedin";//
    public static final String Loading = "Loading";//
    public static final String LoadingScreen = "LoadingScreen";//
    public static final String Login_Form = "LoginForm";//
    public static final String LoginFormScreen = "LoginFormScreen";//
    public static final String Login_Register = "Login/Register";//
    public static final String LoginRegisterScreen = "LoginRegisterScreen";//
    public static final String ManualQuantityInput = "Manual Quantity Input";//Select the quantity using numeric keypad
    public static final String MessageComposer_Show = "MessageComposer/Show";//
    public static final String MessageComposer_Send = "MessageComposer/Send";//
    public static final String MoneySelection = "MoneySelection";//
    public static final String PositionList = "PositionList";
    public static final String PremiumFollow_Success = "PremiumFollow/Success";
    public static final String Profile = "Profile";
    public static final String QQ = "QQ";//
    public static final String WECHAT = "Wechat";//
    public static final String Register_Form = "RegisterForm";//
    public static final String RegisterFormScreen = "RegisterFormScreen";//
    public static final String Screen = "Screen ";//
    public static final String Slider = "Slider";//
    public static final String Splash = "Splash";//
    public static final String SplashScreen = "SplashScreen";//
    public static final String SplashScreenCancel = "SplashScreenCancel";//
    public static final String Trade_Buy = "Trade/Buy";//
    public static final String Trade_Sell = "Trade/Sell";//
    public static final String Twitter = "Twitter";//
    public static final String WeiBo = "WeiBo";//
    **/

    public static final String PrivateMessage = "Private Message";
    public static final String BroadcastAllFollowers = "Broadcast All Followers";
    public static final String BroadcastFreeFollowers = "Broadcast Free Followers";
    public static final String BroadcastPremiumFollowers = "Broadcast Premium Followers";
    public static final String BuyCreditsDialog_Show = "BuyCreditsDialog/Show";
    public static final String BuyExtraCashDialog_Show = "BuyExtraCashDialog/Show";
    public static final String BuyStockAlertDialog_Show = "BuyStockAlertDialog/Show";
    public static final String ResetPortfolioDialog_Show = "ResetPortfolioDialog/Show";
    public static final String FollowedFromScreen = "FollowedFromScreen";
    public static final String Trade_Buy = "Trade/Buy";
    public static final String Trade_Sell = "Trade/Sell";
    public static final String PickChart = "PickChart/%s"; // Changing a chart
    public static final String TrendingStock = "TrendingStock"; // Tapping on a stock item in Trending screen
    public static final String TabBar_Trending = "TabBar/Trending";
    public static final String TabBar_Settings = "TabBar/Settings";

    //Splash & Guide
    public static final String Splash = "Splash";
    public static final String SplashScreen = "SplashScreen";
    public static final String SplashScreenCancel = "SplashScreenCancel";
    public static final String Email = "Email";
    public static final String Screen = "Screen ";

    public static final String SignUp_Success = "SignUp/Success"; //User tap sign up on the landing

    public static final String SignUp_Email = "SignUp/Email"; //User tap on sign up via email on

    //Launch app
    public static final String AppLaunch = "AppLaunch";

    public static final String CHINA_BUILD_BUTTON_CLICKED = "china_build_button_clicked";

    //Go to login
    public static final String Login_Register = "Login/Register";
    public static final String LoginRegisterScreen = "LoginRegisterScreen";

    //Sign Up
    public static final String SignUp_Tap = "SignUp/Tap"; //User tap sign up on the landing
    public static final String Twitter = "Twitter";
    public static final String WeiBo = "WeiBo";
    public static final String QQ = "QQ";
    public static final String WECHAT = "Wechat";
    public static final String Linkedin = "Linkedin";

    //Analytics Palmer

    //打开软件看到介绍图片，浏览到第几个页面点击或马上体验和登陆
    public static final String SIGN_IN_ANONYMOUS = "SIGN_IN_ANONYMOUS";
    public static final String SIGN_IN_ACCOUNT = "SIGN_IN_ACCOUNT";

    //我的页面，个人主页的点击量，设置点击量，我的动态点击量，邀请好友的点击量
    public static final String MINE_PERSONAL_PAGE = "MINE_PERSONAL_PAGE";
    public static final String MINE_SETTING = "MINE_SETTING";
    public static final String MINE_INVITE_FRIENDS = "MINE_INVITE_FRIENDS";
    public static final String MINE_MY_MOMENT = "MINE_MY_MOMENT";

    //个股页面每条讨论，每条资讯的点赞，评论，分享的点击量
    public static final String USER_PAGE_PRAISE = "USER_PAGE_PRAISE";
    public static final String USER_PAGE_COMMENT = "USER_PAGE_COMMENT";
    public static final String USER_PAGE_SHARE = "USER_PAGE_SHARE";

    //发现页面，最新动态中每条咨询你的点击量，点赞次数，点评论次数，点击分享次数
    public static final String DISCOVERY_MESSAGE_CENTER = "DISCOVERY_MESSAGE_CENTER";
    public static final String DISCOVERY_ITEM_PERSON = "DISCOVERY_ITEM_PERSON";
    public static final String DISCOVERY_ITEM_PRAISE = "DISCOVERY_ITEM_PRAISE";
    public static final String DISCOVERY_ITEM_COMMENT = "DISCOVERY_ITEM_COMMENT";
    public static final String DISCOVERY_ITEM_SHARE = "DISCOVERY_ITEM_SHARE";

    //交易，股神，发现，竞赛，我的这五块的点击量及默认点击量
    public static final String MAIN_PAGE_TRADE = "MAIN_PAGE_TRADE";
    public static final String MAIN_PAGE_STOCK = "MAIN_PAGE_STOCK";
    public static final String MAIN_PAGE_DISCOVERY = "MAIN_PAGE_DISCOVERY";
    public static final String MAIN_PAGE_COMPETITION = "MAIN_PAGE_COMPETITION";
    public static final String MAIN_PAGE_MINE = "MAIN_PAGE_MINE";

    //交易中我的交易，热门关注，热门持有，中国概念的点击量，右上角的搜索点击量
    public static final String TRADE_PAGE_MINE_TRADE = "TRADE_PAGE_MINE_TRADE";
    public static final String TRADE_PAGE_RISE = "TRADE_PAGE_RISE";
    public static final String TRADE_PAGE_HOLD = "TRADE_PAGE_HOLD";
    public static final String TRADE_PAGE_CHINA = "TRADE_PAGE_CHINA";
    public static final String TRADE_PAGE_SEARCH = "TRADE_PAGE_SEARCH";

    //交易中热门关注和热门持有的证券切换内容及次数
    public static final String TRADE_PAGE_HOLD_PARTIES = "TRADE_PAGE_HOLD_PARTIES";
    public static final String TRADE_PAGE_RISE_PARTIES = "TRADE_PAGE_RISE_PARTIES";

    //我的页面点击进去后，头像和名字的点击量
    public static final String ME_PERSONAL_INFORMATION_AVATAR = "ME_PERSONAL_INFORMATION_AVATAR";
    public static final String ME_PERSONAL_INFORMATION_NAME = "ME_PERSONAL_INFORMATION_NAME";

    //我的页面，总资产，股神，粉丝点击数
    public static final String ME_TOTAL_PROPERTY = "ME_TOTAL_PROPERTY";
    public static final String ME_STOCK_HEROES = "ME_STOCK_HEROES";
    public static final String ME_STOCK_FOLLOWER = "ME_STOCK_FOLLOWER";

    //设置内去评分，FAQ，推送通知
    public static final String SETTING_SCORE = "SETTING_SCORE";
    public static final String SETTING_FAQ = "SETTING_FAQ";
    public static final String SETTING_NOTIFICAITONS_ON_OFF = "SETTING_NOTIFICAITONS_ON_OFF";

    //Palmer


    //Analytics Windy
    //股神中每个榜单内每个用户的点击量
    public static final String LEADERBOARD_USER_CLICKED_POSITION = "LEADERBOARD_USER_CLICKED_POSITION";

    //股神中推荐榜，人气榜，土豪榜，更多榜，总盈利榜，季度榜，半年榜
    public static final String BUTTON_STOCK_ROI = "BUTTON_STOCK_ROI";
    public static final String BUTTON_STOCK_HOT = "BUTTON_STOCK_HOT";
    public static final String BUTTON_STOCK_WEALTH = "BUTTON_STOCK_WEALTH";
    public static final String BUTTON_STOCK_MORE = "BUTTON_STOCK_MORE";
    public static final String BUTTON_STOCK_MOST_SKILL = "BUTTON_STOCK_MOST_SKILL";
    public static final String BUTTON_STOCK_90DAY = "BUTTON_STOCK_90DAY";
    public static final String BUTTON_STOCK_180DAY = "BUTTON_STOCK_180DAY";

    //竞赛中所有比赛和我的比赛及这两个栏目的banner条的点击量，及右上角的搜索量
    public static final String BUTTON_COMPETITION_DETAIL_BANNER = "BUTTON_COMPETITION_DETAIL_BANNER";
    public static final String BUTTON_COMPETITION_DETAIL_SEARCH = "BUTTON_COMPETITION_DETAIL_SEARCH";

    //竞赛中所有比赛的，官方比赛和热点比赛的点击量，创建比赛的点击量，右上角的搜索量
    //竞赛中我的比赛的，官方比赛和热点比赛的点击量，创建比赛的点击量，右上角的搜索量
    public static final String BUTTON_COMPETITION_DETAIL_LIST_ITEM = "BUTTON_COMPETITION_DETAIL_LIST_ITEM";
    public static final String BUTTON_COMPETITION_DETAIL_CREATE = "BUTTON_COMPETITION_DETAIL_CREATE";

    //每个比赛的比赛详情，参加比赛和去比赛的点击量，邀请好友的点击量，排行中每个用户的点击量。
    public static final String BUTTON_COMPETITION_DETAIL_JOIN = "BUTTON_COMPETITION_DETAIL_JOIN";
    public static final String BUTTON_COMPETITION_DETAIL_GOTO = "BUTTON_COMPETITION_DETAIL_GOTO";
    public static final String BUTTON_COMPETITION_DETAIL_INVITE = "BUTTON_COMPETITION_DETAIL_INVITE";
    public static final String BUTTON_COMPETITION_DETAIL_RANK_POSITION = "BUTTON_COMPETITION_DETAIL_RANK_POSITION";
    //个股页面右上角点击+自选股，取消自选 ,1天，5天，3个月，1年，讨论tab，资讯tab的点击量，最下方购买，出售，讨论的点击量。讨论tab和资讯tab下方的点击加载更多,抢沙发 的点击量
    public static final String BUTTON_STOCK_DETAIL_ADDWATCH = "BUTTON_STOCK_DETAIL_ADDWATCH";
    public static final String BUTTON_STOCK_DETAIL_CANCELWATCH = "BUTTON_STOCK_DETAIL_CANCELWATCH";
    public static final String BUTTON_STOCK_DETAIL_CHART_ONEDAY = "BUTTON_STOCK_DETAIL_CHART_ONEDAY";
    public static final String BUTTON_STOCK_DETAIL_CHART_FIVEDAY = "BUTTON_STOCK_DETAIL_CHART_FIVEDAY";
    public static final String BUTTON_STOCK_DETAIL_CHART_90DAY = "BUTTON_STOCK_DETAIL_CHART_90DAY";
    public static final String BUTTON_STOCK_DETAIL_CHART_YEAR = "BUTTON_STOCK_DETAIL_CHART_YEAR";
    public static final String BUTTON_STOCK_DETAIL_TAB_DISCUSS = "BUTTON_STOCK_DETAIL_TAB_DISCUSS";
    public static final String BUTTON_STOCK_DETAIL_TAB_NEWS = "BUTTON_STOCK_DETAIL_TAB_NEWS";
    public static final String BUTTON_STOCK_DETAIL_OPER_BUY = "BUTTON_STOCK_DETAIL_OPER_BUY";
    public static final String BUTTON_STOCK_DETAIL_OPER_SALE = "BUTTON_STOCK_DETAIL_OPER_SALE";
    public static final String BUTTON_STOCK_DETAIL_OPER_DISCUSS = "BUTTON_STOCK_DETAIL_OPER_DISCUSS";
    public static final String BUTTON_STOCK_DETAIL_GETMORE = "BUTTON_STOCK_DETAIL_GETMORE";
    public static final String BUTTON_STOCK_DETAIL_SAFA = "BUTTON_STOCK_DETAIL_SAFA";

    //他的持仓页面中关注点击量，及关注后持仓和平仓个股点击量，他的主页的点击量
    public static final String BUTTON_PORTFOLIO_FOLLOW_USER = "BUTTON_PORTFOLIO_FOLLOW_USER";
    public static final String BUTTON_PORTFOLIO_POSITION_CLICKED = "BUTTON_PORTFOLIO_POSITION_CLICKED";
    public static final String BUTTON_PORTFOLIO_MAIN_PAGE = "BUTTON_PORTFOLIO_MAIN_PAGE";
    public static final String BUTTON_PORTFOLIO_GOTO_COMPETITION = "BUTTON_PORTFOLIO_GOTO_COMPETITION";
    //Windy
}
