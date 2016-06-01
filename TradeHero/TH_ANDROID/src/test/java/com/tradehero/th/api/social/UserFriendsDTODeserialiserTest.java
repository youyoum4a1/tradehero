package com.ayondo.academy.api.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ayondo.academyRobolectricTestRunner;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.common.utils.IOUtils;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.BaseApiTestClass;
import com.ayondo.academy.base.TestTHApp;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedString;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UserFriendsDTODeserialiserTest extends BaseApiTestClass
{
    @Inject @ForApp ObjectMapper objectMapper;
    @Inject Converter retrofitConverter;
    private InputStream userFriendsDTOListBody1WindyLinkedInStream;

    @Before public void setUp()
    {
        TestTHApp.staticInject(this);
        userFriendsDTOListBody1WindyLinkedInStream = getClass().getResourceAsStream(getPackagePath() + "/UserFriendsDTOListBody1WindyLinkedin.json");
    }

    @Test public void canDeserialiseWindyLinkedin() throws IOException
    {
        UserFriendsDTOList windyList = objectMapper.readValue(userFriendsDTOListBody1WindyLinkedInStream, UserFriendsDTOList.class);
        assertThat(windyList.size()).isEqualTo(20);
        assertEquals(UserFriendsLinkedinDTO.class, windyList.get(0).getClass());
    }

    @Test public void canConvertWindyLinkedin() throws ConversionException, IOException
    {
        String asAString = new String(IOUtils.streamToBytes(userFriendsDTOListBody1WindyLinkedInStream));
        UserFriendsDTOList windyList = (UserFriendsDTOList) retrofitConverter.fromBody(new TypedString(asAString) , UserFriendsDTOList.class);
        assertThat(windyList.size()).isEqualTo(20);
        assertEquals(UserFriendsLinkedinDTO.class, windyList.get(0).getClass());
    }
}
