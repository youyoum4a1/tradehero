package com.androidth.general.common.text;

import android.support.annotation.NonNull;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class BoldTagProcessor extends RichSpanTextProcessor
{
    /* "**xxx**" = xxx in bold */
    private static final String THMarkdownURegexBold = "\\*\\*(.+?)\\*\\*";

    @NonNull @Override public String getExtractionPattern(@NonNull MatchResult matchResult)
    {
        return "$1";
    }

    @NonNull @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        return new RichStyleSpan(android.graphics.Typeface.BOLD, replacement, matchStrings);
    }

    @NonNull @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexBold);
    }

    @NonNull @Override public String key()
    {
        return "bold";
    }
}
