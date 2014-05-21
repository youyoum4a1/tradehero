package com.tradehero.common.widget.filter;

import com.android.internal.util.Predicate;

public interface CharSequencePredicate<T> extends Predicate<T>
{
    CharSequence getCharSequence();
    void setCharSequence(CharSequence charSequence);
}
