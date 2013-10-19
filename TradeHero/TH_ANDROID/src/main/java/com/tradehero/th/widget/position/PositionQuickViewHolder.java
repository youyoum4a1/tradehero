package com.tradehero.th.widget.position;

import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.FlipAlphaTransformation;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.utils.ColorUtils;

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
            if (positionProfitIndicatorLeft != null)
            {
                picasso.get()
                        .load(R.drawable.edit_button_bg)
                        .transform(new FlipAlphaTransformation())
                        .into(positionProfitIndicatorLeft);
            }

            moreInfoIndicator = (ImageView) view.findViewById(R.id.ic_more_info);
        }

        displayMoreInfoIndicator();
    }

    @Override public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        super.linkWith(positionDTO, andDisplay);
        if (andDisplay)
        {
            displayPositionProfitIndicatorLeft();
        }
    }

    @Override public void display()
    {
        super.display();
        displayPositionProfitIndicatorLeft();
        displayMoreInfoIndicator();
    }

    public void displayPositionProfitIndicatorLeft()
    {
        if (positionProfitIndicatorLeft != null)
        {
            // TODO
            if (positionDTO != null)
            {
                Double roiSinceInception = positionDTO.getROISinceInception();
                if (roiSinceInception == null)
                {
                    positionProfitIndicatorLeft.setBackgroundColor(context.getResources().getColor(R.color.gray_2));
                }
                else
                {
                    positionProfitIndicatorLeft.setBackgroundColor(
                            ColorUtils.getColorForPercentage((float) roiSinceInception.doubleValue() * PERCENT_STRETCHING_FOR_COLOR));
                }
            }
        }
    }

    public void displayMoreInfoIndicator()
    {
        if (moreInfoIndicator != null)
        {
            moreInfoIndicator.setVisibility(View.VISIBLE);
        }
    }
}
