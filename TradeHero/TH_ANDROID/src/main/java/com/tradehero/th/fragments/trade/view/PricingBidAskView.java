package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;

public class PricingBidAskView extends LinearLayout
{
    private TextView mLastPrice;
    private int activeColor;
    private int inactiveColor;

    private SecurityCompactDTO securityCompactDTO;
    private QuoteDTO quoteDTO;
    private PortfolioCompactDTO portfolioCompactDTO;
    private boolean buy = true;
    private boolean refreshingQuote = false;

    //<editor-fold desc="Constructors">
    public PricingBidAskView(Context context)
    {
        super(context);
        init();
    }

    public PricingBidAskView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PricingBidAskView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    //<editor-fold desc="Accessors">
    public boolean isBuy()
    {
        return buy;
    }

    public void setBuy(boolean buy)
    {
        this.buy = buy;
        updateVisibilities();
        displayLastPrice();
        displayLastPriceRefCcy();
    }

    public boolean isRefreshingQuote()
    {
        return refreshingQuote;
    }

    public void setRefreshingQuote(boolean refreshingQuote)
    {
        this.refreshingQuote = refreshingQuote;
        display();
    }
    //</editor-fold>

    private void init()
    {
        activeColor = getResources().getColor(R.color.black);
        inactiveColor = getResources().getColor(R.color.title);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initView();
    }

    private void initView()
    {
        mLastPrice = (TextView) findViewById(R.id.last_price);
        display();
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;
        if (andDisplay)
        {
            displayLastPrice();
        }
    }

    public void linkWith(QuoteDTO quoteDTO, boolean andDisplay)
    {
        this.quoteDTO = quoteDTO;
        if (andDisplay)
        {
            displayAskPrice();
            displayBidPrice();
            displayLastPrice();
            displayLastPriceRefCcy();
            updateVisibilities();
        }
    }

    public void linkWith(PortfolioCompactDTO portfolioCompactDTO, boolean andDisplay)
    {
        this.portfolioCompactDTO = portfolioCompactDTO;
        if (andDisplay)
        {
            displayLastPriceRefCcy();
        }
    }

    public void display()
    {
        displayLastPrice();
        displayLastPriceRefCcy();
        displayAskPrice();
        displayBidPrice();

        updateVisibilities();
    }

    public void displayLastPrice()
    {
        TextView   lastPriceView = mLastPrice;
        if (lastPriceView != null)
        {
            lastPriceView.setText(getLastPriceText());
        }
    }

    public String getLastPriceText()
    {
        return String.format("%s %s", getCurrencyDisplay(),  buy ? getAskPriceText() : getBidPriceText());
    }

    public void displayAskPrice()
    {
    }

    public String getAskPriceText()
    {
        if (quoteDTO == null)
        {
            return "-";
        }
        else if (quoteDTO.ask == null)
        {
            return getResources().getString(R.string.buy_sell_ask_price_not_available);
        }
        THSignedNumber thSignedNumber = THSignedNumber.builder(quoteDTO.ask)
                .withOutSign()
                .build();
        return thSignedNumber.toString();
    }

    public void displayBidPrice()
    {
    }

    public String getBidPriceText()
    {
        if (quoteDTO == null)
        {
            return "-";
        }
        else if (quoteDTO.bid == null)
        {
            return getResources().getString(R.string.buy_sell_bid_price_not_available);
        }
        THSignedNumber thSignedNumber = THSignedNumber.builder(quoteDTO.bid)
                .withOutSign()
                .build();
        return thSignedNumber.toString();
    }

    public void displayLastPriceRefCcy()
    {

    }

    public String getLastPriceRefCcyText()
    {
        if (portfolioCompactDTO == null)
        {
            return getResources().getString(R.string.na);
        }
        Double priceRefCcy = quoteDTO == null ? null : quoteDTO.getPriceRefCcy(portfolioCompactDTO, buy);
        if (priceRefCcy == null)
        {
            return "= " + portfolioCompactDTO.getNiceCurrency();
        }
        else
        {
            THSignedNumber thSignedNumber = THSignedMoney.builder(priceRefCcy)
                    .withOutSign()
                    .currency(portfolioCompactDTO.getNiceCurrency())
                    .build();
            return String.format("= %s", thSignedNumber.toString());
        }
    }

    private void updateVisibilities()
    {
    }

    public String getCurrencyDisplay()
    {
        return securityCompactDTO == null ? "-" : securityCompactDTO.getCurrencyDisplay();
    }
}
