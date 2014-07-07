package com.tradehero.th.widget.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import com.tradehero.th.models.chart.ChartTimeSpan;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TimeSpanButtonSet extends LinearLayout
{
    private final List<TimeSpanButton> buttons;
    private WeakReference<OnTimeSpanButtonSelectedListener> listener = new WeakReference<>(null);
    private boolean enabled = true;
    private TimeSpanButton currentSelected;

    //<editor-fold desc="Constructors">
    public TimeSpanButtonSet(Context context)
    {
        super(context);
        buttons = new ArrayList<>();
    }

    public TimeSpanButtonSet(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        buttons = new ArrayList<>();
    }

    public TimeSpanButtonSet(Context context, AttributeSet attrs, int defStyle)
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
    //</editor-fold>

    public void addAllChildButtons()
    {
        for (int childIndex = 0; childIndex < getChildCount(); childIndex++)
        {
            if (getChildAt(childIndex) instanceof TimeSpanButton)
            {
                addButton((TimeSpanButton) getChildAt(childIndex));
            }
        }
    }

    public void addButton(TimeSpanButton quickPriceButton)
    {
        if (quickPriceButton != null)
        {
            quickPriceButton.setOnClickListener(createButtonOnClickListener());
            quickPriceButton.setBackgroundResource(R.drawable.basic_transparent_selector);
            buttons.add(quickPriceButton);
        }
    }

    public void addButton(int resourceId)
    {
        addButton((TimeSpanButton) findViewById(resourceId));
    }

    public void removeButton(TimeSpanButton quickPriceButton)
    {
        if (buttons.contains(quickPriceButton))
        {
            quickPriceButton.setOnClickListener(null);
        }
        buttons.remove(quickPriceButton);
    }

    public void clearButtons()
    {
        for(TimeSpanButton button:buttons)
        {
            button.setOnClickListener(null);
        }
        buttons.clear();
    }

    public void setActive(ChartTimeSpan timeSpan)
    {
        for(TimeSpanButton button: buttons)
        {
            if (button.getTimeSpan().equals(timeSpan))
            {
                currentSelected = button;
                break;
            }
        }
        display();
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param listener
     */
    public void setListener(OnTimeSpanButtonSelectedListener listener)
    {
        this.listener = new WeakReference<>(listener);
    }

    private void notifyListener(ChartTimeSpan timeSpan)
    {
        OnTimeSpanButtonSelectedListener listener = this.listener.get();
        if (listener != null)
        {
            listener.onTimeSpanButtonSelected(timeSpan);
        }
    }

    public void display()
    {
        for(TimeSpanButton button: buttons)
        {
            button.setEnabled(enabled); // Presumably there is no other reason a specific button could be disabled
        }
        for (TimeSpanButton button: buttons)
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
                TimeSpanButtonSet.this.currentSelected = (TimeSpanButton) view;
                display();
                notifyListener(((TimeSpanButton) view).getTimeSpan());
            }
        };
    }

    public interface OnTimeSpanButtonSelectedListener
    {
        public void onTimeSpanButtonSelected(ChartTimeSpan selected);
    }
}
