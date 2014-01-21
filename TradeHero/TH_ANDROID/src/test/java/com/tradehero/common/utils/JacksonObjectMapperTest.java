package com.tradehero.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import retrofit.converter.ConversionException;
import retrofit.mime.TypedInput;

import static org.junit.Assert.assertEquals;

/**
 * Created by xavier on 1/21/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class JacksonObjectMapperTest
{
    public static final String TAG = JacksonObjectMapperTest.class.getSimpleName();

    @Test public void canParseDate() throws ConversionException, IOException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        Date now = new Date();
        System.out.println(objectMapper.getDeserializationConfig().getDateFormat().format(now));

        TypedInput typedInput = THJsonAdapter.toTypedInput("2013-04-23T12:30:40");
        //StringWriter writer = new StringWriter();
        //IOUtils.copy(typedInput.in(), writer, "UTF-8");
        //System.out.println(writer.toString());

        Date myDate = objectMapper.readValue(typedInput.in(), Date.class);
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.setTime((Date) myDate);
        assertEquals(2013, myCalendar.get(Calendar.YEAR));
    }
}
