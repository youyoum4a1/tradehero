package com.tradehero.th.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;
import java.util.WeakHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/24/14 Time: 6:24 PM Copyright (c) TradeHero
 */
@Singleton
public class FontUtil
{
    private final Context context;
    private final WeakHashMap<FontType, Typeface> fontTypeToTypeFaceMap;

    @Inject public FontUtil(Context context)
    {
        this.context = context;
        fontTypeToTypeFaceMap = new WeakHashMap<>();
    }

    public void setTypeFace(TextView textView, FontType fontType)
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
