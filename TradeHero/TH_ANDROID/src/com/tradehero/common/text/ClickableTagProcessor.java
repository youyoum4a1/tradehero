package com.tradehero.common.text;

import android.text.style.ClickableSpan;
import android.view.View;
import com.tradehero.common.utils.THLog;

/** Created with IntelliJ IDEA. User: tho Date: 9/18/13 Time: 11:44 AM Copyright (c) TradeHero */
public abstract class ClickableTagProcessor extends RichSpanTextProcessor
{
    @Override protected Object getSpanElement(String replacement)
    {
        return new ClickableSpan()
        {
            @Override public void onClick(View view)
            {
                THLog.d("working", view.getId() + " is clicked");
            }
        };
    }
}
