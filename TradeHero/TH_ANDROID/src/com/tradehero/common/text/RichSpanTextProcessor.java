package com.tradehero.common.text;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Pair;
import com.tradehero.common.utils.THLog;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Created with IntelliJ IDEA. User: tho Date: 9/18/13 Time: 2:30 PM Copyright (c) TradeHero */
public abstract class RichSpanTextProcessor implements RichTextProcessor
{
    private Map<Object, Pair<Integer, Integer>> textMarkers;

    public RichSpanTextProcessor()
    {
        textMarkers = new HashMap<>();
    }

    @Override public SpannableStringBuilder process(SpannableStringBuilder source)
    {
        Pattern pattern = getPattern();
        Matcher match = pattern.matcher(source);

        int currentLength = 0;
        int spanned = 0;
        int originalLength = source.length();
        while (match.find())
        {
            String replacement = match.group(1);
            source.replace(match.start() - spanned, match.end() - spanned, replacement);
            currentLength = match.start() - spanned + replacement.length();
            spanned = originalLength - source.length();

            textMarkers.put(getSpanElement(replacement), new Pair<>(currentLength - replacement.length(), currentLength));
        }

        for (Map.Entry<Object, Pair<Integer, Integer>> marker : textMarkers.entrySet())
        {
            source.setSpan(marker.getKey(), marker.getValue().first, marker.getValue().second, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return source;
    }

    protected abstract Object getSpanElement(String replacement);

    protected abstract Pattern getPattern();
}
