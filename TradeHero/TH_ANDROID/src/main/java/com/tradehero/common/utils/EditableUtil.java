package com.tradehero.common.utils;

import android.text.Editable;
import android.text.SpannableStringBuilder;
import com.tradehero.common.text.Span;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class EditableUtil
{
    //<editor-fold desc="Constructors">
    @Inject public EditableUtil()
    {
    }
    //</editor-fold>

    @NotNull public Editable unSpanText(@NotNull Editable editable)
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
