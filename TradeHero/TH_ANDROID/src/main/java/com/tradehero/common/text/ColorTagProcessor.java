package com.tradehero.common.text;

import android.graphics.Color;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import java.util.regex.Pattern;

/** Created with IntelliJ IDEA. User: tho Date: 9/18/13 Time: 6:03 PM Copyright (c) TradeHero */
public class ColorTagProcessor extends RichSpanTextProcessor
{
    private static final String THMarkdownURegexColor = "\\{(.+?)\\|(.+?)\\}"; /* "{color|text}" = text in specified color */

    @Override public String key()
    {
        return "color";
    }

    @Override public String getExtractionPattern()
    {
        return "$1";
    }

    @Override protected Object getSpanElement(String replacement, String[] matchStrings)
    {
        if (matchStrings.length >= 3) {
            return new ForegroundColorSpan(Color.parseColor(matchStrings[1]));
        } else {
            return null;
        }
    }

    @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexColor);
    }
}
