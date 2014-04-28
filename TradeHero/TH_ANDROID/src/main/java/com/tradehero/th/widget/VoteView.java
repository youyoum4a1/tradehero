package com.tradehero.th.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;

abstract public class VoteView extends CompoundButton
{
    private int originalValue;

    //<editor-fold desc="Constructors">
    public VoteView(Context context)
    {
        super(context);
    }

    public VoteView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public VoteView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    abstract public void display(AbstractDiscussionDTO discussionDTO);

    @Override public void setText(CharSequence text, BufferType type)
    {
        try
        {
            originalValue = Integer.parseInt(text.toString());
        }
        catch (NumberFormatException ex)
        {
            throw new IllegalStateException("Text should be an integer");
        }

        super.setText(text, type);
    }

    public void setValue(int value)
    {
        String toDisplay = "" + value ;//(value + (isChecked() ? 1 : 0));
        super.setText(toDisplay, BufferType.NORMAL);
    }

    @Override public void setChecked(boolean checked)
    {
        boolean oldValue = isChecked();
        super.setChecked(checked);
        if (oldValue != checked)
        {
            //Timber.d("Original value: %d", originalValue);
            //setValue(originalValue);
        }
    }
}
