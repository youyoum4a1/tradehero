package com.tradehero.chinabuild.fragment.security;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.squareup.widgets.AspectRatioImageView;
import com.tradehero.chinabuild.cache.PositionCompactNewCache;
import com.tradehero.chinabuild.cache.PositionDTOKey;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.chinabuild.dialog.DialogFactory;
import com.tradehero.chinabuild.dialog.SecurityDetailDialogLayout;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.SecurityDiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.MainActivity;
import com.tradehero.th.adapters.PositionTradeListAdapter;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemListKey;
import com.tradehero.th.api.news.key.NewsItemListSecurityKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
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
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.api.trade.TradeDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import com.tradehero.th.fragments.security.ChartImageView;
import com.tradehero.th.fragments.trade.FreshQuoteHolder;
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
import com.tradehero.th.network.service.WatchlistServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.news.NewsItemCompactListCacheNew;
import com.tradehero.th.persistence.portfolio.PortfolioCompactCache;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.trade.TradeListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.GuideView;
import com.tradehero.th.widget.MarkdownTextView;
import com.tradehero.th.widget.TradeHeroProgressBar;
import com.viewpagerindicator.SquarePageIndicator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dagger.Lazy;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by huhaiping on 14-9-1.
 */
public class SecurityDetailFragment extends BasePurchaseManagerFragment
        implements DiscussionListCacheNew.DiscussionKeyListListener, View.OnClickListener
{
    public final static String BUNDLE_KEY_SECURITY_NAME = SecurityDetailFragment.class.getName() + ".securityName";
    public final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = SecurityDetailFragment.class.getName() + ".securityId";
    public final static String BUNDLE_KEY_COMPETITION_ID_BUNDLE = SecurityDetailFragment.class.getName() + ".competitionID";
    public final static String BUNDLE_KEY_GOTO_TRADE_DETAIL = SecurityDetailFragment.class.getName() + ".gotoTradeDetail";

    public final static long MILLISEC_QUOTE_REFRESH = 10000;
    public final static long MILLISEC_QUOTE_COUNTDOWN_PRECISION = 50;

    @Inject Analytics analytics;
    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    private MiddleCallback<DiscussionDTO> voteCallback;
    @Inject CurrentUserId currentUserId;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    @Inject ChartDTOFactory chartDTOFactory;
    private ChartDTO chartDTO;

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
    protected FreshQuoteHolder freshQuoteHolder;
    @Nullable protected QuoteDTO quoteDTO;
    protected boolean refreshingQuote = false;
    protected boolean isTransactionTypeBuy = true;
    protected Integer mBuyQuantity;
    protected Integer mSellQuantity;
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    public PositionDTOCompactList positionDTOCompactList;
    protected PortfolioCompactDTO portfolioCompactDTO;
    @Inject PortfolioCompactCache portfolioCompactCache;

    private boolean isAddWatchSheetOpen = false;
    private boolean isInWatchList = false;//是否是自选股
    private String securityName;
    private DialogFactory dialogFactory;
    private Dialog dialog;
    @Nullable private MiddleCallback<WatchlistPositionDTO> middleCallbackUpdate;
    @Nullable private MiddleCallback<WatchlistPositionDTO> middleCallbackDelete;
    @Inject WatchlistServiceWrapper watchlistServiceWrapper;
    @Inject Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject DiscussionCache discussionCache;
    @Inject DiscussionListCacheNew discussionListCache;
    private PaginatedDiscussionListKey discussionListKey;
    private NewsItemListKey listKey;
    @Inject NewsItemCompactListCacheNew newsTitleCache;
    @Nullable private DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> newsCacheListener;
    @Inject public Lazy<PrettyTime> prettyTime;
    private AbstractDiscussionCompactDTO dtoDiscuss;
    private AbstractDiscussionCompactDTO dtoNews;
    protected DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> userWatchlistPositionCacheFetchListener;
    private ProgressDialog progressBar;
    @Inject ProgressDialogUtil progressDialogUtil;
    public int competitionID;
    private int indexChart = -1;
    private int indexDiscussOrNews = -1;

    //Security Detail Tab Start
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.ic_info_buy_sale_btns) LinearLayout llBuySaleButtons;//购买卖出栏
    @InjectView(R.id.llSecurityBuy) RelativeLayout llSecurityBuy;//购买
    @InjectView(R.id.llSecuritySale) RelativeLayout llSecuritySale;//出售
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) SquarePageIndicator indicator;
    private List<View> views = new ArrayList<>();

    private Button[] btnChart;
    Button btnChart0;
    Button btnChart1;
    Button btnChart2;
    Button btnChart3;

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
    protected TextView tvLoadingChart;
    protected TextView tvSecurityDiscussOrNewsMore;

    ImageLoadingListener chartLoadingListener;

    TextView tvSecurityPrice;//当前价格
    TextView tvSecurityDetailRate;//涨跌幅
    TextView tvSecurityDetailNum;//涨跌值
    TextView tvInfo0Value;//最高
    TextView tvInfo1Value;//最低
    TextView tvInfo2Value;//成交量

    LinearLayout llDisscurssOrNews;
    ImageView imgSecurityTLUserHeader;
    TextView tvUserTLTimeStamp;
    TextView tvUserTLContent;
    TextView tvUserTLName;
    LinearLayout llTLPraise;
    LinearLayout llTLPraiseDown;
    LinearLayout llTLComment;
    TextView tvTLPraise;
    TextView btnTLPraise;
    TextView tvTLPraiseDown;
    TextView btnTLPraiseDown;
    TextView tvTLComment;
    LinearLayout bottomBarLL;
    //Security Detail Tab End

    //Portfolio Detail Tab Start
    TextView tvPositionTotalCcy;//累计盈亏
    TextView tvPositionSumAmont;//总投资
    TextView tvPositionStartTime;//建仓时间
    TextView tvPositionLastTime;//最后交易
    TextView tvPositionHoldTime;//持有时间
    TextView tvEmpty;//没有交易明细
    SecurityListView listView;
    TradeHeroProgressBar progressBarPortfolio;
    BetterViewAnimator betterViewAnimatorPortfolio;
    //Portfolio Detail Tab End

    public static final int ERROR_NO_ASK_BID = 0;
    public static final int ERROR_NO_ASK = 1;
    public static final int ERROR_NO_BID = 2;
    public static final int ERROR_NO_COMPETITION_PROTFOLIO = 3;

    boolean isFromCompetition = false;


    private boolean isNews = false;

    public static final String BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE =
            SecurityDetailFragment.class.getName() + ".purchaseApplicablePortfolioId";
    public static final String BUNDLE_KEY_POSITION_DTO_KEY_BUNDLE = SecurityDetailFragment.class.getName() + ".positionDTOKey";

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
    private boolean isFristLunch;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        compactCacheListener = new StockInfoFragmentSecurityCompactCacheListener();
        chartDTO = chartDTOFactory.createChartDTO();
        securityPositionDetailListener = new AbstractBuySellSecurityPositionCacheListener();
        positionNewCacheListener = new PositionNewCacheListener();
        userProfileCacheListener = new AbstractBuySellUserProfileCacheListener();
        userWatchlistPositionCacheFetchListener = new BuySellUserWatchlistCacheListener();
        newsCacheListener = new NewsHeadlineNewsListListener();

        fetchPositionListener = new TradeListFragmentPositionCacheListener();
        fetchTradesListener = new GetTradesListener();
        adapter = new PositionTradeListAdapter(getActivity());

        isGotoTradeDetail = getArguments().getBoolean(BUNDLE_KEY_GOTO_TRADE_DETAIL, false);
        isFristLunch = true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        Bundle args = getArguments();
        if (args != null)
        {
            Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
            securityName = args.getString(BUNDLE_KEY_SECURITY_NAME);
            if (securityIdBundle != null)
            {
                securityId = new SecurityId(securityIdBundle);
            }
            setHeadViewMiddleMain(securityName);
            setHeadViewMiddleSub(securityId.getDisplayName());

            if (watchedList != null && securityId != null)
            {
                isInWatchList = watchedList.contains(securityId);
                displayInWatchButton();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.security_detail_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        updateHeadView(true);
        betterViewAnimator.setDisplayedChildByLayoutId(R.id.progress);

        return view;
    }

    //+自选股 已添加
    @Override public void onClickHeadRight0()
    {
        if (!isInWatchList)
        {
            if (securityId != null && securityCompactDTO != null)
            {
                handleWatchButtonClicked();
            }
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_ADDWATCH));
        }
        else
        {
            if (isAddWatchSheetOpen)
            {
                closeWatchSheet();
            }
            else
            {
                openWatchSheet();
            }
            displayInWatchButton();
        }
    }

    public void openWatchSheet()
    {
        isAddWatchSheetOpen = true;
        displayInWatchButton();

        if (dialogFactory == null)
        {
            dialogFactory = new DialogFactory();
        }

        dialog = dialogFactory.createSecurityDetailDialog(getActivity(),
                new SecurityDetailDialogLayout.OnMenuClickedListener()
                {
                    @Override public void onCancelClicked()
                    {

                    }

                    @Override public void onShareRequestedClicked(int position)
                    {
                        dialog.dismiss();
                        if (position == SecurityDetailDialogLayout.INDEX_CANCEL_WATCH)
                        {
                            //取消关注
                            handleButtonDeleteClicked();
                            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,
                                    AnalyticsConstants.BUTTON_STOCK_DETAIL_CANCELWATCH));
                        }
                    }
                });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                closeWatchSheet();
            }
        });
    }

    public void closeWatchSheet()
    {
        isAddWatchSheetOpen = false;
        displayInWatchButton();
    }

    public void setBuySaleButtonVisable(boolean isCanSale)
    {
        try
        {
            llBuySaleButtons.setVisibility(View.VISIBLE);
            llSecurityBuy.setVisibility(View.VISIBLE);
            llSecuritySale.setVisibility(isCanSale ? View.VISIBLE : View.GONE);
            if (isCanSale || isBuyOrSaleValid(true, false))
            {
                betterViewAnimator.setDisplayedChildByLayoutId(R.id.ic_info_buy_sale_btns);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void initView()
    {
        initTabPageView();
        tvUserTLContent.setMaxLines(8);
        llBuySaleButtons.setVisibility(View.GONE);

        btnChart = new Button[4];
        btnChart[0] = btnChart0;
        btnChart[1] = btnChart1;
        btnChart[2] = btnChart2;
        btnChart[3] = btnChart3;
        setDefaultBtnTabView();

        chartDTO.setIncludeVolume(chartImage.includeVolume);
        if (chartImage != null)
        {
            chartImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        chartLoadingListener = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                //
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                //
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                AspectRatioImageView aspectRatioImageView = (AspectRatioImageView) view;
                int imgHeight = aspectRatioImageView.getDrawable().getIntrinsicHeight();
                int imgWidth = aspectRatioImageView.getDrawable().getIntrinsicWidth();
                aspectRatioImageView.setAspectRatioEnabled(true);
                int dominantMeasurement = aspectRatioImageView.getDominantMeasurement();
                if (dominantMeasurement == AspectRatioImageView.MEASUREMENT_WIDTH)
                {
                    aspectRatioImageView.setAspectRatio((float) imgHeight / (float) imgWidth);
                }
                else if (dominantMeasurement == AspectRatioImageView.MEASUREMENT_HEIGHT)
                {
                    aspectRatioImageView.setAspectRatio((float) imgWidth / (float) imgHeight);
                }
                else
                {
                    throw new IllegalArgumentException("Unhandled dominant measurement " + dominantMeasurement);
                }

                if (chartImageWrapper != null)
                {
                    chartImageWrapper.setDisplayedChildByLayoutId(chartImage.getId());
                }
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                //
            }
        };

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

    public void initListView()
    {
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

        if (isGotoTradeDetail && isFristLunch)
        {
            isFristLunch = false;
            indicator.setCurrentItem(1);
        }
    }

    public void initRootViewTab0(View tabView0)
    {
        btnChart0 = (Button) tabView0.findViewById(R.id.btnTabChart0);
        btnChart1 = (Button) tabView0.findViewById(R.id.btnTabChart1);
        btnChart2 = (Button) tabView0.findViewById(R.id.btnTabChart2);
        btnChart3 = (Button) tabView0.findViewById(R.id.btnTabChart3);

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
        tvLoadingChart = (TextView) tabView0.findViewById(R.id.chart_loading);
        tvSecurityDiscussOrNewsMore = (TextView) tabView0.findViewById(R.id.tvSecurityDiscussOrNewsMore);

        tvSecurityPrice = (TextView) tabView0.findViewById(R.id.tvSecurityDetailPrice);
        tvSecurityDetailRate = (TextView) tabView0.findViewById(R.id.tvSecurityDetailRate);
        tvSecurityDetailNum = (TextView) tabView0.findViewById(R.id.tvSecurityDetailNum);
        tvInfo0Value = (TextView) tabView0.findViewById(R.id.tvInfo0Value);
        tvInfo1Value = (TextView) tabView0.findViewById(R.id.tvInfo1Value);
        tvInfo2Value = (TextView) tabView0.findViewById(R.id.tvInfo2Value);

        llDisscurssOrNews = (LinearLayout) tabView0.findViewById(R.id.llDisscurssOrNews);
        imgSecurityTLUserHeader = (ImageView) tabView0.findViewById(R.id.imgSecurityTLUserHeader);
        tvUserTLTimeStamp = (TextView) tabView0.findViewById(R.id.tvUserTLTimeStamp);
        tvUserTLContent = (TextView) tabView0.findViewById(R.id.tvUserTLContent);
        tvUserTLName = (TextView) tabView0.findViewById(R.id.tvUserTLName);
        llTLPraise = (LinearLayout) tabView0.findViewById(R.id.llTLPraise);
        llTLPraiseDown = (LinearLayout) tabView0.findViewById(R.id.llTLPraiseDown);
        llTLComment = (LinearLayout) tabView0.findViewById(R.id.llTLComment);
        tvTLPraise = (TextView) tabView0.findViewById(R.id.tvTLPraise);
        btnTLPraise = (TextView) tabView0.findViewById(R.id.btnTLPraise);
        tvTLPraiseDown = (TextView) tabView0.findViewById(R.id.tvTLPraiseDown);
        btnTLPraiseDown = (TextView) tabView0.findViewById(R.id.btnTLPraiseDown);
        tvTLComment = (TextView) tabView0.findViewById(R.id.tvTLComment);
        bottomBarLL = (LinearLayout) tabView0.findViewById(R.id.ic_info_buy_sale_btns);
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

        llTLComment.setOnClickListener(this);
        llTLPraiseDown.setOnClickListener(this);
        llTLPraise.setOnClickListener(this);
        llDisscurssOrNews.setOnClickListener(this);
        imgSecurityTLUserHeader.setOnClickListener(this);
        tvUserTLContent.setOnClickListener(this);
        tvSecurityDiscussOrNewsMore.setOnClickListener(this);
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

    @Override public void onPause()
    {
        detachSecurityCompactCache();
        destroyFreshQuoteHolder();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        destroyFreshQuoteHolder();
        detachUserProfileCache();
        detachSecurityCompactCache();
        detachSecurityPositionDetailCache();
        detachCompetitionPositionCache();
        detachWatchlistFetchTask();
        detachSecurityDiscuss();
        detachSecurityNews();

        detachFetchPosition();
        detachFetchTrades();

        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onResume()
    {
        initArgment();
        requestUserProfile();
        fetchWatchlist();
        super.onResume();

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
                positionDTOKey = MainActivity.getSecurityPositionDTOKey(securityId);
            }
            if (positionDTOKey == null && securityCompactDTO != null)
            {
                positionDTOKey = MainActivity.getSecurityPositionDTOKey(securityCompactDTO.id);
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

    private void setDefaultBtnTabView()
    {
        if (indexChart == -1)
        {
            btnChart[0].performClick();
        }
        else
        {
            setChartView(indexChart);
        }
        if (indexDiscussOrNews == -1)
        {
            btnDiscuss.performClick();
        }
        else
        {
            setDiscussOrNewsViewDefault();
        }
    }

    private void linkWith(final SecurityId securityId)
    {
        this.securityId = securityId;
        if (securityId != null)
        {
            queryCompactCache(securityId);
            prepareFreshQuoteHolder();

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

    protected void prepareFreshQuoteHolder()
    {
        destroyFreshQuoteHolder();
        freshQuoteHolder = new FreshQuoteHolder(securityId, MILLISEC_QUOTE_REFRESH, MILLISEC_QUOTE_COUNTDOWN_PRECISION);
        freshQuoteHolder.setListener(new BuySellFreshQuoteListener());
        freshQuoteHolder.start();
    }

    protected void destroyFreshQuoteHolder()
    {
        if (freshQuoteHolder != null)
        {
            freshQuoteHolder.destroy();
        }
        freshQuoteHolder = null;
    }

    abstract protected class AbstractBuySellFreshQuoteListener implements FreshQuoteHolder.FreshQuoteListener
    {
        @Override abstract public void onMilliSecToRefreshQuote(long milliSecToRefresh);

        @Override public void onIsRefreshing(boolean refreshing)
        {
            setRefreshingQuote(refreshing);
        }

        @Override public void onFreshQuote(QuoteDTO quoteDTO)
        {
            if(quoteDTO==null)return;
            if(quoteDTO.ask!=null&&quoteDTO.ask==0)return;
            if(quoteDTO.bid!=null&&quoteDTO.bid==0)return;
            linkWith(quoteDTO);
        }
    }

    protected void linkWith(QuoteDTO quoteDTO)
    {
        this.quoteDTO = quoteDTO;
        setInitialBuySaleQuantityIfCan();
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
        if (this.mSellQuantity != null)
        {
            setBuySaleButtonVisable(mSellQuantity > 0);//可以卖出
        }
    }

    protected void setInitialBuyQuantityIfCan()
    {
        Integer maxPurchasableShares = getMaxPurchasableShares();
        if (maxPurchasableShares != null)
        {
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

    protected void setRefreshingQuote(boolean refreshingQuote)
    {
        this.refreshingQuote = refreshingQuote;
    }

    protected class BuySellFreshQuoteListener extends AbstractBuySellFreshQuoteListener
    {
        @Override public void onMilliSecToRefreshQuote(long milliSecToRefresh)
        {
        }
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
        }
        else
        {
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

    private void initKey()
    {
        discussionListKey = new PaginatedDiscussionListKey(DiscussionType.SECURITY, securityCompactDTO.id, 1, 20);
        listKey = new NewsItemListSecurityKey(new SecurityIntegerId(securityCompactDTO.id), 1, 20);
        fetchSecurityDiscuss(true);
        fetchSecurityNews(true);
    }

    private void linkWith(SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;

        if (securityCompactDTO != null)
        {
            initKey();
            chartDTO.setSecurityCompactDTO(securityCompactDTO);
        }
        displayChartImage();

        displaySecurityInfo();

        getTradeTabDetail();
    }

    public void setTextForMoreButton()
    {
        tvSecurityDiscussOrNewsMore.setVisibility(View.VISIBLE);
        if (getAbstractDiscussionCompactDTO() == null)
        {
            tvSecurityDiscussOrNewsMore.setText(
                    (indexDiscussOrNews == 0) ? getString(R.string.quickly_to_get_first) : getString(R.string.no_useful_data));
        }
        else
        {
            tvSecurityDiscussOrNewsMore.setText(getString(R.string.click_to_get_more));
        }
    }

    public void setChartView(int select)
    {
        indexChart = select;
        for (int i = 0; i < btnChart.length; i++)
        {
            btnChart[i].setBackgroundResource((i == indexChart ? R.drawable.tab_blue_head_active : R.drawable.tab_blue_head_normal));
        }
        linkWith(new ChartTimeSpan(getChartTimeSpanDuration(indexChart)));
    }

    public void setDiscussOrNewsViewDefault()
    {
        setCategoryViews();
        tvSecurityDiscussOrNewsMore.setText("");
    }

    public void setDiscussOrNewsView(int select)
    {
        indexDiscussOrNews = select;
        setCategoryViews();
        if(indexDiscussOrNews <= 1) {
            displayDiscussOrNewsDTO();
        }
        if(indexDiscussOrNews == 2 ){
            enterUserOptFragment();
        }
    }

    private void setCategoryViews(){
        switch(indexDiscussOrNews){
            case 0:
                discussFocusView.setVisibility(View.VISIBLE);
                newsFocusView.setVisibility(View.GONE);
                operationFocusView.setVisibility(View.GONE);
                positionFocusView.setVisibility(View.GONE);
                break;
            case 1:
                discussFocusView.setVisibility(View.GONE);
                newsFocusView.setVisibility(View.VISIBLE);
                operationFocusView.setVisibility(View.GONE);
                positionFocusView.setVisibility(View.GONE);
                break;
            case 2:
                discussFocusView.setVisibility(View.GONE);
                newsFocusView.setVisibility(View.GONE);
                operationFocusView.setVisibility(View.VISIBLE);
                positionFocusView.setVisibility(View.GONE);
                break;
            case 3:
                discussFocusView.setVisibility(View.GONE);
                newsFocusView.setVisibility(View.GONE);
                operationFocusView.setVisibility(View.GONE);
                positionFocusView.setVisibility(View.VISIBLE);
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
            // HACK TODO find something better than skipCache to avoid OutOfMemory
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

    public void displaySecurityInfo()
    {
        if (securityCompactDTO != null)
        {
            setHeadViewMiddleMain(securityCompactDTO.name);

            //涨跌幅
            if (securityCompactDTO.risePercent != null)
            {

                THSignedNumber roi = THSignedPercentage.builder(securityCompactDTO.risePercent * 100)
                        .withSign()
                        .signTypePlusMinusAlways()
                        .build();

                tvSecurityDetailRate.setText(roi.toString());
                tvSecurityDetailRate.setTextColor(getResources().getColor(roi.getColorResId()));

                tvSecurityPrice.setText(SecurityCompactDTO.getShortValue(securityCompactDTO.lastPrice));
                tvSecurityPrice.setTextColor(getResources().getColor(roi.getColorResId()));

                tvSecurityDetailNum.setText(securityCompactDTO.getPriceDifferent());
                tvSecurityDetailNum.setTextColor(getResources().getColor(roi.getColorResId()));

                if (securityCompactDTO.high != null)
                {
                    tvInfo0Value.setText(THSignedMoney.builder(securityCompactDTO.high)
                            .currency(securityCompactDTO.getCurrencyDisplay())
                            .build().toString());
                }

                if (securityCompactDTO.low != null)
                {
                    tvInfo1Value.setText(THSignedMoney.builder(securityCompactDTO.low)
                            .currency(securityCompactDTO.getCurrencyDisplay())
                            .build().toString());
                }

                if (securityCompactDTO.volume != null)
                {
                    tvInfo2Value.setText(NumberDisplayUtils.getString(securityCompactDTO.volume));
                }
            }
        }
    }

    public void linkWith(ChartTimeSpan timeSpan)
    {
        chartDTO.setChartTimeSpan(timeSpan);
        displayChartImage();
    }

    @OnClick({R.id.llSecurityBuy, R.id.llSecuritySale, R.id.llSecurityDiscuss})
    public void onClickItems(View view)
    {
        if (betterViewAnimator.getDisplayedChildLayoutId() == R.id.progress) return;
        int id = view.getId();
        if (id == R.id.llSecurityBuy)
        {
            enterBuySale(id == R.id.llSecurityBuy);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,
                    AnalyticsConstants.BUTTON_STOCK_DETAIL_OPER_BUY));
        }
        else if (id == R.id.llSecuritySale)
        {
            enterBuySale(id == R.id.llSecurityBuy);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,
                    AnalyticsConstants.BUTTON_STOCK_DETAIL_OPER_SALE));
        }
        else if (id == R.id.llSecurityDiscuss)
        {
            enterDiscussSend();
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED,
                            AnalyticsConstants.BUTTON_STOCK_DETAIL_OPER_DISCUSS));
        }
    }

    public boolean isBuyOrSaleValid(boolean isBuy, boolean display)
    {
        if (quoteDTO == null) return false;
        if (!display) {
            return true;
        }
        if (quoteDTO.ask == null && quoteDTO.bid == null)
        {//ask bid 都没有返回 则说明停牌
            showBuyOrSaleError(ERROR_NO_ASK_BID);
            return false;
        }
        else if (quoteDTO.bid == null && (!isBuy))
        {//跌停
            showBuyOrSaleError(ERROR_NO_BID);
            return false;
        }
        else if (quoteDTO.ask == null && (isBuy))
        {//涨停
            showBuyOrSaleError(ERROR_NO_ASK);
            return false;
        }

        if (isFromCompetition && portfolioCompactDTO == null)
        {
            showBuyOrSaleError(ERROR_NO_COMPETITION_PROTFOLIO);
            return false;
        }

        return true;
    }

    public void showBuyOrSaleError(int type)
    {
        if (type == ERROR_NO_ASK_BID)
        {
            THToast.show("你所购买的股票已停牌");
        }
        else if (type == ERROR_NO_BID)
        {
            THToast.show("你所购买的股票已跌停");
        }
        else if (type == ERROR_NO_ASK)
        {
            THToast.show("你所购买的股票已涨停");
        }
        else if (type == ERROR_NO_COMPETITION_PROTFOLIO)
        {
            THToast.show("请先报名参加比赛");
        }
    }

    public void enterBuySale(boolean isBuy) {

        if (isBuyOrSaleValid(isBuy, true)) {
            Bundle bundle = new Bundle();
            if (portfolioCompactDTO!=null&& portfolioCompactDTO.getPortfolioId()!=null) {
                bundle.putBundle(BuySaleSecurityFragment.KEY_PORTFOLIO_ID, portfolioCompactDTO.getPortfolioId().getArgs());
            } else {
                return;
            }
            bundle.putBundle(BuySaleSecurityFragment.KEY_SECURITY_ID, securityId.getArgs());
            bundle.putBundle(BuySaleSecurityFragment.KEY_QUOTE_DTO, quoteDTO.getArgs());
            bundle.putBoolean(BuySaleSecurityFragment.KEY_BUY_OR_SALE, isBuy);
            bundle.putString(BuySaleSecurityFragment.KEY_SECURITY_NAME, securityName);
            bundle.putInt(BuySaleSecurityFragment.KEY_COMPETITION_ID, competitionID);
            bundle.putSerializable(BuySaleSecurityFragment.KEY_POSITION_COMPACT_DTO, positionDTOCompactList);
            pushFragment(BuySaleSecurityFragment.class, bundle);
        }
    }

    public void enterDiscussSend()
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(SecurityDiscussSendFragment.BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
        pushFragment(SecurityDiscussSendFragment.class, bundle);
    }

    protected class PositionNewCacheListener implements DTOCacheNew.Listener<PositionDTOKey, PositionDTO>
    {
        @Override public void onDTOReceived(@NotNull final PositionDTOKey key, @NotNull final PositionDTO value)
        {
            linkWith(value);
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

    private void linkWith(SecurityPositionDetailDTO detailDTO)
    {
        this.securityPositionDetailDTO = detailDTO;
        if (securityPositionDetailDTO != null)
        {
            linkWith(securityPositionDetailDTO.security);
            linkWith(securityPositionDetailDTO.positions);
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

    public void displayInWatchButton()
    {
        if (isInWatchList)//显示已添加^
        {
            setHeadViewRight0("已添加 ");
            setHeadViewRight0Drawable(null, null,
                    getResources().getDrawable(isAddWatchSheetOpen ? R.drawable.icon_arrow_up : R.drawable.icon_arrow_down), null);
        }
        else//显示 ＋自选股
        {
            setHeadViewRight0("+自选股");
            setHeadViewRight0Drawable(null, null, null, null);
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
            closeWatchSheet();
            displayInWatchButton();
        }

        @Override protected void failure(THException ex)
        {
            THToast.show(ex);
            dismissProgress();
        }
    }

    //TODO this extends is better? maybe not alex
    protected class WatchlistDeletedTHCallback extends WatchlistEditTHCallback
    {
        @Override protected void success(@NotNull WatchlistPositionDTO watchlistPositionDTO,
                THResponse response)
        {
            dismissProgress();
            isInWatchList = false;
            closeWatchSheet();
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

    private void detachSecurityDiscuss()
    {
        discussionListCache.unregister(this);
    }

    private void detachSecurityNews()
    {
        newsTitleCache.unregister(newsCacheListener);
    }

    public void fetchSecurityDiscuss(boolean force)
    {
        if (discussionListKey != null)
        {
            detachSecurityDiscuss();
            discussionListCache.register(discussionListKey, this);
            discussionListCache.getOrFetchAsync(discussionListKey, force);
        }
    }

    private void fetchSecurityNews(boolean force)
    {
        if (listKey != null)
        {
            detachSecurityNews();
            newsTitleCache.register(listKey, newsCacheListener);
            newsTitleCache.getOrFetchAsync(listKey, force);
        }
    }

    @Override public void onDTOReceived(@NotNull DiscussionListKey key, @NotNull DiscussionKeyList value)
    {
        if (value != null && value.size() > 0)
        {
            AbstractDiscussionCompactDTO dto = discussionCache.get(value.get(0));
            if (dto != null)
            {
                dtoDiscuss = dto;
                displayDiscussOrNewsDTO();
            }
        }
    }

    @Override public void onErrorThrown(@NotNull DiscussionListKey key, @NotNull Throwable error)
    {

    }

    protected class NewsHeadlineNewsListListener implements DTOCacheNew.HurriedListener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull NewsItemListKey key,
                @NotNull PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(value);
            finish();
        }

        @Override public void onDTOReceived(
                @NotNull NewsItemListKey key,
                @NotNull PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(value);
            finish();
        }

        @Override public void onErrorThrown(
                @NotNull NewsItemListKey key,
                @NotNull Throwable error)
        {
            finish();
        }

        public void finish()
        {
        }
    }

    public void linkWith(@NotNull PaginatedDTO<NewsItemCompactDTO> value)
    {
        if (value.getData() != null && value.getData().size() > 0)
        {
            NewsItemCompactDTO dto = value.getData().get(0);
            dtoNews = dto;
            displayDiscussOrNewsDTO();
        }
    }

    public void enterTimeLineDetail(AbstractDiscussionCompactDTO dto)
    {
        if (dto != null)
        {
            Bundle bundle = new Bundle();
            bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_ID, dto.getDiscussionKey().getArgs());
            bundle.putInt(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_TYPE, TimeLineItemDetailFragment.DISCUSSION_DISCUSSION_TYPE);
            bundle.putBoolean(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_IS_NEWS, isNews);
            pushFragment(TimeLineItemDetailFragment.class, bundle);
        }
    }

    public void comments(AbstractDiscussionCompactDTO dto)
    {
        DiscussionKey discussionKey = dto.getDiscussionKey();
        Bundle bundle = new Bundle();
        bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE,
                discussionKey.getArgs());
        pushFragment(DiscussSendFragment.class, bundle);
    }

    private void openUserProfile(int userId)
    {
        if (userId >= 0)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
            pushFragment(UserMainPage.class, bundle);
        }
    }

    public AbstractDiscussionCompactDTO getAbstractDiscussionCompactDTO()
    {
        return indexDiscussOrNews == 0 ? dtoDiscuss : dtoNews;
    }

    public void displayDiscussOrNewsDTO()
    {
        AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
        llDisscurssOrNews.setVisibility(dto == null ? View.GONE : View.VISIBLE);
        if (dto != null)
        {
            imgSecurityTLUserHeader.setVisibility(dto instanceof NewsItemCompactDTO ? View.GONE : View.VISIBLE);
            tvUserTLName.setVisibility(dto instanceof NewsItemCompactDTO ? View.GONE : View.VISIBLE);
            tvUserTLTimeStamp.setText(prettyTime.get().formatUnrounded(dto.createdAtUtc));

            if (dto instanceof NewsItemCompactDTO)
            {
                tvUserTLContent.setText(((NewsItemCompactDTO) dto).description);
            }
            else if (dto instanceof DiscussionDTO)
            {
                tvUserTLName.setText(((DiscussionDTO) dto).user.getDisplayName());
                tvUserTLContent.setText(((DiscussionDTO) dto).text);
                ImageLoader.getInstance()
                        .displayImage(((DiscussionDTO) dto).user.picture,
                                imgSecurityTLUserHeader,
                                UniversalImageLoader.getAvatarImageLoaderOptions(false));
            }

            btnTLPraise.setBackgroundResource(dto.voteDirection==1?R.drawable.icon_praise_active:R.drawable.icon_praise_normal);
            btnTLPraiseDown.setBackgroundResource(dto.voteDirection==-1?R.drawable.icon_praise_down_active:R.drawable.icon_praise_down_normal);

            tvTLComment.setText("" + dto.commentCount);
            tvTLPraise.setText(Html.fromHtml(dto.getVoteUpString()));
            tvTLPraiseDown.setText(Html.fromHtml(dto.getVoteDownString()));
        }

        setTextForMoreButton();
    }

    public void clickedPraise()
    {
        AbstractDiscussionCompactDTO item = getAbstractDiscussionCompactDTO();

        if (item.voteDirection == 1) {
            item.voteDirection = 0;
            item.upvoteCount = item.upvoteCount > 0 ? (item.upvoteCount - 1) : 0;
            updateVoting(VoteDirection.UnVote, item);
        }
        else if (item.voteDirection == 0)
        {
            item.voteDirection = 1;
            item.upvoteCount += 1;
            updateVoting(VoteDirection.UpVote, item);
        } else if (item.voteDirection == -1)
        {
            item.voteDirection = 1;
            item.upvoteCount += 1;
            item.downvoteCount = item.downvoteCount > 0 ? (item.downvoteCount - 1) : 0;
            updateVoting(VoteDirection.UpVote, item);
        }

        displayDiscussOrNewsDTO();
        if(item.voteDirection != 0) {
            btnTLPraise.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.vote_praise));
        }
    }

    public void clickedPraiseDown()
    {
        AbstractDiscussionCompactDTO item = getAbstractDiscussionCompactDTO();

        if (item.voteDirection == 1)
        {
            item.voteDirection = -1;
            item.downvoteCount += 1;
            item.upvoteCount = item.upvoteCount > 0 ? (item.upvoteCount - 1) : 0;
            updateVoting(VoteDirection.DownVote, item);
        }
        else if (item.voteDirection == 0)
        {
            item.voteDirection = -1;
            item.downvoteCount += 1;
            updateVoting(VoteDirection.DownVote, item);
        } else if (item.voteDirection == -1)
        {
            item.voteDirection = 0;
            item.downvoteCount = item.downvoteCount > 0 ? (item.downvoteCount - 1) : 0;
            updateVoting(VoteDirection.UnVote, item);
        }
        displayDiscussOrNewsDTO();
        if(item.voteDirection != 0){
            btnTLPraiseDown.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.vote_ani));
        }
    }

    protected void detachVoteMiddleCallback()
    {
        if (voteCallback != null)
        {
            voteCallback.setPrimaryCallback(null);
        }
        voteCallback = null;
    }

    private void updateVoting(VoteDirection voteDirection, AbstractDiscussionCompactDTO discussionDTO)
    {
        if (discussionDTO == null)
        {
            return;
        }
        DiscussionType discussionType = getDiscussionType(discussionDTO);

        DiscussionVoteKey discussionVoteKey = new DiscussionVoteKey(
                discussionType,
                discussionDTO.id,
                voteDirection);
        detachVoteMiddleCallback();
        voteCallback = discussionServiceWrapper.get().vote(discussionVoteKey, new VoteCallback(voteDirection));
    }

    private DiscussionType getDiscussionType(AbstractDiscussionCompactDTO discussionDTO)
    {
        if (discussionDTO != null && discussionDTO.getDiscussionKey() != null)
        {
            return discussionDTO.getDiscussionKey().getType();
        }

        throw new IllegalStateException("Unknown discussion type");
    }

    //TODO why null ?
    protected class VoteCallback implements retrofit.Callback<DiscussionDTO>
    {
        public VoteCallback(VoteDirection voteDirection)
        {
        }

        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
        }

        @Override public void failure(RetrofitError error)
        {
        }
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

    //@OnClick(R.id.tvSecurityDiscussOrNewsMore)
    public void onDiscussOrNewsMore()
    {
        if (getString(R.string.no_useful_data).equals(tvSecurityDiscussOrNewsMore.getText().toString())) return;
        if (securityCompactDTO == null) return;
        if (getAbstractDiscussionCompactDTO() != null)
        {//点击加载更多
            //进入股票相关的更多讨论和资讯中
            Bundle bundle = new Bundle();
            bundle.putInt(SecurityDiscussOrNewsFragment.BUNDLE_KEY_DISCUSS_OR_NEWS_TYPE, indexDiscussOrNews);
            bundle.putBundle(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
            bundle.putString(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
            bundle.putInt(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURIYT_COMPACT_ID, securityCompactDTO.id);
            bundle.putBoolean(SecurityDiscussOrNewsFragment.BUNDLE_ARGUMENT_IS_NEWS, isNews);
            pushFragment(SecurityDiscussOrNewsFragment.class, bundle);

            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_GETMORE));
        }
        else
        {//快来抢沙发
            enterDiscussSend();
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_SAFA));
        }
    }

    public void onChartBtnClicked(View view) {
        if (view.getId() == R.id.btnTabChart0) {
            setChartView(0);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_CHART_ONEDAY));
            return;
        }
        if (view.getId() == R.id.btnTabChart1) {
            setChartView(1);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_CHART_FIVEDAY));
            return;
        }
        if (view.getId() == R.id.btnTabChart2) {
            setChartView(2);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_CHART_90DAY));
            return;
        }
        if (view.getId() == R.id.btnTabChart3) {
            setChartView(3);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_CHART_YEAR));
            return;
        }
        if (view.getId() == R.id.btnTabDiscuss) {
            isNews = false;
            setDiscussOrNewsView(0);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_TAB_DISCUSS));
            return;
        }
        if (view.getId() == R.id.btnTabNews) {
            isNews = true;
            setDiscussOrNewsView(1);
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.BUTTON_STOCK_DETAIL_TAB_NEWS));
        }
        if (view.getId() == R.id.btnTabUserOperation){
            isNews = false;
            setDiscussOrNewsView(2);
            return;
        }
        if (view.getId() == R.id.btnTabUserPosition) {
            isNews = false;
            setDiscussOrNewsView(3);
            return;
        }
    }

    public void onOperaterClicked(View view)
    {
        if (view.getId() == R.id.llDisscurssOrNews)
        {
            enterTimeLineDetail(getAbstractDiscussionCompactDTO());
        }
        else if (view.getId() == R.id.tvUserTLContent)
        {
            if (tvUserTLContent instanceof MarkdownTextView)
            {
                if (!((MarkdownTextView) tvUserTLContent).isClicked)
                {
                    enterTimeLineDetail(getAbstractDiscussionCompactDTO());
                }
                ((MarkdownTextView) view).isClicked = false;
            }
        }
        else if (view.getId() == R.id.imgSecurityTLUserHeader)
        {
            openUserProfile(((DiscussionDTO) getAbstractDiscussionCompactDTO()).user.id);
        }
        else if (view.getId() == R.id.llTLPraise)
        {
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.USER_PAGE_PRAISE));
            clickedPraise();
        }
        else if (view.getId() == R.id.llTLPraiseDown)
        {
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.USER_PAGE_PRAISE_DOWN));
            clickedPraiseDown();
        }
        else if (view.getId() == R.id.llTLComment)
        {
            analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.USER_PAGE_COMMENT));
            enterTimeLineDetail(getAbstractDiscussionCompactDTO());
        }
    }

    @Override public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.llTLComment
                || id == R.id.llTLPraise || id == R.id.llTLPraiseDown
                //|| id == R.id.llTLShare
                || id == R.id.llDisscurssOrNews || id == R.id.imgSecurityTLUserHeader || id == R.id.tvUserTLContent) {
            onOperaterClicked(view);
            return;
        }
        if (id == R.id.btnTabChart0
                || id == R.id.btnTabChart1 || id == R.id.btnTabChart2 || id == R.id.btnTabChart3
                || id == R.id.btnTabDiscuss || id == R.id.btnTabUserOperation || id == R.id.btnTabUserPosition
                || id == R.id.btnTabNews) {
            onChartBtnClicked(view);
            return;
        }
        if (id == R.id.tvSecurityDiscussOrNewsMore) {
            onDiscussOrNewsMore();
            return;
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

    protected class TradeListFragmentPositionCacheListener implements DTOCacheNew.Listener<com.tradehero.th.api.position.PositionDTOKey, PositionDTO>
    {
        @Override public void onDTOReceived(@NotNull com.tradehero.th.api.position.PositionDTOKey key, @NotNull PositionDTO value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull com.tradehero.th.api.position.PositionDTOKey key, @NotNull Throwable error)
        {
            //THToast.show(R.string.error_fetch_position_list_info);
        }
    }

    public void linkWith(PositionDTO positionDTO)
    {
        if (getActivity() == null) return;
        this.positionDTO = positionDTO;
        fetchTrades();
        displayPosition(positionDTO);
    }

    public void displayPosition(PositionDTO positionDTO)
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void fetchTrades()
    {
        if (positionDTO != null)
        {
            detachFetchTrades();
            OwnedPositionId key = positionDTO.getOwnedPositionId();
            tradeListCache.get().register(key, fetchTradesListener);
            tradeListCache.get().getOrFetchAsync(key, true);
        }
        else
        {
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

    public void startTimerForView()
    {
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

    public void closeTimerForView()
    {
        try
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTrade);
            handler.removeCallbacks(runnable);
        } catch (Exception e)
        {
        }
    }

    private void enterUserOptFragment()
    {
        Bundle bundle = new Bundle();
        pushFragment(SecurityUserOptFragment.class, bundle);
    }
}
