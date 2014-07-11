package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.thm.R;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemTitleDTO;

public class StoreItemHeader extends LinearLayout
    implements DTOView<StoreItemDTO>
{
    @InjectView(R.id.title) protected TextView title;
    private StoreItemTitleDTO storeItemTitleDTO;

    //<editor-fold desc="Constructors">
    public StoreItemHeader(Context context)
    {
        super(context);
    }

    public StoreItemHeader(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StoreItemHeader(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void display(StoreItemDTO dto)
    {
        storeItemTitleDTO = (StoreItemTitleDTO) dto;
        displayTitle();
    }

    protected void displayTitle()
    {
        if (title != null && storeItemTitleDTO != null)
        {
            title.setText(storeItemTitleDTO.titleResId);
        }
    }
}
