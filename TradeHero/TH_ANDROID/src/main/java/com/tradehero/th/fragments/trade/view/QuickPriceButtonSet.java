package com.tradehero.th.fragments.trade.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.List;

public class QuickPriceButtonSet extends LinearLayout
    implements View.OnClickListener
{
    @Nullable private OnQuickPriceButtonSelectedListener listener;
    private double maxPrice = Double.MAX_VALUE;
    @Nullable private QuickPriceButton currentSelected;
    public boolean isFX;

    //<editor-fold desc="Constructors">
    public QuickPriceButtonSet(Context context, AttributeSet attrs)
    {
        super(context, attrs);
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

    public void setListener(@Nullable OnQuickPriceButtonSelectedListener listener)
    {
        this.listener = listener;
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

    @Override public void onClick(@NonNull View view)
    {
        currentSelected = (QuickPriceButton) view;
        display();
        notifyListener(((QuickPriceButton) view).getPrice());
    }

    protected void display()
    {
        List<QuickPriceButton> buttons = findButtons();
        for (QuickPriceButton button : buttons)
        {
            button.setEnabled(isEnabled() && (isFX || (button.getPrice() <= maxPrice)));
        }
        for (Button button : buttons)
        {
            button.setSelected(button == currentSelected && button.isEnabled());
        }
    }

    private void notifyListener(double price)
    {
        OnQuickPriceButtonSelectedListener listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onQuickPriceButtonSelected(price);
        }
    }

    public interface OnQuickPriceButtonSelectedListener
    {
        public void onQuickPriceButtonSelected(double priceSelected);
    }

    public void setFX(boolean isFX) {
        this.isFX = isFX;
    }
}
