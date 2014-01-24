package com.tradehero.common.widget.filter;

import com.android.internal.util.Predicate;
import java.util.List;

/**
 * Created by xavier on 1/24/14.
 */
public interface ListCharSequencePredicateFilter<T>
{
    void setDefaultPredicate(CharSequencePredicate<? super T> predicate);
    void setCharSequence(CharSequence charSequence);
    List<T> filter(List<T> unfiltered);
    List<T> filter(List<T> unfiltered, CharSequencePredicate<? super T> predicate);
}
