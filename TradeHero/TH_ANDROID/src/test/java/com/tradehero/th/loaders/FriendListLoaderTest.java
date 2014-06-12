package com.tradehero.th.loaders;

import android.content.Context;
import com.tradehero.RobolectricMavenTestRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

@RunWith(RobolectricMavenTestRunner.class)
public class FriendListLoaderTest
{
    private Context context;

    @Before public void setUp()
    {
        context = Robolectric.getShadowApplication().getApplicationContext();
    }

    @After public void tearDown()
    {

    }

    @Test public void shouldReceiveContactList()
    {
        FriendListLoader friendListLoader = new FriendListLoader(context);
        friendListLoader.loadInBackground();
    }
}
