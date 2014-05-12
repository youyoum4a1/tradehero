package com.tradehero.common.text;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;


public abstract class ClickableTagProcessor extends RichSpanTextProcessor
{
    @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        return new RichClickableSpan(replacement, matchStrings);
    }

    private class RichClickableSpan extends ClickableSpan
        implements Span
    {
        private final String replacement;
        private final String originalText;
        private final String[] matchStrings;

        public RichClickableSpan(String replacement, String[] matchStrings)
        {
            this.replacement = replacement;
            this.matchStrings = matchStrings;
            this.originalText = matchStrings.length > 0 ? matchStrings[0] : null;
        }

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

        @Override public String getOriginalText()
        {
            return originalText;
        }
    }
}
