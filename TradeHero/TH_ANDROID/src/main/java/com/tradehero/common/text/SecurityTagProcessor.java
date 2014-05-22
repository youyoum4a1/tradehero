package com.tradehero.common.text;

import java.util.regex.Pattern;

public class SecurityTagProcessor extends ClickableTagProcessor
{
    /* <$LSE:VOD,123$> = security link for securityId 123*/
    private static final String THMarkdownURegexSecurity = "<\\$([a-zA-Z]{3,}):([a-zA-Z0-9\\.]+),(\\d+)\\$>";

    @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexSecurity);
    }

    @Override public String getExtractionPattern()
    {
        return "$1:$2";
    }

    @Override public String key()
    {
        return "security";
    }
}
