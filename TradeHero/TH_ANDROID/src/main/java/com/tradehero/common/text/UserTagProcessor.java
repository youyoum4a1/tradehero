package com.tradehero.common.text;

import android.support.annotation.NonNull;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class UserTagProcessor extends ClickableTagProcessor
{
    private static final String THMarkdownURegexUser = "<@(.+?),(\\d+)@>";

    //  <@dom,123@> = user link for userId 123*/
    @NonNull @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownURegexUser);
    }

    @NonNull @Override public String getExtractionPattern(@NonNull MatchResult matchResult)
    {
        return "$1";
    }

    @NonNull @Override public String key()
    {
        return "user";
    }
}
