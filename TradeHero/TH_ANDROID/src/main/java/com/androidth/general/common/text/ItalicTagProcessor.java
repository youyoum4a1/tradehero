package com.androidth.general.common.text;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class ItalicTagProcessor extends RichSpanTextProcessor
{
    /* "_xxx_" = xxx in italics */
    private static final String THMarkdownURegexItalic = "\\*(.+?)\\*";

    @NonNull @Override public String getExtractionPattern(@NonNull MatchResult matchResult)
    {
        return "$1";
    }

    @NonNull @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        return new RichStyleSpan(Typeface.ITALIC, replacement, matchStrings);
    }

    @NonNull @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexItalic);
    }

    @NonNull @Override public String key()
    {
        return "italic";
    }
}
