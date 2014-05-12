package com.tradehero.learning;

import android.support.v4.util.LruCache;
import java.util.HashMap;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ObjectLearningTest
{
    public static final String TAG = ObjectLearningTest.class.getSimpleName();

    public class MappedKeyWithHashCode
    {
        final private Integer value;

        public MappedKeyWithHashCode(int value)
        {
            super();
            this.value = value;
        }

        @Override public int hashCode()
        {
            return this.value.hashCode();
        }
    }

    public class MappedKeyWithEquals
    {
        final private Integer value;

        public MappedKeyWithEquals(int value)
        {
            super();
            this.value = value;
        }

        @Override public boolean equals(Object other)
        {
            if (other == null || !(other instanceof MappedKeyWithEquals))
            {
                return false;
            }
            return value.equals(((MappedKeyWithEquals) other).value);
        }
    }

    public class MappedKeyWithEqualsAndHashCode
    {
        final private Integer value;

        public MappedKeyWithEqualsAndHashCode(int value)
        {
            super();
            this.value = value;
        }

        @Override public int hashCode()
        {
            return this.value.hashCode();
        }

        @Override public boolean equals(Object other)
        {
            if (other == null || !(other instanceof MappedKeyWithEqualsAndHashCode))
            {
                return false;
            }
            return value.equals(((MappedKeyWithEqualsAndHashCode) other).value);
        }
    }

    private Map<Object, Object> mapForTest;
    private LruCache<Object, Object> lruCacheForTest;
    MappedKeyWithHashCode hKeyA = new MappedKeyWithHashCode(1);
    MappedKeyWithHashCode hKeyB = new MappedKeyWithHashCode(1);
    MappedKeyWithHashCode hKeyC = new MappedKeyWithHashCode(2);
    MappedKeyWithEquals eKeyA = new MappedKeyWithEquals(1);
    MappedKeyWithEquals eKeyB = new MappedKeyWithEquals(1);
    MappedKeyWithEquals eKeyC = new MappedKeyWithEquals(2);
    MappedKeyWithEqualsAndHashCode aKeyA = new MappedKeyWithEqualsAndHashCode(1);
    MappedKeyWithEqualsAndHashCode aKeyB = new MappedKeyWithEqualsAndHashCode(1);
    MappedKeyWithEqualsAndHashCode aKeyC = new MappedKeyWithEqualsAndHashCode(2);

    @Before public void setUp()
    {
        mapForTest = new HashMap<>();
        lruCacheForTest = new LruCache<>(20);
    }

    @Test public void hashCode_shouldBeAsAsked() throws Exception
    {
        assertTrue(hKeyA.hashCode() == hKeyB.hashCode());
        assertFalse(hKeyA.hashCode() == hKeyC.hashCode());
        assertFalse(hKeyB.hashCode() == hKeyC.hashCode());

        assertFalse(eKeyA.hashCode() == eKeyB.hashCode());
        assertFalse(eKeyA.hashCode() == eKeyC.hashCode());
        assertFalse(eKeyB.hashCode() == eKeyC.hashCode());

        assertTrue(aKeyA.hashCode() == aKeyB.hashCode());
        assertFalse(aKeyA.hashCode() == aKeyC.hashCode());
        assertFalse(aKeyB.hashCode() == aKeyC.hashCode());
    }

    @Test public void equals_shouldBeAsAsked() throws Exception
    {
        assertFalse(hKeyA.equals(hKeyB));
        assertFalse(hKeyA.equals(hKeyC));
        assertFalse(hKeyB.equals(hKeyC));

        assertTrue(eKeyA.equals(eKeyB));
        assertFalse(eKeyA.equals(eKeyC));
        assertFalse(eKeyB.equals(eKeyC));

        assertTrue(aKeyA.equals(aKeyB));
        assertFalse(aKeyA.equals(aKeyC));
        assertFalse(aKeyB.equals(aKeyC));
    }

    @Test public void mapHashCode_shouldNotBeEnough() throws Exception
    {
        mapForTest.put(hKeyA, new Object());
        assertTrue(mapForTest.containsKey(hKeyA));
        assertFalse(mapForTest.containsKey(hKeyB));
        assertFalse(mapForTest.containsKey(hKeyC));
    }

    @Test public void lruCacheHashCode_shouldNotBeEnough() throws Exception
    {
        lruCacheForTest.put(hKeyA, new Object());
        assertTrue(lruCacheForTest.get(hKeyA) != null);
        assertTrue(lruCacheForTest.get(hKeyB) == null);
        assertTrue(lruCacheForTest.get(hKeyC) == null);
    }

    @Test public void mapEquals_shouldNotBeEnough() throws Exception
    {
        mapForTest.put(eKeyA, new Object());
        assertTrue(mapForTest.containsKey(eKeyA));
        assertFalse(mapForTest.containsKey(eKeyB));
        assertFalse(mapForTest.containsKey(eKeyC));
    }

    @Test public void lruCacheEquals_shouldNotBeEnough() throws Exception
    {
        lruCacheForTest.put(eKeyA, new Object());
        assertTrue(lruCacheForTest.get(eKeyA) != null);
        assertTrue(lruCacheForTest.get(eKeyB) == null);
        assertTrue(lruCacheForTest.get(eKeyC) == null);
    }

    @Test public void mapEqualsAndHashCode_shouldBeEnough() throws Exception
    {
        mapForTest.put(aKeyA, new Object());
        assertTrue(mapForTest.containsKey(aKeyA));
        assertTrue(mapForTest.containsKey(aKeyB));
        assertFalse(mapForTest.containsKey(aKeyC));
    }

    @Test public void lruCacheEqualsAndHashCode_shouldBeEnough() throws Exception
    {
        lruCacheForTest.put(aKeyA, new Object());
        assertTrue(lruCacheForTest.get(aKeyA) != null);
        assertTrue(lruCacheForTest.get(aKeyB) != null);
        assertTrue(lruCacheForTest.get(aKeyC) == null);
    }
}
