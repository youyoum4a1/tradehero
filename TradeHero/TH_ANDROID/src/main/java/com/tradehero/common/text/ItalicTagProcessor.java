package com.tradehero.common.text;

import android.graphics.Typeface;
import java.util.regex.Pattern;


public class ItalicTagProcessor extends RichSpanTextProcessor
{
    /* "_xxx_" = xxx in italics */
    private static final String THMarkdownURegexItalic = "\\*(.+?)\\*";

    @Override public String getExtractionPattern()
    {
        return "$1";
    }

    @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        return new RichStyleSpan(Typeface.ITALIC, replacement, matchStrings);
    }

    @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexItalic);
    }

    @Override public String key()
    {
        return "italic";
    }
}
