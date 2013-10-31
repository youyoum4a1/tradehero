package com.tradehero.th.widget.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;

import javax.inject.Inject;

/**
 * Created by julien on 30/10/13
 */
public class PositionPartialTopView extends LinearLayout
{
    public static final String TAG = PositionPartialTopView.class.getSimpleName();

    protected static final int PERCENT_STRETCHING_FOR_COLOR = 20;

    @Inject protected Context context;

    private ImageView stockLogo;
    private TextView stockSymbol;
    private TextView companyName;
    private TextView stockMovementIndicator;
    private TextView stockLastPrice;
    private ImageView marketClose;
    private TextView positionPercent;
    private TextView positionLastAmount;

    private ImageButton tradeHistoryButton;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    @Inject Lazy<SecurityIdCache> securityIdCache;
    @Inject Lazy<SecurityCompactCache> securityCompactCache;
    private SecurityCompactCache.Listener<SecurityId, SecurityCompactDTO> securityCompactCacheListener;
    private DTOCache.GetOrFetchTask<SecurityCompactDTO> securityCompactCacheFetchTask;

    protected OwnedPositionId ownedPositionId;
    protected PositionDTO positionDTO;
    @Inject Lazy<PositionCache> filedPositionCache;

    @Inject protected Lazy<Picasso> picasso;

    public PositionPartialTopView(Context context)
    {
        super(context);
    }

    public PositionPartialTopView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialTopView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        initViews();
    }

    protected void initViews()
    {
        stockLogo = (ImageView) findViewById(R.id.stock_logo);
        stockSymbol = (TextView) findViewById(R.id.stock_symbol);
        companyName = (TextView) findViewById(R.id.company_name);
        stockMovementIndicator = (TextView) findViewById(R.id.stock_movement_indicator);
        stockLastPrice = (TextView) findViewById(R.id.stock_last_price);
        marketClose = (ImageView) findViewById(R.id.ic_market_close);
        positionPercent = (TextView) findViewById(R.id.position_percentage);
        positionLastAmount = (TextView) findViewById(R.id.position_last_amount);
        tradeHistoryButton = (ImageButton) findViewById(R.id.btn_trade_history);
    }

    public void destroyViews()
    {
        if (tradeHistoryButton != null)
        {
            tradeHistoryButton.setOnTouchListener(null);
        }
        securityCompactCacheListener = null;
        if (securityCompactCacheFetchTask != null)
        {
            securityCompactCacheFetchTask.forgetListener(true);
        }
        securityCompactCacheFetchTask = null;
    }

    public void linkWith(OwnedPositionId ownedPositionId, boolean andDisplay)
    {
        this.ownedPositionId = ownedPositionId;

        linkWith(filedPositionCache.get().get(this.ownedPositionId), andDisplay);

        if (andDisplay)
        {
            //TODO
        }
    }

    protected void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        if (andDisplay)
        {
            displayPositionPercent();
            displayPositionLastAmount();
        }
        if (positionDTO != null)
        {
            linkWith(securityIdCache.get().get(positionDTO.getSecurityIntegerId()), andDisplay);
        }
    }

    protected void linkWith(SecurityId securityId, boolean andDisplay)
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
                securityCompactCacheFetchTask.forgetListener(true);
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

    protected void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
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

    public void display()
    {
        displayStockLogo();
        displayStockSymbol();
        displayCompanyName();
        displayStockMovementIndicator();
        displayStockLastPrice();
        displayMarketClose();

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
                    positionPercent.setText(String.format("%+,.2f%%", roiSinceInception * 100.0));
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
                if (positionDTO.isClosed())
                {
                    positionLastAmount.setText(String.format("P&L %s",
                            NumberDisplayUtils.formatWithRelevantDigits(positionDTO.realizedPLRefCcy, 3)));
                }
                else
                {
                    positionLastAmount.setText(String.format("%s %s",
                            SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY,
                            NumberDisplayUtils.formatWithRelevantDigits(positionDTO.marketValueRefCcy, 3)));
                }

            }
        }
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

            @Override public void onErrorThrown(SecurityId key, Throwable error)
            {
                THToast.show("There was an error when fetching the security information");
                THLog.e(TAG, "Error fetching the security " + key, error);
            }
        };
    }


    public ImageButton getTradeHistoryButton()
    {
        return tradeHistoryButton;
    }
}
