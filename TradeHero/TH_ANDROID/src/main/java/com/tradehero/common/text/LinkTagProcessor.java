package com.tradehero.common.text;

import android.support.annotation.NonNull;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class LinkTagProcessor extends ClickableTagProcessor
{
    private static final String THMarkdownRegexLink = "\\[(.+?)\\]\\((.+?)\\)";/* "[text](link)" = add link to text */

    @NonNull @Override public String key()
    {
        return "link";
    }

    @NonNull @Override public String getExtractionPattern(@NonNull MatchResult matchResult)
    {
        return "$1";
    }

    @NonNull @Override protected Pattern getPattern()
    {
        return Pattern.compile(THMarkdownRegexLink);
    }
}