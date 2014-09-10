package com.tradehero.th.api.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.th.api.BaseApiTestClass;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class PaginatedUserFriendsDTOListDeserialiserTest extends BaseApiTestClass
{
    @Inject @ForApp ObjectMapper normalMapper;

    private InputStream paginatedUserFriendsDTOListBody1;

    @Before
    public void setUp() throws IOException
    {
        paginatedUserFriendsDTOListBody1 = getClass().getResourceAsStream(getPackagePath() + "/PaginatedUserFriendsDTOListBody1.json");
    }

    @Test
    public void testNormalDeserialiseBody1() throws IOException
    {
        PaginatedUserFriendsDTOList converted = normalMapper.readValue(paginatedUserFriendsDTOListBody1, PaginatedUserFriendsDTOList.class);

        assertThat(((UserFriendsFacebookDTO) converted.getData().get(0)).fbId).isEqualTo("100000000532388");

        assertThat(converted.getPagination().next.page).isEqualTo(2);

    }

}
