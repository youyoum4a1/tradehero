package com.tradehero.th.widget.position;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.PositionUtils;
import com.tradehero.th.widget.position.partial.PositionPartialTopView;
import dagger.Lazy;

import javax.inject.Inject;

/**
 * Created by julien on 31/10/13
 */
public class LockedPositionItem extends LinearLayout
{
    private ColorIndicator colorIndicator;
    private TextView positionPercent;
    private TextView unrealisedPLValue;
    private TextView realisedPLValue;
    private TextView totalInvestedValue;

    private OwnedPositionId ownedPositionId;
    private PositionDTO positionDTO;

    @Inject Lazy<PositionCache> positionCache;

    public LockedPositionItem(Context context)
    {
        super(context);
    }

    public LockedPositionItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LockedPositionItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        initViews();
    }

    protected void initViews()
    {
        positionPercent = (TextView)findViewById(R.id.position_percentage);
        colorIndicator = (ColorIndicator)findViewById(R.id.color_indicator);
        unrealisedPLValue = (TextView) findViewById(R.id.unrealised_pl_value);
        realisedPLValue = (TextView)findViewById(R.id.realised_pl_value);
        totalInvestedValue = (TextView) findViewById(R.id.total_invested_value);
    }

    public void linkWith(OwnedPositionId ownedPositionId, boolean andDisplay)
    {
        this.ownedPositionId = ownedPositionId;
        this.linkWith(positionCache.get().get(ownedPositionId), andDisplay);
    }

    protected void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        if (andDisplay)
            display();
    }

    protected void display()
    {
        if (colorIndicator != null) {
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
            unrealisedPLValue.setText(PositionUtils.getUnrealizedPL(getContext(), positionDTO));
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

    public void displayPositionPercent()
    {
        if (positionPercent != null)
        {
            PositionUtils.setROISinceInception(positionPercent, positionDTO);
        }
    }

}
