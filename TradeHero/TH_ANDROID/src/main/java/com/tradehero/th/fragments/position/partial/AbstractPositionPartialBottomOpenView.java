package com.tradehero.th.fragments.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.utils.PositionUtils;
import com.tradehero.th.utils.THSignedNumber;

/**
 * Created by julien on 30/10/13
 */
abstract public class AbstractPositionPartialBottomOpenView<
        PositionDTOType extends PositionDTO,
        ExpandableListItemType extends ExpandableListItem<PositionDTOType>
        >
        extends AbstractPartialBottomView<PositionDTOType, ExpandableListItemType>
{
    public static final String TAG = AbstractPositionPartialBottomOpenView.class.getSimpleName();

    private TextView unrealisedPLValue;
    private TextView realisedPLValue;
    private TextView totalInvestedValue;
    private TextView marketValueValue;
    private TextView quantityValue;
    private TextView averagePriceValue;

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

    @Override protected void initViews()
    {
        super.initViews();
        unrealisedPLValue = (TextView) findViewById(R.id.unrealised_pl_value);
        realisedPLValue = (TextView) findViewById(R.id.realised_pl_value);
        totalInvestedValue = (TextView) findViewById(R.id.total_invested_value);
        marketValueValue = (TextView) findViewById(R.id.market_value_value);
        quantityValue = (TextView) findViewById(R.id.quantity_value);
        averagePriceValue = (TextView) findViewById(R.id.average_price_value);
    }

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

    @Override public void linkWith(PortfolioDTO portfolioDTO, boolean andDisplay)
    {
        super.linkWith(portfolioDTO, andDisplay);
        if (andDisplay)
        {
            displayUnrealisedPLValue();
            displayRealisedPLValue();
            displayTotalInvested();
            displayMarketValue();
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

    public void displayMarketValue()
    {
        if (marketValueValue != null)
        {
            if (portfolioDTO != null)
            {
                marketValueValue.setText(PositionUtils.getMarketValue(getContext(), positionDTO, portfolioDTO.getNiceCurrency()));
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
            if (positionDTO != null && positionDTO.averagePriceRefCcy != null && portfolioDTO != null)
            {
                THSignedNumber ThAveragePriceRefCcy = new THSignedNumber(THSignedNumber.TYPE_MONEY, positionDTO.averagePriceRefCcy, false, portfolioDTO.getNiceCurrency());
                averagePriceValue.setText(ThAveragePriceRefCcy.toString());
            }
            else
            {
                averagePriceValue.setText(R.string.na);
            }
        }
    }
}
