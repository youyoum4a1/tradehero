package com.tradehero.common.text;

import android.text.style.StyleSpan;
import java.util.regex.Pattern;

/** Created with IntelliJ IDEA. User: tho Date: 9/18/13 Time: 2:16 PM Copyright (c) TradeHero */
public class BoldTagProcessor extends RichSpanTextProcessor
{
    /* "**xxx**" = xxx in bold */
    private static final String THMarkdownURegexBold = "\\*\\*(.+?)\\*\\*";

    @Override public String getExtractionPattern()
    {
        return "$1";
    }

    @Override protected Object getSpanElement(String replacement, String ... matchStrings)
    {
        return new StyleSpan(android.graphics.Typeface.BOLD);
    }

    @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexBold);
    }

    @Override public String key()
    {
        return "bold";
    }
}
