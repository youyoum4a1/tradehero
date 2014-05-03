package com.tradehero.common.text;

import android.graphics.Typeface;
import android.text.style.StyleSpan;
import java.util.regex.Pattern;


public class BackTickTagProcessor extends RichSpanTextProcessor
{
    /* "`xxx`" = xxx in Courier font */
    private static final String THMarkdownRegexBackTick = "`(.+?)`";

    @Override public String key()
    {
        return "backTick";
    }

    @Override public String getExtractionPattern()
    {
        return "$1";
    }

    @Override protected Object getSpanElement(String replacement, String[] matchStrings)
    {
        return new StyleSpan(Typeface.MONOSPACE.getStyle());
    }

    @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownRegexBackTick);
    }
}