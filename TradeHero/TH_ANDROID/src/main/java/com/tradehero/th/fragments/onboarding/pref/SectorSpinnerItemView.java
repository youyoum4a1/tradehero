package com.tradehero.th.fragments.onboarding.pref;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.SectorCompactDTO;
import android.support.annotation.Nullable;

public class SectorSpinnerItemView extends RelativeLayout
    implements DTOView<SectorCompactDTO>
{
    @InjectView(R.id.spinner_item_label) TextView label;

    @Nullable private SectorCompactDTO sectorCompactDTO;

    //<editor-fold desc="Constructors">
    public SectorSpinnerItemView(Context context)
    {
        super(context);
    }

    public SectorSpinnerItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SectorSpinnerItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void display(SectorCompactDTO dto)
    {
        linkWith(dto, true);
    }

    public void linkWith(@Nullable SectorCompactDTO dto, boolean andDisplay)
    {
        this.sectorCompactDTO = dto;
        if (andDisplay)
        {
            displayText();
        }
    }

    public void displayText()
    {
        if (label != null)
        {
            if (sectorCompactDTO != null)
            {
                label.setText(sectorCompactDTO.name);
            }
            else
            {
                label.setText(R.string.na);
            }
        }
    }
}
