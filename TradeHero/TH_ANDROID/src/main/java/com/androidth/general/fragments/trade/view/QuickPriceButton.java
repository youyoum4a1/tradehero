package com.androidth.general.fragments.trade.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.Button;
import com.androidth.general.R;

public class QuickPriceButton extends Button
{
    public static final float ALPHA_DISABLED = 0.5f;

    private boolean isPercent;
    private double price;
    private double percentValue;
    private String priceText;
    private String percentText;

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
        setPriceText(a.getString(R.styleable.QuickPriceButton_priceText));
        setPercentValue(((double) a.getInt(R.styleable.QuickPriceButton_percent, 0)) / 100);
        setPercentText(a.getString(R.styleable.QuickPriceButton_percentText));
        a.recycle();
    }

    //<editor-fold desc="Accessor>
    public boolean isPercent()
    {
        return isPercent;
    }

    public void setPercent(boolean isPercent)
    {
        this.isPercent = isPercent;
        display();
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public void setPriceText(String priceText)
    {
        this.priceText = priceText;
        display();
    }

    public double getPercentValue()
    {
        return percentValue;
    }

    public void setPercentValue(double percentValue)
    {
        this.percentValue = percentValue;
    }

    public void setPercentText(String percentText)
    {
        this.percentText = percentText;
        display();
    }
    //</editor-fold>

    @Override public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        setAlpha(enabled ? 1 : ALPHA_DISABLED);
    }

    protected void display()
    {
        setText(isPercent ? percentText : priceText);
    }
}
