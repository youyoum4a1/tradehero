package com.tradehero.th.api.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
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
public class PaginatedUserFriendsDTOListDeserialiserTest extends BaseApiTest
{
    @Inject ObjectMapper normalMapper;

    private InputStream securityCompactDTOBody1Stream;

    @Before
    public void setUp() throws IOException
    {
        securityCompactDTOBody1Stream = getClass().getResourceAsStream(getPackagePath() + "/PaginatedUserFriendsDTOListBody1.json");
    }

    @Test
    public void testNormalDeserialiseBody1() throws IOException
    {
        PaginatedUserFriendsDTOList converted = normalMapper.readValue(securityCompactDTOBody1Stream, PaginatedUserFriendsDTOList.class);

        assertThat(((UserFriendsFacebookDTO) converted.getData().get(0)).fbId).isEqualTo("100000000532388");

        assertThat(converted.getPagination().next.page).isEqualTo(2);

    }

}
