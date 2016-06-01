package com.ayondo.academy.rx;

import android.support.annotation.NonNull;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class PaginationObservable
{
    /**
     * Create pagination observable from an observable of a sorted list of comparable items
     * @param listObservable input observable of a sorted list of comparable items
     * @param <T> comparable type of item
     * @return pagination observable
     */
    @NonNull public static <T extends Comparable<T>> Observable<List<T>> create(@NonNull Observable<List<T>> listObservable)
    {
        return listObservable
                .scan(new LinkedList<T>(), new Func2<LinkedList<T>, List<T>, LinkedList<T>>()
                {
                    @Override public LinkedList<T> call(LinkedList<T> collector, List<T> newList)
                    {
                        int newListSize = newList.size();
                        if (newListSize > 0)
                        {
                            if (collector.size() == 0)
                            {
                                collector.addAll(newList);
                            }
                            else
                            {
                                // merge two sorted list the hard way, complex but supposed to be fast
                                return quickMerge(collector, newList);
                            }
                        }

                        return collector;
                    }
                })
                .map(new Func1<LinkedList<T>, List<T>>()
                {
                    @Override public List<T> call(LinkedList<T> ts)
                    {
                        return ts;
                    }
                });
    }

    @NonNull private static <T extends Comparable<T>> LinkedList<T> quickMerge(@NonNull LinkedList<T> collector, @NonNull List<T> newList)
    {
        T first = collector.getFirst();
        T last = collector.getLast();

        T newFirst = newList.get(0);
        T newLast = newList.get(newList.size() - 1);

        boolean isFirstNewItemOutsideBound = checkOutsideSegment(first, last, newFirst);
        boolean isLastNewItemOutsideBound = checkOutsideSegment(first, last, newLast);

        if (isFirstNewItemOutsideBound && isLastNewItemOutsideBound)
        {
            boolean isSmallerNewList = checkInsideSegment(newFirst, last, first);
            if (isSmallerNewList)
            {
                // newList       collector
                // [1-----5]     [8---------20]
                collector.addAll(0, newList);
            }
            else
            {
                // collector     newList
                // [8---------20] [23----30]
                collector.addAll(newList);
            }
            return collector;
        }
        else if (!isFirstNewItemOutsideBound && !isLastNewItemOutsideBound)
        {
            // not applicable, however return collector instead of throwing an error
            return collector;
        }

        if (isFirstNewItemOutsideBound)
        {
            //     collector
            //     [8---------20]
            // newList
            // [1-----11]
            int outBound = 0;
            for (T item : newList)
            {
                boolean isOut = checkOutsideSegment(first, last, item);
                if (isOut)
                {
                    ++outBound;
                }
                else
                {
                    break;
                }
            }
            collector.addAll(0, newList.subList(0, outBound));
        }

        if (isLastNewItemOutsideBound)
        {
            // collector
            // [8---------20]
            //       newList
            //       [18-----31]
            boolean isOut = false;
            for (T item : newList)
            {
                if (isOut)
                {
                    collector.add(item);
                }
                else
                {
                    isOut = checkOutsideSegment(first, last, item);
                }
            }
        }

        return collector;
    }

    private static <T extends Comparable<T>> boolean checkInsideSegment(@NonNull T left, @NonNull T right, @NonNull T obj)
    {
        return getSegmentSign(left, right, obj) < 0;
    }

    private static <T extends Comparable<T>> boolean checkOutsideSegment(@NonNull T left, @NonNull T right, @NonNull T obj)
    {
        return getSegmentSign(left, right, obj) > 0;
    }

    private static <T extends Comparable<T>> int getSegmentSign(@NonNull T left, @NonNull T right, @NonNull T obj)
    {
        return ((int) Math.signum(left.compareTo(obj))) * ((int) Math.signum(right.compareTo(obj)));
    }

    public static <K, T extends Comparable<T>> Observable<List<T>> createFromRange(@NonNull Observable<K> rangeObservable,
            @NonNull Func1<K, Observable<List<T>>> fetchFunc)
    {
        return create(rangeObservable.flatMap(fetchFunc));
    }
}
