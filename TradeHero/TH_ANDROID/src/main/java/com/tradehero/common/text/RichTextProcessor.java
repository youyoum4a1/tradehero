package com.tradehero.common.text;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

/** Created with IntelliJ IDEA. User: tho Date: 9/17/13 Time: 3:40 PM Copyright (c) TradeHero */
public interface RichTextProcessor
{
    SpannableStringBuilder process(SpannableStringBuilder source);

    String getExtractionPattern();

    String key();
}
