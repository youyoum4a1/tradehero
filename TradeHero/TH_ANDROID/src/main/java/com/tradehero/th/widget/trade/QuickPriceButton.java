package com.tradehero.th.widget.trade;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Button;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 9/23/13 Time: 5:40 PM To change this template use File | Settings | File Templates. */
public class QuickPriceButton extends Button
{
    private double price;

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
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ValidatedText);
        price = a.getInt(R.styleable.QuickPriceButton_price, 0);
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

}
