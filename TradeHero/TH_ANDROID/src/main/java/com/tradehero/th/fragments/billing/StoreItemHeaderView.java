package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemTitleDTO;

public class StoreItemHeaderView extends LinearLayout
    implements DTOView<StoreItemDTO>
{
    @Bind(R.id.title) protected TextView title;

    //<editor-fold desc="Constructors">
    public StoreItemHeaderView(Context context)
    {
        super(context);
    }

    public StoreItemHeaderView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StoreItemHeaderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override public void display(@NonNull StoreItemDTO dto)
    {
        StoreItemTitleDTO storeItemTitleDTO = (StoreItemTitleDTO) dto;

        if (title != null)
        {
            title.setText(storeItemTitleDTO.titleResId);
        }
    }
}
