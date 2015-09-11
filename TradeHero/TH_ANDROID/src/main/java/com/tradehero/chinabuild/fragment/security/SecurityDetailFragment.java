package com.tradehero.chinabuild.fragment.security;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.squareup.widgets.AspectRatioImageView;
import com.tradehero.chinabuild.cache.PositionCompactNewCache;
import com.tradehero.chinabuild.cache.PositionDTOKey;
import com.tradehero.chinabuild.data.KLineItem;
import com.tradehero.chinabuild.data.QuoteDetail;
import com.tradehero.chinabuild.data.QuoteTick;
import com.tradehero.chinabuild.data.SecurityCommentList;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.fragment.competition.CompetitionSecuritySearchFragment;
import com.tradehero.chinabuild.fragment.message.SecurityDiscussSendFragment;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.SecurityOptActivity;
import com.tradehero.th.activities.TradeHeroMainActivity;
import com.tradehero.th.adapters.PositionTradeListAdapter;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.PositionDTOKeyFactory;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.fragments.security.ChartImageView;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.chart.ChartDTO;
import com.tradehero.th.models.chart.ChartDTOFactory;
import com.tradehero.th.models.chart.ChartTimeSpan;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.GuideView;
import com.tradehero.th.widget.KChart.KChartsView;
import com.tradehero.th.widget.KChart.TimesView;
import com.tradehero.th.widget.ScrollViewExtend;
import com.tradehero.th.widget.ScrollViewListener;
import com.tradehero.th.widget.TradeHeroProgressBar;
import com.tradehero.th.wxapi.WXEntryActivity;
import com.tradehero.th.wxapi.WXMessage;
import com.viewpagerindicator.SquarePageIndicator;
import dagger.Lazy;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * 股票交易界面
 */
public class SecurityDetailFragment extends BasePurchaseManagerFragment
        implements View.OnClickListener
{
    public final static String BUNDLE_KEY_SECURITY_NAME = SecurityDetailFragment.class.getName() + ".securityName";
    public final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = SecurityDetailFragment.class.getName() + ".securityId";
    public final static String BUNDLE_KEY_COMPETITION_ID_BUNDLE = SecurityDetailFragment.class.getName() + ".competitionID";
    public final static String BUNDLE_KEY_GOTO_TRADE_DETAIL = SecurityDetailFragment.class.getName() + ".gotoTradeDetail";
    public final static String BUNDLE_KEY_SECURITY_DTO_ID_BUNDLE = SecurityDetailFragment.class.getName() + ".SecurityCompactDTO.id";

    @Inject Analytics analytics;
    @Inject CurrentUserId currentUserId;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    @Inject ChartDTOFactory chartDTOFactory;
    private ChartDTO chartDTO;

    private FragmentManager fragmentManager;

    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    private DTOCacheNew.Listener<SecurityId, SecurityCompactDTO> compactCacheListener;

    @Inject Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    protected DTOCacheNew.Listener<SecurityId, SecurityPositionDetailDTO> securityPositionDetailListener;

    @Inject Lazy<PositionCompactNewCache> positionCompactNewCache;
    private DTOCacheNew.Listener<PositionDTOKey, PositionDTO> positionNewCacheListener;

    protected ProviderId providerId;
    protected UserProfileDTO userProfileDTO;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected SecurityPositionDetailDTO securityPositionDetailDTO;
    protected WatchlistPositionDTOList watchedList;
    @Inject UserWatchlistPositionCache userWatchlistPositionCache;

    @Nullable protected QuoteDTO quoteDTO;
    protected boolean isTransactionTypeBuy = true;
    protected Integer mBuyQuantity;
    protected Integer mSellQuantity;
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    public PositionDTOCompactList positionDTOCompactList;
    protected PortfolioCompactDTO portfolioCompactDTO;
    @Inject PortfolioCompactCache portfolioCompactCache;
    @Inject DiscussionServiceWrapper discussionServiceWrapper;

    private boolean isInWatchList = false;//是否是自选股
    private String securityName = "";
    @Nullable private MiddleCallback<WatchlistPositionDTO> middleCallbackUpdate;
    @Nullable private MiddleCallback<WatchlistPositionDTO> middleCallbackDelete;
    @Inject WatchlistServiceWrapper watchlistServiceWrapper;
    @Inject Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject QuoteServiceWrapper quoteServiceWrapper;
    protected DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> userWatchlistPositionCacheFetchListener;
    private ProgressDialog progressBar;
    @Inject ProgressDialogUtil progressDialogUtil;
    public int competitionID;
    private int indexChart = -1;
    private int indexSubFragment = 0;

    //Security Detail Tab Start
    private BetterViewAnimator betterViewAnimator;
    private LinearLayout llBuySaleButtons;//购买卖出栏
    private RelativeLayout llSecurityBuy;//购买
    private RelativeLayout llSecuritySale;//出售
    private RelativeLayout llSecurityDiscuss;//讨论
    private RelativeLayout llWatchList;//自选
    private TextView addWatchListTV;
    private ViewPager pager;
    private SquarePageIndicator indicator;
    private List<View> views = new ArrayList<>();

    private Button[] btnChart;
    private Button btnChart0;
    private Button btnChart1;
    private Button btnChart2;
    private Button btnChart3;
    private View chart0Line;
    private View chart1Line;
    private View chart2Line;
    private View chart3Line;

    private Button btnDiscuss;
    private Button btnNews;
    private Button btnUserOperation;
    private Button btnUserPosition;
    private TextView discussNumTV;
    private View discussFocusView;
    private View newsFocusView;
    private View operationFocusView;
    private View positionFocusView;

    protected BetterViewAnimator chartImageWrapper;
    protected ChartImageView chartImage;
    protected TimesView timeListView;
    protected KChartsView kChartsListView;
    protected TextView tvLoadingChart;

    private ImageLoadingListener chartLoadingListener;

    private TextView tvSecurityPrice;//当前价格
    private TextView tvSecurityDetailRate;//涨跌幅
    private TextView tvSecurityDetailNum;//涨跌值
    private TextView tvTodayPriceBegin; //今开
    private TextView tvYesterdayPriceEnd; //昨收
    private TextView tvInfo0Value;//最高
    private TextView tvInfo1Value;//最低
    private TextView tvInfo2Value;//成交量
    private TextView tvTotalAmount; //成交额

    private Callback<QuoteDetail> quoteDetailCallback;
    private Callback<QuoteDTO> quoteCallback;
    private Callback<List<KLineItem>> kLinesListCallback;
    private int quoteErrors;

    //Portfolio Detail Tab Start
    private TextView tvPositionTotalCcy;//累计盈亏
    private TextView tvPositionSumAmont;//总投资
    private TextView tvPositionStartTime;//建仓时间
    private TextView tvPositionLastTime;//最后交易
    private TextView tvPositionHoldTime;//持有时间
    private TextView tvEmpty;//没有交易明细
    private SecurityListView listView;
    private TradeHeroProgressBar progressBarPortfolio;
    private BetterViewAnimator betterViewAnimatorPortfolio;
    //Portfolio Detail Tab End

    private ScrollViewExtend securityDetailSV;
    private int stockDetailHeight;
    private int colorUp;
    private int colorNormal;
    private int colorDown;
    private int colorText;
    private int colorBlue;

    //Discuss, news, opts, postions
    private SecurityDetailSubViewPager subViewPager;
    private ArrayList<Fragment> subFragments = new ArrayList();
    private SubFragmentPagerAdapter subFragmentPagerAdapter;

    //Watchlist Pop Window
    private PopupWindow popWin;
    private LinearLayout watchlistShareLL;
    private LinearLayout watchlistRemoveLL;

    //Notice Layout
    private LinearLayout noticeLL;
    private TextView costPriceTV;
    private TextView benefitTV;
    private TextView benefitPercentTV;

    //Share to WeChat
    private int percent;
    private String securityHead;

    boolean isFromCompetition = false;

    private Double preClose;

    public static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE =
            SecurityDetailFragment.class.getName() + ".purchaseApplicablePortfolioId";
    public static final String BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE = SecurityDetailFragment.class.getName() + ".positionDTOKey";

    public static Double mPreClose;

    public static void putApplicablePortfolioId(@NotNull Bundle args, @NotNull OwnedPortfolioId ownedPortfolioId)
    {
        args.putBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, ownedPortfolioId.getArgs());
    }

    public static void putPositionDTOKey(@NotNull Bundle args, @NotNull com.tradehero.th.api.position.PositionDTOKey positionDTOKey)
    {
        args.putBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE, positionDTOKey.getArgs());
    }

    private static com.tradehero.th.api.position.PositionDTOKey getPositionDTOKey(Bundle args, PositionDTOKeyFactory positionDTOKeyFactory)
    {
        if (args != null && args.getBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE) != null)
        {
            return positionDTOKeyFactory.createFrom(args.getBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE));
        }
        return null;
    }

    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<TradeListCache> tradeListCache;
    @Inject PositionDTOKeyFactory positionDTOKeyFactory;

    protected com.tradehero.th.api.position.PositionDTOKey positionDTOKey;
    protected DTOCacheNew.Listener<com.tradehero.th.api.position.PositionDTOKey, PositionDTO> fetchPositionListener;
    protected PositionDTO positionDTO;
    protected TradeDTOList tradeDTOList;
    private DTOCacheNew.Listener<OwnedPositionId, TradeDTOList> fetchTradesListener;
    private PositionTradeListAdapter adapter;

    private boolean isGotoTradeDetail;
    private boolean isFirstLunch;

    private View view;

    private QuoteDetail quoteDetail;
    private int scrollY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        compactCacheListener = new StockInfoFragmentSecurityCompactCacheListener();
        chartDTO = chartDTOFactory.createChartDTO();
        securityPositionDetailListener = new AbstractBuySellSecurityPositionCacheListener();
        positionNewCacheListener = new PositionNewCacheListener();
        userProfileCacheListener = new AbstractBuySellUserProfileCacheListener();
        userWatchlistPositionCacheFetchListener = new BuySellUserWatchlistCacheListener();

        fetchPositionListener = new TradeListFragmentPositionCacheListener();
        fetchTradesListener = new GetTradesListener();
        adapter = new PositionTradeListAdapter(getActivity());

        isGotoTradeDetail = getArguments().getBoolean(BUNDLE_KEY_GOTO_TRADE_DETAIL, false);
        isFirstLunch = true;

        fragmentManager = getChildFragmentManager();

        Bundle args = getArguments();
        if (args != null) {
            Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
            securityName = args.getString(BUNDLE_KEY_SECURITY_NAME);
            if (securityIdBundle != null) {
                securityId = new SecurityId(securityIdBundle);
            }

        }
        initResources();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        setHeadViewRight0(R.drawable.search);
        setHeadViewRight1(R.drawable.share);

        if(securityId!=null) {
            securityHead = securityName + "(" + securityId.getSecuritySymbol() + ")";
            setHeadViewMiddleMain(securityHead);
        }
        if (watchedList != null && securityId != null) {
            isInWatchList = watchedList.contains(securityId);
            displayInWatchButton();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view != null){
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            return view;
        }
        view = inflater.inflate(R.layout.security_detail_layout, container, false);

        SecurityDetailSubCache.getInstance().clearAll();
        if(securityId!=null) {
            SecurityDetailSubCache.getInstance().setSecurityId(securityId);
        }

        initBaseView(view);
        initView();
        updateHeadView(true);
        betterViewAnimator.setDisplayedChildByLayoutId(R.id.progress);

        return view;
    }

    private void initResources(){
        stockDetailHeight = (int)getResources().getDimension(R.dimen.security_detail_stock_detail_height);
        colorUp = getResources().getColor(R.color.bar_up);
        colorNormal = getResources().getColor(R.color.bar_normal);
        colorDown = getResources().getColor(R.color.bar_down);
        colorText = getResources().getColor(R.color.color_text_security_detail);
        colorBlue = getResources().getColor(R.color.color_blue);
    }

    private void initNoticeLL(View parent){
        noticeLL = (LinearLayout)parent.findViewById(R.id.linearlayout_security_detail_notice);
        costPriceTV = (TextView)parent.findViewById(R.id.textview_security_detail_price);
        benefitTV = (TextView)parent.findViewById(R.id.textview_security_detail_benefit);
        benefitPercentTV = (TextView)parent.findViewById(R.id.textview_security_detail_benefit_percentage);
    }

    private void displayNotice(String cost, double benefit, String benefitPercent, String currencyDisplay){
        noticeLL.setVisibility(View.VISIBLE);
        costPriceTV.setText(currencyDisplay + cost);
        benefitTV.setText(currencyDisplay + String.valueOf(benefit));
        benefitPercentTV.setText(benefitPercent);
        if(benefit > 0){
            benefitTV.setTextColor(colorUp);
            benefitPercentTV.setTextColor(colorUp);
            costPriceTV.setTextColor(colorUp);
        }
        if(benefit == 0){
            benefitTV.setTextColor(colorNormal);
            benefitPercentTV.setTextColor(colorNormal);
            costPriceTV.setTextColor(colorNormal);
        }
        if(benefit < 0){
            benefitTV.setTextColor(colorDown);
            benefitPercentTV.setTextColor(colorDown);
            costPriceTV.setTextColor(colorDown);
        }
    }

    private void setBuySaleButtonVisible() {
        try {
            llBuySaleButtons.setVisibility(View.VISIBLE);
            if (isBuyOrSaleValid()) {
                betterViewAnimator.setDisplayedChildByLayoutId(R.id.ic_info_buy_sale_btns);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBaseView(View view){
        betterViewAnimator = (BetterViewAnimator)view.findViewById(R.id.bvaViewAll);
        llBuySaleButtons = (LinearLayout)view.findViewById(R.id.ic_info_buy_sale_btns);
        llSecurityBuy = (RelativeLayout)view.findViewById(R.id.llSecurityBuy);
        llSecurityBuy.setOnClickListener(this);
        llSecuritySale = (RelativeLayout)view.findViewById(R.id.llSecuritySale);
        llSecuritySale.setOnClickListener(this);
        llSecurityDiscuss = (RelativeLayout)view.findViewById(R.id.llSecurityDiscuss);
        llSecurityDiscuss.setOnClickListener(this);
        addWatchListTV = (TextView)view.findViewById(R.id.textview_add_watchlist);
        llWatchList = (RelativeLayout)view.findViewById(R.id.llSecurityWatchList);
        llWatchList.setOnClickListener(this);
        pager = (ViewPager)view.findViewById(R.id.pager);
        indicator = (SquarePageIndicator)view.findViewById(R.id.indicator);
    }

    private void initView() {
        initTabPageView();
        llBuySaleButtons.setVisibility(View.GONE);

        btnChart = new Button[4];
        btnChart[0] = btnChart0;
        btnChart[1] = btnChart1;
        btnChart[2] = btnChart2;
        btnChart[3] = btnChart3;
        setDefaultBtnTabView();

        chartDTO.setIncludeVolume(chartImage.includeVolume);
        if (chartImage != null) {
            chartImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        chartLoadingListener = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                AspectRatioImageView aspectRatioImageView = (AspectRatioImageView) view;
                int imgHeight = aspectRatioImageView.getDrawable().getIntrinsicHeight();
                int imgWidth = aspectRatioImageView.getDrawable().getIntrinsicWidth();
                aspectRatioImageView.setAspectRatioEnabled(true);
                int dominantMeasurement = aspectRatioImageView.getDominantMeasurement();
                if (dominantMeasurement == AspectRatioImageView.MEASUREMENT_WIDTH) {
                    aspectRatioImageView.setAspectRatio((float) imgHeight / (float) imgWidth);
                } else if (dominantMeasurement == AspectRatioImageView.MEASUREMENT_HEIGHT) {
                    aspectRatioImageView.setAspectRatio((float) imgWidth / (float) imgHeight);
                } else {
                    throw new IllegalArgumentException("Unhandled dominant measurement " + dominantMeasurement);
                }
                chartImageWrapper.setDisplayedChildByLayoutId(R.id.chart_view);
            }

            @Override
            public void onLoadingCancelled(String s, View view) { }
        };
        showTimeList();

        initListView();
        if (adapter != null && adapter.getCount() == 0)
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.tradeheroprogressbar_my_securities_history);
            progressBarPortfolio.startLoading();
            startTimerForView();
        }
        else
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTrade);
        }
    }

    public void initListView() {
        listView.setAdapter(adapter);
        listView.setMode(PullToRefreshBase.Mode.DISABLED);
        listView.setEmptyView(tvEmpty);
    }

    public void initTabPageView()
    {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        views = new ArrayList<>();
        View viewTab0 = layoutInflater.inflate(R.layout.security_detail_tab0_layout, null);
        initRootViewTab0(viewTab0);
        views.add(viewTab0);

        View viewTab1 = layoutInflater.inflate(R.layout.security_detail_tab1_layout, null);
        initRootViewTab1(viewTab1);
        views.add(viewTab1);

        setOnclickListeners();
        pager.setAdapter(pageAdapter);
        indicator.setViewPager(pager);

        if (isGotoTradeDetail && isFirstLunch)
        {
            isFirstLunch = false;
            indicator.setCurrentItem(1);
        }
    }

    public void initRootViewTab0(View tabView0) {

        securityDetailSV = (ScrollViewExtend)tabView0.findViewById(R.id.scrollview_security_detail);
        securityDetailSV.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(ScrollViewExtend scrollView, int x, int y, int oldx, int oldy) {
                scrollY = y;
                updateSubHead();
            }
        });

        initNoticeLL(tabView0);

        btnChart0 = (Button) tabView0.findViewById(R.id.btnTabChart0);
        btnChart1 = (Button) tabView0.findViewById(R.id.btnTabChart1);
        btnChart2 = (Button) tabView0.findViewById(R.id.btnTabChart2);
        btnChart3 = (Button) tabView0.findViewById(R.id.btnTabChart3);
        chart0Line = tabView0.findViewById(R.id.view_tab0_focus);
        chart1Line = tabView0.findViewById(R.id.view_tab1_focus);
        chart2Line = tabView0.findViewById(R.id.view_tab2_focus);
        chart3Line = tabView0.findViewById(R.id.view_tab3_focus);

        btnDiscuss = (Button) tabView0.findViewById(R.id.btnTabDiscuss);
        btnNews = (Button) tabView0.findViewById(R.id.btnTabNews);
        btnUserOperation = (Button)tabView0.findViewById(R.id.btnTabUserOperation);
        btnUserPosition = (Button)tabView0.findViewById(R.id.btnTabUserPosition);
        discussNumTV = (TextView)tabView0.findViewById(R.id.textview_discuss_num);
        discussFocusView = tabView0.findViewById(R.id.view_discuss_focus);
        newsFocusView = tabView0.findViewById(R.id.view_news_focus);
        operationFocusView = tabView0.findViewById(R.id.view_operation_focus);
        positionFocusView = tabView0.findViewById(R.id.view_position_focus);

        chartImageWrapper = (BetterViewAnimator) tabView0.findViewById(R.id.chart_image_wrapper);
        chartImage = (ChartImageView) tabView0.findViewById(R.id.chart_imageView);
        timeListView = (TimesView) tabView0.findViewById(R.id.time_list_view);
        kChartsListView = (KChartsView) tabView0.findViewById(R.id.my_charts_view);
        tvLoadingChart = (TextView) tabView0.findViewById(R.id.chart_loading);

        tvSecurityPrice = (TextView) tabView0.findViewById(R.id.tvSecurityDetailPrice);
        tvSecurityDetailRate = (TextView) tabView0.findViewById(R.id.tvSecurityDetailRate);
        tvSecurityDetailNum = (TextView) tabView0.findViewById(R.id.tvSecurityDetailNum);

        tvInfo0Value = (TextView) tabView0.findViewById(R.id.tvInfo0Value);
        tvInfo1Value = (TextView) tabView0.findViewById(R.id.tvInfo1Value);
        tvInfo2Value = (TextView) tabView0.findViewById(R.id.tvInfo2Value);

        tvTodayPriceBegin = (TextView) tabView0.findViewById(R.id.tvTodayPriceBegin);
        tvYesterdayPriceEnd = (TextView) tabView0.findViewById(R.id.tvYesterdayPriceEnd);
        tvTotalAmount = (TextView) tabView0.findViewById(R.id.tvTotalAmount);

        subViewPager = (SecurityDetailSubViewPager) tabView0.findViewById(R.id.securitydetailsubviewpager);
    }


    public void setOnclickListeners() {
        btnChart0.setOnClickListener(this);
        btnChart1.setOnClickListener(this);
        btnChart2.setOnClickListener(this);
        btnChart3.setOnClickListener(this);

        btnDiscuss.setOnClickListener(this);
        btnNews.setOnClickListener(this);
        btnUserPosition.setOnClickListener(this);
        btnUserOperation.setOnClickListener(this);
    }

    public void initRootViewTab1(View tabView1)
    {
        tvPositionTotalCcy = (TextView) tabView1.findViewById(R.id.tvPositionTotalCcy);
        tvPositionSumAmont = (TextView) tabView1.findViewById(R.id.tvPositionSumAmont);
        tvPositionStartTime = (TextView) tabView1.findViewById(R.id.tvPositionStartTime);
        tvPositionLastTime = (TextView) tabView1.findViewById(R.id.tvPositionLastTime);
        tvPositionHoldTime = (TextView) tabView1.findViewById(R.id.tvPositionHoldTime);
        tvEmpty = (TextView) tabView1.findViewById(R.id.tvEmpty);
        listView = (SecurityListView) tabView1.findViewById(R.id.listTrade);
        progressBarPortfolio = (TradeHeroProgressBar) tabView1.findViewById(R.id.tradeheroprogressbar_my_securities_history);
        betterViewAnimatorPortfolio = (BetterViewAnimator) tabView1.findViewById(R.id.bvaViewAllPortfolio);
    }

    public PagerAdapter pageAdapter = new PagerAdapter()
    {
        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }

        @Override
        public int getCount()
        {
            return (views == null) ? 0 : views.size();
        }
    };

    @Override public void onPause() {
        detachSecurityCompactCache();
        quoteServiceWrapper.stopQuoteDetailTask();
        quoteServiceWrapper.stopSecurityCompactTask();
        quoteServiceWrapper.stopQuoteTicksTask();
        quoteServiceWrapper.stopQuoteTask();

        super.onPause();
    }

    @Override public void onDestroyView()
    {
        quoteServiceWrapper.stopQuoteDetailTask();
        quoteServiceWrapper.stopSecurityCompactTask();
        detachUserProfileCache();
        detachSecurityCompactCache();
        detachSecurityPositionDetailCache();
        detachCompetitionPositionCache();
        detachWatchlistFetchTask();

        detachFetchPosition();
        detachFetchTrades();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onResume() {
        initArgment();
        requestUserProfile();
        fetchWatchlist();
        super.onResume();

        chartImageWrapper.setDisplayedChildByLayoutId(R.id.chart_loading);
        indexSubFragment = 0;
        setCategoryViews();

        if (THSharePreferenceManager.isGuideAvailable(getActivity(), THSharePreferenceManager.GUIDE_STOCK_BUY)) {
            showGuideView();
        }

        getTradeTabDetail();
    }

    public void getTradeTabDetail()
    {
        com.tradehero.th.api.position.PositionDTOKey positionDTOKey = getPositionDTOKey(getArguments(), positionDTOKeyFactory);

        if(competitionID==0)//普通进来
        {
            if (positionDTOKey == null && securityId != null)
            {
                positionDTOKey = TradeHeroMainActivity.getSecurityPositionDTOKey(securityId);
            }
            if (positionDTOKey == null && securityCompactDTO != null)
            {
                positionDTOKey = TradeHeroMainActivity.getSecurityPositionDTOKey(securityCompactDTO.id);
            }
            if (positionDTOKey != null)
            {
                linkWith(positionDTOKey);
            }
            else //显示没有交易记录
            {
                if (betterViewAnimatorPortfolio != null)
                {
                    betterViewAnimatorPortfolio.setDisplayedChildByLayoutId(R.id.listTrade);
                }
            }
        }
        else//比赛进来
        {
            if (positionDTOKey != null)
            {
                linkWith(positionDTOKey);
            }
            else //显示没有交易记录
            {
                if (betterViewAnimatorPortfolio != null)
                {
                    betterViewAnimatorPortfolio.setDisplayedChildByLayoutId(R.id.listTrade);
                }
            }
        }

    }

    public void initArgment()
    {
        Bundle args = getArguments();
        if (args != null)
        {
            Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
            securityName = args.getString(BUNDLE_KEY_SECURITY_NAME);
            competitionID = args.getInt(BUNDLE_KEY_COMPETITION_ID_BUNDLE, 0);
            if (securityIdBundle != null)
            {
                linkWith(new SecurityId(securityIdBundle));
            }
        }
    }

    private void setDefaultBtnTabView() {
        if (indexChart == -1) {
            btnChart[0].performClick();
        } else {
            setChartView(indexChart);
            linkWith(new ChartTimeSpan(getChartTimeSpanDuration(indexChart)));
        }
    }

    private void linkWith(final SecurityId securityId)
    {
        this.securityId = securityId;
        if (securityId != null)
        {
            queryCompactCache(securityId);
            refreshQuoteInfo(securityId);


            if (competitionID == 0)//不是比赛
            {
                isFromCompetition = false;
                SecurityPositionDetailDTO detailDTO = securityPositionDetailCache.get().get(this.securityId);
                if (detailDTO != null)
                {
                    linkWith(detailDTO);
                }
                else
                {
                    requestPositionDetail();
                }
            }
            else//是比赛
            {
                isFromCompetition = true;
                requestCompetitionPosition();
            }
        }
    }

    public Integer getMaxPurchasableShares()
    {
        return portfolioCompactDTOUtil.getMaxPurchasableShares(
                this.portfolioCompactDTO,
                this.quoteDTO);
    }

    public Integer getMaxSellableShares()
    {
        OwnedPortfolioId ownedPortfolioId = getApplicablePortfolioId();
        if (ownedPortfolioId != null && positionDTOCompactList != null && this.quoteDTO != null)
        {
            return positionDTOCompactList.getMaxSellableShares(
                    this.quoteDTO,
                    this.portfolioCompactDTO);
        }
        return 0;
    }

    public void setInitialBuySaleQuantityIfCan()
    {
        setInitialBuyQuantityIfCan();
        setInitialSellQuantityIfCan();
        setBuySaleButtonVisible();
    }

    protected void setInitialBuyQuantityIfCan()
    {
        Integer maxPurchasableShares = getMaxPurchasableShares();
        if (maxPurchasableShares != null) {
            linkWithBuyQuantity((int) Math.ceil(((double) maxPurchasableShares) / 2));
        }
    }

    protected void setInitialSellQuantityIfCan()
    {
        Integer maxSellableShares = getMaxSellableShares();
        if (maxSellableShares != null)
        {
            linkWithSellQuantity(maxSellableShares);
            if (maxSellableShares == 0)
            {
                setTransactionTypeBuy(true);
            }
        }
    }

    protected Integer getClampedBuyQuantity(Integer candidate)
    {
        Integer maxPurchasable = getMaxPurchasableShares();
        if (candidate == null || maxPurchasable == null)
        {
            return candidate;
        }
        return Math.min(candidate, maxPurchasable);
    }

    protected void linkWithBuyQuantity(Integer buyQuantity)
    {
        this.mBuyQuantity = getClampedBuyQuantity(buyQuantity);
    }

    protected Integer getClampedSellQuantity(Integer candidate)
    {
        Integer maxSellable = getMaxSellableShares();
        if (candidate == null || maxSellable == null || maxSellable == 0)
        {
            return candidate;
        }
        return Math.min(candidate, maxSellable);
    }

    protected void linkWithSellQuantity(Integer sellQuantity)
    {
        this.mSellQuantity = getClampedSellQuantity(sellQuantity);
    }

    public void setTransactionTypeBuy(boolean transactionTypeBuy)
    {
        this.isTransactionTypeBuy = transactionTypeBuy;
    }

    protected void requestPositionDetail()
    {
        detachSecurityPositionDetailCache();
        securityPositionDetailCache.get().register(this.securityId, securityPositionDetailListener);
        securityPositionDetailCache.get().getOrFetchAsync(this.securityId);
    }

    protected void requestCompetitionPosition()
    {
        detachCompetitionPositionCache();
        if (competitionID != 0 && this.securityId != null)
        {
            PositionDTOKey key = new PositionDTOKey(this.competitionID, this.securityId);
            positionCompactNewCache.get().register(key, positionNewCacheListener);
            positionCompactNewCache.get().getOrFetchAsync(key, true);
        }
    }

    protected void detachSecurityPositionDetailCache()
    {
        securityPositionDetailCache.get().unregister(securityPositionDetailListener);
    }

    protected void detachCompetitionPositionCache()
    {
        positionCompactNewCache.get().unregister(positionNewCacheListener);
    }

    private void queryCompactCache(final SecurityId securityId)
    {
        //先显示缓存，然后再请求数据显示股票信息。
        SecurityCompactDTO securityCompactDTO = securityCompactCache.get().get(securityId);
        if (securityCompactDTO != null)
        {
            linkWith(securityCompactDTO);
        }
        detachSecurityCompactCache();
        securityCompactCache.get().register(securityId, compactCacheListener);
        securityCompactCache.get().getOrFetchAsync(securityId, true);
    }

    protected void detachSecurityCompactCache()
    {
        securityCompactCache.get().unregister(compactCacheListener);
    }

    @Override protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId)
    {
        super.linkWithApplicable(purchaseApplicablePortfolioId);
        if (purchaseApplicablePortfolioId != null)
        {
            linkWith(portfolioCompactCache.get(purchaseApplicablePortfolioId.getPortfolioIdKey()));
            purchaseApplicableOwnedPortfolioId = purchaseApplicablePortfolioId;
        } else {
            linkWith((PortfolioCompactDTO) null);
        }
    }

    protected void linkWith(PortfolioCompactDTO portfolioCompactDTO)
    {
        if (getActivity() != null)
        {
            this.portfolioCompactDTO = portfolioCompactDTO;
            linkWithSellQuantity(mSellQuantity);
            linkWithBuyQuantity(mBuyQuantity);

            setInitialBuySaleQuantityIfCan();
        }
    }

    protected class StockInfoFragmentSecurityCompactCacheListener implements DTOCacheNew.Listener<SecurityId, SecurityCompactDTO>
    {
        @Override public void onDTOReceived(@NotNull SecurityId key, @NotNull SecurityCompactDTO value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull SecurityId key, @NotNull Throwable error)
        {
        }
    }

    private void linkWith(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;

        if (securityCompactDTO != null)
        {
            chartDTO.setSecurityCompactDTO(securityCompactDTO);
            setHeadViewMiddleMain(securityCompactDTO.name + "(" + securityCompactDTO.getSecurityId().getSecuritySymbol() + ")");
            updateSubHead();
            retrieveDiscussionCount();
        }
        displayChartImage();

        updateSecurityInfoByCompactDTO();

        getTradeTabDetail();
        initSubViewPager();
    }

    private void retrieveDiscussionCount() {
        if (securityCompactDTO == null) {
            return;
        }

        Callback<SecurityCommentList> callback = new Callback<SecurityCommentList>() {
            @Override
            public void success(SecurityCommentList securityCommentList, Response response) {
                if (securityCommentList == null) {
                    return;
                }
                updateDiscussionCount(securityCommentList.commentCount);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        };
        discussionServiceWrapper.getSecurityComment(securityId, 1, 0, callback);


    }

    public void setChartView(int select) {
        indexChart = select;
        for (int i = 0; i < btnChart.length; i++) {
            btnChart[i].setTextColor((i == indexChart ? colorBlue : colorText));
        }
        if(select == 0){
            chart0Line.setVisibility(View.VISIBLE);
            chart1Line.setVisibility(View.GONE);
            chart2Line.setVisibility(View.GONE);
            chart3Line.setVisibility(View.GONE);
        }
        if(select == 1){
            chart0Line.setVisibility(View.GONE);
            chart1Line.setVisibility(View.VISIBLE);
            chart2Line.setVisibility(View.GONE);
            chart3Line.setVisibility(View.GONE);
        }
        if(select == 2){
            chart0Line.setVisibility(View.GONE);
            chart1Line.setVisibility(View.GONE);
            chart2Line.setVisibility(View.VISIBLE);
            chart3Line.setVisibility(View.GONE);
        }
        if(select == 3){
            chart0Line.setVisibility(View.GONE);
            chart1Line.setVisibility(View.GONE);
            chart2Line.setVisibility(View.GONE);
            chart3Line.setVisibility(View.VISIBLE);
        }
    }

    public void setDiscussOrNewsView(int select) {
        if(select > 0){
            if(select == indexSubFragment){
                return;
            }
        }
        indexSubFragment = select;
        setCategoryViews();
        if (indexSubFragment == 0){
            subViewPager.setCurrentItem(0);
            return;
        }
        if (indexSubFragment == 1) {
            subViewPager.setCurrentItem(1);
            return;
        }
        if (indexSubFragment == 2){
            subViewPager.setCurrentItem(2);
            return;
        }
        if (indexSubFragment == 3){
            subViewPager.setCurrentItem(3);
            return;

        }
    }

    private void setCategoryViews(){
        switch(indexSubFragment){
            case 0:
                discussFocusView.setVisibility(View.VISIBLE);
                newsFocusView.setVisibility(View.GONE);
                operationFocusView.setVisibility(View.GONE);
                positionFocusView.setVisibility(View.GONE);
                btnDiscuss.setTextColor(colorBlue);
                btnNews.setTextColor(colorText);
                btnUserOperation.setTextColor(colorText);
                btnUserPosition.setTextColor(colorText);
                break;
            case 1:
                discussFocusView.setVisibility(View.GONE);
                newsFocusView.setVisibility(View.VISIBLE);
                operationFocusView.setVisibility(View.GONE);
                positionFocusView.setVisibility(View.GONE);
                btnDiscuss.setTextColor(colorText);
                btnNews.setTextColor(colorBlue);
                btnUserOperation.setTextColor(colorText);
                btnUserPosition.setTextColor(colorText);
                break;
            case 2:
                discussFocusView.setVisibility(View.GONE);
                newsFocusView.setVisibility(View.GONE);
                operationFocusView.setVisibility(View.VISIBLE);
                positionFocusView.setVisibility(View.GONE);
                btnDiscuss.setTextColor(colorText);
                btnNews.setTextColor(colorText);
                btnUserOperation.setTextColor(colorBlue);
                btnUserPosition.setTextColor(colorText);
                break;
            case 3:
                discussFocusView.setVisibility(View.GONE);
                newsFocusView.setVisibility(View.GONE);
                operationFocusView.setVisibility(View.GONE);
                positionFocusView.setVisibility(View.VISIBLE);
                btnDiscuss.setTextColor(colorText);
                btnNews.setTextColor(colorText);
                btnUserOperation.setTextColor(colorText);
                btnUserPosition.setTextColor(colorBlue);
                break;
        }
    }

    public long getChartTimeSpanDuration(int index)
    {
        if (index == 0)
        {
            return ChartTimeSpan.DAY_1;
        }
        else if (index == 1)
        {
            return ChartTimeSpan.DAY_5;
        }
        else if (index == 2)
        {
            return ChartTimeSpan.MONTH_3;
        }
        else if (index == 3)
        {
            return ChartTimeSpan.YEAR_1;
        }
        return ChartTimeSpan.DAY_1;
    }

    //分时线 和 5天线 每一分钟需要更新一次
    long TIMER_FOR_DISPLAY_CHART_IMAGE0 = System.currentTimeMillis();
    long TIMER_FOR_DISPLAY_CHART_IMAGE1 = System.currentTimeMillis();

    public boolean isValidTimerForChartImage0()
    {
        return System.currentTimeMillis() - TIMER_FOR_DISPLAY_CHART_IMAGE0 > 60000;
    }

    public boolean isValidTimerForChartImage1()
    {
        return System.currentTimeMillis() - TIMER_FOR_DISPLAY_CHART_IMAGE1 > 60000;
    }

    public void displayChartImage()
    {
        if (!isDetached() && chartImage != null)
        {
            String imageURL = chartDTO.getChartUrl();
            if ((indexChart == 0) && isValidTimerForChartImage0())
            {
                ImageLoader.getInstance().displayImage(imageURL, chartImage, chartLoadingListener);
                TIMER_FOR_DISPLAY_CHART_IMAGE0 = System.currentTimeMillis();
            }
            else if ((indexChart == 1) && isValidTimerForChartImage1())
            {
                ImageLoader.getInstance().displayImage(imageURL, chartImage, chartLoadingListener);
                TIMER_FOR_DISPLAY_CHART_IMAGE1 = System.currentTimeMillis();
            }
            else
            {
                ImageLoader.getInstance().displayImage(imageURL, chartImage, chartLoadingListener);
            }
        }
    }

    private void refreshQuoteInfo(final SecurityId securityId) {
        getQuote(securityId);
        askKLines(securityId);
        if (QuoteServiceWrapper.isChinaStock(securityId)) {
            refreshCNQuoteInfo(securityId);
        } else {
            refreshROWQuoteInfo(securityId);
        }
    }

    private void refreshROWQuoteInfo(final SecurityId securityId) {
        Callback<SecurityCompactDTO>  securityCallback = new Callback<SecurityCompactDTO>() {
            @Override
            public void success(SecurityCompactDTO securityDTO, Response response) {
                if (securityDTO == null) {
                    return;
                }
                securityCompactDTO = securityDTO;
                updateSecurityInfoByCompactDTO();
                if (quoteDTO != null) {
                    quoteDTO.ask = securityDTO.askPrice;
                    quoteDTO.bid = securityDTO.bidPrice;
                    setInitialBuySaleQuantityIfCan();
                }
                preClose = securityDTO.previousClose;
            }

            @Override
            public void failure(RetrofitError error) {
            }
        };

        quoteServiceWrapper.getRepeatingSecurityCompactDTO(securityId, securityCallback);

    }

    private void updateSecurityInfoByCompactDTO() {
        if (getActivity() == null || securityCompactDTO == null || quoteDTO == null) {
            return;
        }

        //For SHE/SHA, don't update stock detail using SecurityCompactDTO
        if (QuoteServiceWrapper.isChinaStock(securityId)) {
            return;
        }

        //涨跌幅
        int colorResId = R.color.black;

        String currency = "";
        if (securityCompactDTO != null) {
            currency = securityCompactDTO.getCurrencyDisplay();
        }

        Double rate = securityCompactDTO.risePercent;
        if (rate != null) {
            percent = (int)(rate * 100);
            THSignedNumber roi = THSignedPercentage.builder(rate * 100)
                    .withSign()
                    .signTypePlusMinusAlways()
                    .build();
            colorResId = roi.getColorResId();
            tvSecurityDetailRate.setText(roi.toString());
            tvSecurityDetailRate.setTextColor(getResources().getColor(colorResId));
        }

        if(securityCompactDTO.lastPrice == null){
            tvSecurityPrice.setText("- -");
        } else {
            tvSecurityPrice.setText(SecurityCompactDTO.getShortValue(securityCompactDTO.lastPrice));
            tvSecurityPrice.setTextColor(getResources().getColor(colorResId));
        }

        tvSecurityDetailNum.setText(securityCompactDTO.getPriceDifferent());
        tvSecurityDetailNum.setTextColor(getResources().getColor(colorResId));

        if (securityCompactDTO.high != null) {
            tvInfo0Value.setText(THSignedMoney.builder(securityCompactDTO.high)
                    .currency(currency)
                    .build().toString());
        }

        if (securityCompactDTO.low != null) {
            tvInfo1Value.setText(THSignedMoney.builder(securityCompactDTO.low)
                    .currency(currency)
                    .build().toString());
        }

        if (securityCompactDTO.volume != null) {
            tvInfo2Value.setText(NumberDisplayUtils.getString(securityCompactDTO.volume)
                    + getString(R.string.stock_unit));
        }

        if (securityCompactDTO.open != null) {
            tvTodayPriceBegin.setText(THSignedMoney.builder(securityCompactDTO.open)
                    .currency(currency)
                    .build().toString());
        }

        if (securityCompactDTO.previousClose != null) {
            tvYesterdayPriceEnd.setText(THSignedMoney.builder(securityCompactDTO.previousClose)
                    .currency(currency)
                    .build().toString());
        }

    }

    private void getQuote(final SecurityId securityId) {
        quoteErrors = 0;
        if (quoteCallback == null) {
            quoteCallback = new Callback<QuoteDTO>() {
                @Override
                public void success(QuoteDTO dto, Response response) {
                    if (dto == null) {
                        return;
                    }
                    quoteDTO = dto;
                    setInitialBuySaleQuantityIfCan();
                }

                @Override
                public void failure(RetrofitError error) {
                    if (quoteErrors < QuoteServiceWrapper.MAX_API_RETRIES) {
                        quoteServiceWrapper.getQuote(securityId, quoteCallback);
                        quoteErrors ++;
                    }
                }
            };
        }
        quoteServiceWrapper.getQuote(securityId, quoteCallback);
    }

    private void refreshCNQuoteInfo(final SecurityId securityId) {
        if (quoteDetailCallback == null) {
            quoteDetailCallback = new Callback<QuoteDetail>() {
                @Override
                public void success(QuoteDetail quoteDetail, Response response) {
                    if(quoteDetail == null){
                        return;
                    }
                    SecurityDetailFragment.this.quoteDetail = quoteDetail;
                    updateSecurityInfoByQuoteDetails(quoteDetail);
                    if (quoteDTO != null) {
                        if (quoteDetail.sp1 != null) {
                            quoteDTO.ask = quoteDetail.sp1;
                        }
                        if (quoteDetail.bp1 != null) {
                            quoteDTO.bid = quoteDetail.bp1;
                        }
                        setInitialBuySaleQuantityIfCan();
                    }
                    preClose = quoteDetail.prec;
                    updateSubHead();
                }

                @Override
                public void failure(RetrofitError error) {
                }
            };
        }
        quoteServiceWrapper.getRepeatingQuoteDetails(securityId, quoteDetailCallback);

        //time list
        quoteServiceWrapper.getQuoteTicks(securityId, new Callback<List<QuoteTick>>() {
            @Override public void success(List<QuoteTick> quoteTicks, Response response) {
                timeListView.setTimesList(quoteTicks);
            }

            @Override public void failure(RetrofitError error){
            }
        });
    }

    private void askKLines(SecurityId securityId)
    {
        //k-lines
        if (kLinesListCallback == null) {
            kLinesListCallback = new Callback<List<KLineItem>>()
            {
                @Override public void success(List<KLineItem> kLineItemList, Response response)
                {
                    kChartsListView.setOHLCData(kLineItemList);
                    kChartsListView.postInvalidate();
                    chartImageWrapper.setDisplayedChildByLayoutId(R.id.chart_view);
                }

                @Override public void failure(RetrofitError error)
                {

                }
            };
        }
        //quoteServiceWrapper.getKline(securityId, QuoteServiceWrapper.K_LINE_DAY, kLinesListCallback);
    }

    public void updateSecurityInfoByQuoteDetails(QuoteDetail quoteDetail) {
        if ((getActivity() == null)
                || (quoteDetail == null)
                || (securityCompactDTO == null)) {
            return;
        }

        String currency = "";
        if (securityCompactDTO != null) {
            currency = securityCompactDTO.getCurrencyDisplay();
        }


        //涨跌幅
        int colorResId = R.color.black;

        Double rate = quoteDetail.getRiseRate();
        if (rate != null) {
            percent = (int)(rate * 100);
            THSignedNumber roi = THSignedPercentage.builder(rate * 100)
                    .withSign()
                    .signTypePlusMinusAlways()
                    .build();
            colorResId = roi.getColorResId();
            tvSecurityDetailRate.setText(roi.toString());
            tvSecurityDetailRate.setTextColor(getResources().getColor(colorResId));
        }


        if(quoteDetail.last !=null ) {
            tvSecurityPrice.setText(SecurityCompactDTO.getShortValue(quoteDetail.last));
        }
        tvSecurityPrice.setTextColor(getResources().getColor(colorResId));

        tvSecurityDetailNum.setText(quoteDetail.getPriceDifferent());
        tvSecurityDetailNum.setTextColor(getResources().getColor(colorResId));

        if (quoteDetail.high != null) {
            tvInfo0Value.setText(THSignedMoney.builder(quoteDetail.high)
                    .currency(currency)
                    .build().toString());
        }

        if (quoteDetail.low != null) {
            tvInfo1Value.setText(THSignedMoney.builder(quoteDetail.low)
                    .currency(currency)
                    .build().toString());
        }

        if (quoteDetail.vol != null) {
            tvInfo2Value.setText(NumberDisplayUtils.getString(quoteDetail.vol)
                    + getString(R.string.stock_unit));
        }

        if (quoteDetail.open != null) {
            tvTodayPriceBegin.setText(THSignedMoney.builder(quoteDetail.open)
                    .currency(currency)
                    .build().toString());
        }

        if (quoteDetail.prec != null) {
            tvYesterdayPriceEnd.setText(THSignedMoney.builder(quoteDetail.prec)
                    .currency(currency)
                    .build().toString());
            mPreClose = quoteDetail.prec;
        }

        if (quoteDetail.amou != null) {
            tvTotalAmount.setText(NumberDisplayUtils.getString(quoteDetail.amou));
        }

    }

    private void linkWith(ChartTimeSpan timeSpan) {
        chartDTO.setChartTimeSpan(timeSpan);
        displayChartImage();
    }

    private boolean isBuyOrSaleValid() {
        if (quoteDTO == null) return false;
        if (isFromCompetition && portfolioCompactDTO == null) {
            return false;
        }
        return true;
    }


    public void enterBuySale(boolean isBuy) {
        if (isBuyOrSaleValid()) {
            if (portfolioCompactDTO==null ||  portfolioCompactDTO.getPortfolioId()==null) {
                return;
            }
            if (isBuy) {
                enterSecurityOptBuy();
            } else {
                enterSecurityOptSell();
            }
        }
    }

    protected class PositionNewCacheListener implements DTOCacheNew.Listener<PositionDTOKey, PositionDTO>
    {
        @Override public void onDTOReceived(@NotNull final PositionDTOKey key, @NotNull final PositionDTO value)
        {
            linkWith((PositionDTOCompact)value);
        }

        @Override public void onErrorThrown(@NotNull PositionDTOKey key, @NotNull Throwable error)
        {
        }
    }

    protected void linkWith(PositionDTOCompact value)
    {
        if (value != null)
        {
            PositionDTOCompactList positionDTOCompacts = new PositionDTOCompactList();
            positionDTOCompacts.add(value);
            linkWith(positionDTOCompacts);
        }

        if (value instanceof PositionDTO)
        {
            if (((PositionDTO) value).userId == 0)
            {
                ((PositionDTO) value).userId = currentUserId.toUserBaseKey().getUserId();
            }
            getArguments().putBundle(BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE, ((PositionDTO) value).getPositionDTOKey().getArgs());
            getArguments().putBundle(BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, ((PositionDTO) value).getOwnedPortfolioId().getArgs());
            getTradeTabDetail();
        }
    }

    protected class AbstractBuySellSecurityPositionCacheListener implements DTOCacheNew.Listener<SecurityId, SecurityPositionDetailDTO>
    {
        @Override public void onDTOReceived(@NotNull final SecurityId key, @NotNull final SecurityPositionDetailDTO value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull SecurityId key, @NotNull Throwable error)
        {
        }
    }

    protected class AbstractBuySellUserProfileCacheListener implements DTOCacheNew.HurriedListener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onPreCachedDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            linkWith(value);
        }

        @Override public void onDTOReceived(@NotNull final UserBaseKey key, @NotNull final UserProfileDTO value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
        }
    }

    private void linkWith(SecurityPositionDetailDTO detailDTO) {
        this.securityPositionDetailDTO = detailDTO;
        if (securityPositionDetailDTO != null) {
            if(securityPositionDetailDTO.security != null) {
                linkWith(securityPositionDetailDTO.security);
            }
            if(securityPositionDetailDTO.positions != null) {
                linkWith(securityPositionDetailDTO.positions);
            }
        }
        else
        {
            linkWith((SecurityCompactDTO) null);
            linkWith((PositionDTOCompactList) null);
        }
    }

    public void linkWith(final PositionDTOCompactList positionDTOCompacts)
    {
        this.positionDTOCompactList = positionDTOCompacts;
        setInitialBuySaleQuantityIfCan();
    }

    public void linkWith(final UserProfileDTO userProfileDTO)
    {
        this.userProfileDTO = userProfileDTO;
        setInitialBuySaleQuantityIfCan();
    }

    protected void requestUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.get().register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    protected class BuySellUserWatchlistCacheListener
            implements DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull WatchlistPositionDTOList value)
        {
            linkWithWatchlist(value, true);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
        }
    }

    protected void linkWithWatchlist(WatchlistPositionDTOList watchedList, boolean andDisplay)
    {
        this.watchedList = watchedList;
        if (andDisplay)
        {
            if (watchedList != null && securityId != null)
            {
                isInWatchList = watchedList.contains(securityId);
                displayInWatchButton();
            }
        }
    }

    public void displayInWatchButton() {
        if(isInWatchList){
            addWatchListTV.setText("－自选");
        }else{
            addWatchListTV.setText("＋自选");
        }
    }

    protected void detachWatchlistFetchTask()
    {
        userWatchlistPositionCache.unregister(userWatchlistPositionCacheFetchListener);
    }

    public void fetchWatchlist()
    {
        detachWatchlistFetchTask();
        userWatchlistPositionCache.register(currentUserId.toUserBaseKey(), userWatchlistPositionCacheFetchListener);
        userWatchlistPositionCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    private double watchPrice = 1.0;
    private int watchQuantity = 1;

    private void handleWatchButtonClicked()
    {
        showProgressBar();
        try
        {
            double price = watchPrice;
            int quantity = watchQuantity;
            // add new watchlist
            WatchlistPositionFormDTO watchPositionItemForm = new WatchlistPositionFormDTO(securityCompactDTO.id, price, quantity);
            detachMiddleCallbackUpdate();
            middleCallbackUpdate = watchlistServiceWrapper.createWatchlistEntry(
                    watchPositionItemForm,
                    new WatchlistEditTHCallback());
        } catch (NumberFormatException ex) {
            THToast.show(getString(R.string.wrong_number_format));
            dismissProgress();
        } catch (Exception ex) {
            THToast.show(ex.getMessage());
            dismissProgress();
        }
    }

    private void handleButtonDeleteClicked()
    {
        WatchlistPositionDTO watchlistPositionDTO = watchlistPositionCache.get().get(securityId);
        if (watchlistPositionDTO != null)
        {
            showProgressBar();
            detachMiddleCallbackDelete();
            middleCallbackDelete =
                    watchlistServiceWrapper.deleteWatchlist(watchlistPositionDTO.getPositionCompactId(), new WatchlistDeletedTHCallback());
        }
    }

    private void showProgressBar()
    {
        if (progressBar != null)
        {
            progressBar.show();
        }
        else
        {
            progressBar = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.watchlist_updating);
        }
    }

    private void dismissProgress()
    {
        ProgressDialog progressBarCopy = progressBar;
        if (progressBarCopy != null)
        {
            progressBarCopy.dismiss();
        }
        progressBar = null;
    }

    protected class WatchlistEditTHCallback extends THCallback<WatchlistPositionDTO>
    {
        @Override protected void finish()
        {
            dismissProgress();
        }

        @Override protected void success(@NotNull WatchlistPositionDTO watchlistPositionDTO, THResponse response)
        {
            dismissProgress();
            THToast.show("添加自选成功");
            isInWatchList = true;
            displayInWatchButton();
        }

        @Override protected void failure(THException ex)
        {
            THToast.show(ex);
            dismissProgress();
        }
    }

    protected class WatchlistDeletedTHCallback extends WatchlistEditTHCallback {
        @Override protected void success(@NotNull WatchlistPositionDTO watchlistPositionDTO,
                THResponse response) {
            dismissProgress();
            isInWatchList = false;
            displayInWatchButton();
            THToast.show("取消自选成功");
        }
    }

    protected void detachMiddleCallbackDelete()
    {
        if (middleCallbackDelete != null)
        {
            middleCallbackDelete.setPrimaryCallback(null);
        }
        middleCallbackDelete = null;
    }

    protected void detachMiddleCallbackUpdate()
    {
        if (middleCallbackUpdate != null)
        {
            middleCallbackUpdate.setPrimaryCallback(null);
        }
        middleCallbackUpdate = null;
    }

    public int getCompetitionID()
    {
        return competitionID;
    }

    private void showGuideView()
    {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (betterViewAnimator == null)
                {
                    return;
                }
                int width = betterViewAnimator.getWidth();
                int height = betterViewAnimator.getHeight();
                int radius = betterViewAnimator.getHeight() / 2;
                int position_y = (int) betterViewAnimator.getY() + height / 2;
                int position_x = (int) betterViewAnimator.getX() + width / 4;
                ((DashboardActivity) getActivity()).showGuideView(position_x, position_y, radius, GuideView.TYPE_GUIDE_STOCK_BUY);
            }
        }, 500);
    }

    @Override public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.btnTabChart0) {
            setChartView(0);
            showTimeList();
            linkWith(new ChartTimeSpan(getChartTimeSpanDuration(0)));
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_CHART_ONEDAY));
            return;
        }
        if (viewId == R.id.btnTabChart1) {
            setChartView(1);
            showKChartList();
            if (QuoteServiceWrapper.isChinaStock(securityId)) {
                quoteServiceWrapper.getKline(securityId, QuoteServiceWrapper.K_LINE_DAY, kLinesListCallback);
            } else {
                linkWith(new ChartTimeSpan(getChartTimeSpanDuration(1)));
            }
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_CHART_FIVEDAY));
            return;
        }
        if (viewId == R.id.btnTabChart2) {
            setChartView(2);
            showKChartList();
            if (QuoteServiceWrapper.isChinaStock(securityId)) {
                quoteServiceWrapper.getKline(securityId, QuoteServiceWrapper.K_LINE_WEEK, kLinesListCallback);
            } else {
                linkWith(new ChartTimeSpan(getChartTimeSpanDuration(2)));
            }
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_CHART_90DAY));
            return;
        }
        if (viewId == R.id.btnTabChart3) {
            setChartView(3);
            showKChartList();
            if (QuoteServiceWrapper.isChinaStock(securityId)) {
                quoteServiceWrapper.getKline(securityId, QuoteServiceWrapper.K_LINE_MONTH, kLinesListCallback);
            } else {
                linkWith(new ChartTimeSpan(getChartTimeSpanDuration(3)));
            }
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_CHART_YEAR));
            return;
        }
        if (viewId == R.id.btnTabDiscuss) {
            setDiscussOrNewsView(0);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_TAB_DISCUSS));
            return;
        }
        if (viewId == R.id.btnTabNews) {
            setDiscussOrNewsView(1);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_TAB_NEWS));
        }
        if (viewId == R.id.btnTabUserOperation){
            setDiscussOrNewsView(2);
            return;
        }
        if (viewId == R.id.btnTabUserPosition) {
            setDiscussOrNewsView(3);
            return;
        }

        if (betterViewAnimator.getDisplayedChildLayoutId() == R.id.progress) return;

        if (viewId == R.id.llSecurityBuy) {
            enterBuySale(true);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,
                    AnalyticsConstants.BUTTON_STOCK_DETAIL_OPER_BUY));
            return;
        }
        if (viewId == R.id.llSecuritySale) {
            enterBuySale(false);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,
                    AnalyticsConstants.BUTTON_STOCK_DETAIL_OPER_SALE));
            return;
        }
        if (viewId == R.id.llSecurityDiscuss) {
            enterDiscussSend();
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,
                    AnalyticsConstants.BUTTON_STOCK_DETAIL_OPER_DISCUSS));
            return;
        }
        if(viewId == R.id.llSecurityWatchList){
            onAddWatchListBtnClicked();
            return;
        }
    }

    private void showKChartList()
    {
        if (chartImageWrapper != null) {
            chartImageWrapper.setDisplayedChildByLayoutId(R.id.chart_loading);
            if (quoteServiceWrapper.isChinaStock(securityId)) {
                chartImage.setVisibility(View.GONE);
                timeListView.setVisibility(View.GONE);
                kChartsListView.setVisibility(View.VISIBLE);
            } else {
                chartImage.setVisibility(View.VISIBLE);
                timeListView.setVisibility(View.GONE);
                kChartsListView.setVisibility(View.GONE);
            }
        }
    }

    private void showTimeList()
    {
        if (chartImageWrapper != null && timeListView != null && chartImage != null && kChartsListView != null) {
            chartImageWrapper.setDisplayedChildByLayoutId(R.id.chart_loading);
            if (quoteServiceWrapper.isChinaStock(securityId)) {
                chartImage.setVisibility(View.GONE);
                timeListView.setVisibility(View.VISIBLE);
                kChartsListView.setVisibility(View.GONE);
            } else {
                chartImage.setVisibility(View.VISIBLE);
                timeListView.setVisibility(View.GONE);
                kChartsListView.setVisibility(View.GONE);
            }
        }
    }

    public void linkWith(com.tradehero.th.api.position.PositionDTOKey newPositionDTOKey)
    {
        if (newPositionDTOKey != null)
        {
            this.positionDTOKey = newPositionDTOKey;
            fetchPosition();
        }
        else
        {
            if (betterViewAnimatorPortfolio != null)
            {
                betterViewAnimatorPortfolio.setDisplayedChildByLayoutId(R.id.listTrade);
            }
        }
    }

    protected void detachFetchPosition()
    {
        positionCache.get().unregister(fetchPositionListener);
    }

    protected void detachFetchTrades()
    {
        tradeListCache.get().unregister(fetchTradesListener);
    }

    protected void fetchPosition()
    {
        detachFetchPosition();
        positionCache.get().register(positionDTOKey, fetchPositionListener);
        positionCache.get().getOrFetchAsync(positionDTOKey);
    }

    protected class TradeListFragmentPositionCacheListener implements DTOCacheNew.Listener<com.tradehero.th.api.position.PositionDTOKey, PositionDTO> {
        @Override public void onDTOReceived(@NotNull com.tradehero.th.api.position.PositionDTOKey key, @NotNull PositionDTO value) {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull com.tradehero.th.api.position.PositionDTOKey key, @NotNull Throwable error) { }
    }

    public void linkWith(PositionDTO positionDTO) {
        if (getActivity() == null) return;
        this.positionDTO = positionDTO;
        fetchTrades();
        displayPosition(positionDTO);
    }

    private void displayPosition(PositionDTO positionDTO)
    {
        try {
            THSignedNumber roi = THSignedPercentage.builder(positionDTO.getROISinceInception() * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            tvPositionTotalCcy.setTextColor(getResources().getColor(roi.getColorResId()));
            tvPositionTotalCcy.setText("$" + positionDTO.getTotalScoreOfTrade() + "(" + roi.toString() + ")");
            tvPositionSumAmont.setText("$" + Math.round(positionDTO.sumInvestedAmountRefCcy));
            tvPositionStartTime.setText(DateUtils.getFormattedDate(getResources(), positionDTO.earliestTradeUtc));
            tvPositionLastTime.setText(DateUtils.getFormattedDate(getResources(), positionDTO.latestTradeUtc));
            tvPositionHoldTime.setText(getResources().getString(R.string.position_hold_days,
                    DateUtils.getNumberOfDaysBetweenDates(positionDTO.earliestTradeUtc, positionDTO.getLatestHoldDate())));

            THSignedNumber roiNew = THSignedPercentage.builder(positionDTO.getROISinceInception() * 100)
                    .withSign()
                    .signTypeMinusOnly()
                    .build();
            String cost = "--";
            double benefit = positionDTO.getTotalScoreOfTrade();
            if(positionDTO.averagePriceRefCcy!=null) {
                double costPrice = positionDTO.averagePriceRefCcy;
                DecimalFormat df = new DecimalFormat("######0.00");
                cost = df.format(costPrice);
            }
            displayNotice(cost, benefit, roiNew.toString(), "$");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void fetchTrades() {
        if (positionDTO != null) {
            detachFetchTrades();
            OwnedPositionId key = positionDTO.getOwnedPositionId();
            tradeListCache.get().register(key, fetchTradesListener);
            tradeListCache.get().getOrFetchAsync(key, true);
        } else {
            betterViewAnimatorPortfolio.setDisplayedChildByLayoutId(R.id.listTrade);
        }
    }

    private class GetTradesListener implements TradeListCache.Listener<OwnedPositionId, TradeDTOList>
    {
        @Override public void onDTOReceived(@NotNull OwnedPositionId key, @NotNull TradeDTOList tradeDTOs)
        {
            linkWith(tradeDTOs);
            onFinish();
        }

        @Override public void onErrorThrown(@NotNull OwnedPositionId key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_trade_list_info);
            onFinish();
        }

        public void onFinish()
        {
            if (progressBarPortfolio != null)
            {
                progressBarPortfolio.stopLoading();
            }
            if (betterViewAnimatorPortfolio != null)
            {
                betterViewAnimatorPortfolio.setDisplayedChildByLayoutId(R.id.listTrade);
            }
        }
    }

    public void linkWith(TradeDTOList tradeDTOs) {
        this.tradeDTOList = tradeDTOs;
        adapter.setTradeList(tradeDTOList);
    }

    private Handler handler = new Handler();

    private Runnable runnable;

    public void startTimerForView() {
        runnable = new Runnable()
        {
            @Override
            public void run()
            {
                closeTimerForView();
                getTradeTabDetail();
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    public void closeTimerForView() {
        try {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTrade);
            handler.removeCallbacks(runnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enterDiscussSend() {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDiscussSendFragment.BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
        pushFragment(SecurityDiscussSendFragment.class, bundle);
    }

    private void initSubFragments() {
        if(subFragments.size()<=0) {
            subFragments.clear();
            SecurityDetailSubDiscussFragment discussFragment = new SecurityDetailSubDiscussFragment();
            Bundle args = getArguments();
            args.putInt(BUNDLE_KEY_SECURITY_DTO_ID_BUNDLE, securityCompactDTO.id);
            discussFragment.setArguments(args);
            subFragments.add(discussFragment);
            SecurityDetailSubNewsFragment newsFragment = new SecurityDetailSubNewsFragment();
            newsFragment.setArguments(args);
            subFragments.add(newsFragment);
            SecurityDetailSubOptFragment optFragment = new SecurityDetailSubOptFragment();
            optFragment.setArguments(getArguments());
            subFragments.add(optFragment);
            SecurityDetailSubPositionFragment positionFragment = new SecurityDetailSubPositionFragment();
            positionFragment.setArguments(getArguments());
            subFragments.add(positionFragment);
        }
    }

    private void initSubViewPager(){
        initSubFragments();
        if(subFragmentPagerAdapter==null){
            subFragmentPagerAdapter = new SubFragmentPagerAdapter(fragmentManager);
        }
        subViewPager.setAdapter(subFragmentPagerAdapter);
        subViewPager.setCurrentItem(0);
        setCategoryViews();
        subViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                indexSubFragment = position;
                setCategoryViews();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    void updateDiscussionCount(int count) {
        if (discussNumTV != null) {
            if(count <=0 ){
                discussNumTV.setVisibility(View.GONE);
                return;
            }
            if(count > 99){
                discussNumTV.setText("99");
                return;
            }
            discussNumTV.setText(String.valueOf(count));
        }
    }

    public class SubFragmentPagerAdapter extends FragmentPagerAdapter{

        public SubFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return subFragments.get(position);
        }

        @Override
        public int getCount() {
            return subFragments.size();
        }
    }

    private void updateSubHead(){
        if(securityCompactDTO == null || quoteDetail == null){
            setHeadViewMiddleSub("");
            return;
        }
        if(scrollY%4 != 0){
            return;
        }
        if(scrollY < stockDetailHeight){
            setHeadViewMiddleSubTextColor(colorNormal);
            if(securityCompactDTO.marketOpen){
                setHeadViewMiddleSub("交易中");
            } else {
                setHeadViewMiddleSub("交易结束");
            }
        } else {
            double raisePercent = quoteDetail.getRiseRate() * 100.0;
            DecimalFormat df = new DecimalFormat("######0.00");
            String raisePercentStr = df.format(raisePercent) + "%";
            if(quoteDetail.getRiseRate() > 0){
                setHeadViewMiddleSubTextColor(colorUp);

                setHeadViewMiddleSub(quoteDetail.last + " +" + raisePercentStr + " +" + quoteDetail.getRise());
            }
            if(quoteDetail.getRiseRate() == 0){
                setHeadViewMiddleSubTextColor(colorNormal);
                setHeadViewMiddleSub(quoteDetail.last + " 0 0");
            }
            if(quoteDetail.getRiseRate() < 0){
                setHeadViewMiddleSubTextColor(colorDown);
                setHeadViewMiddleSub(quoteDetail.last + " " + raisePercentStr + " " + quoteDetail.getRise());
            }
        }

    }



    @Override
    public void onClickHeadRight0() {
        enterSearchPage();
    }

    private void enterSearchPage(){
        pushFragment(SearchUnitFragment.class, new Bundle());
    }

    @Override
    public void onClickHeadRight1() {
        enterWechatSharePage();
    }

    private void enterWechatSharePage(){
        WeChatDTO weChatDTO = new WeChatDTO();
        String message = WXMessage.getSecurityShareMessage(percent, securityHead);
        weChatDTO.title = message + Constants.WECHAT_SHARE_URL_INSTALL_APP;
        weChatDTO.type = WeChatMessageType.Trade;
        Intent gotoShareToWeChatIntent = new Intent(getActivity(), WXEntryActivity.class);
        gotoShareToWeChatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        WXEntryActivity.putWeChatDTO(gotoShareToWeChatIntent, weChatDTO);
        Application.context().startActivity(gotoShareToWeChatIntent);
    }

    private void onAddWatchListBtnClicked(){
        if(isInWatchList){
            showWatchListPopWindow(llWatchList);
        }else{
            handleWatchButtonClicked();
        }
    }

    private void showWatchListPopWindow(final View parent) {
        if(popWin==null){
            LayoutInflater lay = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = lay.inflate(R.layout.popwindow_security_detail_watchlistopt, null);
            watchlistShareLL = (LinearLayout)v.findViewById(R.id.ll_popwin_share);
            watchlistRemoveLL = (LinearLayout)v.findViewById(R.id.ll_popwin_removewathlist);
            popWin = new PopupWindow(v,(int)getActivity().getResources().getDimension(R.dimen.security_detail_popwindow_width), (int)getActivity().getResources().getDimension(R.dimen.security_detail_popwindow_height));
            popWin.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.popwindow_security_detail_bg));
            popWin.setOutsideTouchable(true);
            watchlistRemoveLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleButtonDeleteClicked();
                    popWin.dismiss();
                }
            });
            watchlistShareLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterWechatSharePage();
                    popWin.dismiss();
                }
            });
        }
        popWin.setFocusable(true);
        popWin.update();
        popWin.showAsDropDown(parent, -30, 0);
    }

    private void enterSecurityOptBuy(){
        if (isFromCompetition && portfolioCompactDTO == null) {
            THToast.show("参加比赛后才能买卖股票");
            return;
        }
        if(securityId == null){
            return;
        }
        if (portfolioCompactDTO==null ||  portfolioCompactDTO.getPortfolioId()==null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityOptActivity.KEY_PORTFOLIO_ID, portfolioCompactDTO.getPortfolioId().getArgs());
        bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_BUY);
        bundle.putString(SecurityOptActivity.KEY_SECURITY_EXCHANGE, securityId.getExchange());
        bundle.putString(SecurityOptActivity.KEY_SECURITY_SYMBOL, securityId.getSecuritySymbol());
        bundle.putInt(CompetitionSecuritySearchFragment.BUNDLE_COMPETITION_ID, competitionID);
        bundle.putString(BUNDLE_KEY_SECURITY_NAME, securityName);
        Intent intent = new Intent(getActivity(), SecurityOptActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
    }

    private void enterSecurityOptSell(){
        if (isFromCompetition && portfolioCompactDTO == null) {
            THToast.show("请先参加比赛");
            return;
        }
        if(securityId == null){
            return;
        }
        if (portfolioCompactDTO==null ||  portfolioCompactDTO.getPortfolioId()==null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityOptActivity.KEY_PORTFOLIO_ID, portfolioCompactDTO.getPortfolioId().getArgs());
        bundle.putString(SecurityOptActivity.BUNDLE_FROM_TYPE, SecurityOptActivity.TYPE_SELL);
        bundle.putString(SecurityOptActivity.KEY_SECURITY_EXCHANGE, securityId.getExchange());
        bundle.putString(SecurityOptActivity.KEY_SECURITY_SYMBOL, securityId.getSecuritySymbol());
        bundle.putInt(CompetitionSecuritySearchFragment.BUNDLE_COMPETITION_ID, competitionID);
        bundle.putString(BUNDLE_KEY_SECURITY_NAME, securityName);
        Intent intent = new Intent(getActivity(), SecurityOptActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in,R.anim.slide_left_out);
    }
}