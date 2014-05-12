package com.tradehero.th.loaders;

import android.content.Context;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
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
