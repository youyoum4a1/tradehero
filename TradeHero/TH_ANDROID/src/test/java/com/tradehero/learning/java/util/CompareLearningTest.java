package com.tradehero.learning.java.util;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/** Created with IntelliJ IDEA. User: xavier Date: 10/24/13 Time: 7:19 PM To change this template use File | Settings | File Templates. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class CompareLearningTest
{
    public static final String TAG = CompareLearningTest.class.getSimpleName();

    @Test public void leftBigger_shouldReturn1()
    {
        assertThat(new Integer(12).compareTo(11), equalTo(1));
    }

    @Test public void leftSmaller_shouldReturnMinus1()
    {
        assertThat(new Integer(12).compareTo(13), equalTo(-1));
    }

    @Test public void equal_shouldReturn0()
    {
        assertThat(new Integer(12).compareTo(12), equalTo(0));
    }

    @Test public void treeSet_shouldOrderCrescendo()
    {
        Set<Integer> treeSet = new TreeSet<>();
        treeSet.add(10);
        treeSet.add(5);

        Iterator<Integer> iterator = treeSet.iterator();
        assertThat(iterator.next(), equalTo(5));
        assertTrue(iterator.hasNext());
        assertThat(iterator.next(), equalTo(10));
    }
}
