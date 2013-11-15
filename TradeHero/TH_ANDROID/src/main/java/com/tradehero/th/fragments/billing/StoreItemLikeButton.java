package com.tradehero.th.fragments.billing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 7:09 PM To change this template use File | Settings | File Templates. */
public class StoreItemLikeButton extends StoreItemHasFurther
{
    public static final String TAG = StoreItemLikeButton.class.getSimpleName();

    protected ImageView imageButton;
    protected int imageButtonResId;

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

    @Override protected void initViews()
    {
        super.initViews();
        imageButton = (ImageView) findViewById(R.id.btn_buy_now);
    }

    public void setImageButtonResId(int imageButtonResId)
    {
        this.imageButtonResId = imageButtonResId;
        displayImageButton();
    }

    @Override public void display()
    {
        super.display();
        displayImageButton();
    }

    protected void displayImageButton()
    {
        if (imageButton != null)
        {
            imageButton.setImageResource(imageButtonResId);
        }
    }
}
