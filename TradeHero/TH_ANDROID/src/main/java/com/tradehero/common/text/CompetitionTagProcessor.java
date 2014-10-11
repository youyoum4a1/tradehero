package com.tradehero.common.text;

import java.util.regex.Pattern;

/*
    比赛相关 <#股海拾贝,301#> 显示 股海拾贝 ,competition id is 301
*/
public class CompetitionTagProcessor extends ClickableTagProcessor
{
    private static final String THMarkdownURegexUser = "<#(.+?),(\\d+)#>";

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
        return "competition";
    }
}
