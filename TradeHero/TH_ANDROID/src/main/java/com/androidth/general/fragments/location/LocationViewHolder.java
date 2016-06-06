package com.androidth.general.fragments.location;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.market.Country;

class LocationViewHolder implements DTOView<ListedLocationDTO>
{
    @Bind(R.id.country_logo) protected ImageView logo;
    @Bind(R.id.country_code) protected TextView code;
    @Bind(R.id.country_name) protected TextView name;
    @Bind(R.id.location_tick_is_current) protected View currentView;

    @Nullable protected ListedLocationDTO listedLocationDTO;
    @Nullable protected Country currentCountry;

    @Override public void display(@Nullable ListedLocationDTO dto)
    {
        this.listedLocationDTO = dto;
        display();
    }

    public void setCurrentCountry(@Nullable Country currentCountry)
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
