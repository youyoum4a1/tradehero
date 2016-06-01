package com.ayondo.academy.utils;

import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class StringUtilsTest
{
    @Test public void joinListNull_shouldReturnNull()
    {
        assertThat(StringUtils.join(",", (List<String>) null)).isNull();
    }

    @Test public void joinListEmpty_shouldReturnNull()
    {
        assertThat(StringUtils.join(",", new ArrayList<String>())).isNull();
    }

    @Test public void joinListOne_shouldReturnSame()
    {
        List<String> toJoin = new ArrayList<>();
        toJoin.add("a");
        assertThat(StringUtils.join(",", toJoin)).isEqualTo("a");
    }

    @Test public void joinListTwo_shouldReturnOk()
    {
        List<String> toJoin = new ArrayList<>();
        toJoin.add("a");
        toJoin.add("b");
        assertThat(StringUtils.join(",", toJoin)).isEqualTo("a,b");
    }

    @Test public void joinArrayNull_shouldReturnNull()
    {
        assertThat(StringUtils.join(",", (Object[]) null)).isNull();
    }

    @Test public void joinArrayEmpty_shouldReturnNull()
    {
        assertThat(StringUtils.join(",", (Object[]) new String[0])).isNull();
    }

    @Test public void joinArrayOne_shouldReturnSame()
    {
        assertThat(StringUtils.join(",", (Object[]) new String[] {"a"})).isEqualTo("a");
    }

    @Test public void joinArrayTwo_shouldReturnOk()
    {
        assertThat(StringUtils.join(",", (Object[]) new String[]{"a", "b"})).isEqualTo("a,b");
    }

    @Test public void joinParamNull_shouldReturnNull()
    {
        assertThat(StringUtils.join(","));
    }

    @Test public void joinParamOne_shouldReturnSame()
    {
        assertThat(StringUtils.join(",", "a")).isEqualTo("a");
    }

    @Test public void joinParamTwo_shouldReturnOk()
    {
        assertThat(StringUtils.join(",", "a", "b")).isEqualTo("a,b");
    }
}
