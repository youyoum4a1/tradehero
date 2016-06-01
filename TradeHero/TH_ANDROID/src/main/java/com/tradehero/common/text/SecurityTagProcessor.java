package com.tradehero.common.text;

import android.support.annotation.NonNull;
import android.view.View;
import com.ayondo.academy.api.security.SecurityId;
import com.ayondo.academy.utils.SecurityUtils;
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

    @NonNull @Override protected Span getSpanElement(String replacement, String[] matchStrings)
    {
        return new SecurityClickableSpan(replacement, matchStrings);
    }

    protected class SecurityClickableSpan extends RichClickableSpan
    {
        //<editor-fold desc="Constructors">
        public SecurityClickableSpan(String replacement, String[] matchStrings)
        {
            super(replacement, matchStrings);
        }
        //</editor-fold>

        @Override public void onClick(View view)
        {
            if (matchStrings.length >= 3)
            {
                String exchange = matchStrings[1];
                String symbol = matchStrings[2];
                userActionSubject.onNext(new SecurityTagProcessor.SecurityUserAction(matchStrings, new SecurityId(exchange, symbol)));
            }
        }
    }

    public static class SecurityUserAction extends UserAction
    {
        @NonNull public final SecurityId securityId;

        public SecurityUserAction(@NonNull String[] matchStrings, @NonNull SecurityId securityId)
        {
            super(matchStrings);
            this.securityId = securityId;
        }
    }
}
