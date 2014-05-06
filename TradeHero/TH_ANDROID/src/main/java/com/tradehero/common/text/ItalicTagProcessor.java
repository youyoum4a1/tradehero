package com.tradehero.common.text;

import android.graphics.Typeface;
import android.text.style.StyleSpan;
import java.util.regex.Pattern;


public class ItalicTagProcessor extends RichSpanTextProcessor
{
    /* "_xxx_" = xxx in italics */
    private static final String THMarkdownURegexItalic = "\\*(.+?)\\*";

    @Override public String getExtractionPattern()
    {
        return "$1";
    }

    @Override protected Object getSpanElement(String replacement, String[] matchStrings)
    {
        return new StyleSpan(Typeface.ITALIC);
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
