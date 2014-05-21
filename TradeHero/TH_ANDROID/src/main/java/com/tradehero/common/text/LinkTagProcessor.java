package com.tradehero.common.text;

import java.util.regex.Pattern;

public class LinkTagProcessor extends ClickableTagProcessor
{
    private static final String THMarkdownRegexLink = "\\[(.+?)\\]\\((.+?)\\)";/* "[text](link)" = add link to text */

    @Override public String key()
    {
        return "link";
    }

    @Override public String getExtractionPattern()
    {
        return "$1";
    }

    @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownRegexLink);
    }
}