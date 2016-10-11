package com.androidth.general.fragments.trade;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.util.Log;
import android.util.Pair;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.util.Predicate;
import com.androidth.general.R;
import com.androidth.general.activities.SignUpLiveActivity;
import com.androidth.general.api.competition.ProviderDTO;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.api.portfolio.OwnedPortfolioIdList;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.androidth.general.api.portfolio.PortfolioCompactDTOList;
import com.androidth.general.api.portfolio.PortfolioCompactDTOUtil;
import com.androidth.general.api.portfolio.PortfolioDTO;
import com.androidth.general.api.portfolio.PortfolioId;
import com.androidth.general.api.portfolio.key.PortfolioCompactListKey;
import com.androidth.general.api.position.PositionDTO;
import com.androidth.general.api.position.PositionDTOCompact;
import com.androidth.general.api.position.PositionDTOList;
import com.androidth.general.api.position.SecurityPositionTransactionDTO;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.TransactionFormDTO;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.exception.THException;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.base.BaseShareableDialogFragment;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.fragments.discussion.SecurityDiscussionEditPostFragment;
import com.androidth.general.fragments.discussion.TransactionEditCommentFragment;
import com.androidth.general.fragments.live.LiveViewFragment;
import com.androidth.general.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.androidth.general.fragments.position.TabbedPositionListFragment;
import com.androidth.general.fragments.security.LiveQuoteDTO;
import com.androidth.general.fragments.social.ShareDelegateFragment;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.models.portfolio.MenuOwnedPortfolioId;
import com.androidth.general.network.service.Live1BServiceWrapper;
import com.androidth.general.network.service.QuoteServiceWrapper;
import com.androidth.general.network.service.SecurityServiceWrapper;
import com.androidth.general.network.share.SocialSharer;
import com.androidth.general.persistence.competition.ProviderCacheRx;
import com.androidth.general.persistence.portfolio.OwnedPortfolioIdListCacheRx;
import com.androidth.general.persistence.portfolio.PortfolioCompactListCacheRx;
import com.androidth.general.persistence.position.PositionListCacheRx;
import com.androidth.general.persistence.security.SecurityCompactCacheRx;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.utils.DeviceUtil;
import com.androidth.general.utils.GraphicUtil;
import com.androidth.general.utils.LiveConstants;
import com.androidth.general.utils.StringUtils;
import com.androidth.general.utils.broadcast.GAnalyticsProvider;
import com.androidth.general.utils.metrics.AnalyticsConstants;
import com.androidth.general.utils.metrics.events.SharingOptionsEvent;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import dagger.Lazy;
import retrofit.RetrofitError;
import retrofit.mime.TypedByteArray;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func4;
import rx.functions.Func6;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

abstract public class AbstractBuySellPopupDialogFragment extends BaseShareableDialogFragment {
    private static final String KEY_REQUISITE = AbstractBuySellPopupDialogFragment.class.getName() + ".requisite";
    private static final double INITIAL_VALUE = 5000;

    @Bind(R.id.dialog_buy_sell_top_bar)
    ViewGroup topBarView;

    @Bind(R.id.dialog_buy_sell_top_image)
    protected ImageView topBarImageView;

    @Bind(R.id.dialog_buy_sell_security_logo)
    protected ImageView securityLogo;

    @Bind(R.id.dialog_buy_sell_security_name)
    protected TextView securityName;

    @Bind(R.id.dialog_buy_sell_security_symbol)
    protected TextView securitySymbol;

    @Bind(R.id.dialog_buy_sell_quantity)
    protected TextView buySellQuantity;

    @Bind(R.id.vtrade_value)
    protected TextView mLeftNumber;

    @Bind(R.id.vquantity)
    protected EditText mMiddleNumber;

    @Bind(R.id.vcash_left)
    protected TextView mRightNumber;

    @Bind(R.id.dialog_trade_value_label) protected TextView mTradeValue;

    @Bind(R.id.seek_bar) protected SeekBar mSeekBar;

    @Bind(R.id.dialog_buy_sell_top_text_1) protected TextView topText1;
    @Bind(R.id.dialog_buy_sell_top_text_2) protected TextView topText2;

    @Bind(R.id.vmarket_symbol)
    protected TextView mMarketPriceSymbol;

    @Bind(R.id.dialog_buy_sell_top_competition_view) ViewGroup topCompetitionView;
    @Bind(R.id.dialog_buy_sell_top_normal_view) ViewGroup topNormalView;

//    @Bind(R.id.price_updated_time)
//    protected TextView mPriceUpdatedTime;
//    @Bind(R.id.vtrade_symbol)
//    protected TextView mTradeSymbol;

    @Bind(R.id.dialog_currency_label_cash_value)
    protected TextView mSymbolCashValue;

    @Bind(R.id.dialog_currency_label_cash_left)
    protected TextView mSymbolCashLeft;

    @Bind(R.id.market_price)
    protected TextView mStockPriceTextView;

    @Bind(R.id.dialog_cash_left)
    protected TextView mCashOrStocksLeftLabel;

    @Bind(R.id.comments)
    protected TextView mCommentsEditText;
    @Bind(R.id.dialog_btn_confirm)
    protected Button mConfirm;
//    @Bind(R.id.portfolio_spinner)
//    protected Spinner mPortfolioSpinner;
//    @Bind(R.id.cash_or_stock_left)
//    protected TextView mCashOrStockLeft;

    @Inject
    SecurityCompactCacheRx securityCompactCache;
    @Inject
    PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject
    SecurityServiceWrapper securityServiceWrapper;
    @Inject
    Live1BServiceWrapper live1BServiceWrapper;
    @Inject
    PositionListCacheRx positionCompactListCache;
    @Inject
    ProviderCacheRx providerCacheRx;
    //TODO Change Analytics
    //@Inject Analytics analytics;
    @Inject
    QuoteServiceWrapper quoteServiceWrapper;
    @Inject
    Lazy<DashboardNavigator> navigator;
    @Inject
    CurrentUserId currentUserId;
    @Inject
    protected OwnedPortfolioIdListCacheRx ownedPortfolioIdListCache;
    @Inject
    protected Lazy<SocialSharer> socialSharerLazy;

//    @Bind(R.id.close_button)
//    protected ImageView closeButton;

    @Inject
    Picasso picasso;

    @NonNull
    protected final UsedDTO usedDTO;
    @NonNull
    private final BehaviorSubject<Integer> quantitySubject; // It can pass null values

    @Nullable
    protected OwnedPortfolioIdList applicableOwnedPortfolioIds;

    protected Subscription buySellSubscription;
    protected Requisite requisite;
    protected BuySellTransactionListener buySellTransactionListener;

    private String mPriceSelectionMethod = AnalyticsConstants.DefaultPriceSelectionMethod;
    private TransactionEditCommentFragment transactionCommentFragment;
    private List<MenuOwnedPortfolioId> menuOwnedPortfolioIdList;
    private ShareDelegateFragment shareDelegateFragment;
    private Boolean hasTradeValueTextFieldFocus;
    private boolean isFx;
    Editable unSpannedComment;

    String topBarColor;
    private boolean isInCompetition;
    private boolean isBuy;

    @Nullable
    protected abstract Integer getMaxValue(@NonNull PortfolioCompactDTO portfolioCompactDTO,
                                           @NonNull LiveQuoteDTO quoteDTO,
                                           @Nullable PositionDTOCompact closeablePosition);

    protected abstract boolean hasValidInfo();

    abstract protected Subscription getTransactionSubscription(TransactionFormDTO transactionFormDTO);

    @Nullable
    public abstract Double getPriceCcy(@Nullable PortfolioCompactDTO portfolioCompactDTO, @Nullable LiveQuoteDTO quoteDTO);

    public static boolean canShowTransactionScreen(@NonNull LiveQuoteDTO quoteDTO, boolean isBuy) {
        return (isBuy && quoteDTO.getAskPrice() != null) ||
                (!isBuy && quoteDTO.getBidPrice() != null);
    }

    protected AbstractBuySellPopupDialogFragment() {
        super();
        this.usedDTO = new UsedDTO();
        this.quantitySubject = BehaviorSubject.create();
        this.shareDelegateFragment = new ShareDelegateFragment(this);
        this.hasTradeValueTextFieldFocus = false;
    }

    //<editor-fold desc="Arguments passing">
    public static void putRequisite(@NonNull Bundle args, @NonNull Requisite requisite) {
        args.putBundle(KEY_REQUISITE, requisite.getArgs());
    }

    @NonNull
    private static Requisite getRequisite(@NonNull Bundle args) {
        Bundle requisiteArgs = args.getBundle(KEY_REQUISITE);
        if (requisiteArgs != null) {
            return new Requisite(requisiteArgs);
        } else {
            throw new NullPointerException("Requisite cannot be null");
        }
    }
    //</editor-fold>

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        requisite = getRequisite(getArguments());
        usedDTO.quoteDTO = requisite.quoteDTO;
        quantitySubject.onNext(requisite.quantity);
        shareDelegateFragment.onCreate(savedInstanceState);
        if(getArguments().containsKey(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR)){
            topBarColor = getArguments().getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR);
            Log.v(getTag(), "HEX_COLOR="+topBarColor);
            isInCompetition = true;
        }else{
            Log.v(getTag(), "HEX_COLOR nothing");
            isInCompetition = false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_buy_sell_security, container, false);
        return inflater.inflate(R.layout.security_buy_sell_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        //is in competition
        if(topBarColor!=null){
            topBarView.setBackgroundColor(GraphicUtil.parseColor(topBarColor));
            topCompetitionView.setVisibility(View.VISIBLE);
            topNormalView.setVisibility(View.GONE);
        }else{
            topCompetitionView.setVisibility(View.GONE);
            topNormalView.setVisibility(View.VISIBLE);
        }

        mMiddleNumber.setCustomSelectionActionModeCallback(createActionModeCallBackForQuantityEditText());
        mMiddleNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if(isInCompetition){
                    if(isBuy){
                        GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_BUY_INPUT);
                    }else{
                        GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_SELL_INPUT);
                    }
                }
                if(usedDTO.securityCompactDTO!=null
                        && usedDTO.securityCompactDTO.lotSize!=null){
                    if(Integer.parseInt(textView.getText().toString()) % usedDTO.securityCompactDTO.lotSize != 0){
                        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                        dialog.setMessage("Quantity must be a multiple of "+usedDTO.securityCompactDTO.lotSize);
                        dialog.setNeutralButton("Got it", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialog.show();

                        return true;
                    }
                }
                return false;
            }
        });

        mLeftNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hasTradeValueTextFieldFocus = hasFocus;
            }
        });

        if (this.getClass() == SellStockFragment.class || this.getClass() == SellFXFragment.class) {
            mConfirm.setText(R.string.buy_sell_confirm_sell_now);
            isBuy = false;
        }else if (this.getClass() == BuyStockFragment.class || this.getClass() == BuyFXFragment.class) {
            mConfirm.setText(R.string.buy_sell_confirm_buy_now);
            isBuy = true;
        }//else, it's "Confirm"

//        mCashOrStockLeft.setText(getCashShareLabel());

        shareDelegateFragment.onViewCreated(view, savedInstanceState);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if(usedDTO.securityCompactDTO!=null
                        && usedDTO.securityCompactDTO.lotSize!=null){

                    int newValue = (progress/usedDTO.securityCompactDTO.lotSize) * 100;
                    progress = newValue;
                }
                if (fromUser)
                {
                    quantitySubject.onNext(progress);
                    mPriceSelectionMethod = AnalyticsConstants.Slider;

                }
                if(isInCompetition){
                    if(isBuy){
                        GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_BUY_MOVE_SLIDER);
                    }else{
                        GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_SELL_MOVE_SLIDER);
                    }

                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            @Override public void onStopTrackingTouch(SeekBar seekBar)
            {
            }
        });

        if (requisite.quantity != null && requisite.quantity > 0)
        {
            mSeekBar.setMax(requisite.quantity);
            mSeekBar.setEnabled(requisite.quantity > 0);
            mSeekBar.setProgress(requisite.quantity);
        }

        onStopSubscriptions.add(
                Observable.combineLatest(
                        getPortfolioCompactObservable(),
                        getQuoteObservable(),
                        getCloseablePositionObservable(),
                        WidgetObservable.text(mLeftNumber),
                        new Func4<PortfolioCompactDTO, LiveQuoteDTO, PositionDTO, OnTextChangeEvent, Integer>() {
                            @Override
                            public Integer call(PortfolioCompactDTO portfolioCompactDTO, LiveQuoteDTO quoteDTO, PositionDTO positionDTO,
                                                OnTextChangeEvent onTextChangeEvent) {
                                Integer quantity = 0;
                                Double tradeValue = 0.0;

                                if (hasTradeValueTextFieldFocus) {
                                    String tradeValueText = mLeftNumber.getText().toString().replace(",", "");

                                    if (tradeValueText.isEmpty()) {
                                        return 0;
                                    }

                                    try {
                                        tradeValue = Double.parseDouble(tradeValueText);
                                    } catch (NumberFormatException e) {
                                        Timber.d("Trade value is not a number.");
                                    }

                                    quantity = getQuantityFromTradeValue(portfolioCompactDTO, quoteDTO, tradeValue);

                                    // selling current open position (sell)
                                    if (positionDTO != null) {
                                        String quantityText = mMiddleNumber.getText().toString();
                                        Integer quantityValue = Integer.parseInt(quantityText);

                                        if (quantityValue.equals(getMaxSellableShares(portfolioCompactDTO, quoteDTO, positionDTO))) {
                                            String calculateTradeValueText =
                                                    getTradeValueText(portfolioCompactDTO, quoteDTO, quantityValue).replace(",", "");

                                            if (!calculateTradeValueText.equals(tradeValueText)) {
                                                mLeftNumber.setText(calculateTradeValueText);
                                            }

                                            return quantityValue;
                                        }
                                    }
                                    // open new position (buy)
                                    else {
                                        double maxTradeValue = portfolioCompactDTO.cashBalance;

                                        if (portfolioCompactDTO.providerId != null) {

                                            ProviderDTO providerDTO = providerCacheRx.getCachedValue(new ProviderId(portfolioCompactDTO.providerId));

                                            if (providerDTO.maxLimitPerTrade != null && providerDTO.maxLimitPerTrade < maxTradeValue) {
                                                maxTradeValue = providerDTO.maxLimitPerTrade;
                                            }
                                        }

                                        if (tradeValue - 1 > maxTradeValue) {
                                            tradeValueText = THSignedNumber.builder(maxTradeValue)
                                                    .relevantDigitCount(1)
                                                    .withOutSign()
                                                    .build().toString();

                                            mLeftNumber.setText(tradeValueText);
                                        }
                                    }
                                }

                                setupCompetitionDisplay(portfolioCompactDTO.providerId);

                                return quantity;
                            }
                        })
                        .filter(new Func1<Integer, Boolean>() {
                            @Override
                            public Boolean call(Integer integer) {
                                return hasTradeValueTextFieldFocus;
                            }
                        })
                        .doOnNext(new Action1<Integer>() {
                            @Override
                            public void call(Integer integer) {
                                mMiddleNumber.setText(integer.toString());
                                buySellQuantity.setText(integer.toString());
                            }
                        })
                        .doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if(throwable!=null){
                                    new TimberOnErrorAction1(throwable.getMessage());
                                }else{
                                    new TimberOnErrorAction1("Abstract buy sell popup error");
                                }
                            }
                        })
                        .subscribe()
        );
    }

    private void setupCompetitionDisplay(Integer providerId) {

        if(providerId!=null){
            topText1.setVisibility(View.GONE);
            topText2.setVisibility(View.GONE);

            ProviderDTO providerDTO = providerCacheRx.getCachedValue(new ProviderId(providerId));
            picasso.load(providerDTO.navigationLogoUrl).into(topBarImageView);

        }else{
            topBarImageView.setVisibility(View.GONE);
        }
    }
//
//    protected abstract int getCashShareLabel();
//
//    protected abstract Boolean isBuyTransaction();

    @Override
    public void onStart() {
        super.onStart();

        disableUI();
        initFetches();

//        onStopSubscriptions.add(
//                Observable.zip(
//                        requisite.getPortfolioIdObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
//                        getPortfolioCompactListObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
//                        getApplicablePortfolioIdsObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
//                        getAllCloseablePositionObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
//                        new Func4<PortfolioId, PortfolioCompactDTOList, OwnedPortfolioIdList, List<OwnedPortfolioId>, Boolean>() {
//                            @Override
//                            public Boolean call(
//                                    @NonNull PortfolioId selectedPortfolioId,
//                                    @NonNull PortfolioCompactDTOList portfolioCompactDTOs,
//                                    @NonNull OwnedPortfolioIdList ownedPortfolioIds,
//                                    @Nullable List<OwnedPortfolioId> closeableOwnedPortfolioIds) {
//                                int selectedPortfolioPosition = 0;
//
//                                menuOwnedPortfolioIdList = new ArrayList<>();
//                                for (PortfolioCompactDTO candidate : portfolioCompactDTOs) {
//                                    if (ownedPortfolioIds.contains(candidate.getOwnedPortfolioId())) {
//                                        if (isBuyTransaction() || closeableOwnedPortfolioIds.contains(candidate.getOwnedPortfolioId())) {
//                                            MenuOwnedPortfolioId menuOwnedPortfolioId = new MenuOwnedPortfolioId(candidate.getUserBaseKey(), candidate);
//
//                                            if (menuOwnedPortfolioId.title != null) {
//                                                if (menuOwnedPortfolioId.title.equals(getString(R.string.my_stocks_con))) {
//                                                    menuOwnedPortfolioId.title = getString(R.string.trending_tab_stocks_main);
//                                                } else if (menuOwnedPortfolioId.title.equals(getString(R.string.my_fx_con))) {
//                                                    menuOwnedPortfolioId.title = getString(R.string.my_fx);
//                                                }
//                                            }
//
//                                            menuOwnedPortfolioIdList.add(menuOwnedPortfolioId);
//                                        }
//
//                                        if (candidate.getPortfolioId().key.equals(selectedPortfolioId.key)) {
//                                            selectedPortfolioPosition = menuOwnedPortfolioIdList.size() - 1;
//                                        }
//                                    }
//                                }
//
//                                LollipopArrayAdapter<MenuOwnedPortfolioId> menuOwnedPortfolioIdLollipopArrayAdapter =
//                                        new LollipopArrayAdapter<>(
//                                                getActivity(),
//                                                menuOwnedPortfolioIdList);
//                                mPortfolioSpinner.setAdapter(menuOwnedPortfolioIdLollipopArrayAdapter);
//                                mPortfolioSpinner.setSelection(selectedPortfolioPosition);
//                                mPortfolioSpinner.setEnabled(menuOwnedPortfolioIdList.size() > 1);
//
//                                return null;
//                            }
//                        })
//                        .subscribe(
//                                new Action1<Boolean>() {
//                                    @Override
//                                    public void call(Boolean aBoolean) {
//                                        // Nothing to do
//                                    }
//                                },
//                                new TimberOnErrorAction1("Failed to update portfolio selector")));
//
//        onStopSubscriptions.add(AdapterViewObservable.selects(mPortfolioSpinner)
//                .map(new Func1<OnSelectedEvent, PortfolioId>() {
//                    @Override
//                    public PortfolioId call(OnSelectedEvent onSelectedEvent) {
//                        Integer i = (int) onSelectedEvent.parent.getSelectedItemId();
//
//                        return new PortfolioId(menuOwnedPortfolioIdList.get(i).portfolioId);
//                    }
//                })
//                .subscribe(new Action1<PortfolioId>() {
//                    @Override
//                    public void call(PortfolioId portfolioId) {
//                        requisite.portfolioIdSubject.onNext(portfolioId);
//                    }
//                }, new TimberOnErrorAction1("Failed on listening Spinner select event."))
//        );
    }

    @Override
    public void onDetach() {
        transactionCommentFragment = null;
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        unsubscribe(buySellSubscription);
        buySellSubscription = null;
        ButterKnife.unbind(this);
        shareDelegateFragment.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shareDelegateFragment.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        populateComment();

        /** To make sure that the dialog will not show when active dashboard fragment is not BuySellFragment */
        if (!(navigator.get().getCurrentFragment() instanceof AbstractBuySellFragment))
        {
            getDialog().hide();
        }

        //make the dialog smaller
//        int currentWidth = getResources().getDisplayMetrics().widthPixels;
//        int currentHeight = getResources().getDisplayMetrics().heightPixels;

//        getDialog().getWindow().setLayout(currentWidth, Math.round(currentHeight*8/10));
    }

    @NonNull
    protected Observable<PortfolioCompactDTOList> getPortfolioCompactListObservable() {
        return portfolioCompactListCache.get(currentUserId.toUserBaseKey())
                .map(new PairGetSecond<UserBaseKey, PortfolioCompactDTOList>())
                .share();
    }

    @NonNull
    protected Observable<OwnedPortfolioIdList> getApplicablePortfolioIdsObservable() {
        return ownedPortfolioIdListCache.get(requisite.securityId)
                .distinctUntilChanged(
                        new Func1<Pair<PortfolioCompactListKey, OwnedPortfolioIdList>, String>() {
                            @Override
                            public String call(Pair<PortfolioCompactListKey, OwnedPortfolioIdList> pair) {
                                String code = "first=" + pair.first + ", second=";
                                for (OwnedPortfolioId portfolioId : pair.second) {
                                    code += portfolioId.toString() + ",";
                                }
                                return code;
                            }
                        })
                .map(new Func1<Pair<PortfolioCompactListKey, OwnedPortfolioIdList>, OwnedPortfolioIdList>() {
                    @Override
                    public OwnedPortfolioIdList call(Pair<PortfolioCompactListKey, OwnedPortfolioIdList> pair) {
                        applicableOwnedPortfolioIds = pair.second;
                        return pair.second;
                    }
                })
                .share();
    }

    //<editor-fold desc="Fetches">
    private void initFetches() {
        onStopSubscriptions.add(
                Observable.combineLatest(
                        getSecurityObservable(),
                        getPortfolioCompactObservable(),
                        getQuoteObservable(),
                        getCloseablePositionObservable(),
                        getMaxValueObservable(),
                        getClampedQuantityObservable(),
                        new Func6<SecurityCompactDTO,
                                PortfolioCompactDTO,
                                LiveQuoteDTO,
                                PositionDTO,
                                Integer,
                                Integer,
                                Boolean>() {
                            @Override
                            public Boolean call(
                                    @NonNull SecurityCompactDTO securityCompactDTO,
                                    @NonNull final PortfolioCompactDTO portfolioCompactDTO,
                                    @NonNull LiveQuoteDTO quoteDTO,
                                    @Nullable PositionDTO closeablePosition,
                                    @Nullable Integer maxValue,
                                    @Nullable Integer clamped) {

                                initPortfolioRelatedInfo(portfolioCompactDTO, quoteDTO, closeablePosition, clamped);

                                updateDisplay();

                                mLeftNumber.setText(getTradeValueText(portfolioCompactDTO, quoteDTO, clamped));
                                if (clamped != null)
                                {
                                    mRightNumber.setText(getCashShareLeft(portfolioCompactDTO, quoteDTO, closeablePosition, clamped));
                                }

                                if(securityCompactDTO!=null && securityCompactDTO instanceof FxSecurityCompactDTO){
                                    isFx = true;
//                                    mTradeValue.setText(getString(R.string.buy_sell_fx_quantity));
//                                    mSymbolCashValue.setVisibility(View.INVISIBLE);
                                }else{
                                    isFx = false;
                                }

                                if(closeablePosition!=null){//is selling
                                    mSymbolCashLeft.setVisibility(View.INVISIBLE);
                                    if(isFx){
                                        mCashOrStocksLeftLabel.setText(getString(R.string.buy_sell_fx_quantity_left));
                                    }else{
                                        mCashOrStocksLeftLabel.setText(getString(R.string.buy_sell_share_left));
                                    }
                                }else{
                                    if(isFx){
                                        mCashOrStocksLeftLabel.setText(getString(R.string.buy_sell_fx_cash_left));
                                    }else{
                                        mCashOrStocksLeftLabel.setText(getString(R.string.buy_sell_cash_left));
                                    }
                                }

//
//                                mRightNumber.setText(getCashLeftLabelResId(closeablePosition));
//                                mProfitLossView.setVisibility(
//                                        getProfitOrLossUsd(portfolioCompactDTO, quoteDTO, closeablePosition, clamped) == null ? View.GONE
//                                                : View.VISIBLE);
//
//                                Double profitLoss = showProfitLossUsd
//                                        ? getProfitOrLossUsd(portfolioCompactDTO, quoteDTO, closeablePosition, clamped)
//                                        : getProfitOrLossUsd(portfolioCompactDTO, quoteDTO, closeablePosition, clamped);
//                                if (profitLoss != null && clamped != null && clamped > 0)
//                                {
//                                    int stringResId = profitLoss < 0 ? R.string.buy_sell_sell_loss : R.string.buy_sell_sell_profit;
//                                    mProfitLossView.setText(
//                                            getString(
//                                                    stringResId,
//                                                    THSignedMoney.builder(profitLoss)
//                                                            .withOutSign()
//                                                            .currency(showProfitLossUsd ? null : null)
//                                                            .build().toString()));
//                                }
//                                else
//                                {
//                                    mProfitLossView.setText(getString(R.string.buy_sell_sell_loss, "--"));
//                                }




                                return true;
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean aBoolean) {
                                        Timber.d("Element");
                                    }
                                },
                                new TimberAndToastOnErrorAction1("Failed to fetch all")));
    }

    @NonNull
    protected Observable<SecurityCompactDTO> getSecurityObservable() {
        return securityCompactCache.getOne(requisite.securityId)
                .map(new Func1<Pair<SecurityId, SecurityCompactDTO>, SecurityCompactDTO>() {
                    @Override
                    public SecurityCompactDTO call(Pair<SecurityId, SecurityCompactDTO> pair) {
                        usedDTO.securityCompactDTO = pair.second;
                        return pair.second;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<SecurityCompactDTO>() {
                    @Override
                    public void call(@NonNull SecurityCompactDTO securityCompactDTO) {
                        initSecurityRelatedInfo(securityCompactDTO);
//                        String dateTime = DateUtils.getDisplayableDate(getResources(), securityCompactDTO.lastPriceDateAndTimeUtc,
//                                R.string.data_format_dd_mmm_yyyy_kk_mm_z);
//                        mPriceUpdatedTime.setText(getString(R.string.buy_sell_market_price_time, dateTime));
                    }
                })
                .share();
    }

    @NonNull
    protected Observable<PortfolioCompactDTO> getPortfolioCompactObservable() {
        return Observable.combineLatest(
                requisite.getPortfolioIdObservable(),
                portfolioCompactListCache.get(currentUserId.toUserBaseKey()),
                new Func2<PortfolioId, Pair<UserBaseKey, PortfolioCompactDTOList>, PortfolioCompactDTO>() {
                    @Override
                    public PortfolioCompactDTO call(
                            @NonNull final PortfolioId portfolioId,
                            @NonNull Pair<UserBaseKey, PortfolioCompactDTOList> pair) {
                        return pair.second.findFirstWhere(new Predicate<PortfolioCompactDTO>() {
                            @Override
                            public boolean apply(PortfolioCompactDTO candidate) {
                                return candidate.getPortfolioId().equals(portfolioId);
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<PortfolioCompactDTO>() {
                    @Override
                    public void call(@NonNull PortfolioCompactDTO portfolioCompactDTO) {
                        usedDTO.portfolioCompactDTO = portfolioCompactDTO;
                    }
                })
                .share();
    }

    @NonNull
    protected Observable<LiveQuoteDTO> getQuoteObservable() {
        return quoteServiceWrapper.getQuoteRx(requisite.securityId.getSecurityIdNumber())
                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Void> observable) {
                        return observable.delay(5000, TimeUnit.MILLISECONDS);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<LiveQuoteDTO>() {
                    @Override
                    public void call(@NonNull LiveQuoteDTO quoteDTO) {
                        usedDTO.quoteDTO = quoteDTO;
                        mStockPriceTextView.setText(String.valueOf(getLabel(quoteDTO)));
                        mMarketPriceSymbol.setText(quoteDTO.getCurrencyDisplay());

                        if(mConfirm.getText().toString().toLowerCase().contains(getString(R.string.buy_sell_confirm_buy_now).toLowerCase()))
                            mConfirm.setText(getString(R.string.buy_sell_confirm_buy_now) + " @ " + quoteDTO.getCurrencyDisplay() + " " + String.valueOf(getLabel(quoteDTO)));
                        if(mConfirm.getText().toString().toLowerCase().contains(getString(R.string.buy_sell_confirm_sell_now).toLowerCase()))
                            mConfirm.setText(getString(R.string.buy_sell_confirm_sell_now) + " @ " + quoteDTO.getCurrencyDisplay() + " " + String.valueOf(getLabel(quoteDTO)));
                    }
                })
                .share();
    }

    @NonNull
    protected Observable<PositionDTO> getCloseablePositionObservable() // It can pass null values
    {
        return Observable.combineLatest(
                positionCompactListCache.get(requisite.securityId),
                requisite.getPortfolioIdObservable(),
                new Func2<Pair<SecurityId, PositionDTOList>, PortfolioId, PositionDTO>() {
                    @Override
                    public PositionDTO call(@NonNull Pair<SecurityId, PositionDTOList> positionDTOsPair, @NonNull PortfolioId portfolioId) {
                        PositionDTO position = positionDTOsPair.second.findFirstWhere(getCloseablePositionPredicate(
                                positionDTOsPair.second,
                                portfolioId));
                        usedDTO.closeablePosition = position;
                        return position;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .share();
    }
    //</editor-fold>

//    @NonNull
//    protected Observable<List<OwnedPortfolioId>> getAllCloseablePositionObservable() {
//        return Observable.combineLatest(
//                positionCompactListCache.get(requisite.securityId),
//                getApplicablePortfolioIdsObservable(),
//                new Func2<Pair<SecurityId, PositionDTOList>, OwnedPortfolioIdList, List<OwnedPortfolioId>>() {
//                    @Override
//                    public List<OwnedPortfolioId> call(Pair<SecurityId, PositionDTOList> securityIdPositionDTOListPair,
//                                                       OwnedPortfolioIdList ownedPortfolioIds) {
//                        List<OwnedPortfolioId> closeableOwnedPortfolioIdList = new ArrayList<>();
//
//                        for (PositionDTO positionDTO : securityIdPositionDTOListPair.second) {
//                            if (ownedPortfolioIds.contains(positionDTO.getOwnedPortfolioId())
//                                    && positionDTO.shares != null
//                                    && positionDTO.shares != 0) {
//                                closeableOwnedPortfolioIdList.add(positionDTO.getOwnedPortfolioId());
//                            }
//                        }
//
//                        return closeableOwnedPortfolioIdList;
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .share();
//    }

    @NonNull
    protected Observable<Integer> getMaxValueObservable() // It can pass null values
    {
        return getCloseablePositionObservable()
                .flatMap(new Func1<PositionDTO, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(@Nullable final PositionDTO closeablePosition) {
                        if (closeablePosition != null && closeablePosition.shares != null) {
                            return Observable.just(Math.abs(closeablePosition.shares));
                        }
                        return Observable.combineLatest(
                                getPortfolioCompactObservable(),
                                getQuoteObservable(),
                                new Func2<PortfolioCompactDTO, LiveQuoteDTO, Integer>() {
                                    @Override
                                    public Integer call(@NonNull PortfolioCompactDTO portfolioCompactDTO, @NonNull LiveQuoteDTO quoteDTO) {
                                        return getMaxValue(portfolioCompactDTO, quoteDTO, closeablePosition);
                                    }
                                });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .doOnNext(new Action1<Integer>()
                {
                    @Override public void call(@Nullable Integer maxValue)
                    {
                        if (maxValue != null)
                        {
                            mSeekBar.setMax(maxValue);
//                            mSeekBar.setEnabled(maxValue > 0);
                        }
                    }
                })
                .share();
    }

    @NonNull
    protected Observable<Integer> getClampedQuantityObservable() // It can pass null values
    {
        return Observable.combineLatest(
                getMaxValueObservable(),
                quantitySubject.distinctUntilChanged()
                        .flatMap(new Func1<Integer, Observable<Integer>>() {
                            @Override
                            public Observable<Integer> call(Integer quantity) {
                                if (quantity != null) {
                                    return Observable.just(quantity);
                                }
                                return getProposedInitialQuantity().take(1);
                            }
                        }),
                new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(@Nullable Integer maxValue, @Nullable Integer quantity) {
                        if (maxValue == null || quantity == null) {
                            return null;
                        }
                        return Math.max(0, Math.min(maxValue, quantity));
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(@Nullable Integer clampedQuantity) {
                        usedDTO.clampedQuantity = clampedQuantity;
                        if (clampedQuantity != null) {
                            if (mSeekBar.getProgress() != clampedQuantity)
                            {
                                mSeekBar.setProgress(clampedQuantity);
                            }

                            boolean updateText;
                            try {
                                updateText = clampedQuantity != Integer.parseInt(mMiddleNumber.getEditableText().toString());
                            } catch (NumberFormatException e) {
                                updateText = true;
                            }
                            if (updateText) {
                                if(clampedQuantity==0){
                                    mMiddleNumber.setText(String.valueOf(""));
                                }else{
                                    mMiddleNumber.setText(String.valueOf(clampedQuantity));
                                    buySellQuantity.setText(String.valueOf(clampedQuantity));
                                }
                                mMiddleNumber.setSelection(mMiddleNumber.getText().length());
                            }
                        }
                    }
                })
                .share();
    }

    @NonNull
    protected Observable<Integer> getProposedInitialQuantity() // It can pass null values
    {
        return Observable.combineLatest(
                getPortfolioCompactObservable(),
                getQuoteObservable(),
                new Func2<PortfolioCompactDTO, LiveQuoteDTO, Integer>() // It can pass null values
                {
                    @Override
                    public Integer call(@NonNull PortfolioCompactDTO portfolioCompactDTO, @NonNull LiveQuoteDTO quoteDTO) {
                        if(portfolioCompactDTO.providerId!=null){
                            ProviderDTO providerDTO = providerCacheRx.getCachedValue(new ProviderId(portfolioCompactDTO.providerId));
                            if(providerDTO.noDefaultShareQty){
                                return 0;
                            }else{
                                Double priceCcy = getPriceCcy(portfolioCompactDTO, quoteDTO);
                                return (priceCcy == null || priceCcy == 0) ? null : (int) Math.floor(INITIAL_VALUE / priceCcy);
                            }
                        }else{
                            Double priceCcy = getPriceCcy(portfolioCompactDTO, quoteDTO);
                            return (priceCcy == null || priceCcy == 0) ? null : (int) Math.floor(INITIAL_VALUE / priceCcy);
                        }
                    }
                })
                .share();
    }

    @NonNull
    abstract protected Predicate<PositionDTO> getCloseablePositionPredicate(
            @NonNull PositionDTOList positionDTOs,
            @NonNull PortfolioId portfolioId);

    private void initSecurityRelatedInfo(@Nullable SecurityCompactDTO securityCompactDTO){
        if (securityCompactDTO != null)
        {
            if (!StringUtils.isNullOrEmpty(securityCompactDTO.name))
            {
                securityName.setText(securityCompactDTO.name);
                securitySymbol.setText(securityCompactDTO.getExchangeSymbol());

                topText1.setText(securityCompactDTO.getExchangeSymbol());
            }
            else
            {
                securityName.setText(securityCompactDTO.getExchangeSymbol());
            }

            picasso.load(securityCompactDTO.imageBlobUrl).into(securityLogo);

        }
        else
        {
            securityName.setText("-");
        }
    }

    protected void initPortfolioRelatedInfo(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @NonNull LiveQuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition,
            @Nullable Integer clamped) {

        updateDisplay();

        if (!hasTradeValueTextFieldFocus) {
            mLeftNumber.setText(getTradeValueText(portfolioCompactDTO, quoteDTO, clamped));
        }

//        mTradeSymbol.setText(portfolioCompactDTO.currencyDisplay);

        mSymbolCashValue.setText(portfolioCompactDTO.currencyDisplay);
        mSymbolCashLeft.setText(portfolioCompactDTO.currencyDisplay);

        enableUI();

//        if (clamped != null) {
//            mRightNumber.setText(getCashShareLeft(portfolioCompactDTO, quoteDTO, closeablePosition, clamped));
//        }

        updateDisplay();
    }

    protected abstract String getLabel(@NonNull LiveQuoteDTO quoteDTO);

    @NonNull
    protected abstract THSignedNumber getFormattedPrice(double price);

    @Nullable
    protected Integer getMaxPurchasableShares(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull LiveQuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition) {
        if (portfolioCompactDTO.providerId != null) {
            ProviderDTO providerDTO = providerCacheRx.getCachedValue(new ProviderId(portfolioCompactDTO.providerId));

            return PortfolioCompactDTOUtil.getMaxPurchasableShares(
                    portfolioCompactDTO,
                    quoteDTO,
                    closeablePosition,
                    providerDTO);
        }

        return PortfolioCompactDTOUtil.getMaxPurchasableShares(
                portfolioCompactDTO,
                quoteDTO,
                closeablePosition,
                null);
    }

    @Nullable
    protected Integer getMaxSellableShares(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull LiveQuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition) {
        return PortfolioCompactDTOUtil.getMaxSellableShares(
                portfolioCompactDTO,
                quoteDTO,
                closeablePosition);
    }

    @Nullable
    protected Double getRemainingForPurchaseInPortfolioRefCcy(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable LiveQuoteDTO quoteDTO,
            int quantity) {
        LiveQuoteDTO quoteInPortfolioCcy = PortfolioCompactDTOUtil.createQuoteInPortfolioRefCcy(quoteDTO, portfolioCompactDTO);
        if (quoteInPortfolioCcy != null
                && quoteInPortfolioCcy.getAskPrice() != null
                && portfolioCompactDTO != null) {
            double available = portfolioCompactDTO.getUsableForTransactionRefCcy();
            double value;
            if(portfolioCompactDTO.providerId!=null){
                value = quantity * quoteInPortfolioCcy.getAskPrice();
            }else{
                value = quantity * quoteInPortfolioCcy.getAskUSD();
            }

            return available - value;
        }
        return null;
    }

    @Nullable
    protected Double getRemainingForShortingInPortfolioRefCcy(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable LiveQuoteDTO quoteDTO,
            int quantity) {
        LiveQuoteDTO quoteInPortfolioCcy = PortfolioCompactDTOUtil.createQuoteInPortfolioRefCcy(quoteDTO, portfolioCompactDTO);
        if (quoteInPortfolioCcy != null
                && quoteInPortfolioCcy.getBidPrice() != null
                && portfolioCompactDTO != null) {
            double available = portfolioCompactDTO.getUsableForTransactionRefCcy();
            double value = quantity * quoteInPortfolioCcy.getBidPrice();
            return available - value;
        }
        return null;
    }

    @NonNull
    public String getRemainingWhenBuy(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull LiveQuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition,
            int quantity) {
        String cashLeftText = null;
        if (closeablePosition != null) {
            Integer maxPurchasableShares = getMaxPurchasableShares(portfolioCompactDTO, quoteDTO, closeablePosition);
            if (maxPurchasableShares != null && maxPurchasableShares != 0) {
                cashLeftText = THSignedNumber.builder(maxPurchasableShares - quantity)
                        .relevantDigitCount(1)
                        .withOutSign()
                        .build().toString();
            }
        } else {
            Double remaining = getRemainingForPurchaseInPortfolioRefCcy(portfolioCompactDTO, quoteDTO, quantity);
            if (remaining != null) {
                if (portfolioCompactDTO.leverage != null && portfolioCompactDTO.leverage != 0) {
                    remaining /= portfolioCompactDTO.leverage;
                }
                THSignedNumber thSignedNumber = THSignedNumber
                        .builder(remaining)
                        .withOutSign()
//                        .currency(portfolioCompactDTO.currencyDisplay)//disable currency
                        .build();

                cashLeftText = thSignedNumber.toString();
            }
        }

        if (cashLeftText == null) {
            cashLeftText = "0"; //getResources().getString(R.string.na);
        }

        return cashLeftText;
    }

    @NonNull
    public String getRemainingWhenSell(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull LiveQuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition,
            int quantity) {
        String shareLeftText = null;
        if (closeablePosition != null) {
            Integer maxSellableShares = getMaxSellableShares(portfolioCompactDTO, quoteDTO, closeablePosition);
            if (maxSellableShares != null && maxSellableShares != 0) {
                shareLeftText = THSignedNumber.builder(maxSellableShares - quantity)
                        .relevantDigitCount(1)
                        .withOutSign()
                        .build().toString();
            }
        } else {
            Double remaining = getRemainingForShortingInPortfolioRefCcy(portfolioCompactDTO, quoteDTO, quantity);
            if (remaining != null) {
                if (portfolioCompactDTO.leverage != null && portfolioCompactDTO.leverage != 0) {
                    remaining /= portfolioCompactDTO.leverage;
                }
                THSignedNumber thSignedNumber = THSignedMoney
                        .builder(remaining)
                        .withOutSign()
                        .currency(portfolioCompactDTO.currencyDisplay)
                        .build();
                shareLeftText = thSignedNumber.toString();
            }
        }

        if (shareLeftText == null) {
            shareLeftText = getResources().getString(R.string.na);
        }
        return shareLeftText;
    }

    public String getTradeValueText(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable LiveQuoteDTO quoteDTO,
            @Nullable Integer quantity) {
        String valueText = "-";
        if (portfolioCompactDTO != null && quoteDTO != null && quantity != null) {
            Double priceRefCcy = getPriceCcy(portfolioCompactDTO, quoteDTO);
            if (priceRefCcy != null) {
                double value = quantity * priceRefCcy;
                if (portfolioCompactDTO.leverage != null && portfolioCompactDTO.leverage != 0) {
                    value /= portfolioCompactDTO.leverage;
                }
                THSignedNumber thTradeValue = THSignedNumber.builder(value)
                        .withOutSign()
                        .build();
                valueText = thTradeValue.toString();
            }
        }
        return valueText;
    }

    public Integer getQuantityFromTradeValue(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable LiveQuoteDTO quoteDTO,
            @Nullable Double tradeValue
    ) {
        Double quantity = 0.0;
        if (portfolioCompactDTO != null && quoteDTO != null && tradeValue != null) {
            Double priceRefCcy = getPriceCcy(portfolioCompactDTO, quoteDTO);
            if (priceRefCcy != null) {
                quantity = tradeValue / priceRefCcy;
            }
        }

        return quantity.intValue();
    }

    public String getQuantityString() {
        return mMiddleNumber.getText().toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.vquantity)
    public void onQuantityClicked(View v) {
        mPriceSelectionMethod = AnalyticsConstants.ManualQuantityInput;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.dialog_btn_confirm)
    public void onConfirmClicked(View v) {
        if(!mSeekBar.isEnabled()){
            return;
        }
        if(isInCompetition){
            if(isBuy){
                GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_BUY_BUY_NOW);
            }else{
                GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_SELL_SELL_NOW);
            }
        }
        updateConfirmButton(true);
        fireBuySellReport();
        launchBuySell();
        getDialog().hide();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.comments)
    void onCommentAreaClicked(View commentTextBox) {
        Bundle bundle = new Bundle();
        SecurityDiscussionEditPostFragment.putSecurityId(bundle, requisite.securityId);

        if (unSpannedComment != null) {
            SecurityDiscussionEditPostFragment.putComment(bundle, unSpannedComment.toString());
        }
        transactionCommentFragment = navigator.get().pushFragment(TransactionEditCommentFragment.class, bundle);

        //Google Analtyics
        if(isInCompetition){
            if(isBuy){
                GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_BUY_COMMENT);
            }else{
                GAnalyticsProvider.sendGAActionEvent("Competition", GAnalyticsProvider.ACTION_ENTER_SELL_COMMENT);
            }
        }

        getDialog().hide();
    }

    @Deprecated
    public void setBuySellTransactionListener(BuySellTransactionListener buySellTransactionListener) {
        this.buySellTransactionListener = buySellTransactionListener;
    }

    private void updateDisplay() {
        updateConfirmButton(false);
    }

    @Nullable
    public Double getProfitOrLossUsd() {
        return getProfitOrLossUsd(usedDTO.portfolioCompactDTO, usedDTO.quoteDTO, usedDTO.closeablePosition, usedDTO.clampedQuantity);
    }

    @Nullable
    protected abstract Double getProfitOrLossUsd(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable LiveQuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition,
            @Nullable Integer quantity);  // TODO do a getProfitOrLossPortfolioCcy

    @NonNull
    public abstract String getCashShareLeft(@NonNull PortfolioCompactDTO portfolioCompactDTO,
                                            @NonNull LiveQuoteDTO quoteDTO,
                                            @Nullable PositionDTOCompact closeablePosition,
                                            int quantity);

    protected void updateConfirmButton(boolean forceDisable) {


        if (forceDisable) {
            mConfirm.setEnabled(false);
        } else {

            if(usedDTO.securityCompactDTO!=null
                    && usedDTO.securityCompactDTO.lotSize!=null){

                mConfirm.setEnabled(usedDTO.clampedQuantity != null && usedDTO.clampedQuantity != 0 && hasValidInfo()
                && Integer.parseInt(mMiddleNumber.getText().toString()) % usedDTO.securityCompactDTO.lotSize ==0) ;

            }else{
                mConfirm.setEnabled(usedDTO.clampedQuantity != null && usedDTO.clampedQuantity != 0 && hasValidInfo());
            }
        }
    }

    private boolean checkValidToTransact() {
        return requisite.securityId.getExchange() != null
                && requisite.securityId.getSecuritySymbol() != null;
    }

    private void launchBuySell() {
        if (checkValidToTransact()
                && usedDTO.quoteDTO != null
                && usedDTO.clampedQuantity != null) {
            TransactionFormDTO transactionFormDTO = getBuySellOrder(
                    usedDTO.quoteDTO,
                    requisite.getPortfolioIdObservable().toBlocking().first(),
                    usedDTO.clampedQuantity);
            if (transactionFormDTO != null) {
                unsubscribe(buySellSubscription);
                buySellSubscription = getTransactionSubscription(transactionFormDTO);
            } else {
                AlertDialogBuySellRxUtil.informBuySellOrderWasNull(getActivity())
                        .subscribe(
                                Actions.empty(),
                                new EmptyAction1<Throwable>());
            }
        }
    }

    public TransactionFormDTO getBuySellOrder(
            @NonNull LiveQuoteDTO quoteDTO,
            @NonNull PortfolioId portfolioId,
            int quantity) {
        return new TransactionFormDTO(
                shareDelegateFragment.shareTo(SocialNetworkEnum.FB),
                shareDelegateFragment.shareTo(SocialNetworkEnum.TW),
                shareDelegateFragment.shareTo(SocialNetworkEnum.LN),
                shareDelegateFragment.shareTo(SocialNetworkEnum.WB),
                null,
                null,
                null,
                false,
                unSpannedComment != null ? unSpannedComment.toString() : null,
                quoteDTO.getRawResponse(),
                quantity,
                portfolioId.key
        );
    }

    protected void fireBuySellReport() {
        //TODO Change Analytics
        //analytics.fireEvent(getSharingOptionEvent());
    }

    public SharingOptionsEvent getSharingOptionEvent() {
        SharingOptionsEvent.Builder builder = new SharingOptionsEvent.Builder()
                .setSecurityId(requisite.securityId)
                .setProviderId(usedDTO.portfolioCompactDTO == null ? null : usedDTO.portfolioCompactDTO.getProviderIdKey())
                .setPriceSelectionMethod(mPriceSelectionMethod)
                .hasComment(!mCommentsEditText.getText().toString().isEmpty())
                .facebookEnabled(shareDelegateFragment.shareTo(SocialNetworkEnum.FB))
                .twitterEnabled(shareDelegateFragment.shareTo(SocialNetworkEnum.TW))
                .linkedInEnabled(shareDelegateFragment.shareTo(SocialNetworkEnum.LN))
                .wechatEnabled(shareDelegateFragment.shareTo(SocialNetworkEnum.WECHAT))
                .weiboEnabled(shareDelegateFragment.shareTo(SocialNetworkEnum.WB));
        setBuyEventFor(builder);

        return builder.build();
    }

    protected abstract void setBuyEventFor(SharingOptionsEvent.Builder builder);

    private ActionMode.Callback createActionModeCallBackForQuantityEditText() {
        //We want to disable action mode since it's irrelevant
        return new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }
        };
    }

    @SuppressWarnings("unused")
    @OnTextChanged(value = R.id.vquantity, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterQuantityTextChanged(Editable editable) {
        String stringValue = editable.toString();
        if (usedDTO.portfolioCompactDTO != null
                && usedDTO.quoteDTO != null) {
            int val = 0;
            try {
                val = Integer.parseInt(stringValue);
            } catch (NumberFormatException e) {
                val = 0;
                Timber.e(e, "Failed to parse number: " + stringValue);
            }
            quantitySubject.onNext(val);
        }
    }

    public void populateComment() {
        if (transactionCommentFragment != null) {
            unSpannedComment = transactionCommentFragment.getComment();

            mCommentsEditText.setText(unSpannedComment);
        }
    }

    protected class BuySellObserver implements Observer<SecurityPositionTransactionDTO> {
        @NonNull
        private final SecurityId securityId;
        @NonNull
        private final TransactionFormDTO transactionFormDTO;
        private final boolean isBuy;

        public BuySellObserver(@NonNull SecurityId securityId,
                               @NonNull TransactionFormDTO transactionFormDTO,
                               boolean isBuy) {
            this.securityId = securityId;
            this.transactionFormDTO = transactionFormDTO;
            this.isBuy = isBuy;
        }

        @Override
        public void onNext(SecurityPositionTransactionDTO securityPositionDetailDTO) {
            if (securityPositionDetailDTO == null) {
                AlertDialogBuySellRxUtil.informBuySellOrderReturnedNull(getActivity())
                        .subscribe(
                                Actions.empty(),
                                new EmptyAction1<Throwable>());
                return;
            }

            //server is handling this
//            if (shareDelegateFragment.shareTo(SocialNetworkEnum.FB))
//            {
//                shareFacebookClient(isBuy);
//            }

//            if (shareDelegateFragment.isShareToWeChat()) {
//                shareToWeChat(mCommentsEditText.getText().toString(), isBuy);
//            }

            String positionType = null;

            if (securityPositionDetailDTO.positions == null) {
                positionType = TabbedPositionListFragment.TabType.CLOSED.name();
            } else {
                if (securityPositionDetailDTO.positions.size() == 0) {
                    positionType = TabbedPositionListFragment.TabType.CLOSED.name();
                } else {
                    positionType = securityPositionDetailDTO.positions.get(0).positionStatus.name();
                }
            }

            navigator.get().popFragment();
            pushPortfolioFragment(
                    new OwnedPortfolioId(currentUserId.get(), securityPositionDetailDTO.portfolio.id),
                    securityPositionDetailDTO.portfolio,
                    positionType
            );
        }

        @Override
        public void onCompleted() {
            updateConfirmButton(false);
            //navigator.get().popFragment();
        }

        @Override
        public void onError(Throwable e) {
            onCompleted();

//            Timber.e(e, "Failed to %s %s with %s", isBuy ? "buy" : "sell", securityId, transactionFormDTO);
            try{
                THException thException = new THException(e);
                THToast.show(thException);
                if (buySellTransactionListener != null) {
                    buySellTransactionListener.onTransactionFailed(isBuy, thException);
                }
            }catch (Exception exception){
                THToast.show("Failed to buy/sell");
            }
        }
    }

    //server is handling this
//    protected void shareFacebookClient(boolean isBuy)
//    {
//        Intent shareIntent = new Intent(getActivity(), FacebookShareActivity.class);
//        Bundle extras = new Bundle();
//        FacebookShareActivity.setMessage(
//                extras,
//                String.format(
//                        getString(R.string.traded_facebook_share_message),
//                        mMiddleNumber.getText(),
//                        usedDTO.securityCompactDTO.name,
//                        SecurityCompactDTOUtil.getShortSymbol(usedDTO.securityCompactDTO),
//                        getFormattedPrice(isBuy ? usedDTO.quoteDTO.ask : usedDTO.quoteDTO.bid)));
//        FacebookShareActivity.setName(extras, "TradeHero");
//        FacebookShareActivity.setCaption(extras, "by myhero");
//        FacebookShareActivity.setDescription(
//                extras,
//                String.format(
//                        "Follow %s on TradeHero for great stock tips!",
//                        shareDelegateFragment.getUserProfileDTO().displayName));
//        if (usedDTO.securityCompactDTO.imageBlobUrl == null)
//        {
//            FacebookShareActivity.setDefaultPictureUrl(extras);
//        }
//        else
//        {
//            FacebookShareActivity.setPictureUrl(extras, usedDTO.securityCompactDTO.imageBlobUrl);
//        }
//        shareIntent.putExtras(extras);
//        startActivity(shareIntent);
//    }

//    protected void shareToWeChat(String commentString, boolean isTransactionTypeBuy) {
//        WeChatDTO weChatDTO = new WeChatDTO();
//        weChatDTO.id = usedDTO.securityCompactDTO.id;
//        weChatDTO.type = WeChatMessageType.Trade;
//        if (usedDTO.securityCompactDTO.imageBlobUrl != null && usedDTO.securityCompactDTO.imageBlobUrl.length() > 0) {
//            weChatDTO.imageURL = usedDTO.securityCompactDTO.imageBlobUrl;
//        }
//        if (isTransactionTypeBuy) {
//            weChatDTO.title = getString(R.string.buy_sell_switch_buy) + " "
//                    + usedDTO.securityCompactDTO.name + " " + getString(
//                    R.string.buy_sell_share_count) + " @" + usedDTO.quoteDTO.getAskPrice();
//        } else {
//            weChatDTO.title = getString(R.string.buy_sell_switch_sell) + " "
//                    + usedDTO.securityCompactDTO.name + " " + mMiddleNumber.getText() + getString(
//                    R.string.buy_sell_share_count) + " @" + usedDTO.quoteDTO.getBidPrice();
//        }
//        if (commentString != null && !commentString.isEmpty()) {
//            weChatDTO.title = commentString + '\n' + weChatDTO.title;
//        }
//
//        Intent intent = new Intent(getActivity(), WXEntryActivity.class);
//        WXEntryActivity.putWeChatDTO(intent, weChatDTO);
//        startActivity(intent);
//    }

    private void pushPortfolioFragment(OwnedPortfolioId ownedPortfolioId, PortfolioDTO portfolioDTO, String positionType) {
        DeviceUtil.dismissKeyboard(getActivity());

        if (navigator.get().hasBackStackName(TabbedPositionListFragment.class.getName())) {
            navigator.get().popFragment(TabbedPositionListFragment.class.getName());
        } else if (navigator.get().hasBackStackName(CompetitionLeaderboardPositionListFragment.class.getName())) {
            navigator.get().popFragment(CompetitionLeaderboardPositionListFragment.class.getName());
            // Test for other classes in the future
        } else {
            // TODO find a better way to remove this fragment from the stack
            navigator.get().popFragment();

            Bundle args = new Bundle();

            CompetitionLeaderboardPositionListFragment.putApplicablePortfolioId(args, ownedPortfolioId);
            CompetitionLeaderboardPositionListFragment.putIsFX(args, portfolioDTO.assetClass);
            CompetitionLeaderboardPositionListFragment.putGetPositionsDTOKey(args, ownedPortfolioId);
            CompetitionLeaderboardPositionListFragment.putShownUser(args, ownedPortfolioId.getUserBaseKey());
            CompetitionLeaderboardPositionListFragment.putPositionType(args, positionType);

            if (portfolioDTO.providerId != null) {
                ProviderId providerId = new ProviderId(portfolioDTO.providerId);

                CompetitionLeaderboardPositionListFragment.putProviderId(args, providerId);

                ProviderDTO providerDTO = providerCacheRx.getCachedValue(providerId);
                args.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL, providerDTO.navigationLogoUrl);
                args.putString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_COLOR, providerDTO.hexColor);
                isInCompetition = true;
            }

            navigator.get().pushFragment(CompetitionLeaderboardPositionListFragment.class, args);
        }
    }

    @Deprecated
    public interface BuySellTransactionListener {
        void onTransactionSuccessful(boolean isBuy, @NonNull SecurityPositionTransactionDTO securityPositionTransactionDTO, String commentString);

        void onTransactionFailed(boolean isBuy, THException error);
    }

    public static class Requisite {
        private static final String KEY_SECURITY_ID = Requisite.class.getName() + ".security_id";
        private static final String KEY_PORTFOLIO_ID = Requisite.class.getName() + ".portfolio_id";
        private static final String KEY_QUOTE_DTO = Requisite.class.getName() + ".quote_dto";
        private static final String KEY_QUANTITY = Requisite.class.getName() + ".quantity";
        private static final String KEY_BAR_COLOR = Requisite.class.getName() + ".barColor";

        @NonNull
        public final SecurityId securityId;
        @NonNull
        public final LiveQuoteDTO quoteDTO;
        @Nullable
        public final Integer quantity;
        @NonNull
        private final BehaviorSubject<PortfolioId> portfolioIdSubject;

        public Requisite(@NonNull SecurityId securityId,
                         @NonNull PortfolioId portfolioId,
                         @NonNull LiveQuoteDTO quoteDTO,
                         @Nullable Integer quantity) {

            this.securityId = securityId;
            this.quoteDTO = quoteDTO;
            this.quantity = quantity;
            this.portfolioIdSubject = BehaviorSubject.create(portfolioId);
        }

        public Requisite(@NonNull Bundle args) {
            Bundle securityArgs = args.getBundle(KEY_SECURITY_ID);
            if (securityArgs != null) {
                this.securityId = new SecurityId(securityArgs);
            } else {
                throw new NullPointerException("SecurityId cannot be null");
            }
            Bundle portfolioArgs = args.getBundle(KEY_PORTFOLIO_ID);
            if (portfolioArgs != null) {
                this.portfolioIdSubject = BehaviorSubject.create(new PortfolioId(portfolioArgs));
            } else {
                throw new NullPointerException("PortfolioId cannot be null");
            }
            Bundle quoteArgs = args.getBundle(KEY_QUOTE_DTO);
            if (quoteArgs != null) {
                this.quoteDTO = new LiveQuoteDTO(quoteArgs);
            } else {
                throw new NullPointerException("Quote cannot be null");
            }
            if (args.containsKey(KEY_QUANTITY)) {
                this.quantity = args.getInt(KEY_QUANTITY);
            } else {
                this.quantity = null;
            }
        }

        @NonNull
        public Bundle getArgs() {
            Bundle args = new Bundle();
            populate(args);
            return args;
        }

        protected void populate(@NonNull Bundle args) {
            args.putBundle(KEY_SECURITY_ID, securityId.getArgs());
            args.putBundle(KEY_PORTFOLIO_ID, portfolioIdSubject.toBlocking().first().getArgs());
            args.putBundle(KEY_QUOTE_DTO, quoteDTO.getArgs());
            if (quantity != null) {
                args.putInt(KEY_QUANTITY, quantity);
            } else {
                args.remove(KEY_QUANTITY);
            }
        }

        @NonNull
        public Observable<PortfolioId> getPortfolioIdObservable() {
            return portfolioIdSubject.distinctUntilChanged().asObservable();
        }
    }

    @SuppressWarnings("unused")
    @OnClick(android.R.id.closeButton)
    protected void onCloseClicked(View button)
    {
        dismiss();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.close_button_competition)
    protected void onCloseClickedCompetition(View button)
    {
        dismiss();
    }

    private void disableUI(){
        mMiddleNumber.setEnabled(false);
        mSeekBar.setEnabled(false);
        mConfirm.setEnabled(false);
    }

    private void enableUI(){
        mMiddleNumber.setEnabled(true);
        if(!mSeekBar.isEnabled()){//not yet enabled
            YoYo.with(Techniques.Pulse).playOn(mSeekBar);
        }
        mSeekBar.setEnabled(true);

    }

    public static class UsedDTO {
        @Nullable
        public SecurityCompactDTO securityCompactDTO;
        @Nullable
        public PortfolioCompactDTO portfolioCompactDTO;
        @Nullable
        public LiveQuoteDTO quoteDTO;
        @Nullable
        public PositionDTOCompact closeablePosition;
        @Nullable
        public Integer clampedQuantity;
    }

    protected void pushLiveLogin(RetrofitError error)
    {
        try {
            if (LiveConstants.hasLiveAccount) {
                JSONObject buySellStockError = new JSONObject(new String(((TypedByteArray) error.getResponse().getBody()).getBytes()));
                // user has a live account, but not logged in, redirect to the extracted json URL
                Bundle args = getArguments();
                String redirectURL = buySellStockError.get(LiveViewFragment.BUNDLE_KEY_REDIRECT_URL_ID).toString();
                args.putString(LiveViewFragment.BUNDLE_KEY_REDIRECT_URL_ID, redirectURL);
                LiveViewFragment liveViewFragment = new LiveViewFragment();
                liveViewFragment.setArguments(args);
                liveViewFragment.putUrl(args, redirectURL);

                try {
                    unsubscribe(buySellSubscription);
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }

                navigator.get().pushFragment(LiveViewFragment.class, args);
                dismiss();

            } else {
                Intent kycIntent = new Intent(getActivity(), SignUpLiveActivity.class);
                startActivity(kycIntent);
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error in redirection: " + e.getStackTrace().toString() , Toast.LENGTH_LONG).show();
        }
    }
}