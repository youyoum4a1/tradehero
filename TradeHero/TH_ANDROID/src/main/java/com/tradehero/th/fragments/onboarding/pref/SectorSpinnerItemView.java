package com.tradehero.th.fragments.onboarding.pref;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.market.SectorDTO;

@Deprecated
public class SectorSpinnerItemView extends RelativeLayout
    implements DTOView<SectorDTO>
{
    @InjectView(R.id.spinner_item_label) TextView label;

    @Nullable private SectorDTO sectorDTO;

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

    @Override public void display(SectorDTO dto)
    {
        linkWith(dto, true);
    }

    public void linkWith(@Nullable SectorDTO dto, boolean andDisplay)
    {
        this.sectorDTO = dto;
        if (andDisplay)
        {
            displayText();
        }
    }

    public void displayText()
    {
        if (label != null)
        {
            if (sectorDTO != null)
            {
                label.setText(sectorDTO.name);
            }
            else
            {
                label.setText(R.string.na);
            }
        }
    }
}
