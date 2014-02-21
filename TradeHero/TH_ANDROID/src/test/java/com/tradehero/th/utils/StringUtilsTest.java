package com.tradehero.th.utils;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 5:12 PM To change this template use File | Settings | File Templates. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)public class StringUtilsTest
{
    public static final String TAG = StringUtilsTest.class.getSimpleName();

    @Test public void joinListNull_shouldReturnNull()
    {
        assertNull(StringUtils.join(",", (List<String>) null));
    }

    @Test public void joinListEmpty_shouldReturnNull()
    {
        assertNull(StringUtils.join(",", new ArrayList<String>()));
    }

    @Test public void joinListOne_shouldReturnSame()
    {
        List<String> toJoin = new ArrayList<String>();
        toJoin.add("a");
        assertThat(StringUtils.join(",", toJoin), equalTo("a"));
    }

    @Test public void joinListTwo_shouldReturnOk()
    {
        List<String> toJoin = new ArrayList<String>();
        toJoin.add("a");
        toJoin.add("b");
        assertThat(StringUtils.join(",", toJoin), equalTo("a,b"));
    }

    @Test public void joinArrayNull_shouldReturnNull()
    {
        assertNull(StringUtils.join(",", (String[]) null));
    }

    @Test public void joinArrayEmpty_shouldReturnNull()
    {
        assertNull(StringUtils.join(",", new String[0]));
    }

    @Test public void joinArrayOne_shouldReturnSame()
    {
        assertThat(StringUtils.join(",", new String[] {"a"}), equalTo("a"));
    }

    @Test public void joinArrayTwo_shouldReturnOk()
    {
        assertThat(StringUtils.join(",", new String[]{"a", "b"}), equalTo("a,b"));
    }

    @Test public void joinParamNull_shouldReturnNull()
    {
        assertNull(StringUtils.join(","));
    }

    @Test public void joinParamOne_shouldReturnSame()
    {
        assertThat(StringUtils.join(",", "a"), equalTo("a"));
    }

    @Test public void joinParamTwo_shouldReturnOk()
    {
        assertThat(StringUtils.join(",", "a", "b"), equalTo("a,b"));
    }
}
