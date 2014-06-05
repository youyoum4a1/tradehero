package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.utils.THSignedNumber;

abstract public class AbstractPositionPartialBottomOpenView<
        PositionDTOType extends PositionDTO,
        ExpandableListItemType extends ExpandableListItem<PositionDTOType>
        >
        extends AbstractPartialBottomView<PositionDTOType, ExpandableListItemType>
{
    @InjectView(R.id.unrealised_pl_value) protected TextView unrealisedPLValue;
    @InjectView(R.id.realised_pl_value) protected TextView realisedPLValue;
    @InjectView(R.id.total_invested_value) protected TextView totalInvestedValue;
    @InjectView(R.id.market_value_value) protected TextView marketValueValue;
    @InjectView(R.id.quantity_value) protected TextView quantityValue;
    @InjectView(R.id.average_price_value) protected TextView averagePriceValue;

    //<editor-fold desc="Constructors">
    public AbstractPositionPartialBottomOpenView(Context context)
    {
        super(context);
    }

    public AbstractPositionPartialBottomOpenView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AbstractPositionPartialBottomOpenView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void linkWith(PositionDTOType positionDTO, boolean andDisplay)
    {
        super.linkWith(positionDTO, andDisplay);
        if (andDisplay)
        {
            displayUnrealisedPLValue();
            displayRealisedPLValue();
            displayTotalInvested();
            displayMarketValue();
            displayQuantityValue();
            displayAveragePriceValue();
        }
    }

    @Override public void displayModelPart()
    {
        super.displayModelPart();
        displayUnrealisedPLValue();
        displayRealisedPLValue();
        displayTotalInvested();
        displayMarketValue();
        displayQuantityValue();
        displayAveragePriceValue();
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

    public void displayMarketValue()
    {
        if (marketValueValue != null)
        {
            if (positionDTO != null)
            {
                marketValueValue.setText(positionUtils.getMarketValue(getContext(), positionDTO));
            }
        }
    }

    public void displayQuantityValue()
    {
        if (quantityValue != null)
        {
            if (positionDTO != null && positionDTO.shares != null)
            {
                quantityValue.setText(String.format("%,d", positionDTO.shares));
            }
            else
            {
                quantityValue.setText(R.string.na);
            }
        }
    }

    public void displayAveragePriceValue()
    {
        if (averagePriceValue != null)
        {
            if (positionDTO != null && positionDTO.averagePriceRefCcy != null)
            {
                THSignedNumber ThAveragePriceRefCcy = new THSignedNumber(THSignedNumber.TYPE_MONEY, positionDTO.averagePriceRefCcy, false, positionDTO.getNiceCurrency());
                averagePriceValue.setText(ThAveragePriceRefCcy.toString());
            }
            else
            {
                averagePriceValue.setText(R.string.na);
            }
        }
    }
}
