package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.models.position.PositionDTOUtils;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

public class PositionLockedView extends LinearLayout
{
    @InjectView(R.id.color_indicator) protected ColorIndicator colorIndicator;
    @InjectView(R.id.position_percentage) protected TextView positionPercent;
    @InjectView(R.id.unrealised_pl_value_header) protected TextView unrealisedPLValueHeader;
    @InjectView(R.id.unrealised_pl_value) protected TextView unrealisedPLValue;
    @InjectView(R.id.realised_pl_value_header) protected TextView realisedPLValueHeader;
    @InjectView(R.id.realised_pl_value) protected TextView realisedPLValue;
    @InjectView(R.id.total_invested_value) protected TextView totalInvestedValue;

    private PositionDTO positionDTO;

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
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
    }

    public void linkWith(PositionDTO positionDTO)
    {
        this.positionDTO = positionDTO;
        display();
    }

    public void display()
    {
        if (colorIndicator != null && positionDTO != null)
        {
            Double roi = positionDTO.getROISinceInception();
            colorIndicator.linkWith(roi);
        }
        displayUnrealisedPLValueHeader();
        displayUnrealisedPLValue();
        displayRealisedPLValueHeader();
        displayRealisedPLValue();
        displayPositionPercent();
        displayTotalInvested();
    }

    public void displayUnrealisedPLValueHeader()
    {
        if (unrealisedPLValueHeader != null)
        {
            if (positionDTO != null && positionDTO.unrealizedPLRefCcy != null && positionDTO.unrealizedPLRefCcy < 0)
            {
                unrealisedPLValueHeader.setText(R.string.position_unrealised_loss_header);
            }
            else
            {
                unrealisedPLValueHeader.setText(R.string.position_unrealised_profit_header);
            }
        }
    }

    public void displayUnrealisedPLValue()
    {
        if (unrealisedPLValue != null)
        {
            PositionDTOUtils.setUnrealizedPLLook(unrealisedPLValue, positionDTO);
        }
    }

    public void displayRealisedPLValueHeader()
    {
        if (realisedPLValueHeader != null)
        {
            if (positionDTO != null && positionDTO.unrealizedPLRefCcy != null && positionDTO.realizedPLRefCcy < 0)
            {
                realisedPLValueHeader.setText(R.string.position_realised_loss_header);
            }
            else
            {
                realisedPLValueHeader.setText(R.string.position_realised_profit_header);
            }
        }
    }

    public void displayRealisedPLValue()
    {
        if (realisedPLValue != null)
        {
            PositionDTOUtils.setRealizedPLLook(realisedPLValue, positionDTO);
        }
    }

    public void displayTotalInvested()
    {
        if (totalInvestedValue != null)
        {
            if (positionDTO != null)
            {
                totalInvestedValue.setText(PositionDTOUtils.getSumInvested(getResources(), positionDTO));
            }
        }
    }

    public void displayPositionPercent()
    {
        if (positionPercent != null)
        {
            PositionDTOUtils.setROISinceInception(positionPercent, positionDTO);
        }
    }
}
