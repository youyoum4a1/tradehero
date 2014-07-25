package com.tradehero.th.api.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.th.api.BaseApiTest;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class UserFriendsDTODeserialiserTest extends BaseApiTest
{
    @Inject @ForApp ObjectMapper objectMapper;
    private InputStream userFriendsDTOListBody1WindyLinkedInStream;

    @Before public void setUp()
    {
        userFriendsDTOListBody1WindyLinkedInStream = getClass().getResourceAsStream(getPackagePath() + "/UserFriendsDTOListBody1WindyLinkedin.json");
    }

    @Test public void canDeserialiseWindyLinkedin() throws IOException
    {
        UserFriendsDTOList windyList = objectMapper.readValue(userFriendsDTOListBody1WindyLinkedInStream, UserFriendsDTOList.class);
        assertThat(windyList.size()).isEqualTo(20);
        assertEquals(UserFriendsLinkedinDTO.class, windyList.get(0).getClass());
    }
}
