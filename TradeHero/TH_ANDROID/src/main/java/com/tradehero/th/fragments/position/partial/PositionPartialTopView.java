package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.api.position.PositionInPeriodDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.security.SecurityIdCache;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.THSignedNumber;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

public class PositionPartialTopView extends LinearLayout
{
    @Inject protected Lazy<Picasso> picasso;
    @Inject protected Lazy<SecurityIdCache> securityIdCache;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected PositionDTOUtils positionDTOUtils;

    @InjectView(R.id.stock_logo) protected ImageView stockLogo;
    @InjectView(R.id.stock_symbol) protected TextView stockSymbol;
    @InjectView(R.id.company_name) protected TextView companyName;
    @InjectView(R.id.stock_movement_indicator) protected TextView stockMovementIndicator;
    @InjectView(R.id.stock_last_price) protected TextView stockLastPrice;
    @InjectView(R.id.ic_market_close) protected ImageView marketClose;
    @InjectView(R.id.position_percentage) protected TextView positionPercent;
    @InjectView(R.id.position_last_amount_header) protected TextView positionLastAmountHeader;
    @InjectView(R.id.position_last_amount) protected TextView positionLastAmount;
    @InjectView(R.id.btn_trade_history) protected View tradeHistoryButton;

    protected SecurityId securityId;
    protected SecurityCompactDTO securityCompactDTO;
    protected PositionDTO positionDTO;

    private DTOCacheNew.Listener<SecurityId, SecurityCompactDTO> securityCompactCacheFetchListener;

    //<editor-fold desc="Constructors">
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
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
        initViews();
        securityCompactCacheFetchListener = createSecurityCompactCacheListener();
    }

    protected void initViews()
    {
        if (stockLogo != null)
        {
            stockLogo.setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (securityCompactCacheFetchListener == null)
        {
            securityCompactCacheFetchListener = createSecurityCompactCacheListener();
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (tradeHistoryButton != null)
        {
            tradeHistoryButton.setOnTouchListener(null);
        }
        tradeHistoryButton = null;

        detachSecurityCompactCache();
        securityCompactCacheFetchListener = null;
        if (stockLogo != null)
        {
            stockLogo.setImageDrawable(null);
        }
        super.onDetachedFromWindow();
    }

    protected void detachSecurityCompactCache()
    {
        securityCompactCache.get().unregister(securityCompactCacheFetchListener);
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        if (andDisplay)
        {
            displayPositionPercent();
            displayPositionLastAmountHeader();
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
            detachSecurityCompactCache();
            securityCompactCache.get().register(securityId, securityCompactCacheFetchListener);
            securityCompactCache.get().getOrFetchAsync(securityId);
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

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayStockLogo();
        displayStockSymbol();
        displayCompanyName();
        displayStockMovementIndicator();
        displayStockLastPrice();
        displayMarketClose();

        displayPositionPercent();
        displayPositionLastAmountHeader();
        displayPositionLastAmount();
    }

    public void displayStockLogo()
    {
        if (stockLogo != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.imageBlobUrl != null)
            {
                picasso.get()
                        .load(securityCompactDTO.imageBlobUrl)
                        .transform(new WhiteToTransparentTransformation())
                        .into(stockLogo, new Callback()
                        {
                            @Override public void onSuccess()
                            {
                            }

                            @Override public void onError()
                            {
                                displayStockLogoExchange();
                            }
                        });
            }
            else
            {
                displayStockLogoExchange();
            }
        }
    }

    public void displayStockLogoExchange()
    {
        if (securityCompactDTO != null)
        {
            picasso.get()

                    .load(securityCompactDTO.getExchangeLogoId())
                    .into(stockLogo);
        }
        else
        {
            stockLogo.setImageResource(R.drawable.default_image);
        }
    }

    public void displayStockSymbol()
    {
        if (stockSymbol != null)
        {
            if (securityId != null)
            {
                stockSymbol.setText(String.format("%s:%s", securityId.getExchange(), securityId.getSecuritySymbol()));
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
                if (securityCompactDTO.pc50DMA == null)
                {
                    stockMovementIndicator.setText(R.string.na);
                    return;
                }
                else if (securityCompactDTO.pc50DMA > 0)
                {
                    stockMovementIndicator.setText(R.string.arrow_prefix_positive);
                }
                else if (securityCompactDTO.pc50DMA < 0)
                {
                    stockMovementIndicator.setText(R.string.arrow_prefix_negative);
                }
                stockMovementIndicator.setTextColor(ColorUtils.getProperColorForNumber(securityCompactDTO.pc50DMA / 5));
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

                if (securityCompactDTO.marketOpen == null || securityCompactDTO.marketOpen)
                {
                    stockLastPrice.setTextColor(getResources().getColor(R.color.exchange_symbol));
                }
                else
                {
                    stockLastPrice.setTextColor(getResources().getColor(android.R.color.darker_gray));
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
                marketClose.setVisibility(securityCompactDTO.marketOpen == null || securityCompactDTO.marketOpen ? View.INVISIBLE : View.VISIBLE);
            }
        }
    }

    public void displayPositionPercent()
    {
        if (positionPercent != null)
        {
            if (positionDTO instanceof PositionInPeriodDTO && ((PositionInPeriodDTO) positionDTO).isProperInPeriod())
            {
                positionDTOUtils.setROIInPeriod(positionPercent, (PositionInPeriodDTO) positionDTO);
            }
            else
            {
                positionDTOUtils.setROISinceInception(positionPercent, positionDTO);
            }
        }
    }

    public void displayPositionLastAmountHeader()
    {
        if (positionLastAmountHeader != null)
        {
            Boolean isOpen = positionDTO == null ? null : positionDTO.isOpen();
            if (isOpen == null || isOpen)
            {
                positionLastAmountHeader.setVisibility(GONE);
            }
            else
            {
                positionLastAmountHeader.setVisibility(VISIBLE);
            }
        }
    }

    public void displayPositionLastAmount()
    {
        if (positionLastAmount != null)
        {
            THSignedNumber number = null;
            if (positionDTO != null)
            {
                Boolean closed = positionDTO.isClosed();
                if (closed != null && closed && positionDTO.realizedPLRefCcy != null)
                {
                    number = new THSignedNumber(
                            THSignedNumber.TYPE_MONEY,
                            positionDTO.realizedPLRefCcy,
                            THSignedNumber.WITH_SIGN,
                            /*portfolioDTO*/positionDTO.getNiceCurrency(),
                            THSignedNumber.TYPE_SIGN_MINUS_ONLY
                            );
                }
                else if (closed != null && !closed)
                {
                    number = new THSignedNumber(
                            THSignedNumber.TYPE_MONEY,
                            positionDTO.marketValueRefCcy,
                            THSignedNumber.WITH_SIGN,
                            /*portfolioDTO*/positionDTO.getNiceCurrency(),
                            THSignedNumber.TYPE_SIGN_MINUS_ONLY
                    );
                }

            }

            if (number == null)
            {
                positionLastAmount.setText(R.string.na);
            }
            else
            {
                positionLastAmount.setText(number.toString());
            }
        }
    }
    //</editor-fold>

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
                Timber.e("Error fetching the security %s", key, error);
            }
        };
    }

    public View getTradeHistoryButton()
    {
        return tradeHistoryButton;
    }

}
