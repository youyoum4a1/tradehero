package com.androidth.general.common.text;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class BackTickTagProcessor extends RichSpanTextProcessor
{
    /* "`xxx`" = xxx in Courier font */
    private static final String THMarkdownRegexBackTick = "`(.+?)`";

    @NonNull @Override public String key()
    {
        return "backTick";
    }

    @NonNull @Override public String getExtractionPattern(@NonNull MatchResult matchResult)
    {
        return "$1";
    }

    @NonNull @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        return new RichStyleSpan(Typeface.MONOSPACE.getStyle(), replacement, matchStrings);
    }

    @NonNull @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownRegexBackTick);
    }
}