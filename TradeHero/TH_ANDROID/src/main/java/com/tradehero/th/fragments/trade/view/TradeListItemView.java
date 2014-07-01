package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.squareup.picasso.Picasso;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.fragments.trade.TradeListItemAdapter;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.models.trade.TradeDTOUtils;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.trade.TradeCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

public class TradeListItemView extends LinearLayout implements DTOView<TradeListItemAdapter.ExpandableTradeItem>
{
    private TradeListItemAdapter.ExpandableTradeItem tradeItem;
    private TradeDTO trade;
    private PositionDTO position;
    private boolean prettyDate = true;

    @Inject Lazy<TradeCache> tradeCache;
    @Inject Lazy<Picasso> picasso;
    @Inject TradeDTOUtils tradeDTOUtils;
    @Inject Lazy<PositionDTOUtils> positionDTOUtils;
    @Inject PrettyTime prettyTime;

    // all the 3 caches below are needed to get the security currencyDisplay display
    // 1) use the position cache to get the the PositionDTO containing the securityId (type SecurityIntegerId)
    // 2) in securityIdCache lookup the SecurityId (exchange + symbol) corresponding to the SecurityIntegerId
    // 3) in securityCache get the SecurityCompactDTO
    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<SecurityCompactCache> securityCache;

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
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(TradeListItemAdapter.ExpandableTradeItem expandableItem)
    {
        linkWith(expandableItem, true);
    }

    public void linkWith(TradeListItemAdapter.ExpandableTradeItem item, boolean andDisplay)
    {
        this.tradeItem = item;
        if (this.tradeItem != null)
        {
            this.position = positionCache.get().get(tradeItem.getModel().positionDTOKey);
            this.trade = tradeCache.get().get(tradeItem.getModel().ownedTradeId);
        }
        else
        {
            this.position = null;
            this.trade = null;
        }

        if (andDisplay)
        {
            display();
        }
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
            THSignedNumber tradeValue = new THSignedNumber(
                    THSignedNumber.TYPE_MONEY,
                    trade.unitPrice*trade.exchangeRate,
                    THSignedNumber.WITHOUT_SIGN,
                    getCurrencyDisplay());
            return getContext().getString(
                    textResId,
                    Math.abs(trade.quantity),
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
            return getContext().getString(
                tradeItem.isLastTrade() ? R.string.trade_holding_quantity_verbose : R.string.trade_held_quantity_verbose,
                trade.quantityAfterTrade);
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

    @NotNull
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM d HH:mm");
                sdf.setTimeZone(TimeZone.getDefault());
                return sdf.format(trade.dateTime);
            }
        }
        else
        {
            return "";
        }
    }

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
        if (this.unrealisedPLContainer != null && tradeItem != null && position != null)
        {
            this.unrealisedPLContainer.setVisibility((tradeItem.isLastTrade() && position.isOpen()) ? VISIBLE : GONE);
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
            if (tradeItem.isLastTrade() && position.isOpen())
            {
                positionDTOUtils.get().setUnrealizedPLLook(unrealizedPLValue, position);
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
            tradeDTOUtils.setRealizedPLLook(realisedPLValue, trade, position.getNiceCurrency());
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
            THSignedNumber tradeValue = new THSignedNumber(
                    THSignedNumber.TYPE_MONEY,
                    trade.quantity * trade.unitPrice * trade.exchangeRate,
                    THSignedNumber.WITHOUT_SIGN,
                    getCurrencyDisplay());
            //return String.format("%s %s", getCurrencyDisplay(), tradeValue.toString());
            return String.format("%s",tradeValue.toString());
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
            this.commentSection.setVisibility( trade.commentText == null ? GONE : VISIBLE);
        }
    }

    private void displayCommentText()
    {
        if (this.commentText != null && trade != null)
        {
            this.commentText.setText(trade.commentText);
        }
    }

    @NotNull
    private String getCurrencyDisplay()
    {
        if (position == null)
        {
            return "null";
        }
        return position.getNiceCurrency();
    }

    private double getNumberToDisplay()
    {
        if (tradeItem == null || position == null)
        {
            return 0;
        }
        else if (tradeItem.isLastTrade() && !position.isClosed())
        {
           return position.unrealizedPLRefCcy;
        }
        else
        {
            return trade.realizedPLAfterTradeRefCcy;
        }
    }
}
