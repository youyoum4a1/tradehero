package com.androidth.general.common.text;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.style.ForegroundColorSpan;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class ColorTagProcessor extends RichSpanTextProcessor
{
    private static final String THMarkdownURegexColor = "\\{(.+?)\\|(.+?)\\}"; /* "{color|text}" = text in specified color */

    @NonNull @Override public String key()
    {
        return "color";
    }

    @NonNull @Override public String getExtractionPattern(@NonNull MatchResult matchResult)
    {
        return "$2";
    }

    @NonNull @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        if (matchStrings.length >= 3)
        {
            return new RichForegroundColorSpan(replacement, matchStrings);
        }
        else
        {
            return null;
        }
    }

    @NonNull @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexColor);
    }

    private class RichForegroundColorSpan extends ForegroundColorSpan
        implements Span
    {
        private final String originalText;

        public RichForegroundColorSpan(String replacement, String[] matchStrings)
        {
            super(Color.parseColor(matchStrings[1]));

            this.originalText = matchStrings[0];
        }

        @Override public String getOriginalText()
        {
            return originalText;
        }
    }
}
