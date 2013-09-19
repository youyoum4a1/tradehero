package com.tradehero.common.text;

import android.text.SpannableStringBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Created with IntelliJ IDEA. User: tho Date: 9/19/13 Time: 4:43 PM Copyright (c) TradeHero */
public class SpecialCharacterProcessor implements RichTextProcessor
{
    private static Map<String, String> characterMap;

    private void initMap()
    {
        Map<String, String> map = new HashMap<>();
        map.put("\uE335", "\u2605"); //star
        characterMap = Collections.unmodifiableMap(map);
    }

    @Override public SpannableStringBuilder process(SpannableStringBuilder source)
    {
        if (characterMap == null)
        {
            initMap();
        }
        for (Map.Entry<String, String> entry: characterMap.entrySet())
        {
            String normalString = source.toString();

            int pos = source.length();
            while ((pos = normalString.lastIndexOf(entry.getKey(), pos-1)) > -1)
            {
                source.replace(pos, entry.getKey().length() + pos, entry.getValue());
            }

        }
        return source;
    }

    @Override public String getExtractionPattern()
    {
        return null;
    }

    @Override public String key()
    {
        return "specialCharacter";
    }
}
