package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionDTOKey;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.fragments.trade.TradeListItemAdapter;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.persistence.position.PositionCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.rx.EmptyAction1;
import dagger.Lazy;
import java.text.DateFormat;
import java.util.TimeZone;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class TradeListItemView extends LinearLayout
        implements DTOView<TradeListItemAdapter.ExpandableTradeItem>
{
    private TradeListItemAdapter.ExpandableTradeItem tradeItem;
    @Nullable private TradeDTO trade;
    @Nullable private PositionDTO position;
    @Nullable private Subscription positionSubscription;
    private boolean prettyDate = true;
    @Nullable private String strDisplay;

    @Inject PrettyTime prettyTime;

    // all the 3 caches below are needed to get the security currencyDisplay display
    // 1) use the position cache to get the the PositionDTO containing the securityId (type SecurityIntegerId)
    // 2) in securityIdCache lookup the SecurityId (exchange + symbol) corresponding to the SecurityIntegerId
    // 3) in securityCache get the SecurityCompactDTO
    @Inject Lazy<PositionCacheRx> positionCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<SecurityCompactCacheRx> securityCache;

    @InjectView(R.id.ic_position_profit_indicator_left) protected ColorIndicator profitIndicatorView;
    @InjectView(R.id.trade_date_label) protected TextView dateTextView;
    @InjectView(R.id.traded_quantity_verbose) protected TextView tradedQuantityVerbose;
    @InjectView(R.id.holding_quantity_verbose) protected TextView holdingQuantityVerbose;
    @InjectView(R.id.trade_avg_price) protected TextView averagePriceTextView;
    @InjectView(R.id.realised_pl_value_header) protected TextView realisedPLValueHeader;
    @InjectView(R.id.realised_pl_value) protected TextView realisedPLValue;
    @InjectView(R.id.unrealised_pl_container) protected View unrealisedPLContainer;
    @InjectView(R.id.unrealised_pl_value_header) protected TextView unrealisedPLValueHeader;
    @InjectView(R.id.unrealised_pl_value) protected TextView unrealizedPLValue;
    @InjectView(R.id.trade_value_header) protected TextView tradeValueHeader;
    @InjectView(R.id.trade_value) protected TextView tradeValue;
    @InjectView(R.id.trade_list_comment_section) protected View commentSection;
    @InjectView(R.id.trade_list_comment) protected TextView commentText;

    //<editor-fold desc="Constructors">
    public TradeListItemView(Context context)
    {
        super(context);
    }

    public TradeListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TradeListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        detachPositionSubscription();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    protected void detachPositionSubscription()
    {
        Subscription copy = positionSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        positionSubscription = null;
    }

    @Override public void display(TradeListItemAdapter.ExpandableTradeItem expandableItem)
    {
        linkWith(expandableItem);
    }

    public void linkWith(TradeListItemAdapter.ExpandableTradeItem item)
    {
        this.tradeItem = item;
        if (this.tradeItem != null)
        {
            this.trade = tradeItem.getModel().tradeDTO;
            detachPositionSubscription();
            positionSubscription = positionCache.get().get(tradeItem.getModel().positionDTOKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(new Func1<Pair<PositionDTOKey, PositionDTO>, Observable<? extends Pair<SecurityIntegerId, SecurityId>>>()
                    {
                        @Override public Observable<? extends Pair<SecurityIntegerId, SecurityId>> call(Pair<PositionDTOKey, PositionDTO> pair1)
                        {
                            position = pair1.second;
                            display();
                            return securityIdCache.get().get(pair1.second.getSecurityIntegerId());
                        }
                    })
                    .flatMap(new Func1<Pair<SecurityIntegerId, SecurityId>, Observable<? extends Pair<SecurityId, SecurityCompactDTO>>>()
                    {
                        @Override public Observable<? extends Pair<SecurityId, SecurityCompactDTO>> call(
                                Pair<SecurityIntegerId, SecurityId> pair2)
                        {
                            return securityCache.get().get(pair2.second);
                        }
                    })
                    .subscribe(
                            new Action1<Pair<SecurityId, SecurityCompactDTO>>()
                            {
                                @Override public void call(Pair<SecurityId, SecurityCompactDTO> pair)
                                {
                                    strDisplay = pair.second.currencyDisplay;
                                    display();
                                }
                            },
                            new EmptyAction1<Throwable>());
        }
        else
        {
            this.position = null;
            this.trade = null;
        }

        display();
    }

    public void display()
    {
        if (trade == null)
        {
            return;
        }

        displayTopSection();
        displayExpandableSection();
    }

    private void displayTopSection()
    {
        if (this.profitIndicatorView != null)
        {
            this.profitIndicatorView.linkWith(getNumberToDisplay());
        }

        displayTradeBoughtText();
        displayTradeDate();
        displayHoldingQuantity();
    }

    private void displayTradeBoughtText()
    {
        if (tradedQuantityVerbose != null)
        {
            tradedQuantityVerbose.setText(getTradeBoughtText());
        }
    }

    protected String getTradeBoughtText()
    {
        if (trade != null && position != null)
        {
            int textResId = trade.quantity >= 0 ? R.string.trade_bought_quantity_verbose : R.string.trade_sold_quantity_verbose;
            THSignedNumber tradeQuantity = THSignedNumber.builder((double) Math.abs(trade.quantity))
                    .withOutSign()
                    .build();
            THSignedNumber tradeValue = THSignedMoney.builder(trade.unitPriceRefCcy)
                    .withOutSign()
                    .currency(getCurrencyDisplay())
                    .build();
            return getContext().getString(
                    textResId,
                    tradeQuantity.toString(),
                    tradeValue.toString());
        }
        else
        {
            return getContext().getString(R.string.na);
        }
    }

    private void displayHoldingQuantity()
    {
        if (this.holdingQuantityVerbose != null)
        {
            this.holdingQuantityVerbose.setText(getHoldingQuantityText());
        }
    }

    protected String getHoldingQuantityText()
    {
        if (trade != null)
        {
            THSignedNumber tradeQuantityAfterTrade = THSignedNumber
                    .builder((double) Math.abs(trade.quantityAfterTrade))
                    .withOutSign()
                    .build();
            return getContext().getString(
                    tradeItem.isLastTrade() ? R.string.trade_holding_quantity_verbose : R.string.trade_held_quantity_verbose,
                    tradeQuantityAfterTrade.toString());
        }
        else
        {
            return "";
        }
    }

    private void displayTradeDate()
    {
        if (dateTextView != null)
        {
            dateTextView.setText(getTradeDateText());
        }
    }

    @NonNull
    protected String getTradeDateText()
    {
        if (trade != null && trade.dateTime != null)
        {
            if (prettyDate)
            {
                return prettyTime.format(trade.dateTime);
            }
            else
            {
                DateFormat sdf = DateFormat.getDateTimeInstance();
                sdf.setTimeZone(TimeZone.getDefault());
                return sdf.format(trade.dateTime);
            }
        }
        else
        {
            return "";
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.trade_date_label)
    protected void toggleTradeDateLook(View view)
    {
        prettyDate = !prettyDate;
        displayTradeDate();
    }

    private void displayExpandableSection()
    {
        displayAveragePrice();
        displayUnrealisedPLContainer();
        displayUnrealisedPLValueHeader();
        displayUnrealisedPLValue();
        displayRealisedPLValueHeader();
        displayRealisedPLValue();
        displayTradeValue();
        displayCommentSection();
        displayCommentText();
    }

    private void displayAveragePrice()
    {
        if (this.averagePriceTextView != null && trade != null && position != null)
        {
            String avgPriceString = String.format("%s %,.2f", position.getNiceCurrency(), trade.averagePriceAfterTradeRefCcy);
            this.averagePriceTextView.setText(avgPriceString);
        }
    }

    private void displayUnrealisedPLContainer()
    {
        if (this.unrealisedPLContainer != null
                && tradeItem != null
                && position != null)
        {
            Boolean isOpen = position.isOpen();
            this.unrealisedPLContainer.setVisibility((tradeItem.isLastTrade() && isOpen != null && isOpen) ? VISIBLE : GONE);
        }
    }

    private void displayUnrealisedPLValueHeader()
    {
        if (unrealisedPLValueHeader != null)
        {
            if (position != null && position.unrealizedPLRefCcy != null && position.unrealizedPLRefCcy < 0)
            {
                unrealisedPLValueHeader.setText(R.string.position_unrealised_loss_header);
            }
            else
            {
                unrealisedPLValueHeader.setText(R.string.position_unrealised_profit_header);
            }
        }
    }

    private void displayUnrealisedPLValue()
    {
        if (this.unrealizedPLValue != null && tradeItem != null && position != null)
        {
            Boolean isOpen = position.isOpen();
            if (tradeItem.isLastTrade() && isOpen != null && isOpen)
            {
                PositionDTOUtils.setUnrealizedPLLook(unrealizedPLValue, position);
            }
            else
            {
                this.unrealizedPLValue.setText(R.string.na);
            }
        }
    }

    private void displayRealisedPLValueHeader()
    {
        if (realisedPLValueHeader != null)
        {
            if (trade != null && trade.realizedPLAfterTradeRefCcy < 0)
            {
                realisedPLValueHeader.setText(R.string.position_realised_loss_header);
            }
            else
            {
                realisedPLValueHeader.setText(R.string.position_realised_profit_header);
            }
        }
    }

    private void displayRealisedPLValue()
    {
        if (this.realisedPLValue != null && trade != null && position != null)
        {
            THSignedMoney
                    .builder(trade.realizedPLAfterTradeRefCcy)
                    .withOutSign()
                    .currency(position.getNiceCurrency())
                    .build()
                    .into(realisedPLValue);
        }
    }

    private void displayTradeValue()
    {
        if (tradeValue != null)
        {
            tradeValue.setText(getTradeValueText());
        }
    }

    protected String getTradeValueText()
    {
        if (trade != null)
        {
            THSignedNumber tradeValue = THSignedMoney
                    .builder(trade.quantity * trade.unitPriceRefCcy)
                    .withOutSign()
                    .currency(getCurrencyDisplay())
                    .build();
            return tradeValue.toString();
        }
        else
        {
            return "";
        }
    }

    private void displayCommentSection()
    {
        if (this.commentSection != null && trade != null)
        {
            this.commentSection.setVisibility(trade.commentText == null ? GONE : VISIBLE);
        }
    }

    private void displayCommentText()
    {
        if (this.commentText != null && trade != null)
        {
            this.commentText.setText(trade.commentText);
        }
    }

    @NonNull
    private String getCurrencyDisplay()
    {
        if (strDisplay != null)
        {
            return strDisplay;
        }
        if (position == null)
        {
            return "null";
        }
        return position.getNiceCurrency();
    }

    @Nullable private Double getNumberToDisplay()
    {
        if (tradeItem == null || position == null)
        {
            return null;
        }
        Boolean isClosed = position.isClosed();
        if (tradeItem.isLastTrade() && isClosed != null && !isClosed)
        {
            return position.unrealizedPLRefCcy;
        }
        else if (trade != null)
        {
            return trade.realizedPLAfterTradeRefCcy;
        }
        return null;
    }
}
