package com.tradehero.th.rx;

import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by thonguyen on 23/10/14.
 */
public class PaginationObservable
{
    public static <T extends Comparable<T>> Observable<List<T>> create(Observable<List<T>> listObservable)
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
                                T first = collector.getFirst();
                                T last = collector.getLast();

                                T newFirst = newList.get(0);
                                T newLast = newList.get(newList.size() - 1);

                                boolean isFirstNewItemOutsideBound =
                                        ((int) Math.signum(first.compareTo(newFirst))) * ((int) Math.signum(last.compareTo(newFirst))) > 0;
                                boolean isLastNewItemOutsideBound =
                                        ((int) Math.signum(first.compareTo(newLast))) * ((int) Math.signum(last.compareTo(newLast))) > 0;

                                if (isFirstNewItemOutsideBound && isLastNewItemOutsideBound)
                                {
                                    boolean isSmallerNewList =
                                            ((int) Math.signum(first.compareTo(newFirst))) * ((int) Math.signum(first.compareTo(last))) < 0;
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
                                    int outBound = -1;
                                    for (T item : newList)
                                    {
                                        boolean isOut = ((int) Math.signum(first.compareTo(item))) * ((int) Math.signum(last.compareTo(item))) < 0;
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
                                            isOut = ((int) Math.signum(first.compareTo(item))) * ((int) Math.signum(last.compareTo(item))) < 0;
                                        }
                                    }
                                }
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
}
