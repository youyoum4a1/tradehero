package com.tradehero.common.text;

import java.util.regex.Pattern;

/** Created with IntelliJ IDEA. User: tho Date: 9/18/13 Time: 3:12 PM Copyright (c) TradeHero */
public class SecurityTagProcessor extends ClickableTagProcessor
{
    /* <$LSE:VOD,123$> = security link for securityId 123*/
    private static final String THMarkdownURegexSecurity = "<\\$([a-zA-Z]{3,}):([a-zA-Z0-9\\.]+),(\\d+)\\$>";

    @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexSecurity);
    }

    @Override public String key()
    {
        return "security";
    }
}
