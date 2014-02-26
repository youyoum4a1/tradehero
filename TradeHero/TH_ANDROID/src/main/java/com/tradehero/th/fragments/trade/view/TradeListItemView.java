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
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.fragments.trade.AbstractTradeListItemAdapter;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.persistence.trade.TradeCache;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import javax.inject.Inject;

/**
 * Created by julien on 23/10/13
 */
public class TradeListItemView extends LinearLayout implements DTOView<AbstractTradeListItemAdapter.ExpandableTradeItem>
{
    public static final String TAG = TradeListItemView.class.getName();

    private AbstractTradeListItemAdapter.ExpandableTradeItem tradeItem;
    private TradeDTO trade;
    private PositionDTO position;
    private PortfolioDTO portfolioDTO;

    private String currencyDisplay; //cached value - cleared in onDetach

    @Inject Lazy<TradeCache> tradeCache;
    @Inject Lazy<Picasso> picasso;

    // all the 3 caches below are needed to get the security currency display
    // 1) use the position cache to get the the PositionDTO containing the securityId (type SecurityIntegerId)
    // 2) in securityIdCache lookup the SecurityId (exchange + symbol) corresponding to the SecurityIntegerId
    // 3) in securityCache get the SecurityCompactDTO
    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<SecurityCompactCache> securityCache;

    @InjectView(R.id.ic_position_profit_indicator_left) protected ColorIndicator profitIndicatorView;
    @InjectView(R.id.trade_date_label) protected TextView dateTextView;
    @InjectView(R.id.trade_quantity_label) protected TextView quantityTextView;
    @InjectView(R.id.trade_avg_price) protected TextView averagePriceTextView;
    @InjectView(R.id.trade_realized_pl) protected TextView realizedPLTextView;
    @InjectView(R.id.trade_unrealized_pl_container) protected View unrealizedPLContainer;
    @InjectView(R.id.trade_unrealized_pl) protected TextView unrealizedPLTextView;
    @InjectView(R.id.trade_quantity) protected TextView positionQuantityTextView;
    @InjectView(R.id.trade_list_comment_section) protected View commentSection;
    @InjectView(R.id.trade_list_comment) protected TextView commentTextView;

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
        initViews();
    }

    protected void initViews()
    {
    }

    @Override protected void onDetachedFromWindow()
    {
        this.currencyDisplay = null;
        super.onDetachedFromWindow();
    }

    @Override public void display(AbstractTradeListItemAdapter.ExpandableTradeItem expandableItem)
    {
        linkWith(expandableItem, true);
    }

    public void linkWith(AbstractTradeListItemAdapter.ExpandableTradeItem item, boolean andDisplay)
    {
        this.tradeItem = item;
        if (this.tradeItem != null)
        {
            this.position = positionCache.get().get(new OwnedPositionId(tradeItem.getModel()));
            this.trade = tradeCache.get().get(tradeItem.getModel());
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

    public void linkWith(PortfolioDTO portfolioDTO, boolean andDisplay)
    {
        this.portfolioDTO = portfolioDTO;
        if (andDisplay)
        {
            displayExpandableSection();
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

        if (this.quantityTextView != null && trade != null)
        {
            String quantityString = String.format("%+,d @ %s %,.2f", trade.quantity, getSecurityCurrencyDisplay(), trade.unit_price);
            this.quantityTextView.setText(quantityString);
        }

        if (this.dateTextView != null && trade != null && trade.date_time != null)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("d MMM H:m z");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

            String dateString = sdf.format(trade.date_time);
            dateTextView.setText(dateString);
        }
    }

    private void displayExpandableSection()
    {
        if (this.averagePriceTextView != null && trade != null && portfolioDTO != null)
        {
            String avgPriceString = String.format("%s %,.2f", portfolioDTO.getNiceCurrency(), trade.average_price_after_trade);
            this.averagePriceTextView.setText(avgPriceString);
        }

        if (this.realizedPLTextView != null && trade != null && portfolioDTO != null)
        {
            String realizedPLString = String.format("%s %+,.2f", portfolioDTO.getNiceCurrency(), trade.realized_pl_after_trade);
            this.realizedPLTextView.setText(realizedPLString);
            this.realizedPLTextView.setTextColor(getResources().getColor(ColorUtils.getColorResourceForNumber(trade.realized_pl_after_trade)));
        }

        if (this.unrealizedPLContainer != null && tradeItem != null && position != null)
        {
            this.unrealizedPLContainer.setVisibility(tradeItem.isLastTrade() && position.isClosed() ? GONE : VISIBLE);
        }

        if (this.unrealizedPLTextView != null && tradeItem != null && position != null)
        {
            if (tradeItem.isLastTrade() && position.isClosed())
            {
                this.unrealizedPLTextView.setText(R.string.na);
            }
            else if (portfolioDTO != null)
            {
                String realizedPLString = String.format("%s %+,.2f", portfolioDTO.getNiceCurrency(), position.unrealizedPLRefCcy);
                this.unrealizedPLTextView.setText(realizedPLString);
                unrealizedPLTextView.setTextColor(getResources().getColor(ColorUtils.getColorResourceForNumber(position.unrealizedPLRefCcy)));
            }
        }

        if (this.positionQuantityTextView != null && trade != null)
        {
            String quantityAfterTradeString = String.format("%,d", trade.quantity_after_trade);
            this.positionQuantityTextView.setText(quantityAfterTradeString);
        }

        if (this.commentSection != null && trade != null)
        {
            this.commentSection.setVisibility( trade.commentText == null ? GONE : VISIBLE);
        }
        if (this.commentTextView != null && trade != null)
        {
            this.commentTextView.setText(trade.commentText);
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
            return trade.realized_pl_after_trade;
        }
    }
}
