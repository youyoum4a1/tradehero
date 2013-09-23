package com.tradehero.th.widget.trade;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 9/23/13 Time: 5:38 PM To change this template use File | Settings | File Templates. */
public class QuickPriceButtonSet extends LinearLayout
{
    private List<QuickPriceButton> buttons;
    private OnQuickPriceButtonSelectedListener listener;

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

    public void setEnabled(boolean enabled)
    {
        for(QuickPriceButton button: buttons)
        {
            button.setEnabled(enabled);
        }
    }

    private OnClickListener createButtonOnClickListener()
    {
        return new OnClickListener()
        {
            @Override public void onClick(View view)
            {
                updateLookSelectors(view);
                notifyListener(((QuickPriceButton) view).getPrice());
            }
        };
    }

    private void updateLookSelectors(View selected)
    {
        for (Button button: buttons)
        {
            if (button == selected)
            {
                button.setTextColor(getResources().getColor(R.color.black));
            }
            else
            {
                button.setTextColor(getResources().getColor(R.color.price_bar_text_default));
            }
        }
    }

    public interface OnQuickPriceButtonSelectedListener
    {
        public void onQuickPriceButtonSelected(double priceSelected);
    }
}
