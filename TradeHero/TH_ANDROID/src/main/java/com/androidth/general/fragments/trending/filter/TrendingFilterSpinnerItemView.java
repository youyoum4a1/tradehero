package com.androidth.general.fragments.trending.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import android.support.annotation.Nullable;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.security.SecurityTypeDTO;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.models.market.ExchangeCompactSpinnerDTO;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class TrendingFilterSpinnerItemView extends LinearLayout
    implements DTOView<DTO>
{
    @Bind(R.id.trending_filter_spinner_item_label) TextView label;
    @Nullable @Bind(R.id.trending_filter_spinner_item_icon) ImageView icon;

    @Nullable private DTO compactSpinnerDTO;

    //<editor-fold desc="Constructors">
    public TrendingFilterSpinnerItemView(Context context)
    {
        super(context);
    }

    public TrendingFilterSpinnerItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TrendingFilterSpinnerItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override public void display(DTO dto)
    {
        linkWith(dto, true);
    }

    public void linkWith(@Nullable DTO dto, boolean andDisplay)
    {
        this.compactSpinnerDTO = dto;
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
            if (compactSpinnerDTO != null)
            {
                if(compactSpinnerDTO instanceof ExchangeCompactSpinnerDTO){
                    if (((ExchangeCompactSpinnerDTO) compactSpinnerDTO).desc != null)
                    {
                        label.setText(((ExchangeCompactSpinnerDTO) compactSpinnerDTO).desc);
                    }
                    else
                    {
                        label.setText(compactSpinnerDTO.toString());
                    }
                }else if(compactSpinnerDTO instanceof SecurityTypeDTO){
                    if (((SecurityTypeDTO) compactSpinnerDTO).name != null)
                    {
                        label.setText(((SecurityTypeDTO) compactSpinnerDTO).name);
                    }
                    else
                    {
                        label.setText(compactSpinnerDTO.toString());
                    }
                }else{
                    label.setText(R.string.na);
                }

            }else{
                label.setText(R.string.na);
            }
        }
    }

    public void displayIcon()
    {
        if (icon != null)
        {
            if(compactSpinnerDTO instanceof ExchangeCompactSpinnerDTO){
                if (compactSpinnerDTO != null)
                {
                    Drawable flagDrawable = ((ExchangeCompactSpinnerDTO) compactSpinnerDTO).getFlagDrawable();
                    if (flagDrawable != null)
                    {
                        icon.setImageDrawable(flagDrawable);
                        return;
                    }
                }else{
                    icon.setImageResource(R.drawable.default_image);
                }

            }else if(compactSpinnerDTO instanceof SecurityTypeDTO){
                Picasso.with(getContext()).load(((SecurityTypeDTO) compactSpinnerDTO).getImageUrl()).into(icon);

            }else{
                icon.setImageResource(R.drawable.default_image);
            }
        }
    }
}
