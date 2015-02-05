package com.tradehero.th.api.social;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.base.TestTHApp;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class UserFriendsDTOFactoryTest
{
    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @Test public void recogniseFacebook()
    {
        UserFriendsFacebookDTO dto = (UserFriendsFacebookDTO) UserFriendsDTOFactory.createFrom("fb", "456");
        //noinspection ConstantConditions
        assertThat(dto.fbId).isEqualTo("456");
    }

    @Test public void recogniseLinkedin()
    {
        UserFriendsLinkedinDTO dto = (UserFriendsLinkedinDTO) UserFriendsDTOFactory.createFrom("li", "430");
        //noinspection ConstantConditions
        assertThat(dto.liId).isEqualTo("430");
    }

    @Test public void recogniseTwitter()
    {
        UserFriendsTwitterDTO dto = (UserFriendsTwitterDTO) UserFriendsDTOFactory.createFrom("tw", "416");
        //noinspection ConstantConditions
        assertThat(dto.twId).isEqualTo("416");
    }

    @Test public void nullWhenUnrecognised()
    {
        assertThat(UserFriendsDTOFactory.createFrom("whateva", "34")).isNull();
    }
}
