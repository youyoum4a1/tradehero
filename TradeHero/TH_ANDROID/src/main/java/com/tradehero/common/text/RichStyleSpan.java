package com.tradehero.common.text;

import android.text.style.StyleSpan;

class RichStyleSpan extends StyleSpan
    implements Span
{
    protected final String[] matchTexts;
    protected final String replacement;
    protected final String originalText;

    RichStyleSpan(int style, String replacement, String[] matchTexts)
    {
        super(style);
        this.replacement = replacement;
        this.matchTexts = matchTexts;

        this.originalText = (matchTexts != null && matchTexts.length > 0) ? matchTexts[0] : null;
    }

    @Override public String getOriginalText()
    {
        return originalText;
    }
}
