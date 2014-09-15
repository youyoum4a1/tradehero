package com.tradehero.th.fragments.location;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import com.tradehero.th2.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.Country;

class LocationViewHolder implements DTOView<ListedLocationDTO>
{
    @InjectView(R.id.country_logo) protected ImageView logo;
    @InjectView(R.id.country_code) protected TextView code;
    @InjectView(R.id.country_name) protected TextView name;
    @InjectView(R.id.location_tick_is_current) protected View currentView;

    protected ListedLocationDTO listedLocationDTO;
    protected Country currentCountry;

    @Override public void display(ListedLocationDTO dto)
    {
        this.listedLocationDTO = dto;
        display();
    }

    public void setCurrentCountry(Country currentCountry)
    {
        this.currentCountry = currentCountry;
        display();
    }

    protected void display()
    {
        if (logo != null)
        {
            if (listedLocationDTO != null)
            {
                logo.setImageResource(listedLocationDTO.country.logoId);
            }
            else
            {
                logo.setImageResource(R.drawable.default_image);
            }
        }

        if (code != null)
        {
            if (listedLocationDTO != null)
            {
                code.setText(listedLocationDTO.country.name());
            }
            else
            {
                code.setText(R.string.na);
            }
        }

        if (name != null)
        {
            if (listedLocationDTO != null)
            {
                name.setText(listedLocationDTO.country.locationName);
            }
            else
            {
                name.setText(R.string.na);
            }
        }

        if (currentView != null)
        {

            currentView.setVisibility(isCurrentCountry() ? View.VISIBLE : View.GONE);
        }
    }

    protected boolean isCurrentCountry()
    {
        return currentCountry != null
                && listedLocationDTO != null
                && listedLocationDTO.country.equals(currentCountry);
    }
}
