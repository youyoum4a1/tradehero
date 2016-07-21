package com.androidth.general.fragments.trending.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
import android.support.annotation.Nullable;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.models.market.ExchangeCompactSpinnerDTO;

public class TrendingFilterSpinnerItemView extends LinearLayout
    implements DTOView<ExchangeCompactSpinnerDTO>
{
    @BindView(R.id.trending_filter_spinner_item_label) TextView label;
    @Nullable @BindView(R.id.trending_filter_spinner_item_icon) ImageView icon;

    @Nullable private ExchangeCompactSpinnerDTO exchangeCompactSpinnerDTO;

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

    @Override public void display(ExchangeCompactSpinnerDTO dto)
    {
        linkWith(dto, true);
    }

    public void linkWith(@Nullable ExchangeCompactSpinnerDTO dto, boolean andDisplay)
    {
        this.exchangeCompactSpinnerDTO = dto;
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
            if (exchangeCompactSpinnerDTO != null)
            {
                if (exchangeCompactSpinnerDTO.desc != null)
                {
                    label.setText(exchangeCompactSpinnerDTO.desc);
                }
                else
                {
                    label.setText(exchangeCompactSpinnerDTO.toString());
                }
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
            if (exchangeCompactSpinnerDTO != null)
            {
                Drawable flagDrawable = exchangeCompactSpinnerDTO.getFlagDrawable();
                if (flagDrawable != null)
                {
                    icon.setImageDrawable(flagDrawable);
                    return;
                }
            }
            icon.setImageResource(R.drawable.default_image);
        }
    }
}
