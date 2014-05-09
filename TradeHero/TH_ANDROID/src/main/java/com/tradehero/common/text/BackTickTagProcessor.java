package com.tradehero.common.text;

import android.graphics.Typeface;
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

    @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        return new RichStyleSpan(Typeface.MONOSPACE.getStyle(), replacement, matchStrings);
    }

    @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownRegexBackTick);
    }
}