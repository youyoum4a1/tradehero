package com.tradehero.th.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;

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

    public abstract void display(AbstractDiscussionCompactDTO discussionDTO);

    protected abstract int getCheckedColor();

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
            // mutate to preserve the original drawable
            Drawable drawableLeft = getResources().getDrawable(R.drawable.icn_upvote).mutate();
            if (checked)
            {
                drawableLeft.setColorFilter(getCheckedColor(), PorterDuff.Mode.SRC_ATOP);
            }
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
            invalidate();
        }
    }
}
