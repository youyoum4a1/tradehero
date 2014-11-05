package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import com.tradehero.th.R;
import android.support.annotation.NonNull;

public class QuickPriceButton extends Button
{
    public static final float ALPHA_DISABLED = 0.5f;

    private double price;

    //<editor-fold desc="Constructors">
    public QuickPriceButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }
    //</editor-fold>

    protected void init(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QuickPriceButton);
        setPrice(a.getInt(R.styleable.QuickPriceButton_price, 0));
        a.recycle();
    }

    //<editor-fold desc="Accessors">
    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }
    //</editor-fold>

    @Override public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1 : ALPHA_DISABLED);
    }
}
