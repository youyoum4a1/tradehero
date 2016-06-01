package com.ayondo.academy.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;
import java.util.WeakHashMap;

public class FontUtil
{
    private final WeakHashMap<FontType, Typeface> fontTypeToTypeFaceMap = new WeakHashMap<>();

    public void setTypeFace(@NonNull Context context, @Nullable TextView textView, @NonNull FontType fontType)
    {
        if (textView != null)
        {
            Typeface typeFace = fontTypeToTypeFaceMap.get(fontType);
            if (typeFace == null)
            {
                typeFace = Typeface.createFromAsset(context.getAssets(), fontType.embeddedFont);
                if (typeFace == null)
                {
                    return;
                }
                fontTypeToTypeFaceMap.put(fontType, typeFace);
            }
            textView.setTypeface(typeFace);
        }
    }

    public enum FontType
    {
        AWESOME("FontAwesome.ttf"),
        EightBitsWonder("8-BIT WONDER-1.TTF");

        final String embeddedFont;

        FontType(String embeddedFont)
        {
            this.embeddedFont = embeddedFont;
        }
    }
}
