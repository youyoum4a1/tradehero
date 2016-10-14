package com.androidth.general.fragments.security;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.alert.AlertCompactDTO;
import com.androidth.general.api.market.Exchange;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.watchlist.WatchlistPositionDTOList;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.number.THSignedMoney;
import com.androidth.general.models.number.THSignedPercentage;
import com.androidth.general.utils.DateUtils;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

public class SecurityItemView extends RelativeLayout
        implements DTOView<SecurityCompactDTO>
{
    @Inject protected Picasso picasso;
    @Bind(R.id.stock_logo) ImageView stockLogo;
    @Bind(R.id.ic_market_close) @Nullable ImageView marketCloseIcon;
    @Bind(R.id.stock_name) TextView stockName;
    @Bind(R.id.exchange_symbol) TextView exchangeSymbol;
    @Bind(R.id.last_price) TextView lastPrice;
    @Bind(R.id.tv_stock_roi) @Nullable TextView stockRoi;
    @Bind(R.id.country_logo) @Nullable ImageView countryLogo;
    @Bind(R.id.date) @Nullable TextView date;
    @Bind(R.id.sec_type) @Nullable TextView securityType;

    protected SecurityCompactDTO securityCompactDTO;
    protected Map<SecurityId, AlertCompactDTO> alerts;
    protected WatchlistPositionDTOList watchlist;

    //<editor-fold desc="Constructors">
    public SecurityItemView(Context context)
    {
        super(context);
    }

    public SecurityItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SecurityItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    protected void init()
    {
        HierarchyInjector.inject(this);
        ButterKnife.bind(this);
        stockLogo.setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        loadImage();
    }

    @Override protected void onDetachedFromWindow()
    {
        clearHandler();

        if (stockLogo != null)
        {
            stockLogo.setImageDrawable(null);
        }
        if (countryLogo != null)
        {
            countryLogo.setImageDrawable(null);
        }

        super.onDetachedFromWindow();
    }

    protected void clearHandler()
    {
        Handler handler = getHandler();
        if (handler != null)
        {
            handler.removeCallbacks(null);
        }
    }

    public boolean isMyUrlOk()
    {
        return (securityCompactDTO != null) && isUrlOk(securityCompactDTO.imageBlobUrl);
    }

    public static boolean isUrlOk(String url)
    {
        return (url != null) && (!url.isEmpty());
    }

    @Override public void display(final SecurityCompactDTO securityCompactDTO)
    {
        this.securityCompactDTO = securityCompactDTO;
//
//        Log.d("SecurityItemView.java",
//                "display: -> symbol_ay: " + securityCompactDTO.symbol_ay
//                        + "  | id_ay: " + securityCompactDTO.id_ay
//                        + " | name: " + securityCompactDTO.name
//                        + " | id: " + securityCompactDTO.id
//                        + " | symbol: " + securityCompactDTO.symbol
//                        + " | isCFD: " + securityCompactDTO.isCFD
//        );


        displayStockName();
        displayExchangeSymbol();
        displayDate();
        displayLastPrice();
        displayStockRoi();
        displayIcon();
        displaySecurityType();
        displayCountryLogo();
        loadImage();
    }

    public void display(@Nullable Map<SecurityId, AlertCompactDTO> alerts,
            @Nullable WatchlistPositionDTOList watchlist)
    {
        this.alerts = alerts;
        this.watchlist = watchlist;
        displayIcon();
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayStockName();
        displayExchangeSymbol();
        displayDate();
        displayLastPrice();
        displayStockRoi();
        displayIcon();
        displaySecurityType();
        displayCountryLogo();
        loadImage();
    }

    public void displayStockName()
    {
        if (stockName != null)
        {
            if (securityCompactDTO != null)
            {
                stockName.setText(securityCompactDTO.name);
            }
            else
            {
                stockName.setText(R.string.na);
            }
        }
    }

    public void displayExchangeSymbol()
    {
        if (exchangeSymbol != null)
        {
            if (securityCompactDTO != null)
            {
                exchangeSymbol.setText(securityCompactDTO.getExchangeSymbol());
            }
            else
            {
                exchangeSymbol.setText(R.string.na);
            }
            exchangeSymbol.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    public void displayDate()
    {
        if (date != null)
        {
            if (securityCompactDTO != null)
            {
                if (securityCompactDTO.lastPriceDateAndTimeUtc != null)
                {
                    date.setText(DateUtils.getFormattedUtcDate(getResources(),
                            securityCompactDTO.lastPriceDateAndTimeUtc));
                }
                if (securityCompactDTO.marketOpen != null)
                {
                    date.setTextColor(getResources().getColor(
                            securityCompactDTO.marketOpen ? R.color.text_primary : R.color.text_secondary));
                }
            }
            else
            {
                date.setText(R.string.na);
            }
        }
    }

    public void displayLastPrice()
    {
        if (lastPrice != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.lastPrice != null && !Double.isNaN(
                    securityCompactDTO.lastPrice))
            {
                THSignedMoney.builder(securityCompactDTO.lastPrice)
                        .signTypeArrow()
                        .withValueColor(R.color.text_primary)
                        .withCurrencyColor(R.color.text_primary)
                        .currency(securityCompactDTO.currencyDisplay)
                        .withDefaultColor()
                        .withOutSign()
                        .boldValue()
                        .build()
                        .into(lastPrice);
            }
            else
            {
                lastPrice.setText(R.string.na);
            }
        }
    }

    public void displayIcon()
    {
        if (marketCloseIcon != null && securityCompactDTO != null && securityCompactDTO.marketOpen != null)
        {
            marketCloseIcon.setImageResource(
                    securityCompactDTO.marketOpen
                            ? R.drawable.icn_market_open
                            : R.drawable.icn_market_closed);
        }
    }

    public void displaySecurityType()
    {
        if (securityType != null)
        {
            if (this.securityCompactDTO != null && this.securityCompactDTO.getSecurityTypeStringResourceId() != null)
            {
                securityType.setText(securityCompactDTO.getSecurityTypeStringResourceId());
            }
            else
            {
                securityType.setText(R.string.na);
            }
        }
    }

    public void displayCountryLogo()
    {
        if (countryLogo != null)
        {
            try
            {
                if (securityCompactDTO != null)
                {
                    countryLogo.setImageResource(securityCompactDTO.getExchangeLogoId());
                }
                else
                {
                    countryLogo.setImageResource(R.drawable.default_image);
                }
            } catch (OutOfMemoryError e)
            {
                Timber.e(e, "");
            }
        }
    }

    private void displayStockRoi()
    {
        if (stockRoi != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.risePercent != null)
            {
                double roi = securityCompactDTO.risePercent;
                THSignedPercentage
                        .builder(roi * 100)
                        .withSign()
                        .relevantDigitCount(3)
                        .withDefaultColor()
                        .defaultColorForBackground()
                        .signTypeArrow()
                        .build()
                        .into(stockRoi);

                if(roi<0.00){
                    stockRoi.setBackgroundResource(R.drawable.round_label_down);
                }else if(roi>0.00){
                    stockRoi.setBackgroundResource(R.drawable.round_label_up);
                }else{
                    stockRoi.setBackgroundResource(R.drawable.round_label_zero);
                }
            }
            else
            {
                stockRoi.setVisibility(View.GONE);
            }
        }
    }
    //</editor-fold>

    private void resetImage()
    {
        if (stockLogo != null)
        {
            stockLogo.setImageBitmap(null);
        }
    }

    public void loadImage()
    {
        resetImage();

        if (isMyUrlOk())
        {
            picasso.load(securityCompactDTO.imageBlobUrl)
                    .into(stockLogo);
        }
        else
        {
            loadExchangeImage();
        }
    }

    public void loadExchangeImage()
    {
        if (securityCompactDTO != null && securityCompactDTO.exchange != null)
        {
            try
            {
                Exchange exchange = Exchange.valueOf(securityCompactDTO.exchange);
                stockLogo.setImageResource(exchange.logoId);
            } catch (IllegalArgumentException e)
            {
                Timber.e("Unknown Exchange %s", securityCompactDTO.exchange, e);
                loadDefaultImage();
            }
        }
        else
        {
            loadDefaultImage();
        }
    }

    public void loadDefaultImage()
    {
        if (stockLogo != null)
        {
            stockLogo.setVisibility(View.VISIBLE);
            stockLogo.setImageResource(R.drawable.default_image);
        }
    }
}
