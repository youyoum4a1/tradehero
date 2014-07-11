package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.thm.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.billing.store.StoreItemClickableDTO;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import timber.log.Timber;

public class StoreItemClickable extends RelativeLayout
    implements DTOView<StoreItemDTO>
{
    @InjectView(R.id.title) protected TextView title;
    @InjectView(R.id.icon) protected ImageView icon;
    protected StoreItemClickableDTO storeItemClickableDTO;

    //<editor-fold desc="Constructors">
    public StoreItemClickable(Context context)
    {
        super(context);
    }

    public StoreItemClickable(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StoreItemClickable(Context context, AttributeSet attrs, int defStyle)
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
        storeItemClickableDTO = (StoreItemClickableDTO) dto;
        display();
    }

    public void display()
    {
        displayTitle();
        displayIcon();
    }

    protected void displayTitle()
    {
        if (title != null && storeItemClickableDTO != null)
        {
            title.setText(storeItemClickableDTO.titleResId);
        }
    }

    protected void displayIcon()
    {
        if (icon != null && storeItemClickableDTO != null)
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
