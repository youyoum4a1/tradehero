package com.tradehero.common.text;

import android.support.annotation.NonNull;
import com.tradehero.th.utils.SecurityUtils;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class SecurityTagProcessor extends ClickableTagProcessor
{
    /*
    * <$LSE:VOD,123$> = security link for securityId 123
    * <$FXRATE:GBP_JPY,184083$> = FX link for securityId 184083
    * */
    private static final String THMarkdownURegexSecurity = "<\\$([a-zA-Z]{3,}):([a-zA-Z0-9_\\.]+),(\\d+)\\$>";

    @NonNull @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexSecurity);
    }

    @NonNull @Override public String getExtractionPattern(@NonNull MatchResult matchResult)
    {
        if (matchResult.group(1).equals(SecurityUtils.FX_EXCHANGE))
        {
            return matchResult.group(2).replace('_', '/');
        }
        return "$1:$2";
    }

    @NonNull @Override public String key()
    {
        return "security";
    }
}
