package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.fragments.billing.store.StoreItemDTO;
import com.tradehero.th.fragments.billing.store.StoreItemPromptPurchaseDTO;
import timber.log.Timber;

public class StoreItemLikeButton extends StoreItemHasFurther
{
    @InjectView(R.id.btn_buy_now) protected ImageView imageButton;
    protected StoreItemPromptPurchaseDTO storeItemPromptPurchaseDTO;

    //<editor-fold desc="Constructors">
    public StoreItemLikeButton(Context context)
    {
        super(context);
    }

    public StoreItemLikeButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StoreItemLikeButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void display(StoreItemDTO dto)
    {
        super.display(dto);
        storeItemPromptPurchaseDTO = (StoreItemPromptPurchaseDTO) dto;
        displayImageButton();
    }

    @Override public void display()
    {
        super.display();
        displayImageButton();
    }

    protected void displayImageButton()
    {
        if (imageButton != null && storeItemPromptPurchaseDTO != null)
        {
            try
            {
                imageButton.setImageResource(storeItemPromptPurchaseDTO.buttonIconResId);
            }
            catch (OutOfMemoryError e)
            {
                Timber.e(e, "");
            }
        }
    }
}
