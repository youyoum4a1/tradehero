package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemHasFurtherDTO;

public class StoreItemHasFurther extends StoreItemClickable
{
    protected StoreItemHasFurtherDTO storeItemHasFurtherDTO;

    //<editor-fold desc="Constructors">
    public StoreItemHasFurther(Context context)
    {
        super(context);
    }

    public StoreItemHasFurther(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StoreItemHasFurther(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(StoreItemDTO dto)
    {
        super.display(dto);
        storeItemHasFurtherDTO= (StoreItemHasFurtherDTO) dto;
    }
}
