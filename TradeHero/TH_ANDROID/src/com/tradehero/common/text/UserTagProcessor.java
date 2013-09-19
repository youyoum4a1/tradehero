package com.tradehero.common.text;

import java.util.regex.Pattern;

/** Created with IntelliJ IDEA. User: tho Date: 9/18/13 Time: 3:15 PM Copyright (c) TradeHero */
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
        return "@$1";
    }

    @Override public String key()
    {
        return "user";
    }
}
