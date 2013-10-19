package com.tradehero.th.widget.position;

import android.view.View;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.graphics.FlipAlphaTransformation;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 10/19/13 Time: 11:46 AM To change this template use File | Settings | File Templates. */
public class PositionLongViewHolder extends PositionLongInnerViewHolder
{
    public static final String TAG = PositionLongViewHolder.class.getSimpleName();

    private ImageView positionProfitIndicatorLeft;
    private ImageView moreInfoIndicator;

    public PositionLongViewHolder()
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
    }
}
