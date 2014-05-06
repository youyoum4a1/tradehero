package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.PositionUtils;
import javax.inject.Inject;

public class PositionLockedView extends LinearLayout
{
    private ColorIndicator colorIndicator;
    private TextView positionPercent;
    private TextView unrealisedPLValue;
    private TextView realisedPLValue;
    private TextView totalInvestedValue;

    private PositionDTO positionDTO;

    @Inject protected PositionUtils positionUtils;

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
        DaggerUtils.inject(this);
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
            if (positionDTO != null)
            {
                unrealisedPLValue.setText(positionUtils.getUnrealizedPL(getContext(), positionDTO));
            }
        }
    }

    public void displayRealisedPLValue()
    {
        if (realisedPLValue != null)
        {
            if (positionDTO != null)
            {
                realisedPLValue.setText(positionUtils.getRealizedPL(getContext(), positionDTO));
            }
        }
    }

    public void displayTotalInvested()
    {
        if (totalInvestedValue != null)
        {
            if (positionDTO != null)
            {
                totalInvestedValue.setText(positionUtils.getSumInvested(getContext(), positionDTO));
            }
        }
    }

    public void displayPositionPercent()
    {
        if (positionPercent != null)
        {
            positionUtils.setROISinceInception(positionPercent, positionDTO);
        }
    }
}
