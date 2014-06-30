package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.thm.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import org.jetbrains.annotations.Nullable;

public class TrendingFilterSpinnerItemView extends RelativeLayout
    implements DTOView<ExchangeCompactSpinnerDTO>
{
    @InjectView(R.id.trending_filter_spinner_item_label) TextView label;
    @InjectView(R.id.trending_filter_spinner_item_icon) ImageView icon;

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
        ButterKnife.inject(this);
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
                label.setText(exchangeCompactSpinnerDTO.toString());
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
                }
                else
                {
                    icon.setImageResource(R.drawable.default_image);
                }
            }
            else
            {
                icon.setImageResource(R.drawable.default_image);
            }
        }
    }
}
