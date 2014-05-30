package com.tradehero.th.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import com.tradehero.th.R;

public class CommentActionView extends CompoundButton
{
    public CommentActionView(Context context)
    {
        super(context);
    }

    public CommentActionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CommentActionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override public void setChecked(boolean checked)
    {
        boolean oldValue = isChecked();
        super.setChecked(checked);
        if (oldValue != checked)
        {
            // mutate to preserve the original drawable
            Drawable drawableLeft = getResources().getDrawable(R.drawable.icn_actions_comment).mutate();
            if (checked)
            {
                drawableLeft.setColorFilter(getResources().getColor(R.color.timeline_action_button_text_color_pressed), PorterDuff.Mode.SRC_ATOP);
            }
            setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
            invalidate();
        }
    }
}
