package com.tradehero.common.text;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RichSpanTextProcessor implements RichTextProcessor
{
    @Override public SpannableStringBuilder process(SpannableStringBuilder source)
    {
        Map<Object, Pair<Integer, Integer>> markers = new HashMap<>();
        Pattern pattern = getPattern();
        Matcher match = pattern.matcher(source.toString());

        int originalLength = source.length();
        StringBuffer sb = new StringBuffer();

        while (match.find())
        {
            int removedCharacters = originalLength - source.length();

            // source has changed after every replacement, update matching position
            int correctedMatchingStart = match.start() - removedCharacters;
            int correctedMatchingEnd = match.end() - removedCharacters;

            // extract text element
            match.appendReplacement(sb, getExtractionPattern());
            String textElement = sb.substring(correctedMatchingStart);

            // update span text
            source.replace(correctedMatchingStart, correctedMatchingEnd, textElement);

            // mark new span element
            Object spanElement = getSpanElement(textElement, match);
            markers.put(spanElement, new Pair<>(correctedMatchingStart, correctedMatchingStart + textElement.length()));
        }

        for (Map.Entry<Object, Pair<Integer, Integer>> marker : markers.entrySet())
        {
            source.setSpan(marker.getKey(), marker.getValue().first, marker.getValue().second, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return source;
    }

    private Object getSpanElement(String textElement, Matcher match)
    {
        String[] matchStrings = new String[match.groupCount() + 1];
        for (int i = 0; i <= match.groupCount(); ++i)
        {
            matchStrings[i] = match.group(i);
        }
        return getSpanElement(textElement, matchStrings);
    }

    @Override public abstract String getExtractionPattern();

    protected abstract Span getSpanElement(String replacement, String[] matchStrings);

    protected abstract Pattern getPattern();
}
