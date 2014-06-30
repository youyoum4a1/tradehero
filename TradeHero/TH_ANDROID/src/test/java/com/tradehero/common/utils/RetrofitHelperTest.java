package com.tradehero.common.utils;

import com.tradehero.RobolectricMavenTestRunner;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit.client.Header;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricMavenTestRunner.class)
public class RetrofitHelperTest
{
    private static final String HEADER_TO_SEARCH = "X-TH-CUSTOM-HEADER";

    @Inject RetrofitHelper retrofitHelper;
    private List<Header> headers;

    @Before
    public void setUp()
    {
        headers = new ArrayList<Header>();
    }

    @After
    public void tearDown()
    {
        headers = null;
    }

    @Test public void findHeaderByNameReturnsCorrectHeader()
    {
        headers.add(new Header("X-Auth", String.valueOf(Math.random())));
        headers.add(new Header(HEADER_TO_SEARCH, String.valueOf(Math.random())));
        headers.add(new Header("X-Custom-Header2", String.valueOf(Math.random())));

        Header found = retrofitHelper.findHeaderByName(headers, HEADER_TO_SEARCH);
        assertEquals(found.getName(), HEADER_TO_SEARCH);
    }

    @Test public void findHeaderByNameReturnsNull()
    {
        headers.add(new Header("X-Auth", String.valueOf(Math.random())));
        headers.add(new Header("X-Custom-Header2", String.valueOf(Math.random())));

        Header found = retrofitHelper.findHeaderByName(headers, HEADER_TO_SEARCH);
        assertNull(found);
    }

    @Test public void findHeaderWithNullNameShouldNotFail()
    {
        headers.add(new Header(null, String.valueOf(Math.random())));

        Header found = retrofitHelper.findHeaderByName(headers, HEADER_TO_SEARCH);
        assertNull(found);
    }
}
