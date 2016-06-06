package com.androidth.general.fragments.trade.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.androidth.general.R;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class QuickPriceButtonSet extends LinearLayout
        implements View.OnClickListener
{
    private double maxPrice = Double.MAX_VALUE;
    private boolean isPercent;
    @Nullable private QuickPriceButton currentSelected;
    @NonNull protected final BehaviorSubject<Double> priceSelectedSubject;

    //<editor-fold desc="Constructors">
    public QuickPriceButtonSet(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
        this.priceSelectedSubject = BehaviorSubject.create();
    }
    //</editor-fold>

    protected void init(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QuickPriceButtonSet);
        setPercent(a.getBoolean(R.styleable.QuickPriceButtonSet_isPercent, false));
        a.recycle();
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        propagatePercent();
    }

    //<editor-fold desc="Accessor">
    public boolean isPercent()
    {
        return isPercent;
    }

    public void setPercent(boolean isPercent)
    {
        this.isPercent = isPercent;
        propagatePercent();
    }

    @Override public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        display();
    }

    public void setMaxPrice(double maxPrice)
    {
        this.maxPrice = maxPrice;
        display();
    }
    //</editor-fold>

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        for (QuickPriceButton button : findButtons())
        {
            button.setOnClickListener(this);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        for (QuickPriceButton button : findButtons())
        {
            button.setOnClickListener(null);
        }
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<Double> getPriceSelectedObservable()
    {
        return priceSelectedSubject.asObservable();
    }

    @NonNull public List<QuickPriceButton> findButtons()
    {
        return findButtons(this);
    }

    @NonNull protected List<QuickPriceButton> findButtons(@NonNull ViewGroup parent)
    {
        List<QuickPriceButton> found = new ArrayList<>();
        for (int i = 0; i < parent.getChildCount(); i++)
        {
            View child = parent.getChildAt(i);
            if (child instanceof ViewGroup)
            {
                found.addAll(findButtons((ViewGroup) child));
            }
            else if (child instanceof QuickPriceButton)
            {
                found.add((QuickPriceButton) child);
            }
        }
        return found;
    }

    protected void propagatePercent()
    {
        for (QuickPriceButton button : findButtons())
        {
            button.setPercent(isPercent);
        }
    }

    @Override public void onClick(@NonNull View view)
    {
        currentSelected = (QuickPriceButton) view;
        display();
        double value;
        if (isPercent)
        {
            value = ((QuickPriceButton) view).getPercentValue();
        }
        else
        {
            value = ((QuickPriceButton) view).getPrice();
        }
        priceSelectedSubject.onNext(value);
    }

    protected void display()
    {
        List<QuickPriceButton> buttons = findButtons();
        for (QuickPriceButton button : buttons)
        {
            button.setEnabled(isEnabled() && button.getPrice() <= maxPrice);
            button.setSelected(button == currentSelected && button.isEnabled());
            button.setTextColor(button == currentSelected ? Color.BLACK : getResources().getColor(R.color.text_secondary));
        }
    }
}
