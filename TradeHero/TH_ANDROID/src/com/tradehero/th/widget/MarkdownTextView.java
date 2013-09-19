package com.tradehero.th.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.common.text.RichTextCreator;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/17/13 Time: 11:18 AM Copyright (c) TradeHero */
public class MarkdownTextView extends TextView
{
    @Inject RichTextCreator parser;

    //<editor-fold desc="Constructors">
    public MarkdownTextView(Context context)
    {
        super(context);
    }

    public MarkdownTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MarkdownTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override public void setText(CharSequence text, BufferType type)
    {
        if (parser != null)
        {
            text = parser.load(text).create();
        }
        super.setText(text, type);
    }
}
