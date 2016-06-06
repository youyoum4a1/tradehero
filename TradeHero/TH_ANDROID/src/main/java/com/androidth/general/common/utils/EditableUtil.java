package com.androidth.general.common.utils;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import com.androidth.general.common.text.Span;

public class EditableUtil
{
    @NonNull public static Editable unSpanText(@NonNull Editable editable)
    {
        // keep editable unchange
        SpannableStringBuilder editableCopy = new SpannableStringBuilder(editable);
        Span[] spans = editableCopy.getSpans(0, editableCopy.length(), Span.class);

        // replace all span string with its original text
        for (int i = spans.length - 1; i >= 0; --i)
        {
            Span span = spans[i];
            int spanStart = editableCopy.getSpanStart(span);
            int spanEnd = editableCopy.getSpanEnd(span);

            editableCopy = editableCopy.replace(spanStart, spanEnd, span.getOriginalText());
        }
        return editableCopy;
    }
}
