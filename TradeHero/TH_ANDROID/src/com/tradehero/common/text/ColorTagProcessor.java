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

    @Override protected Object getSpanElement(String replacement)
    {
        // TODO use input color
        return new ForegroundColorSpan(Color.RED);
    }

    @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexColor);
    }
}
