package com.tradehero.th.api.social;

import com.tradehero.THRobolectricTestRunner;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class UserFriendsDTOFactoryTest
{
    @Inject protected UserFriendsDTOFactory userFriendsDTOFactory;

    @Test public void recogniseFacebook()
    {
        UserFriendsFacebookDTO dto = (UserFriendsFacebookDTO) userFriendsDTOFactory.createFrom("fb", "456");
        //noinspection ConstantConditions
        assertThat(dto.fbId).isEqualTo("456");
    }

    @Test public void recogniseLinkedin()
    {
        UserFriendsLinkedinDTO dto = (UserFriendsLinkedinDTO) userFriendsDTOFactory.createFrom("li", "430");
        //noinspection ConstantConditions
        assertThat(dto.liId).isEqualTo("430");
    }

    @Test public void recogniseTwitter()
    {
        UserFriendsTwitterDTO dto = (UserFriendsTwitterDTO) userFriendsDTOFactory.createFrom("tw", "416");
        //noinspection ConstantConditions
        assertThat(dto.twId).isEqualTo("416");
    }

    @Test public void nullWhenUnrecognised()
    {
        assertThat(userFriendsDTOFactory.createFrom("whateva", "34")).isNull();
    }
}
