package com.tradehero.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.utils.JacksonConverter;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import java.io.File;
import java.net.URL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.bytecode.Setup;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/** Created with IntelliJ IDEA. User: xavier Date: 10/17/13 Time: 10:32 AM To change this template use File | Settings | File Templates. */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class JacksonConverterTest
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
        File jsonFile = getLocalFile("GetPositionsDTOBody1.js");
        TypedInput typedInputFile = new TypedFile("application/json; charset=utf-8", jsonFile);
        Object converted = jacksonConverter.fromBody(typedInputFile, GetPositionsDTO.class);
        assertThat(converted, instanceOf(GetPositionsDTO.class));
    }

    @Test public void shouldCreateGetPositionsDTO2() throws Exception
    {
        File jsonFile = getLocalFile("GetPositionsDTOBody2.js");
        TypedInput typedInputFile = new TypedFile("application/json; charset=utf-8", jsonFile);
        Object converted = jacksonConverter.fromBody(typedInputFile, GetPositionsDTO.class);
        assertThat(converted, instanceOf(GetPositionsDTO.class));
        assertThat(((GetPositionsDTO) converted).openPositionsCount, equalTo(10));
    }
}
