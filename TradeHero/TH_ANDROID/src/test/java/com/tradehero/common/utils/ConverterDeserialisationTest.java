package com.tradehero.common.utils;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;

import static com.tradehero.util.TestUtil.getResourceAsByteArray;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class ConverterDeserialisationTest
{
    private static final String JSON_MIME_UTF8 = "application/json; charset=utf-8";

    @Inject Converter converter;

    @Before public void setUp()
    {
        DaggerUtils.inject(this);
    }

    @After public void tearDown()
    {
    }

    @Test public void shouldCreatePortfolioDTO1() throws Exception
    {
        byte[] jsonFile = getResourceAsByteArray(PortfolioDTO.class, "PortfolioDTOBody1.json");
        TypedByteArray typedInput = new TypedByteArray(JSON_MIME_UTF8, jsonFile);
        Object converted = converter.fromBody(typedInput, PortfolioDTO.class);
        assertThat(converted, instanceOf(PortfolioDTO.class));
        assertThat(((PortfolioDTO)converted).id, equalTo(274889));
    }

    @Test public void shouldCreateGetPositionsDTO1() throws Exception
    {
        byte[] jsonFile = getResourceAsByteArray(GetPositionsDTO.class, "GetPositionsDTOBody1.json");
        TypedByteArray typedInput = new TypedByteArray(JSON_MIME_UTF8, jsonFile);

        Object converted = converter.fromBody(typedInput, GetPositionsDTO.class);
        assertThat(converted, instanceOf(GetPositionsDTO.class));
    }

    @Test public void shouldCreateGetPositionsDTO2() throws Exception
    {
        File jsonFile = getLocalFile("GetPositionsDTOBody2.json");
        TypedInput typedInputFile = new TypedFile("application/json; charset=utf-8", jsonFile);
        Object converted = converter.fromBody(typedInputFile, GetPositionsDTO.class);
        assertThat(converted, instanceOf(GetPositionsDTO.class));
        assertThat(((GetPositionsDTO) converted).openPositionsCount, equalTo(10));
    }

    @Test public void canParseDate() throws ConversionException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));

        TypedInput typedInput = THJsonAdapter.toTypedInput("2013-04-23T12:30:40");
        Object myDate = converter.fromBody(typedInput, Date.class);
        assertTrue(myDate instanceof Date);
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.setTime((Date) myDate);
        assertEquals(2013, myCalendar.get(Calendar.YEAR));
    }
}
