package com.tradehero.th.fragments.location;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.Country;

class LocationRelativeView extends RelativeLayout
    implements DTOView<ListedLocationDTO>
{
    protected LocationViewHolder locationViewHolder;

    //<editor-fold desc="Constructors">
    public LocationRelativeView(Context context)
    {
        super(context);
        init();
    }

    public LocationRelativeView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LocationRelativeView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    protected void init()
    {
        locationViewHolder = new LocationViewHolder();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(locationViewHolder, this);
    }

    @Override public void display(ListedLocationDTO dto)
    {
        locationViewHolder.display(dto);
    }

    public void setCurrentCountry(Country isCurrent)
    {
        locationViewHolder.setCurrentCountry(isCurrent);
    }
}
