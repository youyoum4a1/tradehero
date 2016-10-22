package com.androidth.general.fragments.trending.filter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.R;
import com.androidth.general.api.DTOView;
import com.androidth.general.api.security.SecurityTypeDTO;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.models.market.ExchangeCompactSpinnerDTO;

public class TrendingFilterSpinnerItemShortView extends LinearLayout
        implements DTOView<DTO>
{
    @Bind(R.id.trending_filter_spinner_item_label) TextView label;

    @Nullable private DTO compactSpinnerDTO;

    //<editor-fold desc="Constructors">
    public TrendingFilterSpinnerItemShortView(Context context)
    {
        super(context);
    }

    public TrendingFilterSpinnerItemShortView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TrendingFilterSpinnerItemShortView(Context context, AttributeSet attrs, int defStyle)
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
        }
    }

    public void displayText()
    {
        if (label != null)
        {
            if (compactSpinnerDTO != null)
            {
                if(compactSpinnerDTO instanceof ExchangeCompactSpinnerDTO){
                    if (compactSpinnerDTO != null) {
                        label.setText(((ExchangeCompactSpinnerDTO)compactSpinnerDTO).name);
                    }
                }else if(compactSpinnerDTO instanceof SecurityTypeDTO){
                    label.setText(((SecurityTypeDTO)compactSpinnerDTO).name);
                }else{
                    label.setText(compactSpinnerDTO.toString());
                }
            }
            else
            {
                label.setText(R.string.na);
            }
        }
    }
}
