package com.tradehero.common.widget.filter;

import com.android.internal.util.Predicate;

/**
 * Created by xavier on 1/24/14.
 */
public interface CharSequencePredicate<T> extends Predicate<T>
{
    CharSequence getCharSequence();
    void setCharSequence(CharSequence charSequence);
}
