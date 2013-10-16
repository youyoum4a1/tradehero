package com.tradehero.th.widget.position;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 11:53 AM To change this template use File | Settings | File Templates. */
public class PositionQuickInnerViewHolder
{
    private Context context;
    private ImageView stockLogo;
    private TextView stockSymbol;
    private TextView companyName;
    private TextView stockMovementIndicator;
    private TextView currencyDisplay;
    private TextView stockLastPrice;
    private ImageView marketClose;
    private TextView positionProfitIndicator;
    private TextView positionCurrencyDisplay;
    private TextView positionLastAmount;
    private ImageButton tradeHistoryButton;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;

    @Inject Lazy<SecurityCompactCache> securityCompactCache;

    public PositionQuickInnerViewHolder(Context context)
    {
        super();
        this.context = context;
        DaggerUtils.inject(this);
    }

    public void initViews(View view)
    {
        if (view != null)
        {
            stockLogo = (ImageView) view.findViewById(R.id.stock_logo);
            stockSymbol = (TextView) view.findViewById(R.id.stock_symbol);
            companyName = (TextView) view.findViewById(R.id.company_name);
            stockMovementIndicator = (TextView) view.findViewById(R.id.stock_movement_indicator);
            currencyDisplay = (TextView) view.findViewById(R.id.currency_display);
            stockLastPrice = (TextView) view.findViewById(R.id.stock_last_price);
            marketClose = (ImageView) view.findViewById(R.id.ic_market_close);
            positionProfitIndicator = (TextView) view.findViewById(R.id.position_profit_indicator);
            positionCurrencyDisplay = (TextView) view.findViewById(R.id.position_currency_display);
            positionLastAmount = (TextView) view.findViewById(R.id.position_last_amount);

            tradeHistoryButton = (ImageButton) view.findViewById(R.id.btn_trade_history);
            if (tradeHistoryButton != null)
            {
                tradeHistoryButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override public void onClick(View clickedView)
                    {
                        handleTradeHistoryButtonClicked(clickedView);
                    }
                });
            }
        }
    }

    public void destroyViews()
    {
        if (tradeHistoryButton != null)
        {
            tradeHistoryButton.setOnClickListener(null);
        }
    }

    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;

        SecurityCompactDTO cachedSecurityCompactDTO = securityCompactCache.get().get(securityId);
        if (cachedSecurityCompactDTO == null)
        {
            // TODO query cache for security position detail DTO
        }
        else
        {
            linkWith(cachedSecurityCompactDTO, andDisplay);
        }

        if (andDisplay)
        {
            displayStockSymbol();
        }
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;
        if (andDisplay)
        {
            displayStockLogo();
            displayCompanyName();
            // TODO more
        }
    }

    public void display()
    {
        displayStockLogo();
        displayStockSymbol();
        displayCompanyName();
        // TODO more
    }

    public void displayStockLogo()
    {
        if (stockLogo != null)
        {
            if (securityCompactDTO != null)
            {
                Picasso.with(context)
                        .load(securityCompactDTO.imageBlobUrl)
                        .transform(new WhiteToTransparentTransformation())
                        .into(stockLogo);
            }
            else
            {
                stockLogo.setImageResource(R.drawable.default_image);
            }
        }
    }

    public void displayStockSymbol()
    {
        if (stockSymbol != null)
        {
            if (securityId != null)
            {
                stockSymbol.setText(String.format("%s:%s", securityId.exchange, securityId.securitySymbol));
            }
            else
            {
                stockSymbol.setText("");
            }
        }
    }

    public void displayCompanyName()
    {
        if (companyName != null)
        {
            if (securityCompactDTO != null)
            {
                companyName.setText(securityCompactDTO.name);
            }
            else
            {
                companyName.setText("");
            }
        }
    }

    protected void handleTradeHistoryButtonClicked(View view)
    {
        // TODO
    }
}
