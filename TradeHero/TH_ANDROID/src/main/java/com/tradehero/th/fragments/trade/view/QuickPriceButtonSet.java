package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class QuickPriceButtonSet extends LinearLayout
        implements View.OnClickListener
{
    private double maxPrice = Double.MAX_VALUE;
    @Nullable private QuickPriceButton currentSelected;
    @NonNull protected final BehaviorSubject<Double> priceSelectedSubject;

    //<editor-fold desc="Constructors">
    public QuickPriceButtonSet(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.priceSelectedSubject = BehaviorSubject.create();
    }
    //</editor-fold>

    //<editor-fold desc="Accessors">
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

    @NonNull protected List<QuickPriceButton> findButtons()
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

    @Override public void onClick(@NonNull View view)
    {
        currentSelected = (QuickPriceButton) view;
        display();
        priceSelectedSubject.onNext(((QuickPriceButton) view).getPrice());
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
