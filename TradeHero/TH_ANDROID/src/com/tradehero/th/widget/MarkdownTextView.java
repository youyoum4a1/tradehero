package com.tradehero.th.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.common.text.RichTextCreator;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: tho Date: 9/17/13 Time: 11:18 AM Copyright (c) TradeHero */
public class MarkdownTextView extends TextView
{
    @Inject RichTextCreator parser;
    private boolean processed;

    //<editor-fold desc="Constructors">
    public MarkdownTextView(Context context)
    {
        super(context, null);
    }

    public MarkdownTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs, 0);
    }

    public MarkdownTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    public void init()
    {
        DaggerUtils.inject(this);
    }

    @Override protected void onFinishInflate()
    {
        init();
        super.onFinishInflate();
    }

    @Override public void setText(CharSequence text, BufferType type)
    {
        text = RichTextCreator.load(text).create();
        super.setText(text, type);
    }
}
