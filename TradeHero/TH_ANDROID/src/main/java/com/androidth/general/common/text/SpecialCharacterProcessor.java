package com.androidth.general.common.text;

import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.MatchResult;

public class SpecialCharacterProcessor implements RichTextProcessor
{
    private static Map<String, String> characterMap;

    private void initMap()
    {
        Map<String, String> map = new HashMap<>();
        map.put("\uE335", "\u2605"); //star
        characterMap = Collections.unmodifiableMap(map);
    }

    @NonNull @Override public SpannableStringBuilder process(@NonNull SpannableStringBuilder source)
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

    @NonNull @Override public String getExtractionPattern(@NonNull MatchResult matchResult)
    {
        return null;
    }

    @NonNull @Override public String key()
    {
        return "specialCharacter";
    }
}
