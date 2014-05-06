package com.tradehero.common.text;

import android.text.style.StyleSpan;
import java.util.regex.Pattern;


public class BoldTagProcessor extends RichSpanTextProcessor
{
    /* "**xxx**" = xxx in bold */
    private static final String THMarkdownURegexBold = "\\*\\*(.+?)\\*\\*";

    @Override public String getExtractionPattern()
    {
        return "$1";
    }

    @Override protected Object getSpanElement(String replacement, String[] matchStrings)
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
