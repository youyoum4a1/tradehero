package com.tradehero.common.text;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created with IntelliJ IDEA. User: tho Date: 9/18/13 Time: 2:30 PM Copyright (c) TradeHero */
public abstract class RichSpanTextProcessor implements RichTextProcessor
{
    @Override public SpannableStringBuilder process(SpannableStringBuilder source)
    {
        Map<Object, Pair<Integer, Integer>> textMarkers = new HashMap<>();
        Pattern pattern = getPattern();
        Matcher match = pattern.matcher(source);
        // TODO should use only one matcher to process the string, this is a hacky way
        Matcher match2 = pattern.matcher(source.toString());

        int currentPosition = 0;
        int spanned = 0;
        int originalLength = source.length();
        StringBuffer sb = new StringBuffer();
        while (match.find() && match2.find())
        {
            int realMatchingStart = match.start() - spanned;
            int realMatchingEnd = match.end() - spanned;

            match2.appendReplacement(sb, getExtractionPattern());

            String[] matchStrings = new String[match2.groupCount()+1];
            for (int i=0; i<=match2.groupCount(); ++i)
            {
                matchStrings[i] = match2.group(i);
            }

            String replacement = sb.substring(realMatchingStart);

            source.replace(realMatchingStart, realMatchingEnd, replacement);
            currentPosition = realMatchingStart + replacement.length();

            Object spanElement = getSpanElement(replacement, matchStrings);
            textMarkers.put(spanElement, new Pair<>(currentPosition - replacement.length(), currentPosition));
            spanned = originalLength - source.length();
        }

        for (Map.Entry<Object, Pair<Integer, Integer>> marker : textMarkers.entrySet())
        {
            source.setSpan(marker.getKey(), marker.getValue().first, marker.getValue().second, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return source;
    }

    @Override public abstract String getExtractionPattern();

    protected abstract Object getSpanElement(String replacement, String ... args);

    protected abstract Pattern getPattern();
}
