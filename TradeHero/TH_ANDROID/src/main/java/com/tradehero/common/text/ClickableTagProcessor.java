package com.tradehero.common.text;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/** Created with IntelliJ IDEA. User: tho Date: 9/18/13 Time: 11:44 AM Copyright (c) TradeHero */
public abstract class ClickableTagProcessor extends RichSpanTextProcessor
{
    @Override protected Object getSpanElement(final String replacement, String ... matchStrings)
    {
        ClickableSpan spanElement = new ClickableSpan()
        {
            @Override public void onClick(View view)
            {
                if (view instanceof OnElementClickListener) {
                    ((OnElementClickListener) view).onClick(view, replacement, key());
                }
            }

            @Override public void updateDrawState(TextPaint ds)
            {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        return spanElement;
    }
}
