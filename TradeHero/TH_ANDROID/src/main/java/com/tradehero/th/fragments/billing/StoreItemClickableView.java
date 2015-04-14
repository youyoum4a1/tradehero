package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.billing.store.StoreItemClickableDTO;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import timber.log.Timber;

public class StoreItemClickableView extends RelativeLayout
    implements DTOView<StoreItemDTO>
{
    @InjectView(R.id.title) protected TextView title;
    @InjectView(R.id.icon) protected ImageView icon;

    //<editor-fold desc="Constructors">
    public StoreItemClickableView(Context context)
    {
        super(context);
    }

    public StoreItemClickableView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StoreItemClickableView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void display(@NonNull StoreItemDTO dto)
    {
        StoreItemClickableDTO storeItemClickableDTO = (StoreItemClickableDTO) dto;

        if (title != null)
        {
            title.setText(storeItemClickableDTO.titleResId);
        }

        if (icon != null)
        {
            try
            {
                icon.setImageResource(storeItemClickableDTO.iconResId);
            }
            catch (OutOfMemoryError e)
            {
                Timber.e(e, "");
            }
        }
    }
}
