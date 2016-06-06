package com.androidth.general.common.text;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import java.util.regex.MatchResult;

public interface RichTextProcessor
{
    @NonNull SpannableStringBuilder process(@NonNull SpannableStringBuilder source);

    /**
     * The replacement may or may not contain elements like $1 to identify groups from the main pattern.
     * @param matchResult
     * @return the replacement
     */
    @NonNull String getExtractionPattern(@NonNull MatchResult matchResult);

    @NonNull String key();
}
