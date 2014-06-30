package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import com.tradehero.thm.R;

public class QuickPriceButton extends Button
{
    public static final float ALPHA_DISABLED = 0.5f;

    //<editor-fold desc="Constructors">
    public QuickPriceButton(Context context)
    {
        super(context);
    }

    public QuickPriceButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public QuickPriceButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    //</editor-fold>

    protected void init(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QuickPriceButton);
        setPrice(a.getInt(R.styleable.QuickPriceButton_price, 0));
        a.recycle();
    }

    //<editor-fold desc="Accessors">
    public double getPrice()
    {
        Double price = (Double) getTag(R.string.key_price);
        if (price == null)
        {
            return 0;
        }
        return price;
    }

    public void setPrice(double price)
    {
        setTag(R.string.key_price, price);
    }

    @Override public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1 : ALPHA_DISABLED);
    }
    //</editor-fold>

    @Override protected void onDetachedFromWindow()
    {
        setOnClickListener(null);
        super.onDetachedFromWindow();
    }
}
