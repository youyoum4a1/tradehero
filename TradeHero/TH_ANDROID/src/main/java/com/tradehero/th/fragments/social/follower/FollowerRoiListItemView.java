package com.tradehero.th.fragments.social.follower;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;

public class FollowerRoiListItemView extends FollowerListItemView
{
    @InjectView(R.id.follower_roi_info) TextView roiInfo;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public FollowerRoiListItemView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public FollowerRoiListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public FollowerRoiListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    //<editor-fold desc="Display Methods">
    @Override public void display()
    {
        super.display();
        displayRoiInfo();
    }

    public void displayRoiInfo()
    {
        if (roiInfo != null)
        {
            if (userFollowerDTO != null)
            {
                THSignedPercentage
                        .builder(userFollowerDTO.roiSinceInception * 100)
                        .withDefaultColor()
                        .build()
                        .into(roiInfo);
            }
            else
            {
                roiInfo.setText(R.string.na);
            }
        }
    }
    //</editor-fold>
}
