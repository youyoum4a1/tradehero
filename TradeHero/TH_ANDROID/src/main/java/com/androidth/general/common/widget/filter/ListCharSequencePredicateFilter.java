package com.androidth.general.common.widget.filter;

import java.util.List;

public interface ListCharSequencePredicateFilter<T>
{
    void setDefaultPredicate(CharSequencePredicate<? super T> predicate);
    void setCharSequence(CharSequence charSequence);
    List<T> filter(List<? extends T> unfiltered);
    List<T> filter(List<? extends T> unfiltered, CharSequencePredicate<? super T> predicate);
}
