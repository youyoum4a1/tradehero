package com.tradehero.common.text;

import java.util.regex.Pattern;

public class UserTagProcessor extends ClickableTagProcessor
{
    private static final String THMarkdownURegexUser = "<@(.+?),(\\d+)@>";

    //  <@dom,123@> = user link for userId 123*/
    @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexUser);
    }

    @Override public String getExtractionPattern()
    {
        return "$1";
    }

    @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        return super.getSpanElement(replacement, matchStrings);
    }

    @Override public String key()
    {
        return "user";
    }
}
