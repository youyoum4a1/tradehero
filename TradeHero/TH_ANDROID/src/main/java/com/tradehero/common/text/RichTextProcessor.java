package com.tradehero.common.text;

import android.text.SpannableStringBuilder;


public interface RichTextProcessor
{
    SpannableStringBuilder process(SpannableStringBuilder source);

    String getExtractionPattern();

    String key();
}
