package com.tradehero.th.fragments.chinabuild.fragment.security;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.widgets.AspectRatioImageViewCallback;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
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
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOCompactList;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTOList;
import com.tradehero.th.api.watchlist.WatchlistPositionFormDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.chinabuild.cache.PositionCompactNewCache;
import com.tradehero.th.fragments.chinabuild.cache.PositionDTOKey;
import com.tradehero.th.fragments.chinabuild.dialog.DialogFactory;
import com.tradehero.th.fragments.chinabuild.dialog.SecurityDetailDialogLayout;
import com.tradehero.th.fragments.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.th.fragments.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.SecurityDiscussSendFragment;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
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
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.persistence.watchlist.UserWatchlistPositionCache;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-9-1.
 */
public class SecurityDetailFragment extends BasePurchaseManagerFragment implements DiscussionListCacheNew.DiscussionKeyListListener
{
    public final static String BUNDLE_KEY_SECURITY_NAME = SecurityDetailFragment.class.getName() + ".securityName";
    public final static String BUNDLE_KEY_SECURITY_ID_BUNDLE = SecurityDetailFragment.class.getName() + ".securityId";
    public final static String BUNDLE_KEY_PROVIDER_ID_BUNDLE = SecurityDetailFragment.class.getName() + ".providerId";
    public final static String BUNDLE_KEY_COMPETITION_ID_BUNDLE = SecurityDetailFragment.class.getName() + ".competitionID";

    public static final int TYPE_DISCUSS = 0;
    public static final int TYPE_NEWS = 1;

    public final static long MILLISEC_QUOTE_REFRESH = 10000;
    public final static long MILLISEC_QUOTE_COUNTDOWN_PRECISION = 50;


    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    private MiddleCallback<DiscussionDTO> voteCallback;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    @Inject Picasso picasso;
    private Callback chartImageCallback;
    @Inject ChartDTOFactory chartDTOFactory;
    private ChartDTO chartDTO;

    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    private DTOCacheNew.Listener<SecurityId, SecurityCompactDTO> compactCacheListener;

    @Inject Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    protected DTOCacheNew.Listener<SecurityId, SecurityPositionDetailDTO> securityPositionDetailListener;

    @Inject Lazy<PositionCompactNewCache> positionCompactNewCache;
    private DTOCacheNew.Listener<PositionDTOKey, PositionDTOCompact> positionNewCacheListener;

    protected ProviderId providerId;
    protected UserProfileDTO userProfileDTO;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected SecurityPositionDetailDTO securityPositionDetailDTO;
    protected WatchlistPositionDTOList watchedList;
    @Inject UserWatchlistPositionCache userWatchlistPositionCache;
    protected FreshQuoteHolder freshQuoteHolder;
    protected boolean querying = false;
    @Nullable protected QuoteDTO quoteDTO;
    protected boolean refreshingQuote = false;
    protected boolean isTransactionTypeBuy = true;
    protected Integer mBuyQuantity;
    protected Integer mSellQuantity;
    @Inject protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;
    public PositionDTOCompactList positionDTOCompactList;
    protected PortfolioCompactDTO portfolioCompactDTO;
    @Inject PortfolioCompactCache portfolioCompactCache;

    protected DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> userWatchlistPositionCacheFetchListener;
    private ProgressDialog progressBar;
    @Inject ProgressDialogUtil progressDialogUtil;

    private Button[] btnChart;
    @InjectView(R.id.btnTabChart0) Button btnChart0;
    @InjectView(R.id.btnTabChart1) Button btnChart1;
    @InjectView(R.id.btnTabChart2) Button btnChart2;
    @InjectView(R.id.btnTabChart3) Button btnChart3;
    private int indexChart = -1;

    private Button[] btnDiscussOrNews;
    @InjectView(R.id.btnTabDiscuss) Button btnDiscuss;
    @InjectView(R.id.btnTabNews) Button btnNews;
    private int indexDiscussOrNews = -1;

    @InjectView(R.id.chart_image_wrapper) @Optional protected BetterViewAnimator chartImageWrapper;
    @InjectView(R.id.chart_imageView) protected ChartImageView chartImage;
    @InjectView(R.id.chart_loading) protected TextView tvLoadingChart;

    @InjectView(R.id.tvSecurityDetailPrice) TextView tvSecurityPrice;//当前价格
    @InjectView(R.id.tvSecurityDetailRate) TextView tvSecurityDetailRate;//涨跌幅
    @InjectView(R.id.tvSecurityDetailNum) TextView tvSecurityDetailNum;//涨跌值
    @InjectView(R.id.tvInfo0Value) TextView tvInfo0Value;//最高
    @InjectView(R.id.tvInfo1Value) TextView tvInfo1Value;//最低
    @InjectView(R.id.tvInfo2Value) TextView tvInfo2Value;//成交量
    @InjectView(R.id.tvInfo3Value) TextView tvInfo3Value;//平均量

    @InjectView(R.id.ic_info_buy_sale_btns) LinearLayout llBuySaleButtons;//购买卖出栏
    @InjectView(R.id.llSecurityBuy) RelativeLayout llSecurityBuy;//购买
    @InjectView(R.id.llSecuritySale) RelativeLayout llSecuritySale;//出售
    @InjectView(R.id.llSecurityDiscuss) RelativeLayout llSecurityDiscuss;//讨论

    private boolean isAddWatchSheetOpen = false;
    private boolean isInWatchList = false;//是否是自选股
    private String securityName;
    private DialogFactory dialogFactory;
    private Dialog dialog;
    @Nullable private MiddleCallback<WatchlistPositionDTO> middleCallbackUpdate;
    @Nullable private MiddleCallback<WatchlistPositionDTO> middleCallbackDelete;
    @Inject WatchlistServiceWrapper watchlistServiceWrapper;
    @Inject Lazy<WatchlistPositionCache> watchlistPositionCache;

    public int competitionID;

    @Inject DiscussionCache discussionCache;
    @Inject DiscussionListCacheNew discussionListCache;
    private PaginatedDiscussionListKey discussionListKey;
    private NewsItemListKey listKey;
    @Inject NewsItemCompactListCacheNew newsTitleCache;
    @Nullable private DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> newsCacheListener;

    @InjectView(R.id.llDisscurssOrNews) LinearLayout llDisscurssOrNews;
    @InjectView(R.id.imgSecurityTLUserHeader) ImageView imgSecurityTLUserHeader;
    @InjectView(R.id.tvUserTLTimeStamp) TextView tvUserTLTimeStamp;
    @InjectView(R.id.tvUserTLContent) TextView tvUserTLContent;
    @InjectView(R.id.tvUserTLName) TextView tvUserTLName;
    @InjectView(R.id.llTLPraise) LinearLayout llTLPraise;
    @InjectView(R.id.llTLComment) LinearLayout llTLComment;
    @InjectView(R.id.llTLShare) LinearLayout llTLShare;
    @InjectView(R.id.tvTLPraise) TextView tvTLPraise;
    @InjectView(R.id.tvTLComment) TextView tvTLComment;
    @InjectView(R.id.tvTLShare) TextView tvTLShare;

    @Inject public Lazy<PrettyTime> prettyTime;
    AbstractDiscussionCompactDTO dtoDiscuss;
    AbstractDiscussionCompactDTO dtoNews;

    private Dialog mShareSheetDialog;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    public static final int ERROR_NO_ASK_BID = 0;
    public static final int ERROR_NO_ASK = 1;
    public static final int ERROR_NO_BID = 2;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        compactCacheListener = createSecurityCompactCacheListener();
        chartDTO = chartDTOFactory.createChartDTO();
        securityPositionDetailListener = createSecurityPositionCacheListener();
        positionNewCacheListener = createPositionNewCacheListener();
        userProfileCacheListener = createUserProfileCacheListener();
        userWatchlistPositionCacheFetchListener = createUserWatchlistCacheListener();
        newsCacheListener = createNewsCacheListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);

        setHeadViewMiddleMain(securityName);
        setHeadViewMiddleSub(securityId.getDisplayName());
        if (watchedList != null && securityId != null)
        {
            isInWatchList = watchedList.contains(securityId);
            displayInWatchButton();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.security_detail_layout, container, false);
        ButterKnife.inject(this, view);
        initView();
        updateHeadView(true);
        return view;
    }

    @Override public void updateHeadView(boolean display)
    {
        super.updateHeadView(display);
    }

    //+自选股 已添加
    @Override public void onClickHeadRight0()
    {
        Timber.d("isInWatchList = " + isInWatchList);
        if (!isInWatchList)
        {
            addSecurityToWatchList();
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
                        }
                    }
                });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override public void onDismiss(DialogInterface dialogInterface)
            {
                closeWatchSheet();
            }
        });
    }

    public void closeWatchSheet()
    {
        isAddWatchSheetOpen = false;
        displayInWatchButton();
    }

    public void addSecurityToWatchList()
    {
        if (securityId != null && securityCompactDTO != null)
        {
            handleWatchButtonClicked();
        }
    }

    public void setBuySaleButtonVisable(boolean isCanSale)
    {
        llBuySaleButtons.setVisibility(View.VISIBLE);
        llSecurityBuy.setVisibility(View.VISIBLE);
        llSecuritySale.setVisibility(isCanSale ? View.VISIBLE : View.GONE);
    }

    @Override protected void initViews(View view)
    {

    }

    public void initView()
    {
        llBuySaleButtons.setVisibility(View.GONE);

        btnChart = new Button[4];
        btnChart[0] = btnChart0;
        btnChart[1] = btnChart1;
        btnChart[2] = btnChart2;
        btnChart[3] = btnChart3;
        btnDiscussOrNews = new Button[2];
        btnDiscussOrNews[0] = btnDiscuss;
        btnDiscussOrNews[1] = btnNews;
        setDefaultBtnTabView();

        chartDTO.setIncludeVolume(chartImage.includeVolume);
        if (chartImage != null)
        {
            chartImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        chartImageCallback = new AspectRatioImageViewCallback(chartImage)
        {
            @Override public void onSuccess()
            {
                super.onSuccess();
                if (chartImageWrapper != null)
                {
                    chartImageWrapper.setDisplayedChildByLayoutId(chartImage.getId());
                }
            }

            @Override public void onError()
            {
                super.onError();
                Timber.d("Load chartImage error");
            }
        };
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onPause()
    {
        detachSecurityCompactCache();

        super.onPause();
    }

    @Override public void onDestroyView()
    {
        chartImageCallback = null;
        destroyFreshQuoteHolder();
        detachUserProfileCache();
        detachSecurityCompactCache();
        detachSecurityPositionDetailCache();
        detachCompetitionPositionCache();
        detachWatchlistFetchTask();

        detachSecurityDiscuss();
        detachSecurityNews();

        querying = false;
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        Bundle args = getArguments();
        if (args != null)
        {
            //Bundle providerIdBundle = args.getBundle(BUNDLE_KEY_PROVIDER_ID_BUNDLE);
            //if (providerIdBundle != null)
            //{
            //    linkWith(new ProviderId(providerIdBundle), false);
            //}
            Bundle securityIdBundle = args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE);
            securityName = args.getString(BUNDLE_KEY_SECURITY_NAME);
            competitionID = args.getInt(BUNDLE_KEY_COMPETITION_ID_BUNDLE, 0);
            if (securityIdBundle != null)
            {
                linkWith(new SecurityId(securityIdBundle));
            }
        }

        requestUserProfile();
        fetchWatchlist();

        super.onResume();
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
            btnDiscussOrNews[0].performClick();
        }
        else
        {
            setDiscussOrNewsView(indexDiscussOrNews);
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
                SecurityPositionDetailDTO detailDTO = securityPositionDetailCache.get().get(this.securityId);
                if (detailDTO != null)
                {
                    linkWith(detailDTO, true);
                }
                else
                {
                    requestPositionDetail();
                }
            }
            else//是比赛
            {
                requestCompetitionPosition();
            }
        }
    }

    protected void prepareFreshQuoteHolder()
    {
        destroyFreshQuoteHolder();
        freshQuoteHolder = new FreshQuoteHolder(securityId, MILLISEC_QUOTE_REFRESH, MILLISEC_QUOTE_COUNTDOWN_PRECISION);
        freshQuoteHolder.setListener(createFreshQuoteListener());
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

    protected FreshQuoteHolder.FreshQuoteListener createFreshQuoteListener()
    {
        return new BuySellFreshQuoteListener();
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
            linkWith(quoteDTO, true);
        }
    }

    protected void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        this.quoteDTO = quoteDTO;

        setInitialBuySaleQuantityIfCan();
        if (andDisplay)
        {
            // Nothing to do in this class
            Timber.d("QuoteDTO linkWith quoteDTO.ask = " + quoteDTO.ask + "  quoteDTO.bid" + quoteDTO.bid);
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
        //if (this.mBuyQuantity != null && this.mSellQuantity != null)
        if (this.mSellQuantity != null)
        {
            setBuySaleButtonVisable(mSellQuantity > 0);//可以卖出
        }
    }

    protected void setInitialBuyQuantityIfCan()
    {
        //if (mBuyQuantity == null)
        //{
        Integer maxPurchasableShares = getMaxPurchasableShares();
        if (maxPurchasableShares != null)
        {
            linkWithBuyQuantity((int) Math.ceil(((double) maxPurchasableShares) / 2), true);
        }
        //}
    }

    protected void setInitialSellQuantityIfCan()
    {
        //if (mSellQuantity == null)
        //{
        Integer maxSellableShares = getMaxSellableShares();
        if (maxSellableShares != null)
        {
            linkWithSellQuantity(maxSellableShares, true);
            if (maxSellableShares == 0)
            {
                setTransactionTypeBuy(true);
            }
        }
        //}
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

    protected void linkWithBuyQuantity(Integer buyQuantity, boolean andDisplay)
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

    protected void linkWithSellQuantity(Integer sellQuantity, boolean andDisplay)
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
            //if (mQuoteRefreshProgressBar != null)
            //{
            //    mQuoteRefreshProgressBar.setProgress(
            //            (int) (milliSecToRefresh / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
            //}
            //Timber.d("BuySellFreshQuoteListener progress:" + (int) (milliSecToRefresh / MILLISEC_QUOTE_COUNTDOWN_PRECISION));
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
        SecurityCompactDTO securityCompactDTO = securityCompactCache.get().get(securityId);
        if (securityCompactDTO != null)
        {
            linkWith(securityCompactDTO);
        }
        //else
        //{
        detachSecurityCompactCache();
        securityCompactCache.get().register(securityId, compactCacheListener);
        securityCompactCache.get().getOrFetchAsync(securityId, true);
        //}
    }

    protected void detachSecurityCompactCache()
    {
        securityCompactCache.get().unregister(compactCacheListener);
    }

    protected DTOCacheNew.Listener<SecurityId, SecurityCompactDTO> createSecurityCompactCacheListener()
    {
        return new StockInfoFragmentSecurityCompactCacheListener();
    }

    @Override protected void linkWithApplicable(OwnedPortfolioId purchaseApplicablePortfolioId,
            boolean andDisplay)
    {
        super.linkWithApplicable(purchaseApplicablePortfolioId, andDisplay);
        if (purchaseApplicablePortfolioId != null)
        {
            linkWith(portfolioCompactCache.get(purchaseApplicablePortfolioId.getPortfolioIdKey()), andDisplay);
            purchaseApplicableOwnedPortfolioId = purchaseApplicablePortfolioId;
        }
        else
        {
            linkWith((PortfolioCompactDTO) null, andDisplay);
        }
        //if (andDisplay)
        //{
        //    displayBuySellSwitch();
        //    displaySelectedPortfolio();
        //}
    }

    protected void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        clampBuyQuantity(andDisplay);
        clampSellQuantity(andDisplay);
        //if (andDisplay)
        //{
        //    // TODO max purchasable shares
        //    displayBuySellPrice();
        //}
    }

    protected void clampSellQuantity(boolean andDisplay)
    {
        linkWithSellQuantity(mSellQuantity, andDisplay);
    }

    protected void clampBuyQuantity(boolean andDisplay)
    {
        linkWithBuyQuantity(mBuyQuantity, andDisplay);
    }

    protected class StockInfoFragmentSecurityCompactCacheListener implements DTOCacheNew.Listener<SecurityId, SecurityCompactDTO>
    {
        @Override public void onDTOReceived(@NotNull SecurityId key, @NotNull SecurityCompactDTO value)
        {
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull SecurityId key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_security_info);
            Timber.e(error, "Failed to fetch SecurityCompact %s", securityId);
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
    }

    @OnClick(R.id.tvSecurityDiscussOrNewsMore)
    public void onDiscussOrNewsMore()
    {
        Timber.d("更多。。。");
        //进入股票相关的更多讨论和资讯中
        Bundle bundle = new Bundle();
        bundle.putInt(SecurityDiscussOrNewsFragment.BUNDLE_KEY_DISCUSS_OR_NEWS_TYPE, indexDiscussOrNews);
        bundle.putBundle(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
        bundle.putString(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURITY_NAME, securityName);
        bundle.putInt(SecurityDiscussOrNewsFragment.BUNDLE_KEY_SECURIYT_COMPACT_ID, securityCompactDTO.id);
        pushFragment(SecurityDiscussOrNewsFragment.class, bundle);
    }

    @OnClick({R.id.btnTabChart0, R.id.btnTabChart1, R.id.btnTabChart2, R.id.btnTabChart3
            , R.id.btnTabDiscuss, R.id.btnTabNews
    })
    public void onChartBtnClicked(View view)
    {
        if (view.getId() == R.id.btnTabChart0)
        {
            setChartView(0);
        }
        else if (view.getId() == R.id.btnTabChart1)
        {
            setChartView(1);
        }
        else if (view.getId() == R.id.btnTabChart2)
        {
            setChartView(2);
        }
        else if (view.getId() == R.id.btnTabChart3)
        {
            setChartView(3);
        }
        else if (view.getId() == R.id.btnTabDiscuss)
        {
            setDiscussOrNewsView(0);
        }

        else if (view.getId() == R.id.btnTabNews)
        {
            setDiscussOrNewsView(1);
        }
    }

    public void setChartView(int select)
    {
        //if (indexChart != select)
        {
            indexChart = select;
            for (int i = 0; i < btnChart.length; i++)
            {
                btnChart[i].setBackgroundResource((i == indexChart ? R.drawable.tab_blue_head_active : R.drawable.tab_blue_head_normal));
            }
            linkWith(new ChartTimeSpan(getChartTimeSpanDuration(indexChart)), true);
        }
    }

    public void setDiscussOrNewsView(int select)
    {
        indexDiscussOrNews = select;
        for (int i = 0; i < btnDiscussOrNews.length; i++)
        {
            btnDiscussOrNews[i].setBackgroundResource(
                    (i == indexDiscussOrNews ? R.drawable.tab_blue_head_active : R.drawable.tab_blue_head_normal));
        }
        displayDiscussOrNewsDTO();
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

    public void displayChartImage()
    {
        ImageView image = this.chartImage;
        if (!isDetached() && image != null)
        {
            String imageURL = chartDTO.getChartUrl();
            // HACK TODO find something better than skipCache to avoid OutOfMemory
            this.picasso
                    .load(imageURL)
                            //.skipMemoryCache()
                    .into(image, chartImageCallback);
            //if (chartImageWrapper != null)
            //{
            //    chartImageWrapper.setDisplayedChildByLayoutId(tvLoadingChart.getId());
            //}
        }
    }

    public void displayQuoto()
    {
    }

    public void displaySecurityInfo()
    {
        if (securityCompactDTO != null)
        {
            //tvSecurityPrice;//当前价格
            //tvSecurityDetailRate;//涨跌幅
            //tvSecurityDetailNum;//涨跌值
            //tvInfo0Value;//最高
            //tvInfo1Value;//最低
            //tvInfo2Value;//成交量
            //tvInfo3Value;//平均量

            //涨跌幅
            if (securityCompactDTO.risePercent != null)
            {

                THSignedNumber roi = THSignedPercentage.builder(securityCompactDTO.risePercent * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();

                tvSecurityDetailRate.setText(roi.toString());
                tvSecurityDetailRate.setTextColor(getResources().getColor(roi.getColorResId()));

                tvSecurityPrice.setText(String.valueOf(securityCompactDTO.lastPrice));
                tvSecurityPrice.setTextColor(getResources().getColor(roi.getColorResId()));

                tvSecurityDetailNum.setText(securityCompactDTO.getPriceDifferent());

                if (securityCompactDTO.high != null)
                {
                    tvInfo0Value.setText(THSignedMoney.builder(securityCompactDTO.high)
                            .currency(securityCompactDTO.currencyDisplay)
                            .build().toString());
                }

                if (securityCompactDTO.low != null)
                {
                    tvInfo1Value.setText(THSignedMoney.builder(securityCompactDTO.low)
                            .currency(securityCompactDTO.currencyDisplay)
                            .build().toString());
                }

                if (securityCompactDTO.volume != null)
                {
                    tvInfo2Value.setText(THSignedMoney.builder(securityCompactDTO.volume)
                            .currency(securityCompactDTO.currencyDisplay)
                            .build().toString());
                }

                if (securityCompactDTO.averageDailyVolume != null)
                {
                    tvInfo3Value.setText(THSignedMoney.builder(securityCompactDTO.averageDailyVolume)
                            .currency(securityCompactDTO.currencyDisplay)
                            .build().toString());
                }
            }
        }
    }

    public void linkWith(ChartTimeSpan timeSpan, boolean andDisplay)
    {
        chartDTO.setChartTimeSpan(timeSpan);
        if (andDisplay)
        {
            displayChartImage();
        }
    }

    @OnClick({R.id.llSecurityBuy, R.id.llSecuritySale, R.id.llSecurityDiscuss})
    public void onClickItems(View view)
    {
        int id = view.getId();
        if (id == R.id.llSecurityBuy || id == R.id.llSecuritySale)
        {
            //Timber.d("OnClicked buy sale discuss");
            //llSecuritySale.setVisibility(View.VISIBLE);
            enterBuySale(id == R.id.llSecurityBuy);
        }
        else if (id == R.id.llSecurityDiscuss)
        {
            //llSecuritySale.setVisibility(View.GONE);
            enterDiscussSend();
        }
    }

    public boolean isBuyOrSaleValid()
    {
        if (quoteDTO == null) return false;
        if (quoteDTO.ask == null && quoteDTO.bid == null)
        {//ask bid 都没有返回 则说明停牌
            showBuyOrSaleError(ERROR_NO_ASK_BID);
            return false;
        }
        else if (quoteDTO.bid == null)
        {//涨停
            showBuyOrSaleError(ERROR_NO_ASK);
            return false;
        }
        else if (quoteDTO.ask == null)
        {//跌停
            showBuyOrSaleError(ERROR_NO_BID);
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
        else if (type == ERROR_NO_ASK)
        {
            THToast.show("你所购买的股票已跌停");
        }
        else if (type == ERROR_NO_BID)
        {
            THToast.show("你所购买的股票已涨停");
        }
    }

    public void enterBuySale(boolean isBuy)
    {

        if (!isBuyOrSaleValid()) return;
        Bundle bundle = new Bundle();
        //securityId,
        //purchaseApplicableOwnedPortfolioId.getPortfolioIdKey(),
        //quoteDTO,
        //isTransactionTypeBuy
        bundle.putBundle(BuySaleSecurityFragment.KEY_SECURITY_ID, securityId.getArgs());
        bundle.putBundle(BuySaleSecurityFragment.KEY_QUOTE_DTO, quoteDTO.getArgs());
        bundle.putBundle(BuySaleSecurityFragment.KEY_PORTFOLIO_ID, portfolioCompactDTO.getPortfolioId().getArgs());
        bundle.putBoolean(BuySaleSecurityFragment.KEY_BUY_OR_SALE, isBuy);
        bundle.putString(BuySaleSecurityFragment.KEY_SECURITY_NAME, securityName);
        bundle.putInt(BuySaleSecurityFragment.KEY_COMPETITION_ID, competitionID);
        bundle.putSerializable(BuySaleSecurityFragment.KEY_POSITION_COMPACT_DTO, positionDTOCompactList);
        pushFragment(BuySaleSecurityFragment.class, bundle);
    }

    public void enterDiscussSend()
    {
        Bundle bundle = new Bundle();
        //bundle.putBundle(SecurityDetail.BUNDLE_KEY_SECURITY_ID_BUNDLE,securityId.getArgs());
        //gotoDashboard(DiscussSendFragment.class.getName(), bundle);
        //getDashboardNavigator().pushFragment();
        bundle.putBundle(SecurityDiscussSendFragment.BUNDLE_KEY_SECURITY_ID, securityId.getArgs());
        pushFragment(SecurityDiscussSendFragment.class, bundle);
    }

    protected DTOCacheNew.Listener<PositionDTOKey, PositionDTOCompact> createPositionNewCacheListener()
    {
        return new PositionNewCacheListener();
    }

    protected class PositionNewCacheListener implements DTOCacheNew.Listener<PositionDTOKey, PositionDTOCompact>
    {
        @Override public void onDTOReceived(@NotNull final PositionDTOKey key, @NotNull final PositionDTOCompact value)
        {
            //linkWith(value, true);
            linkWith(value);
        }

        @Override public void onErrorThrown(@NotNull PositionDTOKey key, @NotNull Throwable error)
        {
            //THToast.show(R.string.error_fetch_detailed_security_info);
            Timber.e("PositionNewCacheListener", key, error);
        }
    }

    protected void linkWith(PositionDTOCompact value)
    {

        if (value != null)
        {
            PositionDTOCompactList positionDTOCompacts = new PositionDTOCompactList();
            positionDTOCompacts.add(value);
            linkWith(positionDTOCompacts, true);
        }
    }

    protected DTOCacheNew.Listener<SecurityId, SecurityPositionDetailDTO> createSecurityPositionCacheListener()
    {
        return new AbstractBuySellSecurityPositionCacheListener();
    }

    protected class AbstractBuySellSecurityPositionCacheListener implements DTOCacheNew.Listener<SecurityId, SecurityPositionDetailDTO>
    {
        @Override public void onDTOReceived(@NotNull final SecurityId key, @NotNull final SecurityPositionDetailDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull SecurityId key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_detailed_security_info);
            Timber.e("Error fetching the security position detail %s", key, error);
        }
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new AbstractBuySellUserProfileCacheListener();
    }

    protected class AbstractBuySellUserProfileCacheListener implements DTOCacheNew.HurriedListener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onPreCachedDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onDTOReceived(@NotNull final UserBaseKey key, @NotNull final UserProfileDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
            Timber.e("Error fetching the user profile %s", key, error);
        }
    }

    private void linkWith(SecurityPositionDetailDTO detailDTO, boolean andDisplay)
    {
        Timber.d("");
        this.securityPositionDetailDTO = detailDTO;
        if (securityPositionDetailDTO != null)
        {
            linkWith(securityPositionDetailDTO.security, andDisplay);
            linkWith(securityPositionDetailDTO.positions, andDisplay);
        }
        else
        {
            linkWith((SecurityCompactDTO) null, andDisplay);
            linkWith((PositionDTOCompactList) null, andDisplay);
        }

        if (andDisplay)
        {
            // Nothing to do in this class
        }
    }

    public void linkWith(final PositionDTOCompactList positionDTOCompacts, boolean andDisplay)
    {
        this.positionDTOCompactList = positionDTOCompacts;
        if (andDisplay)
        {
            setInitialBuySaleQuantityIfCan();
        }
    }

    public void linkWith(final SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        if (!securityCompactDTO.getSecurityId().equals(this.securityId))
        {
            throw new IllegalArgumentException("This security compact is not for " + this.securityId);
        }
        this.securityCompactDTO = securityCompactDTO;
        if (andDisplay)
        {
            //displayMarketClose();

        }
    }

    public void linkWith(final UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        setInitialBuySaleQuantityIfCan();
        if (andDisplay)
        {
            // Nothing to do really in this class
        }
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

    protected DTOCacheNew.Listener<UserBaseKey, WatchlistPositionDTOList> createUserWatchlistCacheListener()
    {
        return new BuySellUserWatchlistCacheListener();
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
            Timber.e("Failed to fetch list of watch list items", error);
            THToast.show(R.string.error_fetch_portfolio_list_info);
        }
    }

    protected void linkWithWatchlist(WatchlistPositionDTOList watchedList, boolean andDisplay)
    {
        this.watchedList = watchedList;
        if (andDisplay)
        {
            //displayWatchlistButton();
            Timber.d("显示 添加或者删除 自选股 相关");
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
            middleCallbackUpdate = watchlistServiceWrapper.createWatchlistEntry(
                    watchPositionItemForm,
                    createWatchlistUpdateCallback());
        } catch (NumberFormatException ex)
        {
            THToast.show(getString(R.string.wrong_number_format));
            Timber.e("Parsing error", ex);
            dismissProgress();
        } catch (Exception ex)
        {
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
                    watchlistServiceWrapper.deleteWatchlist(watchlistPositionDTO.getPositionCompactId(), createWatchlistDeleteCallback());
        }
        else
        {
            THToast.show(R.string.error_fetch_portfolio_watchlist);
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

    @NotNull protected retrofit.Callback<WatchlistPositionDTO> createWatchlistUpdateCallback()
    {
        return new WatchlistEditTHCallback();
    }

    @NotNull protected retrofit.Callback<WatchlistPositionDTO> createWatchlistDeleteCallback()
    {
        return new WatchlistDeletedTHCallback();
    }

    protected class WatchlistEditTHCallback extends THCallback<WatchlistPositionDTO>
    {
        @Override protected void finish()
        {
            dismissProgress();
        }

        @Override protected void success(@NotNull WatchlistPositionDTO watchlistPositionDTO, THResponse response)
        {
            //DashboardNavigator navigator = getDashboardNavigator();
            //if (navigator != null)
            //{
            //    navigator.popFragment();
            //}
            dismissProgress();
            THToast.show("添加自选成功");
            isInWatchList = true;
            closeWatchSheet();
            displayInWatchButton();
        }

        @Override protected void failure(THException ex)
        {
            Timber.e(ex, "Failed to update watchlist position");
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

    protected void detachMiddleCallbackUpdate()
    {
        if (middleCallbackUpdate != null)
        {
            middleCallbackUpdate.setPrimaryCallback(null);
        }
        middleCallbackUpdate = null;
    }

    protected void detachMiddleCallbackDelete()
    {
        if (middleCallbackDelete != null)
        {
            middleCallbackDelete.setPrimaryCallback(null);
        }
        middleCallbackDelete = null;
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
                Timber.d(dto.toString());
                dtoDiscuss = dto;
                displayDiscussOrNewsDTO();
            }
        }
    }

    @Override public void onErrorThrown(@NotNull DiscussionListKey key, @NotNull Throwable error)
    {

    }

    @NotNull protected DTOCacheNew.Listener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>> createNewsCacheListener()
    {
        return new NewsHeadlineNewsListListener();
    }

    protected class NewsHeadlineNewsListListener implements DTOCacheNew.HurriedListener<NewsItemListKey, PaginatedDTO<NewsItemCompactDTO>>
    {
        @Override public void onPreCachedDTOReceived(
                @NotNull NewsItemListKey key,
                @NotNull PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(key, value);
            finish();
        }

        @Override public void onDTOReceived(
                @NotNull NewsItemListKey key,
                @NotNull PaginatedDTO<NewsItemCompactDTO> value)
        {
            linkWith(key, value);
            finish();
        }

        @Override public void onErrorThrown(
                @NotNull NewsItemListKey key,
                @NotNull Throwable error)
        {
            //THToast.show("");
            finish();
        }

        public void finish()
        {
            //endLoading();
        }
    }

    public void linkWith(@NotNull NewsItemListKey key,
            @NotNull PaginatedDTO<NewsItemCompactDTO> value)
    {
        if (value.getData() != null && value.getData().size() > 0)
        {
            NewsItemCompactDTO dto = value.getData().get(0);
            dtoNews = dto;
            displayDiscussOrNewsDTO();
        }
    }

    @OnClick({R.id.llTLComment, R.id.llTLPraise, R.id.llTLShare, R.id.llDisscurssOrNews, R.id.imgSecurityTLUserHeader})
    public void onOperaterClicked(View view)
    {
        if (view.getId() == R.id.imgSecurityTLUserHeader)
        {
            openUserProfile(((DiscussionDTO) getAbstractDiscussionCompactDTO()).user.id);
        }
        else if(view.getId() == R.id.llTLPraise)
        {
            clickedPraise();
        }
        else if (view.getId() == R.id.llTLComment)
        {
            AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
            comments(dto);
        }
        else if (view.getId() == R.id.llTLShare)
        {
            AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
            String strShare = "";
            if (dto instanceof NewsItemCompactDTO)
            {
                strShare = (((NewsItemCompactDTO) dto).description);
            }
            else if (dto instanceof DiscussionDTO)
            {
                strShare = (((DiscussionDTO) dto).text);
            }
            share(strShare);
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

    public void share(String strShare)
    {
        mShareSheetTitleCache.set(strShare);
        ShareSheetDialogLayout contentView = (ShareSheetDialogLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.share_sheet_dialog_layout, null);
        contentView.setLocalSocialClickedListener(
                new ShareSheetDialogLayout.OnLocalSocialClickedListener()
                {
                    @Override public void onShareRequestedClicked()
                    {

                    }
                });
        mShareSheetDialog = THDialog.showUpDialog(getActivity(), contentView);
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
                tvUserTLName.setText(((DiscussionDTO) dto).user.displayName);
                tvUserTLContent.setText(((DiscussionDTO) dto).text);
                picasso.load(((DiscussionDTO) dto).user.picture)
                        .placeholder(R.drawable.superman_facebook)
                        .error(R.drawable.superman_facebook)
                        .into(imgSecurityTLUserHeader);
            }

            tvTLComment.setText("" + dto.commentCount);
            tvTLPraise.setText(dto.getVoteString());
        }
    }

    public void clickedPraise()
    {
        AbstractDiscussionCompactDTO item = getAbstractDiscussionCompactDTO();
        updateVoting((item.voteDirection == 0) ? VoteDirection.UpVote : VoteDirection.UnVote, item);

        if (item.voteDirection == 0)
        {
            item.voteDirection = 1;
            item.upvoteCount += 1;
        }
        else
        {
            item.voteDirection = 0;
            item.upvoteCount = item.upvoteCount > 0 ? (item.upvoteCount - 1) : 0;
        }
        displayDiscussOrNewsDTO();

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

    protected class VoteCallback implements retrofit.Callback<DiscussionDTO>
    {
        //<editor-fold desc="Constructors">
        public VoteCallback(VoteDirection voteDirection)
        {
        }
        //</editor-fold>

        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            Timber.d("VoteCallback success");
        }

        @Override public void failure(RetrofitError error)
        {
            Timber.d("VoteCallback failed :" + error.toString());
        }
    }
}
