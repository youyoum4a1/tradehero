package com.tradehero.th.rx;

import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;

public class PaginationObservable
{
    /**
     * Create pagination observable from an observable of a sorted list of comparable items
     * @param listObservable input observable of a sorted list of comparable items
     * @param <T> comparable type of item
     * @return pagination observable
     */
    public static <T extends Comparable<T>> Observable<List<T>> create(Observable<List<T>> listObservable)
    {
        return listObservable
                .scan(new LinkedList<T>(), (collector, newList) -> {
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
                                    collector.addAll(0, newList);
                                }
                                else
                                {
                                    collector.addAll(newList);
                                }
                                return collector;
                            }
                            else if (!isFirstNewItemOutsideBound && !isLastNewItemOutsideBound)
                            {
                                return collector;
                            }

                            if (isFirstNewItemOutsideBound)
                            {
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
                        }
                    }

                    return collector;
                })
                .map(ts -> ts);
    }

    private static <T extends Comparable<T>> boolean checkInsideSegment(T left, T right, T obj)
    {
        return getSegmentSign(left, right, obj) < 0;
    }

    private static <T extends Comparable<T>> boolean checkOutsideSegment(T left, T right, T obj)
    {
        return getSegmentSign(left, right, obj) > 0;
    }

    private static <T extends Comparable<T>> int getSegmentSign(T left, T right, T obj)
    {
        return ((int) Math.signum(left.compareTo(obj))) * ((int) Math.signum(right.compareTo(obj)));
    }

    public static <K, T extends Comparable<T>> Observable<List<T>> createFromRange(Observable<K> rangeObservable,
            Func1<K, Observable<List<T>>> fetchFunc)
    {
        return create(rangeObservable.flatMap(fetchFunc));
    }
}
