package com.tradehero.th.fragments.position;

import android.content.Context;
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
    }

    @Override public void display(SecurityId securityId)
    {
        WatchlistPositionDTO watchlistPositionDTO = watchlistPositionCache.get().get(securityId);

        if (watchlistPositionDTO == null)
        {
            return;
        }

        SecurityCompactDTO securityCompactDTO = watchlistPositionDTO.securityDTO;

        displayStockLogo(securityCompactDTO);

        displayExchangeSymbol(securityCompactDTO);

        displayCompanyName(securityCompactDTO);
    }

    private void displayCompanyName(SecurityCompactDTO securityCompactDTO)
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

    private void displayStockLogo(SecurityCompactDTO securityCompactDTO)
    {
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

    private void displayExchangeSymbol(SecurityCompactDTO securityCompactDTO)
    {
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
