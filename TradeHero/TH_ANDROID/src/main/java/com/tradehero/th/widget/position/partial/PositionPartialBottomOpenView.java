package com.tradehero.th.widget.position.partial;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.PositionUtils;
import com.tradehero.th.utils.SecurityUtils;
import dagger.Lazy;

import javax.inject.Inject;

/**
 * Created by julien on 30/10/13
 */
public class PositionPartialBottomOpenView extends RelativeLayout

{
    public static final String TAG = PositionPartialBottomOpenView.class.getSimpleName();

    private OwnedPositionId ownedPositionId;
    private PositionDTO positionDTO;

    @Inject Lazy<PositionCache> positionCache;

    private TextView unrealisedPLValue;
    private TextView realisedPLValue;
    private TextView totalInvestedValue;
    private TextView marketValueValue;
    private TextView quantityValue;
    private TextView averagePriceValue;

    public PositionPartialBottomOpenView(Context context)
    {
        super(context);
    }

    public PositionPartialBottomOpenView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionPartialBottomOpenView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    protected void initViews()
    {
        unrealisedPLValue = (TextView) findViewById(R.id.unrealised_pl_value);
        realisedPLValue = (TextView) findViewById(R.id.realised_pl_value);
        totalInvestedValue = (TextView) findViewById(R.id.total_invested_value);
        marketValueValue = (TextView) findViewById(R.id.market_value_value);
        quantityValue = (TextView) findViewById(R.id.quantity_value);
        averagePriceValue = (TextView) findViewById(R.id.average_price_value);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        initViews();
    }

    public void linkWith(OwnedPositionId ownedPositionId, boolean andDisplay)
    {
        this.ownedPositionId = ownedPositionId;

        linkWith(positionCache.get().get(this.ownedPositionId), andDisplay);
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
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


    public void display()
    {
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
            if (positionDTO != null && positionDTO.unrealizedPLRefCcy != null)
            {
                unrealisedPLValue.setText(NumberDisplayUtils.formatWithRelevantDigits(
                        positionDTO.unrealizedPLRefCcy,
                        4,
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY));
            }
            else
            {
                unrealisedPLValue.setText(R.string.na);
            }
        }
    }

    public void displayRealisedPLValue()
    {
        if (realisedPLValue != null)
        {
             realisedPLValue.setText(PositionUtils.getRealizedPL(getContext(), positionDTO));
        }
    }

    public void displayTotalInvested()
    {
        if (totalInvestedValue != null)
        {
            totalInvestedValue.setText(PositionUtils.getSumInvested(getContext(), positionDTO));
        }
    }

    public void displayMarketValue()
    {
        if (marketValueValue != null)
        {
            marketValueValue.setText(PositionUtils.getMarketValue(getContext(),positionDTO));
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
                averagePriceValue.setText(NumberDisplayUtils.formatWithRelevantDigits(
                        positionDTO.averagePriceRefCcy,
                        4,
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY));
            }
            else
            {
                averagePriceValue.setText(R.string.na);
            }
        }
    }
}
