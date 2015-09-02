package com.tradehero.th.fragments.trade;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.android.internal.util.Predicate;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.activities.FacebookShareActivity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOList;
import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.api.portfolio.key.PortfolioCompactListKey;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOCompact;
import com.tradehero.th.api.position.PositionDTOList;
import com.tradehero.th.api.position.SecurityPositionTransactionDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOUtil;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.base.LollipopArrayAdapter;
import com.tradehero.th.fragments.discussion.SecurityDiscussionEditPostFragment;
import com.tradehero.th.fragments.discussion.TransactionEditCommentFragment;
import com.tradehero.th.fragments.social.ShareDelegateFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.portfolio.MenuOwnedPortfolioId;
import com.tradehero.th.models.share.SocialShareHelper;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.dto.SocialShareResult;
import com.tradehero.th.persistence.portfolio.OwnedPortfolioIdListCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.position.PositionListCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.view.adapter.AdapterViewObservable;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import com.tradehero.th.utils.DateUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SharingOptionsEvent;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.Func6;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

abstract public class AbstractTransactionFragment extends DashboardFragment
{
    private static final String KEY_REQUISITE = AbstractTransactionFragment.class.getName() + ".requisite";
    private static final double INITIAL_VALUE = 5000;

    @Bind(R.id.vcash_left) protected TextView mCashShareLeftTextView;
    @Bind(R.id.vtrade_value) protected TextView mTradeValueTextView;
    @Bind(R.id.vmarket_symbol) protected TextView mMarketPriceSymbol;
    @Bind(R.id.price_updated_time) protected TextView mPriceUpdatedTime;
    @Bind(R.id.vtrade_symbol) protected TextView mTradeSymbol;
    @Bind(R.id.dialog_price) protected TextView mStockPriceTextView;
    @Bind(R.id.vquantity) protected EditText mQuantityEditText;
    @Bind(R.id.comments) protected TextView mCommentsEditText;
    @Bind(R.id.dialog_btn_confirm) protected Button mConfirm;
    @Bind(R.id.portfolio_spinner) protected Spinner mPortfolioSpinner;

    @Inject SecurityCompactCacheRx securityCompactCache;
    @Inject PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject PositionListCacheRx positionCompactListCache;
    @Inject Analytics analytics;
    @Inject QuoteServiceWrapper quoteServiceWrapper;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject CurrentUserId currentUserId;
    @Inject protected SocialShareHelper socialShareHelper;
    @Inject protected OwnedPortfolioIdListCacheRx ownedPortfolioIdListCache;
    @Inject protected Lazy<SocialSharer> socialSharerLazy;

    @NonNull protected final UsedDTO usedDTO;
    @NonNull private final BehaviorSubject<Integer> quantitySubject; // It can pass null values

    @Nullable protected OwnedPortfolioIdList applicableOwnedPortfolioIds;

    protected Subscription buySellSubscription;
    protected Requisite requisite;
    protected BuySellTransactionListener buySellTransactionListener;

    private String mPriceSelectionMethod = AnalyticsConstants.DefaultPriceSelectionMethod;
    private TransactionEditCommentFragment transactionCommentFragment;
    private List<MenuOwnedPortfolioId> menuOwnedPortfolioIdList;
    private ShareDelegateFragment shareDelegateFragment;
    Editable unSpannedComment;

    @Nullable protected abstract Integer getMaxValue(@NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition);

    protected abstract boolean hasValidInfo();

    protected abstract boolean isQuickButtonEnabled();

    protected abstract double getQuickButtonMaxValue(@NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition);

    abstract protected Subscription getTransactionSubscription(TransactionFormDTO transactionFormDTO);

    @Nullable public abstract Double getPriceCcy(@Nullable PortfolioCompactDTO portfolioCompactDTO, @Nullable QuoteDTO quoteDTO);

    public static boolean canShowDialog(@NonNull QuoteDTO quoteDTO, boolean isBuy)
    {
        return (isBuy && quoteDTO.ask != null) ||
                (!isBuy && quoteDTO.bid != null);
    }

    protected AbstractTransactionFragment()
    {
        super();
        this.usedDTO = new UsedDTO();
        this.quantitySubject = BehaviorSubject.create();
    }

    //<editor-fold desc="Arguments passing">
    public static void putRequisite(@NonNull Bundle args, @NonNull Requisite requisite)
    {
        args.putBundle(KEY_REQUISITE, requisite.getArgs());
    }

    @NonNull private static Requisite getRequisite(@NonNull Bundle args)
    {
        Bundle requisiteArgs = args.getBundle(KEY_REQUISITE);
        if (requisiteArgs != null)
        {
            return new Requisite(requisiteArgs);
        }
        else
        {
            throw new NullPointerException("Requisite cannot be null");
        }
    }
    //</editor-fold>

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        requisite = getRequisite(getArguments());
        usedDTO.quoteDTO = requisite.quoteDTO;
        quantitySubject.onNext(requisite.quantity);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.security_buy_sell_dialog, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mQuantityEditText.setCustomSelectionActionModeCallback(createActionModeCallBackForQuantityEditText());
        mQuantityEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent)
            {
                return false;
            }
        });

        if (this.getClass() == SellStockFragment.class || this.getClass() == SellFXFragment.class)
        {
            mConfirm.setText(R.string.buy_sell_confirm_sell_now);
        }

        shareDelegateFragment = new ShareDelegateFragment(this, this.getView());
        shareDelegateFragment.onCreate(savedInstanceState);
    }

    @Override public void onStart()
    {
        super.onStart();
        initFetches();

        onStopSubscriptions.add(
                Observable.zip(
                        requisite.getPortfolioIdObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
                        getPortfolioCompactListObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
                        getApplicablePortfolioIdsObservable().take(1).observeOn(AndroidSchedulers.mainThread()),
                        new Func3<PortfolioId, PortfolioCompactDTOList, OwnedPortfolioIdList, Boolean>()
                        {
                            @Override public Boolean call(
                                    @NonNull PortfolioId selectedPortfolioId,
                                    @NonNull PortfolioCompactDTOList portfolioCompactDTOs,
                                    @NonNull OwnedPortfolioIdList ownedPortfolioIds)
                            {
                                menuOwnedPortfolioIdList = new ArrayList<>();
                                for (PortfolioCompactDTO candidate : portfolioCompactDTOs)
                                {
                                    if (ownedPortfolioIds.contains(candidate.getOwnedPortfolioId()))
                                    {
                                        menuOwnedPortfolioIdList.add(new MenuOwnedPortfolioId(candidate.getUserBaseKey(), candidate));
                                    }
                                }

                                LollipopArrayAdapter<MenuOwnedPortfolioId> menuOwnedPortfolioIdLollipopArrayAdapter =
                                        new LollipopArrayAdapter<>(
                                                getActivity(),
                                                menuOwnedPortfolioIdList);
                                mPortfolioSpinner.setAdapter(menuOwnedPortfolioIdLollipopArrayAdapter);
                                mPortfolioSpinner.setEnabled(menuOwnedPortfolioIdList.size() > 1);

                                return null;
                            }
                        })
                        .subscribe(
                                new Action1<Boolean>()
                                {
                                    @Override public void call(Boolean aBoolean)
                                    {
                                        // Nothing to do
                                    }
                                },
                                new TimberOnErrorAction1("Failed to update portfolio selector")));

        onDestroyViewSubscriptions.add(AdapterViewObservable.selects(mPortfolioSpinner)
                        .map(new Func1<OnSelectedEvent, PortfolioId>()
                        {
                            @Override public PortfolioId call(OnSelectedEvent onSelectedEvent)
                            {
                                Integer i = (int) onSelectedEvent.parent.getSelectedItemId();

                                return new PortfolioId(menuOwnedPortfolioIdList.get(i).portfolioId);
                            }
                        })
                        .subscribe(new Action1<PortfolioId>()
                        {
                            @Override public void call(PortfolioId portfolioId)
                            {
                                requisite.portfolioIdSubject.onNext(portfolioId);
                            }
                        }, new TimberOnErrorAction1("message"))
        );
    }

    @Override public void onDetach()
    {
        transactionCommentFragment = null;
        super.onDetach();
    }

    @Override public void onDestroyView()
    {
        unsubscribe(buySellSubscription);
        buySellSubscription = null;
        ButterKnife.unbind(this);
        shareDelegateFragment.onDestroy();
        super.onDestroyView();
    }

    @Override public void onResume()
    {
        super.onResume();

        populateComment();
    }

    @NonNull protected Observable<PortfolioCompactDTOList> getPortfolioCompactListObservable()
    {
        return portfolioCompactListCache.get(currentUserId.toUserBaseKey())
                .map(new PairGetSecond<UserBaseKey, PortfolioCompactDTOList>())
                .share();
    }

    @NonNull protected Observable<OwnedPortfolioIdList> getApplicablePortfolioIdsObservable()
    {
        return ownedPortfolioIdListCache.get(requisite.securityId)
                .distinctUntilChanged(
                        new Func1<Pair<PortfolioCompactListKey, OwnedPortfolioIdList>, String>()
                        {
                            @Override
                            public String call(Pair<PortfolioCompactListKey, OwnedPortfolioIdList> pair)
                            {
                                String code = "first=" + pair.first + ", second=";
                                for (OwnedPortfolioId portfolioId : pair.second)
                                {
                                    code += portfolioId.toString() + ",";
                                }
                                return code;
                            }
                        })
                .map(new Func1<Pair<PortfolioCompactListKey, OwnedPortfolioIdList>, OwnedPortfolioIdList>()
                {
                    @Override public OwnedPortfolioIdList call(Pair<PortfolioCompactListKey, OwnedPortfolioIdList> pair)
                    {
                        applicableOwnedPortfolioIds = pair.second;
                        return pair.second;
                    }
                })
                .share();
    }

    //<editor-fold desc="Fetches">
    private void initFetches()
    {
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
                                QuoteDTO,
                                PositionDTO,
                                Integer,
                                Integer,
                                Boolean>()
                        {
                            @Override public Boolean call(
                                    @NonNull SecurityCompactDTO securityCompactDTO,
                                    @NonNull final PortfolioCompactDTO portfolioCompactDTO,
                                    @NonNull QuoteDTO quoteDTO,
                                    @Nullable PositionDTO closeablePosition,
                                    @Nullable Integer maxValue,
                                    @Nullable Integer clamped)
                            {
                                initPortfolioRelatedInfo(portfolioCompactDTO, quoteDTO, closeablePosition, clamped);

                                return true;
                            }
                        })
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Boolean>()
                                {
                                    @Override public void call(Boolean aBoolean)
                                    {
                                        Timber.d("Element");
                                    }
                                },
                                new TimberAndToastOnErrorAction1("Failed to fetch all")));
    }

    @NonNull protected Observable<SecurityCompactDTO> getSecurityObservable()
    {
        return securityCompactCache.getOne(requisite.securityId)
                .map(new Func1<Pair<SecurityId, SecurityCompactDTO>, SecurityCompactDTO>()
                {
                    @Override public SecurityCompactDTO call(Pair<SecurityId, SecurityCompactDTO> pair)
                    {
                        usedDTO.securityCompactDTO = pair.second;
                        return pair.second;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<SecurityCompactDTO>()
                {
                    @Override public void call(@NonNull SecurityCompactDTO securityCompactDTO)
                    {
                        initSecurityRelatedInfo(securityCompactDTO);
                        String dateTime = DateUtils.getDisplayableDate(getResources(), securityCompactDTO.lastPriceDateAndTimeUtc,
                                R.string.data_format_dd_mmm_yyyy_kk_mm_z);
                        mPriceUpdatedTime.setText(getString(R.string.buy_sell_market_price_time, dateTime));
                    }
                })
                .share();
    }

    @NonNull protected Observable<PortfolioCompactDTO> getPortfolioCompactObservable()
    {
        return Observable.combineLatest(
                requisite.getPortfolioIdObservable(),
                portfolioCompactListCache.get(currentUserId.toUserBaseKey()),
                new Func2<PortfolioId, Pair<UserBaseKey, PortfolioCompactDTOList>, PortfolioCompactDTO>()
                {
                    @Override public PortfolioCompactDTO call(
                            @NonNull final PortfolioId portfolioId,
                            @NonNull Pair<UserBaseKey, PortfolioCompactDTOList> pair)
                    {
                        return pair.second.findFirstWhere(new Predicate<PortfolioCompactDTO>()
                        {
                            @Override public boolean apply(PortfolioCompactDTO candidate)
                            {
                                return candidate.getPortfolioId().equals(portfolioId);
                            }
                        });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<PortfolioCompactDTO>()
                {
                    @Override public void call(@NonNull PortfolioCompactDTO portfolioCompactDTO)
                    {
                        usedDTO.portfolioCompactDTO = portfolioCompactDTO;
                    }
                })
                .share();
    }

    @NonNull protected Observable<QuoteDTO> getQuoteObservable()
    {
        return quoteServiceWrapper.getQuoteRx(requisite.securityId)
                .repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>()
                {
                    @Override public Observable<?> call(Observable<? extends Void> observable)
                    {
                        return observable.delay(5000, TimeUnit.MILLISECONDS);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<QuoteDTO>()
                {
                    @Override public void call(@NonNull QuoteDTO quoteDTO)
                    {
                        usedDTO.quoteDTO = quoteDTO;
                        mStockPriceTextView.setText(String.valueOf(getLabel(quoteDTO)));
                        mMarketPriceSymbol.setText(quoteDTO.currencyDisplay);
                    }
                })
                .share();
    }

    @NonNull protected Observable<PositionDTO> getCloseablePositionObservable() // It can pass null values
    {
        return Observable.combineLatest(
                positionCompactListCache.get(requisite.securityId),
                requisite.getPortfolioIdObservable(),
                new Func2<Pair<SecurityId, PositionDTOList>, PortfolioId, PositionDTO>()
                {
                    @Override public PositionDTO call(@NonNull Pair<SecurityId, PositionDTOList> positionDTOsPair, @NonNull PortfolioId portfolioId)
                    {
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

    @NonNull protected Observable<Integer> getMaxValueObservable() // It can pass null values
    {
        return getCloseablePositionObservable()
                .flatMap(new Func1<PositionDTO, Observable<Integer>>()
                {
                    @Override public Observable<Integer> call(@Nullable final PositionDTO closeablePosition)
                    {
                        if (closeablePosition != null && closeablePosition.shares != null)
                        {
                            return Observable.just(Math.abs(closeablePosition.shares));
                        }
                        return Observable.combineLatest(
                                getPortfolioCompactObservable(),
                                getQuoteObservable(),
                                new Func2<PortfolioCompactDTO, QuoteDTO, Integer>()
                                {
                                    @Override public Integer call(@NonNull PortfolioCompactDTO portfolioCompactDTO, @NonNull QuoteDTO quoteDTO)
                                    {
                                        return getMaxValue(portfolioCompactDTO, quoteDTO, closeablePosition);
                                    }
                                });
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .share();
    }

    @NonNull protected Observable<Integer> getClampedQuantityObservable() // It can pass null values
    {
        return Observable.combineLatest(
                getMaxValueObservable(),
                quantitySubject.distinctUntilChanged()
                        .flatMap(new Func1<Integer, Observable<Integer>>()
                        {
                            @Override public Observable<Integer> call(Integer quantity)
                            {
                                if (quantity != null)
                                {
                                    return Observable.just(quantity);
                                }
                                return getProposedInitialQuantity().take(1);
                            }
                        }),
                new Func2<Integer, Integer, Integer>()
                {
                    @Override public Integer call(@Nullable Integer maxValue, @Nullable Integer quantity)
                    {
                        if (maxValue == null || quantity == null)
                        {
                            return null;
                        }
                        return Math.max(0, Math.min(maxValue, quantity));
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Integer>()
                {
                    @Override public void call(@Nullable Integer clampedQuantity)
                    {
                        usedDTO.clampedQuantity = clampedQuantity;
                        if (clampedQuantity != null)
                        {
                            boolean updateText;
                            try
                            {
                                updateText = clampedQuantity != Integer.parseInt(mQuantityEditText.getEditableText().toString());
                            }
                            catch (NumberFormatException e)
                            {
                                updateText = true;
                            }
                            if (updateText)
                            {
                                mQuantityEditText.setText(String.valueOf(clampedQuantity));
                                mQuantityEditText.setSelection(mQuantityEditText.getText().length());
                            }
                        }
                    }
                })
                .share();
    }

    @NonNull protected Observable<Integer> getProposedInitialQuantity() // It can pass null values
    {
        return Observable.combineLatest(
                getPortfolioCompactObservable(),
                getQuoteObservable(),
                new Func2<PortfolioCompactDTO, QuoteDTO, Integer>() // It can pass null values
                {
                    @Override public Integer call(@NonNull PortfolioCompactDTO portfolioCompactDTO, @NonNull QuoteDTO quoteDTO)
                    {
                        Double priceCcy = getPriceCcy(portfolioCompactDTO, quoteDTO);
                        return (priceCcy == null || priceCcy == 0) ? null : (int) Math.floor(INITIAL_VALUE / priceCcy);
                    }
                })
                .share();
    }

    @NonNull abstract protected Predicate<PositionDTO> getCloseablePositionPredicate(
            @NonNull PositionDTOList positionDTOs,
            @NonNull PortfolioId portfolioId);

    private void initSecurityRelatedInfo(@Nullable SecurityCompactDTO securityCompactDTO)
    {
        Class klass = this.getClass();

        if (securityCompactDTO != null)
        {
            setActionBarTitle(getString((klass == BuyStockFragment.class || klass == BuyFXFragment.class) ? R.string.transaction_title_buy : R.string.transaction_title_sell,
                    securityCompactDTO.getExchangeSymbol()));
        }
        else
        {
            setActionBarTitle(getString((klass == BuyStockFragment.class || klass == SellFXFragment.class) ? R.string.transaction_title_buy : R.string.transaction_title_sell,
                    getString(R.string.stock)));
        }
    }

    protected void initPortfolioRelatedInfo(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition,
            @Nullable Integer clamped)
    {
        updateDisplay();

        mTradeValueTextView.setText(getTradeValueText(portfolioCompactDTO, quoteDTO, clamped));
        mTradeSymbol.setText(portfolioCompactDTO.currencyDisplay);

        if (clamped != null)
        {
            mCashShareLeftTextView.setText(getCashShareLeft(portfolioCompactDTO, quoteDTO, closeablePosition, clamped));
        }

        updateDisplay();
    }

    protected abstract String getLabel(@NonNull QuoteDTO quoteDTO);

    @NonNull protected abstract THSignedNumber getFormattedPrice(double price);

    @Nullable protected Integer getMaxPurchasableShares(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition)
    {
        return PortfolioCompactDTOUtil.getMaxPurchasableShares(
                portfolioCompactDTO,
                quoteDTO,
                closeablePosition);
    }

    @Nullable protected Integer getMaxSellableShares(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition)
    {
        return PortfolioCompactDTOUtil.getMaxSellableShares(
                portfolioCompactDTO,
                quoteDTO,
                closeablePosition);
    }

    @Nullable protected Double getRemainingForPurchaseInPortfolioRefCcy(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO,
            int quantity)
    {
        QuoteDTO quoteInPortfolioCcy = PortfolioCompactDTOUtil.createQuoteInPortfolioRefCcy(quoteDTO, portfolioCompactDTO);
        if (quoteInPortfolioCcy != null
                && quoteInPortfolioCcy.ask != null
                && portfolioCompactDTO != null)
        {
            double available = portfolioCompactDTO.getUsableForTransactionRefCcy();
            double value = quantity * quoteInPortfolioCcy.ask;
            return available - value;
        }
        return null;
    }

    @Nullable protected Double getRemainingForShortingInPortfolioRefCcy(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO,
            int quantity)
    {
        QuoteDTO quoteInPortfolioCcy = PortfolioCompactDTOUtil.createQuoteInPortfolioRefCcy(quoteDTO, portfolioCompactDTO);
        if (quoteInPortfolioCcy != null
                && quoteInPortfolioCcy.bid != null
                && portfolioCompactDTO != null)
        {
            double available = portfolioCompactDTO.getUsableForTransactionRefCcy();
            double value = quantity * quoteInPortfolioCcy.bid;
            return available - value;
        }
        return null;
    }

    @NonNull public String getRemainingWhenBuy(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition,
            int quantity)
    {
        String cashLeftText = null;
        if (closeablePosition != null)
        {
            Integer maxPurchasableShares = getMaxPurchasableShares(portfolioCompactDTO, quoteDTO, closeablePosition);
            if (maxPurchasableShares != null && maxPurchasableShares != 0)
            {
                cashLeftText = THSignedNumber.builder(maxPurchasableShares - quantity)
                        .relevantDigitCount(1)
                        .withOutSign()
                        .build().toString();
            }
        }
        else
        {
            Double remaining = getRemainingForPurchaseInPortfolioRefCcy(portfolioCompactDTO, quoteDTO, quantity);
            if (remaining != null)
            {
                if (portfolioCompactDTO.leverage != null && portfolioCompactDTO.leverage != 0)
                {
                    remaining /= portfolioCompactDTO.leverage;
                }
                THSignedNumber thSignedNumber = THSignedMoney
                        .builder(remaining)
                        .withOutSign()
                        .currency(portfolioCompactDTO.currencyDisplay)
                        .build();
                cashLeftText = thSignedNumber.toString();
            }
        }

        if (cashLeftText == null)
        {
            cashLeftText = "0"; //getResources().getString(R.string.na);
        }

        return cashLeftText;
    }

    @NonNull public String getRemainingWhenSell(
            @NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition,
            int quantity)
    {
        String shareLeftText = null;
        if (closeablePosition != null)
        {
            Integer maxSellableShares = getMaxSellableShares(portfolioCompactDTO, quoteDTO, closeablePosition);
            if (maxSellableShares != null && maxSellableShares != 0)
            {
                shareLeftText = THSignedNumber.builder(maxSellableShares - quantity)
                        .relevantDigitCount(1)
                        .withOutSign()
                        .build().toString();
            }
        }
        else
        {
            Double remaining = getRemainingForShortingInPortfolioRefCcy(portfolioCompactDTO, quoteDTO, quantity);
            if (remaining != null)
            {
                if (portfolioCompactDTO.leverage != null && portfolioCompactDTO.leverage != 0)
                {
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

        if (shareLeftText == null)
        {
            shareLeftText = getResources().getString(R.string.na);
        }
        return shareLeftText;
    }

    public String getTradeValueText(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO,
            @Nullable Integer quantity)
    {
        String valueText = "-";
        if (portfolioCompactDTO != null && quoteDTO != null && quantity != null)
        {
            Double priceRefCcy = getPriceCcy(portfolioCompactDTO, quoteDTO);
            if (priceRefCcy != null)
            {
                double value = quantity * priceRefCcy;
                THSignedNumber thTradeValue = THSignedNumber.builder(value)
                        .withOutSign()
                        .build();
                valueText = thTradeValue.toString();
            }
        }
        return valueText;
    }

    public String getQuantityString()
    {
        return mQuantityEditText.getText().toString();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.vquantity)
    public void onQuantityClicked(View v)
    {
        mPriceSelectionMethod = AnalyticsConstants.ManualQuantityInput;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.dialog_btn_confirm)
    public void onConfirmClicked(View v)
    {
        updateConfirmButton(true);
        fireBuySellReport();
        launchBuySell();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.comments) void onCommentAreaClicked(View commentTextBox)
    {
        Bundle bundle = new Bundle();
        SecurityDiscussionEditPostFragment.putSecurityId(bundle, requisite.securityId);

        if (unSpannedComment != null)
        {
            SecurityDiscussionEditPostFragment.putComment(bundle, unSpannedComment.toString());
        }

        transactionCommentFragment = navigator.get().pushFragment(TransactionEditCommentFragment.class, bundle);
    }

    @Deprecated public void setBuySellTransactionListener(BuySellTransactionListener buySellTransactionListener)
    {
        this.buySellTransactionListener = buySellTransactionListener;
    }

    private void updateDisplay()
    {
        updateConfirmButton(false);
    }

    @Nullable public Double getProfitOrLossUsd()
    {
        return getProfitOrLossUsd(usedDTO.portfolioCompactDTO, usedDTO.quoteDTO, usedDTO.closeablePosition, usedDTO.clampedQuantity);
    }

    @Nullable protected abstract Double getProfitOrLossUsd(
            @Nullable PortfolioCompactDTO portfolioCompactDTO,
            @Nullable QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition,
            @Nullable Integer quantity);  // TODO do a getProfitOrLossPortfolioCcy

    @NonNull public abstract String getCashShareLeft(@NonNull PortfolioCompactDTO portfolioCompactDTO,
            @NonNull QuoteDTO quoteDTO,
            @Nullable PositionDTOCompact closeablePosition,
            int quantity);

    protected void updateConfirmButton(boolean forceDisable)
    {
        if (forceDisable)
        {
            mConfirm.setEnabled(false);
        }
        else
        {
            mConfirm.setEnabled(usedDTO.clampedQuantity != null && usedDTO.clampedQuantity != 0 && hasValidInfo());
        }
    }

    private boolean checkValidToTransact()
    {
        return requisite.securityId.getExchange() != null
                && requisite.securityId.getSecuritySymbol() != null;
    }

    private void launchBuySell()
    {
        if (checkValidToTransact()
                && usedDTO.quoteDTO != null
                && usedDTO.clampedQuantity != null)
        {
            TransactionFormDTO transactionFormDTO = getBuySellOrder(
                    usedDTO.quoteDTO,
                    requisite.getPortfolioIdObservable().toBlocking().first(),
                    usedDTO.clampedQuantity);
            if (transactionFormDTO != null)
            {
                unsubscribe(buySellSubscription);
                buySellSubscription = getTransactionSubscription(transactionFormDTO);
            }
            else
            {
                AlertDialogBuySellRxUtil.informBuySellOrderWasNull(getActivity())
                        .subscribe(
                                Actions.empty(),
                                new EmptyAction1<Throwable>());
            }
        }
    }

    public TransactionFormDTO getBuySellOrder(
            @NonNull QuoteDTO quoteDTO,
            @NonNull PortfolioId portfolioId,
            int quantity)
    {
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

    protected void fireBuySellReport()
    {
        analytics.fireEvent(getSharingOptionEvent());
    }

    public SharingOptionsEvent getSharingOptionEvent()
    {
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

    private ActionMode.Callback createActionModeCallBackForQuantityEditText()
    {
        //We want to disable action mode since it's irrelevant
        return new ActionMode.Callback()
        {
            @Override public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
            {
                return false;
            }

            @Override public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
            {
                return false;
            }

            @Override public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
            {
                return false;
            }

            @Override public void onDestroyActionMode(ActionMode actionMode)
            {
            }
        };
    }

    @SuppressWarnings("unused")
    @OnTextChanged(value = R.id.vquantity, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterQuantityTextChanged(Editable editable)
    {
        String stringValue = editable.toString();
        if (usedDTO.portfolioCompactDTO != null
                && usedDTO.quoteDTO != null
                && !TextUtils.isEmpty(stringValue))
        {
            try
            {
                int val = Integer.parseInt(stringValue);
                quantitySubject.onNext(val);
            }
            catch (NumberFormatException e)
            {
                Timber.e(e, "Failed to parse number: " + stringValue);
            }
        }
    }

    public void populateComment()
    {
        if (transactionCommentFragment != null)
        {
            unSpannedComment = transactionCommentFragment.getComment();

            mCommentsEditText.setText(unSpannedComment);
        }
    }

    protected class BuySellObserver implements Observer<SecurityPositionTransactionDTO>
    {
        @NonNull private final SecurityId securityId;
        @NonNull private final TransactionFormDTO transactionFormDTO;
        private final boolean isBuy;

        public BuySellObserver(@NonNull SecurityId securityId,
                @NonNull TransactionFormDTO transactionFormDTO,
                boolean isBuy)
        {
            this.securityId = securityId;
            this.transactionFormDTO = transactionFormDTO;
            this.isBuy = isBuy;
        }

        @Override public void onNext(SecurityPositionTransactionDTO securityPositionDetailDTO)
        {
            if (securityPositionDetailDTO == null)
            {
                AlertDialogBuySellRxUtil.informBuySellOrderReturnedNull(getActivity())
                        .subscribe(
                                Actions.empty(),
                                new EmptyAction1<Throwable>());
                return;
            }

            if (buySellTransactionListener != null)
            {
                buySellTransactionListener.onTransactionSuccessful(isBuy, securityPositionDetailDTO, mCommentsEditText.getText().toString());
            }

            if (shareDelegateFragment.shareTo(SocialNetworkEnum.FB))
            {
                shareFacebookClient(isBuy);
            }

            if (shareDelegateFragment.isShareToWeChat())
            {
                shareToWeChat(mCommentsEditText.getText().toString(), isBuy);
            }
        }

        @Override public void onCompleted()
        {
            updateConfirmButton(false);
            navigator.get().popFragment();
        }

        @Override public void onError(Throwable e)
        {
            onCompleted();
            Timber.e(e, "Failed to %s %s with %s", isBuy ? "buy" : "sell", securityId, transactionFormDTO);
            THException thException = new THException(e);
            THToast.show(thException);

            if (buySellTransactionListener != null)
            {
                buySellTransactionListener.onTransactionFailed(isBuy, thException);
            }
        }
    }

    protected void shareFacebookClient(boolean isBuy)
    {
        Intent shareIntent = new Intent(getActivity(), FacebookShareActivity.class);
        Bundle extras = new Bundle();
        FacebookShareActivity.setMessage(
                extras,
                String.format(
                        getString(R.string.traded_facebook_share_message),
                        mQuantityEditText.getText(),
                        usedDTO.securityCompactDTO.name,
                        SecurityCompactDTOUtil.getShortSymbol(usedDTO.securityCompactDTO),
                        getFormattedPrice(isBuy ? usedDTO.quoteDTO.ask : usedDTO.quoteDTO.bid)));
        FacebookShareActivity.setName(extras, "TradeHero");
        FacebookShareActivity.setCaption(extras, "tradehero.mobi");
        FacebookShareActivity.setDescription(
                extras,
                String.format(
                        "Follow %s on TradeHero for great stock tips!",
                        shareDelegateFragment.getUserProfileDTO().displayName));
        FacebookShareActivity.setLinkUrl(extras, "http://www.facebook.com");
        if (usedDTO.securityCompactDTO.imageBlobUrl == null)
        {
            FacebookShareActivity.setDefaultPictureUrl(extras);
        }
        else
        {
            FacebookShareActivity.setPictureUrl(extras, usedDTO.securityCompactDTO.imageBlobUrl);
        }
        shareIntent.putExtras(extras);
        startActivity(shareIntent);
    }

    protected void shareToWeChat(String commentString, boolean isTransactionTypeBuy)
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = usedDTO.securityCompactDTO.id;
        weChatDTO.type = WeChatMessageType.Trade;
        if (usedDTO.securityCompactDTO.imageBlobUrl != null && usedDTO.securityCompactDTO.imageBlobUrl.length() > 0)
        {
            weChatDTO.imageURL = usedDTO.securityCompactDTO.imageBlobUrl;
        }
        if (isTransactionTypeBuy)
        {
            weChatDTO.title = getString(R.string.buy_sell_switch_buy) + " "
                    + usedDTO.securityCompactDTO.name + " " + getString(
                    R.string.buy_sell_share_count) + " @" + usedDTO.quoteDTO.ask;
        }
        else
        {
            weChatDTO.title = getString(R.string.buy_sell_switch_sell) + " "
                    + usedDTO.securityCompactDTO.name + " " + mQuantityEditText.getText() + getString(
                    R.string.buy_sell_share_count) + " @" + usedDTO.quoteDTO.bid;
        }
        if (commentString != null && !commentString.isEmpty())
        {
            weChatDTO.title = commentString + '\n' + weChatDTO.title;
        }
        socialSharerLazy.get().share(weChatDTO)
                .subscribe(
                        new EmptyAction1<SocialShareResult>(),
                        new TimberAndToastOnErrorAction1("Failed to share to WeChat")); // TODO proper callback?
    }

    @Deprecated public interface BuySellTransactionListener
    {
        void onTransactionSuccessful(boolean isBuy, @NonNull SecurityPositionTransactionDTO securityPositionTransactionDTO, String commentString);

        void onTransactionFailed(boolean isBuy, THException error);
    }

    public static class Requisite
    {
        private static final String KEY_SECURITY_ID = Requisite.class.getName() + ".security_id";
        private static final String KEY_PORTFOLIO_ID = Requisite.class.getName() + ".portfolio_id";
        private static final String KEY_QUOTE_DTO = Requisite.class.getName() + ".quote_dto";
        private static final String KEY_QUANTITY = Requisite.class.getName() + ".quantity";

        @NonNull public final SecurityId securityId;
        @NonNull public final QuoteDTO quoteDTO;
        @Nullable public final Integer quantity;
        @NonNull private final BehaviorSubject<PortfolioId> portfolioIdSubject;

        public Requisite(@NonNull SecurityId securityId,
                @NonNull PortfolioId portfolioId,
                @NonNull QuoteDTO quoteDTO,
                @Nullable Integer quantity)
        {
            this.securityId = securityId;
            this.quoteDTO = quoteDTO;
            this.quantity = quantity;
            this.portfolioIdSubject = BehaviorSubject.create(portfolioId);
        }

        public Requisite(@NonNull Bundle args)
        {
            Bundle securityArgs = args.getBundle(KEY_SECURITY_ID);
            if (securityArgs != null)
            {
                this.securityId = new SecurityId(securityArgs);
            }
            else
            {
                throw new NullPointerException("SecurityId cannot be null");
            }
            Bundle portfolioArgs = args.getBundle(KEY_PORTFOLIO_ID);
            if (portfolioArgs != null)
            {
                this.portfolioIdSubject = BehaviorSubject.create(new PortfolioId(portfolioArgs));
            }
            else
            {
                throw new NullPointerException("PortfolioId cannot be null");
            }
            Bundle quoteArgs = args.getBundle(KEY_QUOTE_DTO);
            if (quoteArgs != null)
            {
                this.quoteDTO = new QuoteDTO(quoteArgs);
            }
            else
            {
                throw new NullPointerException("Quote cannot be null");
            }
            if (args.containsKey(KEY_QUANTITY))
            {
                this.quantity = args.getInt(KEY_QUANTITY);
            }
            else
            {
                this.quantity = null;
            }
        }

        @NonNull public Bundle getArgs()
        {
            Bundle args = new Bundle();
            populate(args);
            return args;
        }

        protected void populate(@NonNull Bundle args)
        {
            args.putBundle(KEY_SECURITY_ID, securityId.getArgs());
            args.putBundle(KEY_PORTFOLIO_ID, portfolioIdSubject.toBlocking().first().getArgs());
            args.putBundle(KEY_QUOTE_DTO, quoteDTO.getArgs());
            if (quantity != null)
            {
                args.putInt(KEY_QUANTITY, quantity);
            }
            else
            {
                args.remove(KEY_QUANTITY);
            }
        }

        @NonNull public Observable<PortfolioId> getPortfolioIdObservable()
        {
            return portfolioIdSubject.distinctUntilChanged().asObservable();
        }
    }

    public static class UsedDTO
    {
        @Nullable public SecurityCompactDTO securityCompactDTO;
        @Nullable public PortfolioCompactDTO portfolioCompactDTO;
        @Nullable public QuoteDTO quoteDTO;
        @Nullable public PositionDTOCompact closeablePosition;
        @Nullable public Integer clampedQuantity;
    }
}

