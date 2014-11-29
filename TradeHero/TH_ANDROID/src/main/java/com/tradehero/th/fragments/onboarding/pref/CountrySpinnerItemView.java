package com.tradehero.th.fragments.onboarding.pref;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.Country;

public class CountrySpinnerItemView extends RelativeLayout
    implements DTOView<Country>
{
    @InjectView(R.id.spinner_item_label) TextView label;
    @InjectView(R.id.spinner_item_icon) ImageView icon;

    @Nullable private Country country;

    //<editor-fold desc="Constructors">
    public CountrySpinnerItemView(Context context)
    {
        super(context);
    }

    public CountrySpinnerItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CountrySpinnerItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void display(Country dto)
    {
        linkWith(dto, true);
    }

    public void linkWith(@Nullable Country dto, boolean andDisplay)
    {
        this.country = dto;
        if (andDisplay)
        {
            displayText();
            displayIcon();
        }
    }

    public void displayText()
    {
        if (label != null)
        {
            if (country != null)
            {
                label.setText(country.locationName);
            }
            else
            {
                label.setText(R.string.na);
            }
        }
    }

    public void displayIcon()
    {
        if (icon != null)
        {
            if (country != null)
            {
                icon.setImageResource(country.logoId);
            }
            else
            {
                icon.setImageResource(R.drawable.default_image);
            }
        }
    }
}
