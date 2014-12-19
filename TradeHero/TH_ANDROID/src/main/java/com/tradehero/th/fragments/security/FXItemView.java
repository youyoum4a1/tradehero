package com.tradehero.th.fragments.security;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.compact.FxSecurityCompactDTO;
import com.tradehero.th.api.security.key.FxPairSecurityId;
import com.tradehero.th.inject.HierarchyInjector;
import timber.log.Timber;

public class FXItemView extends RelativeLayout implements DTOView<SecurityCompactDTO>
{
    public static final float DIVISOR_PC_50_COLOR = 5f;

    @InjectView(R.id.stock_name) TextView stockName;
    @InjectView(R.id.flags_container) protected FxFlagContainer flagsContainer;
    @InjectView(R.id.buy_price) TextView buyPrice;
    @InjectView(R.id.sell_price) TextView sellPrice;
    @InjectView(R.id.ic_market_close) ImageView marketCloseIcon;
    protected SecurityCompactDTO securityCompactDTO;

    //<editor-fold desc="Constructors">
    public FXItemView(Context context)
    {
        super(context);
    }

    public FXItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FXItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        init();
    }

    protected void init()
    {
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
    }

    @Override
    public void display(final SecurityCompactDTO securityCompactDTO)
    {
        linkWith(securityCompactDTO, true);
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;
        displayFlagContainer();
        if (andDisplay)
        {
            displayStockName();
            displayMarketClose();
        }
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayStockName();
        displayMarketClose();
        displayFlagContainer();
    }

    public void displayStockName()
    {
        if (stockName != null)
        {
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                FxPairSecurityId pair = ((FxSecurityCompactDTO) securityCompactDTO).getFxPair();
                stockName.setText(String.format("%s/%s", pair.left, pair.right));
            }
            else if (securityCompactDTO != null)
            {
                stockName.setText(securityCompactDTO.symbol);
            }
            else
            {
                stockName.setText(R.string.na);
            }
        }
    }

    public void displayMarketClose()
    {
        if (securityCompactDTO == null)
        {
            // Nothing to do
        }
        else if (securityCompactDTO.marketOpen == null)
        {
            Timber.w("displayMarketClose marketOpen is null");
        }
        else if (securityCompactDTO.marketOpen)
        {
            if (marketCloseIcon != null)
            {
                marketCloseIcon.setVisibility(View.GONE);
            }
        }
        else
        {
            if (marketCloseIcon != null)
            {
                marketCloseIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    public void displayFlagContainer()
    {
        if (flagsContainer != null)
        {
            if (securityCompactDTO instanceof FxSecurityCompactDTO)
            {
                flagsContainer.display(((FxSecurityCompactDTO) securityCompactDTO).getFxPair());
            }
        }
    }
    //</editor-fold>
}
