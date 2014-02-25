package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.utils.PositionUtils;

/**
 * Created by julien on 31/10/13
 */
public class PositionLockedView extends LinearLayout
{
    private ColorIndicator colorIndicator;
    private TextView positionPercent;
    private TextView unrealisedPLValue;
    private TextView realisedPLValue;
    private TextView totalInvestedValue;

    private PositionDTO positionDTO;
    private PortfolioDTO portfolioDTO;

    //<editor-fold desc="Constructors">
    public PositionLockedView(Context context)
    {
        super(context);
    }

    public PositionLockedView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionLockedView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
    }

    protected void initViews()
    {
        positionPercent = (TextView) findViewById(R.id.position_percentage);
        colorIndicator = (ColorIndicator) findViewById(R.id.color_indicator);
        unrealisedPLValue = (TextView) findViewById(R.id.unrealised_pl_value);
        realisedPLValue = (TextView) findViewById(R.id.realised_pl_value);
        totalInvestedValue = (TextView) findViewById(R.id.total_invested_value);
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
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
            displayUnrealisedPLValue();
            displayRealisedPLValue();
            displayTotalInvested();
        }
    }

    public void display()
    {
        if (colorIndicator != null && positionDTO != null)
        {
            Double roi = positionDTO.getROISinceInception();
            colorIndicator.linkWith(roi);
        }
        displayRealisedPLValue();
        displayUnrealisedPLValue();
        displayPositionPercent();
        displayTotalInvested();
    }

    public void displayUnrealisedPLValue()
    {
        if (unrealisedPLValue != null)
        {
            if (portfolioDTO != null)
            {
                unrealisedPLValue.setText(PositionUtils.getUnrealizedPL(getContext(), positionDTO, portfolioDTO.getNiceCurrency()));
            }
        }
    }

    public void displayRealisedPLValue()
    {
        if (realisedPLValue != null)
        {
            if (portfolioDTO != null)
            {
                realisedPLValue.setText(PositionUtils.getRealizedPL(getContext(), positionDTO, portfolioDTO.getNiceCurrency()));
            }
        }
    }

    public void displayTotalInvested()
    {
        if (totalInvestedValue != null)
        {
            if (portfolioDTO != null)
            {
                totalInvestedValue.setText(PositionUtils.getSumInvested(getContext(), positionDTO, portfolioDTO.getNiceCurrency()));
            }
        }
    }

    public void displayPositionPercent()
    {
        if (positionPercent != null)
        {
            PositionUtils.setROISinceInception(positionPercent, positionDTO);
        }
    }
}
