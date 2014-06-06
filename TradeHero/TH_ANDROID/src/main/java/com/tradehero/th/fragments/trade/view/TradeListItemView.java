package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.fragments.trade.TradeListItemAdapter;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.models.trade.TradeDTOUtils;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.trade.TradeCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.inject.Inject;

public class TradeListItemView extends LinearLayout implements DTOView<TradeListItemAdapter.ExpandableTradeItem>
{
    private TradeListItemAdapter.ExpandableTradeItem tradeItem;
    private TradeDTO trade;
    private PositionDTO position;

    private String currencyDisplay; //cached value - cleared in onDetach

    @Inject Lazy<TradeCache> tradeCache;
    @Inject Lazy<Picasso> picasso;
    @Inject TradeDTOUtils tradeDTOUtils;
    @Inject Lazy<PositionDTOUtils> positionDTOUtils;

    // all the 3 caches below are needed to get the security currencyDisplay display
    // 1) use the position cache to get the the PositionDTO containing the securityId (type SecurityIntegerId)
    // 2) in securityIdCache lookup the SecurityId (exchange + symbol) corresponding to the SecurityIntegerId
    // 3) in securityCache get the SecurityCompactDTO
    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<SecurityCompactCache> securityCache;

    @InjectView(R.id.ic_position_profit_indicator_left) protected ColorIndicator profitIndicatorView;
    @InjectView(R.id.trade_date_label) protected TextView dateTextView;
    @InjectView(R.id.trade_quantity_label) protected TextView tradeQuantityHeader;
    @InjectView(R.id.trade_avg_price) protected TextView averagePriceTextView;
    @InjectView(R.id.realised_pl_value_header) protected TextView realisedPLValueHeader;
    @InjectView(R.id.realised_pl_value) protected TextView realisedPLValue;
    @InjectView(R.id.unrealised_pl_container) protected View unrealisedPLContainer;
    @InjectView(R.id.unrealised_pl_value_header) protected TextView unrealisedPLValueHeader;
    @InjectView(R.id.unrealised_pl_value) protected TextView unrealizedPLValue;
    @InjectView(R.id.trade_quantity) protected TextView tradeQuantityValue;
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

    @Override protected void onDetachedFromWindow()
    {
        this.currencyDisplay = null;
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

        if (this.tradeQuantityHeader != null && trade != null)
        {
            String quantityString = String.format("%+,d @ %s %,.2f", trade.quantity, getSecurityCurrencyDisplay(), trade.unitPrice);
            this.tradeQuantityHeader.setText(quantityString);
        }

        if (this.dateTextView != null && trade != null && trade.dateTime != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM H:m z");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

            String dateString = sdf.format(trade.dateTime);
            dateTextView.setText(dateString);
        }
    }

    private void displayExpandableSection()
    {
        displayAveragePrice();
        displayUnrealisedPLContainer();
        displayUnrealisedPLValueHeader();
        displayUnrealisedPLValue();
        displayRealisedPLValueHeader();
        displayRealisedPLValue();
        displayTradeQuantity();
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

    private void displayTradeQuantity()
    {
        if (this.tradeQuantityValue != null && trade != null)
        {
            String quantityAfterTradeString = String.format("%,d", trade.quantityAfterTrade);
            this.tradeQuantityValue.setText(quantityAfterTradeString);
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

    private String getSecurityCurrencyDisplay()
    {
        if (currencyDisplay == null)
        {
            if (position == null)
            {
                return null;
            }

            SecurityId securityId = securityIdCache.get().get(position.getSecurityIntegerId());
            if (securityId == null)
            {
                return null;
            }

            SecurityCompactDTO security = securityCache.get().get(securityId);
            if (security == null)
            {
                return null;
            }

            currencyDisplay = security.currencyDisplay;
        }
        return currencyDisplay;
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
