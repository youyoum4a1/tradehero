package com.tradehero.common.text;

import java.util.regex.Pattern;

/** Created with IntelliJ IDEA. User: tho Date: 9/18/13 Time: 5:57 PM Copyright (c) TradeHero */
public class LinkTagProcessor extends ClickableTagProcessor
{
    private static final String THMarkdownRegexLink = "\\[(.+?)\\]\\((.+?)\\)";/* "[text](link)" = add link to text */

    @Override public String key()
    {
        return "link";
    }

    @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownRegexLink);
    }
}