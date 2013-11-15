package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/23/13 Time: 5:38 PM To change this template use File | Settings | File Templates. */
public class QuickPriceButtonSet extends LinearLayout
{
    public static final String TAG = QuickPriceButtonSet.class.getSimpleName();

    private List<QuickPriceButton> buttons;
    private OnQuickPriceButtonSelectedListener listener;
    private boolean enabled = false;
    private double maxPrice = Double.MAX_VALUE;
    private QuickPriceButton currentSelected;

    //<editor-fold desc="Constructors">
    public QuickPriceButtonSet(Context context)
    {
        super(context);
        buttons = new ArrayList<>();
    }

    public QuickPriceButtonSet(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        buttons = new ArrayList<>();
    }

    public QuickPriceButtonSet(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        buttons = new ArrayList<>();
    }
    //</editor-fold>

    //<editor-fold desc="Accessors">
    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        display();
    }

    public double getMaxPrice()
    {
        return maxPrice;
    }

    public void setMaxPrice(double maxPrice)
    {
        this.maxPrice = maxPrice;
        THLog.d(TAG, "MaxPrice: " + maxPrice);
        display();
    }
    //</editor-fold>

    public void addButton(QuickPriceButton quickPriceButton)
    {
        if (quickPriceButton != null)
        {
            quickPriceButton.setOnClickListener(createButtonOnClickListener());
            buttons.add(quickPriceButton);
        }
    }

    public void addButton(int resourceId)
    {
        addButton((QuickPriceButton) findViewById(resourceId));
    }

    public void removeButton(QuickPriceButton quickPriceButton)
    {
        if (buttons.contains(quickPriceButton))
        {
            quickPriceButton.setOnClickListener(null);
        }
        buttons.remove(quickPriceButton);
    }

    public void clearButtons()
    {
        for(QuickPriceButton button:buttons)
        {
            button.setOnClickListener(null);
        }
        buttons.clear();
    }

    public void setListener(OnQuickPriceButtonSelectedListener listener)
    {
        this.listener = listener;
    }

    private void notifyListener(double price)
    {
        if (this.listener != null)
        {
            this.listener.onQuickPriceButtonSelected(price);
        }
    }

    public void display()
    {
        for(QuickPriceButton button: buttons)
        {
            button.setEnabled(enabled && (button.getPrice() <= maxPrice));
        }
        for (Button button: buttons)
        {
            if (button == this.currentSelected && button.isEnabled())
            {
                button.setTextColor(getResources().getColor(R.color.black));
            }
            else
            {
                button.setTextColor(getResources().getColor(R.color.price_bar_text_default));
            }
        }
    }

    private OnClickListener createButtonOnClickListener()
    {
        return new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                QuickPriceButtonSet.this.currentSelected = (QuickPriceButton) view;
                display();
                notifyListener(((QuickPriceButton) view).getPrice());
            }
        };
    }

    public interface OnQuickPriceButtonSelectedListener
    {
        public void onQuickPriceButtonSelected(double priceSelected);
    }
}
