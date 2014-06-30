package com.tradehero.common.text;

import java.util.regex.Pattern;

public class BoldTagProcessor extends RichSpanTextProcessor
{
    /* "**xxx**" = xxx in bold */
    private static final String THMarkdownURegexBold = "\\*\\*(.+?)\\*\\*";

    @Override public String getExtractionPattern()
    {
        return "$1";
    }

    @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        return new RichStyleSpan(android.graphics.Typeface.BOLD, replacement, matchStrings);
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
