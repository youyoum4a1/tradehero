package com.tradehero.learning.java.util;

import com.tradehero.th.api.security.WarrantDTO;
import java.util.TreeSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class TreeSetLearningTest
{
    private TreeSet<WarrantDTO> treeSet;

    @Before public void setUp()
    {
    }

    @After public void tearDown()
    {
    }

    @Test(expected = NullPointerException.class)
    public void cannotAddNull()
    {
        treeSet.add(null);
    }

    @Test(expected = NullPointerException.class)
    public void cannotAddAllNull()
    {
        treeSet.addAll(null);
    }
}
