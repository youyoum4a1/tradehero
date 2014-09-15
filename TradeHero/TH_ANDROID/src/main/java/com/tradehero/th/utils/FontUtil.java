package com.tradehero.th.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;
import java.util.WeakHashMap;
import javax.inject.Inject;
import javax.inject.Singleton;

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

    public static int px2dip(Context context, float pxValue)
    {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue)
    {

        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue)
    {

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue)
    {

        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
