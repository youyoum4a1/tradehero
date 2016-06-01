package com.ayondo.academy.fragments.location;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import butterknife.ButterKnife;
import com.ayondo.academy.R;
import com.ayondo.academy.api.DTOView;
import com.ayondo.academy.api.market.Country;
public class LocationRelativeView extends RelativeLayout
    implements DTOView<ListedLocationDTO>
{
    private static final int BG_REGULAR = R.drawable.basic_white_selector;
    private static final int BG_CURRENT = R.drawable.basic_green_selector;

    protected LocationViewHolder locationViewHolder;
    @Nullable protected ListedLocationDTO listedLocationDTO;
    @Nullable protected Country currentCountry;

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
        ButterKnife.bind(locationViewHolder, this);
    }

    @Override public void display(@Nullable ListedLocationDTO dto)
    {
        this.listedLocationDTO = dto;
        locationViewHolder.display(dto);
        display();
    }

    public void setCurrentCountry(@Nullable Country currentCountry)
    {
        this.currentCountry = currentCountry;
        locationViewHolder.setCurrentCountry(currentCountry);
        display();
    }

    protected void display()
    {
        setBackgroundResource(isCurrentCountry() ? BG_CURRENT : BG_REGULAR);
    }

    protected boolean isCurrentCountry()
    {
        return listedLocationDTO != null &&
                currentCountry != null &&
                listedLocationDTO.country.equals(currentCountry);
    }
}
