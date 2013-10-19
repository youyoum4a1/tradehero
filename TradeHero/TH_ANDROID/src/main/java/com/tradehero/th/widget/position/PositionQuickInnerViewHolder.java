package com.tradehero.th.widget.position;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.position.FiledPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.position.FiledPositionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 11:53 AM To change this template use File | Settings | File Templates. */
public class PositionQuickInnerViewHolder
{
    public static final String TAG = PositionQuickInnerViewHolder.class.getSimpleName();

    protected static final int PERCENT_STRETCHING_FOR_COLOR = 20;

    @Inject protected Context context;

    private ImageView stockLogo;
    private TextView stockSymbol;
    private TextView companyName;
    private TextView stockMovementIndicator;
    private TextView stockLastPrice;
    private ImageView marketClose;
    private TextView positionProfitIndicator;
    private TextView positionPercent;
    private TextView positionLastAmount;
    private ImageButton tradeHistoryButton;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    private SecurityCompactCache.Listener<SecurityId, SecurityCompactDTO> securityCompactCacheListener;
    private AsyncTask<Void, Void, SecurityCompactDTO> securityCompactCacheFetchTask;

    protected FiledPositionId filedPositionId;
    protected PositionDTO positionDTO;
    @Inject Lazy<FiledPositionCache> filedPositionCache;

    @Inject protected Lazy<Picasso> picasso;

    public PositionQuickInnerViewHolder()
    {
        super();
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
            stockLastPrice = (TextView) view.findViewById(R.id.stock_last_price);
            marketClose = (ImageView) view.findViewById(R.id.ic_market_close);
            positionProfitIndicator = (TextView) view.findViewById(R.id.position_profit_indicator);
            positionPercent = (TextView) view.findViewById(R.id.position_percentage);
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
        securityCompactCacheListener = null;
        if (securityCompactCacheFetchTask != null)
        {
            securityCompactCacheFetchTask.cancel(false);
        }
        securityCompactCacheFetchTask = null;
    }

    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;

        SecurityCompactDTO cachedSecurityCompactDTO = securityCompactCache.get().get(securityId);
        if (cachedSecurityCompactDTO == null)
        {
            if (securityCompactCacheListener == null)
            {
                securityCompactCacheListener = createSecurityCompactCacheListener();
            }
            if (securityCompactCacheFetchTask != null)
            {
                securityCompactCacheFetchTask.cancel(false);
            }
            securityCompactCacheFetchTask = securityCompactCache.get().getOrFetch(securityId, securityCompactCacheListener);
            securityCompactCacheFetchTask.execute();
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
            displayStockMovementIndicator();
            displayStockLastPrice();
            displayMarketClose();
            // TODO more
        }
    }

    public void linkWith(FiledPositionId filedPositionId, boolean andDisplay)
    {
        this.filedPositionId = filedPositionId;

        linkWith(filedPositionCache.get().get(this.filedPositionId), andDisplay);
        linkWith(securityIdCache.get().get(this.filedPositionId.getSecurityIntegerId()), andDisplay);

        if (andDisplay)
        {
            //TODO
        }
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        if (andDisplay)
        {
            displayPositionProfitIndicator();
            displayPositionPercent();
            displayPositionLastAmount();
        }
    }

    public void display()
    {
        displayStockLogo();
        displayStockSymbol();
        displayCompanyName();
        displayStockMovementIndicator();
        displayStockLastPrice();
        displayMarketClose();

        displayPositionProfitIndicator();
        displayPositionPercent();
        displayPositionLastAmount();
    }

    public void displayStockLogo()
    {
        if (stockLogo != null)
        {
            if (securityCompactDTO != null)
            {
                picasso.get()
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

    public void displayStockMovementIndicator()
    {
        if (stockMovementIndicator != null)
        {
            if (securityCompactDTO != null)
            {
                if(securityCompactDTO.pc50DMA > 0)
                {
                    stockMovementIndicator.setText(R.string.positive_prefix);
                }
                else if(securityCompactDTO.pc50DMA < 0)
                {
                    stockMovementIndicator.setText(R.string.negative_prefix);
                }
                stockMovementIndicator.setTextColor(ColorUtils.getColorForPercentage(securityCompactDTO.pc50DMA / 5));
            }
        }
    }

    public void displayStockLastPrice()
    {
        if (stockLastPrice != null)
        {
            if (securityCompactDTO != null)
            {
                if (securityCompactDTO.lastPrice != null)
                {
                    stockLastPrice.setText(String.format("%s %.2f", securityCompactDTO.currencyDisplay, securityCompactDTO.lastPrice));
                }
                else
                {
                    stockLastPrice.setText(R.string.na);
                }

                if (securityCompactDTO.marketOpen)
                {
                    stockLastPrice.setTextColor(context.getResources().getColor(R.color.exchange_symbol));
                }
                else
                {
                    stockLastPrice.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                }
            }
        }
    }

    public void displayMarketClose()
    {
        if (marketClose != null)
        {
            if (securityCompactDTO != null)
            {
                marketClose.setVisibility(securityCompactDTO.marketOpen ? View.INVISIBLE : View.VISIBLE);
            }
        }
    }

    public void displayPositionProfitIndicator()
    {
        if (positionProfitIndicator != null)
        {
            if (positionDTO != null)
            {
                Double roiSinceInception = positionDTO.getROISinceInception();
                if (roiSinceInception == null || roiSinceInception == 0)
                {
                    positionProfitIndicator.setText(R.string.na);
                    positionProfitIndicator.setTextColor(context.getResources().getColor(R.color.black));
                }
                else
                {
                    if (roiSinceInception > 0)
                    {
                        positionProfitIndicator.setText(R.string.positive_prefix);
                    }
                    else
                    {
                        positionProfitIndicator.setText(R.string.negative_prefix);
                    }
                    positionProfitIndicator.setTextColor(
                            ColorUtils.getColorForPercentage((float) roiSinceInception.doubleValue() * PERCENT_STRETCHING_FOR_COLOR));
                }
            }
        }
    }

    public void displayPositionPercent()
    {
        if (positionPercent != null)
        {
            if (positionDTO != null)
            {
                Double roiSinceInception = positionDTO.getROISinceInception();
                if (roiSinceInception == null)
                {
                    positionPercent.setText(R.string.na);
                    positionPercent.setTextColor(context.getResources().getColor(R.color.black));
                }
                else
                {
                    positionPercent.setText(String.format("%,.2f%%", Math.abs(100 * roiSinceInception)));
                    positionPercent.setTextColor(
                            ColorUtils.getColorForPercentage((float) roiSinceInception.doubleValue() * PERCENT_STRETCHING_FOR_COLOR));
                }
            }
        }
    }

    public void displayPositionLastAmount()
    {
        if (positionLastAmount != null)
        {
            if (positionDTO != null)
            {
                positionLastAmount.setText(String.format("%s %s",
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY,
                        NumberDisplayUtils.formatWithRelevantDigits(positionDTO.marketValueRefCcy, 3)));
            }
        }
    }

    public void displayTradeHistoryButton()
    {
        if (tradeHistoryButton != null)
        {
            tradeHistoryButton.setFocusable(false);
        }
    }

    protected void handleTradeHistoryButtonClicked(View view)
    {
        THToast.show("No Trade History for now");
        // TODO
    }

    private SecurityCompactCache.Listener<SecurityId, SecurityCompactDTO> createSecurityCompactCacheListener()
    {
        return new SecurityCompactCache.Listener<SecurityId, SecurityCompactDTO>()
        {
            @Override public void onDTOReceived(SecurityId key, SecurityCompactDTO value)
            {
                if (key.equals(securityId))
                {
                    linkWith(value, true);
                }
            }
        };
    }
}
