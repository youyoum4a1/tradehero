package com.tradehero.th.fragments.position;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.WhiteToTransparentTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.persistence.watchlist.WatchlistPositionCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/10/14 Time: 4:40 PM Copyright (c) TradeHero
 */
public class WatchlistItemView extends LinearLayout implements DTOView<SecurityId>
{
    @Inject protected Lazy<WatchlistPositionCache> watchlistPositionCache;
    @Inject protected Lazy<Picasso> picasso;

    private ImageView stockLogo;
    private TextView stockSymbol;
    private TextView companyName;
    private TextView numberOfShares;
    private WatchlistPositionDTO watchlistPositionDTO;
    private TextView positionPercentage;
    private TextView positionLastAmount;

    //<editor-fold desc="Constructors">
    public WatchlistItemView(Context context)
    {
        super(context);
    }

    public WatchlistItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WatchlistItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        init();
    }

    private void init()
    {
        DaggerUtils.inject(this);
        stockLogo = (ImageView) findViewById(R.id.stock_logo);
        stockSymbol = (TextView) findViewById(R.id.stock_symbol);
        companyName = (TextView) findViewById(R.id.company_name);
        numberOfShares = (TextView) findViewById(R.id.number_of_shares);
        positionPercentage = (TextView) findViewById(R.id.position_percentage);
        positionLastAmount = (TextView) findViewById(R.id.position_last_amount);
    }

    @Override public void display(SecurityId securityId)
    {
        watchlistPositionDTO = watchlistPositionCache.get().get(securityId);

        if (watchlistPositionDTO == null)
        {
            return;
        }

        displayStockLogo();

        displayExchangeSymbol();

        displayNumberOfShares();

        displayCompanyName();

        displayPlPercentageAndLastPrice();
    }

    private void displayPlPercentageAndLastPrice()
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;

        if (securityCompactDTO != null)
        {
            Double lastPrice = securityCompactDTO.lastPrice;
            Double watchlistPrice = watchlistPositionDTO.getWatchlistPrice();
            if (lastPrice == null)
            {
                lastPrice = 0.0;
            }
            // last price
            positionLastAmount.setText(formatLastPrice(securityCompactDTO.currencyDisplay, lastPrice));

            // pl percentage
            if (watchlistPrice != 0)
            {
                double pl = (lastPrice - watchlistPrice) * 100 / watchlistPrice;
                positionPercentage.setText(String.format(getContext().getString(R.string.watchlist_pl_percentage_format), pl));

                if (pl > 0)
                {
                    positionPercentage.setTextColor(getResources().getColor(R.color.number_green));
                }
                else if (pl < 0)
                {
                    positionPercentage.setTextColor(getResources().getColor(R.color.number_red));
                }
                else
                {
                    positionPercentage.setTextColor(getResources().getColor(R.color.text_gray_normal));
                }
            }
            else
            {
                positionPercentage.setText("");
            }
        }
        else
        {
            positionPercentage.setText("");
        }
    }

    private Spanned formatLastPrice(String currencyDisplay, Double lastPrice)
    {
        return Html.fromHtml(String.format(getContext().getString(R.string.watchlist_last_price_format), currencyDisplay, lastPrice));
    }

    private void displayNumberOfShares()
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;
        if (numberOfShares != null)
        {
            if (securityCompactDTO != null)
            {
                Double watchListPrice = watchlistPositionDTO.getWatchlistPrice();
                numberOfShares.setText(formatNumberOfShares(watchlistPositionDTO.shares, securityCompactDTO.currencyDisplay, watchListPrice));
            }
            else
            {
                numberOfShares.setText("");
            }
        }
    }

    private Spanned formatNumberOfShares(Integer shares, String currencyDisplay, Double formattedPrice)
    {
        if (formattedPrice == null)
        {
            formattedPrice = 0.0;
        }
        if (shares == null)
        {
            shares = 0;
        }
        return Html.fromHtml(String.format(
                getContext().getString(R.string.watchlist_number_of_shares),
                shares, currencyDisplay, formattedPrice
        ));
    }

    private void displayCompanyName()
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;
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

    private void displayStockLogo()
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;

        if (stockLogo != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.imageBlobUrl != null)
            {
                picasso.get()
                        .load(securityCompactDTO.imageBlobUrl)
                        .transform(new WhiteToTransparentTransformation())
                        .into(stockLogo);
            }
            else if (securityCompactDTO != null)
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
    }

    private void displayExchangeSymbol()
    {
        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;

        if (stockSymbol != null)
        {
            if (securityCompactDTO != null)
            {
                stockSymbol.setText(securityCompactDTO.getExchangeSymbol());
            }
            else
            {
                stockSymbol.setText("");
            }
        }
    }
}
