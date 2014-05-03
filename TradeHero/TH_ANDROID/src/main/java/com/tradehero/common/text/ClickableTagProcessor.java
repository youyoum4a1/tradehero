package com.tradehero.common.text;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;


public abstract class ClickableTagProcessor extends RichSpanTextProcessor
{
    @Override protected Object getSpanElement(final String replacement, final String[] matchStrings)
    {
        ClickableSpan spanElement = new ClickableSpan()
        {
            @Override public void onClick(View view)
            {
                if (view instanceof OnElementClickListener) {
                    ((OnElementClickListener) view).onClick(view, replacement, key(), matchStrings);
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
