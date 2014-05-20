package com.tradehero.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import retrofit.converter.ConversionException;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class JacksonConverterDeserialisationTest
{
    private JacksonConverter jacksonConverter;
    private String localPath = "C:\\Users\\xavier\\Documents\\Projects\\TH_ANDROID3\\TradeHero\\TH_ANDROID\\src\\test\\java\\com\\tradehero\\common\\";

    @Before public void setUp()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        jacksonConverter = new JacksonConverter(objectMapper);
    }

    @After public void tearDown()
    {
    }

    private File getLocalFile(String fileName)
    {
        return new File(localPath + fileName);
        //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //URL url = classLoader.getResource(".");
        //return new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    @Test public void shouldCreatePortfolioDTO1() throws Exception
    {
        File jsonFile = getLocalFile("PortfolioDTOBody1.json");
        TypedInput typedInputFile = new TypedFile("application/json; charset=utf-8", jsonFile);
        Object converted = jacksonConverter.fromBody(typedInputFile, PortfolioDTO.class);
        assertThat(converted, instanceOf(PortfolioDTO.class));
        assertThat(((PortfolioDTO)converted).id, equalTo(274889));
    }

    @Test public void shouldCreateGetPositionsDTO1() throws Exception
    {
        File jsonFile = getLocalFile("GetPositionsDTOBody1.json");
        TypedInput typedInputFile = new TypedFile("application/json; charset=utf-8", jsonFile);
        Object converted = jacksonConverter.fromBody(typedInputFile, GetPositionsDTO.class);
        assertThat(converted, instanceOf(GetPositionsDTO.class));
    }

    @Test public void shouldCreateGetPositionsDTO2() throws Exception
    {
        File jsonFile = getLocalFile("GetPositionsDTOBody2.json");
        TypedInput typedInputFile = new TypedFile("application/json; charset=utf-8", jsonFile);
        Object converted = jacksonConverter.fromBody(typedInputFile, GetPositionsDTO.class);
        assertThat(converted, instanceOf(GetPositionsDTO.class));
        assertThat(((GetPositionsDTO) converted).openPositionsCount, equalTo(10));
    }

    @Test public void canParseDate() throws ConversionException
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        jacksonConverter = new JacksonConverter(objectMapper);

        TypedInput typedInput = THJsonAdapter.toTypedInput("2013-04-23T12:30:40");
        Object myDate = jacksonConverter.fromBody(typedInput, Date.class);
        assertTrue(myDate instanceof Date);
        Calendar myCalendar = Calendar.getInstance();
        myCalendar.setTime((Date) myDate);
        assertEquals(2013, myCalendar.get(Calendar.YEAR));
    }
}
