package com.tradehero.th.widget.position;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 10/16/13 Time: 12:05 PM To change this template use File | Settings | File Templates. */
public class PositionQuickViewHolder extends PositionQuickInnerViewHolder
{
    private ImageView positionProfitIndicatorLeft;
    private ImageView moreInfoIndicator;

    public PositionQuickViewHolder()
    {
        super();
    }

    @Override public void initViews(View view)
    {
        super.initViews(view);

        if (view != null)
        {
            positionProfitIndicatorLeft = (ImageView) view.findViewById(R.id.ic_position_profit_indicator_left);
            moreInfoIndicator = (ImageView) view.findViewById(R.id.ic_more_info);
        }
    }

    @Override public void display()
    {
        super.display();
        displayPositionProfitIndicatorLeft();
    }

    public void displayPositionProfitIndicatorLeft()
    {
        if (positionProfitIndicatorLeft != null)
        {
            // TODO
        }
    }
}
